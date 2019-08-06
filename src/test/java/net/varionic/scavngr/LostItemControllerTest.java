package net.varionic.scavngr;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LostItemControllerTest {
    private OffsetDateTime reftime = OffsetDateTime.of(2010,10,10,10,10,10,10, ZoneOffset.ofHours(-7));

    @MockBean
    private LostItemRepository repo;

    @Autowired
    private LostItemController controller;

    @Rule
    public Snapshot snapshot = new Snapshot();

    @Test
    public void testListAll() throws IOException {
        var builder = Item.builder()
                .id(1L)
                .category("Foo")
                .description("Foo-bar")
                .email("foo@example.com")
                .whenLost(reftime)
                .lat(32.7174f)
                .lon(-117.1628f);

        var item1 = builder.build();
        var item2 = builder
                .id(2L)
                .description("Bar Baz")
                .build();
        var item3 = builder
                .id(3L)
                .description("Something Else")
                .build();

        Mockito.when(repo.allLost()).thenReturn(
                List.of(item1, item2, item3)
        );

        var result = controller.listAll();
        snapshot.matches(result);
    }

    @Test
    public void testCreateItem() throws IOException {
        var inputBuilder = Item.Input.builder()
                .description("Hello World")
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
        var item = Item.builder()
                .description("132413412341234")
                .build();

        Mockito.when(repo.save(any()))
            .thenReturn(item);

        var captor = ArgumentCaptor.forClass(Item.class);
        var result = controller.create(inputBuilder.build());
        verify(repo, atLeastOnce()).save(captor.capture());

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(item.getDescription());
        captor.getValue().setModified(reftime);

        snapshot.matches(captor.getValue());
    }

    @Test
    public void testUpdateItem() throws JsonProcessingException {
        String token = "how now brown cow";

        long id = 123L;
        var baseItem = Item.builder()
                .description("Hello")
                .category("Accessories")
                .email("foo@example.com")
                .modified(reftime)
                .id(id)
                .lat(1.23f)
                .lon(9.87f)
                .token(token)
                .whenLost(reftime)
                .build();

        var update = Item.Update.builder()
                .description("Stylish spork")
                .returned(true);

        assertThatThrownBy( () -> controller.update(1L, update.build()))
                .isInstanceOf(LostItemController.BadRequestException.class)
                .hasMessage("A token is required");

        update.token("Wrong token");
        assertThatThrownBy( () -> controller.update(1L, update.build()))
                .isInstanceOf(LostItemController.NotFoundException.class);

        Mockito.when(repo.findById(id)).thenReturn(Optional.of(baseItem));
        assertThatThrownBy( () -> controller.update(id, update.build()))
                .isInstanceOf(LostItemController.BadRequestException.class);

        update.token(token);
        controller.update(id, update.build());

        var captor = ArgumentCaptor.forClass(Item.class);
        verify(repo, atLeastOnce()).save(captor.capture());

        snapshot.matches(captor.getValue());
    }
}
