package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.unary;

import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.DualNumber;

import java.util.Map;

public class CosVertex extends DoubleUnaryOpVertex {

    public CosVertex(DoubleVertex inputVertex) {
        super(inputVertex);
    }

    public CosVertex(double inputValue) {
        super(new ConstantDoubleVertex(inputValue));
    }

    @Override
    protected Double op(Double a) {
        return Math.cos(a);
    }

    @Override
    public DualNumber calculateDualNumber(Map<Vertex, DualNumber> dualNumbers) {
        return dualNumbers.get(inputVertex).cos();
    }
}
