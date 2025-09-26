package tests;
import adventure_game.Character;
import adventure_game.Player;
import adventure_game.items.HealingPotion;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import adventure_game.*;
import java.util.Random;

public class HealingPotionTests {

    private HealingPotion potion;
    private Character c;
    @BeforeEach
    void setup(){
        // TO-DO 
        // Implement this
        potion = new HealingPotion();
        c = new Player("TestPlayer", 100, 50, 10);

    }

    @Test
    void testHealingPotion(){
        // TO-DO
        // Implement this
        c.modifyHealth(-30);
        assertTrue(c.getHealth() == 70);
        potion.consume(c);
        assertTrue(c.getHealth() > 70);
    }
}
