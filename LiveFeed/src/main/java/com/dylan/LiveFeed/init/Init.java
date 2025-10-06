package com.dylan.LiveFeed.init;

import com.dylan.LiveFeed.auth.AuthenticationService;
import com.dylan.LiveFeed.auth.RegisterRequest;
import com.dylan.LiveFeed.post.PostRepo;
import com.dylan.LiveFeed.post.PostService;
import com.dylan.LiveFeed.user.User;
import com.dylan.LiveFeed.user.UserRepo;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Random;


@Component
@Transactional
public class Init {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PostRepo postRepo;

    @PostConstruct
    public void init(){
        if(userRepo.count() == 0 || postRepo.count() == 0){

        User dylan = createUser("Dylan", "Anderson",
                "dylan@gmail.com", "password");

        User john = createUser("John", "Doe",
                "john@gmail.com", "password");

        User walt = createUser("Walter", "White",
                "walter@gmail.com", "password");

        User[] users = {dylan, john, walt};

        String[] posts = {"Hello", "Welcome", "Spring", "First post", "Test", "init"};

        Random rand = new Random();
        for(int i = 0;i<20;i++){
            User randomUser = users[rand.nextInt(users.length)];
            String randomPost = posts[rand.nextInt(posts.length)];
            postService.createPost(randomPost, randomUser);
        }

        }
    }

    private User createUser(String firstName, String lastName, String email, String password){
        try {
            RegisterRequest request = new RegisterRequest(firstName, lastName, email, password);
            authenticationService.register(request);
            return userRepo.findByEmail(email).orElse(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


}
