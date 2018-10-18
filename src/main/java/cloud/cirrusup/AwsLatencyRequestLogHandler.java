package cloud.cirrusup;

import cloud.cirrusup.publisher.Publisher;
import cloud.cirrusup.publisher.PlainLogPublisher;
import cloud.cirrusup.publisher.model.PublishedInfo;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.services.s3.model.AmazonS3Exception;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AWS Request handler that logs in a text file the request latency + status code.
 */
public class AwsLatencyRequestLogHandler extends RequestHandler2 {

    private static final String REQUEST_ID = "requestId";

    private final Publisher publisher;
    private final ConcurrentHashMap<String, Long> REQUEST_MAP = new ConcurrentHashMap<>();

    /**
     * Constructor.
     */
    public AwsLatencyRequestLogHandler() {

        this(new PlainLogPublisher());
    }

    /**
     * Constructor.
     */
    public AwsLatencyRequestLogHandler(Publisher publisher) {

        this.publisher = publisher;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beforeRequest(Request<?> request) {

        final String id = RandomGenerator.getUUIDRandomString();
        request.getHeaders().put(REQUEST_ID, id);
        REQUEST_MAP.put(id, System.currentTimeMillis());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterResponse(Request<?> request, Response<?> response) {

        final String id = request.getHeaders().get(REQUEST_ID);
        PublishedInfo info = new PublishedInfo();
        try {

            info.addInfo(PublishedInfo.SERVICE_NAME, request.getServiceName());
            info.addInfo(PublishedInfo.HTTP_METHOD, request.getHttpMethod().name());
            info.addInfo(PublishedInfo.API_METHOD, getApiMethod(request));
            info.addInfo(PublishedInfo.REQUEST_ID, getRequestId(request, response));
            info.addLatency(getTime(id));
            info.setStatusCode(getStatusCode(response, null));
        } catch (Exception ex) {

        } finally {

            publisher.publish(info);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterError(Request<?> request, Response<?> response, Exception e) {

        final String id = request.getHeaders().get(REQUEST_ID);
        PublishedInfo info = new PublishedInfo();
        try {

            info.addInfo(PublishedInfo.SERVICE_NAME, request.getServiceName());
            info.addInfo(PublishedInfo.HTTP_METHOD, request.getHttpMethod().name());
            info.addInfo(PublishedInfo.REQUEST_ID, getRequestId(e));
            info.addInfo(PublishedInfo.API_METHOD, getApiMethod(request));
            info.addInfo(PublishedInfo.ERROR_SUMMARY, e.getMessage());
            info.addLatency(getTime(id));
            info.setStatusCode(getStatusCode(response, e));
        } catch (Exception ex) {

        } finally {

            publisher.publish(info);
        }
    }

    private String getApiMethod(Request<?> request) {

        if (request != null && request.getHeaders() != null && request.getHeaders().containsKey("X-Amz-Target")) {

            return request.getHeaders().get("X-Amz-Target");
        }

        if (request != null && request.getParameters() != null && request.getParameters().containsKey("Action")) {

            List<String> action = request.getParameters().get("Action");
            if (!action.isEmpty()) {

                return action.get(0);
            }
        }

        return null;
    }

    private String getRequestId(Exception e) {

        if (e != null && e instanceof AmazonServiceException) {

            AmazonServiceException awsException = (AmazonServiceException) e;
            if (awsException.getServiceName().equals("Amazon S3") && (awsException instanceof AmazonS3Exception)) {

                return awsException.getRequestId() + " " + ((AmazonS3Exception) e).getExtendedRequestId();
            }
            return awsException.getRequestId();
        }

        return null;
    }


    private String getRequestId(Request<?> request, Response<?> response) {

        if (response != null && response.getHttpResponse() != null && response.getHttpResponse().getHeaders() != null) {

            if (request.getServiceName().equals("Amazon S3")) {

                return response.getHttpResponse().getHeaders().get("x-amz-request-id") + " " + response.getHttpResponse().getHeaders().get("x-amz-id-2");
            }
            return response.getHttpResponse().getHeaders().get("x-amzn-RequestId");
        }

        return null;
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
