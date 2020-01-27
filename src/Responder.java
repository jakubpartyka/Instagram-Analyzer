import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Responder extends Messenger implements Runnable {
    private InstagramDriver driver;

    Responder(){
        driver = new InstagramDriver();
    }

    @Override
    public void run() {
        log("thread started");
        try {
            driver.logIn();
        } catch (InterruptedException e) {
            log(e.getMessage());
            e.printStackTrace();
        }

    }

    private static void log(String message) {
        //this function logs all actions performed by the program.
        if(Main.isLogToFile()){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("[DD-MM-YYYY-HH:mm:ss]: ");
                String ts = sdf.format(Calendar.getInstance().getTime());
                FileWriter fileWriter = new FileWriter("log.txt",true);
                fileWriter.write(ts + "[RSPNDR]" + message + "\n");
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("an error occurred while logging to file...");
                e.printStackTrace();
            }
        }
        if (Main.isLogToConsole())
            System.out.println(message);
    }
}
