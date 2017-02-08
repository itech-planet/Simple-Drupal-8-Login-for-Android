package net.iplanet.utils;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTime {
    public String getDateAsString() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd:MMMM:yyyy HH:mm:ss a");
        return simpleDateFormat.format(calendar.getTime());
    }
}
