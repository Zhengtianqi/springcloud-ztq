package com.ztq.common;


import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * @author zhengtianqi
 */
public class ResultUtils {

    public static String composeJSONResult(boolean success, String code, String msg) {
        return composeJSONResult(success, code, null, msg);
    }

    public static String composeJSONResult(boolean success, String code) {
        return composeJSONResult(success, code, null, null);
    }

    public static String composeJSONResult(boolean success, String code, Object data, String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("result", success);

        if (null != data) {
            result.put("data", data);
        }
        if (null != code) {
            result.put("code", code);
        }
        if (null != msg) {
            result.put("message", msg);
        } else if (null != code) {
            String errorMsg = ReturnCode.ERROR_MESSEGE.get(code);
            result.put("message", errorMsg);
        }

        return JSONObject.fromObject(result).toString();
        //return GSON.toJson(result);
    }

    /**
     * generate a successful result object
     *
     * @param data
     * @return
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(ReturnCode.SUCCESS, ReturnCode.MSG_SUCCESS, data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * generate a error result object
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> Result<T> fail(String code, String msg) {
        return new Result<T>(code, msg);
    }

    /**
     * generate a error result object by ErrorCode enum
     *
     * @param errorCode
     * @return
     */
    public static <T> Result<T> fail(ErrorCode.CodeMessageEnum errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }

}
