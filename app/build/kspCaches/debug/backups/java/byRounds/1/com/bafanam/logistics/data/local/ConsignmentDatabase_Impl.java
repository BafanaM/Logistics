package com.bafanam.logistics.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.bafanam.logistics.data.local.dao.LocalConsignmentDao;
import com.bafanam.logistics.data.local.dao.LocalConsignmentDao_Impl;
import com.bafanam.logistics.data.local.dao.SyncQueueDao;
import com.bafanam.logistics.data.local.dao.SyncQueueDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ConsignmentDatabase_Impl extends ConsignmentDatabase {
  private volatile LocalConsignmentDao _localConsignmentDao;

  private volatile SyncQueueDao _syncQueueDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `local_consignment` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `businessKey` TEXT NOT NULL, `rawPayload` TEXT NOT NULL, `validationState` TEXT NOT NULL, `status` TEXT NOT NULL, `failureReason` TEXT, `retryCount` INTEGER NOT NULL, `createdAtEpochMs` INTEGER NOT NULL, `inboundLastUpdated` TEXT)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_local_consignment_businessKey` ON `local_consignment` (`businessKey`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `sync_queue` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recordId` INTEGER NOT NULL, `queuedAtEpochMs` INTEGER NOT NULL, `attemptCount` INTEGER NOT NULL, `lastAttemptAtEpochMs` INTEGER, `outcome` TEXT NOT NULL, FOREIGN KEY(`recordId`) REFERENCES `local_consignment`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_sync_queue_recordId` ON `sync_queue` (`recordId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '895334bfbf616f850f9f302ceeaad158')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `local_consignment`");
        db.execSQL("DROP TABLE IF EXISTS `sync_queue`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsLocalConsignment = new HashMap<String, TableInfo.Column>(9);
        _columnsLocalConsignment.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("businessKey", new TableInfo.Column("businessKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("rawPayload", new TableInfo.Column("rawPayload", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("validationState", new TableInfo.Column("validationState", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("failureReason", new TableInfo.Column("failureReason", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("retryCount", new TableInfo.Column("retryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("createdAtEpochMs", new TableInfo.Column("createdAtEpochMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocalConsignment.put("inboundLastUpdated", new TableInfo.Column("inboundLastUpdated", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocalConsignment = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLocalConsignment = new HashSet<TableInfo.Index>(1);
        _indicesLocalConsignment.add(new TableInfo.Index("index_local_consignment_businessKey", true, Arrays.asList("businessKey"), Arrays.asList("ASC")));
        final TableInfo _infoLocalConsignment = new TableInfo("local_consignment", _columnsLocalConsignment, _foreignKeysLocalConsignment, _indicesLocalConsignment);
        final TableInfo _existingLocalConsignment = TableInfo.read(db, "local_consignment");
        if (!_infoLocalConsignment.equals(_existingLocalConsignment)) {
          return new RoomOpenHelper.ValidationResult(false, "local_consignment(com.bafanam.logistics.data.local.entity.LocalConsignmentEntity).\n"
                  + " Expected:\n" + _infoLocalConsignment + "\n"
                  + " Found:\n" + _existingLocalConsignment);
        }
        final HashMap<String, TableInfo.Column> _columnsSyncQueue = new HashMap<String, TableInfo.Column>(6);
        _columnsSyncQueue.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("recordId", new TableInfo.Column("recordId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("queuedAtEpochMs", new TableInfo.Column("queuedAtEpochMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("attemptCount", new TableInfo.Column("attemptCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("lastAttemptAtEpochMs", new TableInfo.Column("lastAttemptAtEpochMs", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSyncQueue.put("outcome", new TableInfo.Column("outcome", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSyncQueue = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSyncQueue.add(new TableInfo.ForeignKey("local_consignment", "CASCADE", "NO ACTION", Arrays.asList("recordId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSyncQueue = new HashSet<TableInfo.Index>(1);
        _indicesSyncQueue.add(new TableInfo.Index("index_sync_queue_recordId", true, Arrays.asList("recordId"), Arrays.asList("ASC")));
        final TableInfo _infoSyncQueue = new TableInfo("sync_queue", _columnsSyncQueue, _foreignKeysSyncQueue, _indicesSyncQueue);
        final TableInfo _existingSyncQueue = TableInfo.read(db, "sync_queue");
        if (!_infoSyncQueue.equals(_existingSyncQueue)) {
          return new RoomOpenHelper.ValidationResult(false, "sync_queue(com.bafanam.logistics.data.local.entity.SyncQueueItemEntity).\n"
                  + " Expected:\n" + _infoSyncQueue + "\n"
                  + " Found:\n" + _existingSyncQueue);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "895334bfbf616f850f9f302ceeaad158", "f7a639067e8db13f516e82ad880ecb6e");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "local_consignment","sync_queue");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `local_consignment`");
      _db.execSQL("DELETE FROM `sync_queue`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(LocalConsignmentDao.class, LocalConsignmentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SyncQueueDao.class, SyncQueueDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public LocalConsignmentDao localConsignmentDao() {
    if (_localConsignmentDao != null) {
      return _localConsignmentDao;
    } else {
      synchronized(this) {
        if(_localConsignmentDao == null) {
          _localConsignmentDao = new LocalConsignmentDao_Impl(this);
        }
        return _localConsignmentDao;
      }
    }
  }

  @Override
  public SyncQueueDao syncQueueDao() {
    if (_syncQueueDao != null) {
      return _syncQueueDao;
    } else {
      synchronized(this) {
        if(_syncQueueDao == null) {
          _syncQueueDao = new SyncQueueDao_Impl(this);
        }
        return _syncQueueDao;
      }
    }
  }
}
