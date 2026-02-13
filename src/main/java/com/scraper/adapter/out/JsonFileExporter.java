package com.scraper.adapter.out;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.scraper.domain.model.Product;
import com.scraper.domain.port.out.ProductExporterPort;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class JsonFileExporter implements ProductExporterPort {

    private final ObjectMapper mapper;
    private final File outputFile;

    public JsonFileExporter(String filePath) {
        this.outputFile = new File(filePath);
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void export(List<Product> products) {
        try {
            mapper.writeValue(outputFile, products);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write products to " + outputFile, e);
        }
    }
}
