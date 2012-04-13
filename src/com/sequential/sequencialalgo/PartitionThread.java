/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sequential.sequencialalgo;

import com.sequential.associationrules.agrawal_Apriori_version.AlgoAgrawalFaster94;
import com.sequential.associationrules.agrawal_Apriori_version.RuleAgrawal;
import com.sequential.associationrules.agrawal_Apriori_version.RulesAgrawal;
import com.sequential.frequentpatterns.apriori.AlgoApriori;
import com.sequential.frequentpatterns.apriori.ContextApriori;
import com.sequential.frequentpatterns.apriori.Itemsets;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Lucas
 */
public class PartitionThread implements Runnable {
    private int partitionId;
    private AlgoSequential algoSeq;

    public PartitionThread(int id, AlgoSequential algoSeq) {
        partitionId = id;
        this.algoSeq = algoSeq;
    }

    public void run() {
        algoSeq.execute(partitionId);
    }
}
