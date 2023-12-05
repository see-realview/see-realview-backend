package com.see.realview.search.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "search_history_tb"
)
@NoArgsConstructor
@Getter
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private LocalDateTime time;

    @Builder
    public SearchHistory(Long id, String keyword, LocalDateTime time) {
        this.id = id;
        this.keyword = keyword;
        this.time = time;
    }
}
