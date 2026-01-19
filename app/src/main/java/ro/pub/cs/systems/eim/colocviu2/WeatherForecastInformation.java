
package ro.pub.cs.systems.eim.colocviu2;

public class WeatherForecastInformation {
    // gestiunea obiectului local cu informațiile meteorologice solicitate anterior
    private String temperature;
    private String windSpeed;
    private String condition;
    private String pressure;
    private String humidity;

    public String getTemperature() { return temperature; }
    public void setTemperature(String temperature) { this.temperature = temperature; }

    public String getWindSpeed() { return windSpeed; }
    public void setWindSpeed(String windSpeed) { this.windSpeed = windSpeed; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getPressure() { return pressure; }
    public void setPressure(String pressure) { this.pressure = pressure; }

    public String getHumidity() { return humidity; }
    public void setHumidity(String humidity) { this.humidity = humidity; }
}
