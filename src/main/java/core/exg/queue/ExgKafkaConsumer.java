package core.exg.queue;

import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.dto.MtcExgRequest;
import core.dto.MtcExgResponse;
import core.dto.MtcResultRequest;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        MtcResultRequest resultRequest = new MtcResultRequest();

        log.info ("@@영은 kafka 'mtc.ncr.exgRequest' 잡음! --> {}" , exgReqInfo.toString());

        SdaMainMas nowAcInfo = sdaMainMasRepository.
                                        findById(new SdaMainMasId(exgReqInfo.getAcno() , "KRW")).orElseThrow();
        log.info ("@@영은 현재 KRW = {}" , nowAcInfo);

        Double KRW_jan = nowAcInfo.getAc_jan(); // 현재 원화 금액

        try {
            // 공통 정보 셋팅
            // 현재 금액
            resultRequest.setNujkJan(KRW_jan);
            // 계좌번호(고객번호)
            resultRequest.setAcno(exgReqInfo.getAcno());
            // 통화코드
            resultRequest.setCurC(exgReqInfo.getCurC());
            // 충전 요청 금액
            resultRequest.setTrxAmt(exgReqInfo.getTrxAmt());
            // 거래일자
            resultRequest.setTrxdt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
            // 충전 일련번호
            resultRequest.setAprvSno(exgReqInfo.getAcser());

            // 해당 계좌번호의 KRW 금액 < 환전 요청 금액 이면 ERROR
            if(KRW_jan < exgReqInfo.getTrxAmt())
            {
                log.info("@@영은 충전 금액 부족 error : 현재 금액 {}, 충전 요청 금액 {}", KRW_jan, exgReqInfo.getTrxAmt());

                // 충전 불가로 result 큐에 넣어줄 값 셋팅한다.
                resultRequest.setKey("FAIL");
                // 충전 실패
                resultRequest.setUpmuG(4);
//                resultRequest.setErrCode("DEP27001");

                // result 큐로 send
                kafkaTemplate.send("mtc.ncr.result", resultRequest.getKey() , resultRequest);
            }
            // 해당 계좌번호의 KRW 금액 >= 환전 요청 금액 -> 충전 서비스 태우기
            else
            {
                try {
                    log.info("@@영은 충전 시도 시작!");

                    // 충전 불가로 result 큐에 넣어줄 값 셋팅한다.
                    resultRequest.setKey("FAIL");
                    // 충전 실패
                    resultRequest.setUpmuG(4);
//                resultRequest.setErrCode("DEP27001");
                }
                catch (Exception e) {

                }
            }
        }
        catch(Exception e) {

        }
    }
}
