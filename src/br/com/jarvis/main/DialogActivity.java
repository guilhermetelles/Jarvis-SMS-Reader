package br.com.jarvis.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import br.com.jarvis.R;

public class DialogActivity extends Activity {
	
//	TextView messageTextView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		// Configura o dialog a fechar se houver toque fora da área dele
		setFinishOnTouchOutside(true);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_dialog);
		
		loadViews();
		loadMessage();
		
		Log.e("Dialog", "Dialog showing?");
	}
	
	private void loadViews() {
//		messageTextView = (TextView) findViewById(R.id.messageText);
	}
	
	private void loadMessage() {
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			String messageText = extras.getString(getString(R.string.messageText));
//			messageTextView.setText(messageText);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Nova Mensagem de ");
			builder.setMessage(messageText);
			builder.setNeutralButton("OK", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.e("Teste", "Button pressed");
				}
			});
			
			AlertDialog dialog = builder.create();
			dialog.show();
		}
		
	}
}
