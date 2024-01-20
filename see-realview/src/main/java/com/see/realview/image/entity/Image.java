package com.see.realview.image.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "image_tb",
        indexes = {
                @Index(columnList = "link")
        })
@Getter
@NoArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 500)
    private String link;

    @Column(nullable = false)
    private Boolean advertisement;

    @Column(nullable = false)
    private Long count;


    @Builder
    public Image(Long id, String link, Boolean advertisement, Long count) {
        this.id = id;
        this.link = link;
        this.advertisement = advertisement;
        this.count = count;
    }

    public static Image of(String link, Boolean advertisement) {
        return Image.builder()
                .link(link)
                .advertisement(advertisement)
                .count(1L) // batch update 시에 중복 레코드는 count + 1로 업데이트하기 때문에 디폴트는 항상 1로 고정
                .build();
    }

    public void updateCount(Long count) {
        this.count = count;
    }
}
