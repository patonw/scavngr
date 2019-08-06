package net.varionic.scavngr;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MatchMaker {
    @Autowired
    LostItemRepository repo;

    @Async
    void processMatches(LostItem found) {
        var candidates = repo.boundedLostItems(
                found.getWhenLost(),
                found.getLat() - 1.0f,
                found.getLat() + 1.0f,
                found.getLon() - 1.0f,
                found.getLon() + 1.0f);

        log.info("Candidates are " + candidates);
        // TODO persist matches and queue notifications
    }
}
