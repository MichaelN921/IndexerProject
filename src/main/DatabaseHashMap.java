package main;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//TODO: entrySet()
public class DatabaseHashMap<K,V> extends HashMap<K,V> implements Map<K,V> {
    List<Entry<K,V>>[] entries;
    static final int MAXIMUM_CAPACITY = 1 << 30;
    int arrayLength = 10007;
    int numCollisions = 0;
    int size = 0;
    static final double FACTOR = .1;
    Entry<K,V> entry;

    // uses default arrayLength
    public DatabaseHashMap(){
        construct();
    }

    @SuppressWarnings("unchecked")
    private void construct(){
        entries = new ArrayList[arrayLength];
//        Arrays.setAll(entries, element -> new ArrayList<>());
    }

    static class Entry<K,V>{
        K key;
        V value;

        public Entry(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }

    @Override
    public V put(K key, V value){
        int hashedKey = hashKey(key);
        int index = 0;
        if (entries[hashedKey] == null){
            entries[hashedKey] = new ArrayList<>();
        }
        // overwrite existing
        else if (containsKey(key)){
            entry = fetch(key);
            entry.setValue(value);
            return value;
        }
        // collision
        else if (containsHashedKey(key)) {
            numCollisions++;
            index = entries[hashedKey].size();
        }
        entries[hashedKey].add(index, new Entry<>(key, value));
        size++;
        if ((size > arrayLength * FACTOR) && ((arrayLength * 2) + 1 < MAXIMUM_CAPACITY)){
            rehash();
        }
        return value;
    }

    @Override
    public V get(Object key){
        entry = fetch(key);
        if (entry != null){
            return entry.getValue();
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public Entry<K,V> fetch(Object key) {
        if (!containsHashedKey((K) key)) return null;

        return entries[hashKey(key)].stream()
                .filter(kvEntry -> kvEntry.getKey().equals(key))
                .findAny()
                .orElse(null);
    }

    @Override
    public V remove(Object key){
        int hashedKey = hashKey(key);
        entry = fetch(key);
        if (entry != null) {
            V oldValue = entry.getValue();
            entries[hashedKey].remove(entry);
            size--;
            return oldValue;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()){
            put(entry.getKey(), entry.getValue());
        }
    }

    private int hashKey(Object key){
        if (key instanceof String){
            return Math.abs(myStringHash((String) key)) % arrayLength;
        }
        return Math.abs(key.hashCode()) % arrayLength;
    }

    private int myStringHash(String key){
        char[] charArr = key.toCharArray();
        int[] asciiArray = getAsciiArray(charArr);
        int result = 0;
        for (int i = 0; i<asciiArray.length;i++){
            result += (int) ((asciiArray[i] - 38) * Math.pow(4, i));
        }
        return result;
    }

    private int[] getAsciiArray(char[] charArr){
        int[] asciiArray = new int[charArr.length];
        for (int i=0; i<charArr.length; i++){
            if (Character.isLetter(charArr[i]) && Character.isLowerCase(charArr[i])) {
                charArr[i] = Character.toUpperCase(charArr[i]);
            }
            asciiArray[i] = charArr[i];
        }
        return asciiArray;
    }

    private boolean containsHashedKey(K key){
        return entries[hashKey(key)] != null && !entries[hashKey(key)].isEmpty();
    }

    @Override
    public boolean containsKey(Object key){
        return get(key) != null;
    }

    @Override
    public boolean containsValue(Object value){
        return Arrays.stream(entries)
                .filter(Objects::nonNull)
                .anyMatch(a -> a.stream()
                        .anyMatch(e -> e.getValue().equals(value)));
    }

    public Set<K> getKey(V value){
        return Arrays.stream(entries)
                .filter(Objects::nonNull)
                .flatMap(a -> a.stream()
                        .filter(e -> e.getValue().equals(value))
                        .map(Entry::getKey))
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public void rehash(){
        List<Entry<K, V>>[] tempEntries = new ArrayList[arrayLength];
        IntStream.range(0, arrayLength).filter(i -> entries[i] != null).forEach(i -> tempEntries[i] = entries[i]);
        arrayLength = (arrayLength * 2) - 1;
        construct();
        Arrays.stream(tempEntries).filter(Objects::nonNull).forEach(a -> a.forEach(e -> put(e.getKey(), e.getValue())));
    }

    @Override
    public void clear(){
        construct();
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        return Arrays.stream(entries)
                .filter(Objects::nonNull)
                .flatMap(a -> a.stream()
                               .map(Entry::getKey))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return Arrays.stream(entries)
                .filter(Objects::nonNull)
                .flatMap(a -> a.stream()
                               .map(Entry::getValue))
                .collect(Collectors.toSet());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public int getNumCollisions(){
        return numCollisions;
    }

}