package ro.pub.cs.systems.eim.colocviu2;

public class Constants {
    public static final String TAG = "EIM";
    public static final boolean DEBUG = true;

    // API public JSON fără cheie:
    public static final String WEATHER_URL = "https://wttr.in/%s?format=j1";

    // Cheile pe care le alege userul în Spinner (trebuie să corespundă cu arrays.xml)
    public static final String TEMP = "temperature";
    public static final String WIND = "wind";
    public static final String COND = "condition";
    public static final String PRESS = "pressure";
    public static final String HUM = "humidity";
    public static final String ALL = "all";
}
