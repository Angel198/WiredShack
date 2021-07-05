package com.jaylax.wiredshack.webcommunication;

public class WebConstants {

    /* To try the app with Enablex hosted service you need to set the kTry = true */
    public  static  final  boolean kTry = true;

    /*Your webservice host URL, Keet the defined host when kTry = true */

//    public static final String baseURL = "https://api.enablex.io/";
//    public static final String baseURL = "https://events.jaylaxsolution.com/api/";
    public static final String baseURL = "https://wiredshack.com/api/";

    /*The following information required, Only when kTry = true, When you hosted your own webservice remove these fileds*/

    /*Use enablec portal to create your app and get these following credentials*/
    public static final String kAppId = "609cf062a3cc602dbf297c38";
    public static final String kAppkey = "y3aSuUe8uZeeareEaPubeRaqujabu9uhypu5";


    public static final String createRoomURL = "createRoom/";
    public static final int createRoomCode = 1;
    public static final String createTokenURL = "createToken";
    public static final int createTokenCode = 2;
    public static final String uploadImageURL = "createSnapShot/";
    public static final int uploadImageCode = 3;
}
