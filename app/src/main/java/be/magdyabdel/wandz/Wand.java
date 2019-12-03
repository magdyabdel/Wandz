package be.magdyabdel.wandz;

public class Wand {

    private String id;
    private int Timer;

    public Wand(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTimer() {
        return Timer;
    }

    public void setTimer(int timer) {
        Timer = timer;
    }
}
