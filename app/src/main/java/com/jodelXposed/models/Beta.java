package com.jodelXposed.models;

import java.util.ArrayList;
import java.util.List;

public class Beta {

    public boolean active = false;
    private List<String> notificationList = new ArrayList<>();

    public List<String> getNotificationList() {
        return notificationList;
    }

    public void setNotificationList(List<String> notificationList) {
        this.notificationList = notificationList;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
