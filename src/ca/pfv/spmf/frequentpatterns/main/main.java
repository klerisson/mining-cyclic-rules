package ca.pfv.spmf.frequentpatterns.main;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import ca.pfv.spmf.frequentpatterns.apriori.*;
import ca.pfv.spmf.associationrules.agrawal_Apriori_version.*;
import ca.pfv.spmf.frequentpatterns.apriori.domain.Cycle;
import java.util.*;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<String, RuleAgrawal> mHash = new HashMap<String, RuleAgrawal>();
                int N = 1;
                int Lmin = 1;
                int Lmax = 2;
		try {
			
                        for(int i=0; i< N; i++){
                            System.out.println("Vou ler aquivo: " + ("resource/retail3.dat"));
                            
                            ContextApriori context = new ContextApriori();
                            context.loadFile("resource/retail3.dat");
                            
                            System.out.println("li aquivo");
                            AlgoApriori algo = new AlgoApriori(context);
                            Itemsets itemSets =  algo.runAlgorithm(0.005);
                            //algo.printStats(context.getMapeamentoIntString());

                            AlgoAgrawalFaster94 algoRules = new AlgoAgrawalFaster94(0.1, N);
                            RulesAgrawal rules = algoRules.runAlgorithm(itemSets);
                            List<RuleAgrawal> listaRegras = rules.getRules();
                            for(int j=0; j< listaRegras.size(); j++){
                                RuleAgrawal regra = listaRegras.get(j);
                                String regraStr = regra.toString();
                                if(mHash.containsKey(regraStr))
                                    mHash.get(regraStr).updateBinarySequence(i);
                                else {
                                    regra.updateBinarySequence(i);
                                    mHash.put(regraStr, regra);
                                }
                            }
                            /*int cont = 0;
                            List<RuleAgrawal> l1 = rules.getRules();
                            List<RuleAgrawal> l2 = rules2.getRules();
                            for(int i=0; i< l1.size(); i++){
                                    for(int j=0; j< l2.size(); j++){
                                            //System.out.println("Comparando " + l1.get(i).toString() + "e " + l2.get(j).toString());
                                            if(l1.get(i).toString().equals(l2.get(j).toString())){
                                                    cont++;
                                            }
                                    }


                            }
                            System.out.println("Cont =" + cont );*/;

                            //rules.printRules(context.size());
                        }
                        
                        AlgoSequencial algoSeq = new AlgoSequencial(Lmin, Lmax);
                        algoSeq.generateCycles();
                        
                        for(RuleAgrawal ra : mHash.values())     
                            ra.setPrunedCycles(algoSeq.pruneRule(ra));
                            
                       
                        //test
                        for(RuleAgrawal ra : mHash.values()) {
                            
                            List<Cycle> l = ra.getPrunedCycles();
                            
                            for(Cycle c : l)
                                System.out.println(c);
                            
                        }
                            
                        
                        Set<String> keys = mHash.keySet();
                        Iterator<String> it = keys.iterator();
                        while(it.hasNext()){
                            String key = it.next();
                            mHash.get(key).printBinarySequence();
                        }
                        
                } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*for(int i=0; i<100; i++){
			Random numRandon = new Random();
			for(int j=0; j< 10; j++){
				//System.out.print(numRandon.nextInt(10));
				System.out.print(j);
				if(j!=9)
					System.out.print(" ");
			}
			System.out.println();
			
		}*/
	}

}
