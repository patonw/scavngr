package net.varionic.scavngr;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;

public interface LostItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT e FROM Item e WHERE e.isFound = FALSE")
    List<Item> allLost();

    @Query("SELECT e FROM Item e WHERE e.isFound = TRUE")
    List<Item> allFound();

    @Query("SELECT it FROM Item it WHERE it.isFound = FALSE AND it.whenLost < ?1")
    List<Item> lostBefore(OffsetDateTime cutoff);

    @Query("SELECT it FROM Item it WHERE it.isFound = FALSE AND it.whenLost < ?1 AND it.lat > ?2 AND it.lat < ?3 AND it.lon > ?4 AND it.lon < ?5")
    List<Item> boundedLostItems(OffsetDateTime cutoff, float latMin, float latMax, float lonMin, float lonMax);
}
