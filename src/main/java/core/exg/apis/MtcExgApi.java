package core.exg.apis;

import core.dto.MtcExgRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MtcExgApi {

    @PostMapping("/exchange")
    ResponseEntity<?> exchange(@RequestBody MtcExgRequest exgRequest);
}
