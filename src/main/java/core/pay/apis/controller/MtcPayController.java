package core.pay.apis.controller;

import lombok.RequiredArgsConstructor;
import core.pay.apis.MtcPayApi;
import core.pay.apis.dto.MtcNcrPayRequest;
import core.pay.apis.dto.MtcNcrPayResponse;
import core.pay.queue.PayRequestProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class MtcPayController implements MtcPayApi {

    private final PayRequestProducer kafka;

    private final static Logger log = LoggerFactory.getLogger(MtcPayController.class);

    @Override
    public ResponseEntity<?> pay(MtcNcrPayRequest mtcNcrPayRequest) {
        MtcNcrPayResponse  mtcNcrPayResponse= new MtcNcrPayResponse();

        mtcNcrPayRequest.setPayAcser(mtcNcrPayRequest.getTrxDt()
                    .concat( Double.toString(((Math.random() * 8999999999.0) + 1000000000 )%1000000000))) ;

        log.info("$$$request => " , mtcNcrPayRequest.toString());
        kafka.produceMessage(mtcNcrPayRequest);
        mtcNcrPayResponse.setPayAcser(mtcNcrPayRequest.getPayAcser());
        return ResponseEntity.ok(mtcNcrPayResponse);
    }
}
