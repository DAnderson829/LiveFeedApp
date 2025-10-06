package com.dylan.LiveFeed.post;

import com.dylan.LiveFeed.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    @Query(value = "SELECT * FROM post ORDER BY created_at DESC LIMIT 20", nativeQuery = true)
    List<Post> findLast20Posts();

    List<Post> findByUserId(Long userId);
}
