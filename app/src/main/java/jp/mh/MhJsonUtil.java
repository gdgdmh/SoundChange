package jp.mh;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;

/**
 * Created by mh on 2016/04/11.
 */
public class MhJsonUtil {

  public void Initialize() {
    json_object = new JSONObject();
    //json_array = new JSONArray();
  }

  public void Release() {
    json_object = null;
    json_array = null;
  }

  // java.lang.Object
  @Override
  public String toString() {
    if (json_object == null) {
      return "json null";
    }
    return json_object.toString();
  }

  public boolean InitializeWithBuffer(byte[] buffer) {
    // 既存のものはクリア
    json_object = null;
    json_array = null;

    try {
      String json_string = new String(buffer);
      json_object = new JSONObject(json_string);
    } catch (Exception e) {
      json_object = null;
      MhUtil.Print("MhJsonUtil::InitializeWithBuffer " + e);
    }
    // nullかどうかで成功を判断
    if (json_object != null) {
      return true;
    } else {
      return false;
    }
  }

  public byte[] GetBytes() {
    if (json_object == null) {
      return null;
    }
    return json_object.toString().getBytes();
  }

  public  boolean AddDataBoolean(String name, boolean add_data) {
    boolean is_success = true;
    if (json_object == null) {
      return false;
    }
    try {
      //JSONObject json_data = new JSONObject();
      //json_data.put(name, add_data);
      json_object.put(name, add_data);
    } catch (JSONException e) {
      is_success = false;
      MhUtil.Print("MhJsonUtil::AddDataBoolean " + e);
    }
    return is_success;
  }

  public boolean AddDataInt(String name, int add_data) {
    boolean is_success = true;
    if (json_object == null) {
      return false;
    }
    try {
      json_object.put(name, add_data);
    } catch (JSONException e) {
      is_success = false;
      MhUtil.Print("MhJsonUtil::AddDataInt " + e);
    }
    return is_success;
  }

  public boolean AddDataLong(String name, long add_data) {
    boolean is_success = true;
    if (json_object == null) {
      return false;
    }
    try {
      json_object.put(name, add_data);
    } catch (JSONException e) {
      is_success = false;
      MhUtil.Print("MhJsonUtil::AddDataLong " + e);
    }
    return is_success;
  }

  public boolean AddDataDouble(String name, long add_data) {
    boolean is_success = true;
    if (json_object == null) {
      return false;
    }
    try {
      json_object.put(name, add_data);
    } catch (JSONException e) {
      is_success = false;
      MhUtil.Print("MhJsonUtil::AddDataDouble " + e);
    }
    return is_success;
  }

  public boolean AddDataString(String name, String add_data) {
    boolean is_success = true;
    if (json_object == null) {
      return false;
    }
    try {
      json_object.put(name, add_data);
    } catch (JSONException e) {
      is_success = false;
      MhUtil.Print("MhJsonUtil::AddDataString " + e);
    }
    return is_success;
  }

  public boolean GetDataBoolean(String name) throws JSONException, IllegalStateException {
    if (json_object == null) {
      throw new IllegalStateException("json_object == null");
    }
    return json_object.getBoolean(name);
  }

  public int GetDataInt(String name) throws JSONException, IllegalStateException {
    if (json_object == null) {
      throw new IllegalStateException("json_object == null");
    }
    return json_object.getInt(name);
  }

  public long GetDataLong(String name) throws JSONException, IllegalStateException {
    if (json_object == null) {
      throw new IllegalStateException("json_object == null");
    }
    return json_object.getLong(name);
  }

  public double GetDataDouble(String name) throws JSONException, IllegalStateException {
    if (json_object == null) {
      throw new IllegalStateException("json_object == null");
    }
    return json_object.getDouble(name);
  }

  public String GetDataString(String name) throws JSONException {
    if (json_object == null) {
      throw new IllegalStateException("json_object == null");
    }
    return json_object.getString(name);
  }

  private JSONObject json_object = null;
  private JSONArray json_array = null;
}

