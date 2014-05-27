package br.com.jarvis.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;

public class ContactsUtil {
	
	public static String getContactNameByNumber(Context context, String number){
		
		String[] projection = {
				PhoneLookup.DISPLAY_NAME,
		};
		
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

		Cursor result = context.getContentResolver().query(uri, projection, null, null, null);
		
		int nameIndex = result.getColumnIndex(PhoneLookup.DISPLAY_NAME);
		
		String name = "";
		
		if(result != null) {
			while(result.moveToNext()) {
				name = result.getString(nameIndex);
			}
		} 
		
		// Se não existir contato com esse número, dizer o número. Intercalando pontos para o TTS dizer cada um individualmente.
		if(name.equals("")){
			for(int i = 0; i < number.length(); i++) {
				char c = number.charAt(i);
				name += c + " ";
			}
		}
		
		result.close();
		
		return name;
	}
}
