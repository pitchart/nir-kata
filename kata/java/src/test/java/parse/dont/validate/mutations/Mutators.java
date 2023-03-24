package parse.dont.validate.mutations;

import io.vavr.Tuple;
import io.vavr.collection.List;
import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;

public class Mutators {
    private static Gen<Integer> digits3Gen = Gen.frequency(
            Tuple.of(7, Gen.choose(1000, 9999)),
            Tuple.of(3, Gen.choose(1, 99))
    );

    private static Mutator sexMutator = new Mutator("Sex mutator", nir ->
            Gen.choose(3, 9).map(invalidSex -> concat(invalidSex, nir.toString().substring(1)))
    );

    private static String concat(Object... elements) {
        return List.of(elements).mkString();
    }

    public static Arbitrary<Mutator> mutators = Gen.choose(
            sexMutator
    ).arbitrary();
}