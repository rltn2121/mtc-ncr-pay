package core.pay.apis.controller;

import lombok.RequiredArgsConstructor;
import core.pay.apis.MtcPayApi;
import core.dto.MtcNcrPayRequest;
import core.dto.MtcNcrPayResponse;
import core.pay.queue.PayRequestProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(value="", method= RequestMethod.POST, consumes="application/json;")
@RequiredArgsConstructor
public class MtcPayController implements MtcPayApi {

    private final PayRequestProducer kafka;

    private final static Logger log = LoggerFactory.getLogger(MtcPayController.class);

    @Override
    public ResponseEntity<?> pay(MtcNcrPayRequest mtcNcrPayRequest) {
        MtcNcrPayResponse  mtcNcrPayResponse= new MtcNcrPayResponse();
        mtcNcrPayRequest.setPayAcser( LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        log.info("$$$request => " , mtcNcrPayRequest.toString());
        kafka.produceMessage(mtcNcrPayRequest);
        mtcNcrPayResponse.setPayAcser(mtcNcrPayRequest.getPayAcser());
        return ResponseEntity.ok(mtcNcrPayResponse);
    }
}

