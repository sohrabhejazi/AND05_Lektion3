package android.and05.lektion3;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class RegisterHelper extends SQLiteOpenHelper {
	private final String CREATE_QUELLEN = 
			"CREATE TABLE Quellen( kurzbezeichnung TEXT PRIMARY KEY,"
			+ "autoren TEXT NOT NULL, titel TEXT NOT NULL, "
			+ "verlag_ort_url TEXT NOT NULL,publikationsdatum TEXT);";
	private final String CREATE_STICHWORTE =
			"CREATE TABLE Stichworte(id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "stichwort TEXT NOT NULL,quelle TEXT NOT NULL , fundstelle TEXT NOT NULL,"
			+ "text TEXT NOT NULL,"
			+ "CONSTRAINT QuellFK FOREIGN KEY(quelle) REFERENCES Quellen(kurzbezeichnung) ON DELETE RESTRICT ON UPDATE CASCADE);";
    private final String CREATE_LastId = 
					"CREATE TABLE LastId( kurzbezeichnung TEXT NOT NULL);";
	private static final String DB_NAME = "Register";
	private static final int DB_VERSION = 1;
	private static final String TAG =
	RegisterHelper.class.getSimpleName();
	//this.onCreate(DB_NAME);
	public RegisterHelper(Context context) {
	super(context, DB_NAME, null, DB_VERSION);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try{
		db.execSQL(CREATE_QUELLEN);
		db.execSQL(CREATE_STICHWORTE);
		db.execSQL(CREATE_LastId);
		Log.d("TAG", "DB erzeugt in: \"" + db.getPath()
		+ "\"");
		}
		catch(SQLException sqle) {
			// android.database.SQLException importieren!
			Log.e(TAG, "onCreate: " + sqle.toString());
			}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
