package com.playpalgames.backend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by javi on 16/10/2014.
 */
public class Utils {

    public static final SimpleDateFormat sdfDate = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy

    static{ sdfDate.setTimeZone(TimeZone.getTimeZone("GMT+2"));}

public static String now(){
    return sdfDate.format(new Date());
}
}
