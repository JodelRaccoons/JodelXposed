package com.jodelXposed.models;

public class Theme {
    public boolean active = false;
    public String orange = "#FF9908";
    public String yellow = "#FFBA00";
    public String red = "#DD5F5F";
    public String blue = "#06A3CB";
    public String bluegrayish = "#8ABDB0";
    public String green = "#9EC41C";

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
