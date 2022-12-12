package task1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
//class for processing json and counting fine amount sum by every type of violation by all years
public class FinesProcessor {
    //regex for JSON object
    private static final String JSON_REGEX = " *\\{\n" +
                                             "* *(\"\\S*\" *: *\"\\S* \\S*\",*)\n" +
                                             "* *(\"\\S*\" *: *\"\\S*\",*)\n" +
                                             "* *(\"\\S*\" *: *\"\\S*\",*)\n" +
                                             "* *(\"\\S*\" *: *\"\\S*\",*)\n" +
                                             "* *(\"\\S*\" *: *\\d*.\\d\\d)\n" +
                                             "* *}";

    //mappers to read json and write xml, lock for thread-safety
    private static final ObjectMapper OBJECT_MAPPER_DEFAULT;
    private static final XmlMapper XML_MAPPER;
    private static final ReentrantLock LOCK;
    //initializing static fields
    static {
        OBJECT_MAPPER_DEFAULT = new ObjectMapper();
        XML_MAPPER = new XmlMapper();
        XML_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
        LOCK = new ReentrantLock();
    }
    //returns all files in folder
    private static List<File> getFilesListFromFolder(final File folder) {
        ArrayList<File> filesInFolder = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory())
                getFilesListFromFolder(fileEntry);
            else
                filesInFolder.add(new File(folder.getPath() + "\\" + fileEntry.getName()));
        }
        return filesInFolder;
    }
    //reads statistic from single json file in map from args
    private static void readFile(File file, Map<String,Double> resultMap){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file))))
        {
            //buffer for reading lines from file and regex for finding json object
            StringBuilder stringBuilder = new StringBuilder();
            Pattern jsonFinePattern = Pattern.compile(JSON_REGEX);
            Matcher jsonFineMatcher;
            String currentLine = reader.readLine();
            while (currentLine!=null){
                stringBuilder.append(currentLine);
                jsonFineMatcher = jsonFinePattern.matcher(stringBuilder);
                if (jsonFineMatcher.find()){
                    //getting violation type and fine amount
                    String jsonFineString = jsonFineMatcher.group();
                    JsonNode jsonNode = OBJECT_MAPPER_DEFAULT.readTree(jsonFineString);
                    String type = jsonNode.get("type").asText();
                    Double fineAmount = Double.parseDouble(jsonNode.get("fine_amount").asText());
                    //lock in order to make writing to map thread-safe
                    LOCK.lock();
                    try {
                        if (!resultMap.containsKey(type)) {
                            resultMap.put(type, fineAmount);
                        } else {
                            resultMap.put(type, resultMap.get(type) + fineAmount);
                        }
                    } finally {
                        LOCK.unlock();
                    }
                    stringBuilder = new StringBuilder();
                }
                //clearing buffer
                currentLine = reader.readLine();
            }
        } catch (IOException exception){
            throw new RuntimeException(exception);
        }
    }
    //reads statistic from folder with json files in map from args with amount of async threads from args
    private static void fillStatisticMapUsingAsyncThreads(List<File> filesInFolder, int threadsAmount, Map<String, Double> resultMap){
        ExecutorService executorService = Executors.newFixedThreadPool(threadsAmount);
        //latch has start value as files in folder, in order to make sure that every thread will finish process the file
        CountDownLatch latch = new CountDownLatch(filesInFolder.size());
        for (File file : filesInFolder){
            //every thread writes in result map from args, and then after thread finished latch value decreases by 1
            CompletableFuture.runAsync(()-> readFile(file, resultMap), executorService).thenAccept(e -> latch.countDown());
        }
        try {
            //main thread will wait until latch value = 0;
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }
    // writing sorted map entries to .xml file
    private static void writeSortedStatisticMapToXml(String outputFilePath, Map<String, Double> mapToXml) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath)))) {
            String xmlList = XML_MAPPER.writeValueAsString(mapToXml.entrySet().stream()
                    .sorted((e1, e2) -> -e1.getValue().compareTo(e2.getValue()))
                    .collect(Collectors.toList()))
                    .replaceAll("ArrayList", "ViolationStatistics");
            bufferedWriter.write(xmlList);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //main method to run the program
    public static void getFinesStatisticsWithThreads(String inputFolderPath, String outputFilePath, int threadAmount) {
        Map<String, Double> map = new HashMap<>();
        fillStatisticMapUsingAsyncThreads(getFilesListFromFolder(new File(inputFolderPath)), threadAmount, map);
        writeSortedStatisticMapToXml(outputFilePath, map);
    }
}

