package cloud.cirrusup.publisher;

import cloud.cirrusup.publisher.Publisher;
import cloud.cirrusup.publisher.model.PublishedInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloud.cirrusup.publisher.model.PublishedInfo.API_METHOD;
import static cloud.cirrusup.publisher.model.PublishedInfo.ERROR_SUMMARY;
import static cloud.cirrusup.publisher.model.PublishedInfo.HTTP_METHOD;
import static cloud.cirrusup.publisher.model.PublishedInfo.REQUEST_ID;
import static cloud.cirrusup.publisher.model.PublishedInfo.SERVICE_NAME;

/**
 * Publisher that exports entries in JSON format.
 */
public class JSONPublisher implements Publisher {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(LOG_NAME);

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(PublishedInfo info) {

        ObjectNode node = mapper.createObjectNode();
        node.put("requestId", info.getInfo(REQUEST_ID));
        node.put("serviceName", info.getInfo(SERVICE_NAME));
        node.put("httpMethod", info.getInfo(HTTP_METHOD));
        node.put("latency", info.getLatency());
        node.put("statusCode", info.getStatusCode());
        if (info.hasInfo(ERROR_SUMMARY)) {
            node.put("errorMessage", info.getInfo(ERROR_SUMMARY));
        }
        if (info.hasInfo(API_METHOD)) {
            node.put("apiMethod", info.getInfo(API_METHOD));
        }

        try {

            LOG.info(mapper.writeValueAsString(node));
        } catch (JsonProcessingException e) {
        }
    }
}
