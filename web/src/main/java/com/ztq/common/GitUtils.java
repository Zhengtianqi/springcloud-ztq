package com.ztq.common;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: zhengtianqi
 */
public class GitUtils {


    public static String getBranches(String gitUrl, String privateToken, int perPage, int page) throws Exception {
        Integer gitProjectId = getGitProjectId(gitUrl, privateToken);
        String apiUrl = getApiUrl(gitUrl) + "projects/%d/repository/branches?per_page=%d&page=%d&private_token=%s";
        String url = String.format(apiUrl, gitProjectId, perPage, page, privateToken);
        HttpClientResult result = HttpClientUtils.doGet(url);
        return result.getContent();
    }

    public static String getTags(String gitUrl, String privateToken) throws Exception {
        Integer gitProjectId = getGitProjectId(gitUrl, privateToken);
        String apiUrl = getApiUrl(gitUrl) + "projects/%d/repository/tags?per_page=100&private_token=%s";
        String url = String.format(apiUrl, gitProjectId, privateToken);
        HttpClientResult result = HttpClientUtils.doGet(url);
        return result.getContent();
    }

    //返回所有分支的commit
    public static JSONArray getCommits(String gitUrl, String branch, String privateToken) throws Exception {
        Integer gitProjectId = getGitProjectId(gitUrl, privateToken);
        String apiUrl = getApiUrl(gitUrl) + "projects/%d/repository/commits?per_page=100&&ref_name=%s&private_token=%s";
        String url = String.format(apiUrl, gitProjectId, branch, privateToken);
        String content = HttpClientUtils.doGet(url).getContent();
        return JSONArray.fromObject(content);
    }

    public static Integer getGitProjectId(String url, String privateToken) throws Exception {
        String apiUrl = getApiUrl(url);
        String projectName;
        if (url.startsWith("git")) {
            projectName = URLEncoder.encode(url.substring(url.lastIndexOf(":") + 1, url.length() - 4), "UTF-8");
        } else {
            String path = new URL(url).getPath();
            projectName = URLEncoder.encode(path.substring(1, path.length() - 4), "UTF-8");
        }
        Map<String, String> map = new HashMap<>();
        map.put("private_token", privateToken);
        HttpClientResult result = HttpClientUtils.doGet(apiUrl + "projects/" + projectName, map);
        if (result.getCode() == 200) {
            String content = result.getContent();
            return JSONObject.fromObject(content).optInt("id");
        } else {
            return 0;
        }
    }

    public static Boolean checkPrivateToken(String gitUrl, String privateToken) throws Exception {
        String apiUrl = getApiUrl(gitUrl) + "projects?private_token=%s";
        apiUrl = String.format(apiUrl, privateToken);
        HttpClientResult result = HttpClientUtils.doGet(apiUrl);
        if (result.getCode() == 200) {
            return true;
        } else {
            return false;
        }
    }

    private static String getApiUrl(String urlStr) throws MalformedURLException {
        String GIT_URL_V3 = "git.corp.qihoo.net";
        String GIT_URL_V3_1 = "v.src.corp.qihoo.net";
        // String GIT_URL_V4 = "adgit.src.corp.qihoo.net";
        String apiUrl;
        if (urlStr.startsWith("git")) {
            //git@git.corp.qihoo.net:webtest/qixiao-fe.git
            String substring = urlStr.substring(4, urlStr.lastIndexOf(":"));
            if (GIT_URL_V3.equals(substring) || GIT_URL_V3_1.equals(substring)) {
                apiUrl = "https://" + substring + "/api/v3/";
            } else {
                apiUrl = "https://" + substring + "/api/v4/";
            }
        } else {
            URL url = new URL(urlStr);
            if (GIT_URL_V3.equals(url.getHost()) || GIT_URL_V3_1.equals(url.getHost())) {
                apiUrl = url.getProtocol() + "://" + url.getHost() + "/api/v3/";
            } else {
                apiUrl = url.getProtocol() + "://" + url.getHost() + "/api/v4/";
            }
        }
        return apiUrl;
    }

}
