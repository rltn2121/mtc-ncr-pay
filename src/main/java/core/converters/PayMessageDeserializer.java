package core.converters;

import core.dto.MtcNcrPayRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;


public class PayMessageDeserializer implements Deserializer<MtcNcrPayRequest> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MtcNcrPayRequest deserialize(String s, byte[] bytes) {
        try {
            return objectMapper.readValue(new String(bytes), MtcNcrPayRequest.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getOriginalMessage());
            throw new SerializationException(e);
        }
    }
}

