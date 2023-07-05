package vip.aquan.juccachedemo.cache;

/**
 * @author: wcp
 * @date: 2023/7/5 01:29
 * @Description: 计算业务接口
 */
public interface Calculate<V, R> {
    R calculate(V arg) throws Exception;
}
