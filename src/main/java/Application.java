import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);

    enum ExitCode {
        Ok,
        BadResponse,
        NoLocations,
        UnhandledExceptions
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.print("Usage: XXX CITY_NAME\n");
            return;
        }
        final String cityName = args[0];
        logger.debug("Retrieving information for {}...", cityName);

        try {
            ExitCode code = printSuggestion(cityName);
            System.exit(code.ordinal());
        } catch (Exception ex) {
            logger.error("Unhandled exception: ", ex);
            System.exit(ExitCode.UnhandledExceptions.ordinal());
        }
    }

    static ExitCode printSuggestion(String cityName) throws URISyntaxException,IOException {
        final CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                        "http://api.goeuro.com/api/v2/position/suggest/en/" + URLEncoder.encode(cityName, "UTF-8"));
        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.error("Bad response for city: {}. Status line: {}", cityName, response.getStatusLine().getReasonPhrase());
                return ExitCode.BadResponse;
            }
            List<Location> locations = parseLocation(response.getEntity().getContent());
            if (locations.isEmpty()) {
                logger.error("There are no suggestions for {}", cityName);
                return ExitCode.NoLocations;
            }
            logger.debug("Retrieved {} locations.", locations.size());
            writeLocationsAsCVS(new OutputStreamWriter(System.out), locations);
        }
        return ExitCode.Ok;
    }

    static void writeLocationsAsCVS(Writer writer, List<Location> locations) throws IOException {
        try (final CsvListWriter listWriter = new CsvListWriter(writer, CsvPreference.EXCEL_PREFERENCE)) {
            listWriter.writeHeader("id", "name", "type", "latitude", "longitude");
            for (Location location : locations) {
                listWriter.write(locationToCvs(location));
            }
        }
    }

    static List<Object> locationToCvs(Location location) {
        return Arrays.<Object>asList(location._id, location.name, location.type, location.position.latitude, location.position.longitude);
    }

    static List<Location> parseLocation(InputStream input) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return mapper.readValue(input, new TypeReference<List<Location>>() {
            });
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to parse json", exception);
        }
    }

    static class Location {
        public final String _id;
        public final String name;
        public final String type;
        public final GeoPosition position;

        @JsonCreator
        public Location(
                @JsonProperty(value = "_id", required = true) String _id,
                @JsonProperty(value = "name", required = true) String name,
                @JsonProperty(value = "type", required = true) String type,
                @JsonProperty(value = "geo_position", required = true) GeoPosition position) {
            this._id = _id;
            this.name = name;
            this.type = type;
            this.position = position;
        }
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
}
