package com.see.realview.search.repository.impl;

import com.see.realview.search.entity.SearchItem;
import com.see.realview.search.repository.SearchItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SearchItemRepositoryImpl implements SearchItemRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final String TABLE = "search_item_tb";


    public SearchItemRepositoryImpl(@Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void saveAll(List<SearchItem> items) {
        String sql = String.format("""
                    INSERT INTO `%s` (title, url, description, blogger_name, post_date, content, advertisement)
                    VALUES (:title, :url, :description, :bloggerName, :postDate, :content, :advertisement)
                    ON DUPLICATE KEY UPDATE title = title, url = url, description = description, blogger_name = blogger_name, post_date = post_date, content = content, advertisement = advertisement
                """, TABLE);

        SqlParameterSource[] parameterSources = items
                .stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }
}
