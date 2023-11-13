package core.exg.queue;

import core.dto.MtcExgRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExgKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(ExgKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void produceMessage(MtcExgRequest exgRequest) {

        log.info("@@영은 kafka : exchange produce message : " + exgRequest.toString());

        // 여기서 충전큐에 넣는 경우는 직접 충전 요청이 들어온 경우임
        // --> payYn 에 'N' 넣기
        exgRequest.setPayYn("N");

        // topic , key, value
        // topic : mtc.ncr.core.exgRequest <-- 응답받들어온거 충전 일련번호 채번 후 먼저 던져주기
    }
}
