package adventure_game;

/*
 * Project-01: Adventure Game
 * Name: Quinn Stevens
 */

import java.util.Scanner;

import adventure_game.items.HealingPotion;
import adventure_game.items.AdrenalineShot;
import adventure_game.items.EchoBell;

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

    /** Constructor for Game class. 
     * Currently does nothing but can be expanded later.
    */
    public Game() {
        
    }

    /**
     * Create a player character by prompting the user for input.
     * The player can customize their name, health, mana, and base damage.
     * The player has 20 points to distribute among health, mana, and base damage.
     * The created player is assigned to the 'player' attribute of the Game class.
     */
    public void createPlayer(){
        /* TO-DO */
        /* Modify this method to allow the user to create their own player */
        /* The user will specify the player's name and description, and spend */
        /* 20 points on health, mana, and baseDamage as they see fit. */
        System.out.println("Welcome to the Adventure Game!");
        System.out.print("Enter your character's name: ");
        String name = in.nextLine();
        int health = 0;
        int mana = 0;
        int baseDamage = 0;
        int points = 20;
        System.out.printf("\nYou have %d points to allocate to %s's health, damage, and mana.\n", points, name);
        while(points > 0){
            System.out.printf("\nYou have %d points remaining.\n", points);
            System.out.print("Enter health (1 point = 10 health): ");
            int h = in.nextInt();
            if(h < 0 || h > points){
                System.out.println("\nInvalid input. Try again.");
                continue;
            }
            health += h*10;
            points -= h;

            System.out.printf("\nYou have %d points remaining.\n", points);
            System.out.print("Enter mana (1 point = 3 mana): ");
            int m = in.nextInt();
            if(m < 0 || m > points){
                System.out.println("\nInvalid input. Try again.");
                continue;
            }
            mana += m*3;
            points -= m;

            System.out.printf("\nYou have %d points remaining.\n", points);
            System.out.print("Enter base damage (1 point = 1 damage): ");
            int d = in.nextInt();
            if(d < 0 || d > points){
                System.out.println("\nInvalid input. Try again.");
                continue;
            }
            baseDamage += d;
            points -= d;
        }
        player = new Player(name, health, mana, baseDamage);
        //player = new Player("The Hero", 100, 9, 7);
        player.obtain(new HealingPotion());
        player.obtain(new AdrenalineShot());
        player.obtain(new EchoBell());
    }

    /**
     * Enter combat between the player and an NPC opponent.
     * The combat continues in turns until either the player or the opponent is defeated.
     * Each turn, the player and opponent take actions against each other.
     * @param opponent The NPC character that the player will fight against.
     */
    public void enterCombat(NPC opponent){
        System.out.printf("%s and %s are in a brawl to the bitter end.\n", this.player.getName(), opponent.getName());
        while(true){
            this.player.takeTurn(opponent);
            if(!opponent.isAlive() && this.player.isAlive()){
                System.out.printf("%S is SLAIN!!\n",opponent.getName());
                break;
            }

            opponent.takeTurn(this.player);
            if(!this.player.isAlive() && this.player.lastLaugh){
                System.out.printf("%S has the last laugh and deals out %d damage with them!!\n",this.player.getName(), this.player.getBaseDamage()*5);
                this.player.setTempDamageBuff(5);
                this.player.attack(opponent);
            }
            if(!this.player.isAlive() && opponent.isAlive()){
                System.out.printf("%S is SLAIN!!\n",this.player.getName());
                break;
            }else if(!this.player.isAlive() && this.player.lastLaugh){
                System.out.printf("%S is SLAIN and takes out %S with them!!\n",this.player.getName(), opponent.getName());
                break;
            }
        }
    }
}
