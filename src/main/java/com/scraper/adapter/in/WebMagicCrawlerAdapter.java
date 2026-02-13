package com.scraper.adapter.in;

import com.scraper.Config;
import com.scraper.domain.model.Product;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WebMagicCrawlerAdapter implements PageProcessor {

    private static final Logger log = LoggerFactory.getLogger(WebMagicCrawlerAdapter.class);

    private final Config config;
    private final Set<Product> products = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Site site;

    public WebMagicCrawlerAdapter(Config config) {
        this.config = config;
        this.site = Site.me()
                .setRetryTimes(config.retryTimes())
                .setSleepTime(config.sleepTime())
                .setUserAgent(config.userAgent());
    }

    @Override
    public void process(Page page) {
        // Follow all links within the test site
        page.addTargetRequests(page.getHtml().links()
                .regex(config.followUrlRegex())
                .all());

        if (!page.getUrl().toString().startsWith(config.productUrlPrefix())) {
            page.setSkip(true);
            return;
        }

        log.info("Processing product page: {}", page.getUrl());

        Document doc = page.getHtml().getDocument();

        String name = extractText(doc, config.selectors().name());
        if (name.isEmpty()) {
            page.setSkip(true);
            return;
        }

        String description = extractText(doc, config.selectors().description());
        BigDecimal price = parsePrice(doc);
        List<String> colors = parseColors(doc);
        List<String> memory = parseMemory(doc);

        Product product = new Product(name, description, price, colors, memory);
        log.info("Scraped product: {} | price: {}", name, price);
        products.add(product);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public Set<Product> getProducts() {
        return Set.copyOf(products);
    }

    private String extractText(Document doc, String cssQuery) {
        Element el = doc.selectFirst(cssQuery);
        return el != null ? el.text().trim() : "";
    }

    private BigDecimal parsePrice(Document doc) {
        Element priceEl = doc.selectFirst(config.selectors().price());
        if (priceEl == null) {
            return BigDecimal.ZERO;
        }
        String raw = priceEl.text().replaceAll("[^\\d.]", "");
        if (raw.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(raw);
    }

    private List<String> parseColors(Document doc) {
        Elements colorEls = doc.select(config.selectors().colors());
        List<String> colors = new ArrayList<>();
        for (Element el : colorEls) {
            String value = el.attr("value").trim();
            if (!value.isEmpty()) {
                colors.add(value);
            }
        }
        return colors;
    }

    private List<String> parseMemory(Document doc) {
        Elements memoryEls = doc.select(config.selectors().memory());
        List<String> memory = new ArrayList<>();
        for (Element el : memoryEls) {
            String value = el.attr("value").trim();
            if (!value.isEmpty()) {
                memory.add(value);
            }
        }
        return memory;
    }
}
