package br.com.jarvis.service;

import java.util.Locale;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import br.com.jarvis.Jarvis;

public class JarvisService extends IntentService {

	private Jarvis jarvis = null;

	public JarvisService() {
		super("JarvisService");
	}

	@Override
	public void onCreate() {

		jarvis = new Jarvis(JarvisService.this);

		// workaround for http://code.google.com/p/android/issues/detail?id=20915
		try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
		}

		super.onCreate();
	}

	@Override
	protected void onHandleIntent(final Intent intent) {

		SharedPreferences sharedPreferences = getSharedPreferences("JarvisSharedPreferences", Context.MODE_PRIVATE);
		int localePosition = sharedPreferences.getInt("localePosition", 0);
		Locale selectedLocale = jarvis.getLocaleByPosition(localePosition);
		jarvis.setLanguage(selectedLocale);

		String text = intent.getExtras().getString("text");

		jarvis.askToSpeak(text);
	}

	@Override
	public void onDestroy() {
		if(jarvis != null) {
			jarvis.stop();
			jarvis.shutdown();

			Log.i("DESTROYED", "Jarvis Destroyed!");
		}
		super.onDestroy();
	}


}
