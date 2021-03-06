package io.improbable.keanu.vertices.generic;

import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.generic.nonprobabilistic.MultiplexerVertex;
import io.improbable.keanu.vertices.generic.probabilistic.discrete.SelectVertex;
import io.improbable.keanu.vertices.intgr.IntegerVertex;
import io.improbable.keanu.vertices.intgr.nonprobabilistic.ConstantIntegerVertex;
import io.improbable.keanu.vertices.intgr.probabilistic.UniformIntVertex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class MultiplexerVertexTest {
    private final Logger log = LoggerFactory.getLogger(MultiplexerVertexTest.class);

    private int N = 100000;
    private double epsilon = 0.01;

    @Test
    public void multiplexerGivesReasonableDistributionOfSamples() {
        Random selectorRandom = new Random(1);
        IntegerVertex selectorOrigin = new ConstantIntegerVertex(0);
        IntegerVertex selectorBound = new ConstantIntegerVertex(2);
        IntegerVertex selectorControlVertex = new UniformIntVertex(selectorOrigin, selectorBound, selectorRandom);
        Map<TestEnum, Double> expected = new HashMap<>();
        expected.put(TestEnum.A, 0.25);
        expected.put(TestEnum.B, 0.25);
        expected.put(TestEnum.C, 0.25);
        expected.put(TestEnum.D, 0.25);

        LinkedHashMap<TestEnum, DoubleVertex> optionGroup1 = new LinkedHashMap<>();
        optionGroup1.put(TestEnum.A, new ConstantDoubleVertex(0.5));
        optionGroup1.put(TestEnum.B, new ConstantDoubleVertex(0.5));
        Random r1 = new Random(1);
        SelectVertex<TestEnum> select1 = new SelectVertex<>(optionGroup1, r1);

        LinkedHashMap<TestEnum, DoubleVertex> optionGroup2 = new LinkedHashMap<>();
        optionGroup2.put(TestEnum.C, new ConstantDoubleVertex(0.5));
        optionGroup2.put(TestEnum.D, new ConstantDoubleVertex(0.5));
        Random r2 = new Random(1);
        SelectVertex<TestEnum> select2 = new SelectVertex<>(optionGroup2, r2);

        MultiplexerVertex<TestEnum> multiplexerVertex = new MultiplexerVertex<>(selectorControlVertex, select1, select2);

        LinkedHashMap<TestEnum, Integer> frequencies = new LinkedHashMap<>();
        frequencies.put(TestEnum.A, 0);
        frequencies.put(TestEnum.B, 0);
        frequencies.put(TestEnum.C, 0);
        frequencies.put(TestEnum.D, 0);

        for (int i = 0; i < N; i++) {
            selectorControlVertex.setValue(selectorControlVertex.sample());
            select1.setValue(select1.sample());
            select2.setValue(select2.sample());
            TestEnum s = multiplexerVertex.sample();
            frequencies.put(s, frequencies.get(s) + 1);
        }

        LinkedHashMap<TestEnum, Double> proportions = calculateProportions(frequencies, N);
        assertProportionsWithinExpectedRanges(expected, proportions);
    }

    private LinkedHashMap<TestEnum, Double> calculateProportions(LinkedHashMap<TestEnum, Integer> sampleFrequencies, int n) {
        LinkedHashMap<TestEnum, Double> proportions = new LinkedHashMap<>();
        for (Map.Entry<TestEnum, Integer> entry : sampleFrequencies.entrySet()) {
            double proportion = (double) entry.getValue() / n;
            proportions.put(entry.getKey(), proportion);
        }

        return proportions;
    }

    private void assertProportionsWithinExpectedRanges(Map<TestEnum, Double> expectedValues,
                                                       HashMap<TestEnum, Double> proportions) {

        for (Map.Entry<TestEnum, Double> entry : proportions.entrySet()) {
            log.info(entry.getKey() + ": " + entry.getValue());
            double expected = expectedValues.get(entry.getKey());
            double p = entry.getValue();
            assertEquals(expected, p, epsilon);
        }
    }

    private enum TestEnum {
        A, B, C, D
    }
}