package com.wesleyreisz.whosnext.util;

/**
 * Created by wesleyreisz on 10/19/14.
 */
public class WhosNextConstants {
    public static final String BASE_SERVICE_URL = "http://gcloud.wesleyreisz.com:3000";
    //public static final String BASE_SERVICE_URL = "http://dev.wesleyreisz.com:3000";
    public static final String GET_TEAMS = BASE_SERVICE_URL + "/teams";
    public static final String GET_NEXT_OPPONENT = BASE_SERVICE_URL + "/teams/nickname/%s/next";
}
