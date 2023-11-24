package core.queue;

import core.domain.SdaMainMasId;
import core.dto.*;
import core.service.MtcPayService;
import lombok.RequiredArgsConstructor;
import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PayRequestConsumer {
    private static final Logger log = LoggerFactory.getLogger(PayRequestProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private final WebClient webClient;
    private final SdaMainMasRepository sdaMainMasRepository;
    private final MtcPayService mtcPayService;

    @KafkaListener(topics = "mtc.ncr.payRequest", groupId="practice22201785")
    public void consumeMessage(@Payload MtcNcrPayRequest payReqInfo ,
                               @Header(name = KafkaHeaders.RECEIVED_KEY , required = false) String key ,
                               @Header(KafkaHeaders.RECEIVED_TOPIC ) String topic ,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                               @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("$$ pay request 구독시작 : {}" , payReqInfo.toString());
        MtcNcrPayResponse payResponse = new MtcNcrPayResponse();

        try{
            //sda_main_mas 테이블을 내부연동을 통해 읽어온다.
            SdaMainMas sdaMainMas = WebClient.create("http://mtc-ncr-com-svc.coc-mtc.svc.cluster.local:8080")
                            .get()
                                    .uri("/sdaMainMas/" + payReqInfo.getAcno() + "?cur_c" + payReqInfo.getCurC())
                                            .retrieve()
                                                    .bodyToMono(SdaMainMas.class)
                                                            .block();

            log.info("$$ get sda_main_mas in payRequestConsumer:{}" , sdaMainMas.toString());
            Double ac_jan = sdaMainMas.getAc_jan();

            if (ac_jan < payReqInfo.getTrxAmt()) // 계좌 잔액이 결제요청금액보다 작은 경우
            {
                // 충전 큐에 넣는다.
                MtcExgRequest exgRequest
                        = new MtcExgRequest("Y" , payReqInfo.getAcno() , payReqInfo.getCurC(), payReqInfo.getTrxAmt() - ac_jan,
                            payReqInfo.getPayAcser() , payReqInfo
                        );
                kafkaTemplate.send("mtc.ncr.exgRequest", "PAY" , exgRequest);
            }
            else //결제요청금액이 잔액보다 큰 경우
            {
                // 잔액update 큐에 넣는다.
                MtcNcrUpdateMainMasRequestSub mainMasRequestSub
                        = new MtcNcrUpdateMainMasRequestSub(-1 /*결제니까 요청금액을 음수로 set*/,
                            payReqInfo.getTrxAmt() , payReqInfo.getCurC() , payReqInfo.getTrxDt());

                // 결제요청 list 에 set
                List<MtcNcrUpdateMainMasRequestSub> mainMasRequestSubList = new ArrayList<>();
                mainMasRequestSubList.add(mainMasRequestSub);

                // comRequest 토픽에 set
                MtcNcrUpdateMainMasRequest mainMasRequest =
                        new MtcNcrUpdateMainMasRequest(
                                payReqInfo.getAcno(),payReqInfo.getGid() , mainMasRequestSubList , "PAY");

                kafkaTemplate.send("mtc.ncr.comRequest", "PAY" , mainMasRequest);
            }
        }
        catch(Exception e)
        {
            log.info("$$ error while 결제 : {}" , e.toString());
            //실패했으면 바로 result 큐에 넣는다.
            MtcResultRequest resultDto
                    = new MtcResultRequest(payReqInfo.getAcno() , payReqInfo.getTrxDt() , payReqInfo.getCurC()
                    , 2 /* 업무구분 2 : 결제 실패*/ , payReqInfo.getPayAcser() , payReqInfo.getTrxAmt() ,
                    0.0 , "결제시 오류가 발생 - "+e.toString() , payReqInfo ) ;
            kafkaTemplate.send("mtc.ncr.result", "FAIL" , resultDto);
        }
    }
}
