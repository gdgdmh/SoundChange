package jp.mh;

import android.content.Context;
import android.sax.RootElement;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by mh on 2016/04/14.
 */
public class MhFileInput {

  public static final int RESULT_SUCCESS = 0;
  public static final int RESULT_OPEN_ERROR = 1;
  public static final int RESULT_AVAILABLE_ERROR = 2;
  public static final int RESULT_BUFFER_CREATE_ERROR = 3;

  public void Initialize() {
  }

  public void Release() {
    CloseFile();
  }

  // object
  @Override
  protected void finalize() {
    try {
      super.finalize();
      CloseFile();
    } catch (Throwable throwable) {
      MhUtil.Print("MhFileOutput::finalize t" + throwable);
    }
  }


  // ファイルオープン(resultはRESULT_～)
  public byte[] ReadBuffer(Context context, String file_name) {
    // ファイルオープン
    if (OpenFile(context, file_name) == false) {
      return null;
    }

    // サイズを取得
    int size = Available();
    if (size < 0) {
      return null;
    }

    // バッファ読み込み
    byte[] buffer = null;
    try {
      buffer = new byte[size];
    } catch (Exception e) {
      MhUtil.Print("MhFileInput::Readbuffer " + e);
      Release();
      return null;
    }

    try {
      input_stream_.read(buffer);
    } catch (IOException e) {
      MhUtil.Print("MhFileInput::Readbuffer " + e);
    }

    CloseFile();

    return buffer;
  }

  private boolean OpenFile(Context context, String file_name) {
    // 2重に開かれないようにcloseをしておく
    CloseFile();

    boolean is_success = true;
    try {
      input_stream_ = context.openFileInput(file_name);
    } catch (Exception e) {
      is_success = false;
      input_stream_ = null;
      MhUtil.Print("MhFileInput::OpenFile " + e);
    }
    return is_success;
  }

  private boolean CloseFile() {
    boolean is_success = true;
    if (input_stream_ != null) {
      try {
        input_stream_.close();
      } catch (IOException e) {
        // エラー(特に問題ないと思うのでエラーがあったことだけ返す)
        is_success = false;
        MhUtil.Print("MhFileInput::CloseFile " + e);
      }
      //
      input_stream_ = null;
    }
    return is_success;
  }

  /**
   *
   * @return int input_streamが無効な状態なら-1
   */
  private int Available() {
    if (input_stream_ == null) {
      return -1;
    }
    int size = -1;
    try {
      size = input_stream_.available();
    } catch (IOException e) {
      MhUtil.Print("MhFileInput::Available " + e);
      e.printStackTrace();
    }
    return size;
  }

  private FileInputStream input_stream_ = null;
}
