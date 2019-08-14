package main.java.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
    private static String LOG_PATH = System.getProperty("user.home")
            + File.separator + "Documents" + File.separator + "scdz" + File.separator + "wbs_log";

    public static void append(String... newlofinfo) {
        try {
            File logFile = getTodayLogFile();
            FileWriter mWriter = new FileWriter(logFile, true);
            mWriter.append("----------------")
                    .append(timeFormat.format(new Date()))
                    .append("----------------")
                    .append(System.getProperty("line.separator"));
            for (String s : newlofinfo) {
                mWriter.append(s);
                mWriter.append(System.getProperty("line.separator"));
            }
            mWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void checkFile() {
        File logParent = new File(LOG_PATH);
        if (!logParent.exists() || logParent.isFile())
            logParent.mkdirs();
    }

    private static File getTodayLogFile() throws IOException {
        checkFile();
        String logName = LOG_PATH + File.separator + dateFormat.format(new Date()) + ".log";
        File logFile = new File(logName);
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        return logFile;
    }
}
