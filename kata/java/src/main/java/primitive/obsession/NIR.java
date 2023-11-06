package primitive.obsession;

import lombok.experimental.ExtensionMethod;
import lombok.experimental.UtilityClass;

import java.util.function.Function;

@UtilityClass
@ExtensionMethod(StringExtensions.class)
public class NIR {

    private static final int VALID_LENGTH = 15;
    private static final char MALE = '1';
    private static final char FEMALE = '2';
    public static final int CONTROL_KEY_COMPLEMENT = 97;

    public static Boolean validate(String potentialNIR) {
        return validateLength(potentialNIR)
                && validateSex(potentialNIR.charAt(0))
                && validateYear(potentialNIR.substring(1, 3))
                && validateMonth(potentialNIR.substring(3, 5))
                && validateDepartment(potentialNIR.substring(5, 7))
                && validateCity(potentialNIR.substring(7, 10))
                && validateSerialNumber(potentialNIR.substring(10, 13))
                && validateKey(potentialNIR);
    }

    private static boolean validateKey(String potentialNir) {
        int sum = potentialNir
            .substring(0, 13)
            .chars()
            .map(Character::getNumericValue)
            .sum();
        int controlKey = CONTROL_KEY_COMPLEMENT - (sum % CONTROL_KEY_COMPLEMENT);
        return controlKey == Integer.parseInt(potentialNir.substring(13));
    }

    private static boolean validateLength(String potentialNIR) {
        return potentialNIR.length() == VALID_LENGTH;
    }

    private static boolean validateSex(char sex) {
        return sex == MALE || sex == FEMALE;
    }

    private static boolean validateYear(String year) {
        return year.isANumber();
    }

    private static boolean validateMonth(String month) {
        return validateNumber(month, x -> x > 0 && x <= 12);
    }

    private static boolean validateDepartment(String department) {
        return validateNumber(department, x -> x > 0 && (x <= 95 || x == 99));
    }

    private static boolean validateCity(String city) {
        return city.isANumber();
    }
    private static boolean validateSerialNumber(String serialNumber) {
        return serialNumber.isANumber();
    }
    private static boolean validateNumber(String potentialNumber, Function<Integer, Boolean> isValid) {
        return potentialNumber
                .toInt()
                .map(isValid)
                .getOrElse(false);
    }
}
