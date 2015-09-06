package com.brucewuu.android.qlcy.config;

/**
 * Created by brucewuu on 15/9/1.
 */
public final class AppConfig {

    public static final String REGISTER_URL = "http://imas.ucpaas.com/user/reg.do";
    public static final String LOGIN_URL = "http://imas.ucpaas.com/user/login.do";
    public static final String GET_GROUP = "http://imas.ucpaas.com/user/queryGroup.do?userid=";
    public static final String ADD_GROUP = "http://imas.ucpaas.com/user/joinGroup.do";
    public static final String CREATE_GROPU = "http://imas.ucpaas.com/user/createGroup.do";
    public static final String EXIT_GROUP ="http://imas.ucpaas.com/user/quitGroup.do";

    public static final String CONNECT_SUCCESS = "c_s";
    public static final String NO_MSG_TO_FRIENDS = "t_f";
    public static final String NETWORK_CONNECTED = "n_c";
    public static final String ADD_GROUP_SUCCESS = "a_g_s";
    public static final String EXIT_GROUP_SUCCESS = "e_g_s";
    public static final String CREATE_DISCUSSION_SUCCESS = "c_d_s";

    public static final String [] FRIENDS = new String[] {
            "15066658481", "13916903892", "18217301273", "15806409045",
            "15250803263", "15221053927", "13795355299", "18702149411",
            "13502245635", "18778988998"};
    public static final String [] NAMES = new String[] {
            "xiaobuding", "xiaohuanxiong", "xiaopingguo", "dashuge",
            "donggua", "yefangzi", "dabaishu", "maomaochong", "daxiazi", "laoquezi"};
}
