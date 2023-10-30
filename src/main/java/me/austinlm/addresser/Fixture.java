package me.austinlm.addresser;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Builder
@Data
public class Fixture {

    private final String name;
    private final FixtureProfile preferredProfile;
    @Singular
    private final List<FixtureProfile> backupProfiles;
}
