package com.scraper.adapter.out;

import com.scraper.domain.model.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CloneByProductMemoryPostProcessorTest {

    private final CloneByProductMemoryPostProcessor processor = new CloneByProductMemoryPostProcessor("GB");

    @Test
    void shouldReturnSameProductIfMemoryIsEmpty() {
        Product product = new Product("Phone", "Desc", new BigDecimal("100"), List.of("Red"), Collections.emptyList());
        Set<Product> input = Set.of(product);

        Set<Product> result = processor.process(input);

        assertThat(result).hasSize(1).contains(product);
    }

    @Test
    void shouldReturnSameProductIfMemoryIsNull() {
        Product product = new Product("Phone", "Desc", new BigDecimal("100"), List.of("Red"), null);
        Set<Product> input = Set.of(product);

        Set<Product> result = processor.process(input);

        assertThat(result).hasSize(1).contains(product);
    }

    @Test
    void shouldCloneProductForEveryMemoryOption() {
        Product product = new Product("Phone", "Desc", new BigDecimal("100"), List.of("Red"), List.of("64", "128"));
        Set<Product> input = Set.of(product);

        Set<Product> result = processor.process(input);

        assertThat(result).hasSize(2);

        assertThat(result).anySatisfy(p -> {
            assertThat(p.name()).isEqualTo("Phone 64 GB");
            assertThat(p.memory()).isEqualTo(List.of("64"));
        });
        assertThat(result).anySatisfy(p -> {
            assertThat(p.name()).isEqualTo("Phone 128 GB");
            assertThat(p.memory()).isEqualTo(List.of("128"));
        });
    }
}
