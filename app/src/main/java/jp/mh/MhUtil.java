package jp.mh;

/**
 * Created by mh on 15/09/21.
 */

// log
import android.util.Log;

import java.util.Calendar;

// ユーティリティー用クラス
public class MhUtil
{
  protected static final long kHourMilli = 1000 * 60 * 60;
  protected static final long kMinuteMilli = 1000 * 60;
  protected static final long kSecondMilli = 1000;

  /**
   * ログを出力する
   * @param message 出力するメッセージ
   */
  static public void Print(String message) {
    if ( !is_debug_ ) {
      // Debugでないときはログを表示しない(必要なら表示してもいい)
      return;
    }
    Log.d( "mh", message );
  }

  /**
   * デバッグモードかどうか
   * @return trueならデバッグモード
   */
  static public boolean get_is_debug() {return is_debug_;
  }

  /**
   * プログラム結果がtrueであることをチェック(デバッグモードのときのみ動作)
   * @param assertValue falseならassertが発生
   * @param message falseの際にログに出すメッセージ
   */
  static public void Assert( boolean assertValue, String message ) {
    if ( is_debug_ ) {
      if (!assertValue) {
        MhUtil.Print(message);
        throw new AssertionError();
      }
    }
  }
/*
static public boolean IsDebug()
{
    return is_debug_;
}
*/

  static public long GetUnixTimeMilli() { return System.currentTimeMillis(); }
  static public long GetUnitTimeMilli( long hour, long minute, long second )
  {
    long t = 0;
    t += hour * kHourMilli;
    t += minute * kMinuteMilli;
    t += second * kSecondMilli;
    return t;
  }

  static public long[] GetUnixTimeMilli2Time( long milli_time ) {

    long time[] = new long[ 4 ]; // 0:hour 1:minute 2:second 3:milli
    long milli = milli_time;
    // hour,minute,secを算出
    time[ 0 ] = milli / kHourMilli;
    milli -= time[ 0 ] * kHourMilli;

    time[ 1 ] = milli / kMinuteMilli;
    milli -= time[ 1 ] * kMinuteMilli;

    time[ 2 ] = milli / kSecondMilli;
    milli -= time[ 2 ] * kSecondMilli;

    time[ 3 ] = milli;
    return time;
  }

  static public long GetUnixTimeMilliAddTime( long unixTimeMilli, long hour, long minute, long second )
  {
    return ( unixTimeMilli + MhUtil.GetUnitTimeMilli(hour, minute, second) );
  }

  static public Calendar GetUnixTime2Calendar( long unixTimeMilli )
  {
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis( unixTimeMilli );
    return c;
  }

  // デバッグモードフラグ(trueならデバッグ)
  protected static boolean is_debug_ = true;
}
