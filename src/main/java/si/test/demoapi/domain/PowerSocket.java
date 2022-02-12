package si.test.demoapi.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PowerSocket {

    private int power;
    private PowerSocketType socketType;

}
