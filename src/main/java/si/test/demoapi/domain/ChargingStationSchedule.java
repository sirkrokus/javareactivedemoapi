package si.test.demoapi.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document("charging_station_schedule")
public class ChargingStationSchedule {

    @Id
    private String id;
    private String stationId;
    private LocalDate bookDate;
    private int bookHour;

}
