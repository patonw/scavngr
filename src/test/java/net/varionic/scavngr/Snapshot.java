package net.varionic.scavngr;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    private ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setDateFormat(new StdDateFormat().withColonInTimeZone(true))
            .registerModule(new JavaTimeModule());

    @Override
    protected void starting(Description d) {
        desc = d;
        name = d.getTestClass().getSimpleName() + "__" + d.getMethodName() + ".snap";
    }

    /**
     * @return the name of the currently-running test method
     */
    public String get() {
        return name;
    }

    public void matches(Object obj) throws JsonProcessingException {
        var json = mapper.writeValueAsString(obj);

        // TODO detect exists
        try {
            InputStream resource = desc.getTestClass().getResourceAsStream(this.get());
            assertThat(resource).as("Snapshot %s exists", this.get()).isNotNull();

            var expected = new String(resource.readAllBytes());
            assertThat(json).isEqualTo(expected);
        } catch (IOException e) {
            // TODO write out new snapshot
            e.printStackTrace();
        }
    }
}