package net.homeip.dometec.minutiinfostrada;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private static final DateFormat sdf = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT, Locale.ITALIAN);
	private static final String LOG = "net.homeip.dometec.minutiinfostrada";

	@Override
	public void onStart(Intent intent, int startId) {

		Log.i(LOG, "Called");

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		ComponentName thisWidget = new ComponentName(getApplicationContext(), MyWidgetProvider.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

		Log.i(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
		Log.i(LOG, "Direct" + String.valueOf(allWidgetIds2.length));

		for (int widgetId : allWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);

			SharedPreferences prefs = getSharedPreferences(MainActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
			String user = prefs.getString(MainActivity.TEXT_DATA_KEY_USER, "");
			String pass = prefs.getString(MainActivity.TEXT_DATA_KEY_PASS, "");
			String urlInfo = "https://95.110.169.43/backend/infostrada/info?username=" + user + "&password=" + pass;

			HttpGet request = new HttpGet(urlInfo);
			String out = "";

			try {

				HttpResponse response = HttpManager.execute(request);
				InputStream inputStream = response.getEntity().getContent();
				out = convertStreamToString(inputStream).replace(" minuti ", ":").replace(" secondi", "");
				out += " (Upd. " + sdf.format(new Date()) + ")";

			} catch (IOException e) {
				out = "Errore di connessione: " + e.getMessage();
				e.printStackTrace();
			}

			// Set the text
			remoteViews.setTextViewText(R.id.update, out);

			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), MyWidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);

		}

		stopSelf();

		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {

			InputStreamReader in = new InputStreamReader(is);

			StringBuilder sb = null;

			try {
				sb = convertReaderToString(in);
			} finally {
				is.close();
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static StringBuilder convertReaderToString(Reader in) throws IOException {

		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(in);
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			if (!reader.ready())
				break;
		}

		return sb;

	}
}