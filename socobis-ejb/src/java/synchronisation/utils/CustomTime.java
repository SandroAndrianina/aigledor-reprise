package synchronisation.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomTime {
    private long days = 0;
    private long hours = 0;
    private long minutes = 0;
    private long seconds = 0;

    public CustomTime(){}

    public CustomTime(long days, long hours,long minutes, long seconds){
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public long toMillisecond(){
        long ret = TimeUnit.DAYS.toMillis(days);
        ret+= TimeUnit.HOURS.toMillis(hours);
        ret+= TimeUnit.MINUTES.toMillis(minutes);
        ret+= TimeUnit.SECONDS.toMillis(seconds);
        return ret;
    }

    public static CustomTime format(String value){
        CustomTime ret = new CustomTime();
        String[] array = value.split(":");
        for(String s : array){
            Pattern p = Pattern.compile("[0-9]+|[A-Za-z]+");
            Matcher m = p.matcher(s);
            List<String> matches = new ArrayList<>();
            while (m.find()) {
                matches.add(m.group());
            }
            switch(matches.get(1)){
                case "d": ret.setDays(Long.valueOf(matches.get(0))); break;
                case "h": ret.setHours(Long.valueOf(matches.get(0))); break;
                case "m": ret.setMinutes(Long.valueOf(matches.get(0))); break;
                case "s": ret.setSeconds(Long.valueOf(matches.get(0))); break;
            }
        }
        return ret;
    }

    public CustomTime fromMillisecond(long milliseconds){
        long s = milliseconds / 1000;
        long m = s / 60;
        long h = m / 60;
        long d = h / 24;
        seconds = s%60;
        minutes = m%60;
        hours = h%24;
        days = d%365;
        String time = d % 365 + ":" + h % 24 + ":" + m % 60 + ":" + s % 60;

        return this;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(long minutes) {
        this.minutes = minutes;
    }

    public long getSeconds() {
        return seconds;
    }

    public void setSeconds(long seconds) {
        this.seconds = seconds;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }
}
