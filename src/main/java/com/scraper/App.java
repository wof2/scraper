package com.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scraper.adapter.out.JsonFileExporter;
import com.scraper.application.CrawlProductsService;
import com.scraper.domain.port.in.CrawlProductsPort;
import com.scraper.domain.port.out.ProductExporterPort;

import java.io.File;
import java.io.IOException;

public class App {

    public static void main(String[] args) throws IOException {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            System.err.println("config.json not found in working directory");
            System.exit(1);
        }

        record Config(String seedUrl, String productUrlPrefix, String followUrlRegex,
                      String outputFile, int threads) {}

        Config config = new ObjectMapper().readValue(configFile, Config.class);

        ProductExporterPort exporter = new JsonFileExporter(config.outputFile());
        CrawlProductsPort useCase = new CrawlProductsService(
                exporter, config.threads(), config.productUrlPrefix(), config.followUrlRegex());

        useCase.crawl(config.seedUrl());
    }
}
