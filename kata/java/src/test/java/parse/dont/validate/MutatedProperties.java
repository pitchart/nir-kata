package parse.dont.validate;

import io.vavr.test.Property;
import org.junit.jupiter.api.Test;
import parse.dont.validate.mutations.Mutator;

import static java.lang.System.out;
import static parse.dont.validate.NIRGenerator.validNIR;
import static parse.dont.validate.mutations.Mutators.mutators;

class MutatedProperties {
    @Test
    void invalidNIRCanNeverBeParsed() {
        Property.def("mutate(nir.toString) == left")
                .forAll(validNIR, mutators)
                .suchThat(MutatedProperties::canNotParseMutatedNIR)
                .check()
                .assertIsSatisfied();
    }

    private static boolean canNotParseMutatedNIR(NIR nir, Mutator mutator) {
        final String mutatedNIR = mutator.mutate(nir);
        out.println(mutator.name() + " mutated " + nir + " to " + mutatedNIR);

        return NIR.parseNIR(mutatedNIR).isLeft();
    }
}
