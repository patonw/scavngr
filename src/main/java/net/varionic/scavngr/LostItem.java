package net.varionic.scavngr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.OffsetDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LostItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String category;
    private String description;
    private String email;
    private String token;
    private OffsetDateTime whenLost;
    private Float lat; // TODO Give PostGIS a try
    private Float lon;
    private boolean isFound;
    private boolean returned;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Input {
        private String category;
        private String description;
        private String email;
        private OffsetDateTime whenLost;
        private Float lat;
        private Float lon;
        private boolean isFound;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class Update {
        private String category;
        private String description;
        private String token;
        private Float lat;
        private Float lon;
        private boolean returned;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Output {
        private Long id;
        private String category;
        private String description;
        private OffsetDateTime whenLost;
        private Float lat;
        private Float lon;
        private boolean returned;
    }
}
