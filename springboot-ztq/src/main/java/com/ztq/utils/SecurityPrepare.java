package com.ztq.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author zhengtianqi
 */
public class SecurityPrepare {
    private static final Logger logger = LoggerFactory.getLogger(SecurityPrepare.class);
    /**
     * 用户自己申请的机机账号keytab文件名称
     */
    // private static final String USER_KEYTAB_FILE = "user.keytab";

    /**
     * 用户自己申请的机机账号名称
     */
    // private static final String USER_PRINCIPAL = "flinkuser@BFD6DD2C_619A_4E50_99F3_84FDD3594552.COM";
    public static void kerbrosLogin(String sslDir, String userKeytab, String userPrincipal) {

        try {
            logger.info("", "Securitymode start.");

            //!!注意，安全认证时，需要用户手动修改为自己申请的机机账号
            securityPrepare(sslDir, userKeytab, userPrincipal);
        } catch (IOException e) {
            logger.error("", "Security prepare failure.");
            logger.error("The IOException occured : {}.", e);
            return;
        }
        logger.info("", "Security prepare success.");

    }

    public static void securityPrepare(String sslDir, String userKeytab, String userPrincipal) throws IOException {
        String filePath = sslDir;
        String krbFile = filePath + "krb5.conf";
        String userKeyTableFile = filePath + userKeytab;
        //windows路径下分隔符替换
        userKeyTableFile = userKeyTableFile.replace("\\", "\\\\");
        krbFile = krbFile.replace("\\", "\\\\");

        LoginUtil.setKrb5Config(krbFile);
        LoginUtil.setZookeeperServerPrincipal("zookeeper/hadoop.hadoop.com");
        LoginUtil.setJaasFile(userPrincipal, userKeyTableFile);
    }
    /**
     * 判断是否是权限认证模式
     * */

    //    public static Boolean isSecurityModel() {
    //        Boolean isSecurity = false;
    //        String krbFilePath =
    //            System.getProperty("user.dir") + File.separator + "conf" + File.separator + "kafkaSecurityMode";
    //
    //        Properties securityProps = new Properties();
    //
    //        // file does not exist.
    //        if (!isFileExists(krbFilePath)) {
    //            return isSecurity;
    //        }
    //
    //        try {
    //            securityProps.load(new FileInputStream(krbFilePath));
    //            if ("yes".equalsIgnoreCase(securityProps.getProperty("kafka.client.security.mode"))) {
    //                isSecurity = true;
    //            }
    //        } catch (Exception e) {
    //            LOG.info("The Exception occured : {}.", e);
    //        }
    //
    //        return isSecurity;
    //    }

    /*
     * 判断文件是否存在
     */
    //    private static boolean isFileExists(String fileName) {
    //        File file = new File(fileName);
    //
    //        return file.exists();
    //    }

}
