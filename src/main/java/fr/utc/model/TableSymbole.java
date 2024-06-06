package fr.utc.model;

import java.util.HashMap;
import java.util.Map;

public class TableSymbole {
    
    private Map<String, Double> tableSymbole = new HashMap<String, Double>();

    public void addEntry(String symbole, Double value) {
        tableSymbole.put(symbole, value);
    }

    public Double getEntry(String symbole) {
        return tableSymbole.get(symbole);
    }
}
