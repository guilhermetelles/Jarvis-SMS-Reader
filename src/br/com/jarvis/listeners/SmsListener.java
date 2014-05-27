package br.com.jarvis.listeners;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import br.com.jarvis.Jarvis;
import br.com.jarvis.R;
import br.com.jarvis.main.DialogActivity;
import br.com.jarvis.util.CheckPreferencesUtil;
import br.com.jarvis.util.ContactsUtil;

public class SmsListener extends BroadcastReceiver{

	SharedPreferences sharedPreferences = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		boolean shouldSpeak = CheckPreferencesUtil.shouldSpeakSMS(context);
		
		Log.i("SMSListener","Receiving... " + shouldSpeak);

		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){

			Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
			SmsMessage[] msgs = null;
			if (bundle != null){
				try{
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for(int i=0; i<msgs.length; i++){
						msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
						String msgFrom = msgs[i].getOriginatingAddress();
						String msgBody = msgs[i].getMessageBody();

						buildMessageAndSpeak(context, shouldSpeak, msgFrom,	msgBody);
					}
				}catch(Exception e){
					Log.e("Exception caught",e.getMessage());
				}
			}
		}
	}

	private void buildMessageAndSpeak(Context context, boolean shouldSpeak, String msgFrom, String msgBody) {
		
		Log.i("SMSListener","Building... ");
		
		// Get contact name by number
		String contactName = ContactsUtil.getContactNameByNumber(context, msgFrom);
		String newMessageFrom = context.getString(R.string.new_message);
		String text = newMessageFrom + " " + contactName + ": " + msgBody;
		
		//
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
		
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("Nova Mensagem de " + contactName);
//		builder.setMessage(text);
		
//		builder.setPositiveButton("Sim", new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		builder.setNegativeButton("Não", new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
//		AlertDialog dialog = builder.create();
//		dialog.show();
		
		Intent intent = new Intent(context, DialogActivity.class);
		intent.putExtra(context.getString(R.string.messageText), text);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		//
		
		if(shouldSpeak) {
			Jarvis.startJarvisIntent(context, text);
		}
	}

}