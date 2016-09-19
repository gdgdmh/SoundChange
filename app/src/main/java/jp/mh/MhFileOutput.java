package jp.mh;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mh on 2016/04/14.
 */
public class MhFileOutput {

  public static final int OPEN_MODE_PRIVATE = 0;
  public static final int OPEN_MODE_APPEND = 1;
  public static final int OPEN_MODE_PRIVATE_APPEND = 2;

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

  /*
  // ファイルオープン(resultはRESULT_～)
  public byte[] ReadBuffer(Context context, String file_name) {
    return null;
  }
  */

  public boolean WriteBuffer(Context context, String file_name, int open_mode, byte[] buffer) {
    if (OpenFile(context, file_name, open_mode) == false) {
      return false;
    }

    boolean is_success = true;
    try {
      output_stream_.write(buffer);
    } catch (IOException e) {
      is_success = false;
      MhUtil.Print("MhFileOutput::WriteBuffer " + e);
    }
    return is_success;
  }

  private boolean OpenFile(Context context, String file_name, int open_mode) {
    CloseFile();

    boolean is_success = true;
    try {
      int mode = 0;
      switch (open_mode) {
        case OPEN_MODE_PRIVATE:
          mode = Context.MODE_PRIVATE;
          break;
        case OPEN_MODE_APPEND:
          mode = Context.MODE_APPEND;
          break;
        case OPEN_MODE_PRIVATE_APPEND:
          mode = Context.MODE_PRIVATE | Context.MODE_APPEND;
          break;
        default:
          mode = Context.MODE_PRIVATE;
          break;
      }
      output_stream_ = context.openFileOutput(file_name, mode);
    } catch (Exception e) {
      is_success = false;
      output_stream_ = null;
      MhUtil.Print("MhFileOutput::OpenFile " + e);
    }
    return is_success;
  }


  private boolean CloseFile() {
    boolean is_success = true;
    if (output_stream_ != null) {
      try {
        output_stream_.close();
      } catch (IOException e) {
        // エラー(特に問題ないと思うのでエラーがあったことだけ返す)
        is_success = false;
        MhUtil.Print("MhFileOutput::CloseFile " + e);
      }
      //
      output_stream_ = null;
    }
    return is_success;
  }

  private FileOutputStream output_stream_ = null;
}
