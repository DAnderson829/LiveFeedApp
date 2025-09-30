package com.dylan.LiveFeed.post;

import com.dylan.LiveFeed.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InteractionRepo extends JpaRepository<Interaction, Long> {
    List<Interaction> findInteractionByUser(User user);

    Optional<Interaction> findByUserAndPost(User user, Post post);
}
