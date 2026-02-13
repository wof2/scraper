package com.scraper;

public record Config(
        String seedUrl,
        String productUrlPrefix,
        String followUrlRegex,
        int threads,
        int retryTimes,
        int sleepTime,
        String userAgent,
        String outputFile,
        Selectors selectors
) {
    public record Selectors(
            String name,
            String description,
            String price,
            String colors,
            String memory
    ) {
    }
}
