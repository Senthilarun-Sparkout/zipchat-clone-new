package com.chat.zipchat.clone.Model.Contact;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import com.chat.zipchat.clone.Model.DaoSession;

@Entity
public class ContactResponse {
    private static final long serialVersionUID = 1L;

    @Id
    public Long contact_id;

    private boolean status;

    @ToMany(referencedJoinProperty = "contact_id")
    List<ResultItem> result;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1331790420)
    private transient ContactResponseDao myDao;

    @Generated(hash = 1899397185)
    public ContactResponse(Long contact_id, boolean status) {
        this.contact_id = contact_id;
        this.status = status;
    }

    @Generated(hash = 1086569173)
    public ContactResponse() {
    }

    public Long getContact_id() {
        return this.contact_id;
    }

    public void setContact_id(Long contact_id) {
        this.contact_id = contact_id;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 928391329)
    public List<ResultItem> getResult() {
        if (result == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ResultItemDao targetDao = daoSession.getResultItemDao();
            List<ResultItem> resultNew = targetDao
                    ._queryContactResponse_Result(contact_id);
            synchronized (this) {
                if (result == null) {
                    result = resultNew;
                }
            }
        }
        return result;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 750945346)
    public synchronized void resetResult() {
        result = null;
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
    @Generated(hash = 1163401516)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getContactResponseDao() : null;
    }

}