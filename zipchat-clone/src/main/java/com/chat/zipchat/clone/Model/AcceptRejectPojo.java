package com.chat.zipchat.clone.Model;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class AcceptRejectPojo {

    @Index(unique = true)
    private String friendId;

    private String receive_request_count;
    private String send_request_count;
    private String status;

    @Generated(hash = 136525098)
    public AcceptRejectPojo(String friendId, String receive_request_count,
            String send_request_count, String status) {
        this.friendId = friendId;
        this.receive_request_count = receive_request_count;
        this.send_request_count = send_request_count;
        this.status = status;
    }
    @Generated(hash = 958418911)
    public AcceptRejectPojo() {
    }
    public String getFriendId() {
        return this.friendId;
    }
    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }
    public String getReceive_request_count() {
        return this.receive_request_count;
    }
    public void setReceive_request_count(String receive_request_count) {
        this.receive_request_count = receive_request_count;
    }
    public String getSend_request_count() {
        return this.send_request_count;
    }
    public void setSend_request_count(String send_request_count) {
        this.send_request_count = send_request_count;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    

}
