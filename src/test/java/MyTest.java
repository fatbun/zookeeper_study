import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class MyTest {


    @Test
    public void test01() throws InterruptedException {
        Latch l = new Latch();
        CountDownLatch latch = new CountDownLatch(1);
        l.setLatch(latch);

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                // 外面countDownLatch在await时，里面new了一个新的counDownLatch会导致外面的一直阻塞释放不了
                l.setLatch(new CountDownLatch(1));
                //                l.getLatch()
                //                        .countDown();
                l.getLatch()
                        .countDown();
                System.out.println("thread count down...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("1");
        l.getLatch()
                .await();
        System.out.println("2");

    }

    class Latch {
        CountDownLatch latch;

        public CountDownLatch getLatch() {
            return latch;
        }

        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }
    }
}
