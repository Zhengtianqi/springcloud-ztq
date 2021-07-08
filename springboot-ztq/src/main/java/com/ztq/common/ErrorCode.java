package com.ztq.common;

/**
 * @author zhengtianqi
 */
public enum ErrorCode {
    ;

    public interface CodeMessageEnum {
        String getMessage();

        String getCode();
    }

    /**
     * start with 1---
     */
    public enum System implements CodeMessageEnum {
        SYSTEM_ERROR("1005", "系统内部错误，请联系管理员！");

        private String code;
        private String message;

        System(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public String getCode() {
            return this.code;
        }
    }

    /**
     * start with 8---
     *
     * @author zhengtianqi
     */
    public enum ApplicationCode implements CodeMessageEnum {
        INVALID_PIPELINE("8002", "参数不正确");

        private String code;
        private String message;

        ApplicationCode(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public String getCode() {
            return this.code;
        }
    }

}
