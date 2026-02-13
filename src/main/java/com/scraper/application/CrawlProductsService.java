package com.scraper.application;

import com.scraper.adapter.in.WebMagicCrawlerAdapter;
import com.scraper.domain.model.Product;
import com.scraper.domain.port.in.CrawlProductsPort;
import com.scraper.domain.port.out.ProductExporterPort;
import us.codecraft.webmagic.Spider;

import java.util.List;

public class CrawlProductsService implements CrawlProductsPort {

    private final ProductExporterPort exporter;
    private final int threads;
    private final String productUrlPrefix;
    private final String followUrlRegex;

    public CrawlProductsService(ProductExporterPort exporter, int threads,
                                String productUrlPrefix, String followUrlRegex) {
        this.exporter = exporter;
        this.threads = threads;
        this.productUrlPrefix = productUrlPrefix;
        this.followUrlRegex = followUrlRegex;
    }

    @Override
    public void crawl(String seedUrl) {
        WebMagicCrawlerAdapter processor = new WebMagicCrawlerAdapter(productUrlPrefix, followUrlRegex);

        Spider.create(processor)
                .addUrl(seedUrl)
                .thread(threads)
                .run();

        List<Product> products = processor.getProducts();
        exporter.export(products);
    }
}
