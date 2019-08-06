package net.varionic.scavngr;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MatchedItemRepository extends CrudRepository<MatchedItem, MatchedItem.ID> {
    @Query("SELECT it from MatchedItem it WHERE it.id.lostItem = ?1")
    Iterable<MatchedItem> findByLost(Long lostId);
}
