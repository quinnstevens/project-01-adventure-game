package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import adventure_game.Character;
import adventure_game.Player;
import adventure_game.items.AdrenalineShot;

import org.junit.jupiter.api.BeforeEach;

public class AdrenalineShotTest {
    
    private Character c;
    private Character attacker;

    @BeforeEach
    void setup(){
        c = new Player("Hero", 100, 10, 10);
        attacker = new Player("Attacker", 200, 10,10);
    }

    @Test
    void testAdrenalineShot(){
        c.obtain(new AdrenalineShot());
        assertTrue(c.getTempDamageBuff() == 1);
        c.getItems().get(0).consume(c);
        assertTrue(c.getTempDamageBuff() == 3);

        c.attack(attacker);
        assertTrue(attacker.getHealth()<200);
        assertTrue(c.getTempDamageBuff() == 1);
    }
}
