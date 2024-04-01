package com.jeromecuny.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeromecuny.InvoicingApiModule;
import com.jeromecuny.request.EnergyType;
import com.jeromecuny.request.InvoiceRequest;
import com.jeromecuny.request.client.Civility;
import com.jeromecuny.request.client.Individual;
import com.jeromecuny.response.InvoiceResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Integration Test that will compare actual / expected output results based on:
 *  <li>[id]-spec.json file: the input file</li>
 *  <li>[id]-response.json file: the output file</li>
 */
@TestInstance(PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = InvoicingApiModule.class)
class InvoicingControllerTest {

    private static final String INVOICING_ENDPOINT = "/api/v1/invoices";
    private static final String INPUT_FILE_SUFFIX = "-spec.json";
    private static final String OUTPUT_FILE_SUFFIX = "-response.json";

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void testWrongReferenceFormat() {
        final InvoiceRequest invoiceRequest = new InvoiceRequest().setMarketDate(LocalDate.of(2024, 1, 1))
                .setClient(new Individual().setCivility(Civility.MR)
                        .setFirstName("Jerome")
                        .setLastName("Cuny")
                        .setReference("1200393"))
                .setDailyRate(40.)
                .setEnergyType(EnergyType.ELECTRICITY);

        final ResponseEntity<InvoiceResponse> response = restTemplate.postForEntity(INVOICING_ENDPOINT, invoiceRequest,
                InvoiceResponse.class);

        Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    /**
     *  Parameterized test to compare actual and expected output
     *
     * @param dataId the id of the current file spec
     * @param requestContent the json content read from the input file
     * @param expectedResponseContent the json content read from the output file
     */
    @ParameterizedTest
    @MethodSource("provideTestInputs")
    void computeInvoiceTest(String dataId, String requestContent, String expectedResponseContent) {
        final InvoiceRequest invoiceRequest = extractFromJson(requestContent, InvoiceRequest.class);
        final InvoiceResponse expectedResponse = extractFromJson(expectedResponseContent, InvoiceResponse.class);

        final ResponseEntity<InvoiceResponse> response = restTemplate.postForEntity(INVOICING_ENDPOINT,
                invoiceRequest, InvoiceResponse.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();

        writeActualResponseContent(dataId, response.getBody());
        Assertions.assertThat(response.getBody())
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    private Stream<? extends Arguments> provideTestInputs() throws IOException {
        final Path path = Path.of("*-spec.json");
        final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                InvoicingControllerTest.class.getClassLoader());
        final Resource[] resources = resolver.getResources(ResourceLoader.CLASSPATH_URL_PREFIX + path);

        final Map<String, Pair<String, String>> jsonElements = new TreeMap<>(Comparator.naturalOrder());

        for (Resource file : resources) {
            if (!(file.getFilename() == null || file.getFilename().endsWith(INPUT_FILE_SUFFIX))) {
                throw new IOException("Incorrect file name: " + file.getURL());
            }
            final String id = file.getFilename().substring(0, file.getFilename().indexOf(INPUT_FILE_SUFFIX));
            final Resource outputResource = getOutputResource(id + OUTPUT_FILE_SUFFIX);
            final String jsonInput = readFileContent(file);
            final String jsonOutput = readFileContent(outputResource);
            jsonElements.put(id, Pair.of(jsonInput, jsonOutput));
        }

        return jsonElements.entrySet().stream()
                .map(e -> Arguments.of(e.getKey(), e.getValue().getLeft(), e.getValue().getRight()));
    }

    private static Resource getOutputResource(String fileName) throws FileNotFoundException {
        final Path path = Path.of(fileName);
        final ClassPathResource resource = new ClassPathResource(path.toString(), InvoicingControllerTest.class.getClassLoader());
        if (!resource.exists()) {
            throw new FileNotFoundException("data resource doesn't exist: " + fileName);
        }
        return resource;
    }

    private void writeActualResponseContent(String dataId, InvoiceResponse response) {
        final String responseContent = convertResponseToJson(response);
        try {
            final Path path = Path.of("actual", dataId + OUTPUT_FILE_SUFFIX);
            final File file = new File(Objects.requireNonNull(InvoicingControllerTest.class.getClassLoader()
                            .getResource("."))
                    .getFile(), path.toString());
            FileUtils.write(file, responseContent, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error while writing response in output file: " + e.getMessage());
        }
    }

    private static String readFileContent(@NonNull Resource file) throws IOException {
        return file.exists() ? IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8) : "";
    }

    private <T> T extractFromJson(String jsonContent, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonContent)) jsonContent = "{}";
        try {
            return objectMapper.readValue(jsonContent, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    private String convertResponseToJson(InvoiceResponse response) {
        try {
            final DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter();
            prettyPrinter.indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);
            return objectMapper.writer(prettyPrinter).writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }
}
