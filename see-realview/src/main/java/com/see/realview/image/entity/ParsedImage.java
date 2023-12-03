package com.see.realview.image.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "parsed_image_tb",
        indexes = {
                @Index(columnList = "url")
        })
@Getter
@NoArgsConstructor
public class ParsedImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String url;

    @Column(nullable = false)
    private Boolean advertisement;

    @Column(nullable = false)
    private Long count;


    @Builder
    public ParsedImage(Long id, String url, Boolean advertisement, Long count) {
        this.id = id;
        this.url = url;
        this.advertisement = advertisement;
        this.count = count;
    }

    public static ParsedImage of(String url, Boolean advertisement) {
        return ParsedImage.builder()
                .url(url)
                .advertisement(advertisement)
                .count(1L) // batch update 시에 중복 레코드는 count + 1로 업데이트하기 때문에 디폴트는 항상 1로 고정
                .build();
    }

    public void updateCount(Long count) {
        this.count = count;
    }
}
