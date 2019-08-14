package main.java.net;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import main.java.entity.Device;
import main.java.impl.PingCallback;
import main.java.utils.FileUtils;

public class PingWorker {
    private ArrayList<Device> mDevices;
    private PingCallback mPingCall;
    private ExecutorService mPool;
    public static boolean ping = true;

    public PingWorker(PingCallback mPingCall) {
        this.mPingCall = mPingCall;
        mDevices = FileUtils.readDeicesFile();
    }

    public void startPing() {
        updatePingThread();
    }

    public void updatePingThread() {
        ping = false;
        if (mPool != null) {
            mPool.shutdown();
            mPool = null;
        }
        mDevices = FileUtils.readDeicesFile();

        if (mDevices.size() > 0) {
            mPool = Executors.newFixedThreadPool(mDevices.size());
            ping = true;
            for (Device mDevice : mDevices) {
                PingThread t = new PingThread(mDevice.getIp());
                mPool.execute(t);
            }
        } else {
            System.out.println("设备列表空，Ping队列暂停。");
        }
    }

    private class PingThread extends Thread {
        private String ip;

        PingThread(String ip) {
            this.ip = ip;
        }

        @Override
        public void run() {
            while (ping) {
                try {
                    int timeOut = 3000;
                    boolean status = InetAddress.getByName(ip)
                            .isReachable(timeOut);
                    mPingCall.pingCallBack(ip, status);
                } catch (IOException e) {
                    e.printStackTrace();
                    mPingCall.pingCallBack(ip, false);
                }
                try {
                    Thread.sleep(1000 * 5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
