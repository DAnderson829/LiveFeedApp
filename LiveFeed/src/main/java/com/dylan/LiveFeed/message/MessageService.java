package com.dylan.LiveFeed.message;

import com.dylan.LiveFeed.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    public void createMessage(String message, User user){

    }

    public void deleteMessage(Long id, User user){

    }

    public void updateMessage(Long id, User user){

    }

    public Message getMessageById(Long id){

    }

    public List<Message> getMessagesByUser(User user){

    }

    public void likeOrDislikeMessage(Long id, User user){

    }


}
