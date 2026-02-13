package com.scraper.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Product(
        String name,
        String description,
        BigDecimal price,
        List<String> colors,
        List<String> memory
) {
}
