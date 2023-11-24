package core.apis.controller;

import core.apis.MtcPayApi;
import core.domain.SdaMainMas;
import core.queue.PayRequestProducer;
import lombok.RequiredArgsConstructor;
import core.dto.MtcNcrPayRequest;
import core.dto.MtcNcrPayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
//@RequestMapping(value="", method= RequestMethod.POST, consumes="application/json;") // 기수: MtcPayApi 인터페이스에서 @PostMapping으로 경로 지정해줬기 때문에 이거 없어도 돌아감
@RequiredArgsConstructor
public class MtcPayController implements MtcPayApi {

    private final PayRequestProducer kafka;
    private final WebClient webClient;
    private final static Logger log = LoggerFactory.getLogger(MtcPayController.class);

    @Override
    public ResponseEntity<?> pay(MtcNcrPayRequest mtcNcrPayRequest) {

        MtcNcrPayResponse  mtcNcrPayResponse= new MtcNcrPayResponse();
        String gid = callGidApi();
        log.info("@@@@@ gid: " + gid);
        try
        {
            mtcNcrPayRequest.setPayAcser( LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            log.info("$$$request => " , mtcNcrPayRequest.toString());
            kafka.produceMessage(mtcNcrPayRequest);
            mtcNcrPayResponse.setPayAcser(mtcNcrPayRequest.getPayAcser());
            mtcNcrPayResponse.setResult(0);
        }
        catch (Exception e)
        {
            mtcNcrPayResponse.setResult(-1);
            mtcNcrPayResponse.setErrStr(e.toString());
        }

        return ResponseEntity.ok(mtcNcrPayResponse);
    }

    private String callGidApi() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/log/mkgid")
                        .queryParam("svcid", "pay")
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

