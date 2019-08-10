package net.varionic.scavngr.controller;

import lombok.extern.log4j.Log4j2;
import net.varionic.scavngr.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

import static io.vavr.API.For;

@Log4j2
@RequestMapping(value = "/api/v1/found", produces = "application/json")
@RestController
public class FoundItemController extends BaseController {
    @Autowired
    MatchMaker matcher;

    @GetMapping
    public List<Item.Output> listAll() {
        var foo = For(repo.allFound())
                .yield(mapper::toOutput);

        return foo.toJavaList();
    }

    @PostMapping
    public Item.Output create(@RequestBody Item.Input item) {
        validateCreate(item);
        var entity = mapper.fromInput(item);

        entity.setModified(OffsetDateTime.now());
        entity.setToken("abc123"); // TODO cryptographic token
        entity.setFound(true);
        entity = repo.save(entity);

        log.info("Created new Found item:" + entity);

        return mapper.toOutput(entity);
    }

    // TODO show extended information when token matches
    @GetMapping("/{id}")
    public Item.Output findOne(@PathVariable Long id,
                        @RequestParam(value="token", required=false) String token) {
        var result = repo.findById(id)
                .filter(Item::isFound)
                .orElseThrow(() -> new NotFoundException("Item " + id + " does not exist"));

        matcher.processMatches(result);
        log.info("Called async method");

        return mapper.toOutput(result);
    }

    // If the item with {id} exists and the request token matches, update the item.
    @PutMapping("/{id}")
    public Item.Output update(@PathVariable Long id, @RequestBody Item.Update up) {
        return mapper.toOutput(updateItem(id, up));
    }
}
