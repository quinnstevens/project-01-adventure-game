package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import adventure_game.Character;
import adventure_game.Player;

import org.junit.jupiter.api.BeforeEach;

public class CharacterTests{

    private Character c;
    @BeforeEach
    void setup(){
        c = new Player("Hero", 100, 9, 7);
    }

    @Test
    void testModifyHealth(){
        assertTrue(c.getHealth() == 100);
        c.modifyHealth(-10);
        assertTrue(c.getHealth() == 90);
        c.modifyHealth(-10000);
        assertTrue(c.getHealth() == 0);
        c.modifyHealth(50);
        assertTrue(c.getHealth() == 50);
        c.modifyHealth(10000);
        assertTrue(c.getHealth() == 100);
    }

    @Test
    void testAttack(){
        Character target = new Player("Target", 100, 0, 0);
        assertTrue(target.getHealth() == 100);
        c.attack(target);
        assertTrue(target.getHealth() < 100);
        c.setTempDamageBuff(target.getMaxHealth());
        c.attack(target);
        assertTrue(target.getHealth() == 0);

        target.setAsInvincible(2);
        c.attack(target);
        assertTrue(target.isInvincible() == true);
        c.attack(target);
        assertTrue(target.isInvincible() == false);
    }

    @Test
    void testDefend(){
        Character attacker = new Player("Attacker", 100, 0, 10);
        assertTrue(c.isInvincible() == false && c.isVulnerable() == false);
        c.defend(attacker);
        assertTrue(c.isInvincible() == true || c.isVulnerable() == true);
    }

    @Test
    void testVulnerable(){
        assertTrue(c.isVulnerable() == false);
        c.setAsVulnerable(3);

        assertTrue(c.getHealth() == 100);
        Character attacker = new Player("Attacker", 100, 0, 10);
        attacker.attack(c);
        assertTrue(c.getHealth() < 90);

        assertTrue(c.isVulnerable() == true);
        c.decreaseTurnsVulnerable();
        assertTrue(c.isVulnerable() == true);
        c.decreaseTurnsVulnerable();
        assertTrue(c.isVulnerable() == false);
    }

    @Test
    void testInvincible(){
        assertTrue(c.isInvincible() == false);
        c.setAsInvincible(3);

        assertTrue(c.getHealth() == 100);
        Character attacker = new Player("Attacker", 100, 0, 10);
        attacker.attack(c);
        assertTrue(c.getHealth() == 100);

        assertTrue(c.isInvincible() == true);
        c.decreaseTurnsInvincible();
        assertTrue(c.isInvincible() == true);
        c.decreaseTurnsInvincible();
        assertTrue(c.isInvincible() == false);
    }

    @Test
    void testStunned(){
        assertTrue(c.isStunned() == false);
        c.setAsStunned(2);
        assertTrue(c.isStunned() == true);
        c.decreaseTurnsStunned();
        assertTrue(c.isStunned() == true);
        c.decreaseTurnsStunned();
        assertTrue(c.isStunned() == false);
        c.decreaseTurnsStunned();
    }

    @Test
    void testItems(){
        assertTrue(c.hasItems() == false);
        c.obtain(new adventure_game.items.HealingPotion());
        assertTrue(c.hasItems() == true);
    }
}