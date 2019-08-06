package net.varionic.scavngr;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.ZoneOffset;
import java.time.OffsetDateTime;
import java.util.List;
import static io.vavr.API.*;

@Log4j2
@RequestMapping(value = "/api/v1/lost", produces = "application/json")
@RestController
public class LostItemController extends BaseController {
    @Autowired
    private LostItemRepository repo;

    @GetMapping
    public List<LostItem.Output> listAll() {
        var foo = For(repo.allLost())
                .yield(mapper::toOutput);

        return foo.toJavaList();
    }

    @PostMapping
    LostItem.Output create(@RequestBody LostItem.Input item) {
        validateCreate(item);
        var entity = mapper.fromInput(item);

        entity.setModified(OffsetDateTime.now()); // TODO configure MapStruct to do this
        entity.setToken("abc123"); // TODO generate cryptographic token

        var record = repo.save(entity);
        log.info("Created new LostItem:" + record);

        return mapper.toOutput(record);
    }

    @GetMapping("/{id}")
    LostItem.Output findOne(@PathVariable Long id) {
        var result = repo.findById(id)
                .filter(it -> !it.isFound())
                .orElseThrow(() -> new NotFoundException("Item " + id + " does not exist"));
        return mapper.toOutput(result);
    }

    // If the item with {id} exists and the request token matches, update the item.
    @PutMapping("/{id}")
    LostItem.Output update(@PathVariable Long id, @RequestBody LostItem.Update up) {
        return mapper.toOutput(updateItem(id, up));
    }
}
