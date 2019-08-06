package net.varionic.scavngr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchedItem {
    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ID implements Serializable {
        @Column(name = "lostItem_id")
        private Long lostItem;

        @Column(name= "foundItem_id")
        private Long foundItem;
    }

    @EmbeddedId
    private ID id;

    @MapsId("id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "lostItem_id", referencedColumnName = "id")
    Item lostItem;

    @MapsId("id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "foundItem_id", referencedColumnName = "id")
    Item foundItem;

    public MatchedItem(Item lost, Item found) {
        this.id = new ID(lost.getId(), found.getId());
        this.lostItem = lost;
        this.foundItem = found;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Output {
        Item.Output lostItem;
        Item.Output foundItem;
    }
}
