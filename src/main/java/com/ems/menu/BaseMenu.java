package com.ems.menu;

import com.ems.model.User;

/*
 * Serves as the base class for all console menu implementations.
 *
 * Responsibilities:
 * - Hold the logged in user context
 * - Define a common entry point for menu execution
 */
public abstract class BaseMenu {


    protected User loggedInUser;

    public BaseMenu(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public abstract void start();
}
