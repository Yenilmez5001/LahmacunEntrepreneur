import java.io.IOException;

public class Employee {
    public String position;
    public String fullName;

    public String name;
    public String surname;
    public int promotionPoint;
    public int bonus;
    public String district;
    public String city;

    public Employee(String name, String surname, String district, String city, String position) {
        this.name = name;
        this.surname = surname;
        this.bonus = 0;
        this.district = district;
        this.city = city;
        this.position = position;
        this.fullName = name + surname;
    }

    /**
     * toString method
     * @return the string representation of the employee
     */
    @Override
    public String toString() {
        return "Employee{" +
                "fullName='" + name+surname + '\'' +
                ", promotionPoint=" + promotionPoint +
                ", score=" + bonus +
                ", district='" + district + '\'' +
                '}';
    }

    public void performanceUpdate(int monthlyScore, BranchHash table){

        if (monthlyScore > 0){
            int a = monthlyScore % 200;
            this.promotionPoint += monthlyScore / 200;
            this.bonus += a;
            table.overallBonus += a;
            table.monthlyBonus += a;
        }
        else {
            this.promotionPoint += monthlyScore / 200;
        }

        if (this.position.equals("Courier")) {
            if (this.promotionPoint > -5 && table.courierToDismiss==this){
                table.courierToDismiss = null;
            }
            if (this.promotionPoint <= -5) {
                table.courierToDismiss = this;
                table.dismissCourier(this);
//                if (this.name+this)
            }
        }
        // case: Cashier deserves to be promoted
        if (this.position.equals("Cashier")){
            if (this.promotionPoint > -5 && table.cashierToDismiss==this){
                table.cashierToDismiss=null;
            }
            if (this.promotionPoint < 3 && table.cashierToPromote==this){
                table.cashierToPromote=null;
            }
            if (this.promotionPoint >= 3) {
                table.cashierToPromote = this;
                if (table.cashierCount > 1 ){
                    table.promoteCashier(this);
                }
            }
            else if (this.promotionPoint <= -5){
                table.cashierToDismiss = this;
                table.dismissCashier(this);
            }
        }




        else if (this.position.equals("Cook")){


            if (this.promotionPoint > -5 && table.cookToDismiss==this){
                table.cookToDismiss = null;
                if (this.fullName.equals("BahaIpek")){
                    int a = 1;
                }

            }
            if (this.promotionPoint < 10){
                table.cooksToPromote.remove(this);
                if (this.fullName.equals("BahaIpek")){
                    int a = 2;
                }
            }


            if (this.promotionPoint >= 10) {
                if (this.fullName.equals("BahaIpek")){
                    int a = 3;
                }

                // since promotion from cook to manager requires to store the previous cooks to be promoted
                // we store the cooks to be promoted in a list
                java.util.ArrayList<Employee> cooksToPromote = table.cooksToPromote;
                if (!cooksToPromote.contains(this)){
                    cooksToPromote.add(this);
                }

                // if the manager needs to be dismissed, we dismiss the manager and promote THE COOK WHICH WAS THE FIRSTLY ADDED TO THE cooksToPromote list
                if ((table.manager.promotionPoint <= -5) && (table.cookCount > 1)) {
                    Employee manager = table.manager;
                    Employee a = cooksToPromote.get(0);   // a is the cook which has the priority to be promoted
                    a.position = "Manager";
                    a.promotionPoint -= 10;
                    table.manager = a;
                    table.cookCount--;

                    while(cooksToPromote.remove(a));
                    if (table.cookToDismiss == a) {
                        table.cookToDismiss = null;
                    }

                    int index = table.getIndexOf(manager);

                    table.table[index].fullName = "silindi";

                    try {
                        project2.fw.write(manager.name +" "+ manager.surname + " is dismissed from branch: "+ table.district + ".\n");
                        project2.fw.write(a.name + " "+ a.surname +" is promoted from Cook to Manager.\n");
                    }
                    catch (IOException e){
                        System.out.println("IOException");
                    }

                }
            }
            else if (this.promotionPoint <= -5){
//                table.cooksToDismiss.add(this);
                table.cookToDismiss = this;
                table.dismissCook(this);
            }
        }


        else if (this.position.equals("Manager")){
            table.promoteCook(this);
        }
    }
}
