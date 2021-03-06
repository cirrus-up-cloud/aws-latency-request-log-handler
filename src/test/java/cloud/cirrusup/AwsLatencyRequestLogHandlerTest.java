package cloud.cirrusup;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.Response;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Tests for {@link AwsLatencyRequestLogHandler} class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Response.class, RandomGenerator.class})
public class AwsLatencyRequestLogHandlerTest {

    private static final String SERVICE_NAME = "AmazonSQS";
    private static final String REQUEST_ID = "requestId";
    private static final String REQUEST_VALUE = "49342343-df0b-4378-9fbf-893445f6e74f";

    private AwsLatencyRequestLogHandler awsLatencyRequestHandler = new AwsLatencyRequestLogHandler();

    @Test
    public void testWithGoodRequestId() throws InterruptedException {

        //setup
        Request request = mock(Request.class);
        Response response = PowerMockito.mock(Response.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        Map mapMock = mock(Map.class);
        mockStatic(RandomGenerator.class);

        when(request.getServiceName()).thenReturn("AmazonSQS");
        when(request.getHttpMethod()).thenReturn(HttpMethodName.POST);
        when(RandomGenerator.getUUIDRandomString()).thenReturn(REQUEST_VALUE);
        when(request.getHeaders()).thenReturn(mapMock);
        when(response.getHttpResponse()).thenReturn(httpResponse);
        when(httpResponse.getStatusCode()).thenReturn(200);
        when(httpResponse.getHeaders()).thenReturn(mapMock);
        when(mapMock.get("x-amzn-RequestId")).thenReturn(REQUEST_VALUE);
        when(mapMock.get("requestId")).thenReturn(REQUEST_VALUE);

        //call
        awsLatencyRequestHandler.beforeRequest(request);
        Thread.sleep(150);
        awsLatencyRequestHandler.afterResponse(request, response);

        //verify
        verify(request, times(4)).getHeaders();
        verify(request, times(2)).getParameters();
        verify(response, times(5)).getHttpResponse();
        verify(request, times(1)).getHttpMethod();
        verify(request, times(2)).getServiceName();
        verify(httpResponse, times(1)).getStatusCode();
        verify(httpResponse, times(2)).getHeaders();
        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get("requestId");
        verify(mapMock, times(1)).get("x-amzn-RequestId");
        verify(mapMock, times(1)).containsKey("X-Amz-Target");
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(httpResponse);
        verifyNoMoreInteractions(mapMock);

        PowerMockito.verifyStatic(times(1));
        RandomGenerator.getUUIDRandomString();
        PowerMockito.verifyNoMoreInteractions(RandomGenerator.class);

        assertEquals(SERVICE_NAME, request.getServiceName());
        assertEquals(HttpMethodName.POST, request.getHttpMethod());
        assertEquals(REQUEST_VALUE, request.getHeaders().get(REQUEST_ID));
        assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    public void testWithMethodName() throws InterruptedException {

        //setup
        Request request = mock(Request.class);
        Response response = PowerMockito.mock(Response.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        Map mapMock = mock(Map.class);
        mockStatic(RandomGenerator.class);

        when(request.getServiceName()).thenReturn("AmazonSQS");
        when(request.getHttpMethod()).thenReturn(HttpMethodName.POST);
        when(RandomGenerator.getUUIDRandomString()).thenReturn(REQUEST_VALUE);
        when(request.getHeaders()).thenReturn(mapMock);
        when(response.getHttpResponse()).thenReturn(httpResponse);
        when(httpResponse.getStatusCode()).thenReturn(200);
        when(httpResponse.getHeaders()).thenReturn(mapMock);
        when(mapMock.get("x-amzn-RequestId")).thenReturn(REQUEST_VALUE);
        when(mapMock.get("requestId")).thenReturn(REQUEST_VALUE);
        when(mapMock.containsKey("X-Amz-Target")).thenReturn(true);
        when(mapMock.get("X-Amz-Target")).thenReturn("list queues");

        //call
        awsLatencyRequestHandler.beforeRequest(request);
        Thread.sleep(150);
        awsLatencyRequestHandler.afterResponse(request, response);

        //verify
        verify(request, times(5)).getHeaders();
        verify(response, times(5)).getHttpResponse();
        verify(request, times(1)).getHttpMethod();
        verify(request, times(2)).getServiceName();
        verify(httpResponse, times(1)).getStatusCode();
        verify(httpResponse, times(2)).getHeaders();
        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get("requestId");
        verify(mapMock, times(1)).get("x-amzn-RequestId");
        verify(mapMock, times(1)).get("X-Amz-Target");
        verify(mapMock, times(1)).containsKey("X-Amz-Target");
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(httpResponse);
        verifyNoMoreInteractions(mapMock);

        PowerMockito.verifyStatic(times(1));
        RandomGenerator.getUUIDRandomString();
        PowerMockito.verifyNoMoreInteractions(RandomGenerator.class);

        assertEquals(SERVICE_NAME, request.getServiceName());
        assertEquals(HttpMethodName.POST, request.getHttpMethod());
        assertEquals(REQUEST_VALUE, request.getHeaders().get(REQUEST_ID));
        assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    public void testWithGoodRequestIdS3Service() throws InterruptedException {

        //setup
        Request request = mock(Request.class);
        Response response = PowerMockito.mock(Response.class);
        HttpResponse httpResponse = mock(HttpResponse.class);
        Map mapMock = mock(Map.class);
        mockStatic(RandomGenerator.class);

        when(request.getServiceName()).thenReturn("Amazon S3");
        when(request.getHttpMethod()).thenReturn(HttpMethodName.GET);
        when(RandomGenerator.getUUIDRandomString()).thenReturn(REQUEST_VALUE);
        when(request.getHeaders()).thenReturn(mapMock);
        when(response.getHttpResponse()).thenReturn(httpResponse);
        when(httpResponse.getStatusCode()).thenReturn(200);
        when(httpResponse.getHeaders()).thenReturn(mapMock);
        when(mapMock.get("x-amz-request-id")).thenReturn(REQUEST_VALUE);
        when(mapMock.get("x-amz-id-2")).thenReturn("value-2");
        when(mapMock.get("requestId")).thenReturn(REQUEST_VALUE);

        //call
        awsLatencyRequestHandler.beforeRequest(request);
        Thread.sleep(150);
        awsLatencyRequestHandler.afterResponse(request, response);

        //verify
        verify(request, times(4)).getHeaders();
        verify(response, times(6)).getHttpResponse();
        verify(request, times(1)).getHttpMethod();
        verify(request, times(2)).getServiceName();
        verify(request, times(2)).getParameters();
        verify(httpResponse, times(1)).getStatusCode();
        verify(httpResponse, times(3)).getHeaders();
        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get("requestId");
        verify(mapMock, times(1)).get("x-amz-request-id");
        verify(mapMock, times(1)).get("x-amz-id-2");
        verify(mapMock, times(1)).containsKey("X-Amz-Target");
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(httpResponse);
        verifyNoMoreInteractions(mapMock);

        PowerMockito.verifyStatic(times(1));
        RandomGenerator.getUUIDRandomString();
        PowerMockito.verifyNoMoreInteractions(RandomGenerator.class);

        assertEquals("Amazon S3", request.getServiceName());
        assertEquals(HttpMethodName.GET, request.getHttpMethod());
        assertEquals(REQUEST_VALUE, request.getHeaders().get(REQUEST_ID));
        assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    public void testRequestIdNotFound() {

        //setup
        Request request = mock(Request.class);
        Map mapMock = mock(Map.class);

        when(request.getServiceName()).thenReturn("AmazonSQS");
        when(request.getHeaders()).thenReturn(mapMock);
        when(request.getHttpMethod()).thenReturn(HttpMethodName.GET);
        when(mapMock.get(REQUEST_ID)).thenReturn(REQUEST_VALUE);
        when(mapMock.get("x-amzn-RequestId")).thenReturn(REQUEST_VALUE);

        //call
        awsLatencyRequestHandler.beforeRequest(request);
        awsLatencyRequestHandler.afterResponse(request, null);

        //verify
        verify(request, times(4)).getHeaders();
        verify(request, times(2)).getParameters();
        verify(request, times(1)).getHttpMethod();
        verify(request, times(1)).getServiceName();
        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get(anyString());
        verify(mapMock, times(1)).containsKey(anyString());

        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(mapMock);
    }

    @Test
    public void testResponseNullAfterError() {

        //setup
        Request request = mock(Request.class);
        Map mapMock = mock(Map.class);
        mockStatic(RandomGenerator.class);

        when(request.getServiceName()).thenReturn("AmazonSQS");
        when(request.getHeaders()).thenReturn(mapMock);
        when(mapMock.get(REQUEST_ID)).thenReturn(REQUEST_VALUE);
        when(RandomGenerator.getUUIDRandomString()).thenReturn(REQUEST_VALUE);
        when(request.getHttpMethod()).thenReturn(HttpMethodName.GET);

        AmazonServiceException exception = new AmazonServiceException("AmazonServiceException");
        exception.setServiceName("AmazonSQS");
        exception.setStatusCode(500);
        exception.setRequestId(REQUEST_VALUE);

        //call
        awsLatencyRequestHandler.beforeRequest(request);
        awsLatencyRequestHandler.afterError(request, null, exception);

        //verify
        verify(request, times(4)).getHeaders();
        verify(request, times(2)).getParameters();
        verify(request, times(1)).getServiceName();
        verify(request, times(1)).getHttpMethod();

        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get(anyString());
        verify(mapMock, times(1)).containsKey(anyString());

        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(mapMock);

        PowerMockito.verifyStatic(times(1));
        RandomGenerator.getUUIDRandomString();
        PowerMockito.verifyNoMoreInteractions(RandomGenerator.class);
    }

    @Test
    public void testS3ExceptionAfterError() {

        //setup
        Request request = mock(Request.class);
        Map mapMock = mock(Map.class);
        mockStatic(RandomGenerator.class);

        when(request.getServiceName()).thenReturn("Amazon S3");
        when(request.getHeaders()).thenReturn(mapMock);
        when(mapMock.get(REQUEST_ID)).thenReturn(REQUEST_VALUE);
        when(RandomGenerator.getUUIDRandomString()).thenReturn(REQUEST_VALUE);
        when(request.getHttpMethod()).thenReturn(HttpMethodName.GET);

        AmazonS3Exception exception = new AmazonS3Exception("AmazonS3Exception");
        exception.setServiceName("Amazon S3");
        exception.setRequestId(REQUEST_VALUE);
        exception.setExtendedRequestId("extended value");
        exception.setStatusCode(500);
        exception.setRequestId(REQUEST_VALUE);

        //call
        awsLatencyRequestHandler.beforeRequest(request);
        awsLatencyRequestHandler.afterError(request, null, exception);

        //verify
        verify(request, times(4)).getHeaders();
        verify(request, times(1)).getServiceName();
        verify(request, times(1)).getHttpMethod();
        verify(request, times(2)).getParameters();

        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get(anyString());
        verify(mapMock, times(1)).containsKey(anyString());

        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(mapMock);

        PowerMockito.verifyStatic(times(1));
        RandomGenerator.getUUIDRandomString();
        PowerMockito.verifyNoMoreInteractions(RandomGenerator.class);
    }

    @Test
    public void testResponseNotNullAfterError() {

        //setup
        Request request = mock(Request.class);
        Response response = Mockito.mock(Response.class);
        HttpResponse httpResponse = Mockito.mock(HttpResponse.class);
        Map mapMock = mock(Map.class);
        mockStatic(RandomGenerator.class);

        when(request.getServiceName()).thenReturn("AmazonSQS");
        when(RandomGenerator.getUUIDRandomString()).thenReturn(REQUEST_VALUE);
        when(request.getHttpMethod()).thenReturn(HttpMethodName.GET);
        when(request.getHeaders()).thenReturn(mapMock);
        when(mapMock.get(REQUEST_ID)).thenReturn(REQUEST_VALUE);
        when(response.getHttpResponse()).thenReturn(httpResponse);
        when(httpResponse.getStatusCode()).thenReturn(403);
        AmazonServiceException exception = new AmazonServiceException("AmazonServiceException");
        exception.setServiceName("AmazonSQS");
        exception.setStatusCode(403);
        exception.setRequestId(REQUEST_VALUE);
        exception.setErrorCode("403");

        //call
        awsLatencyRequestHandler.beforeRequest(request);
        awsLatencyRequestHandler.afterError(request, response, exception);

        //verify
        verify(request, times(4)).getHeaders();
        verify(request, times(2)).getParameters();
        verify(request, times(1)).getHttpMethod();
        verify(request, times(1)).getServiceName();
        verify(response, times(2)).getHttpResponse();
        verify(httpResponse, times(1)).getStatusCode();
        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get(anyString());
        verify(mapMock, times(1)).containsKey(anyString());

        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
        verifyNoMoreInteractions(httpResponse);
        verifyNoMoreInteractions(mapMock);

        PowerMockito.verifyStatic(times(1));
        RandomGenerator.getUUIDRandomString();
        PowerMockito.verifyNoMoreInteractions(RandomGenerator.class);
    }

    @Test
    public void testResponseWithClientException() {

        //setup
        Request request = mock(Request.class);
        Response response = Mockito.mock(Response.class);
        Map mapMock = mock(Map.class);
        mockStatic(RandomGenerator.class);

        when(request.getServiceName()).thenReturn("AmazonSQS");
        when(RandomGenerator.getUUIDRandomString()).thenReturn(REQUEST_VALUE);
        when(request.getHeaders()).thenReturn(mapMock);
        when(request.getHttpMethod()).thenReturn(HttpMethodName.GET);
        when(mapMock.get(REQUEST_ID)).thenReturn(REQUEST_VALUE);
        when(response.getHttpResponse()).thenReturn(null);
        AmazonClientException exception = new AmazonClientException("AmazonClientException");

        //call
        awsLatencyRequestHandler.beforeRequest(request);

        awsLatencyRequestHandler.afterError(request, null, exception);

        //verify
        verify(request, times(4)).getHeaders();
        verify(request, times(2)).getParameters();
        verify(request, times(1)).getHttpMethod();
        verify(request, times(1)).getServiceName();
        verify(mapMock, times(1)).put(anyString(), anyString());
        verify(mapMock, times(1)).get(anyString());
        verify(mapMock, times(1)).containsKey(anyString());

        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
        verifyNoMoreInteractions(mapMock);

        PowerMockito.verifyStatic(times(1));
        RandomGenerator.getUUIDRandomString();
        PowerMockito.verifyNoMoreInteractions(RandomGenerator.class);
    }
}
