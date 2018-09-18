package cloud.cirrusup;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link RandomGenerator class}.
 */
public class RandomGeneratorTest {

    @Test
    public void testComplete() {

        //call
        String rnd = RandomGenerator.getUUIDRandomString();

        //verify
        Assert.assertNotNull(rnd);
    }

}
