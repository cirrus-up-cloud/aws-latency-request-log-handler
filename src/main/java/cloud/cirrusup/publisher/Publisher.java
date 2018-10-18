package cloud.cirrusup.publisher;

import cloud.cirrusup.publisher.model.PublishedInfo;

/**
 * Model for any publisher.
 */
public interface Publisher {

    String LOG_NAME = "aws-latency-log";

    /**
     * Publish info in the underlying destination.
     *
     * @param info information to be published
     */
    void publish(PublishedInfo info);
}
