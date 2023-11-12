package core.pay.apis;

import core.dto.MtcNcrPayRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MtcPayApi {
    @PostMapping("")
    ResponseEntity<?> pay(@RequestBody MtcNcrPayRequest mtcNcrPayRequest);
}
