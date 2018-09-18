package cloud.cirrusup;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.handlers.RequestHandler2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * AWS Request handler that logs in a text file the request latency + status code.
 */
public class AwsLatencyRequestLogHandler extends RequestHandler2 {

    private static final String LOG_NAME = "aws-latency-log";
    private static final String REQUEST_ID = "requestId";
    private static final Logger LOG = LoggerFactory.getLogger(LOG_NAME);


    private final ConcurrentHashMap<String, Long> REQUEST_MAP = new ConcurrentHashMap<>();

    /**
     * Constructor.
     */
    public AwsLatencyRequestLogHandler() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRequest(Request<?> request) {

        final String id = RandomGenerator.getUUIDRandomString();

        LOG.info("ID [{}] - Request done to the service [{}] with the method [{}]", id, request.getServiceName(), request.getHttpMethod());

        request.getHeaders().put(REQUEST_ID, id);
        REQUEST_MAP.put(id, System.currentTimeMillis());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterResponse(Request<?> request, Response<?> response) {

        final String id = request.getHeaders().get(REQUEST_ID);

        try {

            LOG.info("Request ID [{}] took [{}] milliseconds and returned the RESPONSE_CODE [{}]",
                    id, getTime(id), getStatusCode(response, null));
        } catch (Exception ex) {

            LOG.error("ID [{}] for the request to [{}] was not found.", id, request.getServiceName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterError(Request<?> request, Response<?> response, Exception e) {

        final String id = request.getHeaders().get(REQUEST_ID);

        try {

            LOG.error("Request ID [{}] took [{}] milliseconds and returned the RESPONSE_CODE [{}] and ERROR_MESSAGE [{}]",
                    id, getTime(id), getStatusCode(response, e), e.getMessage());
        } catch (Exception ex) {

            LOG.error("ID [{}] for the request to [{}] was not found.", id, request.getServiceName());
        }
    }

    private int getStatusCode(Response<?> response, Exception e) {

        if (response != null && response.getHttpResponse() != null) {

            return response.getHttpResponse().getStatusCode();
        } else if (e != null && e instanceof AmazonServiceException) {

            return ((AmazonServiceException) e).getStatusCode();
        }

        return -1;
    }

    private long getTime(String id) throws Exception {

        final Long time = REQUEST_MAP.get(id);
        if (time != null) {

            REQUEST_MAP.remove(id);
            return System.currentTimeMillis() - time;
        }

        throw new Exception("ID [" + id + "] was not found int the map.");
    }
}
