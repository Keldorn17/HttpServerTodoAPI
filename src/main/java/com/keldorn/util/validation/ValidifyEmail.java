package com.keldorn.util.validation;

import com.keldorn.exception.InvalidEmailException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidifyEmail {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("([\\w.-]+)@((\\w+(-\\w+)*\\.)+\\w{2,})");

    public static void validify(String email) throws InvalidEmailException {
        Matcher emailMatcher = EMAIL_PATTERN.matcher(email);
        if (!emailMatcher.matches()) {
            throw new InvalidEmailException("Invalid email format: \"%s\" does not match the required pattern.".formatted(email));
        }
    }
}
