package superclean.solution.com.superspeed.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import superclean.solution.com.superspeed.service.AccessibilityMonitorService;


/**
 * Created by admin on 2017/10/16.
 */

public class AccessUtil {


    public static boolean isAccessibilitySettingsOn (Context mContext) {
        final String service = mContext.getPackageName() + "/" + AccessibilityMonitorService.class.getName();

        try {
            int accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);

            TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
            if ( accessibilityEnabled == 1 ) {
                String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if ( settingValue != null ) {
                    mStringColonSplitter.setString(settingValue);
                    while ( mStringColonSplitter.hasNext() ) {
                        String accessibilityService = mStringColonSplitter.next();
                        if ( accessibilityService.equalsIgnoreCase(service) ) {
                            return true;
                        }
                    }
                }
            }
        } catch ( Settings.SettingNotFoundException e ) {

        }
        return false;
    }



}
