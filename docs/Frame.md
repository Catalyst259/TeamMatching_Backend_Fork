# Spring 核心链路

## 1. Bean 的创建和管理
Bean 是被 Spring 管理的对象，而不是自己 new 出来的普通对象。Spring 通过依赖注入（Dependency Injection）创建并管理 Bean 的生命周期。

应用启动时，Spring 会：
- 扫描指定包
- 找到被 @Component、@Service、@Repository 等注解标记的类
- 将它们注册为 Bean
- 处理 Bean 之间的依赖关系，确保在需要时正确注入

比如：
```java
@Service
public class UserServiceImpl{
    // 业务方法
}
```
Spring 扫描到 @Service 注解后，会创建 UserServiceImpl 的实例，并将其注册为 Bean。当其他组件需要使用 UserServiceImpl 时，Spring 会自动注入这个 Bean。

## 2. IOC (Inversion of Control，控制反转)
以前：
```java
UserService service = new UserServiceImpl();
```
对象由程序员手动创建。

现在：
Spring 通过控制反转（IOC）来管理对象的创建和依赖关系。程序员不再直接创建对象，而是由 Spring 负责创建和管理 Bean。
当需要使用某个 Bean 时，Spring 会自动注入它。

## 3. IOC 容器与 @Autowired
IOC 容器是 Spring 用来管理 Bean 的核心组件。

它里面存：Controller、Service、Mapper、Component 等被注解标记的类实例。

当 Spring 运行时，如果需要对象，就从 IOC 容器中获取并自动注入。

比如：
```java
@RestController // 把 UserController 作为 Bean 注册到 IOC 容器。
public class UserController {
    @Autowired // 让容器把已经创建好的 UserService Bean 注入进来。
    private UserService userService;
    // 业务方法
}
```
在这个例子中，UserController 被注册为 Bean，由于 UserController 依赖 UserService，且打了 @Autowired 注解，Spring 会去 IOC 容器里寻找 UserService Bean，并将其注入到 UserController 中。这样，UserController 就可以使用 UserService 的功能，而不需要自己创建 UserService 的实例。

相当于：
```java
controller.userService = userServiceBean;
```
如果自己 new UserServiceImpl()，就会绕过 Spring 的管理，导致无法享受 Spring 提供的各种功能，比如 AOP、事务管理等，因为 Spring 根本不知道这个对象存在。

## 4. AOP (Aspect-Oriented Programming，面向切面编程)
Spring 会通过额外的一层封装给 Bean 创建一个代理对象。调用 Bean 的方法时，代理对象会先执行一些额外逻辑（比如日志记录、权限检查、事务管理等），再调用原始的 Bean 方法。

这就是 AOP 的核心思想：将横切关注点（cross-cutting concerns）从业务逻辑中分离出来，使代码更加清晰和可维护。

比如：
```java
@Service
public class UserServiceImpl implements UserService {
    @Override
    @Transactional // 这个注解会让 Spring 在调用这个方法时，自动开启事务，并在方法执行完成后提交或回滚事务。
    // 这个注解消除了混沌态，操作后，要么事务全部成功，要么事务全部失败
    public void createUser(User user) {
        // 业务逻辑
    }
}
```
加了 @Transactional 注解后，Spring 会在调用 createUser 方法时自动处理事务的开启和提交/回滚，我们只需要写业务逻辑即可。

Spring 会构建代理对象 TransactionProxy(UserServiceImpl)。当调用 createUser 方法时，会先进入代理对象，执行额外逻辑，再调用真实方法 createUser。这样，我们就可以在不修改业务逻辑代码的情况下，轻松地添加事务管理等功能。

@Transactional 能在这里生效的前提是：
- UserServiceImpl 是被 Spring 管理的 Bean
- createUser 方法是通过 Spring 的代理对象调用的

如果直接 new UserServiceImpl()，就会绕过 Spring 的管理，导致 @Transactional 注解无法生效，因为 Spring 根本不知道这个对象存在。

## 5. Spring 的核心运行思路
```
启动 SpringBoot
↓
扫描注解
↓
创建 Bean
↓
放入 IOC 容器
↓
自动依赖注入（Autowired）
↓
Spring 创建代理对象（AOP）
↓
@Transactional 等增强生效
↓
项目运行
```


