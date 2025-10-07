package com.dylan.LiveFeed.post;

import com.dylan.LiveFeed.dto.PostRequest;
import com.dylan.LiveFeed.dto.PostResponse;
import com.dylan.LiveFeed.user.User;
import com.dylan.LiveFeed.user.UserRepo;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.web.servlet.function.ServerResponse.status;

@RestController
@RequestMapping("/api/v1/liveFeed")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepo userRepo;

    @GetMapping("/home")
    public ResponseEntity<List<PostResponse>> home(){
        List<Post> posts = postService.getLast20Posts();
        List<PostResponse> last20 = postService.convertFromPostToResponse(posts);
        return ResponseEntity.ok().body(last20);
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request,
                                                   @AuthenticationPrincipal User user){
        Post post = postService.createPost(request, user);
        PostResponse response = new PostResponse(
                post.getId(),
                post.getMessage(),
                user.getUsername(),
                post.getCreatedAt(),
                false,
                null,
                0
        );
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping("delete/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Long postId,
                                             @AuthenticationPrincipal User user){
        boolean deleted = postService.deletePost(postId, user);
        if(!deleted){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this post.");
        }else{
            return ResponseEntity.ok().body("Post deleted.");
        }
    }

    @PutMapping("update/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long postId,
                                                   @RequestBody PostRequest request,
                                                   @AuthenticationPrincipal User user){
        Post post = postService.getPostById(postId);

        if(post == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Post updatedPost = postService.updatePost(postId, request, user);

        PostResponse response = new PostResponse(
                postId,
                updatedPost.getMessage(),
                user.getUsername(),
                updatedPost.getCreatedAt(),
                true,
                updatedPost.getUpdatedAt(),
                updatedPost.getLikeCount()
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("get/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long postId) {
        Post post = postService.getPostById(postId);

        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        PostResponse response = new PostResponse(
                post.getId(),
                post.getMessage(),
                post.getUser().getUsername(),
                post.getCreatedAt(),
                post.isUpdated(),
                post.getUpdatedAt(),
                post.getLikeCount()
        );
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/get/all/{email}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(@PathVariable String email){
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Post> posts = postService.getPostsByUser(user.getId());

        if(posts.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        List<PostResponse> response = posts.stream().map(post -> new PostResponse(
                post.getId(),
                post.getMessage(),
                post.getUser().getUsername(),
                post.getCreatedAt(),
                post.isUpdated(),
                post.getUpdatedAt(),
                post.getLikeCount()
        )).toList();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/post/interact/{postId}")
    public ResponseEntity<String> interact(@PathVariable Long postId,
                                           @RequestParam InteractionType type,
                                           @AuthenticationPrincipal User user){

        if(postService.getPostById(postId) == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        postService.interaction(postId, user.getId(), type);
        return ResponseEntity.ok().body("Successful.");
    }

    @GetMapping("/get/interactions/{email}")
    public ResponseEntity<List<Interaction>> getAllInteractionsByUser(@PathVariable String email){
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Interaction> posts = postService.getAllUserInteractions(user.getId());
        return ResponseEntity.ok().body(posts);
    }


}