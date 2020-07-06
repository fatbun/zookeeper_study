import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zookeeper.DefaultWatcher;
import zookeeper.LockWatchCallback;
import zookeeper.MyConf;
import zookeeper.WatchCallback;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 配置中心
 */
public class TestLock {


    private ZooKeeper zk;

    @Before
    public void before() {

        DefaultWatcher defaultWatch = new DefaultWatcher();
        CountDownLatch cdl = new CountDownLatch(1);

        try {
            zk = new ZooKeeper("127.0.0.1:2181/lock",
                    100000,
                    defaultWatch);
            defaultWatch.setCdl(cdl);
            cdl.await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void after() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void lock() {

        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                LockWatchCallback lockWatchCallback = new LockWatchCallback();
                try {
                    String threadName = Thread.currentThread()
                            .getName();

                    lockWatchCallback.setZk(zk);
                    lockWatchCallback.setThreadName(threadName);

                    lockWatchCallback.tryLock();

                    System.out.println(threadName + " working...");

                    lockWatchCallback.unLock();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }

        while (true) {

        }

    }
}
