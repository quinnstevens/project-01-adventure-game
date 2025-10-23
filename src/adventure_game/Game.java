package adventure_game;

/*
 * Project-01: Adventure Game
 * Name: Quinn Stevens
 */

import java.util.Scanner;

import adventure_game.items.HealingPotion;
import adventure_game.items.AdrenalineShot;
import adventure_game.items.Consumable;
import adventure_game.items.EchoBell;

import java.util.Random;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;

/**
 * Main class to run the adventure game.
 * Handles player creation and combat encounters.
 * Uses Scanner for user input and Random for random number generation.
 */
public class Game {

    static Scanner in = new Scanner(System.in);
    public static Random rand = new Random();
    private Player player;

    public static boolean run = true;
    
    public static void main(String[] args){

        String filePath = "data/levels/the-stilts.txt";

        String filePath2 = "data/levels/dungeon.txt";

        Game game = new Game();

        //ArrayList<Room> rooms = game.createMap(filePath);
        //Room.setCurrentRoom(rooms.get(0));
        //rooms.get(0).currentRoom = 0;
        //System.out.println(rooms.get(rooms.get(0).currentRoom).toString());
        //System.out.println(Room.getCurrentRoom().toString());

        game.createPlayer();
        System.out.println(game.player.toString());

        game.enterExploration(filePath);

        if(run == true){
            game.enterExploration(filePath2);
        }

        //NPC opponent = new NPC("Geoff", 200, 0, 10);
        //System.out.println(opponent.toString());
        //game.enterCombat(opponent);

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

    /**
     * Create a map of rooms by reading room data from a file.
     * The file should contain room definitions and their connections.
     * Each room is created and linked to its neighboring rooms based on the data in the file.
     * Randomly assigns NPCs and items to rooms based on defined probabilities.
     * @param file The path to the file containing room data.
     * @return An ArrayList of Room objects representing the created map.
     * @see Room
     */
    public ArrayList<Room> createMap(String file){
        int roomCount;

        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<Room> roomList = new ArrayList<Room>();
        //ArrayList<Integer> createdRooms = new ArrayList<Integer>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.charAt(0) != '#'){
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        //System.out.printf("########## %s", lines.get(0));
        roomCount = Integer.parseInt(lines.get(0));
        //System.out.println("Number of rooms:\n" + roomCount);
        for(int i = 0; i < roomCount; i++){
            /* 
            int east = Integer.parseInt((lines.get(i + 1+roomCount).split(":")[1]).replaceAll("\\s", ""));
            int north = Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[2]).replaceAll("\\s", ""));
            int west = Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[3]).replaceAll("\\s", ""));
            int south = Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[4]).replaceAll("\\s", ""));

            if(east != -1){
                createdRooms.add(east);
            }
            if(west != -1){
                createdRooms.add(west);
            }
            if(north != -1){
                createdRooms.add(north);
            }
            if(south != -1){
                createdRooms.add(south);
            }*/


            //roomList.add(new Room((lines.get(i + 1)).split(":")[1], (lines.get(i + 1)).split(":")[2], Integer.parseInt((lines.get(i + 1+roomCount).split(":")[1]).replaceAll("\\s", "")), Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[2]).replaceAll("\\s", "")), Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[3]).replaceAll("\\s","")), Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[4]).replaceAll("\\s", ""))));
            roomList.add(new Room((lines.get(i + 1)).split(":")[1], (lines.get(i + 1)).split(":")[2], null, null, null, null));

            Room.setCurrentRoom(roomList.get(0));


        }
        int i = 0;
        for(Room r : roomList){
            Random random = new Random();
            int east = Integer.parseInt((lines.get(i + 1+roomCount).split(":")[1]).replaceAll("\\s", ""));
            int north = Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[2]).replaceAll("\\s", ""));
            int west = Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[3]).replaceAll("\\s", ""));
            int south = Integer.parseInt((lines.get(i + 1 + roomCount).split(":")[4]).replaceAll("\\s", ""));

            if(east != -1){
                r.setRoomEast(roomList.get(east));
            }
            if(west != -1){
                r.setRoomWest(roomList.get(west));
            }
            if(north != -1){
                r.setRoomNorth(roomList.get(north));
            }
            if(south != -1){
                r.setRoomSouth(roomList.get(south));
            }

            int rand = random.nextInt(100);
            if(rand <= 20){
                r.setOpponent(new NPC("Gremlin", random.nextInt(200) + 100, 0, random.nextInt(20) + 5));
            }else if(rand >= 95){
                r.setOpponent(new NPC("Boss", random.nextInt(300) + 300, 0, random.nextInt(10) + 30));
            }

            int rand2 = random.nextInt(100);
            if(rand2 <= 30){
                r.addItem(new AdrenalineShot());
            }else if(rand2 <= 50){
                r.addItem(new HealingPotion());
            }else if(rand2 <= 60){
                r.addItem(new EchoBell());
            }

            i++;
        }

        Random random = new Random();

        //roomList.get(3).setOpponent(new NPC("Boss", random.nextInt(300) + 200, 0, random.nextInt(20) + 15));

        roomList.get(roomList.size()-1).setPortKey();

        //MapPrinter.printAsciiMap(roomList);
        
        return roomList;
    }

    /**
     * Enter exploration mode in the game, allowing the player to navigate through rooms.
     * The player can choose directions to move to adjacent rooms.
     * The exploration continues until the player finds a portkey or defeats all enemies.
     * @param filePath The path to the file containing room data.
     */
    public void enterExploration(String filePath){
        boolean endGame = false;
        ArrayList<Room> rooms = createMap(filePath);
        Room.setCurrentRoom(rooms.get(0));
        if(this.run == true){

            System.out.printf("------------------------------------------------------\nYou start in the%s!%s\n", Room.getCurrentRoom().getRoomID(), Room.getCurrentRoom().getRoomDescription());
        }
        while (!endGame && this.run) { 
            System.out.println("\nExits:");
            
            Room exitEast = Room.getCurrentRoom().getRoomEast();
            Room exitWest = Room.getCurrentRoom().getRoomWest();
            Room exitNorth = Room.getCurrentRoom().getRoomNorth();
            Room exitSouth = Room.getCurrentRoom().getRoomSouth();

            System.out.println("Where would you like to go?");
            if(exitNorth != null){System.out.printf("1. The%s. (North)\n", exitNorth.getRoomID());}else{System.out.println("1. Exit blocked.");}
            if(exitSouth != null){System.out.printf("2. The%s. (South)\n", exitSouth.getRoomID());}else{System.out.println("2. Exit blocked.");}
            if(exitEast != null){System.out.printf("3. The%s. (East)\n", exitEast.getRoomID());}else{System.out.println("3. Exit blocked.");}
            if(exitWest != null){System.out.printf("4. The%s. (West)\n", exitWest.getRoomID());}else{System.out.println("4. Exit blocked.");}
            System.out.println("5. View Map.");
            System.out.println("Where would you like to go?");
            int choice = Game.in.nextInt();

            int count = 0;

            for(Room a : rooms){
                if(a.isEnemy()){
                    count++;
                }
            }
            if(count == 0){
                this.run = false;
            }
            
            switch(choice){
                case 1:
                    if(exitNorth != null){
                        enterRoom(exitNorth);
                    }else{
                        System.out.println("The exit is blocked!");
                    }
                    break;
                case 2:
                    if(exitSouth != null){
                        enterRoom(exitSouth);
                    }else{
                        System.out.println("The exit is blocked!");
                    }
                    break;
                case 3:
                    if(exitEast != null){
                        enterRoom(exitEast);
                    }else{
                        System.out.println("The exit is blocked!");
                    }
                    break;
                case 4:
                    if(exitWest != null){
                        enterRoom(exitWest);
                    }else{
                        System.out.println("The exit is blocked!");
                    }
                    break;
                case 5:
                    MapPrinter.printAsciiMap(rooms);
                    break;
                default:
                    System.out.println("Invalid choice, try again.");

            }
            if(Room.getCurrentRoom().getPortKey()){
                endGame = true;
            }
        }
        if(this.run == false){
            System.out.println("You have defeated all enemies in the area!");
            System.out.println("You find a key on the last one and escape!");
        }else{
            //System.out.printf("You have found the portkey in the%s!\n", Room.getCurrentRoom().getRoomID());
            System.out.println("You use the key to escape to the next room!");
        }
    }

    /**
     * Enter a specified room, updating the current room and handling any encounters or items.
     * @param r The room to enter.
     * @see Room
     * @see Game#enterCombat(NPC)
     */
    public void enterRoom(Room r){
        Room.setCurrentRoom(r);
        System.out.printf("You have entered the%s,%s\n", r.getRoomID(), r.getRoomDescription());
        if(r.isEnemy()){
            System.out.printf("\nYou have encountered a %s!\n", r.getOpponent().getName());
            enterCombat(r.getOpponent());
            if(player.isAlive()){
                if(r.getOpponent().getName().equals("Boss")){
                    System.out.println("The Boss dropped a key!");
                    r.setPortKey();
                }
            }
            r.setOpponent(null);
            if(this.player.isAlive() == true){
                this.player.levelUp();
            }
        }
        if(r.itemAvailable()){
            System.out.println("You find an item!");
            ArrayList<Consumable> items = r.getItems();
            for(Consumable i : items){
                player.obtain(i);
            }
            r.removeItems();
        }
    }
}
