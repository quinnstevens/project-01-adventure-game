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
    protected int mana;

    private int baseDamage;

    private String name;

    private ArrayList<Consumable> items;

    /**
     * Indicates if the character has the Last Laugh condition active.
     * If true, the character will deal 5X damage to the opponent if they are slain.
     * @see #castSpell(Character)
     */
    protected boolean lastLaugh = false;

    /**
     * The name of the last consumable item used by this character.
     * This is used by items like the Echo Bell to allow reusing the last item.
     * @see #useLastItem(Character)
     */
    public String lastItemUsed = "";

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

    public ArrayList<Consumable> getItems(){
        return items;
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

    public double getTempDamageBuff(){
        return this.tempDamageBuff;
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
        //damage = this.getBaseDamage();

        if(other.isVulnerable()){
            damage *= 1.5;
            other.decreaseTurnsVulnerable();
        }

        System.out.printf("\n%s dealt %d damage to %s\n", 
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

    /**
     * Casts a spell, taking user input to find out which spell to use (with mana)
     * Health swap switches the health of the other character with this character
     * Health split cuts the others health in half
     * Flip a coin gives a 50-50 chance to double your base damage or the others base damage
     * Last laugh activates the condition that when you die, you automatically deal 5X damage to your opponent. Allowing for ties.
     * @param other the other character, for some condtional spells to be applied to.
     */
    public void castSpell(Character other){
        // Placeholder for spell casting logic
        System.out.printf("  1: Health Swap?\n");
        System.out.printf("  2: Health Split?\n");
        System.out.printf("  3: Flip a Coin (50/50 chance to double your base damage or opponent's base damage)?\n");
        System.out.printf("  4: Last Laugh (deal 5X damage to opponent if you are slain?\n");
        System.out.print("Enter your choice: ");

        int choice = Game.in.nextInt();
        switch(choice){
            case 1:
                healthSwap(other);
                break;
            case 2:
                halfHealth(other);
                break;
            case 3:
                coinFlip(other);
                break;
            case 4:
                this.lastLaugh = true;
                break;
            default:
                System.out.println("Invalid choice. You lose your turn!");
                break;
        }
    }


    /**
     * Swap this characters health with the other characters health
     * @param other character to swap health with
     */
    public void healthSwap(Character other){
        if(other.getMaxHealth() > this.getMaxHealth()){
            this.maxHealth = other.getMaxHealth();
        }
        int tempHealth = this.getHealth();
        this.modifyHealth(other.getHealth() - this.getHealth());
        other.modifyHealth(tempHealth - other.getHealth());
        System.out.printf("\n%s swaps health with %s!\n", this.getName(), other.getName());
        System.out.printf("%s now has %d health and %s now has %d health!\n", this.getName(), this.getHealth(), other.getName(), other.getHealth());
    }

    /**
     * flips a coin, 50-50 chance that your characters damage is doubled or the other characters damage is doubled
     * @param other the other character to potentially buff
     */

    public void coinFlip(Character other){
        double coinFlip = Game.rand.nextDouble();
        if(coinFlip < 0.5){
            other.baseDamage *= 2;
            System.out.printf("\n%s's damage is now %d!\n", other.getName(), other.getBaseDamage());
        } else {
            this.baseDamage *= 2;
            System.out.printf("\n%s's damage is now %d!\n", this.getName(), this.getBaseDamage());
        }
    }

    public void halfHealth(Character other){
        int halfHealth = other.getHealth()/2;
        other.modifyHealth(-halfHealth);
        System.out.printf("\n%s splits %s's health in half!\n", this.getName(), other.getName());
    }

    /* 
     * CONDITIONS
     * If vulnerable, Character takes 1.5x damage from attacks.
     * If invincible, Character takes no damage from attacks.
     * If stunned, Character cannot take any actions on their turn.
     * Each condition lasts for a set number of turns.
    */
    /**
     * Set the character as vulnerable for a number of turns.
     * @param numTurns The number of turns the character will be vulnerable.
     */
    public void setAsVulnerable(int numTurns){
        this.turnsVulnerable = numTurns;
    }


    /**
     * Check if the character is currently vulnerable.
     * @return true if the character is vulnerable, false otherwise.
     */
    public boolean isVulnerable(){
        return this.turnsVulnerable > 0;
    }

    /** Decrease the number of turns the character is vulnerable by one.
     * Should be called at the end of each turn.
     */
    public void decreaseTurnsVulnerable(){
        this.turnsVulnerable--;
    }

    /**
     * Set the character as invincible for a number of turns.
     * @param numTurns The number of turns the character will be invincible.
     */
    public void setAsInvincible(int numTurns){
        this.turnsInvincible = numTurns;
    }

    /**
     * Check if the character is currently invincible.
     * @return true if the character is invincible, false otherwise.
     */
    public boolean isInvincible(){
        return this.turnsInvincible > 0;
    }

    /** Decrease the number of turns the character is invincible by one.
     * Should be called at the end of each turn.
     */
    public void decreaseTurnsInvincible(){
        this.turnsInvincible--;
    }

    /**
     * Set the character as stunned for a number of turns.
     * @param numTurns The number of turns the character will be stunned.
     */
    public void setAsStunned(int numTurns){
        this.turnsStunned = numTurns;
    }

    /**
     * Check if the character is currently stunned.
     * @return true if the character is stunned, false otherwise.
     */
    public boolean isStunned(){
        return this.turnsStunned > 0;
    }

    /** Decrease the number of turns the character is stunned by one.
     * Should be called at the end of each turn.
     */
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

     /**
      * Obtain a consumable item and add it to the character's inventory.
      * @param item The consumable item to be added.
      */
    public void obtain(Consumable item){
        items.add(item);
    }

    /**
     * Use a consumable item from the character's inventory.
     * Prompts the user to choose which item to use.
     * @param owner The character who is using the item.
     * @param other The other character involved in the action (if applicable).
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

    /**
     * Reuse the last consumed item.
     * If no item has been used yet, informs the user.
     * @param owner The character who is reusing the last item.
     * @see #lastItemUsed
     * @see #obtain(Consumable)
     */
    public void useLastItem(Character owner){
        if(this.lastItemUsed.equals("")){
            System.out.printf("\nYou have not used any items yet!\n");
            return;
        }else if(this.lastItemUsed.equals("Healing Potion")){
            owner.obtain(new adventure_game.items.HealingPotion());
        } else if(this.lastItemUsed.equals("Adrenaline Shot")){
            owner.obtain(new adventure_game.items.AdrenalineShot());
        }
        items.get(items.size()-1).consume(owner);
        items.remove(items.size()-1);
    }

    /**Check if the character has any items in their inventory. 
     * @return true if the character has at least one item, false otherwise.
    */
    public boolean hasItems(){
        return !items.isEmpty();
    }
}
