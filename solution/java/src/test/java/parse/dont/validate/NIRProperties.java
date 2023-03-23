package parse.dont.validate;

import io.vavr.test.Arbitrary;
import io.vavr.test.Gen;
import io.vavr.test.Property;
import org.junit.jupiter.api.Test;

import java.util.Random;

class NIRProperties {
    private final Random random = new Random();
    private final Gen<Sex> sexGenerator = Gen.choose(Sex.values());
    private final Gen<Year> yearGenerator = Gen.choose(0, 99).map(Year::new);
    private final Gen<Month> monthGenerator = Gen.choose(Month.values());

    private final Arbitrary<NIR> validNIR =
            sexGenerator
                    .map(NIRBuilder::new)
                    .map(nirBuilder -> nirBuilder.withYear(yearGenerator.apply(random)))
                    .map(nirBuilder -> nirBuilder.withMonth(monthGenerator.apply(random)))
                    .map(x -> new NIR(x.getSex(), x.getYear(), x.getMonth()))
                    .arbitrary();

    @Test
    void roundTrip() {
        Property.def("parseNIR(nir.ToString()) == nir")
                .forAll(validNIR)
                .suchThat(nir -> NIR.parseNIR(nir.toString()).contains(nir))
                .check()
                .assertIsSatisfied();
    }
}
