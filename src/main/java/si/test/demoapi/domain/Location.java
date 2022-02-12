package si.test.demoapi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Location {

    private String type = "Point";
    private List<Double> coordinates = new ArrayList<>();

    public Location() {
    }

    public Location(double longitude, double latitude) {
        coordinates = new ArrayList<>() {
            {add(longitude);}
            {add(latitude);}
        };
    }

}
