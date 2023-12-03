package com.see.realview.image.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.see.realview.image.entity.ParsedImage;
import com.see.realview.image.entity.QParsedImage;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParsedImageRepositoryImpl implements ParsedImageRepository {

    private final EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    private final static QParsedImage TABLE = QParsedImage.parsedImage;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final String PARSED_IMAGE_TABLE = "parsed_image_tb";

    private final static int IMAGE_CACHING_SIZE = 100;


    public ParsedImageRepositoryImpl(@Autowired EntityManager entityManager,
                                     @Autowired JPAQueryFactory jpaQueryFactory,
                                     @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = jpaQueryFactory;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<ParsedImage> findAllByUrlIn(List<String> urls) {
        return jpaQueryFactory
                .selectFrom(TABLE)
                .where(TABLE.url.in(urls))
                .fetch();
    }

    @Override
    public List<ParsedImage> findCachingImages() {
        return jpaQueryFactory
                .selectFrom(TABLE)
                .orderBy(TABLE.count.desc())
                .limit(IMAGE_CACHING_SIZE)
                .fetch();
    }

    @Override
    public void save(ParsedImage image) {
        entityManager.persist(image);
    }

    @Override
    public void saveAll(List<ParsedImage> images) {
        String sql = String.format("""
                INSERT INTO `%s` (url, advertisement, count)
                VALUES (:url, :advertisement, :count)
                ON DUPLICATE KEY UPDATE url = :url, advertisement = :advertisement, count = count + :count
                """, PARSED_IMAGE_TABLE);

        SqlParameterSource[] parameterSources = images
                .stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }
}
