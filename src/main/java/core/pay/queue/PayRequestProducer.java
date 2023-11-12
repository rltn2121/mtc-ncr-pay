package core.pay.queue;

import lombok.RequiredArgsConstructor;
import core.pay.apis.dto.MtcNcrPayRequest;
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
        // 결제 토픽의 key는 결제 일련번호로 삼는다.
        kafkaTemplate.send("mtc.ncr.core.payRequest", mtcNcrPayRequest.getPayAcser() , mtcNcrPayRequest);
        //kafkaTemplate.send("coc.practice.22201785", "NEW" , mtcNcrPayRequest);
    }
}
