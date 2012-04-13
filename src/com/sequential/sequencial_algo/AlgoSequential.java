/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sequential.sequencial_algo;

import com.sequential.associationrules.agrawal_Apriori_version.AlgoAgrawalFaster94;
import com.sequential.associationrules.agrawal_Apriori_version.RuleAgrawal;
import com.sequential.associationrules.agrawal_Apriori_version.RulesAgrawal;
import com.sequential.frequentpatterns.apriori.AlgoApriori;
import com.sequential.frequentpatterns.apriori.ContextApriori;
import com.sequential.frequentpatterns.apriori.Itemsets;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Lucas
 */
public class AlgoSequential {

    private int Lmin;
    private int Lmax;
    private Map<String, RuleAgrawal> mHash;
    private Map<String, List<Cycle>> mBinarySequenceMap;
    private double minSup;
    private double minConf;
    private int numPartitions;
    private List<Cycle> cycles;

    public Map<String, RuleAgrawal> getmHash() {
        return mHash;
    }

    public void setmHash(Map<String, RuleAgrawal> mHash) {
        this.mHash = mHash;
    }

    public AlgoSequential(int Lmin, int Lmax, double minSup, double minConf, int N) {

        if (Lmin > Lmax) {
            throw new IllegalArgumentException("Lmin must have to be greather than Lmax");
        }

        this.mHash = new HashMap<String, RuleAgrawal>();
        this.Lmin = Lmin;
        this.Lmax = Lmax;
        this.minSup = minSup;
        this.minConf = minConf;
        this.numPartitions = N;
        this.cycles = new ArrayList<Cycle>();
        this.mBinarySequenceMap = new HashMap<String, List<Cycle>>();
    }

    public Map<String, List<Cycle>> getmBinarySequenceMap() {
        return mBinarySequenceMap;
    }

    public void setLmax(int Lmax) {
        this.Lmax = Lmax;
    }

    public void setLmin(int Lmin) {
        this.Lmin = Lmin;
    }

    public void runAlgorithm() {
        try {

            for (int i = 0; i < numPartitions; i++) {
                System.out.println("Vou ler aquivo: " + (System.getProperty("user.dir") + "\\" + numPartitions + "\\retail" + (i + 2) + ".dat"));
                ContextApriori context = new ContextApriori();
                context.loadFile(System.getProperty("user.dir") + "\\" + numPartitions + "\\retail" + (i + 2) + ".dat");
                System.out.println("li aquivo");
                AlgoApriori algo = new AlgoApriori(context);
                Itemsets itemSets = algo.runAlgorithm(0.005);
                //algo.printStats(context.getMapeamentoIntString());
                AlgoAgrawalFaster94 algoRules = new AlgoAgrawalFaster94(0.1, numPartitions);
                RulesAgrawal rules = algoRules.runAlgorithm(itemSets);
                List<RuleAgrawal> listaRegras = rules.getRules();
                for (int j = 0; j < listaRegras.size(); j++) {
                    RuleAgrawal regra = listaRegras.get(j);
                    String regraStr = regra.toString();
                    if (mHash.containsKey(regraStr)) {
                        mHash.get(regraStr).updateBinarySequence(i);
                    } else {
                        regra.updateBinarySequence(i);
                        mHash.put(regraStr, regra);
                    }
                }
            }
            Set<String> keys = mHash.keySet();
            Iterator<String> it = keys.iterator();
            while (it.hasNext()) {
                String key = it.next();
                mHash.get(key).printBinarySequence();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public synchronized void addRules(List<RuleAgrawal> listaRegras, int pos) {
        for (int j = 0; j < listaRegras.size(); j++) {
            RuleAgrawal regra = listaRegras.get(j);
            String regraStr = regra.toString();
            if (mHash.containsKey(regraStr)) {
                mHash.get(regraStr).updateBinarySequence(pos);
            } else {
                regra.updateBinarySequence(pos);
                mHash.put(regraStr, regra);
            }
        }
    }

    public void printRegras() {
        Set<String> keys = mHash.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            mHash.get(key).printBinarySequence();
        }
    }

    public void execute(int partitionId) {
        try {
            System.out.println("Vou ler aquivo: " + (System.getProperty("user.dir") + "\\" + numPartitions + "\\retail" + partitionId + ".dat"));
            ContextApriori context = new ContextApriori();
            context.loadFile(System.getProperty("user.dir") + "\\" + numPartitions + "\\retail" + partitionId + ".dat");
            System.out.println("li aquivo");
            AlgoApriori algo = new AlgoApriori(context);
            Itemsets itemSets = algo.runAlgorithm(0.005);
            //algo.printStats(context.getMapeamentoIntString());
            AlgoAgrawalFaster94 algoRules = new AlgoAgrawalFaster94(0.1, numPartitions);
            RulesAgrawal rules = algoRules.runAlgorithm(itemSets);
            List<RuleAgrawal> listaRegras = rules.getRules();
            addRules(listaRegras, partitionId - 2);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<Cycle> generateCycles() {

        this.cycles = new ArrayList<Cycle>();

        for (int i = this.Lmin; i <= this.Lmax; i++) {

            for (int j = 0; j < i; j++) {

                this.cycles.add(new Cycle(i, j));

            }

        }

        return cycles;
    }

    private List<Cycle> pruneRule(RuleAgrawal ra) {

        if (ra == null) {
            throw new IllegalArgumentException("Invalid rule");
        }

        List<Cycle> listRet = (List<Cycle>) ((ArrayList) cycles).clone();

        boolean[] bs = ra.getBinarySequence();
        for (int index = 0; index < bs.length; index++) {

            if (bs[index] == false) {

                for (Iterator<Cycle> itr = listRet.iterator(); itr.hasNext();) {
                    Cycle c = itr.next();
                    if (c.getOffset() == (index % c.getLentgh())) {
                        itr.remove();

                    }

                }

            }

        }

        return listRet;
    }

    private List<Cycle> findRuleCycles(RuleAgrawal ra) {

        List<Cycle> cycles = pruneRule(ra);
        eliminateUselessCycles(cycles);

        return cycles;
    }

    private void eliminateUselessCycles(List<Cycle> prunedCycles) {
        List<Cycle> listRet = (List<Cycle>) ((ArrayList) prunedCycles).clone();

        for (Iterator<Cycle> itr = listRet.iterator(); itr.hasNext();) {
            Cycle currentCycle = itr.next();
            int currentCycleLength = currentCycle.getLentgh();
            int currentCycleOffset = currentCycle.getOffset();

            for (Iterator<Cycle> itr2 = prunedCycles.iterator(); itr2.hasNext();) {
                Cycle nextCycle = itr2.next();
                int nextCycleLength = nextCycle.getLentgh();
                int nextCycleOffset = nextCycle.getOffset();
                if ((currentCycleLength != nextCycleLength)
                        && ((nextCycleLength % currentCycleLength) == 0)
                        && (currentCycleOffset == (nextCycleOffset % currentCycleLength))) {
                    itr2.remove();
                }
            }
        }
    }

    public void printCyclicRules() {
        Set<String> keys = mHash.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next();
            RuleAgrawal rule = mHash.get(key);
            if (rule.getPrunedCycles() != null && rule.getPrunedCycles().size() > 0) {
                System.out.print(rule.toString() + " ");
                System.out.println(rule.getPrunedCycles());
            }
        }
    }

    public void findCycles() {
        for (RuleAgrawal ra : mHash.values()) {
            boolean binarySequence[] = ra.getBinarySequence();
            String binarySequenceStr = "";
            for (int i = 0; i < numPartitions; i++) {
                if (binarySequence[i]) {
                    binarySequenceStr += "1";
                } else {
                    binarySequenceStr += "0";
                }
            }
            if (mBinarySequenceMap.containsKey(binarySequenceStr)) {
                ra.setPrunedCycles(mBinarySequenceMap.get(binarySequenceStr));
            } else {
                List<Cycle> listCycles = findRuleCycles(ra);
                ra.setPrunedCycles(listCycles);
                mBinarySequenceMap.put(binarySequenceStr, listCycles);
            }
        }
    }
}
