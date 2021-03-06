package etienned.lecteuropus;
/**
 * Created by etienned on 10/6/16.
 */

import android.util.SparseIntArray;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {


    public static final String TAG = "Util";

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();


    private Utils(){
        // do nothing.
    }

    public final static SparseIntArray m_listBus = new SparseIntArray();

    static
    {
        // ID Number, Real Number RTC
        m_listBus.put(95, 800);
        m_listBus.put(96, 801);
        m_listBus.put(172, 803);
        m_listBus.put(61, 807);
        m_listBus.put(16, 28);
        m_listBus.put(78, 330);
        m_listBus.put(41, 74);
        m_listBus.put(84, 350);
        m_listBus.put(12, 18);
        m_listBus.put(47, 87);
        m_listBus.put(39, 77);
        m_listBus.put(88, 377);
        m_listBus.put(7, 11);
        m_listBus.put(1, 1);
        m_listBus.put(3, 4);
        m_listBus.put(2, 3);
        m_listBus.put(50, 19);
        m_listBus.put(37, 74);

        // 89 --> L3 (Levis)
    }
   public static Calendar intToDateTime(int p_days, int p_mins){
       Calendar result = Calendar.getInstance();
       result.set(1997,Calendar.JANUARY ,1,0,0,0);
       result.add(Calendar.DATE, p_days);
       result.add(Calendar.MINUTE, p_mins);

       return result;
   }


    public static String TimeToString(Calendar p_time) {
        DateFormat df = new SimpleDateFormat("HH:mm");
        String reportTime = df.format(p_time.getTime());
        return reportTime;
    }


    public static String DateToString(Calendar p_date) {
//        if (Locale.getDefault() == Locale.ENGLISH){
//            df = new SimpleDateFormat("EEEE y LLLL d", Locale.ENGLISH);
//        }
//        else{
//            df = new SimpleDateFormat("EEEE 'le' d LLLL y", Locale.FRENCH);
//        }
        DateFormat day = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault());
        String reportDate = day.format(p_date.getTime());
        //reportDate = reportDate.substring(0, 1).toUpperCase() + reportDate.substring(1);
        return reportDate;
    }

    public static String DateToStringShort(Calendar p_date) {
        //DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        String reportDate = df.format(p_date.getTime());
        //reportDate = reportDate.substring(0, 1).toUpperCase() + reportDate.substring(1);
        return reportDate;
    }


    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static int bytesToInt(byte[] data, int indexFirstBit, int length) {
        int currentByte = indexFirstBit / 8; // First
        int firstBit = indexFirstBit % 8;
        int lastByte = (indexFirstBit + length - 1) / 8;
        int lastBit = (indexFirstBit + length - 1) % 8;
        int result = 0;
        int mask = 0;
        int offset = 0;

        while( currentByte < lastByte){
            offset = 8 * (lastByte - currentByte - 1) + lastBit + 1;
            mask = 0xFF >> firstBit;
            result += (data[currentByte] & mask) <<  offset;
            firstBit = 0;
            currentByte += 1;
        }

        mask = 0xFF >> firstBit;
        result += (data[currentByte] & mask) >> (7 - lastBit);
        return result;

    }

}

