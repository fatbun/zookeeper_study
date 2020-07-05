import org.apache.zookeeper.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zookeeper.DefaultWatcher;
import zookeeper.MyConf;
import zookeeper.WatchCallback;

import java.util.concurrent.CountDownLatch;

/**
 * zookeeper 配置中心
 */
public class TestConf {

    private ZooKeeper zk;

    private TestConf myConf;

    @Before
    public void before() {

        DefaultWatcher defaultWatch = new DefaultWatcher();
        CountDownLatch cdl = new CountDownLatch(1);

        try {
            zk = new ZooKeeper("127.0.0.1:2181/conf",
                    1000,
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
    public void conf() {
        MyConf conf = new MyConf();
        WatchCallback watchCallback = new WatchCallback();

        watchCallback.setZk(zk);
        watchCallback.setMyConf(conf);

        watchCallback.await();

        while (true) {
            // 配置不存在
            if ("".equals(watchCallback.getMyConf()
                    .getConf())) {
                zk.exists("/",
                        watchCallback,
                        watchCallback,
                        "exist");
            }
            // 存在
            else {
                System.out.println(watchCallback.getMyConf()
                        .getConf());
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }
}
