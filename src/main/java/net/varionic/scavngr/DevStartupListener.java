package net.varionic.scavngr;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Log4j2
@Component
@Profile("dev")
public class DevStartupListener {
    @Autowired
    LostItemController lostCtrl;

    @Autowired
    FoundItemController foundCtrl;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info(">>>>>>>>>>>> Populating Development database <<<<<<<<<<<");

        ZoneOffset zoneOffSet = ZoneOffset.of("-08:00"); // TODO get from client
        var now = OffsetDateTime.now(zoneOffSet);
        var builder = LostItem.Input.builder()
                .category("Clothing")
                .email("hello@world.net")
                .whenLost(now.minusDays(3))
                .lat(32.7174f)
                .lon(-117.1628f);
        var item1 = builder.description("Foobar").build(); //new LostItem.Input("Clothing", "Foobar", "hello@world.net", now, 32.7174f, -117.1628f, false);
        var item2 = builder.description("Barbaz").build(); //new LostItem.Input("Clothing", "Barbaz", "hello@world.net", now, 32.7174f, -117.1628f, false);
        lostCtrl.create(item1);
        lostCtrl.create(item2);

        var builder2 = LostItem.Input.builder()
                .category("Electronics")
                .email("hello@world.net")
                .whenLost(now)
                .lat(32.7174f)
                .lon(-117.1628f);
        var item12 = builder2.description("Old Widget").build();
        var item22 = builder2.description("Futuristic walkie-talkie").build();
        foundCtrl.create(item12);
        foundCtrl.create(item22);
    }
}