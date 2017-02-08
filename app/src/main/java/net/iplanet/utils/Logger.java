package net.iplanet.utils;
import android.util.Log;
import net.iplanet.drupal.v8.Tokens;

public class Logger {

    DateTime dt = new DateTime();

    public String SyslogTrace(String trace){
        String key = dt.getDateAsString() + " | " +  Tokens.APP_NAME  + " " + Tokens.APP_VERSION + " [TRACE]: ";
        String value = trace;
        Log.v(key, value);
        return key + value;
    }
    public String SyslogException(Exception ex){
        String key = dt.getDateAsString() + " | " +  Tokens.APP_NAME  + " " + Tokens.APP_VERSION + " [ERROR]: ";
        String value = ex.getMessage();
        Log.v(key, value);
        return key + value;
    }
}
