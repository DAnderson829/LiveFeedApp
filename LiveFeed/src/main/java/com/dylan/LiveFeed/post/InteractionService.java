package com.dylan.LiveFeed.post;

import com.dylan.LiveFeed.user.User;
import com.dylan.LiveFeed.user.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractionService {

    private final PostRepo postRepo;

    private final UserRepo userRepo;

    private final InteractionRepo interactionRepo;

    public Interaction interactWithPost(Long postId, Long userId, InteractionType type){
        Optional<Post> postOpt = postRepo.findById(postId);
        if(postOpt.isEmpty()) return null;
        Post post = postOpt.get();

        Optional<User> userOpt = userRepo.findById(userId);
        if(userOpt.isEmpty()) return null;
        User user = userOpt.get();
        Interaction interaction = interactionRepo.findByUserAndPost(user, post).orElse(null);

        if(interaction == null) {
             interaction = Interaction.builder()
                    .type(type)
                    .user(user)
                    .post(post)
                    .build();
             interactionRepo.save(interaction);
             post.setLikeCount(post.getLikeCount() + type.getValue());
        }else if(!interaction.getType().equals(type)){
            post.setLikeCount(post.getLikeCount() - interaction.getType().getValue());
            post.setLikeCount(post.getLikeCount() + type.getValue());

            interaction.setType(type);
            interactionRepo.save(interaction);
        }else{
            return null;
        }

        postRepo.save(post);
        return interaction;
    }
}
