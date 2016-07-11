package android.and05.lektion3;

import java.text.RuleBasedCollator;

//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;

public class SourceActivity extends Activity {
	
	private RegisterHelper helper;
	private SQLiteDatabase registerDB;
	private String returnSourceIdentifier = "testReturnValue";
	private String AU,TT,TP,TD ;
	private String[] sourceIdentifiers =
			new String[] {"JAV02", "JAV01", "AND05",
			"AND01", "AND04", "AND03", "AND02" };
			// Sortierregel:
	private String sortRuleGerman =
			"< a,A< b,B< c,C< d,D< e,E< f,F< g,G< h,H< i,I"
			+ "< j,J< k,K< l,L< m,M< n,N< o,O< p,P< q,Q< r,R"
			+ "< s,S< t,T< u,U< v,V< w,W< x,X< y,Y< z,Z";
	private AutoCompleteTextView autocompleteTvIdentifiers;
	private ArrayAdapter<String> adapter;
	private static final String TAG =
			SourceActivity.class.getSimpleName();
	
	
	public void saveLastIdentifier() {
			String insertSQL = "INSERT into LastId VALUES('"
			+ returnSourceIdentifier + "');";
			if(helper == null)
			helper = new RegisterHelper(this);
			registerDB = helper.getWritableDatabase();
			try {
					registerDB.execSQL(insertSQL);
					Log.d(TAG, "Last Kurzbezeichnung  " + returnSourceIdentifier
					+ "\" in DB gespeichert");
			} catch(Exception e) {
					Log.e(TAG , "Fehler beim Speichern einer "
					+ "Last Kurzbezeichnung " + e.toString());
			} finally {
			registerDB.close();
			}
		} // end of saveLastIdentifier
	
	public boolean delete(String Kennziffer) {
		String deleteSQL;
		if(helper == null)
			helper = new RegisterHelper(this);
			registerDB = helper.getWritableDatabase();
		deleteSQL = "DELETE FROM Quellen WHERE kurzbezeichnung='"
		+ Kennziffer+"'";
		try {
		registerDB.execSQL(deleteSQL);
		return true;
		} catch (Exception e) {
		Log.e(TAG, e.toString());
		return false;
		}
		}
	public boolean insert(String identifier, String
			authors,String titel, String publisher,
			String text) {
			String insertSQL = "INSERT into Quellen VALUES('"
			+ identifier + "', '" + authors + "', '"
			+ titel + "', '" + publisher + "', '"
			+ text + "');";
			// Datenbank im Schreibmodus beschaffen ...
			if(helper == null)
			helper = new RegisterHelper(this);
			registerDB = helper.getWritableDatabase();
			try {
			registerDB.execSQL(insertSQL);
			adapter.add(returnSourceIdentifier);
			this.sortArrayAdapter();
			Log.d("TAG insert", "Quelle zu \"" + returnSourceIdentifier
			+ "\" in DB gespeichert");
			return true;
			} catch(Exception e) {
			Log.e("TAG insert", "Fehler beim Speichern einer "
			+ "Literaturquelle. " + e.toString());
			return false;
			} finally {
			// ... und wieder schließen:
			registerDB.close();
			return true;
			}
			 
		} // end of insert
	
	private void initializeAdapterFromDB() {
		String selectSQL = "SELECT kurzbezeichnung "
		+ " FROM Quellen ORDER BY kurzbezeichnung";
		// Datenbank öffnen:
		if(helper == null)
			helper = new RegisterHelper(this);
		registerDB = helper.getReadableDatabase();
		try {
		Cursor cursor = registerDB.rawQuery(selectSQL, null);
		// die neue Liste aus der Datenbank:
		String[] identifiers = new String[cursor.getCount()];
		int index = 0;
		while(cursor.moveToNext()){
		String temp = cursor.getString(0);
		identifiers[index] = temp;
		Log.d("TAG", "Stichwort " + index + ": \""
		+ temp + "\"");
		index++;
		}
		cursor.close();
		// Adapter aus DB initialisieren:
		adapter = new ArrayAdapter<String>(
		this, R.layout.list_item, identifiers);
		}
		catch(Exception e) {
		Log.e("TAG", "Fehler beim Lesen der Kurzbezeichner. "
		+ e.toString());
		} finally {
		// Datenbank wieder schließen:
		registerDB.close();
		}
		} // end of initialize
	
	private void callback() {
		Intent pushIntent = this.getIntent();
		Bundle intentBundle = pushIntent.getExtras();
		if (intentBundle == null)
		Log.d("TAG", "keine Extras im Push-Intent");
		else {
		String intentParam = intentBundle.getString(
		KeywordActivity.SOURCE_IDENTIFIER);
		if(intentParam == null)
		Log.d("TAG", "No key=\""
		+ KeywordActivity.SOURCE_IDENTIFIER
		+ "\" in Intent-Bundle");
		else
		Log.d("TAG", "key=\""
		+ KeywordActivity.SOURCE_IDENTIFIER
		+ "\" / value=\"" + intentParam + "\" received.");
		}
		pushIntent.putExtra(KeywordActivity.SOURCE_IDENTIFIER,
		returnSourceIdentifier);
		
		this.setResult(Activity.RESULT_OK, pushIntent);
		//this.setResult(Activity.RESULT_CANCELED , pushIntent);
		Log.d("TAG", "SourceIdentifier \""
		+ returnSourceIdentifier + "\" returned.");
		}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_source);
		Button btBack = (Button) this.findViewById 
				(R.id.back_bt);
		
		autocompleteTvIdentifiers = (AutoCompleteTextView)
				this.findViewById(R.id.autocomplete_identifier);
		final EditText tvAuthors = (EditText) this.findViewById 
				(R.id.et_authors);
		
		final EditText tvTitel = (EditText) this.findViewById 
				(R.id.et_titel);
	
		final EditText tvPublisher = (EditText) this.findViewById 
				(R.id.et_publisher);
		
		final EditText tvDate = (EditText) this.findViewById 
				(R.id.et_date);
		
        
		    this.initializeAdapterFromDB();
				autocompleteTvIdentifiers.setAdapter(adapter);
				
		    btBack.setOnClickListener(new OnClickListener() {
		    	
				@Override
				public void onClick(View view) {
					
				//tvOutput.setText("aktuelle Quelle: "+sourceIdentifier);
				// identifier auch für callback speichern!
				returnSourceIdentifier =
				autocompleteTvIdentifiers.getText().toString();
				// insert ausführen
				AU=tvAuthors.getText().toString();
				TT=tvTitel.getText().toString();
				TP=tvPublisher.getText().toString();
				TD=tvDate.getText().toString();
				SourceActivity.this.insert(
											returnSourceIdentifier,
											AU,
											TT,
											TP,
											TD
									      );
				
				SourceActivity.this.callback();
				SourceActivity.this.finish();
				}
				});
	 }

	private void sortArrayAdapter() throws java.text.ParseException {
		
		adapter.sort(new RuleBasedCollator(sortRuleGerman));
		
		}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		this.saveLastIdentifier();
		registerDB.close();
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.source, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_source,
					container, false);
			return rootView;
		}
	}

}
