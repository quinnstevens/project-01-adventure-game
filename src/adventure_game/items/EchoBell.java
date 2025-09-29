package adventure_game.items;

import adventure_game.Character;
import adventure_game.Game;

/**
 * An Echo Bell that allows the character to reuse their last consumed item.
 * Implements the Consumable interface.
 * @see Consumable
 */
public class EchoBell implements Consumable {

    /**
     * Consumes the Echo Bell, allowing the owner to reuse their last consumed item.
     * @param owner The character who consumes the Echo Bell.
     * @see Character#useLastItem(Character)
     */
    @Override
    public void consume(Character owner) {
        if(!owner.lastItemUsed.equals("")){
            System.out.printf("\n%s rings the Echo Bell and uses their last item! (%s)\n", owner.getName(), owner.lastItemUsed);
        }
        owner.useLastItem(owner);
    }
}
