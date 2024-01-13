// our aim is to store the BranchHash objects in the hash table
// we will use quadratic probing to find the next available index
// our keys will be the names of the branches

public class hashTable {

    private static int capacity = 9629;  // a random prime number to initialize the table
    private final double LOAD_FACTOR_THRESHOLD = 0.5;
    private BranchHash[] table;
    private int size;
    private static int currentPrime = 31;
    public hashTable(){
        table = new BranchHash[capacity];
        size = 0;
    }

    /**
     * Adds the employee to the hash table
     * @param e the employee to be added
     * use quadratic probing to find the next available index
     * if the load factor is greater than 0.5, rehash the table
     */
    public void add(BranchHash e){
        if ((double) size / capacity > LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = hash(e.branchName);
        int i = 1;
        while(table[index] != null){
            index = (index + i*i) % capacity;

            i++;
        }
        table[index] = e;
        size++;
    }

    /**
     * hash function using quadratic probing
     * @param branchName the name of the branch
     */
    public static int hash(String branchName) {
        int hash = 0;

        for (char c : branchName.toCharArray()) {
            hash += (int) c + (currentPrime*hash);
        }
        // use modulo to get the index
        return Math.abs(hash % capacity);
    }

    /**
     * Resize the hash table
     * uses the next prime number as the new capacity
     */
    public void resize(){
        // update the current prime number to rehash the table
        currentPrime = nextPrime(currentPrime);
        capacity = capacity * 2 + 1;

        BranchHash[] newTable = new BranchHash[capacity];

        for (BranchHash a : table) {
            if (a != null) {
                int index = hash(a.branchName);
                int j = 1;
                while (newTable[index] != null) {
                    index = (index + j * j) % capacity;
                    j++;
                }
                newTable[index] = a;
            }
        }
        table = newTable;
    }

    private int nextPrime(int curPrime) {
        int nextPrime = curPrime + 2;

        while (!isPrime(nextPrime)) {
            nextPrime += 2;
        }
        return nextPrime;
    }

    private boolean isPrime(int a) {
        // we start from 3 because we know that the argument is odd
        for (int i = 3; i <= Math.sqrt(a) + 1; i++) {
            if (a % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Search method
     * @param city city
     * @param district district
     * use quadratic probing to find the employee
     * @return the employee if found, null otherwise
     */
    public BranchHash search(String city, String district) {
        int index = hash(city+district);
        int i = 1;
        while (table[index] != null) {
            if (table[index].branchName.equals(city+district)) {
                return table[index];
            }
            index = (index + i * i) % capacity;
            i++;
        }
        return null; // Employee not found
    }

    /**
     * checks if the hash table contains the employee
     * @param city city
     * @param district district
     * @return true if the employee is in the hash table, false otherwise
     */
    public boolean contains(String city,String district){
        return search(city,district) != null;
    }

}
