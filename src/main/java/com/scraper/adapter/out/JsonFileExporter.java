package com.scraper.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.scraper.domain.model.Product;
import com.scraper.domain.port.out.ProductExporterPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

public class JsonFileExporter implements ProductExporterPort {

    private static final Logger log = LoggerFactory.getLogger(JsonFileExporter.class);

    private final ObjectMapper mapper;
    private final File outputFile;

    public JsonFileExporter(String filePath) {
        this.outputFile = new File(filePath);
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void export(List<Product> products) {
        BigDecimal total = products.stream()
                .map(Product::price)
                .distinct()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        try {
            mapper.writeValue(outputFile, Map.of(
                    "results", products,
                    "total", total
            ));
            log.info("Exported {} products to {}", products.size(), outputFile);
            System.out.println(Files.readString(outputFile.toPath()));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write products to " + outputFile, e);
        }
    }
}
