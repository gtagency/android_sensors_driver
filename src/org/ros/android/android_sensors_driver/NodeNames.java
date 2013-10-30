package org.ros.android.android_sensors_driver;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.provider.Settings.Secure;

public class NodeNames {

	@SuppressWarnings("serial")
	private static final Map<String, String> nameLookup = new HashMap<String, String>() {{
		put("", "dragon_driver");
		put("", "dragon_pass");
		put("9c7860ed8688cb78", "phone_nav");
		
	}};
	
	public static String whoAmI(Context context) {
		String androidId = Secure.getString(context.getContentResolver(),
	            Secure.ANDROID_ID);

		return nameLookup.containsKey(androidId)
					? nameLookup.get(androidId)
					: "android_sensor";
	}
}
