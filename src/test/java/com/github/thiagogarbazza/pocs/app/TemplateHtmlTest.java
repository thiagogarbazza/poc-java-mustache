package com.github.thiagogarbazza.pocs.app;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.thiagogarbazza.pocs.app.HtmlAssertions.assertHtml;
import static java.nio.charset.StandardCharsets.UTF_8;

class TemplateHtmlTest {

    @Test
    void processWithMap() throws URISyntaxException {
        final Path path = Paths.get(getClass().getResource("/templates/html/my-template.mustache").toURI());
        final Object context = new HashMap<String, Object>() {{
            put("id", "eb526806-fe9e-4500-8327-f1f8995ae3c1");
            put("dateTime", LocalDateTime.parse("2026-05-12T19:33:56.761", DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            put("title", "Example of html template");
            put("header", new HashMap<String, Object>() {{
                put("title", "Welcome to my website");
                put("subtitle", "This is a subtitle");
            }});
            put("documents", Arrays.asList(
                new HashMap<String, Object>() {{
                    put("code", "AB12");
                    put("status", "Pending");
                }},
                new HashMap<String, Object>() {{
                    put("code", "CD34");
                    put("status", "Completed");
                }}
            ));
            put("footer", new HashMap<String, Object>() {{
                put("year", "2026");
                put("companyName", "My company");
                put("companyAddress", "123 Main Street, Anytown, USA");
            }});
        }};

        final String resultado = MustacheTemplateProcessor.process(path, context);

        assertHtml(readResourceToString("templates/html/my-template-expected.html"), resultado);
    }

    @Test
    void processWithObject() throws URISyntaxException {
        final Path path = Paths.get(getClass().getResource("/templates/html/my-template.mustache").toURI());
        final TemplateContext context = TemplateContext.builder()
            .id(UUID.fromString("eb526806-fe9e-4500-8327-f1f8995ae3c1"))
            .dateTime(LocalDateTime.parse("2026-05-12T19:33:56.761", DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .title("Example of html template")
            .header(TemplateContext.Header.builder()
                .title("Welcome to my website")
                .subtitle("This is a subtitle")
                .build())
            .documents(Arrays.asList(
                TemplateContext.Document.builder()
                    .code("AB12")
                    .status("Pending")
                    .build(),
                TemplateContext.Document.builder()
                    .code("CD34")
                    .status("Completed")
                    .build()
            ))
            .footer(TemplateContext.Footer.builder()
                .year("2026")
                .companyName("My company")
                .companyAddress("123 Main Street, Anytown, USA")
                .build())
            .build();

        final String resultado = MustacheTemplateProcessor.process(path, context);

        assertHtml(readResourceToString("templates/html/my-template-expected.html"), resultado);
    }

    @Getter
    @Builder
    @RequiredArgsConstructor
    private static class TemplateContext {
        private final UUID id;
        private final LocalDateTime dateTime;
        private final String title;
        private final Header header;
        private final Collection<Document> documents;
        private final Footer footer;

        @Getter
        @Builder
        @RequiredArgsConstructor
        public static class Header {
            private final String title;
            private final String subtitle;
        }

        @Getter
        @Builder
        @RequiredArgsConstructor
        public static class Document {
            private final String code;
            private final String status;
        }

        @Getter
        @Builder
        @RequiredArgsConstructor
        public static class Footer {
            private final String year;
            private final String companyName;
            private final String companyAddress;
        }
    }

    private static String readResourceToString(String fileName) {
        // Busca o arquivo na pasta resources (classpath)
        final InputStream inputStream = TemplateHtmlTest.class.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new IllegalArgumentException("Arquivo não encontrado: " + fileName);
        }


        try (final InputStreamReader inputStreamReader = new InputStreamReader(inputStream, UTF_8);
             final BufferedReader reader = new BufferedReader(inputStreamReader)) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler o arquivo", e);
        }
    }
}
