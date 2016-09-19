package jp.mh.soundchange;

import android.content.Context;

import org.json.JSONException;

import jp.mh.MhFileUtil;
import jp.mh.MhJsonUtil;
import jp.mh.MhUtil;

/**
 * Created by mh on 2016/04/20.
 */
// セーブ・ロードを管理する
// 常にインスタンスを保持するのではなくて必要なときに生成、必要なくなったら削除
public class SaveManager {

  // file_name
  public static final String kSoundSettingDataFileName = "save.dat";

  public static final String kLabelSoundModeNormal = "normal_";
  public static final String kLabelSoundModeVibrate = "vibrate_";
  public static final String kLabelSoundModeSilent = "silent_";

  // label
  public static final String kSaveJsonNameAlarmVolume = "alarm_volume";
  public static final String kSaveJsonNameMusicVolume = "music_volume";
  public static final String kSaveJsonNameNotificationVolume = "notification_volume";
  public static final String kSaveJsonNameRingVolume = "ring_volume";
  public static final String kSaveJsonNameSystemVolume = "system_volume";
  public static final String kSaveJsonNameVoiceCallVolume = "alarm_volume";

  public void InitializeSave() {
  }

  public void SaveSettingData(Context context, final SoundSettingData source_data) {

    json_save_ = new MhJsonUtil();
    json_save_.Initialize();

    MhUtil.Print("SaveManager::SaveSettingData");

    // jsonに追加
    SaveSoundVolumeData(context, source_data.GetNormalVolume(), kLabelSoundModeNormal);
    SaveSoundVolumeData(context, source_data.GetVibrateVolume(), kLabelSoundModeVibrate);
    SaveSoundVolumeData(context, source_data.GetSilentVolume(), kLabelSoundModeSilent);

    // 書き込み
    {
      if (MhFileUtil.WriteBufferPrivate(context, kSoundSettingDataFileName, json_save_.GetBytes())) {
        MhUtil.Print("SaveManager::SaveSettingData success");
      } else {
        MhUtil.Print("SaveManager::SaveSettingData failed");
      }
    }

    // 読み込んでみる(Debug)
    {
      byte[] buffer = MhFileUtil.ReadBuffer(context, kSoundSettingDataFileName);
      if (buffer != null) {
        MhJsonUtil json = new MhJsonUtil();
        json.InitializeWithBuffer(buffer);

        MhUtil.Print("SaveManager::SaveSettingData debug read json ---------");
        MhUtil.Print(json.toString());
        MhUtil.Print("------------------------------------------------------");

        json.Release();
        json = null;
      }
    }

  }

  private void SaveSoundVolumeData(Context context, final SoundVolumeData save_data, String add_head_label) {
    if (save_data == null) {
      return;
    }
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameAlarmVolume + " value=" + save_data.get_alarm_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameMusicVolume + " value=" + save_data.get_music_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameNotificationVolume + " value=" + save_data.get_notification_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameRingVolume + " value=" + save_data.get_ring_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameSystemVolume + " value=" + save_data.get_system_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameVoiceCallVolume + " value=" + save_data.get_voice_call_volume());

    json_save_.AddDataInt(add_head_label + kSaveJsonNameAlarmVolume, save_data.get_alarm_volume());
    json_save_.AddDataInt(add_head_label +kSaveJsonNameMusicVolume, save_data.get_music_volume());
    json_save_.AddDataInt(add_head_label +kSaveJsonNameNotificationVolume, save_data.get_notification_volume());
    json_save_.AddDataInt(add_head_label +kSaveJsonNameRingVolume, save_data.get_ring_volume());
    json_save_.AddDataInt(add_head_label +kSaveJsonNameSystemVolume, save_data.get_system_volume());
    json_save_.AddDataInt(add_head_label +kSaveJsonNameVoiceCallVolume, save_data.get_voice_call_volume());
  }


  public void InitializeLoad() {
  }

  public boolean LoadSettingData(Context context, SoundSettingData destination_data) {

    MhUtil.Print("SaveManager::LoadSettingData");
    try
    {
      // データを読み込む
      byte[] buffer = MhFileUtil.ReadBuffer(context, kSoundSettingDataFileName);
      if (buffer != null) {
        json_load_ = new MhJsonUtil();
        json_load_.InitializeWithBuffer(buffer);

        MhUtil.Print("SaveManager::LoadSettingData read json ---------");
        MhUtil.Print(json_load_.toString());
        MhUtil.Print("------------------------------------------------------");

      }
    } catch (OutOfMemoryError memoryError) {
      MhUtil.Print("SaveManager::LoadSettingData e " + memoryError);
      if (json_load_ != null) {
        json_load_.Release();
        json_load_ = null;
      }
      return false;

    } catch (Exception e) {
      MhUtil.Print("SaveManager::LoadSettingData e " + e);
      if (json_load_ != null) {
        json_load_.Release();
        json_load_ = null;
      }
      return false;
    }

    // すべての値を初期化しておく
    // もしボリュームの取得時に例外が発生しているならチェックできる
    destination_data.GetNormalVolume().SetInvalidValue();
    destination_data.GetVibrateVolume().SetInvalidValue();
    destination_data.GetSilentVolume().SetInvalidValue();

    // ロード試行
    LoadSoundVolumeData(context, destination_data.GetNormalVolume(), kLabelSoundModeNormal);
    LoadSoundVolumeData(context, destination_data.GetVibrateVolume(), kLabelSoundModeVibrate);
    LoadSoundVolumeData(context, destination_data.GetSilentVolume(), kLabelSoundModeSilent);

    json_load_.Release();
    json_load_ = null;

    // ちゃんとロードできたかチェック
    // 無視して構わないエラーは無視
    if (destination_data.GetNormalVolume().IsInvalid() == true) {
      return false;
    }
    if (destination_data.GetVibrateVolume().IsInvalid() == true) {
      return false;
    }
    if (destination_data.GetSilentVolume().IsInvalid() == true) {
      return false;
    }
    return true;
  }

  private void LoadSoundVolumeData(Context context, SoundVolumeData load_data, String add_head_label) {
    if (load_data == null) {
      return;
    }

    // jsonから値を取得して設定
    try {
      load_data.set_alarm_volume(json_load_.GetDataInt(add_head_label + kSaveJsonNameAlarmVolume));
    } catch (JSONException e) {
    }

    try {
      load_data.set_music_volume(json_load_.GetDataInt(add_head_label + kSaveJsonNameMusicVolume));
    } catch (JSONException e) {
    }

    try {
      load_data.set_notification_volume(json_load_.GetDataInt(add_head_label + kSaveJsonNameNotificationVolume));
    } catch (JSONException e) {
    }

    try {
      load_data.set_ring_volume(json_load_.GetDataInt(add_head_label + kSaveJsonNameRingVolume));
    } catch (JSONException e) {
    }

    try {
      load_data.set_system_volume(json_load_.GetDataInt(add_head_label + kSaveJsonNameSystemVolume));
    } catch (JSONException e) {
    }

    try {
      load_data.set_voice_call_volume(json_load_.GetDataInt(add_head_label + kSaveJsonNameVoiceCallVolume));
    } catch (JSONException e) {
    }

    MhUtil.Print("label=" + add_head_label + kSaveJsonNameAlarmVolume + " value=" + load_data.get_alarm_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameMusicVolume + " value=" + load_data.get_music_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameNotificationVolume + " value=" + load_data.get_notification_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameRingVolume + " value=" + load_data.get_ring_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameSystemVolume + " value=" + load_data.get_system_volume());
    MhUtil.Print("label=" + add_head_label + kSaveJsonNameVoiceCallVolume + " value=" + load_data.get_voice_call_volume());
  }

  // attribute
  private MhJsonUtil json_save_ = null;
  private MhJsonUtil json_load_ = null;
}
