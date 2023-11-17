package lang;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class MustBeFailedTest {
    
    @Test
    public void testMustBeFailed() {
        String expected = "MUST BE FAILED THIS TEST.";
        String actual = "Failed test.";
        assertThat(actual, is(expected));
    }

}
