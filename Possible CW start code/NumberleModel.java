// NumberleModel.java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NumberleModel extends Observable implements INumberleModel {
    int lineNumber=0;
    private String targetNumber;
    private StringBuilder currentGuess;
    private int remainingAttempts;
    private boolean gameWon;
    private int[] colorArray = new int[7]; ;//0 represents colorless, 1 represents orange, 2 represents green, and 3 represents gray
    private String error="0";
    private int flag3=0;

    //Operation after choosing flag1 and flag2
    public void flag1AndFlag2(){
        assert targetNumber!=null;
        setNotFixed();
        System.out.println("Target: "+targetNumber);
        assert !targetNumber.equals("3=4/2+1");
    }

    //Operation after choosing flag1 and doesn't choose flag2
    public void flag1AndNotFlag2(){
        assert targetNumber!=null;
        System.out.println("Target: "+targetNumber);
    }

    //Operation after choosing flag2 and doesn't choose flag3
    public void NotFlag1AndFlag2(){
        assert targetNumber!=null;
        setNotFixed();
        assert !targetNumber.equals("3=4/2+1");
    }

    public int getFlag3(){
        return flag3;
    }

    public void setFlag3(){
        flag3=1;

        setChanged();
        notifyObservers();
    }

    @Override
    public String getError(){
        return error;
    }

    //Different error values represent different error situations
    public void setError(String i){
        assert !i.equals("0");
        error=i;
    }

    @Override
    public int[] getColorArray() {
        return colorArray;
    }

    //set the color information in form of array
    public void setColorArray(int positionNum, int colorNum){
        assert positionNum>=0 && positionNum<=6;
        assert colorNum==1 || colorNum==2 || colorNum==3;

        colorArray[positionNum]=colorNum;
    }
    @Override
    public StringBuilder getCurrentGuess() {
        return currentGuess;
    }
    @Override
    public String getTargetAttribute(){
        return targetNumber;
    }
    @Override
    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public boolean invariant(){
        return MAX_ATTEMPTS==6 && remainingAttempts>=0;
    }

    /** @pre.	linenumber has been generated and is within the range
     * @post.	targetNumber(target equation) is not "3=4/2+1"
     The status of the Observable object has been updated and observers have been notified.*/
    public void setNotFixed(){
        targetNumber=getTargetNumber();

        setChanged();
        notifyObservers();

        assert !targetNumber.equals("3=4/2+1");
    }


    /** @post.	The game model has been successfully initialized.
     * The targetNumber and colorArray have been set to the initial state.
     * The random number row number has been generated and set.
     * The currentGuess object is instantiated and left empty.
     * RetainingAttempts is set to the initial maximum number of attempts: 6.
     * GameWon is set to false.
     * The error is set to "0".
     * ColorArray is initialized as an array of length 7 and filled with the default value of 0.
     * The target formula is set to a fixed string of "6 * 1-2=4".
     * The status of the Observable object has been updated and observers have been notified.
     */
    @Override
    public void initialize() {
        //Set row random number
        //The starting target formula is fixed,and this is the equation on the last line
        //The generated random number(lineNumber) does not include the last row
        //so the formula generated randomly will not be the same as the fixed formula
        Random rand = new Random();
        lineNumber=rand.nextInt(107)+1;
        targetNumber="3=4/2+1";

        currentGuess = new StringBuilder();
        remainingAttempts = MAX_ATTEMPTS;
        gameWon = false;

        error="0";
        colorArray = new int[7];

        setChanged();
        notifyObservers();

        assert remainingAttempts==6;
        assert invariant():"invariant must be true initially";
        assert !gameWon;
        assert error.equals("0");
        assert colorArray.length==7;
        assert targetNumber.equals("3=4/2+1");
    }

    @Override
    public void startNewGame() {
        initialize();
    }

    /** @pre.	text is String,invariant() is true
     * @post.	the currentGuess be set, return boolean to show if the game is win
     * invariant() is true
     * The status of the Observable object has been updated and observers have been notified.*/
    @Override
    public boolean isGameWon(String text) {
        assert text!=null;
        assert invariant():"invariant must be true initially";

        currentGuess.setLength(0);
        currentGuess.append(text);
        gameWon=processInput(getCurrentGuess().toString());

        //The variables of the model have changed in the processInput() method,
        //so only one notification is needed here, and there is no need to notify in other methods
        setChanged();
        notifyObservers();

        assert invariant():"invariant must be maintained";

        return gameWon;


    }
    /** @pre.	invariant() is true
     * @post.	The return value is either true or false, indicating that the game introduction is not over or not yet finished
     */
    @Override
    public boolean isGameOver() {
        //Returns false, meaning that if both are false, there is no end;
        //Returns true, meaning that if either of the two is true, the game ends.
        assert invariant():"invariant must be maintained";

        return remainingAttempts <= 0 || gameWon;

    }


    /** @pre.	input is String
     * @post.	the error number and the colorArray be set,
     * return boolean to show the input if equal to the target
     */
    public boolean processInput(String input) {
        assert input!=null:	"input must exist";
        assert invariant():"invariant must be true initially";

        if(!isValidExpression(input)){
            assert !getError().equals("0");
            return false;//Not a valid equation (equation holds and length is 7), return false directly
        }else{
            //It is a valid equation to compare with the target
            if(input.equals(targetNumber)){
                assert !getError().equals("0");
                return true;//Directly return true if the same is true
            }else{
                //If different, set the color array
                for (int i = 0; i < input.length(); i++) {
                    char currentChar = input.charAt(i);
                    // Determine if the current character is in string B
                    int indexInB = targetNumber.indexOf(currentChar);
                    if (indexInB != -1) {
                        // The position of characters in B
                        int indexInA = i;
                        if (indexInA == indexInB) {
                            // If the position of characters in A and B is the same, set it to 2
                            setColorArray(indexInA,2);
                        } else {
                            // If the position of the character in A and B is different, set it to 1
                            setColorArray(indexInA,1);
                        }
                    } else {
                        //Character not in B, set to 3
                        setColorArray(i,3);
                    }
                }
                remainingAttempts--;
                setError("6");

                assert !getError().equals("0");
                assert invariant():"invariant must be maintained";

                return false;//Is a valid equation (the equation holds and the length is 7),
            }
        }
    }

    /** @pre.	input is String
     * @post.	the error number be set
     * return boolean to show the input if equal to the target
     */
    public boolean isValidExpression(String input) {
        assert input != null;

        if (input.isEmpty()) {
            setError("1");
            return false; // An empty string is not a valid expression
        }

        // Using regular expressions to check for operator inclusion
        if (!input.matches(".*[+\\-*/].*")) {
            setError("7");
            return false; // Does not contain any operator
        }

        if (!input.contains("=")) {
            setError("3");
            return false; // Missing equal sign
        }

        if (input.length() != 7) {
            setError("2");
            return false; // The length is not 7
        }

        // Contains duplicate operators
        if (input.contains("++") || input.contains("--") || input.contains("**")||input.contains("//")||input.contains("==") || input.contains("+-") ||
                input.contains("-+") || input.contains("+*")||input.contains("*+")||input.contains("+/")||input.contains("/+")||input.contains("+=")||
                input.contains("=+") || input.contains("-*") || input.contains("*-")||input.contains("-/")||input.contains("/-")||input.contains("-=")||
                input.contains("=-")||input.contains("*/")||input.contains("/*")||input.contains("*=")||input.contains("=*")||input.contains("/=")||input.contains("=/"))
        {
            setError("8");
            return false; //Contains duplicate operators
        }

        //If there is a continuous equal sign, it indicates that the left and right sides are not equal
        assert input.contains("=");

        String[] parts = input.split("=");

        String leftSide = parts[0].trim();
        String rightSide = parts[1].trim();

        try {
            double leftValue = evaluateExpression(leftSide);
            double rightValue = evaluateExpression(rightSide);

            if (leftValue == rightValue) {
                return true;
            } else {
                setError("4");//Unequal left and right
                return false;
            }

        } catch (NumberFormatException e) {
            setError("5");
            return false; // The right side cannot be resolved to a numerical value
        }
    }

    /** @pre.	expression is String, and it contains of number or operators
     * @post.	return the calculated value of the String
     */
    public static double evaluateExpression(String expression) {
        assert expression!=null;

        char[] tokens = expression.toCharArray();

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        // Define operator priority
        Map<Character, Integer> precedence = new HashMap<>();
        precedence.put('+', 1);
        precedence.put('-', 1);
        precedence.put('*', 2);
        precedence.put('/', 2);

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] == ' ')
                continue;

            assert (Character.isDigit(tokens[i]) || tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') :
                    "Invalid character in expression: " + tokens[i];

            if (Character.isDigit(tokens[i])) {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length && Character.isDigit(tokens[i])) {
                    sb.append(tokens[i++]);
                }
                numbers.push(Double.parseDouble(sb.toString()));
                i--;
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!operators.isEmpty() && precedence.get(operators.peek()) >= precedence.get(tokens[i])) {
                    applyOperation(numbers, operators);
                }
                operators.push(tokens[i]);
            }
        }
        while (!operators.isEmpty()) {
            applyOperation(numbers, operators);
        }
        return numbers.pop();
    }


    /** @pre.	The operand stack numbers and operator stack operators are not empty.
     *  The operand stack numbers contain at least two operands.
     *  The operator stack operators contain at least one operator.
     *  All operands and operators popped from the stack are valid.
     *  The operators in the operator stack operators must be valid (for example,+, -, *,/).
     * @post. Pop two operands from the operand stack numbers and pop one operator from the operator stack operators for calculation.
     * The calculation result is pushed into the operand stack numbers.
     * If the operator is an addition, perform the addition of operands.
     * If the operator is subtraction, subtract the operands.
     * If the operator is multiplication, perform operands multiplication.
     * If the operator is division, the operands are divided.
     * If the operator is not recognized, throw an IllegalArgumentException exception.
     */
     //Applying Operators to Numbers
    public static void applyOperation(Stack<Double> numbers, Stack<Character> operators) {
        assert !operators.isEmpty() : "No operator to apply";
        assert numbers.size() >= 2 : "Insufficient operands for operation";

        char operator = operators.pop();
        double operand2 = numbers.pop();
        double operand1 = numbers.pop();
        double result = performOperation(operand1, operand2, operator);
        numbers.push(result);
    }


    /** @pre.	Operand1 and operand2 are valid double precision floating-point numbers.
     * Operator is a valid operator (+, -, *,/).
     * @post. If the operator is'+', return the result of adding operand1 and operand2.
     * If the operator is' - ', return the result of operand1 minus operand2.
     * If the operator is' * ', return the result of multiplying operand1 by operand2.
     * If the operator is'/', return the result of operand1 divided by operand2. If operand2 is zero, an ArithmeticException is thrown, indicating a divide by zero error.
     * If the operator is not a valid operator, throw IllegalArgumentException to indicate an unknown operator error.
     */
    // Perform specific operations
    public static double performOperation(double operand1, double operand2, char operator) {
        assert Double.isFinite(operand1) && Double.isFinite(operand2) :
                "The operand is not a finite value:" + operand1 + ", " + operand2;

        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }


    /** @pre.	The file path is correct, the file exists and the lineNumber is in the correct range
     * @post.	return a String contained the random target equation
     */
    @Override
    public String getTargetNumber() {
        assert lineNumber>=1 && lineNumber<=107;
        //Return a random formula based on the file
        String filename = "C://Users//17274//Desktop//英方文件//AOOP//coursework//equations.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            int currentLine = 1;
            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    targetNumber=line;
                    break;
                }
                currentLine++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            targetNumber="";
        }

        assert !targetNumber.isEmpty();

        return targetNumber;
    }
}
