// in this array based hashtable, we want to store the employees working on a branch
// the number of cooks, couriers,  and cashiers working on a branch is not fixed. But we need to store the counts
// Firstly the counts for the positions will be initialized to 0
// After each recruitment, we need to update the counts of each branch
// the hash table will use the name surname for hashing
// the hash table will be resized when the load factor is greater than 0.5
// the hash table will contain the instances of employee class

import java.io.IOException;
import java.util.ArrayList;
public class BranchHash {
    public String branchName;
    public String city;
    public String district;
    private static int capacity = 9629;  // a random prime number to initialize the table
    private final double LOAD_FACTOR_THRESHOLD = 0.5;
    public Employee[] table;

    public Employee manager;
    public int cookCount;
    public int courierCount;
    public int cashierCount;
    private int size;
    private static int currentPrime = 31;
    public int overallBonus;
    public int monthlyBonus;

    public Employee cashierToDismiss = null;
    public Employee cashierToPromote = null;
    public java.util.ArrayList<Employee> cooksToPromote = new ArrayList<>();
    public Employee cookToDismiss = null;
    public Employee courierToDismiss = null;

    public BranchHash(String city,String district){
        this.city = city;
        this.district = district;
        branchName = city+district;
        table = new Employee[capacity];
        size = 0;
        cookCount = 0;
        courierCount = 0;
        cashierCount = 0;
        overallBonus = 0;
        monthlyBonus = 0;
        manager = null;
    }


    /**
     * Adds the employee to the hash table
     * @param e the employee to be added
     * use quadratic probing to find the next available index
     * if the load factor is greater than 0.5, resize and rehash the table
     */
    public void add(Employee e){

        if ((double) size / table.length > LOAD_FACTOR_THRESHOLD) {
            resize();
        }

        int index = hashCode(e.fullName);
        int i = 1;
        while(table[index] != null){
            index = (index + i*i) % capacity;
            i++;
        }
        table[index] = e;
        size++;

        if (e.position.equals("Cook")) {
            cookCount++;
            if (cookToDismiss!= null) {
                Employee a = cookToDismiss;
                dismissCook(a);
            }
            if (!cooksToPromote.isEmpty()) {
                if (manager.promotionPoint <= -5) {
                    Employee a = cooksToPromote.get(0);
                    cooksToPromote.remove(0);
                    a.position = "Manager";
                    int idx = getIndexOf(manager);
                    table[idx].fullName = "silindi";
                    manager = a;
                    cookCount--;

                }
            }
        }

        else if (e.position.equals("Courier")) {
            courierCount++;
            if (!(courierToDismiss ==null)) {
                Employee a = courierToDismiss;
                dismissCourier(a);
            }
        }

        else if (e.position.equals("Cashier")) {
            cashierCount++;
            if (!(cashierToDismiss==null)) {
                Employee a = cashierToDismiss;
                dismissCashier(a);
            }
            if (!(cashierToPromote==null)) {
                Employee a = cashierToPromote;
                if (a.promotionPoint >= 3) {
                    promoteCashier(a);
                }
            }
        }
        else {
            manager = e;
        }
    }

    /**
     * Resizes the hash table
     * update the current prime number to rehash the table for a better distribution
     * double the capacity and add 1 to make it odd, to avoid clustering
     * rehash the table
     */
    public void resize(){
        // update the current prime number to rehash the table
        currentPrime = nextPrime(currentPrime);
        capacity = capacity * 2 + 1;  // we want to double the capacity and add 1 to make it odd, to avoid clustering
        Employee[] newTable = new Employee[capacity];

        for (Employee a : table) {
            if (a != null) {
                int index = hashCode(a.fullName);
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
     * Hash function
     * @param fullName the name of the employee to be hashed
     * @return the index of the employee
     */
    public static int hashCode(String fullName) {
        long hash = 0L;

        for (char c : fullName.toCharArray()) {
            hash += (int) c + (currentPrime*hash);
        }

        // use modulo to get the index
        return Math.abs((int)(hash % capacity));
    }

    /**
     * Search method
     * @param fullName the name of the employee to be searched
     * use quadratic probing to find the employee
     * @return the employee if found, null otherwise
     */
    public Employee search(String fullName) {
        int index = hashCode(fullName);
        int i = 1;
        while (table[index] != null) {
            if (table[index].fullName.equals(fullName)) {
                return table[index];
            }
            index = (index + i * i) % capacity;
            i++;
        }
        return null;
    }

    /**
     * Promotes the cook to manager
     * @param manager to leave or be dismissed
     */
    public void promoteCook(Employee manager) {
        // if there is more than one cook, we can promote this cook to manager
        if (manager.promotionPoint<=-5 && this.cookCount > 1 && !cooksToPromote.isEmpty()) {
            // the firstly added cook to the cooksToPromote arraylist will be promoted
            Employee a = cooksToPromote.get(0);
            a.promotionPoint -= 10;
            this.cookCount--;
            a.position = "Manager";
            this.manager = a;
            cooksToPromote.remove(0);
            if (cookToDismiss == a){
                cookToDismiss = null;
            }
            try {
                project2.fw.write(manager.name +" "+ manager.surname+" is dismissed from branch: "+ this.district+"."+"\n");
                project2.fw.write(a.name+" "+a.surname +" is promoted from " + "Cook to Manager" + "."+"\n");
                project2.fw.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            int index = getIndexOf(manager);

            table[index].fullName = "silindi";
        }
    }

    /**
     * Promotes the cashier to cook
     * @param e the cashier to be promoted
     */
    public void promoteCashier(Employee e) {
        // if there is more than one cashier, we can promote this cashier to cook
        e.position = "Cook";
        e.promotionPoint -= 3;
        this.cookCount++;
        this.cashierCount--;
        this.cashierToPromote=null;
        if (e.promotionPoint >= 10){
            this.cooksToPromote.add(e);
        }
        try {
            project2.fw.write(e.name+" "+e.surname +" is promoted from " + "Cashier to Cook" + "."+"\n");
            project2.fw.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public void dismissCourier(Employee e) {
        // if there is more than one courier, remove the target courier
        if (this.courierCount > 1) {
            try {
                project2.fw.write(e.name+" "+e.surname +" is dismissed from branch: " + this.district + "."+"\n");
                project2.fw.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            this.courierToDismiss=null;
            int index = getIndexOf(e);
            table[index].fullName = "silindi";
            this.courierCount--;
        }
    }
    public void dismissCashier(Employee e) {
        // if there is more than one cashier, remove the target cashier
        if (this.cashierCount > 1) {
            try {
                project2.fw.write(e.name+" "+e.surname +" is dismissed from branch: " + this.district + "."+"\n");
                project2.fw.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            this.cashierToDismiss=null;
            int index = getIndexOf(e);
            table[index].fullName = "silindi";
            this.cashierCount--;
        }
    }
    /**
     * Dismisses the cook
     * @param e the cook to be dismissed
     */
    public void dismissCook (Employee e) {
        // if there is more than one cook, remove the target cook
        if (this.cookCount > 1) {
            try {
                project2.fw.write(e.name+" "+e.surname +" is dismissed from branch: " + this.district + "."+"\n");
                project2.fw.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            int index = getIndexOf(e);
            table[index].fullName = "silindi";
            this.cookCount--;
            this.cooksToPromote.remove(e);
            cookToDismiss = null;
        }
    }

    /**
     * get the index of the employee
     * @param e the employee to be searched
     *          use quadratic probing to find the employee
     */
    public int getIndexOf(Employee e) {
        int index = hashCode(e.fullName);
        int i = 1;
        while (table[index] != null) {
            if (table[index].fullName.equals(e.fullName)) {
                return index;
            }
            index = (index + i * i) % capacity;
            i++;
        }
        return -1;
    }

    /**
     * Leaves the manager
     * @param manager to leave or be dismissed
     * to be called whenever a manager wants to leave
     */
    public void leaveManager(Employee manager) {
        if (this.cookCount>1 && !this.cooksToPromote.isEmpty()){
            Employee a = cooksToPromote.get(0);
            a.position = "Manager";
            a.promotionPoint -= 10;
            this.manager = a;
            this.cookCount--;
            cooksToPromote.remove(0);
            //cooksToDismiss.remove(a);
            if (cookToDismiss == a){
                cookToDismiss = null;
            }
            int index = getIndexOf(manager);
            table[index].fullName = "silindi";

            try{
                project2.fw.write(manager.name+" "+manager.surname + " is leaving from branch: " + this.district + "."+"\n");
                project2.fw.write(a.name+" "+a.surname + " is promoted from " + "Cook to Manager" + "."+"\n");
                project2.fw.flush();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        else {
            if (manager.promotionPoint > -5){
                manager.bonus += 200;
                this.overallBonus += 200;
                this.monthlyBonus += 200;
            }
        }
    }

    /**
     * Leaves the employee other than manager
     * @param e the employee to leave
     * to be called whenever an employee wants to leave
     */
    public void leaveExceptManager(Employee e) {

        if (e.position.equals("Cook")){
            if (this.cookCount > 1) {

                int index = getIndexOf(e);
                table[index].fullName = "silindi";
                this.cookCount--;
                if (this.cookToDismiss == e){
                    this.cookToDismiss = null;
                }
                this.cooksToPromote.remove(e);

                try {
                    project2.fw.write(e.name+" "+e.surname + " is leaving from branch: " + this.district + "."+"\n");
                    project2.fw.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else{
                if (e.promotionPoint > -5){
                    e.bonus += 200;
                    overallBonus += 200;
                    monthlyBonus += 200;
                }
            }
        }
        else if (e.position.equals("Cashier")){
            if (this.cashierCount > 1) {
                int index = getIndexOf(e);
                table[index].fullName = "silindi";
                this.cashierCount--;
                this.cashierToDismiss=null;
                this.cashierToPromote=null;

                try {
                    project2.fw.write(e.name+" "+e.surname + " is leaving from branch: " + this.district + "."+"\n");
                    project2.fw.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else{
                if (e.promotionPoint > -5){
                    e.bonus += 200;
                    overallBonus += 200;
                    monthlyBonus += 200;
                }
            }
        }

        else if (e.position.equals("Courier")){
            if (this.courierCount > 1) {
                int index = getIndexOf(e);
                table[index].fullName = "silindi";
                this.courierCount--;
                if (this.courierToDismiss == e){
                    this.courierToDismiss = null;
                }
                try {
                    project2.fw.write(e.name+" "+e.surname + " is leaving from branch: " + this.district + "."+"\n");
                    project2.fw.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else{
                if (e.promotionPoint > -5){
                    e.bonus += 200;
                    overallBonus += 200;
                    monthlyBonus += 200;
                }
            }
        }
    }
}
