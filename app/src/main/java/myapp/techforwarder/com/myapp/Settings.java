package myapp.techforwarder.com.myapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    public static final String mypreference = "mypref";
    public static final String url = "url";
    public static final String alertKm = "alertKm";
    SharedPreferences sharedpreferences;
    EditText urlTextView;
    EditText alertKmtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        urlTextView = (EditText) findViewById(R.id.editText);
        alertKmtextView = (EditText) findViewById(R.id.editText2);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
            urlTextView.setText(sharedpreferences.getString(url, "http://tracker-test.96.lt/trackers/tracker.php"));
            alertKmtextView.setText(sharedpreferences.getString(alertKm, "60"));


    }

    public void save(View view) {
        String urlText = urlTextView.getText().toString();
        String speedText = alertKmtextView.getText().toString();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(url, urlText);
        editor.putString(alertKm, speedText);
        editor.commit();
        finish();
    }
}
