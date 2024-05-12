public interface INumberleModel {
    int MAX_ATTEMPTS = 6;
    String getError();
    void initialize();
    boolean isGameOver();
    boolean isGameWon(String text);
    String getTargetNumber();
    StringBuilder getCurrentGuess();
    int getRemainingAttempts();
    void startNewGame();
    int[] getColorArray();
    String getTargetAttribute();
    int getFlag3();
    void setFlag3();
    void flag1AndFlag2();
    void flag1AndNotFlag2();
    void NotFlag1AndFlag2();
}