package com.chat.zipchat.clone.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

@Entity
public class Groups {

    @Id
    public Long grp_id;

    @Index(unique = true)
    private String group_id;

    @ToMany(referencedJoinProperty = "grp_id")
    private List<GroupMember> groupMember;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 591463884)
    private transient GroupsDao myDao;

    @Generated(hash = 1689764725)
    public Groups(Long grp_id, String group_id) {
        this.grp_id = grp_id;
        this.group_id = group_id;
    }

    @Generated(hash = 893039872)
    public Groups() {
    }

    public Long getGrp_id() {
        return this.grp_id;
    }

    public void setGrp_id(Long grp_id) {
        this.grp_id = grp_id;
    }

    public String getGroup_id() {
        return this.group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1960392348)
    public List<GroupMember> getGroupMember() {
        if (groupMember == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            GroupMemberDao targetDao = daoSession.getGroupMemberDao();
            List<GroupMember> groupMemberNew = targetDao
                    ._queryGroups_GroupMember(grp_id);
            synchronized (this) {
                if (groupMember == null) {
                    groupMember = groupMemberNew;
                }
            }
        }
        return groupMember;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 845478494)
    public synchronized void resetGroupMember() {
        groupMember = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1196912363)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getGroupsDao() : null;
    }

}
