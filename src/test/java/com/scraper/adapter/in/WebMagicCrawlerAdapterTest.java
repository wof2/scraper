package com.scraper.adapter.in;

import com.scraper.Config;
import com.scraper.domain.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class WebMagicCrawlerAdapterTest {

    private Config config;
    private WebMagicCrawlerAdapter adapter;

    @BeforeEach
    void setUp() {
        Config.Selectors selectors = new Config.Selectors(
                ".name",
                ".desc",
                ".price",
                ".color",
                ".memory"
        );
        config = new Config(
                "http://example.com",
                "http://example.com/product/",
                "http://example.com/.*",
                1, 3, 1000, "UA", "output.json", "GB",
                selectors
        );
        adapter = new WebMagicCrawlerAdapter(config);
    }

    @Test
    void shouldSkipPageIfUrlDoesNotMatchProductPrefix() {
        Page page = new Page();
        page.setUrl(new PlainText("http://example.com/not-a-product"));
        page.setHtml(new Html("<html><body></body></html>"));

        adapter.process(page);

        // WebMagic 1.0.3 uses boolean field for skip
        assertThat(page.getResultItems().isSkip()).isTrue();
        assertThat(adapter.getProducts()).isEmpty();
    }

    @Test
    void shouldProcessProductPageCorrectly() {
        String htmlContent = """
                <html>
                    <body>
                        <div class="name">Test Product</div>
                        <div class="desc">Test Description</div>
                        <div class="price">$1,234.56</div>
                        <div class="color" value="Red">Red</div>
                        <div class="color" value="Blue">Blue</div>
                        <div class="memory" value="64">64GB</div>
                        <div class="memory" value="128">128GB</div>
                    </body>
                </html>
                """;
        Page page = new Page();
        page.setUrl(new PlainText("http://example.com/product/1"));
        page.setHtml(new Html(htmlContent));

        adapter.process(page);

        assertThat(page.getResultItems().isSkip()).isFalse();
        Set<Product> products = adapter.getProducts();
        assertThat(products).hasSize(1);

        Product product = products.iterator().next();
        assertThat(product.name()).isEqualTo("Test Product");
        assertThat(product.description()).isEqualTo("Test Description");
        assertThat(product.price()).isEqualTo(new BigDecimal("1234.56"));
        assertThat(product.colors()).isEqualTo(List.of("Red", "Blue"));
        assertThat(product.memory()).isEqualTo(List.of("64", "128"));
    }

    @Test
    void shouldSkipProductIfNameIsMissing() {
        String htmlContent = """
                <html>
                    <body>
                        <div class="desc">Test Description</div>
                        <div class="price">$1,234.56</div>
                    </body>
                </html>
                """;
        Page page = new Page();
        page.setUrl(new PlainText("http://example.com/product/1"));
        page.setHtml(new Html(htmlContent));

        adapter.process(page);

        assertThat(page.getResultItems().isSkip()).isTrue();
        assertThat(adapter.getProducts()).isEmpty();
    }

    @Test
    void shouldHandleMissingOptionalFields() {
        String htmlContent = """
                <html>
                    <body>
                        <div class="name">Test Product</div>
                    </body>
                </html>
                """;
        Page page = new Page();
        page.setUrl(new PlainText("http://example.com/product/1"));
        page.setHtml(new Html(htmlContent));

        adapter.process(page);

        assertThat(page.getResultItems().isSkip()).isFalse();
        Set<Product> products = adapter.getProducts();
        assertThat(products).hasSize(1);
        Product product = products.iterator().next();
        assertThat(product.name()).isEqualTo("Test Product");
        assertThat(product.description()).isEqualTo("");
        assertThat(product.price()).isEqualTo(BigDecimal.ZERO);
        assertThat(product.colors()).isEmpty();
        assertThat(product.memory()).isEmpty();
    }
}
