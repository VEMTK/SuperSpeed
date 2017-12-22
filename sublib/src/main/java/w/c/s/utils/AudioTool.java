package w.c.s.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by xlc on 2017/5/24.
 */

public class AudioTool {

    private static AudioTool instance = null;

    private AudioManager audioManager = null;

    public static AudioTool getInstance(Context c) {
        if (instance == null) {
            instance = new AudioTool(c);
        }
        return instance;
    }

    private AudioTool(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setSlience() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    public void setNomal() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6, 0);
    }

    public int getRingMode() {
//        audioManager.getRingerMode();
        try {
            return (int) audioManager.getClass().getMethod("getRingerMode").invoke(audioManager);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return 2;
    }

    public void setRingMode(int org){
        audioManager.setRingerMode(org);
    }
}
