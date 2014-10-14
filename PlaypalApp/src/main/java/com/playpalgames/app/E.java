package com.playpalgames.app;

import org.acra.ACRA;

/**
 * Exceptions mananger
 * <p/>
 * Created by javi on 13/10/2014.
 */
public class E {

    public static void manage(Exception e) {
        ACRA.getErrorReporter().handleException(e);
    }
}
