package vip.aquan.juccachedemo.cache;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: wcp
 * @date: 2023/7/5 02:04
 * @Description: 并发测试，使用CountDownLatch来控制多个线程同时执行
 */
public class Test {
    private final static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CalculateCache<String, Integer> calculateCache = new CalculateCache<>(new CalculateImpl());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            Runnable runnable = () -> {
                try {
                    countDownLatch.await();
                    Integer result = calculateCache.calculate("123");
                    System.out.println(Thread.currentThread().getName() + " 计算结果为：" + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            threadPool.execute(runnable);
        }
        countDownLatch.countDown();
        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
        }
        System.out.println("执行结束，耗时:" + (System.currentTimeMillis() - start));
    }
}
