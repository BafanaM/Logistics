package com.bafanam.logistics.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.bafanam.logistics.data.local.ConsignmentTypeConverters;
import com.bafanam.logistics.data.local.entity.SyncQueueItemEntity;
import com.bafanam.logistics.domain.model.SyncQueueOutcome;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SyncQueueDao_Impl implements SyncQueueDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SyncQueueItemEntity> __insertionAdapterOfSyncQueueItemEntity;

  private final ConsignmentTypeConverters __consignmentTypeConverters = new ConsignmentTypeConverters();

  private final EntityDeletionOrUpdateAdapter<SyncQueueItemEntity> __updateAdapterOfSyncQueueItemEntity;

  public SyncQueueDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSyncQueueItemEntity = new EntityInsertionAdapter<SyncQueueItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sync_queue` (`id`,`recordId`,`queuedAtEpochMs`,`attemptCount`,`lastAttemptAtEpochMs`,`outcome`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncQueueItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRecordId());
        statement.bindLong(3, entity.getQueuedAtEpochMs());
        statement.bindLong(4, entity.getAttemptCount());
        if (entity.getLastAttemptAtEpochMs() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getLastAttemptAtEpochMs());
        }
        final String _tmp = __consignmentTypeConverters.syncOutcomeToString(entity.getOutcome());
        statement.bindString(6, _tmp);
      }
    };
    this.__updateAdapterOfSyncQueueItemEntity = new EntityDeletionOrUpdateAdapter<SyncQueueItemEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sync_queue` SET `id` = ?,`recordId` = ?,`queuedAtEpochMs` = ?,`attemptCount` = ?,`lastAttemptAtEpochMs` = ?,`outcome` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SyncQueueItemEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRecordId());
        statement.bindLong(3, entity.getQueuedAtEpochMs());
        statement.bindLong(4, entity.getAttemptCount());
        if (entity.getLastAttemptAtEpochMs() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getLastAttemptAtEpochMs());
        }
        final String _tmp = __consignmentTypeConverters.syncOutcomeToString(entity.getOutcome());
        statement.bindString(6, _tmp);
        statement.bindLong(7, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final SyncQueueItemEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfSyncQueueItemEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final SyncQueueItemEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfSyncQueueItemEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getByRecordId(final long recordId,
      final Continuation<? super SyncQueueItemEntity> $completion) {
    final String _sql = "SELECT * FROM sync_queue WHERE recordId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, recordId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SyncQueueItemEntity>() {
      @Override
      @Nullable
      public SyncQueueItemEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecordId = CursorUtil.getColumnIndexOrThrow(_cursor, "recordId");
          final int _cursorIndexOfQueuedAtEpochMs = CursorUtil.getColumnIndexOrThrow(_cursor, "queuedAtEpochMs");
          final int _cursorIndexOfAttemptCount = CursorUtil.getColumnIndexOrThrow(_cursor, "attemptCount");
          final int _cursorIndexOfLastAttemptAtEpochMs = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAttemptAtEpochMs");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final SyncQueueItemEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRecordId;
            _tmpRecordId = _cursor.getLong(_cursorIndexOfRecordId);
            final long _tmpQueuedAtEpochMs;
            _tmpQueuedAtEpochMs = _cursor.getLong(_cursorIndexOfQueuedAtEpochMs);
            final int _tmpAttemptCount;
            _tmpAttemptCount = _cursor.getInt(_cursorIndexOfAttemptCount);
            final Long _tmpLastAttemptAtEpochMs;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAtEpochMs)) {
              _tmpLastAttemptAtEpochMs = null;
            } else {
              _tmpLastAttemptAtEpochMs = _cursor.getLong(_cursorIndexOfLastAttemptAtEpochMs);
            }
            final SyncQueueOutcome _tmpOutcome;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfOutcome);
            _tmpOutcome = __consignmentTypeConverters.stringToSyncOutcome(_tmp);
            _result = new SyncQueueItemEntity(_tmpId,_tmpRecordId,_tmpQueuedAtEpochMs,_tmpAttemptCount,_tmpLastAttemptAtEpochMs,_tmpOutcome);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object listWorkableItems(final SyncQueueOutcome pending,
      final Continuation<? super List<SyncQueueItemEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT * FROM sync_queue q\n"
            + "        INNER JOIN local_consignment c ON c.id = q.recordId\n"
            + "        WHERE q.outcome = ? AND c.status = 'QUEUED'\n"
            + "        ORDER BY q.queuedAtEpochMs ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __consignmentTypeConverters.syncOutcomeToString(pending);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SyncQueueItemEntity>>() {
      @Override
      @NonNull
      public List<SyncQueueItemEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecordId = CursorUtil.getColumnIndexOrThrow(_cursor, "recordId");
          final int _cursorIndexOfQueuedAtEpochMs = CursorUtil.getColumnIndexOrThrow(_cursor, "queuedAtEpochMs");
          final int _cursorIndexOfAttemptCount = CursorUtil.getColumnIndexOrThrow(_cursor, "attemptCount");
          final int _cursorIndexOfLastAttemptAtEpochMs = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAttemptAtEpochMs");
          final int _cursorIndexOfOutcome = CursorUtil.getColumnIndexOrThrow(_cursor, "outcome");
          final List<SyncQueueItemEntity> _result = new ArrayList<SyncQueueItemEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SyncQueueItemEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRecordId;
            _tmpRecordId = _cursor.getLong(_cursorIndexOfRecordId);
            final long _tmpQueuedAtEpochMs;
            _tmpQueuedAtEpochMs = _cursor.getLong(_cursorIndexOfQueuedAtEpochMs);
            final int _tmpAttemptCount;
            _tmpAttemptCount = _cursor.getInt(_cursorIndexOfAttemptCount);
            final Long _tmpLastAttemptAtEpochMs;
            if (_cursor.isNull(_cursorIndexOfLastAttemptAtEpochMs)) {
              _tmpLastAttemptAtEpochMs = null;
            } else {
              _tmpLastAttemptAtEpochMs = _cursor.getLong(_cursorIndexOfLastAttemptAtEpochMs);
            }
            final SyncQueueOutcome _tmpOutcome;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfOutcome);
            _tmpOutcome = __consignmentTypeConverters.stringToSyncOutcome(_tmp_1);
            _item = new SyncQueueItemEntity(_tmpId,_tmpRecordId,_tmpQueuedAtEpochMs,_tmpAttemptCount,_tmpLastAttemptAtEpochMs,_tmpOutcome);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
