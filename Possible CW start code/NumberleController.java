// NumberleController.java
public class NumberleController {
    private INumberleModel model;
    private NumberleView view;

    public NumberleController(INumberleModel model) {
        this.model = model;
    }
    public void setView(NumberleView view) {this.view = view;}
    public boolean isGameOver() {return model.isGameOver();}
    public boolean isGameWon(String text) {
        return model.isGameWon(text);
    }
    public StringBuilder getCurrentGuess() {
        return model.getCurrentGuess();
    }
    public int getRemainingAttempts() {
        return model.getRemainingAttempts();
    }
    public void startNewGame() {
        model.startNewGame();
    }
    public int[] getColorArray(){
        return model.getColorArray();
    }
    public void setFlag3(){model.setFlag3();}

    public void flag1AndFlag2(){model.flag1AndFlag2();}

    public void flag1AndNotFlag2(){model.flag1AndNotFlag2();}

    public void NotFlag1AndFlag2(){model.NotFlag1AndFlag2();}
}