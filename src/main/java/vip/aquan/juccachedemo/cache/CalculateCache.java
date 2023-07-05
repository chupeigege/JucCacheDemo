package vip.aquan.juccachedemo.cache;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @author: wcp
 * @date: 2023/7/5 01:34
 * @Description: 缓存装饰器
 */
public class CalculateCache<V, R> implements Calculate<V, R> {
    private final Calculate<V, R> calculate;
    private final Map<V, Future<R>> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(10);

    public CalculateCache(Calculate<V, R> calculate) {
        this.calculate = calculate;
    }

    @Override
    public R calculate(V arg) throws ExecutionException, InterruptedException {
        Future<R> future = cache.get(arg);
        if (future == null) {
            Callable<R> callable = () -> this.calculate.calculate(arg);
            FutureTask<R> ft = new FutureTask<>(callable);
            future = cache.putIfAbsent(arg, ft);
            if (future == null) {
                System.out.println("执行计算线程");
                future = ft;
                ft.run();
            }
        }
        try {
            return future.get();
        } catch (Exception e) {
            System.out.println("执行异常，清除缓存");
            cache.remove(arg);
            throw e;
        }
    }

    public R calculate(V arg, long expire) throws ExecutionException, InterruptedException {
        if (expire > 0) {
            System.out.println("开启定时清除，expire:" + expire);
            scheduledThreadPool.schedule(() -> expire(arg), expire, TimeUnit.MILLISECONDS);
        }
        return calculate(arg);
    }

    // 应该单独开个延时器，比如设定延时时间把消息放入MQ,到时间执行一次
    // 这是本地缓存，还需要开订阅模式，各个机器都同步缓存信息
    public R calculateRandomTime(V arg) throws ExecutionException, InterruptedException {
        long random = new Random().nextInt(10000);
        return calculate(arg, random);
    }

    private synchronized void expire(V arg) {
        Future<R> future = cache.get(arg);
        if (future != null) {
            System.out.println("key:" + arg + " 缓存到期，自动清除");
            cache.remove(arg);
        }
    }

}
