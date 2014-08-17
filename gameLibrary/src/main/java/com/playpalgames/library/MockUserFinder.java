package com.playpalgames.library;

import com.playpalgames.backend.gameEndpoint.model.User;

/**
 * Created by javi on 18/07/2014.
 */
public class MockUserFinder implements UserFinder {
    @Override
    public User getUser() {
        User user= new User();

        user.setName("consola");
        user.setPhoneNumber("55555");
        user.setRegId("XXXXXXXXX");
        return user;
    }
}
