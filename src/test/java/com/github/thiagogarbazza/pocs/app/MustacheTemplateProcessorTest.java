package com.github.thiagogarbazza.pocs.app;

import com.samskivert.mustache.MustacheException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MustacheTemplateProcessorTest {

    public static Stream<Arguments> processWithFormatterArguments() {
        return Stream.of(
            Arguments.of(Integer.valueOf("1234"), "1.234"),
            Arguments.of(Integer.valueOf("-1234"), "-1.234"),

            Arguments.of(Long.valueOf("123456789"), "123.456.789"),
            Arguments.of(Long.valueOf("-123456789"), "-123.456.789"),

            Arguments.of(new BigDecimal("1234"), "1.234,00"),
            Arguments.of(new BigDecimal("1234.56"), "1.234,56"),
            Arguments.of(new BigDecimal("1234.556"), "1.234,56"),
            Arguments.of(new BigDecimal("-1234.556"), "-1.234,56"),

            Arguments.of(LocalDate.of(2023, 10, 31), "31/10/2023"),
            Arguments.of(LocalDateTime.of(2023, 10, 31, 23, 55, 59), "31/10/2023 23:55:59"),

            Arguments.of("Some text", "Some text")
        );
    }

    @ParameterizedTest
    @MethodSource("processWithFormatterArguments")
    void processWithFormatter(final Object value, final String expected) {
        final String template = "{{some_variable}}";
        final Map<String, Object> context = new HashMap<String, Object>() {{
            put("some_variable", value);
        }};

        final String processed = MustacheTemplateProcessor.process(template, context);

        assertEquals(expected, processed);
    }

    @Nested
    class ProcessFileTemplate {
        @Test
        void process() throws URISyntaxException {
            final Path path = Paths.get(getClass().getResource("/templates/simple.mustache").toURI());
            final Map<String, String> context = new HashMap<String, String>() {{
                put("my_variable", "World");
            }};

            final String processed = MustacheTemplateProcessor.process(path, context);

            assertEquals("Hello World!", processed.trim());
        }

        @Test
        void processWithNonExistentTemplate() {
            final Path path = Paths.get("/tmp/non-existent-template.mustache");
            final Map<String, String> context = new HashMap<>();

            final Exception exception = assertThrows(MustacheException.class, () -> MustacheTemplateProcessor.process(path, context));

            assertNotNull(exception);
        }

        @Test
        void processWithNonExistentVariable() throws URISyntaxException {
            final Path path = Paths.get(getClass().getResource("/templates/simple.mustache").toURI());
            final Map<String, String> context = new HashMap<>();

            final Exception exception = assertThrows(MustacheException.class, () -> MustacheTemplateProcessor.process(path, context));

            assertEquals("No method or field with name 'my_variable' on line 1", exception.getMessage());
        }
    }

    @Nested
    class ProcessStringTemplate {

        @Test
        void process() {
            final String template = "Hello {{my_variable}}!";
            final Map<String, String> context = new HashMap<String, String>() {{
                put("my_variable", "World");
            }};

            final String processed = MustacheTemplateProcessor.process(template, context);

            assertEquals("Hello World!", processed);
        }

        @Test
        void processWithNullTemplate() {
            final String template = null;
            final Map<String, String> context = new HashMap<>();

            final Exception exception = assertThrows(NullPointerException.class, () -> MustacheTemplateProcessor.process(template, context));
            assertNotNull(exception);
        }

        @Test
        void processWithNonExistentVariable() {
            final String template = "Hello {{my_variable}}!";
            final Map<String, String> context = new HashMap<>();

            final Exception exception = assertThrows(MustacheException.class, () -> MustacheTemplateProcessor.process(template, context));

            assertEquals("No method or field with name 'my_variable' on line 1", exception.getMessage());
        }
    }
}
