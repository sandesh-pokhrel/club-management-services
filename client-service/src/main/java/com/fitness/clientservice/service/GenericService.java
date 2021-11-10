package com.fitness.clientservice.service;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.Objects;

public abstract class GenericService {

    private static final String PAGE = "page";
    private static final String ORDERBY = "orderBy";
    private static final String ORDER = "order";
    private static final String SEARCH = "search";

    protected Integer getPageNumber(Map<String, String> paramMap) {
        String pageParam = paramMap.get(PAGE);
        return Objects.nonNull(pageParam) && NumberUtils.isCreatable(pageParam) ?
                Integer.parseInt(pageParam) : 0;
    }

    protected String getOrderBy(Map<String, String> paramMap, String defaultOrderBy) {
        String orderBy = paramMap.get(ORDERBY);
        return Objects.nonNull(orderBy) ? orderBy : defaultOrderBy;
    }

    protected Sort.Direction getOrder(Map<String, String> paramMap) {
        String order = paramMap.get(ORDER);
        return Objects.nonNull(order) ?
                order.equalsIgnoreCase("ASC")
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC
                : Sort.Direction.ASC;
    }

    protected String getSearch(Map<String, String> paramMap) {
        return
                Objects.nonNull(paramMap.get(SEARCH)) &&
                        !paramMap.get(SEARCH).equalsIgnoreCase(Strings.EMPTY)
                        ? paramMap.get(SEARCH).toLowerCase() : null;
    }
}
