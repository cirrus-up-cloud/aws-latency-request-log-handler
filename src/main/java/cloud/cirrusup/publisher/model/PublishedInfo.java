package cloud.cirrusup.publisher.model;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO class that holds all details about info published.
 */
public class PublishedInfo {

    public static final String REQUEST_ID = "requestId";
    public static final String SERVICE_NAME = "serviceName";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String ERROR_SUMMARY = "errorInfo";
    public static final String API_METHOD = "apiMethod";


    private final Map<String, String> info = new HashMap<>();
    private long latency = -1;
    private int statusCode = -1;


    public void addLatency(long latency) {

        this.latency = latency;
    }

    public void addInfo(String name, String value) {

        this.info.put(name, value);
    }

    public long getLatency() {

        return latency;
    }

    public String getInfo(String name) {

        return info.get(name);
    }

    public boolean hasInfo(String name) {

        return info.containsKey(name) && info.get(name) != null;
    }

    public void setStatusCode(int statusCode) {

        this.statusCode = statusCode;
    }

    public int getStatusCode() {

        return statusCode;
    }
}
