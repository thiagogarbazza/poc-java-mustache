package com.github.thiagogarbazza.pocs.app;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import java.util.Collection;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class HtmlAssertions {

    public static void assertHtml(final String expected, final String actual) {
        assertHtml(expected, actual, null);
    }

    public static void assertHtml(final String expected, final String actual, final String message) {
        final Diff diff = DiffBuilder.compare(expected)
            .withTest(actual)
            .ignoreWhitespace()
            .normalizeWhitespace()
            .checkForSimilar()
            .build();

        final Collection<Executable> assertions = StreamSupport.stream(diff.getDifferences().spliterator(), false)
            .map(d -> (Executable) () -> Assertions.fail(formatDifferenceMessage(d)))
            .collect(toList());

        final String header = "XML comparison failed" + (message != null ? " - " + message : "") + " - differences found:";

        Assertions.assertAll(header, assertions);
    }

    private String formatDifferenceMessage(Difference diff) {
        return String.format("👉 type: %s | expected: %s | actual: %s on path: %s",
            diff.getComparison().getType(),
            diff.getComparison().getControlDetails().getValue(),
            diff.getComparison().getTestDetails().getValue(),
            diff.getComparison().getTestDetails().getXPath());
    }
}
