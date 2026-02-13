package com.scraper.domain.port.out;

import com.scraper.domain.model.Product;

import java.util.Set;

public interface ProductPostProcessor {
    Set<Product> process(Set<Product> products);
}
