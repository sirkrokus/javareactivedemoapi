package si.test.demoapi.domain;

import java.util.Arrays;

public enum PowerSocketType {

    TYPE1, TYPE2, CCS, CHADEMO, TESLA;

    public static PowerSocketType fromString(String val) {
        boolean match = Arrays.stream(values()).anyMatch(t -> t.name().equalsIgnoreCase(val));
        return match ? PowerSocketType.valueOf(val) : null;
    }

}
