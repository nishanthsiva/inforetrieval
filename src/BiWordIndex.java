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
    private Map<String, Set<String>> postingsMap ;
    private Map<String, Set<String>> biWordDocumentMap;

    public BiWordIndex(String folderName){
        this.folder = folderName;
        this.filenames = getAllFileNames();
        this.postingsMap  = new HashMap<>();
        this.biWordDocumentMap = new HashMap<>();

    }

    public void buildIndex(){
        final String METHOD_NAME = "buildIndex";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);
        createTermSet();
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    private String[] getAllFileNames(){

        String[] filepaths = FileUtil.getFiles(folder).toArray(new String[1]);
        String[] filenames = new String[filepaths.length];
        for(int i=0;i<filepaths.length;i++){
            File file = new File(filepaths[i]);
            filenames[i] = file.getName();
            LOGGER.log(Level.FINE, filenames[i]);
        }

        return filenames;
    }

    public Set<String> getDocumentBiWords(String documentName){
        return this.biWordDocumentMap.get(documentName);
    }

    private void createTermSet(){
        final String METHOD_NAME = "createTermSet";

        LOGGER.log(Level.INFO,filenames.length+" files read!");
        for(String file: this.filenames){
            List<String> biWordList = FileUtil.getBiWordsFromFile(this.folder+File.separator+file);
            Set<String> biWordSet = new TreeSet<>();
            biWordSet.addAll(biWordList);
            this.biWordDocumentMap.put(file,biWordSet);
            for(int i=0;i<biWordList.size();i++){
                String term = biWordList.get(i);
                if(this.postingsMap.containsKey(term)){
                    this.postingsMap.get(term).add(file);
                }else{
                    Set<String> postingList = new TreeSet<>();
                    postingList.add(file);
                    this.postingsMap.put(term,postingList);
                }

            }
        }
        LOGGER.log(Level.INFO,"Finished REading ");
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    public List<String> postingList(String term){
        final String METHOD_NAME = "postingList";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        List<String> postingList = new ArrayList<>();
        postingList.addAll(this.postingsMap.get(term));
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
