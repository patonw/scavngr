package net.varionic.scavngr;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LostItemRepository extends CrudRepository<LostItem, Long> {
    @Query("SELECT e FROM LostItem e WHERE e.isFound = FALSE")
    List<LostItem> allLost();

    @Query("SELECT e FROM LostItem e WHERE e.isFound = TRUE")
    List<LostItem> allFound();
}
