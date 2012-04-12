package com.sequential.main;

import com.sequential.associationrules.agrawal_Apriori_version.RuleAgrawal;
import com.sequential.sequencial_algo.AlgoSequential;
import com.sequential.sequencial_algo.Cycle;
import com.sequential.sequencial_algo.PartitionThread;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class main {

    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        int N = 0;
        java.util.Scanner s = new Scanner(System.in);
        try {
            int numeroLinhas = 88162;
            System.out.println("Digite o número de partições: ");
            N = s.nextInt();
            s.nextLine();

            System.out.println("Deseja criar as partições (S ou N): ");
            String opt = s.nextLine();
            if (opt.equals("S")) {

                int nroLinhasPorParticao = numeroLinhas / N;
                System.out.println("Numero Linhas: " + nroLinhasPorParticao);

                String local = System.getProperty("user.dir");
                System.out.println(local);


                File dir = new File(local + "\\" + N);
                boolean result = dir.mkdirs();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("D:\\mestrado\\Data Mining\\Algoritmos Mineração\\retail.dat")));
                String line = null;


                for (int i = 0; i < N; i++) {
                    FileOutputStream saida;
                    PrintStream fileSaida;
                    saida = new FileOutputStream(N + "/retail" + (i + 2) + ".dat");
                    fileSaida = new PrintStream(saida);
                    int countParticao = 0;

                    if (i == N - 1) {
                        while ((line = bufferedReader.readLine()) != null) {
                            fileSaida.print(line + "\n");
                            countParticao++;
                        }
                    } else {
                        while (((line = bufferedReader.readLine()) != null) && countParticao != nroLinhasPorParticao) {
                            fileSaida.print(line + "\n");
                            countParticao++;
                        }
                    }


                }
                bufferedReader.close();

            }

        } catch (Exception e) {
            System.err.println(e);
        }

        int Lmin = 2;
        int Lmax = N;
        double minSup = 0.005;
        double minConf = 0.1;

        AlgoSequential algoSeq = new AlgoSequential(Lmin, Lmax, minSup, minConf, N);

        System.out.println("Deseja executar sequencialmente ou com uso de threads (1 ou 2) ");
        String opt = s.nextLine();
        if (opt.equals("1")) {
            algoSeq.runAlgorithm();
        } else if (opt.equals("2")) {
            int numWorkers = N; //numero de tarefas: igual ao nro de particoes
            int threadPoolSize = N; // numero de threads no pool

            ExecutorService tpes = Executors.newFixedThreadPool(threadPoolSize);

            PartitionThread[] workers = new PartitionThread[numWorkers];

            for (int i = 0; i < numWorkers; i++) {
                workers[i] = new PartitionThread(i + 2, algoSeq);
                tpes.execute(workers[i]);
            }

            tpes.shutdown();
            tpes.awaitTermination(1000, TimeUnit.SECONDS);
        }
        
         algoSeq.generateCycles();
            //algoSeq.findCycles();
            Map<String, List<Cycle>> binarySequenceMap = algoSeq.getmBinarySequenceMap();
            for (RuleAgrawal ra : algoSeq.getmHash().values()) {
                boolean binarySequence[] = ra.getBinarySequence();
                String binarySequenceStr = "";
                for (int i = 0; i < binarySequence.length; i++) {
                    if (binarySequence[i]) {
                        binarySequenceStr += "1";
                    } else {
                        binarySequenceStr += "0";
                    }
                }
                if (binarySequenceMap.containsKey(binarySequenceStr)) {
                    ra.setPrunedCycles(binarySequenceMap.get(binarySequenceStr));
                } else {
                    List<Cycle> listCycles = algoSeq.findCycles(ra);
                    ra.setPrunedCycles(listCycles);
                    binarySequenceMap.put(binarySequenceStr, listCycles);
                }
            }
            
            algoSeq.printCyclicRules();
    }
}
