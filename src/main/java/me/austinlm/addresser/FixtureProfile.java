package me.austinlm.addresser;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FixtureProfile {

    private final String name;
    private final int dmxFootprint;
}
