package net.varionic.scavngr.controller;

import lombok.extern.slf4j.Slf4j;
import net.varionic.scavngr.model.Item;
import net.varionic.scavngr.model.MatchedItem;
import net.varionic.scavngr.repo.LostItemRepository;
import net.varionic.scavngr.repo.MatchedItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MatchMaker {
    @Autowired
    LostItemRepository itemRepo;

    @Autowired
    MatchedItemRepository matchRepo;

    @Async
    Future<Iterable<MatchedItem>> processMatches(Item found) {
        // TODO more intelligent matching
        var candidates = itemRepo.boundedLostItems(
                found.getWhenLost(),
                found.getLat() - 1.0f,
                found.getLat() + 1.0f,
                found.getLon() - 1.0f,
                found.getLon() + 1.0f);

        var matches = candidates.stream()
                .map(it -> new MatchedItem(it, found))
                .collect(Collectors.toList());

        return new AsyncResult<>(matchRepo.saveAll(matches));
    }
}
