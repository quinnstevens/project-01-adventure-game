package adventure_game.items;

import adventure_game.Character;
import adventure_game.Game;


/**
 * A healing potion that restores health to the character who consumes it.
 * Implements the Consumable interface.
 * @see Consumable
 */
public class HealingPotion implements Consumable {

    /** Consumes the healing potion, restoring health to the owner.
     * @param owner The character who consumes the potion.
     * @return void because it just performs an action.
     */
    @Override
    public void consume(Character owner){
        int hitPoints = calculateHealing();

        // Calculate how much healing can actually be done
        int hitPointsFromMax = owner.getMaxHealth() - owner.getHealth();

        // Don't heal beyond max health
        if(hitPoints > hitPointsFromMax){
            hitPoints = hitPointsFromMax;
        }

        // Apply the healing
        owner.modifyHealth(hitPoints);
        // Provide feedback to the player
        System.out.printf("You heal for %d points, back up to %d/%d.\n", hitPoints, owner.getHealth(), owner.getMaxHealth());
    }


    /**
     * Calculate the amount of healing provided by the potion.
     * The healing amount is determined by rolling 4d4 and adding 4.
     * @return The total healing points calculated.
     */
    private int calculateHealing(){
        // Equivalent to rolling 4d4 + 4
        // sum up four random values in the range [1,4] and
        // add 4 to that.
        int points = Game.rand.nextInt(4)+1;
        points += Game.rand.nextInt(4)+1;
        points += Game.rand.nextInt(4)+1;
        points += Game.rand.nextInt(4)+1;
        return points + 4;
    }
}
