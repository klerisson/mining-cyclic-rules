package ca.pfv.spmf.frequentpatterns.apriori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
/**
 * This class represents a binary context.
 * It can read the context directly from a file.
 * See the ca.pfv.spmf.test folder for some examples of files
 * containing binary contexts.
 * 
 * @author Philippe Fournier-Viger 
 */
public class ContextApriori {

	// Contexte
	private final Set<ItemApriori> attributes = new HashSet<ItemApriori>();
	private final List<ItemsetApriori> objects = new ArrayList<ItemsetApriori>();
	private final Map<Integer, String> mapeamentoIntString = new HashMap<Integer, String>();
	private final Map<String, Integer> mapeamentoStringInt = new HashMap<String, Integer>();
	private int proximoCodigo = 1;
	
	public Map<Integer, String> getMapeamentoIntString() {
		return mapeamentoIntString;
	}

	public void addItemset(ItemsetApriori itemset){
		objects.add(itemset);
		attributes.addAll(itemset.getItems());
	}

	public void loadFile(String path) throws IOException {
		String thisLine;
		BufferedReader myInput = null;
		try {
			FileInputStream fin = new FileInputStream(new File(path));
			myInput = new BufferedReader(new InputStreamReader(fin));
			while ((thisLine = myInput.readLine()) != null) {
				if(thisLine.charAt(0) != '#'){ 
					addObject(thisLine.split(" "));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(myInput != null){
				myInput.close();
			}
	    }
	}
	
	public void addObject(String attributs[]){
		//We assume that there is no empty line
		ItemsetApriori itemset = new ItemsetApriori();
		for(String attribute:  attributs){
			ItemApriori item = new  ItemApriori(getIntIdentifier(attribute));
			//ItemApriori item = new  ItemApriori( Integer.parseInt(attribute));
			itemset.addItem(item);
			attributes.add(item);
		}
		objects.add(itemset);
	}
	
	private int getIntIdentifier(String item) {
		if(mapeamentoStringInt.containsKey(item)){
			return mapeamentoStringInt.get(item);
		}
		else
		{
			int codAtual = proximoCodigo;
			mapeamentoStringInt.put(item, proximoCodigo);
			mapeamentoIntString.put(proximoCodigo, item);
			proximoCodigo++;
			return codAtual;
		}
	}

	public void printContext(){
		System.out
		.println("===================  BINARY CONTEXT ===================");
		int count = 0;
		for(ItemsetApriori itemset : objects){ // pour chaque objet
			System.out.print("0" + count + ":  ");
			itemset.print();
			System.out.println("");
			count++;
		}
	}
	
	public int size(){
		return objects.size();
	}

	public List<ItemsetApriori> getObjects() {
		return objects;
	}

	public Set<ItemApriori> getAttributes() {
		return attributes;
	}

}
