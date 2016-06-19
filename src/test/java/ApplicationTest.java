import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class ApplicationTest {

    @Test
    public void testHandleSpaces() throws IOException, URISyntaxException {
        Assert.assertEquals(Application.ExitCode.NoLocations, Application.printSuggestion("Ber lin"));
    }

    @Test
    public void testHandleTwoDots() throws IOException, URISyntaxException {
        Assert.assertEquals(Application.ExitCode.BadResponse, Application.printSuggestion("Berlin/.."));
    }

    @Test
    public void testHandleRussian() throws IOException, URISyntaxException {
        Assert.assertEquals(Application.ExitCode.Ok, Application.printSuggestion("../ru/Берлин"));
    }
}
