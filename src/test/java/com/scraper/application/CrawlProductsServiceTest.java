package com.scraper.application;

import com.scraper.Config;
import com.scraper.domain.model.Product;
import com.scraper.domain.port.out.ProductExporterPort;
import com.scraper.domain.port.out.ProductPostProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.math.BigDecimal.TEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

class CrawlProductsServiceTest {

    private static class ManualExporter implements ProductExporterPort {
        public Set<Product> exportedProducts;
        @Override
        public void export(Set<Product> products) {
            this.exportedProducts = products;
        }
    }

    private static class ManualPostProcessor implements ProductPostProcessor {
        public Set<Product> inputProducts;
        public Set<Product> resultProducts;
        @Override
        public Set<Product> process(Set<Product> products) {
            this.inputProducts = products;
            return resultProducts;
        }
    }

    private final ManualExporter exporter = new ManualExporter();
    private final ManualPostProcessor postProcessor = new ManualPostProcessor();

    private Config config;
    private CrawlProductsService service;

    @BeforeEach
    void setUp() {
        Config.Selectors selectors = new Config.Selectors(".n", ".d", ".p", ".c", ".m");
        config = new Config(
                "http://seed.com",
                "http://seed.com/p/",
                "http://seed.com/.*",
                1, 1, 1, "UA", "out.json", "GB",
                selectors
        );
        service = new CrawlProductsService(config, exporter, postProcessor);
    }

    @Test
    void shouldOrchestrateCrawlProcess() {
        Product product = new Product("P", "D", TEN, List.of("C"), List.of("M"));
        Set<Product> processedProducts = Set.of(product);
        
        postProcessor.resultProducts = processedProducts;

        service.crawl("http://invalid-url-to-prevent-actual-crawl");

        assertThat(postProcessor.inputProducts).isNotNull();
        assertThat(exporter.exportedProducts).isEqualTo(processedProducts);
    }
}
