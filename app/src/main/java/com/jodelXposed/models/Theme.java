package com.jodelXposed.models;

import com.jodelXposed.utils.Utils;

import static com.jodelXposed.utils.Utils.fixHexColor;

public class Theme {

    public boolean active = false;
    public String orange = fixHexColor(Utils.Colors.Colors.get(0));
    public String yellow = fixHexColor(Utils.Colors.Colors.get(0));
    public String red = fixHexColor(Utils.Colors.Colors.get(0));
    public String blue = fixHexColor(Utils.Colors.Colors.get(0));
    public String bluegrayish = fixHexColor(Utils.Colors.Colors.get(0));
    public String green = fixHexColor(Utils.Colors.Colors.get(0));

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
