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

        // topic , key, value
        //kafkaTemplate.send("coc.practice.22201785", "NEW" , mtcNcrPayRequest);
        // topic : mtc.ncr.core.exgRequest <-- 응답받들어온거 충전 일련번호 채번 후 먼저 던져주기
        // 충전 토픽의 key는 충전 일련번호로 삼는다.
        kafkaTemplate.send("mtc.ncr.exgRequest", "EXG" , exgRequest);
    }
}
