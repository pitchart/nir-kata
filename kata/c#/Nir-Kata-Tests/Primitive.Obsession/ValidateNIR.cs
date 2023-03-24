using FluentAssertions;
using static Nir_Kata.Primitive.Obsession.NIR;

namespace Nir_Kata_Tests.Primitive.Obsession
{
    public class ValidateNIR
    {
        [Theory]
        [InlineData("2230", "too short")]
        [InlineData("323115935012322", "incorrect sex")]
        [InlineData("2ab115935012322", "incorrect year")]
        [InlineData("223ab5935012322", "incorrect month")]
        [InlineData("223145935012322", "incorrect month 2")]
        [InlineData("223005935012322", "incorrect month 3")]
        [InlineData("22311xx35012322", "incorrect department")]
        [InlineData("223119635012322", "incorrect department 2")]
        [InlineData("2231159zzz12322", "incorrect city")]
        [InlineData("2231159123zzz22", "incorrect serial number")]
        //[InlineData("223115935012321", "incorrect control key")]
        public void Validate_Should_Return_False(string invalidNir, string reason) =>
            Validate(invalidNir)
                .Should()
                .BeFalse(reason);
    }
}