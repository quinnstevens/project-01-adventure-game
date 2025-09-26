package adventure_game;

/**
 * Class representing a non-player character (NPC) in the adventure game.
 * NPCs can take turns, attack other characters, and be stunned.
 * Inherits from the Character class.
 * @see Character
 */
public class NPC extends Character{
    /**
     * Constructor for NPC class.
     * @param name The name of the NPC character.
     * @param health The maximum and starting health of the NPC character.
     * @param mana The maximum and starting mana of the NPC character.
     * @param baseDamage The base damage of the NPC character.
     */
    public NPC(String name, int health, int mana, int baseDamage){
        super(name, health, mana, baseDamage);
    }

    /** 
     * NPC takes its turn by attacking the other character, unless stunned.
     * @param other The character that the NPC will attack.
     * 
    */
    @Override
    public void takeTurn(Character other){
        if(this.isStunned()){
            this.decreaseTurnsStunned();
            System.out.printf("%S is unable to take any actions this turn!", this.getName());
            return;
        }
        this.attack(other);
    }
}