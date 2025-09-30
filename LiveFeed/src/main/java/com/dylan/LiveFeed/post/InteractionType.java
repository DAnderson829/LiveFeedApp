package com.dylan.LiveFeed.post;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InteractionType {
    LIKE(1),
    DISLIKE(-1);

    private final int value;

}
