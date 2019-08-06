package net.varionic.scavngr;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface LostItemRepository extends CrudRepository<LostItem, Long> {
    @Query("SELECT e FROM LostItem e WHERE e.isFound = FALSE")
    List<LostItem> allLost();

    @Query("SELECT e FROM LostItem e WHERE e.isFound = TRUE")
    List<LostItem> allFound();

    @Query("SELECT it FROM LostItem it WHERE it.isFound = FALSE AND it.whenLost < ?1")
    List<LostItem> lostBefore(OffsetDateTime cutoff);

    @Query("SELECT it FROM LostItem it WHERE it.isFound = FALSE AND it.whenLost < ?1 AND it.lat > ?2 AND it.lat < ?3 AND it.lon > ?4 AND it.lon < ?5")
    List<LostItem> boundedLostItems(OffsetDateTime cutoff, float latMin, float latMax, float lonMin, float lonMax);
}
