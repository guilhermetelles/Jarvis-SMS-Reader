package br.com.jarvis.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class VolumeListener extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

    	Log.i("VOLUME LISTENER", "RECEIVING SOMETHING");
		
		if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode()) {
            	Toast.makeText(context, "VOLUME UP!", Toast.LENGTH_SHORT).show();
            	Log.i("VOLUME", "UP");
            }
            if (KeyEvent.KEYCODE_VOLUME_DOWN == event.getKeyCode()) {
            	Toast.makeText(context, "VOLUME DOWN!", Toast.LENGTH_SHORT).show();
            	Log.i("VOLUME", "DOWN");
            }
        }
	}

}
