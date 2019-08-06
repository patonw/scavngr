package net.varionic.scavngr;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static io.vavr.API.For;

@Log4j2
@RequestMapping(value = "/api/v1/found", produces = "application/json")
@RestController
public class FoundItemController extends BaseController {

    @Autowired
    MatchMaker matcher;

    @GetMapping
    public List<LostItem.Output> listAll() {
        var foo = For(repo.allFound())
                .yield(mapper::toOutput);

        return foo.toJavaList();
    }

    @PostMapping
    LostItem.Output create(@RequestBody LostItem.Input item) {
        validateCreate(item);
        var entity = mapper.fromInput(item);

        entity.setModified(OffsetDateTime.now());
        entity.setToken("abc123"); // TODO cryptographic token
        entity.setFound(true);

        var record = repo.save(entity);
        log.info("Created new Found item:" + record);

        return mapper.toOutput(record);
    }

    @GetMapping("/{id}")
    LostItem.Output findOne(@PathVariable Long id) {
        var result = repo.findById(id)
                .filter(LostItem::isFound)
                .orElseThrow(() -> new NotFoundException("Item " + id + " does not exist"));

        matcher.processMatches(result);
        log.info("Called async method");

        return mapper.toOutput(result);
    }

    // If the item with {id} exists and the request token matches, update the item.
    @PutMapping("/{id}")
    LostItem.Output update(@PathVariable Long id, @RequestBody LostItem.Update up) {
        return mapper.toOutput(updateItem(id, up));
    }
}
