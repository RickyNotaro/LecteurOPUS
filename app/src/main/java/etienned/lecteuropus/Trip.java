package etienned.lecteuropus;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by etienned on 10/6/16.
 */


public class Trip implements Serializable {

    public static final String TAG = "Trip";
    private Calendar m_DateTime;
    private boolean m_Transfer;
    private int m_operatorId;
    private int m_busId;
    private String m_busName;
    private String m_operatorName;
    private int m_logoId;

    public Trip(byte[] page0, byte[] page1, byte[] page2, boolean subscription) throws IllegalArgumentException { // Constructor transit for Mifare

        if (Arrays.equals(page0, Utils.HexStringToByteArray("00000000"))
                || Arrays.equals(page1, Utils.HexStringToByteArray("00000000"))) {
            throw new IllegalArgumentException("Empty Trip");
        } else {
            int days = Utils.bytesToInt(page0, 6, 14);
            int mins0 = Utils.bytesToInt(page0, 20, 11);
            int mins1 = Utils.bytesToInt(page1, 0, 11);
            if (mins0 != mins1){
                m_Transfer = !subscription;
            }
            else {
                m_Transfer = false;
            }
            m_DateTime = Utils.intToDateTime(days, mins1);

            // Bus number
            m_busId = Utils.bytesToInt(page1, 13, 7);
            m_operatorId = Utils.bytesToInt(page2, 8, 6);

        }
    }

    public Trip(byte[] transitData) throws IllegalArgumentException { // Constructor transit for Opus
        // Date and time
        byte[] arrayTempo = Arrays.copyOfRange(transitData, 0, 2);
        if (Arrays.equals(arrayTempo, Utils.HexStringToByteArray("0000"))){
            throw new IllegalArgumentException("Empty Trip");
        }
        int days = Utils.bytesToInt(transitData, 0, 14);
        int mins = Utils.bytesToInt(transitData, 14, 11);
        m_DateTime = Utils.intToDateTime(days, mins);

        m_busId  = Utils.bytesToInt(transitData, 94, 7);
        m_operatorId = Utils.bytesToInt(transitData, 64, 6);
    }

    public Calendar getDateTime(){
        return m_DateTime;
    }

    public int getBusId(){
        return m_busId;
    }

    public int getOperatorId(){
        return m_operatorId;
    }


    public boolean getTransfer() {
        return m_Transfer;
    }

    public int getLogoId(){ return m_logoId;}

    public String getBusName(){ return m_busName;}

    public void setBusFromXml(Context ctx){
        String node = "";
        String busFile = "";
        String logo = "";
        m_busName = "";
        XmlResourceParser operatorXml = ctx.getResources().getXml(R.xml.operators);
        try {
            int event = operatorXml.getEventType();
            outerloop:
            while (event != XmlPullParser.END_DOCUMENT){
                switch (event) {
                    case XmlPullParser.START_TAG:
                        node = operatorXml.getName();
                        if(node.equals("operator")) {
                            if (operatorXml.getAttributeValue(null, "id").equals("" + m_operatorId)) {
                                logo = operatorXml.getAttributeValue(null, "logo");
                                busFile = operatorXml.getAttributeValue(null, "file");
                                break outerloop;
                            }
                        }
                        break;
                }
                event = operatorXml.next();
            }
        } catch (Exception e) {
            Log.e("CardActivity", "Error parsing stations XML file: " + e.getMessage());
        }

        // Find bus in XML file
        if (!busFile.equals("")) {
            int xmlId = ctx.getResources().getIdentifier(busFile, "xml", ctx.getPackageName());
            XmlResourceParser busXml = ctx.getResources().getXml(xmlId);
            try {
                int event = busXml.getEventType();
                outerloop:
                while (event != XmlPullParser.END_DOCUMENT) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            node = busXml.getName();
                            if (node.equals("bus")) {
                                if (busXml.getAttributeValue(null, "id").equals("" + m_busId)) {
                                    m_busName = busXml.getAttributeValue(null, "name");
                                    logo = busXml.getAttributeValue(null, "logo");
                                    break outerloop;
                                }
                            }
                            break;
                    }
                    event = busXml.next();
                }
            } catch (Exception e) {
                Log.e("CardActivity", "Error parsing stations XML file: " + e.getMessage());
            }
        }
        if (m_busName.equals("")) m_busName = "unknown";
        m_logoId = R.mipmap.bus_logo;
        if (!logo.equals("")){
            m_logoId = ctx.getResources().getIdentifier(logo, "drawable", ctx.getPackageName());
        }
    }
}
