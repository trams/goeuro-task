import junit.framework.Assert;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;

public class RESTTest {
    @Test
    public void testBasic() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://api.goeuro.com/api/v2/position/suggest/en/NO_NAME_CITY");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();

            Assert.assertEquals("[]", EntityUtils.toString(entity));
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }

    @Test
    public void testGetAndParse() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://api.goeuro.com/api/v2/position/suggest/en/Berlin");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            Assert.assertEquals(200, response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();

            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
    }
}
