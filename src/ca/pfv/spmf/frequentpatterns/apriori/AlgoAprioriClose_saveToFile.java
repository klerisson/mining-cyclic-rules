package ca.pfv.spmf.frequentpatterns.apriori;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is an implementation of the Apriori algorithm as described by :
 * 
 *   Pasquier, N., Bastide, Y., Taouil, R., Lakhal, L., (1999). Efficient Mining of Association Rules using 
 *   Closed Itemset Lattices. Information Systems, Elsevier Science, 24(1), pages 25-46 .
 *   
 * and the original Apriori article:
 * 
 *   Agrawal R, Srikant R. "Fast Algorithms for Mining Association Rules", VLDB. Sep 12-15 1994, Chile, 487-99,
 * 
 * The Apriori algorithm finds all the frequents itemsets and their support
 * in a binary context.
 * 
 * @author Philippe Fournier-Viger, 2008
 */
public class AlgoAprioriClose_saveToFile {
	
	List<ItemsetApriori> lastLevelFrequentItemset =  new ArrayList<ItemsetApriori>();
	List<ItemsetApriori> currentLevelFrequentItemset =  new ArrayList<ItemsetApriori>();

	protected ContextApriori context;
	protected int k; // level
	
	private int itemsetCount;
	protected long startTimestamp;
	protected long endTimestamp;
	
	// write to file
	BufferedWriter writer = null;	
	

	public AlgoAprioriClose_saveToFile(ContextApriori context) {
		this.context = context;
	}

	public void runAlgorithm(double minsupp, String output) throws IOException {
		startTimestamp = System.currentTimeMillis();
		writer = new BufferedWriter(new FileWriter(output)); 
		itemsetCount=0;

		// Generate candidates of size k =1 (all itemsets of size)
		k=1;
		List<ItemsetApriori> candidatesSize1 = generateCandidateSize1();

		calculateSupportForEachCandidate(candidatesSize1);

		// To build level 1, we keep only the frequent candidates.
		// We scan the database...
		lastLevelFrequentItemset = createLevelWithFrequentCandidates(minsupp,
				candidatesSize1);
		
		currentLevelFrequentItemset =  new ArrayList<ItemsetApriori>();

		k = 2;
		// While the level is not empty
		while (lastLevelFrequentItemset.size() != 0 ) {

			// Generate candidates of size K
			List<ItemsetApriori> candidatesK = generateCandidateSizeK(lastLevelFrequentItemset);

			// We scan the database one time, to calculate the support
			// of each candidate.
			calculateSupportForEachCandidate(candidatesK);

			// we create level k with all the candidates that have 
			// a support higher than minsupp
			currentLevelFrequentItemset = createLevelWithFrequentCandidates(
					minsupp, candidatesK);
			
			// We check all sets of level k-1 for closure
			checkIfItemsetsK_1AreClosed(lastLevelFrequentItemset, currentLevelFrequentItemset);
			
			lastLevelFrequentItemset = currentLevelFrequentItemset;
			k++;
		}
		
		//  We label all items of the last level as closed, if they are not. 
		for(ItemsetApriori itemset : lastLevelFrequentItemset){   
			if(!itemset.isClose()){
				itemset.setClose(true);  
				saveItemsetToFile(itemset); 
			}
		} 

		writer.close();
		endTimestamp = System.currentTimeMillis();
	}
	
	private void checkIfItemsetsK_1AreClosed(Collection<ItemsetApriori> level,
			List<ItemsetApriori> levelK) throws IOException {
		for(ItemsetApriori itemset : level){
			boolean isClosed = true;
			for(ItemsetApriori itemsetK : levelK){
				if(itemsetK.getAbsoluteSupport() == itemset.getAbsoluteSupport() &&
						itemset.includedIn(itemsetK)){
					isClosed = false; 
					break;
				}
			}
			if(isClosed){
				itemset.setClose(true);
				saveItemsetToFile(itemset); 
			}
		}
	}

	protected List<ItemsetApriori> createLevelWithFrequentCandidates(double minsupp,List<ItemsetApriori> candidatesK) {
		List<ItemsetApriori> levelK = new ArrayList<ItemsetApriori>();
		for (ItemsetApriori candidate : candidatesK) { 
			if (candidate.getRelativeSupport(context.size()) >= minsupp) {
				levelK.add(candidate);
				currentLevelFrequentItemset.add(candidate);
			}
		}
		return levelK;
	}

	protected void calculateSupportForEachCandidate(
			List<ItemsetApriori> candidatesK) {
		for (ItemsetApriori transaction : context.getObjects()) {
			// For each candidate of level K, we increase its support
			// if it is included in the transaction.
			for (ItemsetApriori candidate : candidatesK) {
				if (candidate.includedIn(transaction)) {
					candidate.increaseTransactionCount();
				}
			}
		}
	}

	// Based on the description of Pasquier 99: "Efficient mining..."
	protected List<ItemsetApriori> generateCandidateSize1() {
		// sort the items in ascending order.
		List<ItemApriori> list = new ArrayList<ItemApriori>(context.getAttributes());
		Collections.sort(list, new Comparator<ItemApriori>(){
			public int compare(ItemApriori o1, ItemApriori o2) {
				if(o1.getId() == o2.getId()){
					return 0;
				}
				return o1.getId() - o2.getId();
			}
		});
		List<ItemsetApriori> candidates = new ArrayList<ItemsetApriori>(); // liste  d'itemsets
		for (ItemApriori item : list) {
			ItemsetApriori itemset = new ItemsetApriori();
			itemset.addItem(item);
			candidates.add(itemset);
		}
		return candidates;
	}

	protected List<ItemsetApriori> generateCandidateSizeK(List<ItemsetApriori> levelK_1) {
		List<ItemsetApriori> candidates = new ArrayList<ItemsetApriori>();
		
		// For each itemset I1 and I2 of level k-1
		loop1:	for(int i=0; i< levelK_1.size(); i++){
					ItemsetApriori itemset1 = (ItemsetApriori)levelK_1.get(i);
		loop2:		for(int j=i+1; j< levelK_1.size(); j++){
						ItemsetApriori itemset2 = (ItemsetApriori)levelK_1.get(j);
				
				// we compare items of itemset1  and itemset2.
				// If they have all the same k-1 items and the last item of itemset1 is smaller than
				// the last item of itemset2, we will combine them to generate a candidate
				for(int k=0; k< itemset1.size(); k++){
					// if they are the last items
					if(k == itemset1.size()-1){ 
						// the one from itemset1 should be smaller (lexical order) 
						// and different from the one of itemset2
						if(itemset1.getItems().get(k).getId() >= itemset2.get(k).getId()){  
							continue loop1;
						}
					}
					// if they are not the last items, and 
					else if(itemset1.getItems().get(k).getId() < itemset2.get(k).getId()){ 
						continue loop2; // we continue searching
					}
					else if(itemset1.getItems().get(k).getId() > itemset2.get(k).getId()){ 
						continue loop1;  // we stop searching:  because of lexical order
					}
				}
				ItemApriori missing = itemset2.get(itemset2.size()-1);
				// Now we will combine itemset1 and itemset2
				// Create a new candidate by combining itemset1 and itemset2
				ItemsetApriori candidate = new ItemsetApriori();
				for(int k=0; k < itemset1.size(); k++){
					candidate.addItem(itemset1.get(k));
				}
				candidate.addItem(missing);
				// The candidate is tested to see if its subsets of size k-1 are included in
				// level k-1 (they are frequent).
				if(allSubsetsOfSizeK_1AreFrequent(candidate,levelK_1)){
					candidates.add(candidate);
				}
			}
		}
		return candidates;
	}

	protected boolean allSubsetsOfSizeK_1AreFrequent(ItemsetApriori candidate, List<ItemsetApriori> levelK_1) {
		// To generate all the set of size K-1, we will proceed
		// by removing each item, one by one.
		if(candidate.size() == 1){
			return true;
		}
		for(ItemApriori item : candidate.getItems()){
			ItemsetApriori subset = candidate.cloneItemSetMinusOneItem(item);
			boolean found = false;
			for(ItemsetApriori itemset : levelK_1){
				if(itemset.isEqualTo(subset)){
					found = true;
					break;
				}
			}
			if(found == false){
				return false;
			}
		}
		return true;
	}

	
	public void saveItemsetToFile(ItemsetApriori itemset) throws IOException{
		writer.write(itemset.toString() + " Support: " + itemset.getRelativeSupport(context.size()));
		writer.newLine();
		itemsetCount++;
	}

	public void printStats() {
		System.out
				.println("=============  APRIORI CLOSE- STATS =============");
		long temps = endTimestamp - startTimestamp;
//		System.out.println(" Total time ~ " + temps + " ms");
		System.out.println(" Transactions count from database : "
				+ context.size());
		System.out.println(" The algorithm stopped at size " + (k - 1)
				+ ", because there is no candidate");
		System.out.println(" Frequent itemsets count : " + itemsetCount);

		System.out.println(" Total time ~ " + temps + " ms");
		System.out
				.println("===================================================");
	}
}
