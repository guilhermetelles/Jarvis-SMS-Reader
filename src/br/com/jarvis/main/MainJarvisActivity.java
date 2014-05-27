package br.com.jarvis.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import br.com.jarvis.Jarvis;
import br.com.jarvis.R;

public class MainJarvisActivity extends Activity {

	//Views
	private CheckBox headphoneOptionChkBox;
	private CheckBox speakCallerOptionChkBox;
	private EditText editText;
	private Button speakButton;
	private Spinner localeSpinner;

	//Locale
	private Map<String, Object> localeHash;

	//Jarvis
	Jarvis jarvis = null;

	//Preferences
	SharedPreferences sharedPreferences = null;

	AudioManager audioManager = null;
	ComponentName volumeListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main_jarvis);

		jarvis = new Jarvis(this);
		sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.sharedPrefFileName), MODE_PRIVATE);

		loadViews();
		setViewTriggers();

		//Trying to get volume buttons key events. Trying...
//		audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
//		volumeListener = new ComponentName(VolumeListener.class.getPackage().getName(), VolumeListener.class.getName());
//		audioManager.registerMediaButtonEventReceiver(volumeListener);
//		
//		registerReceiver(new VolumeListener(), new IntentFilter(Intent.ACTION_MEDIA_BUTTON));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		ArrayList<String> unavailableLanguages = data.getStringArrayListExtra(TextToSpeech.Engine.EXTRA_UNAVAILABLE_VOICES);
		for (String string : unavailableLanguages) {
			Log.i("unavailableLanguages", string);
		}

		if(requestCode == 0) {

			Log.i("RESULT", "Result: " + resultCode);

			if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				Toast.makeText(this, "Language installed", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Language not installed", Toast.LENGTH_LONG).show();
			}
		}

	}

	private void loadViews(){
		editText = (EditText) findViewById(R.id.textInput);
		speakButton = (Button) findViewById(R.id.speakButton);		
		localeSpinner = (Spinner) findViewById(R.id.localeSpinner);
		headphoneOptionChkBox = (CheckBox) findViewById(R.id.headphoneOption);
		speakCallerOptionChkBox = (CheckBox) findViewById(R.id.callerNameSpeakerOption);

		setCheckBoxesValues();
		fillSpinner();
	}

	private void setCheckBoxesValues() {
		boolean onlyWorkWithHeadPhones = sharedPreferences.getBoolean(getString(R.string.sharedPrefHeadPhoneOption), true);
		headphoneOptionChkBox.setChecked(onlyWorkWithHeadPhones);

		boolean speakCallersName = sharedPreferences.getBoolean(getString(R.string.sharedPrefSpeakCallersOption), true);
		speakCallerOptionChkBox.setChecked(speakCallersName);
	}

	/**
	 * Fill spinner content with locales
	 */
	private void fillSpinner() {

		localeHash = new HashMap<String, Object>();

		int languagePosition = 0;
		Locale selectedLocale = jarvis.getSelectedLocale();

		Locale[] locales = Locale.getAvailableLocales(); 
		List<String> localesNameList = new ArrayList<String>();  
		for (int i = 0; i < locales.length; i++) {
			Locale locale = locales[i];

			String country = locale.getDisplayName();
			localesNameList.add(country);

			localeHash.put(country, locale);

			if(locale.equals(selectedLocale)) {
				languagePosition = i;
			}
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, localesNameList);

		localeSpinner.setAdapter(adapter);

		localeSpinner.setSelection(languagePosition);

		Editor editor = sharedPreferences.edit();
		editor.putInt(getString(R.string.sharedPrefLocalePosition), languagePosition);
		editor.commit();

	}

	/**
	 * Set views actions
	 */
	private void setViewTriggers() {

		// Headphone Option on click listener
		setHeadphoneOptionTrigger();

		// Speak Caller's Name Option on click listener
		setSpeakCallerNameOptionTrigger();

		// Speak Button on click listener
		setSpeakButtonTrigger();

		// Locale Spinner on item selected listener
		setLocaleSpinnerTrigger();
	}

	/**
	 * Set Jarvis Language
	 * Checks if the language it's supported, if it's not, shows a Toast 
	 * @param locale - Locale language to be set
	 */
	private void setJarvisLanguage(Locale locale) {
		int result = jarvis.setLanguage(locale);

		Toast.makeText(this, "Result: " + result, Toast.LENGTH_SHORT).show();

		switch(result) {

		case TextToSpeech.LANG_MISSING_DATA: 
		case TextToSpeech.LANG_NOT_SUPPORTED: 
			String langNotSupported = getString(R.string.lang_not_supported);
			Toast.makeText(this, langNotSupported, Toast.LENGTH_SHORT).show();
			speakButton.setEnabled(false);

			break;
		case TextToSpeech.LANG_AVAILABLE:
			Intent intent = new Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
		    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    intent.setPackage("com.google.android.tts"); /*replace with the package name of the target TTS engine*/
		    try {
		        Toast.makeText(this, "Installing voice data: " + intent.toUri(0), Toast.LENGTH_LONG).show();
		        startActivity(intent);
		    } catch (ActivityNotFoundException ex) {
		        Log.e(Jarvis.JARVIS, "Failed to install TTS data, no acitivty found for " + intent + ")");
		    }
			
			break;
		default:
			speakButton.setEnabled(true);
			jarvis.JARVIS_INITIATED = true;

			int localePosition = jarvis.getLocationPosition(locale);

			Editor editor = sharedPreferences.edit();
			editor.putInt(getString(R.string.sharedPrefLocalePosition), localePosition);
			editor.commit();
		}

	}

	/**
	 * Ask app to speak
	 * If text to be spoken is empty, show o Toast
	 */
	private void askToSpeak() {
		String text = editText.getText().toString();

		if(text.equals("")) {
			String writeSomething = getString(R.string.edit_text_empty);
			Toast.makeText(this, writeSomething, Toast.LENGTH_SHORT).show();
		} else {
			jarvis.askToSpeak(text);
		}
	}

	private void setLocaleSpinnerTrigger() {
		localeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,int position, long id) {
				Log.i("SPINNER", "Item selected");

				String selectetCountry = parent.getItemAtPosition(position).toString();
				Locale selectedLocale = (Locale) localeHash.get(selectetCountry);

				if(jarvis.JARVIS_INITIATED) {
					setJarvisLanguage(selectedLocale);

					//					Intent intent = new Intent();
					//					intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
					//					startActivityForResult(intent, 0);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}

	private void setSpeakButtonTrigger() {
		speakButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				askToSpeak();
			}
		});
	}

	private void setSpeakCallerNameOptionTrigger() {
		speakCallerOptionChkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sharedPreferences.edit();
				editor.putBoolean(getString(R.string.sharedPrefSpeakCallersOption), isChecked);
				editor.commit();
			}
		});
	}

	private void setHeadphoneOptionTrigger() {
		headphoneOptionChkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sharedPreferences.edit();
				editor.putBoolean(getString(R.string.sharedPrefHeadPhoneOption), isChecked);
				editor.commit();
			}
		});
	}

	@Override
	public void onDestroy() {
		if(jarvis != null) {
			jarvis.stop();
			jarvis.shutdown();

			audioManager.unregisterMediaButtonEventReceiver(volumeListener);

			Log.i("DESTROYED", "Jarvis Destroyed!");
		}
		super.onDestroy();
	}

}
