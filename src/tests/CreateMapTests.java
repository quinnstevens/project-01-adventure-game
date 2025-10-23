package tests;
import adventure_game.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.Random;

public class CreateMapTests {
    Game game;
    private String filePath = "data/levels/the-stilts.txt";

    @BeforeEach
    void setup(){
        game = new Game();
    }

    @Test
    void testCreateMap(){
        ArrayList<Room> roomList = game.createMap(filePath);

        assertTrue(Room.getCurrentRoom() != null);
        assertTrue(roomList.get(0).getCurrentRoom().getRoomID().equals(" Foyer"));
        assertTrue(roomList.size() == 16);
    }

    @Test
    void testEnterRoom(){
        ArrayList<Room> roomList = game.createMap(filePath);
        roomList.get(1).setOpponent(null);
        roomList.get(1).removeItems();

        assertTrue(roomList.get(1).isEnemy() == false);
        assertTrue(roomList.get(1).itemAvailable() == false);

        game.enterRoom(roomList.get(1));
        assertTrue(Room.getCurrentRoom().getRoomID().equals(" Study"));
    }
}
