/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.pfv.spmf.frequentpatterns.apriori.domain;

/**
 *
 * @author Crícia
 */
public class Cycle {
    
    /**
     * In multiples of the time unit
     */
    private int lentgh;
    
    /**
     * The ﬁrst time unit in which the cycle occurs
     */
    private int offset;

    public Cycle(int lentgh, int offset) {
        super();
        this.lentgh = lentgh;
        this.offset = offset;
    }

    public int getLentgh() {
        return lentgh;
    }

    public void setLentgh(int lentgh) {
        this.lentgh = lentgh;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "Cycle{" + "lentgh=" + lentgh + ", offset=" + offset + '}';
    }
   
    
}
