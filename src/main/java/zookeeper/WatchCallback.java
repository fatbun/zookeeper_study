package zookeeper;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;


public class WatchCallback implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    private ZooKeeper zk;

    private MyConf myConf;

    private CountDownLatch cdl = new CountDownLatch(1);

    public void setCdl(CountDownLatch cdl) {
        this.cdl = cdl;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public MyConf getMyConf() {
        return myConf;
    }

    public void await() {
        zk.exists("/",
                this,
                this,
                "exist");

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // StatCallback 处理后回调
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        System.out.println("-----StatCallback-----");
        System.out.println("rc: " + rc);
        System.out.println("path: " + path);
        System.out.println("ctx: " + ctx);
        System.out.println("stat: " + stat);

        if (stat == null) {
            System.out.println("=====================");
            System.out.println("conf was lost...");
            System.out.println("=====================");

            myConf.setConf("");
        } else {
            zk.getData("/",
                    this,
                    this,
                    "getdata");
        }
    }

    // Watcher
    public void process(WatchedEvent event) {
        System.out.println("-----watcher-----");
        System.out.println("path: " + event.getPath());
        System.out.println("state: " + event.getState());

        switch (event.getType()) {
            case None:
                System.out.println("None " + event.getPath());
                break;
            case NodeCreated:
                System.out.println("NodeCreated " + event.getPath());
                zk.getData("/",
                        this,
                        this,
                        "created");
                break;
            case NodeDeleted:
                System.out.println("NodeDeleted " + event.getPath());
                myConf.setConf("");
                cdl = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                System.out.println("NodeDataChanged " + event.getPath());
                zk.getData("/",
                        this,
                        this,
                        "created");
                break;
            case NodeChildrenChanged:
                System.out.println("NodeChildrenChanged " + event.getPath());
                break;
            case DataWatchRemoved:
                System.out.println("DataWatchRemoved " + event.getPath());
                break;
            case ChildWatchRemoved:
                System.out.println("ChildWatchRemoved " + event.getPath());
                break;
            case PersistentWatchRemoved:
                System.out.println("PersistentWatchRemoved " + event.getPath());
                break;
        }

    }

    // DataCallback
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {

        if (stat != null) {
            System.out.println("-----DataCallback-----");
            System.out.println("rc: " + rc);
            System.out.println("path: " + path);
            System.out.println("ctx: " + ctx);
            System.out.println("data: " + new String(data));
            System.out.println("stat: " + stat);

            myConf.setConf(new String(data));
            cdl.countDown();
        } else {
            System.out.println("-----DataCallback-----");
            System.out.println("no data...");
        }

    }
}