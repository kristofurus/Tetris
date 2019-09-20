package sample;

import java.util.Arrays;
import java.util.List;

public class Block {
    private int distance;
    private List<Direction> directions;
    private Tetromino parent;
    double x, y;

    public Block(int distance, Direction... direction) {
        this.distance = distance;
        this.directions = Arrays.asList(direction);
    }

    List<Direction> getDirections() {
        return directions;
    }

    void setDirection(Direction... direction) {
        this.directions = Arrays.asList(direction);
        int dx = 0, dy = 0;
        for (Direction d : directions) {
            dx += distance * d.x;
            dy += distance * d.y;
        }
        x = parent.x + dx;
        y = parent.y + dy;
    }

    void setParent(Tetromino parent) {
        this.parent = parent;

        int dx = 0, dy = 0;
        for (Direction d : directions) {
            dx += distance * d.x;
            dy += distance * d.y;
        }
        x = parent.x + dx;
        y = parent.y + dy;
    }

    Block copy() {
        return new Block(distance, directions.toArray(new Direction[0]));
    }
}

