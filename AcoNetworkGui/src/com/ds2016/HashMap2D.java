package com.ds2016;

import java.util.HashMap;

/**
 * Created by damian on 23/5/16.
 */
public class HashMap2D<K1, K2, V> {

    public final HashMap<K1, HashMap<K2, V>> M = new HashMap<>();

    /**
     * Allocates a value to an entry
     *
     * @param key1  First Key
     * @param key2  Second Key
     * @param value Value to be set
     * @return The previous value associated with key, or null if there was no mapping for key.
     */
    V put(K1 key1, K2 key2, V value) {
        HashMap<K2, V> M2;
        if (M.containsKey(key1)) {
            M2 = M.get(key1);
        } else {
            M2 = new HashMap<>();
            M.put(key1, M2);
        }
        return M2.put(key2, value);
    }

    /**
     * Retrieves a row
     *
     * @param key1 First Key
     * @return The row corresponding to the first key
     */
    HashMap<K2, V> get(K1 key1) {
        return M.get(key1);
    }

    /**
     * Retrieves a value for an entry
     *
     * @param key1 First Key
     * @param key2 Second Key
     * @return The value to which the specified key is mapped
     */
    public V get(K1 key1, K2 key2) {
        if (M.containsKey(key1)) {
            return M.get(key1).get(key2);
        } else {
            return null;
        }
    }

    /**
     * Clear 2D HashMap
     */
    public void clear() {
        M.clear();
    }

    /**
     * Checks if a mapping exists for the specified key
     *
     * @param key1 First Key
     * @param key2 Second Key
     * @return True, if a mapping exists
     */
    public boolean containsKey(K1 key1, K2 key2) {
        return M.containsKey(key1) && M.get(key1).containsKey(key2);
    }
}
