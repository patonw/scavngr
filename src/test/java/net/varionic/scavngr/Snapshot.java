package net.varionic.scavngr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Snapshot extends TestWatcher {
    private String name;
    private Description desc;
    private Integer count;

    private ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setDateFormat(new StdDateFormat().withColonInTimeZone(true))
            .configure(DeserializationFeature.USE_LONG_FOR_INTS, true)
            .registerModule(new JavaTimeModule());

    @Override
    protected void starting(Description d) {
        desc = d;
        name = d.getTestClass().getSimpleName() + "__" + d.getMethodName();
        count = -1;
    }

    public void matches(Object obj) throws JsonProcessingException {
        var json = mapper.writeValueAsString(obj);

        try {
            var suffix = (count++ > -1) ? "_" + count : "";
            var snapName = name + suffix + ".snap";

            InputStream resource = desc.getTestClass().getResourceAsStream(snapName);
            assertThat(resource).as("Snapshot %s exists", snapName).isNotNull();

            var expected = new String(resource.readAllBytes());
            assertThat(json).isEqualTo(expected);
        } catch (IOException e) {
            // TODO write out new snapshot
            e.printStackTrace();
        }
    }
}