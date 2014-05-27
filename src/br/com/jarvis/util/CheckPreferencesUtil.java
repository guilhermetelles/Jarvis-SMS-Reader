package br.com.jarvis.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import br.com.jarvis.R;

public class CheckPreferencesUtil {
	
	public static boolean shouldSpeakSMS(Context context) {
		
		String sharedPreferencesFileName = context.getString(R.string.sharedPrefFileName);

		SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);
		
		boolean shouldSpeak = onlyWorkWithHeadPhonesCheck(context, sharedPreferences);
		
		return shouldSpeak;
	}
	
	public static boolean shouldSpeakCallerName(Context context) {
		
		String sharedPreferencesFileName = context.getString(R.string.sharedPrefFileName);
		String sharedPreferencesSpeakCallerNameOption = context.getString(R.string.sharedPrefSpeakCallersOption);

		SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferencesFileName, Context.MODE_PRIVATE);

		boolean speakCallersName = sharedPreferences.getBoolean(sharedPreferencesSpeakCallerNameOption, true);
		boolean onlyWorkWithHeadPhones = onlyWorkWithHeadPhonesCheck(context, sharedPreferences);
		
		boolean shouldSpeak = speakCallersName && onlyWorkWithHeadPhones;
		
		return shouldSpeak;
	}
	
	@SuppressWarnings("deprecation")
	private static boolean onlyWorkWithHeadPhonesCheck(Context context, SharedPreferences sharedPreferences) {

		String sharedPreferencesHeadOptionOption = context.getString(R.string.sharedPrefHeadPhoneOption);
		
		boolean onlyWorkWithHeadPhones = sharedPreferences.getBoolean(sharedPreferencesHeadOptionOption, true);
		boolean hasHeadPhone = true;

		if(onlyWorkWithHeadPhones) {
			AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			hasHeadPhone = (audioManager.isWiredHeadsetOn() || audioManager.isBluetoothA2dpOn());
		}
		
		// It should speak if it doesn't need headphones or if it does and has a headphone
		boolean result = !onlyWorkWithHeadPhones || (onlyWorkWithHeadPhones && hasHeadPhone);
		
		return result;
	}

}
