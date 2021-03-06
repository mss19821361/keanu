package io.improbable.docs;

import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.generic.probabilistic.discrete.SelectVertex;

import java.util.LinkedHashMap;

import static io.improbable.docs.SelectVertexExample.MyType.*;

public class SelectVertexExample {

    public enum MyType {
        A, B, C, D
    }

    public SelectVertex<MyType> getSelectorForMyType() {

        LinkedHashMap<MyType, DoubleVertex> frequency = new LinkedHashMap<>();
        frequency.put(A, new ConstantDoubleVertex(0.25));
        frequency.put(B, new ConstantDoubleVertex(0.25));
        frequency.put(C, new ConstantDoubleVertex(0.25));
        frequency.put(D, new ConstantDoubleVertex(0.25));

        return new SelectVertex<MyType>(frequency);
    }

}
