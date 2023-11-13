package core.exg.queue;

import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.dto.MtcExgRequest;
import core.dto.MtcExgResponse;
import core.dto.MtcNcrPayRequest;
import core.dto.MtcResultRequest;
import core.exg.service.MtcExgService;
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
    private final SdaMainMasRepository sdaMainMasRepository;
    private final MtcExgService exgService;

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

        //원화 금액 정보
        SdaMainMas nowAcInfo = sdaMainMasRepository.
                                        findById(new SdaMainMasId(exgReqInfo.getAcno() , "KRW")).orElseThrow();
        log.info ("@@영은 현재 KRW 금액 = {}" , nowAcInfo);

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

            if("Y".equals(exgReqInfo.getPayYn())) {
                // 결제 일련번호
                resultRequest.setAprvSno(exgReqInfo.getPayInfo().getPayAcser());
            } else {
                // 충전 일련번호
                resultRequest.setAprvSno(exgReqInfo.getAcser());
            }

            // 해당 계좌번호의 KRW 금액 < 환전 요청 금액 이면 ERROR
            if(KRW_jan < exgReqInfo.getTrxAmt())
            {
                log.info("@@영은 충전 금액 부족 error : 현재 금액 {}, 충전 요청 금액 {}", KRW_jan, exgReqInfo.getTrxAmt());

                // 충전 실패
                resultRequest.setUpmuG(4);
                resultRequest.setErrMsg("충전 잔액이 부족합니다.");

                // result 큐로 send
                kafkaTemplate.send("mtc.ncr.result", "FAIL", resultRequest);
            }
            // 해당 계좌번호의 KRW 금액 >= 환전 요청 금액 --> 충전 서비스 태우기
            else
            {
                try {
                    log.info("@@영은 충전 시도 시작!");

                    // 충전 프로세스
                    exgService.exchangeService(exgReqInfo);

                    // 충전 성공
                    resultRequest.setUpmuG(2);

                    // 결제에서 들어온 경우에는 결제큐와 결과큐에 모두 적재
                    if("Y".equals(exgReqInfo.getPayYn())) {
                        MtcNcrPayRequest payRequest = new MtcNcrPayRequest();

                        // 계좌번호(고객번호)
                        payRequest.setAcno(exgReqInfo.getPayInfo().getAcno());

                        // 통화코드
                        payRequest.setCurC(exgReqInfo.getPayInfo().getCurC());

                        // 거래처
                        payRequest.setTrxPlace(exgReqInfo.getPayInfo().getTrxPlace());

                        // 거래금액
                        payRequest.setTrxAmt(exgReqInfo.getPayInfo().getTrxAmt());

                        // 거래일자
                        payRequest.setTrxDt(exgReqInfo.getPayInfo().getTrxDt());

                        // 결제 일련번호
                        payRequest.setPayAcser(exgReqInfo.getPayInfo().getPayAcser());

                        kafkaTemplate.send("mtc.ncr.payRequest", "NEW", payRequest); // 결제 kew는 "NEW"
                    }
                    kafkaTemplate.send("mtc.ncr.result", "SUCCESS", resultRequest);
                }
                catch (Exception e) {
                    log.info("@@영은 서비스 자체 error");

                    // 충전 실패
                    resultRequest.setUpmuG(4);
                    resultRequest.setErrMsg("충전 중 에러가 발생했습니다. 다시 시도하세요.");

                    // result 큐로 send
                    kafkaTemplate.send("mtc.ncr.result", "FAIL", resultRequest);
                }
            }
        }
        catch(Exception e) {
            log.info("@@영은 서비스 자체 error");

            // 충전 실패
            resultRequest.setUpmuG(4);
            resultRequest.setErrMsg("충전 중 에러가 발생했습니다. 다시 시도하세요.");

            // result 큐로 send
            kafkaTemplate.send("mtc.ncr.result", "FAIL", resultRequest);
        }
    }
}
