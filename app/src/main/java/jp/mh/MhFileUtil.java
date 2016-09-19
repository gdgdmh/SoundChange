package jp.mh;

import android.content.Context;

import java.io.File;

/**
 * Created by mh on 2016/04/25.
 */
public class MhFileUtil {

  // ファイル書き込み
  // Privateだとそのアプリしか操作できないらしい Appendは追加書き込み
  // file_nameに書き込むファイル名を指定
  public static boolean WriteBufferPrivate(Context context, String file_name, byte[] buffer) {
    return WriteBuffer(context, file_name, MhFileOutput.OPEN_MODE_PRIVATE, buffer);
  }
  public static boolean WriteBufferAppend(Context context, String file_name, byte[] buffer) {
    return WriteBuffer(context, file_name, MhFileOutput.OPEN_MODE_APPEND, buffer);
  }
  public static boolean WriteBufferPrivateAppend(Context context, String file_name, byte[] buffer) {
    return WriteBuffer(context, file_name, MhFileOutput.OPEN_MODE_PRIVATE_APPEND, buffer);
  }

  // ファイル読み込み
  // file_nameに読み込むファイル名を指定
  // 成功なら戻り値がnull以外になる
  public static byte[] ReadBuffer(Context context, String file_name) {
    byte[] buffer = null;
    boolean result = true;
    MhFileInput input = null;
    try {
      input = new MhFileInput();
      input.Initialize();
      buffer = input.ReadBuffer(context, file_name);
    } catch (OutOfMemoryError memoryError) {
      // メモリ不足
      MhUtil.Print("MhFileUtil::ReadBuffer memory e " + memoryError);
    } catch (Exception e) {
      // メモリ不足
      MhUtil.Print("MhFileUtil::ReadBuffer memory e " + e);
    } finally {
      input.Release();
      input = null;
    }
    return buffer;
  }

  public static boolean IsCurrentDirectoryFileExist(Context context, String file_name) {
    File file = context.getFileStreamPath(file_name);
    return file.exists();

    /*
File file = this.getFileStreamPath("test.txt");
boolean isExists = file.exists();
    */
    /*
String path = (new StringBuffer()).append(getFilesDir()).append("/").append("sample.txt").toString();
Log.d("path", path);

File file = new File(path);
if (file.exists()) {
  Log.d("file", "file exists");
}
     */

  }

  // ファイル削除
  public static boolean DeleteFile(Context context, String file_name) {
    return context.deleteFile(file_name);
  }

  // すべてのファイルリストを削除(ただし、インスタントランを使用しているときには不具合が起きる)
  public static boolean DeleteFileListAll(Context context) {
    // ファイルリストを取得してすべてのファイルを削除
    String[] file_lists = context.fileList();
    int size = file_lists.length;
    for (int i = 0; i < size; ++i) {
      if (DeleteFile(context, file_lists[i]) == false) {
        return false;
      }
    }
    return true;
  }

  // すべてのファイルリストを削除(ただし、インスタントランは除外)
  public static boolean DeleteFileListAllExcludeInstantRun(Context context) {
    // ファイルリストを取得してすべてのファイルを削除
    String[] file_lists = context.fileList();
    int size = file_lists.length;
    for (int i = 0; i < size; ++i) {
      if (file_lists[i].equals("instant-run") == true) {
        // instant-runのファイルは除外
      } else {
        if (DeleteFile(context, file_lists[i]) == false) {
          return false;
        }
      }
    }
    return true;
  }

  public static String[] GetFileList(Context context) {
    return context.fileList();
  }

  public static void PrintFileList(Context context) {
    String[] file_lists = GetFileList(context);
    int size = file_lists.length;
    if (size <= 0) {
      MhUtil.Print("PrintFileList file none");
      return;
    }
    for (int i = 0; i < size; ++i) {
      MhUtil.Print("PrintFileList:" + file_lists[i]);
    }
  }

  // private ---------------------

  private static boolean WriteBuffer(Context context, String file_name, int open_mode, byte[] buffer) {

    boolean result = true;
    MhFileOutput out = null;
    if (buffer == null) {
      return false;
    }
    try {
      out = new MhFileOutput();

      out.Initialize();

      if (out.WriteBuffer(context, file_name, open_mode, buffer) == false) {
        result = false;
      }
    } catch (OutOfMemoryError memoryError) {
      // メモリ不足
      MhUtil.Print("MhFileUtil::WriteBuffer memory e " + memoryError);
      result = false;
    } catch (Exception e) {
      MhUtil.Print("MhFileUtil::WriteBuffer e " + e);
      result = false;
    } finally {
      out.Release();
      out = null;
    }
    return result;
  }


}
