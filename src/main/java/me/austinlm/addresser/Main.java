package me.austinlm.addresser;

public class Main {

    public static void main(String[] args) {
        FixtureProfile rgba = FixtureProfile.builder()
                .name("RGBA")
                .dmxFootprint(4).build();
        FixtureProfile rgbax8 = FixtureProfile.builder()
                .name("RGBA x8")
                .dmxFootprint(34).build();
        FixtureProfile rgbax4 = FixtureProfile.builder()
                .name("RGBA x4")
                .dmxFootprint(18).build();
        FixtureProfile rgbax2 = FixtureProfile.builder()
                .name("RGBA x2")
                .dmxFootprint(10).build();
        FixtureProfile rgb = FixtureProfile.builder()
                .name("RGB")
                .dmxFootprint(3).build();
        Fixture slimPar64RGBA = Fixture.builder()
                .name("Chauvet SlimPAR 64 RGBA")
                .preferredProfile(rgba)
                .backupProfile(rgb)
                .build();
        Fixture slimPar64 = Fixture.builder()
                .name("Chauvet SlimPAR 64")
                .preferredProfile(rgba)
                .build();
        Fixture slimPar56 = Fixture.builder()
                .name("Chauvet SlimPAR 56")
                .preferredProfile(rgb)
                .build();
        Fixture megaBarRGBA = Fixture.builder()
                .name("American DJ MegaBar RGBA")
                .preferredProfile(rgbax8)
                .backupProfile(rgbax4).backupProfile(rgbax2).backupProfile(rgba)
                .build();
        Fixture cobCannon = Fixture.builder()
                .name("American DJ COB Cannon Wash")
                .preferredProfile(rgba)
                .build();

        DMXUniverse universe = new DMXUniverse(2, "2");
        universe.addFixture(slimPar64RGBA, "Upstage Over", 10, 990);
        universe.addFixture(slimPar64RGBA, "Midstage Over", 8, 980);
        universe.addFixture(slimPar64RGBA, "Tree Left", 5, 890);
        universe.addFixture(slimPar64RGBA, "Tree Right", 5, 880);
        universe.calculateAddresses();
    }


}
