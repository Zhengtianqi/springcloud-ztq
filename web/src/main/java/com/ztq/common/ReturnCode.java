package com.ztq.common;

import java.util.HashMap;


/**
 * 错误码
 *
 * @author zhengtianqi
 */
public class ReturnCode {
    public static HashMap<String, String> ERROR_MESSEGE = new HashMap<String, String>();

    public static String SUCCESS = "0000";
    public static String MSG_SUCCESS = "Success";

    //1 开头 用户输入相关
    public static String INPUT_ERROR = "1001";
    public static String MSG_INPUT_ERROR = "Input Format Error";

    //5 开头 系统程序逻辑相关
    public static String SERVER_INTERAL_ERROR = "4999";
    public static String MSG_SERVER_INTERAL_ERROR = "系统内部错误，请联系管理员！";

    /**6，7，8 开头 对应具体模块，team，project，pipeline*/
    public static String PIPELINE_FAILED = "8000";
    public static String MSG_PIPELINE_FAILED = "Failed";
    public static String SCM_FAILED = "8003";
    public static String MSG_SCM_FAILED = "Failed";
    public static String CREDENTIALS_INPUT_ERROR = "8004";
    public static String MSG_CREDENTIALS_FAILED = "Failed";

    // 按照模块分类，定义不同开头错误码
    static{
        ERROR_MESSEGE.put(SUCCESS, MSG_SUCCESS);
        ERROR_MESSEGE.put(INPUT_ERROR, MSG_INPUT_ERROR);
        ERROR_MESSEGE.put(SERVER_INTERAL_ERROR, MSG_SERVER_INTERAL_ERROR);
        ERROR_MESSEGE.put(PIPELINE_FAILED,MSG_PIPELINE_FAILED);
        ERROR_MESSEGE.put(SCM_FAILED,MSG_SCM_FAILED);
        ERROR_MESSEGE.put(CREDENTIALS_INPUT_ERROR,MSG_CREDENTIALS_FAILED);
    }

}
