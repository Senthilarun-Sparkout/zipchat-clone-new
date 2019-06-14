package com.chat.zipchat.clone.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class GroupItems {

    private static final long serialVersionUID = 2L;

    @Index(unique = true)
    private String id;
    private String name;
    private String group_picture;
    private String description;

    @Generated(hash = 1886385017)
    public GroupItems(String id, String name, String group_picture,
            String description) {
        this.id = id;
        this.name = name;
        this.group_picture = group_picture;
        this.description = description;
    }
    @Generated(hash = 1924225087)
    public GroupItems() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGroup_picture() {
        return this.group_picture;
    }
    public void setGroup_picture(String group_picture) {
        this.group_picture = group_picture;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

}
