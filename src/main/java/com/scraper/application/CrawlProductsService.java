package com.scraper.application;

import com.scraper.Config;
import com.scraper.adapter.in.WebMagicCrawlerAdapter;
import com.scraper.domain.model.Product;
import com.scraper.domain.port.in.CrawlProductsPort;
import com.scraper.domain.port.out.ProductPostProcessor;
import com.scraper.domain.port.out.ProductExporterPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Spider;

import java.util.Set;

public class CrawlProductsService implements CrawlProductsPort {

    private static final Logger log = LoggerFactory.getLogger(CrawlProductsService.class);

    private final Config config;
    private final ProductExporterPort exporter;
    private final ProductPostProcessor postProcessor;

    public CrawlProductsService(Config config, ProductExporterPort exporter,
                                ProductPostProcessor postProcessor) {
        this.config = config;
        this.exporter = exporter;
        this.postProcessor = postProcessor;
    }

    @Override
    public void crawl(String seedUrl) {
        log.info("Starting crawl from seed URL: {}", seedUrl);

        WebMagicCrawlerAdapter crawlerAdapter = new WebMagicCrawlerAdapter(config);

        Spider.create(crawlerAdapter)
                .addUrl(seedUrl)
                .thread(config.threads())
                .run();

        Set<Product> products = crawlerAdapter.getProducts();
        log.info("Crawl finished. Total products scraped: {}", products.size());

        Set<Product> processedProducts = postProcessor.process(products);
        exporter.export(processedProducts);
    }
}
