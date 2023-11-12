package core.exg.service;

import core.dto.MtcExgRequest;
import core.dto.MtcExgResponse;
import core.exg.queue.ExgKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MtcExgService {

    private final ExgKafkaProducer exgRequestKafkaProducer;

    public MtcExgResponse exchangeService(MtcExgRequest exgRequest) {

        // 충전 결과
        MtcExgResponse exgResponse = new MtcExgResponse();

        return exgResponse;
    }
}
