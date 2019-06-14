package com.chat.zipchat.clone.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity
public class FavouritePojo {

    @Id
    public Long id;

    @Index(unique = true)
    private String FavouriteId;

    @Generated(hash = 1248770794)
    public FavouritePojo(Long id, String FavouriteId) {
        this.id = id;
        this.FavouriteId = FavouriteId;
    }

    @Generated(hash = 290086727)
    public FavouritePojo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFavouriteId() {
        return this.FavouriteId;
    }

    public void setFavouriteId(String FavouriteId) {
        this.FavouriteId = FavouriteId;
    }

}
