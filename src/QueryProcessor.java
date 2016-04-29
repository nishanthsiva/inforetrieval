import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	public void queryDocuments() {
		String query = null;
		Integer k = null;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Integer numDocs = wordIndex.getAllFileNames().length;
		while(true) {
			try {
	        System.out.print("Enter query : ");
			query = br.readLine();
	        System.out.print("Enter number of top documents to find :");
	        k = Integer.parseInt(br.readLine());
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
	        Map<String,Integer> queryTermOccurenceMap = new HashMap<>();
	        Map<String,Double> weightedDocumentTermMap = new HashMap<>();
	        Map<String,Double> weightedQueryTermMap = new HashMap<>();
	        Map<String,Double> logNbyDFTMap = new HashMap<>();
	        FileUtil.writeTermOccurenceToMap(queryTermOccurenceMap, query);
	        Map<String,Document> documentsContainingTerms = new HashMap<String,Document>();
	        Set<String> terms = queryTermOccurenceMap.keySet();
	        for(String term : terms) {
	        	List<PostingTuple> documentTuples = wordIndex.postingList(term);
	        	Double logNbyDFT = Math.log10(numDocs/documentTuples.size());
	        	logNbyDFTMap.put(term, logNbyDFT);
	        	double weightOfTermInQuery = log2(1 + queryTermOccurenceMap.get(term));
	        	weightedQueryTermMap.put(term, weightOfTermInQuery);
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
	        		
	        		doc.similarity += (weightOfTermInDocument * weightOfTermInQuery);
	        		documentsContainingTerms.put(docName,doc);
	        	}
	        }
	        Double lengthOfQueryVector = (double) 0;
	        for(String term : weightedQueryTermMap.keySet()) {
	        	Double weight = weightedQueryTermMap.get(term);
	        	lengthOfQueryVector += weight*weight;
	        }
	        lengthOfQueryVector = Math.sqrt(lengthOfQueryVector);
	        for(String documentName : documentsContainingTerms.keySet()) {
	        	// calculate cosine similarity with query vector
	        	Document document = documentsContainingTerms.get(documentName);
	        	Double lengthOfDocumentVector = findLengthOfDocumentVector(documentName,numDocs);
	        	document.similarity = (document.similarity) / (lengthOfQueryVector * lengthOfDocumentVector);
	        }
	        List<Document> documentsWithTerms = new ArrayList<Document>(documentsContainingTerms.values());
	        Collections.sort(documentsWithTerms);
	        documentsContainingTerms = new HashMap<String,Document>();
	        int endIndex = (2*k > numDocs) ? documentsWithTerms.size() : 2*k;
	        for (Document doc : documentsWithTerms.subList(0, endIndex)) {
	        	documentsContainingTerms.put(doc.fileName, doc);
	        }
	        Set<String> biWordsFromQuery = FileUtil.getBiWords(query);
	        for (String biWord : biWordsFromQuery) {
	        	List<String> documentTuples = biWordIndex.postingList(biWord);
	        	for (String documentTuple : documentTuples) {
	        		if (documentsContainingTerms.containsKey(documentTuple)) {
	        			Document doc = documentsContainingTerms.get(documentTuple);
	        			doc.rank++;
	        		}
	        	}
	        }
	        documentsWithTerms = new ArrayList<Document>(documentsContainingTerms.values());
	        Collections.sort(documentsWithTerms, new Comparator<Document>() {

				@Override
				public int compare(Document o1, Document o2) {
					if(o1.rank > o2.rank) {
						return -1;
					} else if (o1.rank < o2.rank) {
						return 1;
					} else {
						return o1.compareSimilarities(o2);
					}
				}
			});
	        endIndex = (k > numDocs) ? documentsWithTerms.size() : k;
	        for(Document doc : documentsWithTerms.subList(0, endIndex)) {
	        	System.out.println(doc.fileName + " - " + doc.similarity);
	        }
		}
	}
	
	private Double findLengthOfDocumentVector(String documentName, Integer numDocs) {
		Double lengthSquared = (double) 0;
		for(String term : wordIndex.getDocumentTerms(documentName)) {
			Double weight = (double) 0;
			List<PostingTuple> documentTuples = wordIndex.postingList(term);
			for (PostingTuple tuple : documentTuples) {
				if (tuple.getDocumentName().equals(documentName)){
					weight = log2(1 + tuple.getTermOccurance()) * Math.log10(numDocs/documentTuples.size());;
					break;
				}
			}
			lengthSquared += weight * weight;
		}
		return Math.sqrt(lengthSquared);
	}

	class Document implements Comparable<Document>{
		double similarity = 0.0f;
		String fileName;
		Integer termOccurence;
		Integer rank = 0;
		
		Document(String fileName) {
			this.fileName = fileName;
		}
		
		Document(String fileName, Integer termOccurence){
			this(fileName);
			this.termOccurence = termOccurence;
		}

		@Override
		public boolean equals(Object obj) {
			return ((Document)obj).fileName.equals(this.fileName);
		}
		
		private int compareSimilarities(Document o2) {
			if (this.similarity == o2.similarity) {
				return 0;
			} else if (this.similarity > o2.similarity) {
				return -1;
			} else {
				return 1;
			}
		}

		@Override
		public int compareTo(Document o2) {
			return compareSimilarities(o2);
		}
		
		
	}
	
	private double log2(Integer a) {
		return Math.log(a)/log2;
	}
	
	public static void main(String[] args) {
		QueryProcessor queryProcessor = new QueryProcessor("pa4");
		queryProcessor.queryDocuments();
	}
}
