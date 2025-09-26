package adventure_game;

/**
 * Player class represents the player character in the game.
 * Inherits from the Character class.
 * Implements the takeTurn method to allow player actions during combat.
 * @see Character
 */
public class Player extends Character{
    /**
     * Constructor for Player class.
     * @param name The name of the player character.
     * @param health The initial health of the player character.
     * @param mana The initial mana of the player character.
     * @param baseDamage The base damage the player character can deal.
     * @see Character
     */
    public Player(String name, int health, int mana, int baseDamage){
        super(name, health, mana, baseDamage);
    }

    /**
     * Player takes their turn by choosing an action: attack, defend, or use an item.
     * Prompts the user for input to determine the action.
     * @param other The character that the player will interact with (e.g., attack or defend against).
     * @return void because it just performs an action.
     * @see Character#attack(Character)
     * @see Character#defend(Character)
     * @see Character#useItem(Character, Character)
     */
    @Override
    public void takeTurn(Character other){
        if(this.isStunned()){
            this.decreaseTurnsStunned();
            System.out.printf("%S is unable to take any actions this turn!", this.getName());
            return;
        }
        System.out.println();
        System.out.printf("%s has %d of %d health.\n", this.getName(), this.getHealth(), this.getMaxHealth());
        System.out.printf("%s has %d health.\n", other.getName(), other.getHealth());
        System.out.printf("Do you want to...\n");
        System.out.printf("  1: Attack?\n");
        System.out.printf("  2: Defend?\n");
        if(this.hasItems())
            System.out.printf("  3: Use an item?\n");
        System.out.printf("Enter your choice: ");

        int choice = Game.in.nextInt();
        switch(choice){
            case 1:
                this.attack(other);
                break;
            case 2:
                this.defend(other);
                break;
            case 3:
                if(hasItems()){
                    this.useItem(this, other);
                } else {
                    System.out.println("You dig through your bag but find no items. You lose a turn!!");
                }
                break;
        }
    }
}