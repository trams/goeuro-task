import junit.framework.Assert;
import org.junit.Test;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class CVSTest {
    @Test
    public void testBasic() throws IOException {
        List<Object> data = Arrays.<Object>asList("id", "name", "type", 20.0, 0.5);
        final StringWriter writer = new StringWriter();
        CsvListWriter listWriter = new CsvListWriter(writer, CsvPreference.EXCEL_PREFERENCE);
        listWriter.writeHeader("id", "name", "type", "latitude", "longitude");
        listWriter.write(data);
        listWriter.flush();

        Assert.assertEquals("id,name,type,latitude,longitude\nid,name,type,20.0,0.5\n", writer.toString());
    }
}
