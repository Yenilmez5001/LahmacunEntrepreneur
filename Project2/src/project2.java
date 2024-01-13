import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class project2 {
    public static FileWriter fw;
    private static java.util.ArrayList<BranchHash> branchesArrayList = new ArrayList<>();
    static String inputfile1;
    static String inputfile2;
    public static void main(String[] args) throws IOException {

        String outputfile = "output.txt";

        inputfile1 = args[0];
        inputfile2 = args[1];
        outputfile = args[2];

        try {
            File outputFile = new File(outputfile);
            outputFile.createNewFile();
        }
        catch (Exception var3) {
            System.out.println("An error occurred.");
            var3.printStackTrace();
        }

        hashTable branches = readInitial();
        fw = new FileWriter(outputfile);
        readInput(fw, branches);
        fw.close();
    }

    /**
     * Reads the input file and writes the output to the output file
     * @param allBranches the hash table that contains all the branches
     * @param fw the file writer to write the output
     */
    public static void readInput(FileWriter fw, hashTable allBranches){
        try {
            FileInputStream fis = new FileInputStream(inputfile2);
            Scanner sc = new Scanner(fis);

            while (sc.hasNextLine()) {
                String firstMonth = sc.nextLine().split("/n")[0].split(":")[0];

                String check = "a";
                // loop until the end of the month
                while (!check.equals("/n")){

                    if (!sc.hasNext()){break;}

                    String line = sc.nextLine();

                    if (line.equals("/n")){sc.nextLine();}
                    check = line;

                    if (line.trim().isEmpty()) {
                        for (BranchHash branch : branchesArrayList){
                            branch.monthlyBonus = 0;
                        }
                        break;
                    }
                    String[] arr = line.split(":");

                    String info = arr[1];
                    String[] infoArray = info.split(",");

                    String city = infoArray[0].strip();
                    String district = infoArray[1].strip();

                    if (line.startsWith("PRINT_MO")) {  // PRINT_MONTHLY_BONUSES
                        BranchHash b = allBranches.search(city,district);
                        fw.write("Total bonuses for the "+ district+" branch this month are: "+b.monthlyBonus+"\n");
                        fw.flush();
                    }
                    else if (line.startsWith("PRINT_MA")) {  // PRINT_MANAGER
                        BranchHash b = allBranches.search(city,district);
                        fw.write("Manager of the "+district+" branch is "+b.manager.name + " " + b.manager.surname + "."+"\n");
                        fw.flush();
                    }
                    else if (line.startsWith("PRINT_OV")){  // PRINT_OVERALL_BONUSES
                        BranchHash b = allBranches.search(city,district);
                        fw.write("Total bonuses for the "+b.district+" branch are: "+b.overallBonus+"\n");
                        fw.flush();
                    }
                    else if (line.startsWith("PE")) {  //PERFORMANCE_UPDATE
                        String[]nameSurname = infoArray[2].strip().split(" ");
                        String name = nameSurname[0];
                        String surname = nameSurname[1];
                        int score = Integer.parseInt(infoArray[3].strip());

                        BranchHash t = allBranches.search(city,district);
                        Employee e = t.search(name+surname);

                        if (!(e == null)){e.performanceUpdate(score,t);}
                        else {
                            fw.write("There is no such employee."+"\n");
                            fw.flush();
                        }
                    }
                    else if (line.startsWith("AD")) {
                        String[]nameSurname = infoArray[2].strip().split(" ");
                        String name = nameSurname[0];
                        String surname = nameSurname[1];
                        String position = infoArray[3].strip();

                        BranchHash branch = allBranches.search(city,district);
                        if (!(branch.search(name+surname)==null)) {
                            fw.write("Existing employee cannot be added again."+"\n");
                            fw.flush();
                        }
                        else{
                            if (position.startsWith("COO")) {
                                branch.add(new Employee(name, surname, district, city, "Cook"));
                            } else if (position.startsWith("COU")) {
                                branch.add(new Employee(name, surname, district, city, "Courier"));
                            } else if (position.startsWith("CA")) {
                                branch.add(new Employee(name, surname, district, city, "Cashier"));
                            } else if (position.startsWith("MA")) {
                                branch.add(new Employee(name, surname, district, city, "Manager"));
                            }
                        }
                    }
                    else if (line.startsWith("L")) {
                        String[]nameSurname = infoArray[2].strip().split(" ");
                        String name = nameSurname[0];
                        String surname = nameSurname[1];
                        BranchHash b = allBranches.search(city,district);
                        Employee e = b.search(name+surname);

                        if (e == null){
//                            fw.write("not found employee: "+name+" "+surname);
                            fw.write("There is no such employee."+"\n");
                            fw.flush();
                        }
                        else {
                            if (! e.position.equals("Manager")){
                                b.leaveExceptManager(e);
                            }
                            else {
                                b.leaveManager(e);
                            }
                        }
                    }
                }
            }

        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static hashTable readInitial(){
        hashTable branches = new hashTable();

        try {
            FileInputStream fis = new FileInputStream(inputfile1);
            Scanner sc = new Scanner(fis);
            // create a hash table for the branches
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] arr = line.split(", ");
                String city = arr[0];
                String district = arr[1];
                String[] n_s = arr[2].split(" ");
                String name = n_s[0];
                String surname = n_s[1];

//                String fullName = n_s[0] + n_s[1];
                String position = arr[3];

                Employee e = null;
                // create an employee object depending on the position
                if (position.length() == 4){   // cook
                    e = new Employee(name,surname, district, city,"Cook");
                }
                else if (position.startsWith("M")) { // manager
                    e = new Employee(name,surname, district, city, "Manager");
                }
                else if (position.startsWith("CA")) { // cashier
                    e = new Employee(name,surname, district, city,"Cashier");
                }
                else if (position.startsWith("CO")) { // courier
                    e = new Employee(name,surname, district, city, "Courier");
                }
                // add it to the hash table
                if (branches.contains(city,district)){
                    branches.search(city,district).add(e);
                }
                else {
                    BranchHash b = new BranchHash(city,district);
                    b.add(e);
                    branches.add(b);
                    branchesArrayList.add(b);
                }
            }
            return branches;
        }

        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        return branches;
    }
}