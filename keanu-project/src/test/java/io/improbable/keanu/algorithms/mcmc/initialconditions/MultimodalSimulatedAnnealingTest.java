package io.improbable.keanu.algorithms.mcmc.initialconditions;

import io.improbable.keanu.network.BayesNet;
import io.improbable.keanu.network.NetworkState;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.bool.BoolVertex;
import io.improbable.keanu.vertices.bool.probabilistic.Flip;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.CastDoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.GaussianVertex;
import io.improbable.keanu.vertices.dbl.probabilistic.UniformVertex;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;

import static io.improbable.keanu.vertices.bool.BoolVertex.If;
import static org.junit.Assert.assertTrue;

public class MultimodalSimulatedAnnealingTest {

    private Random random;

    @Before
    public void setup() {
        random = new Random(1);
    }

    @Test
    public void findsBothModesForContinuousNetwork() {

        DoubleVertex A = new UniformVertex(new ConstantDoubleVertex(-3.0), new ConstantDoubleVertex(3.0), random);
        DoubleVertex B = A.multiply(A);
        DoubleVertex C = new GaussianVertex(B, 1.5, random);
        C.observe(4.0);

        BayesNet network = new BayesNet(A.getConnectedGraph());
        List<NetworkState> modes = MultiModeDiscovery.findModesBySimulatedAnnealing(network, 100, 1000);

        boolean findsLowerMode = modes.stream().anyMatch(state -> Math.abs(state.get(A) + 2) < 0.01);
        boolean findsUpperMode = modes.stream().anyMatch(state -> Math.abs(state.get(A) - 2) < 0.01);

        assertTrue(findsLowerMode);
        assertTrue(findsUpperMode);
    }

    @Test
    public void findsModesForDiscreteContinuousHybridNetwork() {

        DoubleVertex A = new UniformVertex(new ConstantDoubleVertex(0.0), new ConstantDoubleVertex(3.0), random);
        DoubleVertex B = A.multiply(A);

        DoubleVertex C = new UniformVertex(new ConstantDoubleVertex(-3.0), new ConstantDoubleVertex(0.0), random);
        DoubleVertex D = C.multiply(C);

        BoolVertex E = new Flip(0.5, random);

        Vertex<Double> F = If(E, B, D);

        DoubleVertex G = new GaussianVertex(new CastDoubleVertex(F), 1.5, random);
        G.observe(4.0);

        BayesNet network = new BayesNet(A.getConnectedGraph());
        List<NetworkState> modes = MultiModeDiscovery.findModesBySimulatedAnnealing(network, 100, 1000);

        boolean findsUpperMode = modes.stream().anyMatch(state -> Math.abs(state.get(A) - 2) < 0.01);
        boolean findsLowerMode = modes.stream().anyMatch(state -> Math.abs(state.get(C) + 2) < 0.01);

        assertTrue(findsLowerMode);
        assertTrue(findsUpperMode);
    }

}
