package com.chat.zipchat.clone.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class LocalDataPojo {

    @Id
    public Long id;

    @Index(unique = true)
    private String messageId;

    private String userId;
    private String storagePath;
    private int isDownloaded;

    @Generated(hash = 371915260)
    public LocalDataPojo(Long id, String messageId, String userId,
            String storagePath, int isDownloaded) {
        this.id = id;
        this.messageId = messageId;
        this.userId = userId;
        this.storagePath = storagePath;
        this.isDownloaded = isDownloaded;
    }
    @Generated(hash = 997680565)
    public LocalDataPojo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMessageId() {
        return this.messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public String getUserId() {
        return this.userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getStoragePath() {
        return this.storagePath;
    }
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }
    public int getIsDownloaded() {
        return this.isDownloaded;
    }
    public void setIsDownloaded(int isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

}
