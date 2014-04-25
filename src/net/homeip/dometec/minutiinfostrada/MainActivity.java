package net.homeip.dometec.minutiinfostrada;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

	public final static String MY_PREFERENCES = "InfostradaAccessStore";
	public final static String TEXT_DATA_KEY_USER = "user";
	public final static String TEXT_DATA_KEY_PASS = "pass";

	private TextView username;
	private TextView password;
	private Button okButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		username = (TextView) findViewById(R.id.userText);
		password = (TextView) findViewById(R.id.passwordText);
		okButton = (Button) findViewById(R.id.okButton);

		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		username.setText(prefs.getString(TEXT_DATA_KEY_USER, ""));
		password.setText(prefs.getString(TEXT_DATA_KEY_PASS, ""));

		okButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		SharedPreferences prefs = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TEXT_DATA_KEY_USER, username.getText().toString());
		editor.putString(TEXT_DATA_KEY_PASS, password.getText().toString());
		editor.commit();

		finish();
	}

}
