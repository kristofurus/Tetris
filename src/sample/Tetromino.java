package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static sample.PlayScene.SIZE;

class Tetromino {

    int x, y;
    private Color color;
    List<Block> blocks;

    public Color getColor() {
        return color;
    }

    Tetromino(Color color, Block... blocks) {
        this.color = color;
        this.blocks = new ArrayList<>(Arrays.asList(blocks));

        for (Block block : this.blocks) {
            block.setParent(this);
        }
    }

    void move(Direction direction) {
        move(direction.x, direction.y);
    }

    void move(int dx, int dy) {
        x += dx;
        y += dy;
        blocks.forEach(block -> {
            block.x += dx;
            block.y += dy;
        });
    }

    void setColor(GraphicsContext gc) {
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        blocks.forEach(block -> gc.fillRect(block.x * SIZE, block.y * SIZE, SIZE, SIZE));
        blocks.forEach(block -> gc.strokeRect(block.x * SIZE, block.y * SIZE, SIZE, SIZE));
    }

    void rotate() {
        /*TODO
         * this rotate is ok for now but it may behave really weird*/
        blocks.forEach(block -> block.setDirection(block.getDirections().stream().map(Direction::next).collect(Collectors.toList()).toArray(new Direction[0])));
    }

    void rotateBack() {
        blocks.forEach(block -> block.setDirection(block.getDirections().stream().map(Direction::previous).collect(Collectors.toList()).toArray(new Direction[0])));
    }

    void detach(int x, int y) {
        blocks.removeIf(block -> block.x == x && block.y == y);
    }

    Tetromino copy() {
        return new Tetromino(color, blocks.stream()
                .map(Block::copy).toArray(Block[]::new));
    }

}
