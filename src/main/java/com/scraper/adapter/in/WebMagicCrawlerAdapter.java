package com.scraper.adapter.in;

import com.scraper.domain.model.Product;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebMagicCrawlerAdapter implements PageProcessor {

    private final String productUrlPrefix;
    private final String followUrlRegex;
    private final List<Product> products = Collections.synchronizedList(new ArrayList<>());

    private final Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setUserAgent("Mozilla/5.0");

    public WebMagicCrawlerAdapter(String productUrlPrefix, String followUrlRegex) {
        this.productUrlPrefix = productUrlPrefix;
        this.followUrlRegex = followUrlRegex;
    }

    @Override
    public void process(Page page) {
        // Follow all links within the test site
        page.addTargetRequests(page.getHtml().links()
                .regex(followUrlRegex)
                .all());

        // Only extract product data from product pages
        if (!page.getUrl().toString().startsWith(productUrlPrefix)) {
            page.setSkip(true);
            return;
        }

        Document doc = page.getHtml().getDocument();

        String name = extractText(doc, "h1");
        if (name.isEmpty()) {
            page.setSkip(true);
            return;
        }

        String description = extractText(doc, ".description, .product-description, [itemprop=description]");
        BigDecimal price = parsePrice(doc);
        List<String> colors = parseColors(doc);

        products.add(new Product(name, description, price, colors));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    private String extractText(Document doc, String cssQuery) {
        Element el = doc.selectFirst(cssQuery);
        return el != null ? el.text().trim() : "";
    }

    private BigDecimal parsePrice(Document doc) {
        Element priceEl = doc.selectFirst(".price, [itemprop=price], .product-price");
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
        Elements colorEls = doc.select(".color-option, .color-swatch, [data-color]");
        if (colorEls.size() <= 1) {
            return List.of();
        }
        List<String> colors = new ArrayList<>();
        for (Element el : colorEls) {
            String color = el.attr("data-color");
            if (color.isEmpty()) {
                color = el.text().trim();
            }
            if (!color.isEmpty()) {
                colors.add(color);
            }
        }
        return colors;
    }
}
