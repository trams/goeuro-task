import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class JacksonLearningTest {
    ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    @After
    public void tearDown() {
        mapper = null;
    }

    @Test
    public void testPojoSimple() throws IOException {
        Record record = mapper.readValue("{name: \"Chernov\", height: 175}", Record.class);
        Assert.assertEquals("Chernov", record.name);
        Assert.assertEquals(175, record.height);
    }

    @Test
    public void testExtraField() throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Record record = mapper.readValue("{name: \"Chernov\", height: 175, weight:70}", Record.class);
        Assert.assertEquals("Chernov", record.name);
        Assert.assertEquals(175, record.height);
    }

    @Test
    public void testListOfPojo() throws IOException {
        List<Record> records = mapper.readValue("[{name: \"Chernov\", height: 175}]", new TypeReference<List<Record>>() {});
        Assert.assertEquals(1, records.size());
        Assert.assertEquals("Chernov", records.get(0).name);
        Assert.assertEquals(175, records.get(0).height);
    }

    static class Record {
        public String name;
        public long height;
    }

    @Test
    public void testGeoPosition() throws IOException {
        GeoPosition position = mapper.readValue("{latitude: 52.39886, longitude: 13.06566}", GeoPosition.class);
        Assert.assertEquals(52.39886, position.latitude, 0.0001);
        Assert.assertEquals(13.06566, position.longitude, 0.0001);
    }

    @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
    public void testMissingField() throws IOException {
        mapper.readValue("{latitude: 52.39886}", GeoPosition.class);
    }

    static class GeoPosition {
        public final double latitude;
        public final double longitude;

        @JsonCreator
        public GeoPosition(
                @JsonProperty(value = "latitude", required = true) double latitude,
                @JsonProperty(value = "longitude", required = true) double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Test
    public void testComplex() throws IOException {
        Complex complex = mapper.readValue("{name: \"name\", position: {latitude: 52.39886, longitude: 13.06566}}", Complex.class);
        Assert.assertEquals("name", complex.name);
        Assert.assertEquals(52.39886, complex.position.latitude, 0.0001);
        Assert.assertEquals(13.06566, complex.position.longitude, 0.0001);
    }

    @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
    public void testComplexMissingField() throws IOException {
        mapper.readValue("{position: {latitude: 52.39886, longitude: 13.06566}}", Complex.class);
    }

    static class Complex {
        public String name;
        public GeoPosition position;

        @JsonCreator
        public Complex(
                @JsonProperty(value = "name", required = true) String name,
                @JsonProperty(value = "position", required = true) GeoPosition position) {
            this.name = name;
            this.position = position;
        }
    }
}
