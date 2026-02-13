package com.scraper.adapter.out;

import com.scraper.domain.model.Product;
import com.scraper.domain.port.out.ProductPostProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CloneByProductMemoryPostProcessor implements ProductPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(CloneByProductMemoryPostProcessor.class);

    private final String memorySuffix;

    public CloneByProductMemoryPostProcessor(String memorySuffix) {
        this.memorySuffix = memorySuffix;
    }

    @Override
    public Set<Product> process(Set<Product> products) {
        Set<Product> postprocessed = new LinkedHashSet<>();

        for (Product product : products) {
            if (product.memory() == null || product.memory().isEmpty()) {
                postprocessed.add(product);
            } else {
                for (String memoryOption : product.memory()) {
                    Product variant = new Product(
                            product.name() + " " + memoryOption + " " + memorySuffix,
                            product.description(),
                            product.price(),
                            product.colors(),
                            List.of(memoryOption)
                    );
                    postprocessed.add(variant);
                }
                log.debug("Expanded product '{}' into {} variants by memory",
                        product.name(), product.memory().size());
            }
        }

        log.info("Postprocessing complete: {} products -> {} products", products.size(), postprocessed.size());
        return postprocessed;
    }
}
