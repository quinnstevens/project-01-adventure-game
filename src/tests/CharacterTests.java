package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import adventure_game.Character;
import adventure_game.Player;

import org.junit.jupiter.api.BeforeEach;

public class CharacterTests{

    private Character c;
    private Character attacker;
    @BeforeEach
    void setup(){
        c = new Player("Hero", 100, 9, 7);
        attacker = new Player("Other", 500, 10, 10);
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
        assertTrue(attacker.getHealth() == 500);
        c.attack(attacker);
        assertTrue(attacker.getHealth() < 500);
        c.setTempDamageBuff(attacker.getMaxHealth());
        c.attack(attacker);
        assertTrue(attacker.getHealth() == 0);

        attacker.setAsInvincible(2);
        c.attack(attacker);
        assertTrue(attacker.isInvincible() == true);
        c.attack(attacker);
        assertTrue(attacker.isInvincible() == false);
    }

    @Test
    void testDefend(){
        assertTrue(c.isInvincible() == false && c.isVulnerable() == false);
        c.defend(attacker);
        assertTrue(c.isInvincible() == true || c.isVulnerable() == true);
    }

    @Test
    void testVulnerable(){
        assertTrue(c.isVulnerable() == false);
        c.setAsVulnerable(3);

        assertTrue(c.getHealth() == 100);
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

    @Test
    void testHealthSwap(){
        assertTrue(c.getHealth() == 100);
        assertTrue(attacker.getHealth() == 500);
        c.healthSwap(attacker);
        assertTrue(c.getHealth() == 500);
        assertTrue(attacker.getHealth() == 100);
    }


    @Test
    void testCoinFlip(){
        assertTrue(c.getBaseDamage() == 7);
        assertTrue(attacker.getBaseDamage() == 10);

        c.coinFlip(attacker);

        assertTrue(c.getBaseDamage() == 14 || attacker.getBaseDamage() == 20);
    }
}