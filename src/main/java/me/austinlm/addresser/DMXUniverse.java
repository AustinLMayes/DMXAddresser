package me.austinlm.addresser;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Data
public class DMXUniverse {

    private final int id;
    private final String name;
    // Sorted by priority
    private final List<FixtureData> fixtures = new ArrayList<>();
    private final List<Integer> reservedAddresses = new ArrayList<>();

    public void addFixture(Fixture fixture, String descriptor, int count, int priority) {
        fixtures.add(new FixtureData(fixture, descriptor, count, priority));
    }

    public void reserveAddresses(int start, int end) {
        for (int i = start; i <= end; i++) {
            reservedAddresses.add(i);
        }
    }

    public void reserveAddresses(int... addresses) {
        for (int address : addresses) {
            reservedAddresses.add(address);
        }
    }

    public void calculateAddresses() {
        fixtures.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
        int neededAddresses = 0;
        for (FixtureData fixture : fixtures) {
            neededAddresses += fixture.getFixture().getPreferredProfile().getDmxFootprint() * fixture.getCount();
            fixture.setCurrentFootPrint(fixture.getFixture().getPreferredProfile().getDmxFootprint());
        }
        boolean full = reservedAddresses.size() + neededAddresses > 512;
        boolean canUseFifths = true;
        if (full) {
            System.out.println("Not enough addresses available for universe " + name + " (needed: " + neededAddresses + ", available: " + (512 - reservedAddresses.size()) + ")");
            fixtures.sort(Comparator.comparingInt(FixtureData::getPriority));
            for (FixtureData fixture : fixtures) {
                for (FixtureProfile profile : fixture.getFixture().getBackupProfiles()) {
                    if (profile.getDmxFootprint() <= fixture.getCurrentFootPrint()) {
                        neededAddresses -= fixture.getCurrentFootPrint() * fixture.getCount();
                        fixture.setCurrentFootPrint(profile.getDmxFootprint());
                        neededAddresses += profile.getDmxFootprint() * fixture.getCount();
                        System.out.println("Using backup profile " + profile.getName() + " for fixture " + fixture.getDescriptor());
                        break;
                    }
                }
                if (reservedAddresses.size() + neededAddresses < 512) {
                    break;
                }
            }
        } else {
            int currentAddress = 1;
            boolean first = true;
            for (FixtureData fixture : fixtures) {
                if (first) {
                    first = false;
                    continue;
                }
                currentAddress += fixture.getCurrentFootPrint() * fixture.getCount();
                int fifths = currentAddress % 5;
                if (fifths != 0) {
                    currentAddress += 5 - fifths;
                }
                if (currentAddress > 512) {
                    canUseFifths = false;
                    break;
                }
            }
        }
        if (reservedAddresses.size() + neededAddresses > 512) {
            throw new IllegalStateException("Not enough addresses available for universe " + name + " (needed: " + neededAddresses + ", available: " + (512 - reservedAddresses.size()) + ")");
        }
        fixtures.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
        int currentAddress = 1;
        int fixtureId = id * 1000;
        for (FixtureData fixture : fixtures) {
            fixture.setId(fixtureId);
            for (int i = 0; i < fixture.getCount(); i++) {
                int address = currentAddress;
                while (reservedAddresses.contains(address)) {
                    address++;
                }
                fixture.getAddresses()[i] = address;
                currentAddress = address + fixture.getCurrentFootPrint();
            }
            if (canUseFifths) {
                int fifths = currentAddress % 5;
                if (fifths != 0) {
                    currentAddress += 5 - fifths;
                }
            }
            fixtureId += 100 - (fixtureId % 100);
        }
        System.out.println("Addresses for universe " + name + ":");
        for (FixtureData fixture : fixtures) {
            System.out.println(fixture.describe());
        }
    }

    @Getter @Setter
    public static final class FixtureData {
        private final Fixture fixture;
        private final String descriptor;
        private final int count;
        private final int priority;
        private int currentFootPrint;
        private final int[] addresses;
        private int id;

        public FixtureData(Fixture fixture, String descriptor, int count, int priority) {
            this.fixture = fixture;
            this.descriptor = descriptor;
            this.count = count;
            this.priority = priority;
            this.addresses = new int[count];
        }

        public String describe() {
            StringBuilder builder = new StringBuilder();
            builder.append(descriptor).append(" ").append(count).append("x Starting Address: ").append(addresses[0]).append("\n");
            for (int i = 0; i < count; i++) {
                builder.append(addresses[i]).append(" - ").append(descriptor).append(" ").append(i + 1).append("\n");
            }
            return builder.toString();
        }
    }
}
