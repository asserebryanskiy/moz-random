package model;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class RandomGenerator {
    private final int min;
    private final int max;
    private final boolean allowRepeats;
    private Set<Integer> used;

    public RandomGenerator(int min, int max, boolean disallowRepeats) {
        this.min = min;
        this.max = max;
        this.allowRepeats = disallowRepeats;
        if (disallowRepeats) used = new HashSet<>();
    }

    public int generateWithoutRepeats() {
        int r = getRandom();
        if (allowRepeats) {
            if (used.size() == max - min) used.clear();
            while (used.contains(r)) r = getRandom();
            used.add(r);
        }
        return r;
    }

    public int generate() {
        return getRandom();
    }

    private int getRandom() {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public int getMin() {
        return min;
    }
}
