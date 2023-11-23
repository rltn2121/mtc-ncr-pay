package core.pay.queue;

import core.dto.MtcExgRequest;
import core.dto.MtcNcrPayResponse;
import core.dto.MtcResultRequest;
import core.service.MtcPayService;
import lombok.RequiredArgsConstructor;
import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.dto.MtcNcrPayRequest;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class PayRequestConsumer {
    private static final Logger log = LoggerFactory.getLogger(PayRequestProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient webClient;
    private final SdaMainMasRepository sdaMainMasRepository;
    private final MtcPayService mtcPayService;

    @KafkaListener(topics = "mtc.ncr.payRequest", groupId="practice22201653")
    public void consumeMessage(@Payload MtcNcrPayRequest payReqInfo ,
                               @Header(name = KafkaHeaders.RECEIVED_KEY , required = false) String key ,
                               @Header(KafkaHeaders.RECEIVED_TOPIC ) String topic ,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                               @Header(KafkaHeaders.OFFSET) long offset
    ) {
        MtcResultRequest resultDto = new MtcResultRequest();
        try {
            log.info("############구독시작한다###############{}", payReqInfo.toString());
            MtcNcrPayResponse payResponse = new MtcNcrPayResponse();



            /*
            SdaMainMas tempAcInfo = sdaMainMasRepository.
                    findById(new SdaMainMasId(payReqInfo.getAcno(), payReqInfo.getCurC())).orElseThrow();
            Double ac_jan = tempAcInfo.getAc_jan();
            log.info("#####ac_jan {} , {}", Double.toString(ac_jan), tempAcInfo.toString());
            try {
                if (ac_jan > payReqInfo.getTrxAmt()) // 계좌 잔액이 결제요청금액보다 큰 경우
                {
                    resultDto.setAcno(payReqInfo.getAcno());
                    resultDto.setCurC(payReqInfo.getCurC());
                    resultDto.setTrxdt(payReqInfo.getTrxDt());
                    resultDto.setTrxAmt(payReqInfo.getTrxAmt());
                    resultDto.setAprvSno(payReqInfo.getPayAcser());
                    //결제처리한다
                    try {
                        payResponse = mtcPayService.withdraw(payReqInfo);
                        //성공했으면 result 큐에 넣어줄 값 셋팅한다.
                        if (payResponse.getResult() == 1) {
                            resultDto.setUpmuG(1);
                            resultDto.setNujkJan(ac_jan - payReqInfo.getTrxAmt());
                            resultDto.setPayinfo(payReqInfo);
                            //결과를 result 에 넣는다. ( result 큐에서 거래내역 넣어줌 )
                            kafkaTemplate.send("mtc.ncr.result", "SUCCESS", resultDto);
                        } else {
                            resultDto.setUpmuG(3);
                            resultDto.setPayinfo(payReqInfo);
                            resultDto.setNujkJan(ac_jan);
                            resultDto.setErrMsg(payResponse.getErrStr());
                            //결과를 result 에 넣는다. ( result 큐에서 거래내역 넣어줌 )
                            log.info("#####RESULT 큐에 넣어준다. : {}", resultDto.toString());
                            kafkaTemplate.send("mtc.ncr.result", "FAIL", resultDto);
                        }
                    } catch (Exception e) {
                        log.info("$$$withdraw error : {}", e.toString());
                        //실패했으면 result 큐에 넣어줄 값 셋팅한다.
                        resultDto.setUpmuG(3);
                        resultDto.setNujkJan(ac_jan);
                        resultDto.setErrMsg(e.toString());
                        resultDto.setPayinfo(payReqInfo);
                        kafkaTemplate.send("mtc.ncr.result", "FAIL", resultDto);
                    }
                } else //결제요청금액이 잔액보다 큰 경우
                {
                    // 충전 큐에 넣는다.
                    MtcExgRequest exgRequest = new MtcExgRequest();
                    exgRequest.setPayInfo(payReqInfo);
                    exgRequest.setAcno(payReqInfo.getAcno());
                    exgRequest.setCurC(payReqInfo.getCurC());
                    exgRequest.setAcser(payReqInfo.getPayAcser());
                    exgRequest.setPayYn("Y");
                    exgRequest.setTrxAmt(payReqInfo.getTrxAmt() - ac_jan);
                    kafkaTemplate.send("mtc.ncr.exgRequest", "PAY", exgRequest);
                    // 업무구분 , 결제 일련번호 , 결제요청금액 , 고객번호
                }
            } catch (Exception e) {
                log.info("$$$$$ 결제하다가 에러난다 : {}", e.toString());
                log.info("$$$withdraw error : {}", e.toString());
                //실패했으면 result 큐에 넣어줄 값 셋팅한다.
                resultDto.setUpmuG(2);
                resultDto.setNujkJan(ac_jan);
                resultDto.setErrMsg(e.toString());
                resultDto.setPayinfo(payReqInfo);
                kafkaTemplate.send("mtc.ncr.result", "FAIL", resultDto);
            }
            */
        }
        catch (NoSuchElementException e)
        {
            resultDto.setUpmuG(2);
            resultDto.setNujkJan(0.0);
            resultDto.setErrMsg("조건에 해당하는 값이 없습니다.");
            resultDto.setPayinfo(payReqInfo);
            kafkaTemplate.send("mtc.ncr.result", "FAIL", resultDto);
        }
    }
}
