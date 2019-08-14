package main.java.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import main.java.impl.HttpCallBack;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpUtils {
    private HttpCallBack mCallback;

    public HttpUtils(HttpCallBack mCallback) {
        this.mCallback = mCallback;
    }

    public void doPost(String url) {
        String ip = url.split("/")[2].split(":")[0];
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (body != null) {
                    String bodyString = body.string();
                    System.out.println(response.body().string());
                    /*if (!bodyString.isEmpty())
                        mCallback.onSuccess(ip);
                    else
                        mCallback.onFailed(ip, "Erroe response：" + bodyString);*/
                }
            }

            public void onFailure(Call call, IOException e) {
                System.out.println(e.getMessage());
                //mCallback.onFailed(ip, e.getMessage());
            }
        });
    }

    public void getRequest(String tag, String url) {
        new Thread(() -> {
            System.out.println("getRequest: " + url + " (" + tag + ")");
            String ip = url.split("/")[2].split(":")[0];
            try {
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                if (tag.equals("install")) {
                    con.setConnectTimeout(15000);
                    con.setReadTimeout(15000);
                } else if (tag.equals("getscreen")) {
                    con.setConnectTimeout(15000);
                    con.setReadTimeout(15000);
                } else {
                    con.setConnectTimeout(5000);
                    con.setReadTimeout(5000);
                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                mCallback.onSuccess(tag, ip, response.toString());
            } catch (IOException e) {
                e.printStackTrace();
                mCallback.onFailed(tag, ip, e.getMessage());
                Logger.append("网络请求异常: ( " + tag + " " + url + " )", e.getMessage());
            }
        }).start();
    }
}
