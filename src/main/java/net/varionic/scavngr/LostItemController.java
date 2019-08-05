package net.varionic.scavngr;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import static io.vavr.API.*;

@Log4j2
@RequestMapping("/lost")
@RestController
public class LostItemController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class BadRequestException extends RuntimeException {
        BadRequestException() { }

        BadRequestException(String msg) {
            super(msg);
        }
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        NotFoundException() { }

        NotFoundException(String msg) {
            super(msg);
        }
    }

    @Autowired
    private LostItemRepository repo;

    private LostItemMapper mapper = LostItemMapper.INSTANCE;

    @GetMapping
    public List<LostItem.Output> listAll() {
        var foo = For(repo.findAll())
                .yield(mapper::toOutput);

        return foo.toJavaList();
    }

    private void validateCreate(LostItem.Input item) {
        // TODO use Vavr Validation applicative functor
        if (item.getLat() == null)
            throw new BadRequestException("Latitude is required");
        if (item.getLon() == null)
            throw new BadRequestException("Longitude is required");
    }

    @PostMapping
    LostItem.Output create(@RequestBody LostItem.Input item) {
        validateCreate(item);
        var entity = mapper.fromInput(item);

        entity.setToken("abc123"); // TODO cryptographic token

        if (item.getWhenLost() == null) {
            ZoneOffset zoneOffSet= ZoneOffset.of("-08:00"); // TODO get from client
            entity.setWhenLost(OffsetDateTime.now(zoneOffSet));
        }

        var record = repo.save(entity);
        log.info("Created new LostItem:" + record);

        return mapper.toOutput(record);
    }

    // If the item with {id} exists and the request token matches, update the item.
    @PutMapping("/{id}")
    LostItem.Output update(@PathVariable Long id, @RequestBody LostItem.Update up) {
        var token = up.getToken();
        if (token == null)
            throw new BadRequestException("A token is required");

        // Find the item and match the tokens
        var item = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found"));

        if (!Objects.equals(item.getToken(), token))
            throw new BadRequestException("Invalid token");

        var updated = mapper.updateItem(up, item);
        var result = repo.save(updated);
        return mapper.toOutput(result);
    }

    @PostConstruct
    void populate() {
        ZoneOffset zoneOffSet = ZoneOffset.of("-08:00"); // TODO get from client
        var now = OffsetDateTime.now(zoneOffSet);
        var item1 = new LostItem.Input("Clothing", "Foobar", "hello@world.net", now, 32.7174f, -117.1628f);
        var item2 = new LostItem.Input("Clothing", "Barbaz", "hello@world.net", now, 32.7174f, -117.1628f);
        this.create(item1);
        this.create(item2);
    }
}
