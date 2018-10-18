package cloud.cirrusup.publisher;

import cloud.cirrusup.publisher.Publisher;
import cloud.cirrusup.publisher.model.PublishedInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloud.cirrusup.publisher.model.PublishedInfo.API_METHOD;
import static cloud.cirrusup.publisher.model.PublishedInfo.ERROR_SUMMARY;
import static cloud.cirrusup.publisher.model.PublishedInfo.HTTP_METHOD;
import static cloud.cirrusup.publisher.model.PublishedInfo.REQUEST_ID;
import static cloud.cirrusup.publisher.model.PublishedInfo.SERVICE_NAME;

/**
 * Publisher that uses a text log file to add plain info.
 */
public class PlainLogPublisher implements Publisher {

    private static final Logger LOG = LoggerFactory.getLogger(LOG_NAME);

    /**
     * {@inheritDoc}
     */
    @Override
    public void publish(PublishedInfo info) {

        LOG.info("Request ID [{}] to [{}/{}] took [{}] milliseconds and returned the RESPONSE_CODE [{}]",
                info.getInfo(REQUEST_ID), info.getInfo(SERVICE_NAME), info.getInfo(HTTP_METHOD), info.getLatency(), info.getStatusCode());

        if (info.hasInfo(ERROR_SUMMARY)) {
            LOG.warn("Request ID [{}], RESPONSE_CODE [{}], MESSAGE [{}]", info.getInfo(REQUEST_ID), info.getStatusCode(), info.getInfo(ERROR_SUMMARY));
        }

        if(info.hasInfo(API_METHOD)){
            LOG.info("Request ID [{}], API method [{}]", info.getInfo(REQUEST_ID), info.getInfo(API_METHOD));
        }
    }
}
