package jp.mh;

/**
 * Created by mh on 2016/03/21.
 */

import android.media.AudioManager;
import android.provider.MediaStore;

public class MhSoundUtil {

  static public int kSoundFlagDefault = AudioManager.FLAG_VIBRATE;
  static public int kSoundFlagDebug = AudioManager.FLAG_SHOW_UI;
  //AudioManager.FLAG_SHOW_UI

  // オーディオボリュームのデバッグ出力
  static public void PrintAudioVolume(AudioManager audio_manager) {

    int alarm_volume = audio_manager.getStreamVolume(AudioManager.STREAM_ALARM);
    int dtmf_volume = audio_manager.getStreamVolume(AudioManager.STREAM_DTMF);
    int music_volume = audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC);
    int notification_volume = audio_manager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
    int ring_volume = audio_manager.getStreamVolume(AudioManager.STREAM_RING);
    int system_volume = audio_manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    int voice_call_volume = audio_manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);

    int max_alarm_volume = audio_manager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
    int max_dtmf_volume = audio_manager.getStreamMaxVolume(AudioManager.STREAM_DTMF);
    int max_music_volume = audio_manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    int max_notification_volume = audio_manager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
    int max_ring_volume = audio_manager.getStreamMaxVolume(AudioManager.STREAM_RING);
    int max_system_volume = audio_manager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
    int max_voice_call_volume = audio_manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);

    // アラーム,DTMFトーン,音楽用,通知用,着信用,システム用,発信用
    MhUtil.Print("MhSoundUtil::PrintAudioVolume alarm=" + alarm_volume + ":" + max_alarm_volume);
    MhUtil.Print("MhSoundUtil::PrintAudioVolume dtmf=" + dtmf_volume + ":" + max_dtmf_volume);
    MhUtil.Print("MhSoundUtil::PrintAudioVolume music=" + music_volume + ":" + max_music_volume);
    MhUtil.Print("MhSoundUtil::PrintAudioVolume notification=" + notification_volume + ":" + max_notification_volume);
    MhUtil.Print("MhSoundUtil::PrintAudioVolume ring=" + ring_volume + ":" + max_ring_volume);
    MhUtil.Print("MhSoundUtil::PrintAudioVolume system=" + system_volume + ":" + max_system_volume);
    MhUtil.Print("MhSoundUtil::PrintAudioVolume voice_call=" + voice_call_volume + ":" + max_voice_call_volume);
  }

  static public void PrintRingerMode(AudioManager audio_manager) {
    int mode = GetRingerMode(audio_manager);
    switch (mode) {
      case AudioManager.RINGER_MODE_NORMAL:
        MhUtil.Print("MhSoundUtil::PrintRingerMode normal");
        break;
      case AudioManager.RINGER_MODE_VIBRATE:
        MhUtil.Print("MhSoundUtil::PrintRingerMode vibrate");
        break;
      case AudioManager.RINGER_MODE_SILENT:
        MhUtil.Print("MhSoundUtil::PrintRingerMode silent");
        break;
      default:
        MhUtil.Print("MhSoundUtil::PrintRingerMode unknown");
        break;
    }
  }

  static public void SetRingerMode(AudioManager audio_manager, int ringer_mode) {
    audio_manager.setRingerMode(ringer_mode);
  }

  static public void SetRingerModeNormal(AudioManager audio_manager) {
    audio_manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
  }

  static public void SetRingerModeVibrate(AudioManager audio_manager) {
    audio_manager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
  }

  static public void SetRingerModeSilent(AudioManager audio_manager) {
    audio_manager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
  }

  // 現在のモード取得
  static public boolean IsRingerModeNormal(AudioManager audio_manager) {
    if (audio_manager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
      return true;
    }
    return false;
  }

  static public boolean IsRingerModeVibrate(AudioManager audio_manager) {
    if (audio_manager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
      return true;
    }
    return false;
  }

  static public boolean IsRingerModeSilent(AudioManager audio_manager) {
    if (audio_manager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
      return true;
    }
    return false;
  }

  static public int GetRingerMode(AudioManager audio_manager) {
    return audio_manager.getRingerMode();
  }

  /*
// AudioManager取得
AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
// サイレントモードに設定
am.setRingerMode(AudioManager.RINGER_MODE_SILENT);   */

  // ボリューム設定
  static public void SetAlarmVolume(AudioManager audio_manager, int volume) {
    audio_manager.setStreamVolume(AudioManager.STREAM_ALARM, volume, kSoundFlagDefault);
  }
  static public void SetDtmfVolume(AudioManager audio_manager, int volume) {
    audio_manager.setStreamVolume(AudioManager.STREAM_DTMF, volume, kSoundFlagDefault);
  }
  static public void SetMusicVolume(AudioManager audio_manager, int volume) {
    audio_manager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, kSoundFlagDefault);
  }
  static public void SetNotificationVolume(AudioManager audio_manager, int volume) {
    audio_manager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, volume, kSoundFlagDefault);
  }
  static public void SetRingVolume(AudioManager audio_manager, int volume) {
    audio_manager.setStreamVolume(AudioManager.STREAM_RING, volume, kSoundFlagDefault);
  }
  static public void SetSystemVolume(AudioManager audio_manager, int volume) {
    audio_manager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, kSoundFlagDefault);
  }
  static public void SetVoiceCallVolume(AudioManager audio_manager, int volume) {
    audio_manager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, volume, kSoundFlagDefault);
  }

  // ボリューム取得
  static public int GetAlarmVolume(AudioManager audio_manager) {
    return audio_manager.getStreamVolume(AudioManager.STREAM_ALARM);
  }

  static public int GetDtmfVolume(AudioManager audio_manager) {
    return audio_manager.getStreamVolume(AudioManager.STREAM_DTMF);
  }

  static public int GetMusicVolume(AudioManager audio_manager) {
    return audio_manager.getStreamVolume(AudioManager.STREAM_MUSIC);
  }

  static public int GetNotificationVolume(AudioManager audio_manager) {
    return audio_manager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
  }

  static public int GetRingVolume(AudioManager audio_manager) {
    return audio_manager.getStreamVolume(AudioManager.STREAM_RING);
  }

  static public int GetSystemVolume(AudioManager audio_manager) {
    return audio_manager.getStreamVolume(AudioManager.STREAM_SYSTEM);
  }

  static public int GetVoiceCallVolume(AudioManager audio_manager) {
    return audio_manager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
  }

  static public int GetAlarmMaxVolume(AudioManager audio_manager) {
    return audio_manager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
  }

  static public int GetDtmfMaxVolume(AudioManager audio_manager) {
    return audio_manager.getStreamMaxVolume(AudioManager.STREAM_DTMF);
  }

  static public int GetMusicMaxVolume(AudioManager audio_manager) {
    return audio_manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
  }

  static public int GetNotificationMaxVolume(AudioManager audio_manager) {
    return audio_manager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
  }

  static public int GetRingMaxVolume(AudioManager audio_manager) {
    return audio_manager.getStreamMaxVolume(AudioManager.STREAM_RING);
  }

  static public int GetSystemMaxVolume(AudioManager audio_manager) {
    return audio_manager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
  }

  static public int GetVoiceCallMaxVolume(AudioManager audio_manager) {
    return audio_manager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
  }

}
