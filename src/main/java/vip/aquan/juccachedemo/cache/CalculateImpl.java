package vip.aquan.juccachedemo.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author: wcp
 * @date: 2023/7/5 01:31
 * @Description: 计算业务实现类
 */
public class CalculateImpl implements Calculate<String, Integer> {
    @Override
    public Integer calculate(String arg) throws Exception {
        //模拟出现概率异常
//        if (Math.random() > 0.5) {
//            throw new RuntimeException("业务执行异常");
//        }
        //休眠，模拟业务耗时
        TimeUnit.SECONDS.sleep(2);
        return Integer.valueOf(arg);
    }
}
