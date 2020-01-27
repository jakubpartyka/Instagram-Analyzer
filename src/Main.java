import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

@SuppressWarnings("FieldCanBeLocal")
public class Main {
    private static boolean logToConsole = true;
    private static boolean logToFile = true;
    private static boolean headless;
    private static boolean responderIsOn;
    private static boolean directMessageReportIsOn;
    private static String scheduleFile = "schedule.txt";    //todo add this to preferences
    private static String login;
    private static String password;
    private static Responder responder;


    public static void main(String[] args) {
        //starting server
        log("server start");
        log("loading preferences from config file");
        if(loadPreferences())
            log("preferences loaded successfully");
        else {
            log("failed to load preferences from configuration file. Quitting");
            System.exit(1);
        }

        if (responderIsOn){
            responder = new Responder();
            Thread responderThread = new Thread(responder);
            responderThread.start();
        }

        while (true){
            Timer timer = new Timer(scheduleFile);
            timer.waitForSchedule();
            System.exit(1);

        }

        //log("server shutdown");   //todo uncomment
    }

    private static boolean loadPreferences() {
        //this method sets program variables accordingly to config.xml file
        //true is returned if preferences were loaded successfully
        //false is returned otherwise.
        try {
            logToConsole = Boolean.valueOf(getPreference("logToConsole"));
            logToFile = Boolean.valueOf(getPreference("logToFile"));
            headless= Boolean.valueOf(getPreference("headless"));
            responderIsOn = Boolean.valueOf(getPreference("responderIsOn"));
            directMessageReportIsOn = Boolean.valueOf("directMessageReportIsOn");
            login = getPreference("login");
            password = getPreference("password");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getPreference(String Key) throws IOException {
        Properties configFile = new Properties();

        InputStream f = new FileInputStream("config.xml");
        configFile.loadFromXML(f);
        f.close();

        return (configFile.getProperty(Key));
    }

    @SuppressWarnings("unused")
    private static void setPreference(String Key, String Value) {
        //this method saves specified preference to configuration file
        Properties configFile = new Properties();
        try {
            InputStream f = new FileInputStream("config.xml");
            configFile.loadFromXML(f);
            f.close();
        }
        catch(IOException ignored) {}

        configFile.setProperty(Key, Value);
        try {
            OutputStream f = new FileOutputStream("config.xml");
            configFile.storeToXML(f,"Configuration file");
        }
        catch(Exception ignored) {}
    }

    private static void log(String message) {
        //this function logs all actions performed by the program.
        if (logToConsole)
            System.out.println(message);
        if(logToFile){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("[DD-MM-YYYY-HH:mm:ss]: ");
                String ts = sdf.format(Calendar.getInstance().getTime());
                FileWriter fileWriter = new FileWriter("log.txt",true);
                fileWriter.write(ts + "[SERVER]" + message + "\n");
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("an error occurred while logging to file...");
                e.printStackTrace();
            }
        }
    }

    static boolean isLogToConsole() {
        return logToConsole;
    }

    static boolean isLogToFile() {
        return logToFile;
    }

    static boolean isHeadless() {
        return headless;
    }

    static String getLogin() {
        return login;
    }

    static String getPassword() {
        return password;
    }
}
