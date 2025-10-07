package com.dylan.LiveFeed;

import com.dylan.LiveFeed.dto.AuthenticationRequest;
import com.dylan.LiveFeed.dto.AuthenticationResponse;
import com.dylan.LiveFeed.auth.AuthenticationService;
import com.dylan.LiveFeed.auth.RegisterRequest;
import com.dylan.LiveFeed.config.JwtService;
import com.dylan.LiveFeed.dto.PostRequest;
import com.dylan.LiveFeed.post.*;
import com.dylan.LiveFeed.user.User;
import com.dylan.LiveFeed.user.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor
public class
Tests {

    @Autowired
    private PostService postService;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private InteractionRepo interactionRepo;

    @Test
    void testRegisterUser(){
        RegisterRequest request = createUser("Dylan", "Anderson", "dylanUnitTest@gmail.com", "password");


        AuthenticationResponse response = authService.register(request);

        String token = response.getToken();
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username)
                .orElseThrow();

        assertEquals("Dylan", user.getFirstName());
        assertEquals("Anderson", user.getLastName());
        assertEquals("dylanUnitTest@gmail.com", user.getEmail());
    }
    @Test
    void testRegisterDuplicateUser(){
        RegisterRequest request1 = createUser("Dylan", "Anderson", "dylanUnitTest@gmail.com", "password");
        authService.register(request1);

        RegisterRequest request2 = createUser("Dylan2", "Anderson", "dylanUnitTest@gmail.com", "password");

        assertThrows(
                RuntimeException.class,
                () -> authService.register(request2)
        );
    }

    @Test
    void testAuthUser(){
        RegisterRequest regRequest = createUser("Dylan", "Anderson", "dylanUnitTest@gmail.com", "password");
        authService.register(regRequest);

        AuthenticationRequest authRequest = new AuthenticationRequest("dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.authenticate(authRequest);

        String token = response.getToken();
        assertNotNull(token);
    }

    @Test
    void testCreatePost(){
        RegisterRequest regRequest = createUser("Dylan","Anderson", "dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.register(regRequest);

        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username).orElse(null);

        String message = "message";
        PostRequest request = new PostRequest(message);
        Post post = postService.createPost(request, user);

        assertEquals(message, post.getMessage());
        assertEquals(user, post.getUser());
        assertFalse(post.isUpdated());
        assertNull(post.getUpdatedAt());
        assertEquals(0, post.getLikeCount());


    }

    @Test
    void testDeletePost(){
        RegisterRequest regRequest = createUser("Dylan","Anderson", "dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.register(regRequest);

        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username).orElse(null);

        String message = "message";
        PostRequest request = new PostRequest(message);
        Post post = postService.createPost(request, user);
        Long postId = post.getId();

        boolean deletePost = postService.deletePost(postId, user);

        assertTrue(deletePost);
        assertTrue(postRepo.findById(postId).isEmpty());


    }

    @Test
    void testUpdatePost(){
        RegisterRequest regRequest = createUser("Dylan","Anderson", "dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.register(regRequest);

        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username).orElse(null);

        String message = "message";
        PostRequest request = new PostRequest(message);
        Post post = postService.createPost(request, user);

        String newMessage = "newMessage";
        PostRequest newRequest = new PostRequest(newMessage);
        postService.updatePost(post.getId(),newRequest, user);

        assertEquals(newMessage, post.getMessage());
        assertTrue(post.isUpdated());
        assertNotNull(post.getUpdatedAt());
    }

    @Test
    void testGetPostById(){
        RegisterRequest regRequest = createUser("Dylan","Anderson", "dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.register(regRequest);

        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username).orElse(null);

        String message = "message";
        PostRequest request = new PostRequest(message);
        Post post = postService.createPost(request, user);

        Post postById = postService.getPostById(post.getId());

        assertEquals(post, postById);
    }

    @Test
    void testGetPostsByUser(){
        RegisterRequest regRequest = createUser("Dylan","Anderson", "dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.register(regRequest);

        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username).orElse(null);

        String message = "message";
        PostRequest request = new PostRequest(message);
        Post post1 = postService.createPost(request, user);
        Post post2 = postService.createPost(request, user);
        Post post3 = postService.createPost(request, user);
        Post post4 = postService.createPost(request, user);

        assert user != null;
        List<Post> posts = postService.getPostsByUser(user.getId());
        for(Post post : posts){
            assertEquals(user, post.getUser());
        }

    }

    @Test
    void testInteraction(){
        RegisterRequest regRequest = createUser("Dylan","Anderson", "dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.register(regRequest);

        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username).orElse(null);

        String message = "message";
        PostRequest request = new PostRequest(message);
        Post post1 = postService.createPost(request, user);
        Post post2 = postService.createPost(request, user);
        Post post3 = postService.createPost(request, user);

        assert user != null;
        postService.interaction(post1.getId(), user.getId(), InteractionType.LIKE);
        postService.interaction(post2.getId(), user.getId(), InteractionType.DISLIKE);
        postService.interaction(post3.getId(), user.getId(), InteractionType.LIKE);

        List<Interaction> interactions = interactionRepo.findInteractionByUser(user);

        Interaction i1 = interactionRepo.findByUserAndPost(user, post1).orElse(null);
        Interaction i2 = interactionRepo.findByUserAndPost(user, post2).orElse(null);
        Interaction i3 = interactionRepo.findByUserAndPost(user, post3).orElse(null);

        assertEquals(i1, interactions.get(0));
        assertEquals(i2, interactions.get(1));
        assertEquals(i3, interactions.get(2));

    }

    @Test
    void testGetLast20Posts(){
        RegisterRequest regRequest = createUser("Dylan","Anderson", "dylanUnitTest@gmail.com", "password");
        AuthenticationResponse response = authService.register(regRequest);

        String token = response.getToken();
        String username = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(username).orElse(null);

        for(int i = 0; i < 20;i++){
            PostRequest request = new PostRequest("message");
            Post post = postService.createPost(request, user);
            assertNotNull(post);
        }

        List<Post> posts = postRepo.findLast20Posts();

        assertEquals(20, posts.size());
    }

    private RegisterRequest createUser(String firstName, String lastName, String email, String password){
        RegisterRequest request = new RegisterRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }

}
