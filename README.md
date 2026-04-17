# Adventure Game

A text-based adventure game written in Java. The player creates a custom character, explores a series of interconnected rooms loaded from text files, fights enemies in turn-based combat, and collects items along the way.

## Gameplay

When you start, you allocate 20 points across health, mana, and base damage to build your character. You then navigate through two maps — The Stilts and the Dungeon — choosing which direction to move at each room. Rooms can contain enemies or items. Defeat all enemies or find the portkey to advance.

### Combat

Each turn the player chooses to attack, use a spell, or use a consumable item. Available spells:

- **Coin Flip** — 50/50 chance to double your damage or the opponent's
- **Health Swap** — swap current health totals with the opponent
- **Last Laugh** — if you die, deal 5× damage on the way out

Killing an enemy levels you up: max health +10, heal 50%, base damage +2.

### Items

- **Healing Potion** — restore health
- **Adrenaline Shot** — 3× base damage for one hit
- **Echo Bell** — repeat the last consumable used

### Map

Press 5 during exploration to view an ASCII map of the current level and your position.

### Bosses

Each map has a small chance to spawn a Boss enemy. Defeating a Boss drops a key that unlocks the path forward. Defeating all enemies in a map also clears a way out.

## Running

Compile and run from the project root:

```bash
javac -cp test-lib/junit-platform-console-standalone-1.9.3.jar -d classes src/adventure_game/*.java src/adventure_game/items/*.java
java -cp classes adventure_game.Game
```

Or run tests:

```bash
java -jar test-lib/junit-platform-console-standalone-1.9.3.jar --class-path classes --scan-class-path
```
