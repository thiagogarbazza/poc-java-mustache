package com.github.thiagogarbazza.pocs.app;

import lombok.experimental.UtilityClass;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@UtilityClass
public class CustomAssertions {

    public static void assertHtml(final String expected, final String actual) {
        assertHtml(expected, actual, null);
    }

    public static void assertHtml(final String expected, final String actual, final String message) {
        final Diff diff = DiffBuilder.compare(expected)
            .withTest(actual)
            .ignoreWhitespace()
            .normalizeWhitespace()
            .build();

        assertEquals(emptyList(), diff.getDifferences(), message);
    }
}
