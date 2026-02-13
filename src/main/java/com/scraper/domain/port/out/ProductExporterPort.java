package com.scraper.domain.port.out;

import com.scraper.domain.model.Product;

import java.util.List;

public interface ProductExporterPort {
    void export(List<Product> products);
}
