package adventure_game;
import java.util.ArrayList;

import adventure_game.items.Consumable;

/**
 * Represents a room in the adventure game.
 * Each room has a unique ID, description, and connections to adjacent rooms.
 * It may also contain an NPC opponent and consumable items.
 * @see NPC
 * @see Consumable
 */
public class Room {
    private static Room currentRoom;

    private final String roomID;
    private final String roomDescription;

    private boolean portKey = false;

    private Room roomEast;
    private Room roomWest;
    private Room roomNorth;
    private Room roomSouth;

    private NPC opponent;
    private ArrayList<Consumable> items = new ArrayList<>();

    /**
     * Constructs a Room with the specified ID, description, and adjacent rooms.
     * @param ID The unique identifier for the room.
     * @param roomDescription A brief description of the room.
     * @param east The room to the east.
     * @param north The room to the north.
     * @param west The room to the west.
     * @param south The room to the south.
     */
    Room(String ID, String roomDescription, Room east, Room north, Room west, Room south){
        this.roomDescription = roomDescription;
        this.roomID = ID;
        this.roomEast = east;
        this.roomWest = west;
        this.roomNorth = north;
        this.roomSouth = south;
    }

    /**
     * Returns a string representation of the room, including its ID and description.
     * @return A string representing the room.
     */
    @Override
    public String toString(){
        return roomID + ": " + roomDescription;
    }
    /* 
    public void exitRoom(String direction){
        if(roomEast.equals(null) && direction.equals("east")){

        }
    }*/
    /**
     * Gets the current room.
     * @return The current Room object.
     */
    public static Room getCurrentRoom(){
        return currentRoom;
    }
    /**
     * Sets the current room.
     * @param r The Room object to set as the current room.
     */
    public static void setCurrentRoom(Room r){
        currentRoom = r;
    }
    /**
     * Gets the room ID.
     * @return The room ID as a string.
     */
    public String getRoomID(){
        return this.roomID;
    }
    /**
     * Gets the room description.
     * @return The room description as a string.
     */
    public String getRoomDescription(){
        return this.roomDescription;
    }

    /**
     * Gets the room to the east.
     * @return The Room object to the east.
     */
    public Room getRoomEast(){
        return this.roomEast;
    }
    /**
     * Gets the room to the west.
     * @return The Room object to the west.
     */
    public Room getRoomWest(){
        return this.roomWest;
    }
    /**
     * Gets the room to the north.
     * @return The Room object to the north.
     */
    public Room getRoomNorth(){
        return this.roomNorth;
    }
    /**
     * Gets the room to the south.
     * @return The Room object to the south.
     */
    public Room getRoomSouth(){
        return this.roomSouth;
    }
    /**
     * Sets the room to the north.
     * @param r The Room object to set as the room to the north.
     */
    public void setRoomNorth(Room r){
        roomNorth = r;
    }
    /**
     * Sets the room to the south.
     * @param r The Room object to set as the room to the south.
     */
    public void setRoomSouth(Room r){
        roomSouth = r;
    }
    /**
     * Sets the room to the east.
     * @param r The Room object to set as the room to the east.
     */
    public void setRoomEast(Room r){
        roomEast = r;
    }
    /**
     * Sets the room to the west.
     * @param r The Room object to set as the room to the west.
     */
    public void setRoomWest(Room r){
        roomWest = r;
    }
    /**
     * Checks if there is an enemy (NPC opponent) in the room.
     * @return true if there is an opponent, false otherwise.
     */
    public boolean isEnemy(){
        if(this.opponent != null){
            return true;
        }
        return false;
    }
    /**
     * Checks if there are any items available in the room.
     * @return true if there are items, false otherwise.
     */
    public boolean itemAvailable(){
        if(items == null){
            return false;
        }
        if(items.size() > 0){
            return true;
        }
        return false;
    }
    /**
     * Gets the NPC opponent in the room.
     * @return The NPC object representing the opponent.
     */
    public NPC getOpponent(){
        return this.opponent;
    }
    /**
     * Gets the list of consumable items in the room.
     * @return An ArrayList of Consumable items.
     */
    public ArrayList<Consumable> getItems(){
        return items;
    }
    /**
     * Sets the NPC opponent in the room.
     * @param opponent The NPC object to set as the opponent.
     */
    public void setOpponent(NPC opponent){
        this.opponent = opponent;
    }
    /**
     * Adds a consumable item to the room.
     * @param item The Consumable item to add.
     */
    public void addItem(Consumable item){
        this.items.add(item);
    }
    /**
     * Removes all items from the room.
     */
    public void removeItems(){
        this.items = null;
    }
    /**
     * Sets the port key status of the room to true.
     */
    public void setPortKey(){
        this.portKey = true;
    }
    /**
     * Gets the port key status of the room.
     * @return true if the room has a port key, false otherwise.
     */
    public boolean getPortKey(){
        return this.portKey;
    }
}
