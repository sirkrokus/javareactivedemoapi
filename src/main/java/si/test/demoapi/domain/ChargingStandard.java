package si.test.demoapi.domain;

import java.util.Arrays;

public enum ChargingStandard {

    OCPP12, OCPP16, OCPP20;

    public static ChargingStandard fromString(String val) {
        boolean match = Arrays.stream(values()).anyMatch(t -> t.name().equalsIgnoreCase(val));
        return match ? ChargingStandard.valueOf(val) : null;
    }

}
