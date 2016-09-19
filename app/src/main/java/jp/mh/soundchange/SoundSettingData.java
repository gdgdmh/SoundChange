package jp.mh.soundchange;

/**
 * Created by mh on 2016/04/09.
 */
public class SoundSettingData {

  void Initialize() {
    sound_normal_volume_ = new SoundVolumeData();
    sound_normal_volume_.SetInvalidValue();
    sound_vibrate_volume_ = new SoundVolumeData();
    sound_vibrate_volume_.SetInvalidValue();
    sound_silent_volume_ = new SoundVolumeData();
    sound_silent_volume_.SetInvalidValue();
  }

  boolean IsInvalidValue() {
    if (sound_normal_volume_ == null) {
      return false;
    }
    if (sound_vibrate_volume_ == null) {
      return false;
    }
    if (sound_silent_volume_ == null) {
      return false;
    }
    if ((sound_normal_volume_.IsInvalid() == true)
        && (sound_vibrate_volume_.IsInvalid() == true)
        && (sound_silent_volume_.IsInvalid() == true)) {
      return true;
    }
    return false;
  }

  public boolean IsEquals(final SoundSettingData source_data) {
    if ((sound_normal_volume_ == null) || (sound_vibrate_volume_ == null)
        || (sound_silent_volume_ == null)) {
      return false;
    }
    if ((source_data.sound_normal_volume_ == null) || (source_data.sound_vibrate_volume_ == null)
        || (source_data.sound_silent_volume_ == null)) {
      return false;
    }

    if ((sound_normal_volume_.IsEquals(source_data.sound_normal_volume_) == false)
        || (sound_vibrate_volume_.IsEquals(source_data.sound_vibrate_volume_) == false)
        || (sound_silent_volume_.IsEquals(source_data.sound_silent_volume_) == false)) {
      return false;
    }
    return true;
  }

  public boolean Copy(final SoundSettingData source_data) {
    if ((sound_normal_volume_ == null) || (sound_vibrate_volume_ == null)
        || (sound_silent_volume_ == null)) {
      return false;
    }
    if ((source_data.sound_normal_volume_ == null) || (source_data.sound_vibrate_volume_ == null)
        || (source_data.sound_silent_volume_ == null)) {
      return false;
    }

    sound_normal_volume_.Copy(source_data.sound_normal_volume_);
    sound_vibrate_volume_.Copy(source_data.sound_vibrate_volume_);
    sound_silent_volume_.Copy(source_data.sound_silent_volume_);
    return true;
  }

  SoundVolumeData GetNormalVolume() { return sound_normal_volume_; }
  SoundVolumeData GetVibrateVolume() { return sound_vibrate_volume_; }
  SoundVolumeData GetSilentVolume() { return sound_silent_volume_; }

  private SoundVolumeData sound_normal_volume_ = null;
  private SoundVolumeData sound_vibrate_volume_ = null;
  private SoundVolumeData sound_silent_volume_ = null;

}
