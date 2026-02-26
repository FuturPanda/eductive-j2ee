package com.formations.spring_products_api.dto;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

public record PaginatedResponse<T>(
    List<T> content,
    Meta meta
) {
    public record Meta(
        int offset,
        int limit,
        int count,
        long totalCount,
        String sort,
        String order
    ) {}

    public static <T> PaginatedResponse<T> from(Page<T> page) {
        Sort.Order sortOrder = page.getSort().stream().findFirst().orElse(null);
        String order = sortOrder != null ? sortOrder.getProperty() : null;
        String sort = sortOrder != null ? sortOrder.getDirection().name() : null;

        return new PaginatedResponse<>(
            page.getContent(),
            new Meta(
                (int) page.getPageable().getOffset(),
                page.getSize(),
                page.getNumberOfElements(),
                page.getTotalElements(),
                sort,
                order
            )
        );
    }
}
