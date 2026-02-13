package com.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scraper.adapter.out.JsonFileExporter;
import com.scraper.application.CrawlProductsService;
import com.scraper.domain.port.in.CrawlProductsPort;
import com.scraper.domain.port.out.ProductExporterPort;

import java.io.File;
import java.io.IOException;

public class App {

    static void main(String[] args) throws IOException {
        File configFile = new File("config.json");
        if (!configFile.exists()) {
            System.err.println("config.json not found in working directory");
            System.exit(1);
        }

        Config config = new ObjectMapper().readValue(configFile, Config.class);

        ProductExporterPort exporter = new JsonFileExporter(config.outputFile());
        CrawlProductsPort crawlService = new CrawlProductsService(config, exporter);

        crawlService.crawl(config.seedUrl());
    }
}
