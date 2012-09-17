/*
 * Copyright (C) 2012 Saurabh Minni <http://100rabh.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bangalore.barcamp.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.bangalore.barcamp.data.BCBUpdatesMessage;

public class MessagesDataSource {
	// Database fields
	private SQLiteDatabase database;
	private BCBSQLiteHelper dbHelper;
	private String[] allColumns = { BCBSQLiteHelper.COLUMN_ID,
			BCBSQLiteHelper.COLUMN_MESSAGE, BCBSQLiteHelper.COLUMN_TIMESTAMP };

	public MessagesDataSource(Context context) {
		dbHelper = new BCBSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public BCBUpdatesMessage createMessage(String message, String timestamp) {
		ContentValues values = new ContentValues();
		values.put(BCBSQLiteHelper.COLUMN_MESSAGE, message);
		values.put(BCBSQLiteHelper.COLUMN_TIMESTAMP, timestamp);
		long insertId = database.insert(BCBSQLiteHelper.TABLE_MESSAGES, null,
				values);
		Cursor cursor = database.query(BCBSQLiteHelper.TABLE_MESSAGES,
				allColumns, BCBSQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		BCBUpdatesMessage newMessage = cursorToMessage(cursor);
		cursor.close();
		return newMessage;
	}

	public void deleteComment(BCBUpdatesMessage message) {
		long id = message.getId();
		System.out.println("Comment deleted with id: " + id);
		database.delete(BCBSQLiteHelper.TABLE_MESSAGES,
				BCBSQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<BCBUpdatesMessage> getAllMessages() {
		List<BCBUpdatesMessage> comments = new ArrayList<BCBUpdatesMessage>();

		Cursor cursor = database.query(BCBSQLiteHelper.TABLE_MESSAGES,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BCBUpdatesMessage comment = cursorToMessage(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	private BCBUpdatesMessage cursorToMessage(Cursor cursor) {
		BCBUpdatesMessage message = new BCBUpdatesMessage();
		message.setId(cursor.getLong(0));
		message.setMessage(cursor.getString(1));
		message.setTimestamp(cursor.getString(2));
		return message;
	}
}
