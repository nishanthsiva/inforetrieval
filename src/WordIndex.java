import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 4/27/16.
 */
public class WordIndex {

    private static final Logger LOGGER = Logger.getLogger(WordIndex.class.getName());
    private static String CLASS_NAME = WordIndex.class.getName();

    private final String[] filenames;
    private String folder;
    private HashMap<String, Set<String>> termMap;
    private HashMap<String, Set<String>> termDocumentMap;
    private int numTerms;
    private Map<String, Map<String,Integer>> postingsMap ;

    public WordIndex(String folderName){
        this.folder = folderName;
        this.filenames = getAllFileNames();
        this.termMap = new HashMap<>();
        this.termDocumentMap = new HashMap<>();
        this.postingsMap  = new HashMap<>();

    }

    public void buildIndex(){
        final String METHOD_NAME = "buildIndex";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);
        createTermSet();
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
    }

    public String[] getAllFileNames(){
        final String METHOD_NAME = "getAllFileNames";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        String[] filepaths = FileUtil.getFiles(folder).toArray(new String[1]);
        String[] filenames = new String[filepaths.length];
        for(int i=0;i<filepaths.length;i++){
            File file = new File(filepaths[i]);
            filenames[i] = file.getName();
        }

        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
        return filenames;
    }

    public Set<String> getDocumentTerms(String documentName){
        return this.termDocumentMap.get(documentName);
    }

    private void createTermSet(){
        final String METHOD_NAME = "createTermSet";
        LOGGER.entering(CLASS_NAME, METHOD_NAME);

        LOGGER.log(Level.FINE,filenames.length+" files read!");
        for(String file: this.filenames){
            Map<String,Integer> termFreqMap = FileUtil.getFileTerms(this.folder+File.separator+file);
            this.termDocumentMap.put(file,termFreqMap.keySet());
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
        }
        LOGGER.exiting(CLASS_NAME, METHOD_NAME);
    }

    public List<PostingTuple> postingList(String term){
        final String METHOD_NAME = "postingList";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        List<PostingTuple> postingList = new ArrayList<>();
        Map<String,Integer> docOccuranceMap = this.postingsMap.get(term);
        if(docOccuranceMap != null) {
            for (String docName : docOccuranceMap.keySet()) {
                PostingTuple tuple = new PostingTuple(docName, docOccuranceMap.get(docName));
                postingList.add(tuple);
            }
        }

        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return postingList;
    }

    public void printPostingList(String term){
        final String METHOD_NAME = "printPostingList";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        System.out.println("PostingList for "+term);
        List<PostingTuple> postingTupleList = postingList(term);
        if(postingTupleList.size() > 0){
            for(PostingTuple postingTuple: postingTupleList)
                System.out.println("[Document name = "+postingTuple.getDocumentName()+", Term Occurrance = "+postingTuple.getTermOccurance()+"]");
        }else{
            System.out.println("Term not found!");
        }
        LOGGER.exiting(CLASS_NAME,METHOD_NAME);

    }

    public double weight(String term, String docName){
        final String METHOD_NAME ="weight";
        LOGGER.entering(CLASS_NAME,METHOD_NAME);

        double weight = 0;
        double part2 = Math.log10(getAllFileNames().length/this.postingsMap.get(term).size());
        double part1 = Math.log(1 + this.postingsMap.get(term).get(docName))/Math.log(2);
        weight = part1*part2;

        LOGGER.exiting(CLASS_NAME,METHOD_NAME);
        return weight;
    }

    public static void main(String arg[]){
        WordIndex index = new WordIndex("pa4");
        index.buildIndex();
        index.printPostingList("sales");
    }

}
