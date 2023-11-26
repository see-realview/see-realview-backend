package com.see.realview.image.repository;

import com.see.realview.image.entity.ParsedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParsedImageJPARepository extends JpaRepository<ParsedImage, Long> {
    List<ParsedImage> findTop30ByOrderByCountDesc();
    List<ParsedImage> findAllByUrlIn(List<String> urls);
}
