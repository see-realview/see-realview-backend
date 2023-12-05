package com.see.realview.search.entity;

import com.see.realview.analyzer.dto.response.PostDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "search_item_tb"
)
@NoArgsConstructor
@Getter
public class SearchItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "description")
    private String description;

    @Column(name = "blogger_name")
    private String bloggerName;

    @Column(name = "post_date")
    private String postDate;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "advertisement")
    private Boolean advertisement;

    @Builder
    public SearchItem(Long id, String title, String url, String description, String bloggerName, String postDate, String content, Boolean advertisement) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.description = description;
        this.bloggerName = bloggerName;
        this.postDate = postDate;
        this.content = content;
        this.advertisement = advertisement;
    }

    public static SearchItem of(PostDTO postDTO, String text) {
        return SearchItem.builder()
                .title(postDTO.title())
                .url(postDTO.url())
                .description(postDTO.description())
                .bloggerName(postDTO.bloggerName())
                .postDate(postDTO.date())
                .content(text)
                .advertisement(postDTO.advertisement())
                .build();
    }
}
