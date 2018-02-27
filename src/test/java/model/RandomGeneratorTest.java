package model;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by andreyserebryanskiy on 10/01/2018.
 */
public class RandomGeneratorTest {
    @Test
    public void generatesRandomNumbers() throws Exception {
        RandomGenerator generator = new RandomGenerator(0, 100, false);
        Set<Integer> result = new HashSet<>();

        for (int i = 0; i < 100; i++) result.add(generator.generate());

        assertThat(result.size(), greaterThan(50));
    }

    @Test
    public void ifRepeatsNotAllowedDoNotGenerateThem() throws Exception {
        RandomGenerator generator = new RandomGenerator(0, 100, true);
        Set<Integer> result = new HashSet<>();

        for (int i = 0; i <= 100; i++) result.add(generator.generateWithoutRepeats());

        assertThat(result.size(), is(101));
    }
}