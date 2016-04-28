import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Collections;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Nishanth Sivakumar and Sriram Balasubramanian on 4/27/16.
 */
public class QueryProcessor {
	private WordIndex wordIndex;
	private BiWordIndex biWordIndex;
	
	private static double log2;
	
	public QueryProcessor(String path){
		wordIndex = new WordIndex(path);
        wordIndex.buildIndex();
        biWordIndex = new BiWordIndex(path);
        biWordIndex.buildIndex();
        log2 = Math.log(2);
	}
	
	public void queryDocuments() throws IOException{
		String query = null;
		Integer k = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Integer numDocs = wordIndex.getAllFileNames().length;
		while(true) {
	        System.out.print("Enter query : ");
			query = br.readLine();
	        System.out.print("Enter number of top documents to find :");
	        k = Integer.parseInt(br.readLine());
	        Map<String,Integer> queryTermOccurenceMap = new HashMap<>();
	       // Map<String,Double> weightedQueryTermMap = new HashMap<>();
	        Map<String,Double> weightedDocumentTermMap = new HashMap<>();
	        FileUtil.writeTermOccurenceToMap(queryTermOccurenceMap, query);
	        Map<String,Document> documentsContainingTerms = new HashMap<String,Document>();
	        Set<String> terms = queryTermOccurenceMap.keySet();
	        for(String term : terms) {
	        	//weightedQueryTermMap.put(term, log2(1 + queryTermOccurenceMap.get(term)));
	        	List<PostingTuple> documentTuples = wordIndex.postingList(term);
	        	Double logNbyDFT = Math.log10(numDocs/documentTuples.size());
	        	for(PostingTuple documentTuple : documentTuples) {
	        		String docName = documentTuple.getDocumentName();
	        		weightedDocumentTermMap.put(docName, (double) 0);
	        		Document doc = null;
	        		if (documentsContainingTerms.containsKey(docName)) {
	        			doc = documentsContainingTerms.get(docName);
	        		} else {
	        			doc = new Document(docName, documentTuple.getTermOccurance());
	        		}
	        		double weightOfTermInDocument = log2(1 + doc.termOccurence) * logNbyDFT;
	        		double weightOfTermInQuery = log2(1 + queryTermOccurenceMap.get(term));
	        		doc.similarity += (weightOfTermInDocument + weightOfTermInQuery);
	        		documentsContainingTerms.put(docName,doc);
	        	}
	        }
	        Integer sizeOfQueryVector = queryTermOccurenceMap.keySet().size();
	        for(String documentName : documentsContainingTerms.keySet()) {
	        	// calculate cosine similarity with query vector
	        	Document document = documentsContainingTerms.get(documentName);
	        	document.similarity = (document.similarity) / (sizeOfQueryVector + wordIndex.getDocumentTerms(documentName).size());
	        }
	        List<Document> documentsWithTerms = (List<Document>) documentsContainingTerms.values();
	        Collections.sort(documentsWithTerms);
	        List<Document> Top2kDocuments = documentsWithTerms.subList(0, 2*k);
	        List<String> biWordsFromQuery = FileUtil.getBiWordsFromFile("");
		}
	}
	
	class Document implements Comparable<Document>{
		float similarity = 0.0f;
		String fileName;
		Integer termOccurence;
		
		Document(String fileName, Integer termOccurence){
			this.fileName = fileName;
			this.termOccurence = termOccurence;
		}

		@Override
		public boolean equals(Object obj) {
			return ((Document)obj).fileName.equals(this.fileName);
		}

		@Override
		public int compareTo(Document o2) {
			if (this.similarity == o2.similarity) {
				return 0;
			} else if (this.similarity > o2.similarity) {
				return 1;
			} else {
				return -1;
			}
		}
		
		
	}
	
	private double log2(Integer a) {
		return Math.log(a)/log2;
	}
}
