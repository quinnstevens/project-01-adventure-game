package adventure_game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Utility helper for rendering a textual representation of a level layout.
 * <p>
 * A map definition is expected to match the format used by {@code data/levels/*.txt}:
 * the first non-comment line gives the room count, the next {@code n} lines define
 * the rooms, and the following {@code n} lines describe exits for each room in the
 * order East, North, West, South.
 */
public final class MapPrinter {

    private static final String NEWLINE = System.lineSeparator();

    private static final int CELL_CONTENT_WIDTH = 3;
    private static final int CELL_WIDTH = CELL_CONTENT_WIDTH + 4;
    private static final String HORIZONTAL_GAP = " ";
    private static final String EMPTY_CELL = " ".repeat(CELL_WIDTH);

    private MapPrinter() {
        throw new AssertionError("MapPrinter should not be instantiated.");
    }

    /**
     * Renders a textual map description from an in-memory collection of {@link Room}s.
     * The rooms are assumed to be ordered in the same sequence in which they were
     * created (typically as returned by {@link Game#createMap(String)}).
     *
     * @param rooms list of rooms that belong to the current level
     * @return formatted map description
     * @throws IllegalArgumentException if {@code rooms} is {@code null}
     */
    public static String renderFromRooms(List<Room> rooms) {
        Objects.requireNonNull(rooms, "rooms cannot be null");

        Map<Room, Integer> indices = buildIndexLookup(rooms);
        Map<Integer, RoomDefinition> definitions = new LinkedHashMap<>();

        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room == null) {
                continue;
            }
            RoomDefinition definition = new RoomDefinition(
                i,
                sanitize(room.getRoomID()),
                sanitize(room.getRoomDescription())
            );
            definition.east = indexOf(room.getRoomEast(), indices);
            definition.north = indexOf(room.getRoomNorth(), indices);
            definition.west = indexOf(room.getRoomWest(), indices);
            definition.south = indexOf(room.getRoomSouth(), indices);
            definitions.put(i, definition);
        }

        return renderDefinitions(definitions.values());
    }

    /**
     * Creates an ASCII rendering of the map derived from the given rooms.
     *
     * @param rooms list of rooms that compose the current level
     * @return ASCII map representation
     */
    public static String renderAsciiMap(List<Room> rooms) {
        Objects.requireNonNull(rooms, "rooms cannot be null");

        Map<Room, Integer> indices = buildIndexLookup(rooms);
        Positioning positioning;
        try {
            positioning = computePositions(rooms);
        } catch (IllegalArgumentException ex) {
            StringBuilder fallback = new StringBuilder();
            fallback.append("Unable to render ASCII map: ").append(ex.getMessage()).append(NEWLINE);
            fallback.append("Fallback room listing:").append(NEWLINE);
            fallback.append(renderFromRooms(rooms));
            return fallback.toString();
        }

        if (positioning.pointToRoom.isEmpty()) {
            return "No rooms to display." + NEWLINE;
        }

        Room currentRoom = Room.getCurrentRoom();
        Bounds bounds = positioning.bounds;
        StringBuilder output = new StringBuilder();

        for (int y = bounds.minY; y <= bounds.maxY; y++) {
            StringBuilder northLine = new StringBuilder();
            StringBuilder middleLine = new StringBuilder();
            StringBuilder southLine = new StringBuilder();

            for (int x = bounds.minX; x <= bounds.maxX; x++) {
                Room room = positioning.pointToRoom.get(Point.of(x, y));
                if (room == null) {
                    appendEmptyCell(northLine);
                    appendEmptyCell(middleLine);
                    appendEmptyCell(southLine);
                } else {
                    boolean hasNorth = hasConnection(room, room.getRoomNorth(), positioning.roomToPoint);
                    boolean hasSouth = hasConnection(room, room.getRoomSouth(), positioning.roomToPoint);
                    boolean hasWest = hasConnection(room, room.getRoomWest(), positioning.roomToPoint);
                    boolean hasEast = hasConnection(room, room.getRoomEast(), positioning.roomToPoint);

                    northLine.append(hasNorth ? "   │   " : EMPTY_CELL);

                    middleLine.append(hasWest ? "─" : " ");
                    middleLine.append("[");
                    String label;
                    if (room == currentRoom) {
                        label = "\u263A";
                    } else {
                        int index = indices.getOrDefault(room, -1);
                        label = index < 0 ? "?" : String.valueOf(index);
                    }
                    middleLine.append(center(label));
                    middleLine.append("]");
                    middleLine.append(hasEast ? "─" : " ");

                    southLine.append(hasSouth ? "   │   " : EMPTY_CELL);
                }

                if (x < bounds.maxX) {
                    northLine.append(HORIZONTAL_GAP);
                    middleLine.append(HORIZONTAL_GAP);
                    southLine.append(HORIZONTAL_GAP);
                }
            }

            output.append(northLine).append(NEWLINE);
            output.append(middleLine).append(NEWLINE);
            output.append(southLine);

            if (y < bounds.maxY) {
                output.append(NEWLINE).append(NEWLINE);
            } else {
                output.append(NEWLINE);
            }
        }

        return output.toString();
    }

    /**
     * Prints the ASCII map for {@code rooms} to standard output.
     *
     * @param rooms list of rooms that compose the current level
     */
    public static void printAsciiMap(List<Room> rooms) {
        System.out.print(renderAsciiMap(rooms));
    }

    /**
     * Determines if there is a connection between two rooms based on their positions.
     * @param origin   the originating room
     * @param neighbor the neighboring room to check connection with
     * @param positions mapping of rooms to their coordinates
     * @return {@code true} if a connection exists; {@code false} otherwise
     * @throws IllegalArgumentException if {@code origin} or {@code neighbor} is {@code null}
     * @throws IllegalArgumentException if either room is not present in {@code positions}
     */
    private static boolean hasConnection(Room origin, Room neighbor, Map<Room, Point> positions) {
        if (origin == null || neighbor == null) {
            return false;
        }
        return positions.containsKey(origin) && positions.containsKey(neighbor);
    }

    /**
     * Appends an empty cell representation to the given StringBuilder.
     * @param builder the StringBuilder to append to
     * @throws IllegalArgumentException if {@code builder} is {@code null}
     */
    private static void appendEmptyCell(StringBuilder builder) {
        builder.append(EMPTY_CELL);
    }

    /**
     * Centers the given text within a fixed-width cell.
     * If the text is longer than the cell width, it will be truncated from the left to fit.
     * @param text the text to center
     * @return centered text within the cell width
     * @throws IllegalArgumentException if {@code text} is {@code null}
     */
    private static String center(String text) {
        if (text == null || text.isEmpty()) {
            text = "?";
        }
        if (text.length() > CELL_CONTENT_WIDTH) {
            text = text.substring(text.length() - CELL_CONTENT_WIDTH);
        }
        int padding = CELL_CONTENT_WIDTH - text.length();
        int left = padding / 2;
        int right = padding - left;
        return " ".repeat(left) + text + " ".repeat(right);
    }

    /**
     * Builds a lookup map of rooms to their indices in the provided list.
     * @param rooms list of rooms to index
     * @return map of rooms to their corresponding indices
     * @throws IllegalArgumentException if {@code rooms} is {@code null}
     * @throws IllegalArgumentException if any room in {@code rooms} is {@code null}
     */
    private static Map<Room, Integer> buildIndexLookup(List<Room> rooms) {
        Map<Room, Integer> indices = new IdentityHashMap<>();
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (room != null) {
                indices.put(room, i);
            }
        }
        return indices;
    }

    /**
     * Computes the positions of rooms in a 2D grid based on their connections.
     * @param rooms list of rooms to position
     * @return positioning information including room coordinates and overall bounds
     * @throws IllegalArgumentException if {@code rooms} is {@code null}
     * @throws IllegalArgumentException if the room connections create inconsistencies or overlaps
     * @throws IllegalArgumentException if any room in {@code rooms} is {@code null}
     * @see #renderAsciiMap(List)
     */
    private static Positioning computePositions(List<Room> rooms) {
        Map<Room, Point> roomPositions = new IdentityHashMap<>();
        Map<Point, Room> coordinateLookup = new HashMap<>();
        Bounds totalBounds = new Bounds();

        int nextStartX = 0;
        for (Room room : rooms) {
            if (room == null || roomPositions.containsKey(room)) {
                continue;
            }
            Bounds componentBounds = assignComponent(room, Point.of(nextStartX, 0), roomPositions, coordinateLookup);
            totalBounds.include(componentBounds);
            nextStartX = totalBounds.maxX + 2;
        }

        return new Positioning(roomPositions, coordinateLookup, totalBounds);
    }

    /**
     * Assigns positions to all rooms connected to the starting room.
     * @param start the starting room for the component 
     * @param startPoint the initial coordinates for the starting room
     * @param roomPositions mapping of rooms to their assigned coordinates
     * @param coordinateLookup mapping of coordinates to their occupying rooms
     * @return bounds of the assigned component
     * @throws IllegalArgumentException if {@code start}, {@code startPoint}, {@code roomPositions},
     *   or {@code coordinateLookup} is {@code null}
     * @throws IllegalArgumentException if the room connections create inconsistencies or overlaps
     * @see #computePositions(List)
     */
    private static Bounds assignComponent(
        Room start,
        Point startPoint,
        Map<Room, Point> roomPositions,
        Map<Point, Room> coordinateLookup
    ) {
        ArrayDeque<Room> queue = new ArrayDeque<>();
        Bounds bounds = new Bounds();

        queue.add(start);
        roomPositions.put(start, startPoint);
        coordinateLookup.put(startPoint, start);
        bounds.include(startPoint);

        while (!queue.isEmpty()) {
            Room current = queue.remove();
            Point origin = roomPositions.get(current);

            processNeighbor(current.getRoomEast(), origin.x + 1, origin.y, Direction.EAST, roomPositions, coordinateLookup, bounds, queue);
            processNeighbor(current.getRoomWest(), origin.x - 1, origin.y, Direction.WEST, roomPositions, coordinateLookup, bounds, queue);
            processNeighbor(current.getRoomNorth(), origin.x, origin.y - 1, Direction.NORTH, roomPositions, coordinateLookup, bounds, queue);
            processNeighbor(current.getRoomSouth(), origin.x, origin.y + 1, Direction.SOUTH, roomPositions, coordinateLookup, bounds, queue);
        }

        return bounds;
    }

    /**
     * Processes a neighboring room during position assignment.
     * @param neighbor the neighboring room to process
     * @param targetX the target x-coordinate for the neighbor
     * @param targetY the target y-coordinate for the neighbor
     * @param direction the direction from the current room to the neighbor
     * @param roomPositions mapping of rooms to their assigned coordinates
     * @param coordinateLookup mapping of coordinates to their occupying rooms
     * @param bounds the current bounds of assigned rooms
     * @param queue the queue of rooms to process
     * @throws IllegalArgumentException if {@code direction}, {@code roomPositions},
     *   {@code coordinateLookup}, or {@code bounds} is {@code null}
     * @throws IllegalArgumentException if the room connections create inconsistencies or overlaps
     * @see #assignComponent(Room, Point, Map, Map)
     */
    private static void processNeighbor(
        Room neighbor,
        int targetX,
        int targetY,
        Direction direction,
        Map<Room, Point> roomPositions,
        Map<Point, Room> coordinateLookup,
        Bounds bounds,
        ArrayDeque<Room> queue
    ) {
        if (neighbor == null) {
            return;
        }

        Point desired = Point.of(targetX, targetY);
        Point existingPosition = roomPositions.get(neighbor);

        if (existingPosition != null) {
            if (!existingPosition.equals(desired)) {
                throw new IllegalArgumentException("Map layout contains inconsistent room connections.");
            }
            return;
        }

        Room occupant = coordinateLookup.get(desired);
        if (occupant != null && occupant != neighbor) {
            resolveCollision(desired, direction, roomPositions, coordinateLookup, bounds);
        }

        roomPositions.put(neighbor, desired);
        coordinateLookup.put(desired, neighbor);
        bounds.include(desired);
        queue.add(neighbor);
    }

    /**
     * Resolves a position collision by shifting existing occupants.
     * @param desired the desired coordinates that are already occupied
     * @param direction the direction to shift occupants
     * @param roomPositions mapping of rooms to their assigned coordinates
     * @param coordinateLookup mapping of coordinates to their occupying rooms
     * @param bounds the current bounds of assigned rooms
     * @throws IllegalArgumentException if {@code desired}, {@code direction},
     * {@code roomPositions}, {@code coordinateLookup}, or {@code bounds} is {@code null}
     * @throws IllegalArgumentException if unable to resolve the collision after multiple attempts
     * @see #processNeighbor(Room, int, int, Direction, Map, Map, Bounds, ArrayDeque)
     * @see #assignComponent(Room, Point, Map, Map)
     * @see #computePositions(List)
     */
    private static void resolveCollision(
        Point desired,
        Direction direction,
        Map<Room, Point> roomPositions,
        Map<Point, Room> coordinateLookup,
        Bounds bounds
    ) {
        int attempts = 0;
        while (coordinateLookup.containsKey(desired)) {
            shiftOccupants(direction, desired, roomPositions, coordinateLookup, bounds);
            attempts++;
            if (attempts > 64) {
                throw new IllegalArgumentException("Unable to resolve map overlap after multiple adjustments.");
            }
        }
    }

    /**
     * Shifts occupants in the specified direction relative to a pivot point.
     * @param direction the direction to shift occupants
     * @param pivot the pivot point for shifting
     * @param roomPositions mapping of rooms to their assigned coordinates
     * @param coordinateLookup mapping of coordinates to their occupying rooms
     * @param bounds the current bounds of assigned rooms
     * @throws IllegalArgumentException if {@code direction}, {@code pivot},
     * {@code roomPositions}, {@code coordinateLookup}, or {@code bounds} is {@code null}
     * @see #resolveCollision(Point, Direction, Map, Map, Bounds)
     * @see #processNeighbor(Room, int, int, Direction, Map, Map, Bounds, ArrayDeque)
     * @see #assignComponent(Room, Point, Map, Map)
     * @see #computePositions(List)
     */
    private static void shiftOccupants(
        Direction direction,
        Point pivot,
        Map<Room, Point> roomPositions,
        Map<Point, Room> coordinateLookup,
        Bounds bounds
    ) {
        Map<Room, Point> updated = new IdentityHashMap<>(roomPositions.size());
        coordinateLookup.clear();

        for (Map.Entry<Room, Point> entry : roomPositions.entrySet()) {
            Point point = entry.getValue();
            if (direction.shouldShift(point, pivot)) {
                point = point.translate(direction.dx, direction.dy);
            }
            updated.put(entry.getKey(), point);
        }

        roomPositions.clear();
        roomPositions.putAll(updated);

        for (Map.Entry<Room, Point> entry : roomPositions.entrySet()) {
            coordinateLookup.put(entry.getValue(), entry.getKey());
        }

        Bounds recalculated = recomputeBounds(roomPositions.values());
        bounds.copyFrom(recalculated);
    }

    /**
     * Recomputes the bounds from a collection of points.
     * @param points collection of points to evaluate
     * @return bounds encompassing all points
     * @throws IllegalArgumentException if {@code points} is {@code null}
     * @see #shiftOccupants(Direction, Point, Map, Map, Bounds)
     * @see #resolveCollision(Point, Direction, Map, Map, Bounds)
     * @see #processNeighbor(Room, int, int, Direction, Map, Map, Bounds, ArrayDeque)
     * @see #assignComponent(Room, Point, Map, Map)
     * @see #computePositions(List)
     */
    private static int indexOf(Room room, Map<Room, Integer> indices) {
        if (room == null) {
            return -1;
        }
        Integer value = indices.get(room);
        return value == null ? -1 : value;
    }

    private static Map<Integer, RoomDefinition> parseMapFile(String filePath) {
        List<String> lines = readMapLines(filePath);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("Map file '" + filePath + "' does not contain any data.");
        }

        int roomCount = parseInteger(lines.get(0), "room count");
        int expectedLines = 1 + (roomCount * 2);
        if (lines.size() < expectedLines) {
            throw new IllegalArgumentException(
                "Map file '" + filePath + "' is incomplete. Expected at least "
                    + expectedLines + " data lines but found " + lines.size() + "."
            );
        }

        Map<Integer, RoomDefinition> rooms = new LinkedHashMap<>();
        int cursor = 1;
        for (int i = 0; i < roomCount; i++, cursor++) {
            String line = lines.get(cursor);
            String[] parts = line.split(":", 3);
            if (parts.length < 3) {
                throw new IllegalArgumentException("Malformed room definition: '" + line + "'");
            }

            int id = parseInteger(parts[0], "room id");
            String name = sanitize(parts[1]);
            String description = parts[2].trim();

            if (rooms.containsKey(id)) {
                throw new IllegalArgumentException("Duplicate room id detected: " + id);
            }
            rooms.put(id, new RoomDefinition(id, name, description));
        }

        for (int i = 0; i < roomCount; i++, cursor++) {
            String line = lines.get(cursor);
            String[] parts = line.split(":");
            if (parts.length < 5) {
                throw new IllegalArgumentException("Malformed exit line: '" + line + "'");
            }

            int id = parseInteger(parts[0], "room id");
            RoomDefinition definition = rooms.get(id);
            if (definition == null) {
                throw new IllegalArgumentException("Exit references undefined room id " + id);
            }

            definition.east = parseInteger(parts[1], "east exit");
            definition.north = parseInteger(parts[2], "north exit");
            definition.west = parseInteger(parts[3], "west exit");
            definition.south = parseInteger(parts[4], "south exit");
        }

        return rooms;
    }

    /**
     * Reads map lines from the specified file, ignoring comments and blank lines.
     * @param filePath path to the map file
     * @return list of relevant map lines
     * @throws IllegalArgumentException if unable to read the file
     * @see #parseMapFile(String)
     * @see #computePositions(List)
     * @see #renderAsciiMap(List)
     * @see #renderFromRooms(List)
     * @see #computeRoomLayout(List)
     * @see #printAsciiMap(List)
     * @see #buildIndexLookup(List)
     * 
     */
    private static List<String> readMapLines(String filePath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                lines.add(trimmed);
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to read map file '" + filePath + "'.", ex);
        }
        return lines;
    }
/**
 * Sanitizes a string by trimming whitespace and returning an empty string for null values.
 * * @param value the string to sanitize
 * @return sanitized string
 */
    private static String sanitize(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Parses an integer from a string, throwing an exception for invalid input.
     * @param value the string to parse
     * @param label a label for the value (used in error messages)
     * @return the parsed integer
     * @throws IllegalArgumentException if the string is null, empty, or not a valid integer
     * @see #parseMapFile(String)
     */
    private static int parseInteger(String value, String label) {
        String candidate = value == null ? "" : value.trim();
        if (candidate.isEmpty()) {
            throw new IllegalArgumentException("Expected numeric value for " + label + " but found nothing.");
        }
        try {
            return Integer.parseInt(candidate);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid numeric value for " + label + ": '" + candidate + "'", ex);
        }
    }
/**
 * Renders room definitions into a formatted string.
 * @param definitions collection of room definitions to render
 * @return formatted string representation of room definitions
 * @throws IllegalArgumentException if {@code definitions} is {@code null}
 * @see #parseMapFile(String)
 */
    private static String renderDefinitions(Collection<RoomDefinition> definitions) {
        List<RoomDefinition> ordered = new ArrayList<>(definitions);
        ordered.sort(Comparator.comparingInt(room -> room.id));

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < ordered.size(); i++) {
            RoomDefinition room = ordered.get(i);
            output.append("Room ").append(room.id).append(": ").append(room.name).append(NEWLINE);
            output.append("  Description: ").append(room.description).append(NEWLINE);
            output.append("  Exits:").append(NEWLINE);
            appendExit(output, "East", room.east, ordered);
            appendExit(output, "North", room.north, ordered);
            appendExit(output, "West", room.west, ordered);
            appendExit(output, "South", room.south, ordered);
            if (i < ordered.size() - 1) {
                output.append(NEWLINE);
            }
        }
        return output.toString();
    }

    /**
     * Appends an exit description to the output.
     * @param output the StringBuilder to append to
     * @param direction the direction of the exit
     * @param targetId the target room id for the exit
     * @param rooms list of all room definitions
     * @throws IllegalArgumentException if {@code output}, {@code direction}, or {@code rooms} is {@code null}
     * @see #renderDefinitions(Collection)
     */
    private static void appendExit(StringBuilder output, String direction, int targetId, List<RoomDefinition> rooms) {
        output.append("    ").append(direction).append(": ");
        if (targetId < 0) {
            output.append("none");
        } else {
            RoomDefinition destination = findById(rooms, targetId);
            if (destination != null) {
                output.append(targetId).append(" (").append(destination.name).append(")");
            } else {
                output.append(targetId).append(" (missing definition)");
            }
        }
        output.append(NEWLINE);
    }

    /**
     * Finds a room definition by its id.
     * @param rooms list of room definitions to search
     * @param id the id to search for
     * @return the matching room definition, or {@code null} if not found
     * @throws IllegalArgumentException if {@code rooms} is {@code null}
     * @see #appendExit(StringBuilder, String, int, List)
     */
    private static RoomDefinition findById(List<RoomDefinition> rooms, int id) {
        for (RoomDefinition room : rooms) {
            if (room.id == id) {
                return room;
            }
        }
        return null;
    }

    /**
     * Recomputes the bounds from a collection of points.
     * @param points collection of points to evaluate
     * @return bounds encompassing all points
     * @throws IllegalArgumentException if {@code points} is {@code null}
     * @see #shiftOccupants(Direction, Point, Map, Map, Bounds)
     * @see #resolveCollision(Point, Direction, Map, Map, Bounds)
     * @see #processNeighbor(Room, int, int, Direction, Map, Map, Bounds, ArrayDeque)
     * @see #assignComponent(Room, Point, Map, Map)
     * @see #computePositions(List)
     */
    private static Bounds recomputeBounds(Collection<Point> points) {
        Bounds result = new Bounds();
        for (Point point : points) {
            result.include(point);
        }
        return result;
    }

    /**
     * Internal representation of a room definition.
     * @see #parseMapFile(String)
     */
    private static final class RoomDefinition {
        final int id;
        final String name;
        final String description;
        int east = -1;
        int north = -1;
        int west = -1;
        int south = -1;

        RoomDefinition(int id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
        }
    }

    /**
     * Internal representation of a 2D point.
     * @see #computePositions(List)
     * @see #assignComponent(Room, Point, Map, Map)
     */
    private static final class Point {
        final int x;
        final int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        static Point of(int x, int y) {
            return new Point(x, y);
        }

        Point translate(int dx, int dy) {
            if (dx == 0 && dy == 0) {
                return this;
            }
            return Point.of(this.x + dx, this.y + dy);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Point)) {
                return false;
            }
            Point other = (Point) obj;
            return this.x == other.x && this.y == other.y;
        }

        @Override
        public int hashCode() {
            return (31 * x) + y;
        }
    }

    /**
     * Internal representation of bounding coordinates.
     * @see #computePositions(List)
     * @see #assignComponent(Room, Point, Map, Map)
     */
    private static final class Bounds {
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        boolean initialized = false;

        void include(Point point) {
            if (point == null) {
                return;
            }
            if (!initialized) {
                minX = maxX = point.x;
                minY = maxY = point.y;
                initialized = true;
                return;
            }
            minX = Math.min(minX, point.x);
            maxX = Math.max(maxX, point.x);
            minY = Math.min(minY, point.y);
            maxY = Math.max(maxY, point.y);
        }

        void include(Bounds other) {
            if (other == null || !other.initialized) {
                return;
            }
            include(Point.of(other.minX, other.minY));
            include(Point.of(other.maxX, other.maxY));
        }

        void copyFrom(Bounds other) {
            if (other == null || !other.initialized) {
                initialized = false;
                minX = Integer.MAX_VALUE;
                maxX = Integer.MIN_VALUE;
                minY = Integer.MAX_VALUE;
                maxY = Integer.MIN_VALUE;
                return;
            }
            initialized = true;
            minX = other.minX;
            maxX = other.maxX;
            minY = other.minY;
            maxY = other.maxY;
        }
    }

    /**
     * Internal representation of room positioning information.
     * @see #computePositions(List)
     * @see #assignComponent(Room, Point, Map, Map)
     */
    private static final class Positioning {
        final Map<Room, Point> roomToPoint;
        final Map<Point, Room> pointToRoom;
        final Bounds bounds;

        Positioning(Map<Room, Point> roomToPoint, Map<Point, Room> pointToRoom, Bounds bounds) {
            this.roomToPoint = roomToPoint;
            this.pointToRoom = pointToRoom;
            this.bounds = bounds;
        }
    }

    /**
     * Enumeration of cardinal directions with associated movement logic.
     * @see #processNeighbor(Room, int, int, Direction, Map, Map, Bounds, ArrayDeque)
     * @see #assignComponent(Room, Point, Map, Map)
     * @see #computePositions(List)
     */
    private enum Direction {
        EAST(1, 0) {
            @Override
            boolean shouldShift(Point point, Point pivot) {
                return point.x >= pivot.x;
            }
        },
        WEST(-1, 0) {
            @Override
            boolean shouldShift(Point point, Point pivot) {
                return point.x <= pivot.x;
            }
        },
        NORTH(0, -1) {
            @Override
            boolean shouldShift(Point point, Point pivot) {
                return point.y <= pivot.y;
            }
        },
        SOUTH(0, 1) {
            @Override
            boolean shouldShift(Point point, Point pivot) {
                return point.y >= pivot.y;
            }
        };

        final int dx;
        final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }

        abstract boolean shouldShift(Point point, Point pivot);
    }
}
