package jp.mh.soundchange;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.media.AudioManager;
import android.widget.TextView;
import android.widget.ToggleButton;


import java.util.concurrent.ExecutionException;

import jp.mh.MhFileInput;
import jp.mh.MhFileOutput;
import jp.mh.MhFileUtil;
import jp.mh.MhJsonUtil;
import jp.mh.MhSoundUtil;
import jp.mh.MhUtil;

public class MainActivity extends Activity implements OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener, View.OnClickListener {

  static final int TYPE_ALARM = 0;
  static final int TYPE_MUSIC = 1;
  static final int TYPE_DTMF = 2; // NOTIFICATION,SYSTEM,RINGに影響する
  static final int TYPE_NOTIFICATION = 3;
  static final int TYPE_RING = 4;
  static final int TYPE_SYSTEM = 5;
  static final int TYPE_VOICE_CALL = 6;
  static final int TYPE_MAX_NUMBER = 7; // typeの数

  // headsetとかのイベントを取る
  private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      final String action = intent.getAction();
      if (action == null) {
        return;
      }

      switch (action) {
        case Intent.ACTION_HEADSET_PLUG:
          MhUtil.Print("MainActivity::onCreate Intent.ACTION_HEADSET_PLUG");
        {
          int state = intent.getIntExtra("state", -1);
          if (state == 0) {
            // ヘッドセットがつけられてないか外された
            MhUtil.Print("MainActivity::onCreate Intent.ACTION_HEADSET_PLUG remove");
          } else if (state > 0) {
            // ヘッドセットがつけられた
            MhUtil.Print("MainActivity::onCreate Intent.ACTION_HEADSET_PLUG add");
          }
        }
        break;
        default:
          break;
      }
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_layout);

    // サウンド設定データを初期化
    InitializeSoundSettingData();

    // ヘッドセットのチェックボックス(デフォルトではfalse)
    {
      ToggleButton button = (ToggleButton)findViewById(R.id.headset_toggle_button);
      button.setChecked(is_headset_on);
    }

    registerReceiver(broadcastReceiver2, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    //registerReceiver(broadcastReceiver2, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

    /*
    plugStateChangeReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        // プラグの状態を取得
        boolean is_plugged = false;
        if (intent.getIntExtra("state", 0) > 0) {
          is_plugged = true;
          MhUtil.Print("MainActivity::onCreate plugged");
        }
        is_headset_on = is_plugged;
        ToggleButton button = (ToggleButton)findViewById(R.id.headset_toggle_button);
        button.setChecked(is_headset_on);
        MhUtil.Print("MainActivity::onCreate plugged=" + is_headset_on);
      }
    };
    */

    // セーブファイルが存在している
    if (MhFileUtil.IsCurrentDirectoryFileExist(this, SaveManager.kSoundSettingDataFileName) == true) {
      MhUtil.Print("MainActivity::onCreate save_file exist");
      // セーブファイルが存在している
      // ロード
      Load();
      // 現在の設定を優先
      SetCurrentSoundToSettingData();
    } else {
      MhUtil.Print("MainActivity::onCreate save_file not exist");
      // セーブファイルが存在しない
      // 今のサウンド状態を設定データに保存
      SetCurrentSoundToSettingData();
      // そしてすぐセーブ
      Save();
    }

    // シークバーの関連付け
    SetSeekbarResource();
    // シークバーにデフォルトパラメータを設定
    SetSeekBarWithSoundValue();
    // ラジオボタンの関連付け
    SetRadioGroupResource();
    // RingerModeでラジオボタンの状態を設定
    SetRadioButtonWithRingerMode();
    // reflesh buttonの関連付け
    SetButtonResource();
    // すべてのボリュームテキストを更新
    UpdateVolumeTextAll();
  }

  @Override
  protected void onDestroy() {
    MhUtil.Print("MainActivity::onDestroy");
    super.onDestroy();
    if (broadcastReceiver2 != null) {
      MhUtil.Print("MainActivity::onDestroy receiver");
      unregisterReceiver(broadcastReceiver2);
      MhUtil.Print("MainActivity::onDestroy receiver unregist");
    }
    MhUtil.Print("MainActivity::onDestroy finish");
  }

  // Activity
  // 画面を離れた
  @Override
  protected void onPause() {
    MhUtil.Print("MainActivity::onPause called");
    super.onPause();
    // データが変更されてたらセーブ
    if (setting_data_.IsEquals(past_setting_data_) == false) {
      if (Save() == true) {
        MhUtil.Print("MainActivity::onPause save success");
      } else {
        MhUtil.Print("MainActivity::onPause save failed");
      }
    }
  }

  // Activity
  // 画面に戻ってきた
  @Override
  protected void onResume() {
    MhUtil.Print("MainActivity::onResume called");
    super.onResume();

    // 更新
    UpdateResourceWithCurrentSoundSource();
    // 現在のボリュームをセット
    SetCurrentSoundToSettingData();
    // 表示パラメータをすべて更新
    //UpdateAllDisplayParameter();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    //MenuItem target = menu.add(0, 0, 0, 0);
      return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      // Handle action bar item clicks here. The action bar will
      // automatically handle clicks on the Home/Up button, so long
      // as you specify a parent activity in AndroidManifest.xml.
      int id = item.getItemId();

      //noinspection SimplifiableIfStatement
      if (id == R.id.action_settings) {
        return true;
      }

      return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        {
          MhUtil.Print("MainActivity::onTouchEvent down");
        }
        break;
      case MotionEvent.ACTION_UP:
        break;
      case MotionEvent.ACTION_MOVE:
        break;
      case MotionEvent.ACTION_CANCEL:
        break;
      default:
        break;
    }
    return true;
  }

  // Override OnSeekBarChangeListener
  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    MhUtil.Print("MainActivity::onProgressChanged progress " + progress);
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    if (seekBar == alarm_seekbar_) {
      MhUtil.Print("alarm");
      MhSoundUtil.SetAlarmVolume(audio_manager, progress);

      UpdateVolumeText(TYPE_ALARM);
    } else if (seekBar == dtmf_seekbar_) {
      MhUtil.Print("dtmf");
      //MhSoundUtil.SetDtmfVolume(audio_manager, progress);
      //UpdateVolumeText(TYPE_DTMF);
    } else if (seekBar == music_seekbar_) {
      MhUtil.Print("music");
      MhSoundUtil.SetMusicVolume(audio_manager, progress);
      UpdateVolumeText(TYPE_MUSIC);
    } else if (seekBar == notification_seekbar_) {
      MhUtil.Print("notification");
      MhSoundUtil.SetNotificationVolume(audio_manager, progress);
      UpdateVolumeText(TYPE_NOTIFICATION);
    } else if (seekBar == ring_seekbar_) {
      MhUtil.Print("ring");
      MhSoundUtil.SetRingVolume(audio_manager, progress);
      UpdateVolumeText(TYPE_RING);
    } else if (seekBar == system_seekbar_) {
      MhUtil.Print("system");
      MhSoundUtil.SetSystemVolume(audio_manager, progress);
      UpdateVolumeText(TYPE_SYSTEM);
    } else if (seekBar == voice_call_seekbar_) {
      MhUtil.Print("voice_call");
      MhSoundUtil.SetVoiceCallVolume(audio_manager, progress);
      UpdateVolumeText(TYPE_VOICE_CALL);
    }
    // 設定を更新
    SetCurrentSoundToSettingData();
  }
  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
  }
  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
  }

  // RadioGroup.OnCheckedChangeListener
  @Override
  public void onCheckedChanged(RadioGroup group, int checkedId) {
    MhUtil.Print("MainActivity::onCheckedChanged checkedId " + checkedId);
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


    switch (checkedId) {
      case R.id.normal_radio_button:
        {
          MhUtil.Print("MainActivity::onCheckedChanged normal");
          // サウンドモードをノーマルに変更
          MhSoundUtil.SetRingerModeNormal(audio_manager);
          // 前のボリュームを適用
          ApplySettingDataToAudioManager();
          // シークバーの状態を更新
          SetSeekBarWithSoundValue();
        }
        break;
      case R.id.vibrate_radio_button:
        MhUtil.Print("MainActivity::onCheckedChanged vibrate");
        MhSoundUtil.SetRingerModeVibrate(audio_manager);
        // 音の鳴るものを無効
        ApplyDisableSound();
        // 前のボリュームを適用
        ApplySettingDataToAudioManager();
        // シークバーの状態を更新
        SetSeekBarWithSoundValue();
        break;
      case R.id.silent_radio_button:
        MhUtil.Print("MainActivity::onCheckedChanged silent");
        MhSoundUtil.SetRingerModeSilent(audio_manager);
        // 音の鳴るものを無効
        ApplyDisableSound();
        // 前のボリュームを適用
        ApplySettingDataToAudioManager();
        // シークバーの状態を更新
        SetSeekBarWithSoundValue();
        break;
      default:
        break;
    }

  }

  // View.OnClickListener
  @Override
  public void onClick(View v) {
    MhUtil.Print("MainActivity::onClick called");
    // 表示系パラメータを更新
    UpdateAllDisplayParameter();
  }

  // リソースからシークバーを取得してセット
  private void SetSeekbarResource() {
    alarm_seekbar_ = (SeekBar)findViewById(R.id.alarm_seekbar);
    dtmf_seekbar_ = null; // 使わなくなった
    //dtmf_seekbar_ = (SeekBar)findViewById(R.id.dtmf_seekbar);
    music_seekbar_ = (SeekBar)findViewById(R.id.music_seekbar);
    notification_seekbar_ = (SeekBar)findViewById(R.id.notification_seekbar);
    ring_seekbar_ = (SeekBar)findViewById(R.id.ring_seekbar);
    system_seekbar_ = (SeekBar)findViewById(R.id.system_seekbar);
    voice_call_seekbar_ = (SeekBar)findViewById(R.id.voice_call_seekbar);
  }

  private void SetRadioGroupResource() {
    ring_mode_radio_group_ = (RadioGroup)findViewById(R.id.ring_mode_radio_group);
    ring_mode_radio_group_.setOnCheckedChangeListener(this);
  }

  private void DispDebugVolume() {
    // volumeをコンソール出力する
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    MhSoundUtil.PrintAudioVolume(audio_manager);
  }

  private void InitializeSettingData() {
    setting_data_ = new SoundSettingData();
    // デフォルトは無効値
    setting_data_.GetNormalVolume().SetInvalidValue();
    setting_data_.GetVibrateVolume().SetInvalidValue();
    setting_data_.GetSilentVolume().SetInvalidValue();

  }

  private void SetSeekBarWithSoundValue() {
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    {
      SeekBar seekbar = (SeekBar) findViewById(R.id.alarm_seekbar);
      seekbar.setProgress(MhSoundUtil.GetAlarmVolume(audio_manager));
      seekbar.setMax(MhSoundUtil.GetAlarmMaxVolume(audio_manager));
      seekbar.setOnSeekBarChangeListener(this);
    }
    {
      // 使わなくなった
      //SeekBar seekbar = (SeekBar) findViewById(R.id.dtmf_seekbar);
      //seekbar.setProgress(MhSoundUtil.GetDtmfVolume(audio_manager));
      //seekbar.setMax(MhSoundUtil.GetDtmfMaxVolume(audio_manager));
      //seekbar.setOnSeekBarChangeListener(this);
    }
    {
      SeekBar seekbar = (SeekBar) findViewById(R.id.music_seekbar);
      seekbar.setProgress(MhSoundUtil.GetMusicVolume(audio_manager));
      seekbar.setMax(MhSoundUtil.GetMusicMaxVolume(audio_manager));
      seekbar.setOnSeekBarChangeListener(this);
    }
    {
      SeekBar seekbar = (SeekBar) findViewById(R.id.notification_seekbar);
      seekbar.setProgress(MhSoundUtil.GetNotificationVolume(audio_manager));
      seekbar.setMax(MhSoundUtil.GetNotificationMaxVolume(audio_manager));
      seekbar.setOnSeekBarChangeListener(this);
    }
    {
      SeekBar seekbar = (SeekBar) findViewById(R.id.ring_seekbar);
      seekbar.setProgress(MhSoundUtil.GetRingVolume(audio_manager));
      seekbar.setMax(MhSoundUtil.GetRingMaxVolume(audio_manager));
      seekbar.setOnSeekBarChangeListener(this);
    }
    {
      SeekBar seekbar = (SeekBar) findViewById(R.id.system_seekbar);
      seekbar.setProgress(MhSoundUtil.GetSystemVolume(audio_manager));
      seekbar.setMax(MhSoundUtil.GetSystemMaxVolume(audio_manager));
      seekbar.setOnSeekBarChangeListener(this);
    }
    {
      SeekBar seekbar = (SeekBar) findViewById(R.id.voice_call_seekbar);
      seekbar.setProgress(MhSoundUtil.GetVoiceCallVolume(audio_manager));
      seekbar.setMax(MhSoundUtil.GetVoiceCallMaxVolume(audio_manager));
      seekbar.setOnSeekBarChangeListener(this);
    }
  }

  // RingerModeからラジオボタンを設定する
  private void SetRadioButtonWithRingerMode() {
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int mode = MhSoundUtil.GetRingerMode(audio_manager);
    // ringモードからラジオボタンを設定する
    switch (mode) {
      case AudioManager.RINGER_MODE_NORMAL:
        ring_mode_radio_group_.check(R.id.normal_radio_button);
        break;
      case AudioManager.RINGER_MODE_VIBRATE:
        ring_mode_radio_group_.check(R.id.vibrate_radio_button);
        break;
      case AudioManager.RINGER_MODE_SILENT:
        ring_mode_radio_group_.check(R.id.silent_radio_button);
        break;
      default:
        ring_mode_radio_group_.check(R.id.normal_radio_button);
        break;
    }
  }

  private void SetButtonResource() {
    Button button = (Button)findViewById(R.id.refresh_button);
    button.setOnClickListener(this);
    reflesh_button_ = button;
  }

  private void UpdateResourceWithCurrentSoundSource() {

    // シークバー更新
    SetSeekBarWithSoundValue();
    // ラジオボタン更新
    SetRadioButtonWithRingerMode();
    // 設定データを今のオーディオボリュームで上書き(つまりシステムボリュームを優先)
    SetCurrentSoundToSettingData();
  }

  // resource_id に text を設定する
  private void SetTextView(int resource_id, String text) {
    TextView text_view;
    text_view = (TextView)findViewById(resource_id);
    text_view.setText(text);
  }

  private void UpdateVolumeText(int type) {
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    switch (type) {
      case TYPE_ALARM:
        SetTextView(R.id.alarm_text, getResources().getString(R.string.alarm_display_name) + " " + MhSoundUtil.GetAlarmVolume(audio_manager) + "/" + MhSoundUtil.GetAlarmMaxVolume(audio_manager));
        break;
      case TYPE_MUSIC:
        SetTextView(R.id.music_text, getResources().getString(R.string.music_display_name) + " " + MhSoundUtil.GetMusicVolume(audio_manager) + "/" + MhSoundUtil.GetMusicMaxVolume(audio_manager));
        break;
      case TYPE_DTMF:
        //SetTextView(R.id.dtmf_text, getResources().getString(R.string.dtmf_display_name) + " " + MhSoundUtil.GetDtmfVolume(audio_manager) + "/" + MhSoundUtil.GetDtmfMaxVolume(audio_manager));
        break;
      case TYPE_NOTIFICATION:
        SetTextView(R.id.notification_text, getResources().getString(R.string.notification_display_name) + " " + MhSoundUtil.GetNotificationVolume(audio_manager) + "/" + MhSoundUtil.GetNotificationMaxVolume(audio_manager));
        break;
      case TYPE_RING:
        SetTextView(R.id.ring_text, getResources().getString(R.string.ring_display_name) + " " + MhSoundUtil.GetRingVolume(audio_manager) + "/" + MhSoundUtil.GetRingMaxVolume(audio_manager));
        break;
      case TYPE_SYSTEM:
        SetTextView(R.id.system_text, getResources().getString(R.string.system_display_name) + " " + MhSoundUtil.GetSystemVolume(audio_manager) + "/" + MhSoundUtil.GetSystemMaxVolume(audio_manager));
        break;
      case TYPE_VOICE_CALL:
        SetTextView(R.id.voice_call_text, getResources().getString(R.string.voice_call_display_name) + " " + MhSoundUtil.GetVoiceCallVolume(audio_manager) + "/" + MhSoundUtil.GetVoiceCallMaxVolume(audio_manager));
        break;
      default:
        break;
    }
  }

  private void UpdateVolumeTextAll() {
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int i = 0;
    for (i = 0; i < TYPE_MAX_NUMBER; ++i) {
      UpdateVolumeText(i);
    }
  }

  private void UpdateAllDisplayParameter() {
    if (IsSeekbarValid() == true) {
      // シークバーにデフォルトパラメータを設定
      SetSeekBarWithSoundValue();
    }
    if (IsRadioGroupValid() == true) {
      // RingerModeでラジオボタンの状態を設定
      SetRadioButtonWithRingerMode();
    }

    // すべてのボリュームテキストを更新
    UpdateVolumeTextAll();
  }

  private boolean IsSeekbarValid() {
    if (alarm_seekbar_ == null || dtmf_seekbar_ == null || music_seekbar_ == null
        || notification_seekbar_ == null || ring_seekbar_ == null
        || system_seekbar_ == null || voice_call_seekbar_ == null) {
      // 無効なオブジェクトがある
      return false;
    }
    return true;
  }

  private boolean IsRadioGroupValid() {
    if (ring_mode_radio_group_ == null) {
      // 無効なオブジェクトがある
      return false;
    }
    return true;

  }

  // サウンド設定データの初期化
  private void InitializeSoundSettingData() {
    setting_data_ = new SoundSettingData();
    setting_data_.Initialize();
    past_setting_data_ = new SoundSettingData();
    past_setting_data_.Initialize();
  }

  // 現在のサウンドモードを記録する
  private void SetCurrentSoundToSettingData() {
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int mode = MhSoundUtil.GetRingerMode(audio_manager);
    switch (mode) {
      case AudioManager.RINGER_MODE_NORMAL:
        setting_data_.GetNormalVolume().SetWithAudioManager(audio_manager);
        break;
      case AudioManager.RINGER_MODE_VIBRATE:
        setting_data_.GetVibrateVolume().SetWithAudioManager(audio_manager);
        break;
      case AudioManager.RINGER_MODE_SILENT:
        setting_data_.GetSilentVolume().SetWithAudioManager(audio_manager);
        break;
      default:
        break;
    }
  }

  // サウンド設定データから実際のデータに適用する
  private void ApplySettingDataToAudioManager() {
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int mode = MhSoundUtil.GetRingerMode(audio_manager);
    switch (mode) {
      case AudioManager.RINGER_MODE_NORMAL:
        // 正常なサウンドが設定されているなら適用
        if (setting_data_.GetNormalVolume().IsInvalid() == false) {
          setting_data_.GetNormalVolume().ApplyAudioManager(audio_manager);
        }
        break;
      case AudioManager.RINGER_MODE_VIBRATE:
        if (setting_data_.GetVibrateVolume().IsInvalid() == false) {
          setting_data_.GetVibrateVolume().ApplyAudioManager(audio_manager);
        }
        break;
      case AudioManager.RINGER_MODE_SILENT:
        if (setting_data_.GetSilentVolume().IsInvalid() == false) {
          setting_data_.GetSilentVolume().ApplyAudioManager(audio_manager);
        }
        break;
      default:
        break;
    }
  }

  // サウンド系の無効を適用(音のなる系なので通話ボリュームは未設定)
  private void ApplyDisableSound() {
    AudioManager audio_manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    int mode = MhSoundUtil.GetRingerMode(audio_manager);
    switch (mode) {
      case AudioManager.RINGER_MODE_NORMAL:
        setting_data_.GetNormalVolume().set_alarm_volume(0);
        setting_data_.GetNormalVolume().set_music_volume(0);
        setting_data_.GetNormalVolume().set_notification_volume(0);
        setting_data_.GetNormalVolume().set_ring_volume(0);
        setting_data_.GetNormalVolume().set_system_volume(0);
        break;
      case AudioManager.RINGER_MODE_VIBRATE:
        setting_data_.GetVibrateVolume().set_alarm_volume(0);
        setting_data_.GetVibrateVolume().set_music_volume(0);
        setting_data_.GetVibrateVolume().set_notification_volume(0);
        setting_data_.GetVibrateVolume().set_ring_volume(0);
        setting_data_.GetVibrateVolume().set_system_volume(0);
        break;
      case AudioManager.RINGER_MODE_SILENT:
        setting_data_.GetSilentVolume().set_alarm_volume(0);
        setting_data_.GetSilentVolume().set_music_volume(0);
        setting_data_.GetSilentVolume().set_notification_volume(0);
        setting_data_.GetSilentVolume().set_ring_volume(0);
        setting_data_.GetSilentVolume().set_system_volume(0);
        break;
      default:
        break;
    }
  }

  private void CopyPastSettingData() {
    if (setting_data_ == null) {
      return;
    } else if (past_setting_data_ == null) {
      return;
    }
    past_setting_data_.Copy(setting_data_);
  }

  // サウンド設定をJsonに変換 + セーブ
  private boolean Save() {
    if (save_active_flag_ == true) {
      MhUtil.Print("MainActivity::Save executing");
      return false;
    }
    save_active_flag_ = true; // 念の為にスレッドなどでセーブ中にセーブが来ないためにフラグを付ける

    if (setting_data_ == null) {
      save_active_flag_ = false;
      return false;
    }

    boolean result = true;
    try {
      // セーブ
      save_manager_ = new SaveManager();
      save_manager_.InitializeSave();
      save_manager_.SaveSettingData(this, setting_data_);
      save_manager_ = null;
      // パラメータの変化がわかるようにコピー
      CopyPastSettingData();
    } catch (OutOfMemoryError memoryError) {
      MhUtil.Print("MainActivity::Save memory " + memoryError);
      result = false;
    } catch (Exception e) {
      MhUtil.Print("MainActivity::Save e " + e);
      result = false;
    }
    save_active_flag_ = false;
    return result;
  }

  // ロード + サウンド設定に適用(システムサウンドには適用しない)
  private boolean Load() {
    if (setting_data_ == null) {
      return false;
    }

    boolean result = true;
    try {
      // ロード
      save_manager_ = new SaveManager();
      save_manager_.InitializeLoad();
      result = save_manager_.LoadSettingData(this, setting_data_);
      save_manager_ = null;
      // パラメータの変化がわかるようにコピー
      CopyPastSettingData();

    } catch (OutOfMemoryError memoryError) {
      MhUtil.Print("MainActivity::Load memory " + memoryError);
      result = false;
    } catch (Exception e) {
      MhUtil.Print("MainActivity::Load e " + e);
      result = false;
    }
    return result;
  }

  private void TestSave() {
    setting_data_ = new SoundSettingData();
    setting_data_.Initialize();
    save_manager_ = new SaveManager();
    save_manager_.InitializeSave();
    save_manager_.SaveSettingData(this, setting_data_);
    //private SaveManager save_manager_ = null;

    // ファイル削除
    //MhFileUtil.DeleteFileListAllExcludeInstantRun(this);
    // 念の為にファイルリスト表示
    //MhFileUtil.PrintFileList(this);
  }

  // attribute
  private SeekBar alarm_seekbar_ = null;
  private SeekBar dtmf_seekbar_ = null;
  private SeekBar music_seekbar_ = null;
  private SeekBar notification_seekbar_ = null;
  private SeekBar ring_seekbar_ = null;
  private SeekBar system_seekbar_ = null;
  private SeekBar voice_call_seekbar_ = null;
  private RadioGroup ring_mode_radio_group_ = null;
  private Button reflesh_button_ = null;

  private static IntentFilter plugIntentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
  private static BroadcastReceiver plugStateChangeReceiver = null;
  /*
    private static IntentFilter plugIntentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
  private static BroadcastReceiver plugStateChangeReceiver = null;
   */

  private SoundSettingData setting_data_ = null;
  private SoundSettingData past_setting_data_ = null; // セーブするときの比較用
  private SaveManager save_manager_ = null;
  private boolean save_active_flag_ = false;
  private boolean is_headset_on = false;

}

