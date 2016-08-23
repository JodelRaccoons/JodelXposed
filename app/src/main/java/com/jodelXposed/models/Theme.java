package com.jodelXposed.models;

import com.jodelXposed.utils.Utils;

import static com.jodelXposed.utils.Color.normalizeColor;

public class Theme {

    public boolean active = false;
    public String orange = normalizeColor(Utils.Colors.Colors.get(0));
    public String yellow = normalizeColor(Utils.Colors.Colors.get(1));
    public String red = normalizeColor(Utils.Colors.Colors.get(2));
    public String blue = normalizeColor(Utils.Colors.Colors.get(3));
    public String bluegrayish = normalizeColor(Utils.Colors.Colors.get(4));
    public String green = normalizeColor(Utils.Colors.Colors.get(5));

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
