package core.pay.queue;

import lombok.RequiredArgsConstructor;
import core.dto.MtcNcrPayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayRequestProducer {
    private static final Logger log = LoggerFactory.getLogger(PayRequestProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public void produceMessage(MtcNcrPayRequest mtcNcrPayRequest) {
        log.info("------> kafka : produce message : " + mtcNcrPayRequest.toString());
        String payAcser = mtcNcrPayRequest.getPayAcser();
        // topic , key, value
        // 결제 토픽의 key는 NEW 로 생성한다.
        kafkaTemplate.send("mtc.ncr.payRequest", "NEW" , mtcNcrPayRequest);
    }
}
