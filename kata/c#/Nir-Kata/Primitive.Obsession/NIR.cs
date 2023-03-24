namespace Nir_Kata.Primitive.Obsession
{
    public static class NIR
    {
        private const int ValidLength = 15;

        public static bool Validate(string input) => ValidateLength(input)
                                                     && ValidateSex(input[0])
                                                     && ValidateYear(input[1..3])
                                                     && ValidateMonth(input[3..5])
                                                     && ValidateDepartment(input[5..7])
                                                     && ValidateCity(input[7..10])
                                                     && ValidateSerialNumber(input[10..13]);

        private static bool ValidateLength(string input) => input.Length == ValidLength;

        private static bool ValidateSex(char sex) => sex is '1' or '2';
        private static bool ValidateYear(string year) => year.IsANumber();

        private static bool ValidateMonth(string month) =>
            ValidateNumber(month, m => m is > 0 and <= 12);

        private static bool ValidateDepartment(string department) =>
            ValidateNumber(department, d => d is > 0 and <= 95 or 99);

        private static bool ValidateCity(string city) => city.IsANumber();

        private static bool ValidateSerialNumber(string serialNumber) => serialNumber.IsANumber();

        private static bool ValidateNumber(string potentialNumber, Predicate<int> predicate) =>
            potentialNumber
                .ToInt()
                .Match(number => predicate(number), false);
    }
}