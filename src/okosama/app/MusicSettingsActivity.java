package okosama.app;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MusicSettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    // static final String KEY_ENABLE_FOCUS_LOSS_DUCKING = "enable_focus_loss_ducking";
    public static final String KEY_ENABLE_MEDIA_CHANGE_VIBRATE = "enable_media_change_vibrate";
    public static final String KEY_ENABLE_HEADSET_PLUG_AND_PLAY = "enable_headset_plug_and_play";
    public static final String KEY_VIBRATE_INTENSITY = "virate_intensity";
    public static final String KEY_ENABLE_ANIMATION = "enable_animation";
    public static final String KEY_ANIMATION_LEVEL = "animation_level";
    public static final String KEY_ANIMATION_SPEED = "animation_speed";
    
    static final String DEFAULT_VIB_INTENSITY_DB = "10";

//    static final String ACTION_ENABLE_GESTURES_CHANGED = "com.android.music.enablegestureschanged";
//    static final String ACTION_GESTURES_CHANGED = "com.android.music.gestureschanged";

    public static final String PREFERENCES_FILE = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(PREFERENCES_FILE);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    		 
		ListPreference vib_intensity_preference = (ListPreference)getPreferenceScreen().findPreference(KEY_VIBRATE_INTENSITY);
		vib_intensity_preference.setSummary(vib_intensity_preference.getEntry());

		ListPreference animation_level_preference = (ListPreference)getPreferenceScreen().findPreference(KEY_ANIMATION_LEVEL);
		animation_level_preference.setSummary(animation_level_preference.getEntry());
    }

    @Override
    protected void onResume() {
        super.onResume();
		ListPreference vib_intensity_preference = (ListPreference)getPreferenceScreen().findPreference(KEY_VIBRATE_INTENSITY);
		vib_intensity_preference.setSummary(vib_intensity_preference.getEntry());
        
		ListPreference animation_level_preference = (ListPreference)getPreferenceScreen().findPreference(KEY_ANIMATION_LEVEL);
		animation_level_preference.setSummary(animation_level_preference.getEntry());

		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
