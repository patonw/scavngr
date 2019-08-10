package net.varionic.scavngr;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ScavngrIntegrationTests {
    private OffsetDateTime reftime = OffsetDateTime.of(2010,10,10,10,10,10,10, ZoneOffset.ofHours(-7));

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("example")
            .withUsername("sa")
            .withPassword("sa");

    @Rule
    public Snapshot snapshot = new Snapshot();

    @Autowired
    private LostItemController controller;

    @Autowired
    private LostItemRepository repo;

    @BeforeClass
    public static void setup() {
        log.info(">>>>> Using JDBC url " + postgreSQLContainer.getJdbcUrl() + " <<<<<");
        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
    }

    @Test
    public void testCreateItem() throws JsonProcessingException {
        var description = "Hello World";
        var inputBuilder = Item.Input.builder()
                .description(description)
                .email("foo@example.com")
                .whenLost(reftime)
                .category("Sporting Goods");

        assertThatThrownBy(() -> controller.create(inputBuilder.build()))
                .isInstanceOf(LostItemController.BadRequestException.class)
                .hasMessageContaining("Latitude is required");

        inputBuilder.lat(10.0f);
        assertThatThrownBy(() -> controller.create(inputBuilder.build()))
                .isInstanceOf(LostItemController.BadRequestException.class)
                .hasMessageContaining("Longitude is required");


        inputBuilder.lon(20.0f);
        var result = controller.create(inputBuilder.build());
        repo.flush();

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(description);
        result.setId(42L);

        snapshot.matches(result);
    }


    @Test
    public void testUpdateItem() throws JsonProcessingException {
        String token = "how now brown cow";

        var baseItem = Item.builder()
                .description("Hello")
                .category("Accessories")
                .email("foo@example.com")
                .modified(reftime)
                .lat(1.23f)
                .lon(9.87f)
                .token(token)
                .whenLost(reftime);

        var id1 = repo.save(baseItem.build()).getId();

        baseItem.token("whoops");
        var id2 = repo.save(baseItem.build()).getId();
        repo.flush();

        var update = Item.Update.builder()
                .description("Stylish spork")
                .returned(true);

        assertThatThrownBy( () -> controller.update(id2, update.build()))
                .isInstanceOf(LostItemController.BadRequestException.class)
                .hasMessage("A token is required");

        update.token("Wrong token");
        assertThatThrownBy( () -> controller.update(id2, update.build()))
                .isInstanceOf(LostItemController.BadRequestException.class);

        assertThatThrownBy( () -> controller.update(id1, update.build()))
                .isInstanceOf(LostItemController.BadRequestException.class);

        update.token(token);
        var result = controller.update(id1, update.build());
        repo.flush();


        snapshot.matches(result);
        snapshot.matches(repo.findById(id2).orElseThrow());
    }
}
