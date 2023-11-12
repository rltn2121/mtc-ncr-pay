package core.exg.queue;

import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.pay.apis.dto.MtcNcrPayRequest;
import core.pay.apis.dto.TransferWiseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class ExgKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ExgKafkaProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient webClient;
    private final SdaMainMasRepository sdaMainMasRepository;

    /* 충전 요청 큐 구독 ing */
    @KafkaListener(topics = "mtc.ncr.exgRequest")
    public void consumeMessage(@Payload MtcNcrPayRequest payReqInfo ,
                               @Header(name = KafkaHeaders.RECEIVED_KEY , required = false) String key ,
                               @Header(KafkaHeaders.RECEIVED_TOPIC ) String topic ,
                               @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                               @Header(KafkaHeaders.OFFSET) long offset
    ) {


    }

}
