import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class CLIApp {
    INumberleModel model=new NumberleModel();

    //ANSI Escape Sequence for color setting
    String[] colors = {
            "\u001B[32m", // green
            "\u001B[33m", // yellow
            "\u001B[37m",  // white
            "\u001B[0m"//Default color
    };

    ArrayList<Character> orange = new ArrayList<>();
    ArrayList<Character> green = new ArrayList<>();
    ArrayList<Character> grey = new ArrayList<>();
    ArrayList<Character> white = new ArrayList<>(Arrays.asList('1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '+', '-', '*', '/'));
    Scanner scanner = new Scanner(System.in);
    StringBuilder currentGuess=new StringBuilder();
    boolean state=false;

    public void startGame(){
        model.startNewGame();//start the game

        String flag1=setFlag1();
        String flag2=setFlag2();
        String flag3=setFlag3();
        flagsOperation(flag1,flag2);
        flag3Operation(flag3);

        setGuess();
        state=model.isGameWon(currentGuess.toString());

        while (!model.isGameOver()){
            //User requests to display error messages
            if(model.getFlag3()==1){
                switch (model.getError()) {
                    case "1":
                        System.out.println("Error message: Null!!");
                        break;
                    case "2":
                        System.out.println("Error message: Too short");
                        break;
                    case "3":
                        System.out.println("Error message: No equal '=' sign");
                        break;
                    case "4":
                        System.out.println("Error message: The left side is not equal to the right");
                        break;
                    case "7":
                        System.out.println("Error message: There must be at least one sign +-*/");
                        break;
                    case "8":
                        System.out.println("Error message: Multiple math symbols in a row");
                        break;
                    case "6":
                        error6Operation();
                        break;
                    default:
                        // Handle default case if necessary
                        break;
                }
            }else{
                //User requests not to display error messages
                if(model.getError().equals("6")){
                    error6Operation();
                }
            }
            //set color array and print the color array
            setColorArray();

            setGuess();
            state=model.isGameWon(currentGuess.toString());
        }
        winOrLost();
    }

    public void winOrLost(){
        if(state){
            System.out.println("You win!!");
        }else{
            System.out.println("You lost!!");
        }
        System.out.println("-----------split line-----------");

        System.out.print("Start a new game ? (0 for no,1 for yes):");
        String again=scanner.nextLine();
        while (!again.equals("1") && !again.equals("0")) {
            System.out.print("Invalid character, set again (0 for no,1 for yes):");
            again=scanner.nextLine();
        }
        System.out.println("-----------split line-----------");

        if(again.equals("1")){
            startGame();
        }
    }

    public void setGuess(){
        System.out.print("Enter an equation:");
        currentGuess.setLength(0); // Clear StringBuilder
        currentGuess.append(scanner.nextLine()); // Set a new equation
    }

    public String setFlag1(){
        //The first flag setting
        System.out.print("Flag 1 set to display target equation (0 for not set,1 for set):");
        String flag1 = scanner.nextLine();
        while (!flag1.equals("1") && !flag1.equals("0")) {
            System.out.println("Invalid character, set again");
            System.out.print("Flag 1 set to display target equation (0 for not set, 1 for set): ");
            flag1 = scanner.nextLine();
        }
        return flag1;
    }

    public String setFlag2(){
        //The second flag setting
        System.out.print("Flag 2 set to target is not fixed (0 for not set,1 for set):");
        String flag2 = scanner.nextLine();
        while (!flag2.equals("1") && !flag2.equals("0")) {
            System.out.println("Invalid character,set again");
            System.out.print("Flag 2 set to target is not fixed (0 for not set, 1 for set): ");
            flag2 = scanner.nextLine();
        }
        return flag2;
    }

    public String setFlag3(){
        //The third Flag Setting
        System.out.print("Flag 3 set to display error message (0 for not set,1 for set):");
        String flag3 = scanner.nextLine();
        while (!flag3.equals("1") && !flag3.equals("0")) {
            System.out.println("Invalid character, set again");
            System.out.print("Flag 3 set to display error message (0 for not set, 1 for set): ");
            flag3 = scanner.nextLine();
        }
        return flag3;
    }

    public void flagsOperation(String flag1,String flag2){
        if (flag1.equals("1") && flag2.equals("1")) {
            model.flag1AndFlag2();
        }else if (flag1.equals("1") && flag2.equals("0")){
            model.flag1AndNotFlag2();
        }else if (flag1.equals("0") && flag2.equals("1")){
            model.NotFlag1AndFlag2();
        }
        System.out.println("-----------start the game-----------");
    }

    public void flag3Operation(String flag3){
        if(flag3.equals("1")){
            model.setFlag3();
        }
    }

    public void setColorArray(){
        // Traverse the first three lists and remove characters
        // that appear in the first three lists but do not appear in the fourth list from the fourth list
        for (char c : orange) {
            if (white.contains(c)) {
                white.remove(Character.valueOf(c));
            }
        }
        for (char c : green) {
            if (white.contains(c)) {
                white.remove(Character.valueOf(c));
            }
        }
        for (char c : grey) {
            if (white.contains(c)) {
                white.remove(Character.valueOf(c));
            }
        }

        System.out.println("Historic color information:"+colors[1]+"Orange:"+orange+colors[3]+colors[0]+",Green:"+green+colors[3]+colors[2]+",Grey:"+grey+colors[3]+colors[3]+",white"+white+colors[3]);
        System.out.println("Remaining Attempts: "+model.getRemainingAttempts());
        System.out.println("-----------split line-----------");
    }

    public void error6Operation(){
        System.out.print("Rsults: ");
        for (int i = 0; i < model.getCurrentGuess().toString().length(); i++) {
            char c = model.getCurrentGuess().charAt(i);
            if(model.getColorArray()[i]==1){
                System.out.print(colors[1]+c);

                if(!isCharInList(green,c)){
                    addUnique(orange,c);
                }

            }else if(model.getColorArray()[i]==2){
                System.out.print(colors[0]+c);

                if(orange.indexOf(c)!=-1){
                    orange.remove(orange.indexOf(c));
                }

                addUnique(green,c);
            }else{
                System.out.print(colors[2]+c);

                addUnique(grey,c);
            }
        }
        System.out.print(colors[3]); //Reset colors to default values
        System.out.println(); // next Line
    }

    // adding elements to ensure that duplicate elements are not added
    public static void addUnique(ArrayList<Character> list, char element) {
        if (!list.contains(element)) {
            list.add(element);
        }
    }

    //Check if an element is in the list
    public static boolean isCharInList(ArrayList<Character> list, char target) {
        for (char c : list) {
            if (c == target) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        CLIApp app = new CLIApp();
        app.startGame();
    }
}
