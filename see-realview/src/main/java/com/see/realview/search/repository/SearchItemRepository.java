package com.see.realview.search.repository;

import com.see.realview.search.entity.SearchItem;

import java.util.List;

public interface SearchItemRepository {
    void saveAll(List<SearchItem> items);
}
