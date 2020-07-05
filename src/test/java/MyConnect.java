import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import zookeeper.DefaultWatcher;

import java.io.IOException;

/**
 * zookeeper crud
 */
public class MyConnect {

    private ZooKeeper zk;

    @Before
    public void before() throws IOException {
        zk = new ZooKeeper("127.0.0.1:2181/crud",
                1000,
                new Watcher() {
                    public void process(WatchedEvent event) {
                        System.out.println("------watch------" + event.getPath());
                        System.out.println(event.getState());
                        System.out.println(event.getType());
                    }
                });
    }

    @After
    public void after() throws InterruptedException {
        zk.close();
    }


    @Test
    public void crud() throws KeeperException, InterruptedException {
        String s = zk.create("/ooxx",
                "my first data".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        System.out.println(s);

        zk.getData("/ooxx",
                new DefaultWatcher(),
                new Stat());

        zk.setData("/ooxx",
                "this is new data".getBytes(),
                -1);

        zk.getData("/ooxx",
                new DefaultWatcher(),
                new Stat());

        zk.setData("/ooxx",
                "this is new new data".getBytes(),
                -1);

    }
}
