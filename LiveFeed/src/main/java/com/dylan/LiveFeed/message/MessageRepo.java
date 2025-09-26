package com.dylan.LiveFeed.message;

import com.dylan.LiveFeed.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    @Query(value = "SELECT * FROM messages ORDER BY created_at DESC LIMIT 20", nativeQuery = true)
    List<Message> findLast20Messages();

    List<Message> findByUserOrderByCreatedAtDesc(User user);
}
