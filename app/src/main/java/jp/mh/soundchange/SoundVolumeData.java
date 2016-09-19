package jp.mh.soundchange;

import android.media.AudioManager;

import jp.mh.MhSoundUtil;

/**
 * Created by mh on 2016/04/17.
 */
public class SoundVolumeData {

  public static final int kInvalidValue = -1;

  public void SetInvalidValue() {
    alarm_volume_ = kInvalidValue;
    music_volume_ = kInvalidValue;
    notification_volume_ = kInvalidValue;
    ring_volume_ = kInvalidValue;
    system_volume_ = kInvalidValue;
    voice_call_volume_ = kInvalidValue;
  }

  // AudioManagerから取得した値で設定する
  public void SetWithAudioManager(AudioManager audio_manager) {
    alarm_volume_ = MhSoundUtil.GetAlarmVolume(audio_manager);
    music_volume_ = MhSoundUtil.GetMusicVolume(audio_manager);
    notification_volume_ = MhSoundUtil.GetNotificationVolume(audio_manager);
    ring_volume_ = MhSoundUtil.GetRingVolume(audio_manager);
    system_volume_ = MhSoundUtil.GetSystemVolume(audio_manager);
    voice_call_volume_ = MhSoundUtil.GetVoiceCallVolume(audio_manager);
  }

  // AudioManagerに値をセットする
  public void ApplyAudioManager(AudioManager audio_manager) {
    MhSoundUtil.SetAlarmVolume(audio_manager, alarm_volume_);
    MhSoundUtil.SetMusicVolume(audio_manager, music_volume_);
    MhSoundUtil.SetNotificationVolume(audio_manager, notification_volume_);
    MhSoundUtil.SetRingVolume(audio_manager, ring_volume_);
    MhSoundUtil.SetSystemVolume(audio_manager, system_volume_);
    MhSoundUtil.SetVoiceCallVolume(audio_manager, voice_call_volume_);
  }

  // 値の比較
  public boolean IsEquals(final SoundVolumeData source_data) {
    if ((alarm_volume_ == source_data.alarm_volume_)
        && (music_volume_ == source_data.music_volume_)
        && (notification_volume_ == source_data.notification_volume_)
        && (ring_volume_ == source_data.ring_volume_)
        && (system_volume_ == source_data.system_volume_)
        && (voice_call_volume_ == source_data.voice_call_volume_)) {
      // パラメータの完全一致
      return true;
    }
    return false;
  }

  // 値のコピー
  public void Copy(final SoundVolumeData source_data) {
    if (source_data == null) {
      return;
    }
    alarm_volume_ = source_data.alarm_volume_;
    music_volume_ = source_data.music_volume_;
    notification_volume_ = source_data.notification_volume_;
    ring_volume_ = source_data.ring_volume_;
    system_volume_ = source_data.system_volume_;
    voice_call_volume_ = source_data.voice_call_volume_;
  }

  public void set_alarm_volume(int alarm_volume_) {
    this.alarm_volume_ = alarm_volume_;
  }

  public void set_music_volume(int music_volume_) {
    this.music_volume_ = music_volume_;
  }

  public void set_notification_volume(int notification_volume_) {
    this.notification_volume_ = notification_volume_;
  }

  public void set_ring_volume(int ring_volume_) {
    this.ring_volume_ = ring_volume_;
  }

  public void set_system_volume(int system_volume_) {
    this.system_volume_ = system_volume_;
  }

  public void set_voice_call_volume(int voice_call_volume_) {
    this.voice_call_volume_ = voice_call_volume_;
  }

  public int get_alarm_volume() {
    return alarm_volume_;
  }

  public int get_music_volume() {
    return music_volume_;
  }

  public int get_notification_volume() {
    return notification_volume_;
  }

  public int get_ring_volume() {
    return ring_volume_;
  }

  public int get_system_volume() {
    return system_volume_;
  }

  public int get_voice_call_volume() {
    return voice_call_volume_;
  }

  // どれか一部が正常なボリュームではないときにtrue
  public boolean IsInvalid() {
    if ((IsInvalidAlarmVolume() == true) && (IsInvalidMusicVolume() == true)
        && (IsInvalidNotificationVolume() == true) && (IsInvalidRingVolume() == true)
        && (IsInvalidSystemVolume() == true) && (IsInvalidVoiceCallVolume() == true)
        ) {
      return true;
    }
    return false;
  }

  public boolean IsInvalidAlarmVolume() {
    if (alarm_volume_ == kInvalidValue) {
      return true;
    }
    return false;
  }

  public boolean IsInvalidMusicVolume() {
    if (music_volume_ == kInvalidValue) {
      return true;
    }
    return false;
  }

  public boolean IsInvalidNotificationVolume() {
    if (notification_volume_ == kInvalidValue) {
      return true;
    }
    return false;
  }

  public boolean IsInvalidRingVolume() {
    if (ring_volume_ == kInvalidValue) {
      return true;
    }
    return false;
  }

  public boolean IsInvalidSystemVolume() {
    if (system_volume_ == kInvalidValue) {
      return true;
    }
    return false;
  }

  public boolean IsInvalidVoiceCallVolume() {
    if (voice_call_volume_ == kInvalidValue) {
      return true;
    }
    return false;
  }

  private int alarm_volume_ = 0;
  private int music_volume_ = 0;
  private int notification_volume_ = 0;
  private int ring_volume_ = 0;
  private int system_volume_ = 0;
  private int voice_call_volume_ = 0;
}
