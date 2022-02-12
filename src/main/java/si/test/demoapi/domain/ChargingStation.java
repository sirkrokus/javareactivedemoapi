package si.test.demoapi.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Document("charging_station")
public class ChargingStation {

    @Id
    private String id;
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Location location;
    private BigDecimal price;
    private List<PowerSocket> powerSocketList;
    private ChargingStandard standard;
    private boolean active;

    public ChargingStation(ChargingStandard standard, Location location, BigDecimal price) {
        this.location = location;
        this.price = price;
        this.standard = standard;
    }
}
