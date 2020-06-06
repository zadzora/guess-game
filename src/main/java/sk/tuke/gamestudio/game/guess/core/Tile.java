package sk.tuke.gamestudio.game.guess.core;

public abstract class Tile {
    private Tile_State state = Tile_State.INVISIBLE;

    public Tile_State getState() {
        return state;
    }

    void setState(Tile_State state) {
        this.state = state;
    }
}
