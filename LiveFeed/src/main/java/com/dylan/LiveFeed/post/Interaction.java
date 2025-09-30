package com.dylan.LiveFeed.post;

import com.dylan.LiveFeed.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InteractionType type;

    @JoinColumn(name = "user_id")
    @OneToMany
    private User user;

    @JoinColumn(name = "post_id")
    @OneToMany
    private Post post;
}
