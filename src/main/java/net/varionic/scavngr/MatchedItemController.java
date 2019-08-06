package net.varionic.scavngr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static io.vavr.API.For;

@Slf4j
@RequestMapping("/api/v1/matched")
@RestController
public class MatchedItemController {
    @Autowired
    MatchedItemRepository matchRepo;

    @Autowired
    LostItemRepository lostRepo;

    @Autowired
    ItemMapper mapper;

    @GetMapping
    public List<MatchedItem.Output> listAll(
            @RequestParam(value="token", required=false) String token
    ) {
        if (token != null)
            log.info("Got a token: " + token);

        return For(matchRepo.findAll()).yield(mapper::toOutput).toJavaList();
    }

    @GetMapping("/lost/{id}")
    public List<Item.Output> matchesByLost(
            @PathVariable Long id,
            @RequestParam(value="token", required=false) String token
    ) {
        if (token == null)
            throw new BaseController.BadRequestException("Token is required");

        var lost = lostRepo.findById(id)
                .orElseThrow(() -> new BaseController.NotFoundException("Item " + id + " does not exist"));

        if (!token.equals(lost.getToken()))
            throw new BaseController.BadRequestException("Invalid token");

        return For(matchRepo.findByLost(id))
                .yield(mapper::toOutput)
                .map(it -> it.foundItem)
                .toJavaList();
    }
}
