import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 3/5/16.
 */
public class FileUtil {

    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    private static String CLASS_NAME = FileUtil.class.getName();


    public static List<String> getFiles(String directoryPath){
        final String METHOD_NAME = "getFiles";

        List<String> fileNames = new ArrayList<>();

        File dirNode = new File(directoryPath);
        if(dirNode.isDirectory()){
            for(File node: dirNode.listFiles()){
                if(node.isDirectory()){
                    fileNames.addAll(getFiles(node.getAbsolutePath()));
                }else{
                    fileNames.add(node.getAbsolutePath());
                }
            }
        }else{
            fileNames.add(dirNode.getAbsolutePath());
        }

        return fileNames;
    }

    public static List<String> getBiWordsFromFile(String filepath){

        List<String> biWordSet = new ArrayList<>();
        List<String> termList = new ArrayList<>();
        FileReader fileReader;
        try {
            fileReader = new FileReader(new File(filepath));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.replaceAll(":|;|\\.|,|'","").split(" ");
                for (String term : tokens) {
                    term = term.toLowerCase();
                    if (term.length() >= 3 && !term.equals("the")) {
                        termList.add(term);
                    }
                }
            }
            for(int i=0;i<termList.size()-1;i++){
                String term_1 = termList.get(i);
                String term_2 = termList.get(i+1);
                biWordSet.add(term_1+","+term_2);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.warning(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warning(e.getMessage());
        }

        return biWordSet;
    }

    public static Map<String,Integer> getFileTerms(String filepath){

        Map<String,Integer> termFreqMap = new HashMap<>();
        FileReader fileReader;
        try {
            fileReader = new FileReader(new File(filepath));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while(bufferedReader.ready()){
                String line = bufferedReader.readLine();
                String[] tokens = StringUtil.removePuncutation(line).split(" ");
                for(String term: tokens){
                    term = term.toLowerCase();
                    if(term.length() >= 3 && !term.equals("the")){
                        if(termFreqMap.containsKey(term)){
                            termFreqMap.put(term,termFreqMap.get(term)+1);
                        }else{
                            termFreqMap.put(term,1);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LOGGER.warning(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.warning(e.getMessage());
        }

        return termFreqMap;
    }


}
