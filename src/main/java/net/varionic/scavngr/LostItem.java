package net.varionic.scavngr;

import lombok.*;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class LostItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private OffsetDateTime modified; // record modification time
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
