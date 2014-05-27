package br.com.jarvis;

import java.util.HashMap;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import br.com.jarvis.service.JarvisService;

public class Jarvis implements TextToSpeech.OnInitListener{

	//Constants
	public static final String JARVIS = "JARVIS";
	public boolean JARVIS_INITIATED = false;

	private static TextToSpeech jarvis;
	private SharedPreferences sharedPreferences = null;

	private Context mContext;
	public Jarvis(Context context) {
		mContext = context;
		
		jarvis = new TextToSpeech(context, this);
		
		String sharedPreferencesFileName = context.getString(R.string.sharedPrefFileName);
		sharedPreferences = context.getApplicationContext().getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
	}

	@Override
	public void onInit(int status) {
		Log.i(JARVIS, "Initiating JARVIS...");

		if (status == TextToSpeech.SUCCESS)	{
			JARVIS_INITIATED = true;

			setLanguage(getSelectedLocale());
		} else {
			Log.e(JARVIS, "Jarvis não iniciado!");

			JARVIS_INITIATED = false;
		}
	}

	public void askToSpeak(String text) {

		Log.i(JARVIS, "Asked to speak");

		if(JARVIS_INITIATED) {
			speak(text);
		} else {
			try {
				Thread.sleep(500);
				speak(text);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void speak(String text) {
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_SYSTEM));
		
		int result = jarvis.speak(text, TextToSpeech.QUEUE_FLUSH, params);
		
		Log.e(JARVIS, "Speak Result" + result);
		
		if(result == TextToSpeech.SUCCESS) {
			Log.i(JARVIS, "Speaking...");
			while(jarvis.isSpeaking());
		} else {
			Log.e(JARVIS, "ERROR: Not Speaking...");
		}
	}

	public int setLanguage(Locale locale) {
		return jarvis.setLanguage(locale);
	}

	public Locale getLocaleByPosition(int position){

		Locale[] locales = Locale.getAvailableLocales();

		return locales[position];
	}

	public Locale getSelectedLocale() {

		String localePositionPreferenceName = mContext.getString(R.string.sharedPrefLocalePosition);
		int localePosition = sharedPreferences.getInt(localePositionPreferenceName, -1);
		Locale selectedLocale = null;

		if(localePosition >=  0) { 
			Locale[] locales = Locale.getAvailableLocales();
			selectedLocale = locales[localePosition];
		} else { 
			selectedLocale = Locale.getDefault();
		}
		return selectedLocale;
	}
	
	public int getLocationPosition(Locale locale) { 
		Locale[] locales = Locale.getAvailableLocales();
		
		for (int i = 0; i < locales.length; i++) {
			if(locales[i].equals(locale)) {
				return i;
			}
		}
		
		return 0;
	}
	
	public static void startJarvisIntent(Context context, String text) {
		Intent jarvisServiceIntent = new Intent(context, JarvisService.class);
		jarvisServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		jarvisServiceIntent.putExtra("text", text);

		context.startService(jarvisServiceIntent);
	}

	public void stop() {
		jarvis.stop();
	}

	public void shutdown() {
		jarvis.shutdown();
	}

}
