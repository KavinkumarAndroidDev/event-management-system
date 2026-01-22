package com.ems.menu;

import com.ems.model.User;

public abstract class BaseMenu {

    protected User loggedInUser;

    public BaseMenu(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public abstract void start();
}
