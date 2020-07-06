package zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class LockWatchCallback implements Watcher, AsyncCallback.Create2Callback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    private ZooKeeper zk;

    private String threadName;

    private CountDownLatch cdl = new CountDownLatch(1);

    private String pathName;

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public void tryLock() {

        System.out.println(threadName + ": try lock...");

        zk.create("/testLock",
                threadName.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL,
                this,
                "abc");

        try {
            cdl.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unLock() {
        try {
            zk.delete(pathName,
                    -1);
            System.out.println(threadName + " over work....");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
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
                break;
            case NodeDeleted:
                System.out.println("NodeDeleted " + event.getPath());
                zk.getChildren("/",
                        false,
                        this,
                        "sdf");
                break;
            case NodeDataChanged:
                System.out.println("NodeDataChanged " + event.getPath());
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

    //    Create2Callback
    @Override
    public void processResult(int rc, String path, Object ctx, String name, Stat stat) {
        pathName = name;

        zk.getChildren("/",
                this,
                this,
                name);


    }

    //    Children2Callback
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {
        Collections.sort(children);

        int i = children.indexOf(pathName.substring(1));

        if (i == 0) {
            //第一个
            try {
                zk.setData("/",
                        threadName.getBytes(),
                        -1);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cdl.countDown();
        } else {
            //no
            zk.exists("/" + children.get(i - 1),
                    this,
                    this,
                    "sdf");
        }
    }

    //    StatCallback
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        //nothing
    }
}