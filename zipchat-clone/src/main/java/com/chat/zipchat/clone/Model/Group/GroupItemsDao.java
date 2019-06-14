package com.chat.zipchat.clone.Model.Group;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.chat.zipchat.clone.Model.DaoSession;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "GROUP_ITEMS".
*/
public class GroupItemsDao extends AbstractDao<GroupItems, Void> {

    public static final String TABLENAME = "GROUP_ITEMS";

    /**
     * Properties of entity GroupItems.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", false, "ID");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Group_picture = new Property(2, String.class, "group_picture", false, "GROUP_PICTURE");
        public final static Property Description = new Property(3, String.class, "description", false, "DESCRIPTION");
    }


    public GroupItemsDao(DaoConfig config) {
        super(config);
    }
    
    public GroupItemsDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"GROUP_ITEMS\" (" + //
                "\"ID\" TEXT," + // 0: id
                "\"NAME\" TEXT," + // 1: name
                "\"GROUP_PICTURE\" TEXT," + // 2: group_picture
                "\"DESCRIPTION\" TEXT);"); // 3: description
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_GROUP_ITEMS_ID ON \"GROUP_ITEMS\"" +
                " (\"ID\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"GROUP_ITEMS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, GroupItems entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String group_picture = entity.getGroup_picture();
        if (group_picture != null) {
            stmt.bindString(3, group_picture);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(4, description);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, GroupItems entity) {
        stmt.clearBindings();
 
        String id = entity.getId();
        if (id != null) {
            stmt.bindString(1, id);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(2, name);
        }
 
        String group_picture = entity.getGroup_picture();
        if (group_picture != null) {
            stmt.bindString(3, group_picture);
        }
 
        String description = entity.getDescription();
        if (description != null) {
            stmt.bindString(4, description);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public GroupItems readEntity(Cursor cursor, int offset) {
        GroupItems entity = new GroupItems( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // group_picture
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // description
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, GroupItems entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setGroup_picture(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDescription(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(GroupItems entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(GroupItems entity) {
        return null;
    }

    @Override
    public boolean hasKey(GroupItems entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
