import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumberleModelTest {
    INumberleModel instance;

    @BeforeEach
    void setUp() {
        instance = new NumberleModel();
    }

    @AfterEach
    void tearDown() {
        instance = null;
    }

    //When the game starts, all the things is in initialize
    //error number is 0, don't represent anything
    //Input "123", is not a valid equation,so the game isn't win and the game isn't over
    //All the flags are default(since The flags are not be set, the flag3 is 0),So the target equation is fixed(which is set "3=4/2+1"),
    // and if I want to set the target is not fixed, the random equation must not be the "3=4/2+1"
    //Because inputting "123" is not a try,so the RemainingAttempts is 6
    //after initializing, the ColorArray is created, so the length of it is 7, and not be set color, so all elements is 0(0 means nothing)
    @Test
    void testInitialization() {
        instance.initialize();
        assertEquals("0", instance.getError());
        assertFalse(instance.isGameWon("123"));
        assertEquals("123", instance.getCurrentGuess().toString());
        assertFalse(instance.isGameOver());
        assertEquals(0, instance.getFlag3());
        assertEquals("3=4/2+1", instance.getTargetAttribute());
        assertNotEquals("3=4/2+1", instance.getTargetNumber());
        assertEquals(6, instance.getRemainingAttempts());


        assertEquals(7, instance.getColorArray().length);
        for (int i = 0; i < instance.getColorArray().length; i++) {
            assertEquals(0, instance.getColorArray()[i]);
        }
    }

    //When the game starts, all the things is in initialize ,error number is 0, target is "3=4/2+1"
    //Input "1+1+1=3", is a valid equation, but it's not same with target so the game isn't win and the game isn't over
    //and the error be set, is 6, means it's it is a valid equation but not the target
    //Since this is a valid equation, so it's a try, and the RemainingAttempts minus 1
    //the color array is set, so the elements of it are not be 0(it should be 1 or 2 or 3)
    //Input "3=4/2+1", is a valid equation, and it's same with target so the game is win and the game is over
    @Test
    void testProcessInput_ValidInput() {
        instance.initialize();
        assertEquals(0, instance.getFlag3());
        assertEquals("3=4/2+1", instance.getTargetAttribute());

        assertFalse(instance.isGameWon("1+1+1=3"));
        assertEquals("1+1+1=3", instance.getCurrentGuess().toString());
        assertFalse(instance.isGameOver());
        assertEquals("6", instance.getError());
        assertEquals(5, instance.getRemainingAttempts());
        for (int i = 0; i < instance.getColorArray().length; i++) {
            assertNotEquals(0, instance.getColorArray()[i]);
        }

        assertTrue(instance.isGameWon("3=4/2+1"));
        assertEquals("3=4/2+1", instance.getCurrentGuess().toString());
        assertTrue(instance.isGameOver());

    }

    //After initializing, set the flag2 and don't set the flag1, it means random the target
    //So the target is not the "3=4/2+1"
    //and input "3=4/2+1", the game isn't win and the game isn't over
    //and the error be set, is 6, means it's it is a valid equation but not the target
    //Since this is a valid equation, so it's a try, and the RemainingAttempts minus 1
    //the color array is set, so the elements of it are not be 0(it should be 1 or 2 or 3)

    //and input "1+1+1=3", the game isn't win and the game isn't over
    //and the error be set, is 6, means it's it is a valid equation but not the target
    //Since this is a valid equation, so it's a try, and the RemainingAttempts minus 1
    //the color array is set, so the elements of it are not be 0(it should be 1 or 2 or 3)

    //only input the same target,the game is win and the game is over
    @Test
    void testProcessInput_InvalidInput() {
        instance.initialize();
        instance.NotFlag1AndFlag2();
        assertNotEquals("3=4/2+1", instance.getTargetAttribute());

        assertFalse(instance.isGameWon("3=4/2+1"));
        assertEquals("3=4/2+1", instance.getCurrentGuess().toString());
        assertFalse(instance.isGameOver());
        assertEquals("6", instance.getError());
        assertEquals(5, instance.getRemainingAttempts());
        for (int i = 0; i < instance.getColorArray().length; i++) {
            assertNotEquals(0, instance.getColorArray()[i]);
        }

        assertFalse(instance.isGameWon("1+1+1=3"));
        assertEquals("1+1+1=3", instance.getCurrentGuess().toString());
        assertFalse(instance.isGameOver());
        assertEquals("6", instance.getError());
        assertEquals(4, instance.getRemainingAttempts());
        for (int i = 0; i < instance.getColorArray().length; i++) {
            assertNotEquals(0, instance.getColorArray()[i]);
        }

        assertTrue(instance.isGameWon(instance.getTargetAttribute()));
        assertTrue(instance.isGameOver());
    }
}