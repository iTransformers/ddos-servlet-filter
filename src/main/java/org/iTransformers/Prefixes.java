package org.iTransformers;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 9/2/14
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Prefixes {
    HashMap<String,PrefixCounter> prefixes;

    public Prefixes(HashMap<String,PrefixCounter> prefixes) {
        this.prefixes = prefixes;
    }
    public int getSize() {
        return prefixes.size();
    }
    public Prefixes() {
       prefixes = new HashMap<String,PrefixCounter>();
    }

    public HashMap<String,PrefixCounter> getPrefixes() {
        return prefixes;
    }

    public void setPrefixes(HashMap<String,PrefixCounter> prefixes) {
        this.prefixes = prefixes;
    }

    public PrefixCounter getPrefix(String networkPrefix){
         return prefixes.get(networkPrefix);
    }

    public int getPrefixCounter(String networkPrefix){
          return prefixes.get(networkPrefix).getHitCounter();
    }


    public void addPrefix(String networkPrefix,long timeticks,boolean quaranteened) {
        PrefixCounter prefixCounter = new PrefixCounter();
        prefixCounter.addMillis(timeticks);
        prefixCounter.setQuarantined(quaranteened);
        prefixes.put(networkPrefix,prefixCounter);
    }

    public void updatePrefixQuarantineStatus(String networkPrefix,boolean quarantine) {
        prefixes.get(networkPrefix).setQuarantined(quarantine);

    }
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for(String prefix : prefixes.keySet()){
            PrefixCounter prefixCounter = prefixes.get(prefix);
            builder.append(prefix+prefixCounter.toString());
        }
       return builder.toString();
    }
    public void deletePrefixes(String networkAddress){

    }
}
