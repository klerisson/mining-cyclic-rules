/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.pfv.spmf.frequentpatterns.apriori;

import ca.pfv.spmf.associationrules.agrawal_Apriori_version.RuleAgrawal;
import ca.pfv.spmf.frequentpatterns.apriori.domain.Cycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Lucas
 * @author Crícia
 */
public class AlgoSequencial {
    
    private int Lmin;
    private int Lmax;
    
    private List<Cycle> cycles;

    public AlgoSequencial(int Lmin, int Lmax) {
        super();
        
        if(Lmin > Lmax)
            throw new IllegalArgumentException("Lmin must have to be greather than Lmax");
        
        this.Lmin = Lmin;
        this.Lmax = Lmax;
    }
    
    public List<Cycle> generateCycles(){
        
        this.cycles = new ArrayList<Cycle>();
        
        for(int i = this.Lmin; i <= this.Lmax; i++){
            
            for(int j = 0; j < i; j++) {
                
                this.cycles.add(new Cycle(i,j));
                
            }
            
        }
        
        return cycles;
    }
    
    public List<Cycle> pruneRule(RuleAgrawal ra){
        
        if(ra == null)
            throw new IllegalArgumentException("Invalid rule");
        
        List<Cycle> listRet = new ArrayList<Cycle>();
        
        boolean[] bs = ra.getBinarySequence();
        for(int index = 0; index <= bs.length; index++){
            
            if(bs[index] == false) {
                
                for(Cycle c : this.cycles) {
                    
                    if(c.getOffset() == (index % c.getLentgh()))
                        continue;
                    
                    listRet.add(c);
                    
                }
                
            }
            
        }
        
        return listRet;
    }
    
    public void setLmax(int Lmax) {
        this.Lmax = Lmax;
    }

    public void setLmin(int Lmin) {
        this.Lmin = Lmin;
    }
    
    public static void main(String... args){
        
        AlgoSequencial as = new AlgoSequencial(1, 3);
        for(Cycle c : as.generateCycles()){
            System.out.println(c);
        }
        
    }
    
}
