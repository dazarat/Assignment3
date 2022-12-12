package task1;

import utils.PropertyReader;
import java.util.Properties;

public class Main {
    public static final int THREAD_AMOUNT = 1;

    public static void main(String[] args) {
        Properties properties = PropertyReader.readProperties("src/main/resources/name.properties");

        long time = System.currentTimeMillis();
        FinesProcessor.getFinesStatisticsWithThreads(properties.getProperty("task1.input"), properties.getProperty("task1.output"), THREAD_AMOUNT);
        long timeTaken = System.currentTimeMillis() - time;

        //if json is written in classic way algorithm is working much slower
//        long startTime = System.currentTimeMillis();
//        FinesProcessor.getFinesStatisticsWithThreads(properties.getProperty("task1.inputFolder"), properties.getProperty("task1.output"), THREAD_AMOUNT);
//        System.out.println(System.currentTimeMillis() - startTime);

        System.out.println("Processing when using " + THREAD_AMOUNT + " threads for reading folder took " + timeTaken + "ms.");
    }
}

