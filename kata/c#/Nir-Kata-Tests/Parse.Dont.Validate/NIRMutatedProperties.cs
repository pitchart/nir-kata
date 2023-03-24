using System.Diagnostics.CodeAnalysis;
using FsCheck;
using FsCheck.Xunit;
using LanguageExt;
using Nir_Kata.Parse.Dont.Validate;
using static System.Tuple;
using static Nir_Kata.Parse.Dont.Validate.NIR;

namespace Nir_Kata_Tests.Parse.Dont.Validate
{
    public class NIRMutatedProperties
    {
        public record Mutator(string Name, Func<NIR, Gen<string>> mutate)
        {
            public string Apply(NIR nir) => mutate(nir).Sample(0, 1).Head;
        }

        private static class MutatorGenerator
        {
            private static Mutator sexMutator = new("Sex mutator", nir =>
                Gen.Choose(3, 9)
                    .Select(invalidSex => invalidSex + nir.ToString()[1..15])
            );

            [SuppressMessage("ReSharper", "UnusedMember.Local", Justification = "Used by FSCheck")]
            public static Arbitrary<Mutator> Mutator() =>
                Gen.Elements(
                        sexMutator
                    )
                    .ToArbitrary();
        }

        [Property(Arbitrary = new[] {typeof(NIRGenerator), typeof(MutatorGenerator)})]
        public Property InvalidNIRCanNeverBeParsed(NIR nir, Mutator mutator) =>
            (ParseNIR(mutator.Apply(nir)) == Option<NIR>.None)
            .ToProperty()
            .Classify(true, mutator.Name);
    }
}