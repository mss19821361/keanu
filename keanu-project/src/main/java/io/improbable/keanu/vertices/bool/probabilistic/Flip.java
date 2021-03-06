package io.improbable.keanu.vertices.bool.probabilistic;

import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;

import java.util.Map;
import java.util.Random;

public class Flip extends ProbabilisticBool {

    private final Vertex<Double> probTrue;
    private final Random random;

    public Flip(Vertex<Double> probTrue, Random random) {
        this.probTrue = probTrue;
        this.random = random;
        setValue(false);
        setParents(probTrue);
    }

    public Flip(double probTrue, Random random) {
        this(new ConstantDoubleVertex(probTrue), random);
    }

    public Flip(double probTrue) {
        this(new ConstantDoubleVertex(probTrue), new Random());
    }

    public Vertex<Double> getProbTrue() {
        return probTrue;
    }

    @Override
    public double logPmf(Boolean value) {
        final double probability = value ? probTrue.getValue() : 1 - probTrue.getValue();
        return Math.log(probability);
    }

    @Override
    public Map<String, Double> dLogPmf(Boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Boolean sample() {
        return random.nextDouble() < probTrue.getValue();
    }

}
