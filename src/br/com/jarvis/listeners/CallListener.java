package br.com.jarvis.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import br.com.jarvis.Jarvis;
import br.com.jarvis.R;
import br.com.jarvis.util.CheckPreferencesUtil;
import br.com.jarvis.util.ContactsUtil;

public class CallListener extends BroadcastReceiver {

	private Context mContext;

	SharedPreferences sharedPreferences = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;

		Log.i("CALL LISTENER", "On Receive");

		boolean shouldSpeak = CheckPreferencesUtil.shouldSpeakCallerName(context);

		if(shouldSpeak) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			int events = PhoneStateListener.LISTEN_CALL_STATE;
			telephonyManager.listen(phoneStateListener, events);
		}
	}

	private final PhoneStateListener phoneStateListener = new PhoneStateListener() {

		public void onCallStateChanged(int state, String incomingNumber) {
			if(state == TelephonyManager.CALL_STATE_RINGING) {
				Log.i("CALL LISTENER", "Hey Listen! New call from " + incomingNumber);

				String contactName = ContactsUtil.getContactNameByNumber(mContext, incomingNumber);
				String newCallFrom = mContext.getString(R.string.new_call);
				String text = newCallFrom + " " + contactName;

				Toast.makeText(mContext, text, Toast.LENGTH_LONG).show();

				Jarvis.startJarvisIntent(mContext, text);
			}
		}

	};

}