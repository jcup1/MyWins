package com.theandroiddev.mywins.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.theandroiddev.mywins.UI.Activities.MainActivity;
import com.theandroiddev.mywins.UI.Helpers.Constants;

/**
 * Created by jakub on 14.08.17.
 */

public class DBHelper extends SQLiteOpenHelper {

    DBHelper(Context context) {
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Constants.CREATE_TB_SUCCESSES);
        sqLiteDatabase.execSQL(Constants.CREATE_TB_IMAGES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL(Constants.DROP_TB_SUCCESSES);
        sqLiteDatabase.execSQL(Constants.DROP_TB_IMAGES);
        onCreate(sqLiteDatabase);

        MainActivity.dbUpdate = true;

    }


}
