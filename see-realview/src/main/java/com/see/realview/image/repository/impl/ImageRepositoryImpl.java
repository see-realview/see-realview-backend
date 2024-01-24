package com.see.realview.image.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.see.realview.image.entity.Image;
import com.see.realview.image.entity.QImage;
import com.see.realview.image.repository.ImageRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ImageRepositoryImpl implements ImageRepository {

    private final EntityManager entityManager;

    private final JPAQueryFactory jpaQueryFactory;

    private final static QImage TABLE = QImage.image;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final String IMAGE_TABLE = "image_tb";

    @Value("${api.image.cache-size}")
    private int IMAGE_CACHING_SIZE;


    public ImageRepositoryImpl(@Autowired EntityManager entityManager,
                               @Autowired JPAQueryFactory jpaQueryFactory,
                               @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.entityManager = entityManager;
        this.jpaQueryFactory = jpaQueryFactory;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Image> findAllByUrlIn(List<String> urls) {
        if (urls.isEmpty()) {
            return List.of();
        }

        return jpaQueryFactory
                .selectFrom(TABLE)
                .where(TABLE.link.in(urls))
                .fetch();
    }

    @Override
    public List<Image> findCachingImages() {
        return jpaQueryFactory
                .selectFrom(TABLE)
                .orderBy(TABLE.count.desc())
                .limit(IMAGE_CACHING_SIZE)
                .fetch();
    }

    @Override
    public void save(Image image) {
        entityManager.persist(image);
    }

    @Override
    public void saveAll(List<Image> images) {
        String sql = String.format("""
                INSERT INTO `%s` (link, advertisement, count)
                VALUES (:link, :advertisement, :count)
                ON DUPLICATE KEY UPDATE link = :link, advertisement = :advertisement, count = count + :count
                """, IMAGE_TABLE);

        SqlParameterSource[] parameterSources = images
                .stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, parameterSources);
    }
}
