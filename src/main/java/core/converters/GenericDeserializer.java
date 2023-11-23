package core.converters;

import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class GenericDeserializer extends JsonDeserializer<Object>
{
    @Override
    public Object deserialize(String topic, Headers headers, byte[] data)
    {
        switch (topic)
        {
            case "mtc.ncr.payRequest":
                PayMessageDeserializer topicOneDeserializer = new PayMessageDeserializer();
                return topicOneDeserializer.deserialize(topic, headers, data);
        }
        return super.deserialize(topic, data);
    }
}