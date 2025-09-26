package adventure_game;

/*
 * Project-01: Adventure Game
 * Name: Quinn Stevens
 */

import java.util.Scanner;

import adventure_game.items.HealingPotion;

import java.util.Random;


/**
 * Main class to run the adventure game.
 * Handles player creation and combat encounters.
 * Uses Scanner for user input and Random for random number generation.
 */
public class Game {
    static Scanner in = new Scanner(System.in);
    public static Random rand = new Random();
    private Player player;
    
    public static void main(String[] args){

        Game game = new Game();

        game.createPlayer();
        System.out.println(game.player.toString());

        NPC opponent = new NPC("Geoff", 200, 0, 10);
        System.out.println(opponent.toString());
        game.enterCombat(opponent);

        in.close();
    }

    /* 
    Constructor for Game class. 
     * Currently does nothing but can be expanded later.
    */
    public Game() {
        
    }

    /*
     * Create a player character by prompting the user for input.
     * The player can customize their name, health, mana, and base damage.
     * The player has 20 points to distribute among health, mana, and base damage.
     * The created player is assigned to the 'player' attribute of the Game class.
     * @return void because it just performs an action.
     */
    public void createPlayer(){
        /* TO-DO */
        /* Modify this method to allow the user to create their own player */
        /* The user will specify the player's name and description, and spend */
        /* 20 points on health, mana, and baseDamage as they see fit. */
        player = new Player("The Hero", 100, 9, 7);
        player.obtain(new HealingPotion());
    }

    /*
     * Enter combat between the player and an NPC opponent.
     * The combat continues in turns until either the player or the opponent is defeated.
     * Each turn, the player and opponent take actions against each other.
     * @param opponent The NPC character that the player will fight against.
     * @return void because it just performs an action.
     */
    public void enterCombat(NPC opponent){
        System.out.printf("%s and %s are in a brawl to the bitter end.\n", this.player.getName(), opponent.getName());
        while(true){
            this.player.takeTurn(opponent);
            if(!opponent.isAlive()){
                System.out.printf("%S is SLAIN!!\n",opponent.getName());
                break;
            }

            opponent.takeTurn(this.player);
            if(!this.player.isAlive()){
                System.out.printf("%S is SLAIN!!\n",this.player.getName());
                break;
            }
        }
    }
}
