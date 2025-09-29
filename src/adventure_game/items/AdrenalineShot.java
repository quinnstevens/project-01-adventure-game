package adventure_game.items;

import adventure_game.Character;
import adventure_game.Game;


/**
 * An Adrenaline Shot that temporarily boosts the character's damage.
 * Implements the Consumable interface.
 * @see Consumable
 */
public class AdrenalineShot implements Consumable {

    /**
     * Consumes the adrenaline shot, boosting the owner's damage for the next attack.
     * @param owner The character who consumes the adrenaline shot.
     * @see Character#setTempDamageBuff(int)
     */
    @Override
    public void consume(Character owner) {
        // Apply the adrenaline shot effect: triple the damage for the next attack
        owner.setTempDamageBuff(3);
        System.out.printf("\nYou feel a rush of energy! Your damage is tripled for the next attack.\n");
        owner.lastItemUsed = "Adrenaline Shot";
    }
}
