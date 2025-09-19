package com.keldorn.util;

import com.keldorn.exception.InvalidEmailException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidifyEmailTest {
    public static Stream<Arguments> validEmails() {
        return Stream.of(
                Arguments.of("user@example.com"),
                Arguments.of("john.doe@sub.domain.org"),
                Arguments.of("a_b-c.123@my-site.net"),
                Arguments.of("hello.world@co.uk"),
                Arguments.of("user-name@domain123.io"),
                Arguments.of("UPPER.CASE@Example.COM")
        );
    }

    public static Stream<Arguments> invalidEmails() {
        return Stream.of(
                Arguments.of("plainaddress"),
                Arguments.of("@missinglocal.org"),
                Arguments.of("user@.com"),
                Arguments.of("user@domain"),
                Arguments.of("user@domain.c"),
                Arguments.of("user@domain..com"),
                Arguments.of("user@domain,com"),
                Arguments.of("user@-domain.com"),
                Arguments.of("user@domain-.com"),
                Arguments.of("first last@example.com")
        );
    }

    @ParameterizedTest(name = "Run {index}: email={0}")
    @MethodSource("validEmails")
    void validify_success(String email){
        assertDoesNotThrow(() -> ValidifyEmail.validify(email));
    }

    @ParameterizedTest(name = "Run {index}: email={0}")
    @MethodSource("invalidEmails")
    void validify_fail(String email) {
        assertThrows(InvalidEmailException.class, () -> {
            ValidifyEmail.validify(email);
        });
    }
}