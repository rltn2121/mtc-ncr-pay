package core.exg.apis;

import core.dto.MtcExgRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MtcExgApi {

    @PostMapping("/charge")
    ResponseEntity<?> exchange(@RequestBody MtcExgRequest exgRequest);
}
