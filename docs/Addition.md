# 第十三阶段：Filter / Interceptor

## 需要知道的问题
Filter 是什么？
Interceptor 是什么？
两者有什么区别？
请求会先经过谁？
DispatcherServlet 和 Interceptor 的关系是什么？
Filter 属于哪一层？
Interceptor 属于哪一层？
preHandle / postHandle / afterCompletion 是什么？
为什么登录校验常写在 Interceptor？
为什么 Spring Security 更偏向 Filter？
Filter / Interceptor / AOP 分别适合做什么？

# 第十四阶段：JWT 登录链路

## 需要知道的问题
JWT 是什么？
JWT 为什么能实现无状态登录？
JWT 的三部分是什么？
JWT 为什么不能被篡改？
JWT 为什么不能存敏感信息？
JWT 一般放在哪里？
JWT 校验通常写在哪？
JWT 登录完整流程是什么？
JWT 和 Session 有什么区别？
JWT 为什么适合分布式？
JWT 为什么常配合 Redis？
refresh token 是什么？

# 第十五阶段：ThreadLocal

## 需要知道的问题
ThreadLocal 是什么？
为什么需要 ThreadLocal？
ThreadLocal 为什么能线程隔离？
为什么适合存当前用户信息？
为什么 Controller 任意位置都能获取当前用户？
ThreadLocal 为什么不能跨线程？
为什么 ThreadLocal 可能内存泄漏？
为什么必须 remove()？
Spring 哪些地方用了 ThreadLocal？
Spring 事务为什么依赖 ThreadLocal？

# 第十四阶段：Redis

## 需要知道的问题
Redis 是什么？
Redis 为什么快？
Redis 和 MySQL 有什么区别？
Redis 为什么适合做缓存？
Redis 常见数据结构有哪些？
Redis 为什么适合存验证码 / Token？
什么是缓存穿透 / 击穿 / 雪崩？
Redis 为什么能做分布式锁？
Redis 为什么适合限流？
Redis 为什么需要过期时间（TTL）？
Redis 为什么会有缓存一致性问题？
SpringBoot 怎么整合 Redis ？
Redis 在企业开发中的典型场景有哪些？