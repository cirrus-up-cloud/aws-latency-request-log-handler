package cloud.cirrusup;

import java.util.UUID;

/**
 * Helper class used to generate random string values.
 */
public class RandomGenerator {

    /**
     * Private constructor to avoid class init.
     */
    private RandomGenerator() {

    }

    /**
     * Return a random string representing a UUID.
     *
     * @return random string
     */
    public static String getUUIDRandomString() {

        return UUID.randomUUID().toString();
    }
}
