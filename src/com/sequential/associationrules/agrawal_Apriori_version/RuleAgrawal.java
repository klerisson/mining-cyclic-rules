package com.sequential.associationrules.agrawal_Apriori_version;

import com.sequential.frequentpatterns.apriori.ItemsetApriori;
import com.sequential.sequencial_algo.Cycle;
import java.util.List;

/**
 * This class is for representing an association rule. An association rule has
 * an antecedent, a consequent, a support and a confidence.
 *
 * This file is part of the SPMF software. See the following website for the
 * license agreement: http://www.philippe-fournier-viger.com/spmf/
 *
 * @author Philippe Fournier-Viger, 2008
 */
public class RuleAgrawal {

    private ItemsetApriori itemset1; // antecedent
    private ItemsetApriori itemset2; // consequent
    private int transactionCount; // absolute support
    private double confidence;
    private boolean[] binarySequence;
    private List<Cycle> prunedCycles;

    public RuleAgrawal(ItemsetApriori itemset1, ItemsetApriori itemset2, int transactionCount, double confidence) {
        this.itemset1 = itemset1;
        this.itemset2 = itemset2;
        this.transactionCount = transactionCount;
        this.confidence = confidence;
    }

    public RuleAgrawal(ItemsetApriori itemset1, ItemsetApriori itemset2, int transactionCount, double confidence, int numberPartitions) {
        this.itemset1 = itemset1;
        this.itemset2 = itemset2;
        this.transactionCount = transactionCount;
        this.confidence = confidence;
        this.binarySequence = new boolean[numberPartitions];
    }

    public double getRelativeSupport(int objectCount) {
        return ((double) transactionCount) / ((double) objectCount);
    }

    public int getSupportAbsolu() {
        return transactionCount;
    }

    public double getConfidence() {
        return confidence;
    }

    public void print() {
        System.out.println(toString());
    }

    public String toString() {
        return itemset1.toString() + " ==> " + itemset2.toString();
    }

    public ItemsetApriori getItemset1() {
        return itemset1;
    }

    public ItemsetApriori getItemset2() {
        return itemset2;
    }

    public void updateBinarySequence(int pos) {
        binarySequence[pos] = true;
    }

    public void printBinarySequence() {
        String str = toString() + "->";
        for (int i = 0; i < binarySequence.length; i++) {
            if (binarySequence[i]) {
                str += "1";
            } else {
                str += "0";
            }
        }
        System.out.println(str);
    }

    public boolean[] getBinarySequence() {
        return binarySequence;
    }

    public List<Cycle> getPrunedCycles() {
        return prunedCycles;
    }

    public void setPrunedCycles(List<Cycle> prunedCycles) {
        this.prunedCycles = prunedCycles;
    }
}
