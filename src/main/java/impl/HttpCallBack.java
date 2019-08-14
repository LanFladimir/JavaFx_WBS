package main.java.impl;

/**
 * 网络交互反馈
 */
public interface HttpCallBack {
    void onSuccess(String tag, String ip,String resp);

    void onFailed(String tag, String ip, String failedinfo);

    void onComplete();
}
