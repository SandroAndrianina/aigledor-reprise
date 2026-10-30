package synchronisation;

import synchronisation.utils.ConstanteSyncho;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class LogSynchro{
    public final static String FILE_PATH_STRING= ConstanteSyncho.LOGFILE;
    public static File log = new File(FILE_PATH_STRING);

    public static void println(String toWrite){
        System.out.println(toWrite);
        writeLog(toWrite);
    }

    public static void print(String toWrite, boolean ended){
        StringBuilder sb = new StringBuilder();
        sb.append('\r');
        sb.append(toWrite);
        System.out.flush();
        System.out.print(sb);
        if(ended){
            writeLog(toWrite.toString());
        }
    }

    public static void printProgress(long startTime, long total, long current) {
        long eta = current == 0 ? 0 :
                (total - current) * (System.currentTimeMillis() - startTime) / current;

        String etaHms = current == 0 ? "N/A" :
                String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(eta),
                        TimeUnit.MILLISECONDS.toMinutes(eta) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(eta) % TimeUnit.MINUTES.toSeconds(1));

        StringBuilder string = new StringBuilder(140);
        int percent = (int) (current * 100 / total);
        string
                .append('\r')
                //.append(String.join("", Collections.nCopies(percent == 0 ? 2 : 2 - (int) (Math.log10(percent)), " ")))
                .append(String.format(" %d%% [", percent))
                //.append(String.join("", Collections.nCopies(percent, "=")))
                .append('>')
                //.append(String.join("", Collections.nCopies(100 - percent, " ")))
                .append(']')
                //.append(String.join("", Collections.nCopies((int) (Math.log10(total)) - (int) (Math.log10(current)), " ")))
                .append(String.format(" %d/%d, ETA: %s", current, total, etaHms));
        System.out.flush();
        System.out.print(string);
    }

    private static void writeLog(String msg){
        Calendar c = Calendar.getInstance();
        int heure = c.get(Calendar.HOUR_OF_DAY);
        int minutes = c.get(Calendar.MINUTE);
        int secondes = c.get(Calendar.SECOND);

        String h = format(heure);
        String m = format(minutes);
        String s = format(secondes);
        String prefix = "<br />\n[" + h + ":" + m + ":" + s + "] (periodictask.SynchroBase):";
        FileWriter fw = null;
        try {
            fw = new FileWriter(log, true);
        } catch (IOException e1) {
            e1.printStackTrace();
            dialogErrorLog();
        }
        BufferedWriter output = new BufferedWriter(fw);

        try {
            output.write(prefix + msg + "\n");
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            dialogErrorLog();
        }
    }


    private static String format(int i){
        String a = Integer.toString(i);
        if(i < 10){
            a = 0 + a;
        }
        return a;
    }

    private static void dialogErrorLog(){
        System.out.println("Error found during sychronization...");
    }
}
