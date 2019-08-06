package net.varionic.scavngr;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ScheduledTasks {
    @Scheduled(fixedRate = 600_000) // Every 10 minutes
    public void batchTask() {
        log.info(">> Running scheduled task <<");
        // TODO Batch notifications by email address and send
    }
}
