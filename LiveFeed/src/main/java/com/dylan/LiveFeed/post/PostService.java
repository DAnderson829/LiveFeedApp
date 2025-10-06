package com.dylan.LiveFeed.post;

import com.dylan.LiveFeed.user.User;
import com.dylan.LiveFeed.user.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepo postRepo;

    private final UserRepo userRepo;

    private final InteractionService interactionService;

    private final InteractionRepo interactionRepo;

    public Post createPost(String message, User user){
        Post post = Post.builder()
                .message(message)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updated(false)
                .updatedAt(null)
                .likeCount(0)
                .build();

        return postRepo.save(post);
    }

    public boolean deletePost(Long id, User user){
        Optional<Post> postOpt = postRepo.findById(id);
        if (postOpt.isEmpty()) return false;

        Post post = postOpt.get();
        if (!post.getUser().equals(user)) return false;

        postRepo.delete(post);
        return true;
    }

    public void updatePost(Long id, User user, String message){
        Optional<Post> postOpt = postRepo.findById(id);
        if (postOpt.isEmpty()) return;

        Post post = postOpt.get();
        if (!post.getUser().equals(user)) return;

        post.setMessage(message);
        post.setUpdated(true);
        post.setUpdatedAt(LocalDateTime.now());
        postRepo.save(post);

        return;
    }

    public Post getPostById(Long id){
        Optional<Post> postOpt = postRepo.findById(id);
        return postOpt.orElse(null);

    }

    public List<Post> getPostsByUser(Long id){
        return postRepo.findByUserId(id);
    }

    public void interaction(Long postId, Long userId, InteractionType type){
        Interaction interaction = interactionService.interactWithPost(postId, userId, type);
    }

    public List<Interaction> getAllUserInteractions(Long userId){
        User user = userRepo.findById(userId).orElse(null);
        return interactionRepo.findInteractionByUser(user);
    }

    public List<Post> getLast20Posts(){
        return postRepo.findLast20Posts();
    }
}
