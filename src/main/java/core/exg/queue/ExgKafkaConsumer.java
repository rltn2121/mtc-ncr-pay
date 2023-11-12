package core.exg.queue;

import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.exg.apis.dto.MtcExgRequest;
import core.exg.apis.dto.MtcExgResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ExgKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ExgKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient webClient;
    private final SdaMainMasRepository sdaMainMasRepository;

    /* 충전 요청 큐 구독 ing */
    @KafkaListener(topics = "mtc.ncr.exgRequest")
    public void consumeMessage(@Payload MtcExgRequest exgReqInfo ,
                               @Header(name = KafkaHeaders.RECEIVED_KEY , required = false) String key ,
                               @Header(KafkaHeaders.RECEIVED_TOPIC ) String topic ,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                               @Header(KafkaHeaders.OFFSET) long offset
    ) {

        MtcExgResponse exgResponse = new MtcExgResponse();

        log.info ("@@영은 kafka 'mtc.ncr.exgRequest' 잡음! --> {}" , exgReqInfo.toString());

        SdaMainMas nowAcInfo = sdaMainMasRepository.
                                        findById(new SdaMainMasId(exgReqInfo.getAcno() , "KRW")).orElseThrow();
        log.info ("@@영은 현재 KRW = {}" , nowAcInfo);

        Double KRW_jan = nowAcInfo.getAc_jan(); // 현재 원화 금액

        // 1-1) 해당 계좌번호의 KRW 금액 < 환전 요청 금액 이면 ERROR
        if(KRW_jan < exgReqInfo.getTrxAmt())
        {
            exgResponse.setResult("FAIL");
            exgResponse.setErrCode("DEP27001");
            exgResponse.setErrMsg("원화 금액이 부족합니다.");


        }
        // 1-2) 해당 계좌번호의 KRW 금액 >= 환전 요청 금액
        else
        {

        }
    }
}
