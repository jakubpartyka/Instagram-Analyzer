import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

class Timer {
    private final String [] daysOfWeek = {"monday","tuesday","wednesday","thursday","friday","saturday","sunday",};
    private String schedule;
    private String scheduledDay;
    private Calendar scheduledTime;

    Timer(String pathToScheduleFile){
        try {
            //reading the schedule file
            BufferedReader reader = new BufferedReader(new FileReader(new File(pathToScheduleFile)));
            schedule = reader.readLine();
            log("schedule file was read properly: " + schedule);

            //parse schedule
            boolean scheduleCorrect = parseSchedule();
            if(scheduleCorrect) {
                log("schedule parsed correctly");
                log("Timer initialized correctly");
            }
            else {
                log("failed to parse schedule");
                log("failed to initialize timer due to schedule formatting");
            }
        } catch (FileNotFoundException e) {
            log("file not found: " + new File(pathToScheduleFile).getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            log("other IOException was thrown. Printing stacktrace");
            e.printStackTrace();
        }
    }

    private boolean parseSchedule() {
        log("trying to parse schedule...");
        schedule = schedule.toLowerCase();
        AtomicReference<String> dayOfWeek = new AtomicReference<>("");
        AtomicBoolean hasDay = new AtomicBoolean(false);

        Arrays.stream(daysOfWeek).forEach(e -> {                       //check for days in the string
            if(schedule.contains(e)){
                hasDay.set(true);
                dayOfWeek.set(e);
            }
        });

        if(!hasDay.get()) {
            log("no day of week inside schedule detected");
            return false;                                               //given schedule does not have a day of week
        }

        scheduledDay = dayOfWeek.get();
        schedule = schedule.replace(dayOfWeek.get(),"");    //remove day of week from string
        schedule = schedule.replace(" ","");         //remove white spaces
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("HH:mm").parse(schedule));
            scheduledTime = calendar;
        } catch (ParseException e) {
            log("time not formatted properly. Please use HH:mm format");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    void waitForSchedule(){
        Calendar now = Calendar.getInstance();
        now.setTime(Calendar.getInstance().getTime());
        int currentDay = now.get(Calendar.DAY_OF_WEEK);
        currentDay -= 2;
        if(currentDay < 0)
            currentDay = 7 + currentDay;
        String dayString = daysOfWeek[currentDay];

        while (!dayString.equals(scheduledDay)){
            log("sleeping for 3 hours");
            try {
                Thread.sleep(3 * 60 * 60 * 1000 );      //sleep for 3 hours
            } catch (InterruptedException e) {
                log("sleeping failed. Halting execution");
                e.printStackTrace();
                System.exit(2);
            }

            //read the day again
            currentDay = now.get(Calendar.DAY_OF_WEEK);
            currentDay -= 2;
            if(currentDay < 0)
                currentDay = 7 + currentDay;
            dayString = daysOfWeek[currentDay];
        }

        //on the post day

        //calculating time between now and closes post time
        int diff;               //sleep time in seconds

        //setting scheduled time for sleep calculation
        int scheduledHour = scheduledTime.get(Calendar.HOUR);
        int scheduledMinute = scheduledTime.get(Calendar.MINUTE);

        //setting current time for sleep calculation
        int currentHour = now.get(Calendar.HOUR);
        int currentMinute = now.get(Calendar.MINUTE);

        //calculating sleep time
        int thisHourInMilis = currentHour * 60 + currentMinute;
        int scheduledTimeInMilis = scheduledHour * 60 + scheduledMinute;
        diff = scheduledTimeInMilis - thisHourInMilis;

        log("sleeping thread until next post time");

        //sleeping until next post time
        try {
            Thread.sleep(diff * 60 * 1000);
        } catch (InterruptedException e) {
            log("sleeping failed. Halting execution");
            e.printStackTrace();
            System.exit(2);
        }
        catch (IllegalArgumentException e){
            //timeout is negative
            try {
                Thread.sleep(3 * 60 * 60 * 1000);           //sleep 3 hours
            } catch (InterruptedException e1) {
                log("sleeping failed. Halting execution");
                System.exit(1);
            }
        }
    }


    private static void log(String message) {
        //this function logs all actions performed by the program.
        if(Main.isLogToFile()){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("[DD-MM-YYYY-HH:mm:ss]: ");
                String ts = sdf.format(Calendar.getInstance().getTime());
                FileWriter fileWriter = new FileWriter("log.txt",true);
                fileWriter.write(ts + " [Timer]" + message + "\n");
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
