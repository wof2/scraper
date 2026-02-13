package com.scraper.adapter.out;

import com.scraper.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JsonFileExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldExportToJsonFile() throws IOException {
        Path outputFile = tempDir.resolve("products.json");
        JsonFileExporter exporter = new JsonFileExporter(outputFile.toString());

        Product p1 = new Product("P1", "D1", new BigDecimal("10.50"), List.of("Red"), List.of("64"));
        Product p2 = new Product("P2", "D2", new BigDecimal("20.00"), List.of("Blue"), List.of("128"));
        Set<Product> products = Set.of(p1, p2);

        exporter.export(products);

        assertThat(outputFile).exists();
        String content = Files.readString(outputFile);
        
        assertThat(content).contains("\"total\" : 30.5");
        assertThat(content).contains("P1");
        assertThat(content).contains("P2");
        // Check that memory is ignored because of the Mixin
        assertThat(content).doesNotContain("\"memory\"");
    }
}
