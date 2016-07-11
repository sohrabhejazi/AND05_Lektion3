package android.and05.lektion3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class KeywordActivity extends  Activity {

	private TextView tvOutput;
	//private SimpleActivity activity;
	private EditText evInput;
	private File app_File;
	// Felder und Zugriffsmethoden:
	private SQLiteDatabase registerDB;
	public SQLiteDatabase getRegisterDB()
	{ return registerDB; }
	private String sourceIdentifier = "Test";
	public String getSourceIdentifier()
	{ return sourceIdentifier; }
	private RegisterHelper helper;
	public static final String SOURCE_IDENTIFIER =
			"android.and05.lektion3.SourceId";
	//private final int REQUEST_CODE = this.hashCode();
	private final int REQUEST_CODE = 0;
	
	
	private void readLastIdentifier() {
		String selectSQL = "SELECT  kurzbezeichnung "
				+ " FROM LastId";
				if(helper == null)
						helper = new RegisterHelper(this);
						registerDB = helper.getReadableDatabase();
				try {
						Cursor cursor = registerDB.rawQuery(selectSQL, null);
						if(cursor.getCount() <1 )
							return;
					    cursor.moveToLast();
						String temp = cursor.getString(0);
						sourceIdentifier = temp;
						Log.d("TAG", "Last kurzbezeichnung ist: " + temp);
						cursor.close();
			        }
				catch(Exception e) {
						Log.e("TAG", "Fehler beim Lesen der Last Kurzbezeichner. "
						+ e.toString());
				} finally {
				        registerDB.close();
				}
		}
		
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		registerDB.close();
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_keyword);
			this.readLastIdentifier();
			// Verkopplung mit RegisterHelper:
			if(helper == null)
			helper = new RegisterHelper(this);
			openDB("onCreate");
			registerDB = helper.getWritableDatabase();
			//helper.onCreate(registerDB);
			/*tvOutput = (TextView) this.findViewById 
			(R.id.tv_output);
			evInput = (EditText) this.findViewById 
			(R.id.et_input); */
			Button btSave = (Button) this.findViewById 
			(R.id.bt_save);
			// neue Instanzvariable:

			// Änderung/Ergänzung in "onCreate":
			tvOutput = (TextView) this.findViewById(R.id.tv_source);
			// erste Variante:
			//String helloText = this.read(helloFile);
			// Variante mit eigenem Ordner
			      TextView tvSource = (TextView) this.findViewById
					(R.id.tv_source);
					tvSource.setText(this.getResources().getString
					(R.string.hello) + "\"" + sourceIdentifier +
					"\"");
					final EditText tvKeyword = (EditText) this.findViewById
					(R.id.et_keyword);
					final EditText tvLocality = (EditText) this.findViewById
					(R.id.et_locality);
					final EditText tvText = (EditText) this.findViewById(R.id.et_text);
			    btSave.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					        KeywordActivity.this.insert(
							tvKeyword.getText().toString(),
							tvLocality.getText().toString(),
							tvText.getText().toString());
				}
				});
			    Button btSource = (Button) this.findViewById 
						(R.id.bt_source);
						
			    
			    btSource.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View view) {
								//tvOutput.setText(evInput.getText());
								
								tvOutput.setText("aktuelle Quelle: "+sourceIdentifier);
								callSourceActivity(sourceIdentifier);
								// Erste Variante
								//DataActivity.this.save(helloFile, evText);
								// Variante mit eigenem Ordner:
								
							}
							});
			    
			    
	} // end on create
	    public void callSourceActivity(String sourceIdentifier) {
		Intent intent = new Intent(this, SourceActivity.class);
		intent.putExtra(SOURCE_IDENTIFIER, sourceIdentifier);
		this.startActivityForResult(intent, REQUEST_CODE);
		}
		// Callback-Methode
		@Override
		protected void onActivityResult(int requestCode,
		int resultCode, Intent returnIntent) {
		super.onActivityResult(requestCode,
		resultCode, returnIntent);
		Log.d("TAG", "requestCode=" + REQUEST_CODE
		+ ", resultCode=" + resultCode);
		Bundle extrasBundle = returnIntent.getExtras();
		if(extrasBundle != null) {
		String temp = extrasBundle.getString(SOURCE_IDENTIFIER);
		if(temp != null) {
		this.sourceIdentifier = temp;
		tvOutput.setText("geänderte Quelle: \""
		+ sourceIdentifier + "\"");
		} else
			tvOutput.setText("keine Quelle zurückgeliefert");
		} else
		Log.d("TAG", "Keine Extras im return-Intent");
		}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		openDB("onstart");
	}
		private void openDB(String callingMethod) {
			if(helper == null)
			helper = new RegisterHelper(this);
			if(registerDB == null || !registerDB.isOpen()) {
			registerDB = helper.getWritableDatabase();
			Log.d("TAG", "Datenbank in \"" + callingMethod
			+ "\" geoeffnet.");
			}
			}
	public boolean insert(String keyword,
			String reference,
			String text) {
			String insertSQL = "INSERT INTO Stichworte VALUES("
			+ "null, '" + keyword+ "', '" + sourceIdentifier
			+ "', '" + reference + "', '" + text + "')";
			try {
			registerDB.execSQL(insertSQL);
			return true;
			} catch (Exception e) {
			Log.e("TAG", e.toString());
			return false;
			}
			}
			public boolean delete(String whereCondition) {
			String deleteSQL;
			if (whereCondition == null)
			deleteSQL = "DELETE FROM Stichworte";
			// Vorsicht! Löscht alles!
			else
			deleteSQL = "DELETE FROM Stichworte WHERE "
			+ whereCondition;
			try {
			registerDB.execSQL(deleteSQL);
			return true;
			} catch (Exception e) {
			Log.e("TAG", e.toString());
			return false;
			}
			}
	

}
