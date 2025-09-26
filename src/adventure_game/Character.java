package adventure_game;
import java.util.ArrayList;

import adventure_game.items.Consumable;
/**
 * Abstract class representing a character in the adventure game.
 * This can be a player or a non-player character (NPC).
 * It includes attributes like health, mana, base damage, and name.
 * It also manages conditions like vulnerability, invincibility, and stun.
 * Characters can attack, defend, take turns, and use consumable items.
 */
abstract public class Character{
    private int maxHealth;
    private int health;

    private int maxMana;
    private int mana;

    private int baseDamage;

    private String name;

    private ArrayList<Consumable> items;

    // Character Conditions:
    private int turnsVulnerable; // number of turns Character is vulnerable
    private int turnsInvincible; // number of turns Character takes no damage
    private int turnsStunned; // number of turns Character gets no actions
    // buffer factor for next attack
    // E.g, if 2.0, the next attack will do double damage
    private double tempDamageBuff;


    /**
     * Constructor for Character class.
     * @param name The name of the character.
     * @param health The maximum and starting health of the character.
     * @param mana The maximum and starting mana of the character.
     * @param damage The base damage the character can deal.
     */
    public Character(String name, int health, int mana, int damage){
        this.name = name;
        this.maxHealth = health;
        this.health = health;
        this.maxMana = mana;
        this.mana = mana;
        this.baseDamage = damage;
        this.tempDamageBuff = 1.0;
        items = new ArrayList<Consumable>();
    }

    /**
     * Returns a string representation of the character's attributes.
     * @return A formatted string with the character's name, health, mana, and base damage.
     */
    @Override
    public String toString(){
        String output;
        output = "";
        output += "Name " + getName() + "\n";
        output += "hp " + getHealth() + "\n";
        output += "mana " + getMana() + "\n";
        output += "damage " + getBaseDamage() + "\n";
        return output;
    }

    /**
     * Get the name of this Character
     * @return the name of this Character
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get the current health of this Character
     * @return the current health of this Character
     */
    public int getHealth(){
        return this.health;
    }

    /** 
     * Get the maximum health of this Character
     * @return the maximum health of this Character
     */
    public int getMaxHealth(){
        return this.maxHealth;
    }

    /**
     * Get the maximum mana of this Character
     * @return the maximum mana of this Character
     */
    public int getMaxMana(){
        return this.maxMana;
    }

    /**
     * Get the current mana of this Character
     * @return the current mana of this Character
     */
    public int getMana(){
        return this.mana;
    }

    /**
     * Get the base damage of this Character
     * @return the base damage of this Character
     */
    public int getBaseDamage(){
        return this.baseDamage;
    }

    /**
     * Check if the character is alive (health > 0).
     * @return true if the character's health is greater than 0, false otherwise.
     */
    public boolean isAlive(){
        return this.health > 0;
    }

    /**
     * Abstract method for taking a turn in combat.
     * Each subclass must implement its own version of this method.
     * @param other
     */
    abstract void takeTurn(Character other);


    /**
     * Attack another character, dealing damage based on base damage and random modifier.
     * Considers conditions like invincibility and vulnerability.
     * @param other The character being attacked.
     * @return void because it just performs an action.
     */
    public void attack(Character other){
        if(other.isInvincible()){
            System.out.printf("%S is unable to attack %S!\n", 
                                this.getName(), 
                                other.getName());
            other.decreaseTurnsInvincible();
            return;
        }
        double modifier = Game.rand.nextDouble();
        modifier = (modifier*0.4) + 0.8;
        int damage = (int)(this.baseDamage * modifier);
        // apply temporary damage buff, then reset it back to 1.0
        damage *= this.tempDamageBuff;
        this.tempDamageBuff = 1.0;

        if(other.isVulnerable()){
            damage *= 1.5;
            other.decreaseTurnsVulnerable();
        }

        System.out.printf("%s dealt %d damage to %s\n", 
                            this.getName(), 
                            damage, 
                            other.getName());
        other.modifyHealth(-damage);
    }

    /**
     * Defend to reduce incoming damage and set up for next turn.
     * Has a 75% chance to become invincible for one turn and gain a damage
     * buff for the next attack. Otherwise, becomes vulnerable for one turn.
     * @param other The character being defended against.
     * @return void because it just performs an action.
     */
    public void defend(Character other){
        double chance = Game.rand.nextDouble();
        if(chance <=0.75){
            System.out.printf("%s enters a defensive posture and charges up their next attack!\n", this.getName());
            this.setAsInvincible(1);
            this.setTempDamageBuff(2.0);
        } else {
            System.out.printf("%s stumbles. They are vulnerable for the next turn!\n", this.getName());
            this.setAsVulnerable(1);
        }
    }
    
    /**
     * Modify the character's health by a given amount.
     * Ensures that health does not exceed maximum health or drop below zero.
     * @param modifier The amount to modify the health by (positive or negative).
     */
    public void modifyHealth(int modifier) {
        this.health += modifier;
        if(this.health < 0){
            this.health = 0;
        }
        if(this.health > this.getMaxHealth()){
            this.health = this.getMaxHealth();
        }
    }

    /* 
     * CONDITIONS
     * If vulnerable, Character takes 1.5x damage from attacks.
     * If invincible, Character takes no damage from attacks.
     * If stunned, Character cannot take any actions on their turn.
     * Each condition lasts for a set number of turns.
    */
    public void setAsVulnerable(int numTurns){
        this.turnsVulnerable = numTurns;
    }

    public boolean isVulnerable(){
        return this.turnsVulnerable > 0;
    }

    public void decreaseTurnsVulnerable(){
        this.turnsVulnerable--;
    }

    public void setAsInvincible(int numTurns){
        this.turnsInvincible = numTurns;
    }

    public boolean isInvincible(){
        return this.turnsInvincible > 0;
    }

    public void decreaseTurnsInvincible(){
        this.turnsInvincible--;
    }

    public void setAsStunned(int numTurns){
        this.turnsStunned = numTurns;
    }

    public boolean isStunned(){
        return this.turnsStunned > 0;
    }

    public void decreaseTurnsStunned(){
        this.turnsStunned--;
    }

    /**
     * Set the temporary damage buff. 
     * 
     * This is a multiplicative factor which will modify the damage 
     * for the next attack made by this Character. After the next 
     * attack, it will get reset back to 1.0
     * 
     * @param buff the multiplicative factor for the next attack's
     * damage.
     */
    public void setTempDamageBuff(double buff){
        this.tempDamageBuff = buff;
    }
    /*
     * ITEMS
     * Characters can hold and use consumable items.
     * Currently, only HealingPotions are implemented.
     * More item types can be added by implementing the Consumable interface.
     */

     /*
      * Obtain a consumable item and add it to the character's inventory.
      * @param item The consumable item to be added.
      * @return void because it just performs an action.
      */
    public void obtain(Consumable item){
        items.add(item);
    }

    /*
     * Use a consumable item from the character's inventory.
     * Prompts the user to choose which item to use.
     * @param owner The character who is using the item.
     * @param other The other character involved in the action (if applicable).
     * @return void because it just performs an action.
     */

    public void useItem(Character owner, Character other){
        int i = 1;
        System.out.printf("  Do you want to use:\n");
        for(Consumable item : items){
            System.out.printf("    %d: %S\n", i, item.getClass().getName());
            i++;
        }
        System.out.print("  Enter your choice: ");
        int choice = Game.in.nextInt();
        items.get(choice-1).consume(owner);
        items.remove(choice-1);
    }

    /* 
    Check if the character has any items in their inventory. 
     * @return true if the character has at least one item, false otherwise.
    */
    public boolean hasItems(){
        return !items.isEmpty();
    }
}
