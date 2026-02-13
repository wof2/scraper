package com.scraper.application;

import com.scraper.Config;
import com.scraper.adapter.in.WebMagicCrawlerAdapter;
import com.scraper.domain.model.Product;
import com.scraper.domain.port.in.CrawlProductsPort;
import com.scraper.domain.port.out.ProductExporterPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.List;

public class CrawlProductsService implements CrawlProductsPort {

    private static final Logger log = LoggerFactory.getLogger(CrawlProductsService.class);

    private final Config config;
    private final ProductExporterPort exporter;

    public CrawlProductsService(Config config, ProductExporterPort exporter) {
        this.config = config;
        this.exporter = exporter;
    }

    @Override
    public void crawl(String seedUrl) {
        log.info("Starting crawl from seed URL: {}", seedUrl);

        WebMagicCrawlerAdapter processor = new WebMagicCrawlerAdapter(config);

        Spider.create(processor)
                .addUrl(seedUrl)
                .thread(config.threads())
                .run();

        List<Product> products = processor.getProducts();
        log.info("Crawl finished. Total products scraped: {}", products.size());

        exporter.export(products);
    }
}
