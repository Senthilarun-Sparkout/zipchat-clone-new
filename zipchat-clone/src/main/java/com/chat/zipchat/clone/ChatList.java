package com.chat.zipchat.clone;

import com.chat.zipchat.clone.Model.ChatListPojo;

import java.util.List;

public interface ChatList {
    void callback(List<ChatListPojo> chatPojoList);
}
