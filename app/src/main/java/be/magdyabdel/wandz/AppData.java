package be.magdyabdel.wandz;

import java.io.Serializable;
import java.util.ArrayList;

public class AppData implements Serializable {

    private String name_player;
    private ArrayList<String> names;

    public AppData() {
        names = new ArrayList<>();
    }

    public String getName_player() {
        return name_player;
    }

    public void setName_player(String name_player) {
        this.name_player = name_player;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public void addName(String newName) {
        names.add(newName);
    }

    public void removeName(String nameToRemove) {
        names.remove(nameToRemove);
    }
}
