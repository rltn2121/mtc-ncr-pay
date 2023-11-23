package core.config;

import core.exg.converters.ExgMessageDeserializer;
import core.pay.converters.MessageDeserializer;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class GenericDeserializer extends JsonDeserializer<Object>
{
    @Override
    public Object deserialize(String topic, Headers headers, byte[] data)
    {
        switch (topic)
        {
            case "mtc.ncr.core.payRequest":
                MessageDeserializer topicOneDeserializer = new MessageDeserializer();
                //topicOneDeserializer.addTrustedPackages("com.xyz");
                return topicOneDeserializer.deserialize(topic, headers, data);
            case "mtc.ncr.exgRequest":
                ExgMessageDeserializer topicTwoDeserializer= new ExgMessageDeserializer();
                return topicTwoDeserializer.deserialize(topic, headers, data);
        }
        return super.deserialize(topic, data);
    }
}