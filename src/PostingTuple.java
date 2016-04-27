/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 4/27/16.
 */
public class PostingTuple {

    public PostingTuple(String documentName, int termOccurance){
        this.documentName = documentName;
        this.termOccurance = termOccurance;
    }

    private String documentName;

    public int getTermOccurance() {
        return termOccurance;
    }

    public void setTermOccurance(int termOccurance) {
        this.termOccurance = termOccurance;
    }

    private int termOccurance;

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }
}
