package com.github.thiagogarbazza.pocs.app;


import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

import static java.nio.charset.StandardCharsets.UTF_8;

@UtilityClass
public class MustacheTemplateProcessor {

    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(new Locale("pt", "BR"));
    private final static Map<Class<?>, Function<Object, String>> FORMATTERS = new HashMap<Class<?>, Function<Object, String>>() {{
        put(BigDecimal.class, v -> {
            final DecimalFormat decimalFormat = createDecimalFormat("#,##0.00");
            return decimalFormat.format(v);
        });
        put(Integer.class, v -> {
            final DecimalFormat decimalFormat = createDecimalFormat("#,###");
            return decimalFormat.format(v);
        });
        put(LocalDate.class, v -> ((LocalDate) v).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        put(LocalDateTime.class, v -> ((LocalDateTime) v).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        put(Long.class, v -> {
            final DecimalFormat decimalFormat = createDecimalFormat("#,###");
            return decimalFormat.format(v);
        });
    }};

    private static DecimalFormat createDecimalFormat(String pattern) {
        final DecimalFormat decimalFormat = new DecimalFormat(pattern, DECIMAL_FORMAT_SYMBOLS);
        decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

        return decimalFormat;
    }

    /**
     * Processa um template Mustache com o contexto fornecido.
     *
     * @param path    O path do template a ser processado.
     *                Os partials do template devem estar no mesmo diretório do template principal, com a extensão ".mustache".
     * @param context O contexto a ser usado para preencher o template.
     * @return O resultado do processamento do template.
     * @throws com.samskivert.mustache.MustacheException se ocorrer um erro durante o processamento do template.
     */
    public static String process(final Path path, final Object context) {
        try (
            final InputStream inputStream = Files.newInputStream(path);
            final Reader reader = new InputStreamReader(inputStream, UTF_8);
            final BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            return Mustache.compiler()
                .strictSections(true)
                .withFormatter(value -> FORMATTERS.getOrDefault(value.getClass(), String::valueOf).apply(value))
                .withLoader(name -> new FileReader(new File(path.getParent().toFile(), name + ".mustache")))
                .compile(bufferedReader)
                .execute(context);
        } catch (IOException e) {
            throw new MustacheException("Error occurred while processing the template", e);
        }
    }

    /**
     * Processa um template Mustache a partir de uma string com o contexto fornecido.
     *
     * @param template O template a ser processado.
     * @param context  O contexto a ser usado para preencher o template.
     * @return O resultado do processamento do template ser usado para preencher o template.
     * @throws com.samskivert.mustache.MustacheException se ocorrer um erro durante o processamento do template.
     */
    public static String process(final String template, final Object context) {
        final StringReader reader = new StringReader(template);

        return Mustache.compiler()
            .strictSections(true)
            .withFormatter(value -> FORMATTERS.getOrDefault(value.getClass(), String::valueOf).apply(value))
            .compile(reader)
            .execute(context);
    }
}
