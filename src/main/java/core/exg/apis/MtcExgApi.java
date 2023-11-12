package core.exg.apis;

import core.exg.apis.dto.MtcExgRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface MtcExgApi {

    @PostMapping("/exg")
    ResponseEntity<?> exchange(@RequestBody MtcExgRequest exgRequest);
}
