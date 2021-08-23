package com.stripe.android.paymentsheet.elements

import androidx.annotation.StringRes
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.stripe.android.paymentsheet.R
import com.stripe.android.paymentsheet.elements.TextFieldStateConstants.Error
import com.stripe.android.paymentsheet.elements.TextFieldStateConstants.Valid
import java.util.Calendar
import java.util.regex.Pattern

internal class DateConfig : TextFieldConfig {
    override val capitalization: KeyboardCapitalization = KeyboardCapitalization.None
    override val debugLabel = "date"

    @StringRes
    override val label = R.string.credit_expiration_date
    override val keyboard = KeyboardType.Number
    override val visualTransformation = ExpiryDateVisualTransformation()

    /**
     * This will allow all characters, but will show as invalid if it doesn't match
     * the regular expression.
     */
    override fun filter(userTyped: String) = userTyped.filter { it.isDigit() }

    override fun convertToRaw(displayName: String) = displayName

    override fun convertFromRaw(rawValue: String) = rawValue

    override fun determineState(input: String): TextFieldState {
        return if (input.isBlank()) {
            Error.Blank
        } else {
            val newString =
                if ((
                    input.isNotBlank() &&
                        !(input[0] == '0' || input[0] == '1')
                    ) ||
                    (
                        (input.length > 1) &&
                            (input[0] == '1' && requireNotNull(input[1].digitToInt()) > 2)
                        )
                ) {
                    "0$input"
                } else {
                    input
                }
            when {
                newString.length < 4 -> {
                    Error.Incomplete(R.string.incomplete_expiry_date)
                }
                newString.length > 4 -> {
                    Error.Incomplete(R.string.invalid_expiry_date)
                }
                else -> {
                    val month = requireNotNull(newString.take(2).toIntOrNull())
                    val year = requireNotNull(newString.takeLast(2).toIntOrNull())
                    val yearMinus1900 = year + (2000 - 1900)
                    val currentYear = Calendar.getInstance().get(Calendar.YEAR) - 1900
                    val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                    if ((yearMinus1900 - currentYear) < 0) {
                        Error.Invalid(R.string.invalid_expiry_year_past)
                    } else if ((yearMinus1900 - currentYear) > 50) {
                        Error.Invalid(R.string.invalid_expiry_year)
                    } else if ((yearMinus1900 - currentYear) == 0 && currentMonth > month) {
                        Error.Invalid(R.string.invalid_expiry_year_past)
                    } else if (month !in 1..12) {
                        Error.Incomplete(R.string.invalid_expiry_month)
                    } else {
                        Valid.Full
                    }
                }
            }
        }

        // eslint-disable-next-line rulesdir/monotonic-time
//        const current = new Date(Date.now());
//        const splitExpiry = expiry.replace(LTR_REGEX, '').split(' / ');
//
//        const yearString = splitExpiry[1] || '';
//        const yearInt = parseInt(yearString, 10);
//        const year = yearString.length === 2 ? yearInt % 100 : yearInt;
//        const currentYear =
//        yearString.length === 2
//        ? current.getFullYear() % 100
//        : current.getFullYear();
//
//        if (isNaN(year) || yearString.length < 2 || yearString.length === 3) {
//            // Return early if there's no year yet, because it doesn't make sense to
//            // validate month without a year.
//            return ignoreIncomplete
//            ? null
//            : createInputValidationError('incomplete_expiry');
//        } else if (year - currentYear < 0) {
//            return createInputValidationError('invalid_expiry_year_past');
//        } else if (year - currentYear > 50) {
//            return createInputValidationError('invalid_expiry_year');
//        }
//
//        const monthString = splitExpiry[0];
//        const month = parseInt(monthString, 10);
//        const currentMonth = current.getMonth() + 1;
//        if (isNaN(month)) {
//            return ignoreIncomplete
//            ? null
//            : createInputValidationError('incomplete_expiry');
//        } else if (year - currentYear === 0 && month < currentMonth) {
//            return createInputValidationError('invalid_expiry_month_past');
//        } else {
//            return null;
//        }
//    },
    }

    private fun containsNameAndDomain(str: String) = str.contains("@") && str.matches(
        Regex(".*@.*\\..+")
    )

    companion object {
        // This is copied from Patterns.EMAIL_ADDRESS because it is not defined for unit tests
        // unless using Robolectric which is quite slow.
        val PATTERN: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
        )
    }
}