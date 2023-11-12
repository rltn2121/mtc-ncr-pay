package core.pay.queue;

import core.pay.apis.service.MtcPayService;
import lombok.RequiredArgsConstructor;
import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.pay.apis.dto.MtcNcrPayRequest;
import core.pay.apis.dto.TransferWiseDto;
import core.domain.SdaMainMasId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class PayRequestConsumer {

    private static final Logger log = LoggerFactory.getLogger(PayRequestProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient webClient;
    private final SdaMainMasRepository sdaMainMasRepository;
    private final MtcPayService mtcPayService;


    @KafkaListener(topics = "mtc.ncr.core.payRequest")
    public void consumeMessage(@Payload MtcNcrPayRequest payReqInfo ,
                               @Header(name = KafkaHeaders.RECEIVED_KEY , required = false) String key ,
                               @Header(KafkaHeaders.RECEIVED_TOPIC ) String topic ,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                               @Header(KafkaHeaders.OFFSET) long offset
    ) {

        log.info("############구독시작한다###############{}" , payReqInfo.toString());

        SdaMainMas tempAcInfo = sdaMainMasRepository.
                                findById(new SdaMainMasId(payReqInfo.getAcno() , payReqInfo.getCurC())).orElseThrow();
        Double ac_jan = tempAcInfo.getAc_jan();

        log.info ("#####ac_jan {} , {}" , ac_jan , tempAcInfo.toString());

        if(ac_jan > payReqInfo.getTrxAmt()) // 계좌 잔액이 결제요청금액보다 큰 경우
        {
            //결제처리한다
            mtcPayService.withdraw(payReqInfo);
            //결과를 result 에 넣는다. ( result 큐에서 거래내역 넣어줌 )


        }
        else //결제요청금액이 잔액보다 큰 경우
        {
            // 충전 큐에 넣는다.

            // 충전 후 결제 재요청여부 , 결제 일련번호 , 결제요청금액 , 고객번호
        }

        // 다른파드 호출할 때도 이런식으로 하면 될거같음.
        /*
        String response  = webClient.post()
                .uri(URI.create("https://api.sandbox.transferwise.tech/v3/quotes"))
                .bodyValue(new TransferWiseDto("USD", "KRW" , 200L,null))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .block();

        kafkaTemplate.send("coc.submit", "22201785" , response)
                .whenComplete((result , ex) -> {
                    if( ex == null)
                    {
                        log.info(" 잘 들어갔음 --> {} " , result.getProducerRecord().key());
                    } else {
                        log.error("error ");
                    }
                });
                */

    }

}
