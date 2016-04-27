import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 4/27/16.
 */
public class BiWordIndex {
    private static final Logger LOGGER = Logger.getLogger(BiWordIndex.class.getName());
    private static String CLASS_NAME = BiWordIndex.class.getName();

    private final String[] filenames;
    private String folder;
    private Map<String, Map<String,Integer>> postingsMap ;

    public BiWordIndex(String folderName){
        this.folder = folderName;
        this.filenames = getAllFileNames();
        this.postingsMap  = new HashMap<>();

    }

    public void buildIndex(){
        final String METHOD_NAME = "buildIndex";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);
        createTermSet();
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    private String[] getAllFileNames(){
        final String METHOD_NAME = "getAllFileNames";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        String[] filepaths = FileUtil.getFiles(folder).toArray(new String[1]);
        String[] filenames = new String[filepaths.length];
        for(int i=0;i<filepaths.length;i++){
            File file = new File(filepaths[i]);
            filenames[i] = file.getName();
            LOGGER.log(Level.FINE, filenames[i]);
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return filenames;
    }

    private void createTermSet(){
        final String METHOD_NAME = "createTermSet";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        LOGGER.log(Level.FINE,filenames.length+" files read!");
        for(String file: this.filenames){
            Map<String,Integer> termFreqMap = FileUtil.getBiWordsFromFile(this.folder+File.separator+file);

            Iterator<String> fileTermIterator = termFreqMap.keySet().iterator();
            while(fileTermIterator.hasNext()){
                String term = fileTermIterator.next();
                if(this.postingsMap.containsKey(term)){
                    this.postingsMap.get(term).put(file,termFreqMap.get(term));
                }else{
                    HashMap<String,Integer> docOccuranceMap = new HashMap<>();
                    docOccuranceMap.put(file,termFreqMap.get(term));
                    this.postingsMap.put(term,docOccuranceMap);
                }

            }
            LOGGER.log(Level.FINE,this.postingsMap+"");
        }
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    public List<String> postingList(String term){
        final String METHOD_NAME = "postingList";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        List<String> postingList = new ArrayList<>();
        Map<String,Integer> docOccuranceMap = this.postingsMap.get(term);
        if(docOccuranceMap != null) {
            for (String docName : docOccuranceMap.keySet()) {
                postingList.add(docName);
            }
        }

        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return postingList;
    }

    public void printPostingList(String term){
        final String METHOD_NAME = "printPostingList";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        System.out.println("Postings for "+term);
        term = term.replace(" ",",");
        List<String> postingList = postingList(term);
        if(postingList.size() > 0){
            for(String posting: postingList)
                System.out.println("[Document name = "+posting+"]");
        }else{
            System.out.println("Term not found!");
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    public static void main(String arg[]){
        BiWordIndex index = new BiWordIndex("pa4");
        index.buildIndex();
        index.printPostingList("ticket sales");
    }
}
