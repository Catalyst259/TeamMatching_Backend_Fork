# SpringBoot 请求生命周期学习导览图

# 目标

理解：

HTTP
→ Controller
→ DTO
→ Service
→ Mapper
→ SQL
→ Response JSON

整个请求是如何运行的。

---

# 第零阶段：Spring 如何启动
pipeline:
```text
main()
 ↓
SpringApplication.run()
 ↓
1. 推断应用类型（NONE / SERVLET / REACTIVE）
 ↓
2. 创建 SpringApplication 对象
 ↓
3. 加载 ApplicationContextInitializer 和 Listener
 ↓
4. 准备 Environment
 ↓
5. 读取 application.yml / application.properties
 ↓
6. 创建 ApplicationContext（IOC容器）
 ↓
7. 执行 ApplicationContextInitializer
 ↓
8. 注册 BeanDefinition
 ↓
9. refresh() 刷新容器
    ├─ BeanFactory 后处理
    ├─ 自动配置生效
    ├─ 实例化单例 Bean
    ├─ AOP/依赖注入
    ├─ 内嵌 Tomcat 创建并启动
10. 发布启动完成事件
 ↓
 ↓
11. ApplicationRunner / CommandLineRunner 执行
```
## 需要知道的问题

- SpringBoot 为什么从 main() 开始运行？
- JVM 启动后会像普通的 Java 程序一样寻找：public static void main(String[] args)
- SpringApplication.run() 到底做了什么？
- 创建 SpringApplication 对象 (确定是Web项目)，准备 Environment(读取application.yml)，创建 IOC 容器(管理全局Bean对象).
- @SpringBootApplication 为什么能启动整个项目？
```java
@SpringBootApplication
```
等于
```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```
@ComponentScan负责扫描Bean
@EnableAutoConfiguration会加载大量自动配置类，完成自动装配

- Spring 是怎么扫描并创建 Bean 的？
- 扫描并找到IOC注解后，Spring会注册 BeanDefinition，最后进行bean的实例化并放入单例池
- IOC 容器是什么时候创建的？
- 在run()里面new出来的，在@ComponentScan扫描之前创建.
- Tomcat 为什么会自动启动？
- Tomcat接收HTTP请求，交给JAVA处理，再返回HTTP响应,在Bean实例化之后，如果在pom.xml中引入了spring-boot-starter-web，Tomcat会自动启动.
- Spring 是什么时候开始接收 HTTP 请求的？
  tomcat.start()启动完成后，就能开始监听，此时日志会弹出：Tomcat started on port 8080 (http) with context path ''
---

# 第一阶段：HTTP 请求进入 Spring

## 需要知道的问题

- HTTP 请求是什么？
- HTTP 请求是客户端向服务器发送的数据包，包含请求行、请求头、请求体，用于规定客户端与服务器之间如何通信。
- GET / POST 有什么区别？
- GET：获取资源；POST：提交数据
- GET 参数通常在URL，可以被缓存，不修改服务器数据，多次请求结果一致；
- POST 数据通常放在 body，会修改服务器数据，不幂等
- 前端请求为什么能进入 SpringBoot？
- Tomcat监听http端口，当浏览器发送请求时，Tomcat 接收到 HTTP 数据，然后按照 Servlet 规范，把请求交给 SpringMVC 的 DispatcherServlet 处理。
- Tomcat 在 SpringBoot 里干什么？
- Tomcat 是 Web 服务器和 Servlet 容器，负责监听 HTTP 请求、解析协议、创建请求对象，并调用 SpringMVC 的 DispatcherServlet。
- 什么是 DispatcherServlet？
- DispatcherServlet 是 SpringMVC 的核心 Servlet，作为前端控制器，统一接收请求并分发到对应的 Controller 方法。
- Spring 为什么能找到对应 Controller？
- Spring 在启动时会扫描 Controller (即@RestController)，并将 URL 与 Controller 方法保存到 HandlerMapping 中。请求到达后，DispatcherServlet 根据 URL 找到对应方法并调用。
- @PostMapping / @GetMapping 做了什么？
- @PostMapping/@GetMapping 本质是路由映射注解。Spring 启动时会解析这些注解，并建立 URL 与 Controller 方法之间的映射关系。
- 比如把/auth/login 路由与 login()方法进行绑定
- URL 为什么能匹配到方法？
- 正如上一步所说，SpringMVC 在启动时通过 HandlerMapping 保存了 URL 与方法的映射，请求到达后 DispatcherServlet 会根据 URL 查找对应方法。

- **Servlet 是 Java Web 处理 HTTP 请求的标准规范。**
- **Tomcat 收到 HTTP 请求后，会调用对应 Servlet 处理请求。**
- **SpringMVC 是基于 Servlet 的封装，其核心是 DispatcherServlet（前端控制器）。**
- **所有请求会先进入 DispatcherServlet，**
- **再通过 HandlerMapping 根据 URL 找到对应 Controller 方法，**
- **最后完成参数绑定、方法调用和响应返回。**

---

# 第二阶段：Controller 层

## 需要知道的问题

- Controller 的职责是什么？
- 只干四个事：接收请求，参数校验，调用 Service，返回结果
- 为什么 Controller 不写业务逻辑？
- 实现接口与业务的解耦，方便业务逻辑的复用与调试
- @RestController 是什么？
- @Controller + @ResponseBody，把类标记为Spring Controller和并将回值自动序列化为JSON
- @RequestMapping 是什么？
- 规定由这个controller负责的根路由如：
```java
@RestController
@RequestMapping("/user")
public class UserController {

  @GetMapping("/info")
  public User info() {}
}
```
- 这里info函数的路由为/user/info
- @RequestBody 是什么？
- 加在参数前的注解，把 HTTP Body 中的 JSON 转成 Java对象如：
```java
public ResponseEntity<CommonResponse<RegisterResponse>> register(
        @ApiParam(value = "注册请求参数", required = true)
        @Valid @RequestBody RegisterRequest registerRequest) {
}
```
registerRequest会从json被转化为RegisterRequest类对象，再传入。
- JSON 为什么能自动变 Java 对象？
  @RequestBody参数注解会让 Spring MVC 使用 HttpMessageConverter 调用 Jackson 的 ObjectMapper 把 JSON 转为 Java对象。
- Spring 怎么创建 DTO？
- Jackson自动赋值
- @PathVariable 是什么？
- 加在参数前的注解，储存在路由中的变量通过@PathVariable传路径参数
- @RequestParam 是什么？
- 加在参数前的注解，获取URL问号后的参数
PathVariable 决定资源定位，RequestParam 决定资源筛选如：
```java
@GetMapping("/post/{postId}/comments")
public ResponseEntity<CommonResponse<List<CommunityCommentItem>>> getPostComments(
        @PathVariable @NotNull(message = "帖子ID不能为空") @Min(1) Long postId,
        @RequestParam(required = false, defaultValue = "1") @Min(1) Integer page,
        @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
    }
```
postId 决定查哪个帖子列表，page,size决定查第几页，每页多少条
- 为什么返回对象会自动变 JSON？
- @RestController 中的 @ResponseBody 会自动处理，处理方式同 @RequestBody
---

一个完整请求在 Controller 层的执行流程

```text
1. HTTP请求进入DispatcherServlet

2. 根据@RequestMapping找到Controller方法

3. 解析参数：
   - @PathVariable
   - @RequestParam
   - @RequestBody

4. HttpMessageConverter调用Jackson
   JSON -> DTO

5. 参数校验：
   @Valid / Hibernate Validator

6. 调用Controller方法

7. Controller调用Service

8. 返回Java对象

9. HttpMessageConverter调用Jackson
   Java对象 -> JSON

10. 返回HTTP响应
```

# 第三阶段：DTO

## 需要知道的问题

- DTO 是什么？
- DTO（Data Transfer Object）是用于不同层之间传输数据的对象，在 Web 开发中通常用于前后端数据交互。
- 为什么不用 Entity 接收前端数据？
- Entity 是数据库模型，不应该直接暴露到接口层。直接用Entity接收无法隔离数据库修改对接口的影响，甚至可能导致数据库被前端篡改
- Request DTO 和 Response DTO 为什么分开？
- Request DTO 定义用户能传什么，Response DTO 定义用户能看到什么。
- DTO 和 VO 的区别是什么？
- View Object 的数据组织更有利于前端页面展示，而 DTO 更强调数据传输
- DTO 为什么更安全？
- 保证前端只能拿到后端想让前端拿到的东西，符合最小暴露原则
- @Valid 为什么会自动校验？
- 因为有@Valid注解
- @NotBlank / @NotNull 有什么作用？
- @NotNull 要求不能为Null，@NotBlank 要求不能为 null，不能为 ""，不能全是空格，@NotEmpty 要求不能为 null且长度不能为0
- 参数错误为什么会自动报错？
- Spring检查到注解，会调用 Hibernate Validator 对 DTO 进行校验，校验失败后抛出异常，再由全局异常处理器统一处理

---

# 第四阶段：Service 层（重点）

## 需要知道的问题

- Service 的职责是什么？
- Service 负责“完成一个业务功能”。
- 为什么业务逻辑放 Service？
- Controller 属于“接口层”，应该尽量简单，只负责调用service
- 为什么要 Service 接口 + Impl？
- 声明与实现分离，接口更加清晰
- 为什么不能 Controller 直接操作数据库？
- 如果数据库有改动，Controller要跟着一起改
- 什么叫“业务逻辑”？
- 业务逻辑=业务流程+业务规则，比如注册流程需要判断手机号是否存在，密码加密，创建用户，初始化头像；下单流程需要检查库存，检查余额，扣库存等。
- 什么叫“业务行为”？
- 业务行为是系统提供给用户的能力，一个业务行为通常对应一个service方法，比如注册，点赞，下单，评论...
- 为什么要拆方法？
- 把一个业务行为拆分成多步业务逻辑，每个业务逻辑封装成一个函数。拆方法是为了让业务结构更清晰，而不是机械拆分。
- 拆分逻辑参考：
```text
1. 谁在操作？
2. 操作对象是否存在？
3. 是否有权限？
4. 当前状态是否合法？
5. 输入是否合法？
6. 是否存在并发问题？
7. 核心动作是什么？
8. 是否有联动数据？
9. 是否有后续副作用？
```
- 为什么这里要加 @Transactional？
- 保证一个逻辑要么全部成功，要么全部失败，不允许有混沌态
- Transaction 到底保护了什么？
- 数据库修改操作的一致性
- Service 为什么通常是 Bean？
- 方便由Spring统一管理，注入依赖 做事务 做AOP 做缓存 做权限 做日志
- Spring 为什么能自动注入 Service？
- 因为用了 @Service 注解，会把类注册进 IOC 容器。
---

# 第五阶段：IOC / Bean / DI

## 需要知道的问题

- Bean 是什么？
- 一个被 Spring IOC 容器管理的对象。
- IOC 是什么？
- 一种机制，对象不再由程序员自己创建，而是交给 Spring 管理。
- IOC 容器是什么？
- Spring内部的一个大型对象管理中心，负责创建对象，保存对象，查找对象，注入对象，销毁对象
```text
IOC Container
 ├── UserService Bean
 ├── UserMapper Bean
 └── EmailService Bean
```
- Spring 为什么要管理对象？
- 自动做依赖注入，AOP功能，生命周期管理
- 为什么不能自己 new？
- new出来后Spring没法自动管理。
- @Service 为什么能自动创建对象？
- Spring启动时会进行组件扫描，统一留位置，放入IOC容器，并实例化
- @Autowired 做了什么？
- 自动从IOC容器中寻找并注入依赖对象，这样不需要重新new一个对象，注入后可以直接使用.操作访问已注入Bean属性与方法
- 可以使用 @AutoWired 进行依赖注入，也可以用 @RequiredArgsConstructor + final关键字
- Spring 怎么找到依赖对象？
- 在IOC里面按类查找
- 什么是依赖注入（DI）？
- 不用自己手动new了，写个public final Class object自动导入一个Bean的类对象，直接访问属性和方法
- Bean 生命周期是什么？
- 全局生命周期,从Spring启动到Spring停止运行

---

# 第六阶段：AOP / Transaction

## 需要知道的问题

- AOP 是什么？
- 不修改原代码，统一给某些方法增加额外逻辑，比如日志，事务，权限检查
- 为什么 Spring 能自动增强方法？
- 因为Spring会创建一个代理对象把原对象包起来，代理对象的方法会先触发
- 什么是代理对象（Proxy）？
- 代理对象 = 包装原对象的增强对象
- 为什么 @Transactional 能生效？
- @Transactional 触发了 Spring AOP，它不会调用原方法，而是会开启一个事务代理对象
- Spring 怎么自动开启事务？
如下：
```text
@Transactional
    ↓
Spring AOP发现注解
    ↓
创建代理对象
    ↓
代理对象调用:
    ↓
transactionManager.begin()
    ↓
执行原方法
    ↓
commit / rollback
```

- 为什么事务会自动回滚？
- @Transactional(rollbackFor = Exception.class)的写法可以让全部异常回滚
- 什么情况下事务会失效？
- 事务失效即Spring的事务代理没有生效，方法自调用同类内方法时，方法为private时，自己new对象等情况
- 为什么事务通常放 Service 层？
- 一个完整的业务流程必须一起成功，Controller只负责HTTP接口，Mapper只负责单条SQL
- AOP 为什么必须依赖 Bean？
- Spring 只能代理自己管理的对象，new出来的对象全程由自己管理，Spring控制不到

---

# 第七阶段：Mapper / MyBatis-Plus

## 需要知道的问题

- Mapper 是什么？
- Mapper 本质是 SQL 的 Java 接口化。它负责调用数据库，执行 SQL，返回 Java 对象
- MyBatis 和 MyBatis-Plus 有什么区别？
- MyBatis是SQL映射框架。你、我自己写 SQL， MyBatis 帮我：执行 SQL，处理 JDBC，封装结果对象
- MyBatisPlus自动实现了CRUD功能，而MyBatis里所有数据库操作需要我们手写.
- 为什么 selectById 不用自己写？
- 因为 MyBatis-Plus 帮我自动实现了,在继承基类 BaseMapper 的时候，方法被一并继承了
- BaseMapper 做了什么？
- BaseMapper是基类，实现了大量的简单CRUD。当我的Mapper继承BaseMapper时，它自动获得所有增删改查功能
- 为什么 Mapper 接口没有实现类？
- Mapper 的 Implementation 由 Spring 动态实现
- 什么是动态代理？
- Spring/MyBatis 在运行时会偷偷生成 Mapper 的实现类
- MyBatis-Plus 怎么生成 SQL？
- selectById()这种操作会调用代理对象，分析 Entity,自动拼 SQL,执行 JDBC,最后封装成对象返回
- Entity 为什么能对应数据库表？
- 因为有注解 @TableName 和 @TableId
- @TableName 是什么？
- 对应数据表的名字
- @TableId 是什么？
- 对应数据表的主键字段
- 为什么复杂查询最终还是要写 SQL？
- 多表Join，子查询，聚合函数，分类统计等复杂功能 CRUD 搞不定

---
# 第八阶段：SQL / 数据库

## 需要知道的问题

- SQL 是什么时候执行的？
- 调用Mapper方法时，MyBatis会生成并执行SQL
- JDBC 是什么？
- Java与数据库交互的统一接口规范
- MyBatis 怎么调用数据库？
- MyBatis会根据Mapper中定义的方法生成SQL语句，然后通过JDBC框架连接数据库并执行SQL
- 数据库连接是谁管理的？
- 数据库连接通常由连接池（如 HikariCP）管理，JDBC 只是提供数据库连接接口。
- 什么是连接池？
- 连接池会提前创建多个数据库连接并重复利用，避免每次执行 SQL 都重新建立连接，从而提升性能。因为建立数据库连接非常昂贵。
```text
JDBC = 连接数据库的方法
连接池 = 管理连接对象的组件
```
- 为什么 SQL 日志很重要？
- 因为能让我清晰地看到是哪一步出问题了
- 如何查看真实执行 SQL？
- 把SQL日志打印到控制台。可以通过 MyBatis 日志配置或数据库监控工具查看真实执行 SQL。
- 什么是事务提交？
- 事务提交（commit）表示将事务中的所有 SQL 修改正式写入数据库，使数据永久生效。（即COMMIT）
- 什么是事务回滚？
- 事务回滚（rollback）是指事务执行过程中出现异常时，撤销事务中已经执行的 SQL，使数据库恢复到事务开始前的状态。
---

# 第九阶段：Response 返回

## 需要知道的问题

- Java 对象为什么自动变 JSON？
- @RestController 中包含了 @ResponseBody，Spring看到后会调用 HttpMessageConverter，再由 Jackson 把 Java对象序列化成 JSON
- Jackson 是什么？
- SpringBoot默认的第三方Json处理框架，负责： Java对象 → JSON（序列化），JSON → Java对象（反序列化）
- 为什么返回 DTO 而不是 Entity？
- 最小暴露原则，只给前端暴露最低限度的内容。Entity是数据库对象，DTO是接口数据对象
- 为什么很多项目有统一返回结构？
- 统一前后端交互规范，方便错误处理，前端解析，以及保持接口一致
- Result<T> 有什么意义？
- 统一接口返回格式，使用泛型让 data 能支持任意类型，保持代码复用性与类型安全
```java
public static <T> CommonResponse<T> ok() {
        CommonResponse<T> response = new CommonResponse<>();
        response.setSuccess(true);
        response.setCode(200);
        response.setMessage("操作成功");
        return response;
    }
```
- HTTP 状态码是什么？
  200	请求成功
  400	请求错误
  401	未登录
  403	无权限
  404	资源不存在
  500	服务器内部错误
- Spring 可以自动产生，也可以后端手动赋值
- Spring 怎么返回 HTTP Response？
- DispatcherServlet 接收返回值，HttpMessageConverter 把对象转JSON，SpringMVC封装HTTP响应，最后由Tomcat把响应数据发送给浏览器
---
```text
Controller
    ↓
DispatcherServlet
    ↓
HttpMessageConverter
    ↓
Jackson
    ↓
Tomcat Response
    ↓
Browser
```
# 第十阶段：异常处理

## 需要知道的问题

- 为什么程序异常不会直接崩？
- Tomcat 是长期运行的服务器进程，每个请求只是一个线程，SpringBoot会兜底处理异常
- 什么是全局异常处理？
- @RestControllerAdvice全局Controller增强器，如：
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // 错误处理函数
}
```
专门用于：统一处理异常，统一返回错误，JSON 统一日志处理。可参考本项目中的GlobalException.java 模块
- @RestControllerAdvice 是什么？
- @ControllerAdvice + @ResponseBody，全局异常处理
- ExceptionHandler 做了什么？
```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<Map<String, Object>> handleValidationExceptions(
        MethodArgumentNotValidException ex) {

  Map<String, Object> response = new HashMap<>();
  response.put("code", 400);
  response.put("message", "参数校验失败");

  Map<String, String> errors = new HashMap<>();
  ex.getBindingResult().getAllErrors().forEach((error) -> {
    String fieldName = ((FieldError) error).getField();
    String errorMessage = error.getDefaultMessage();
    errors.put(fieldName, errorMessage);
  });

  response.put("errors", errors);
  log.warn("参数校验失败: {}", errors);

  return ResponseEntity.badRequest().body(response);
}
```
如果 Controller 出现参数校验异常，SpringMVC 自动调用这个方法。它做了：异常 → 匹配对应Handler → 转成Response
- 为什么参数错误能自动返回 JSON？
- @RestControllerAdvice 捕获异常会统一返回报错的JSON
- 为什么不能到处 try-catch？
- 严重降低可读性，且很多错误交给全局异常处理器更合适
```text
Controller
    ↓
Service
    ↓
出现异常
    ↓
异常向上抛出
    ↓
DispatcherServlet
    ↓
HandlerExceptionResolver
    ↓
@RestControllerAdvice
    ↓
@ExceptionHandler
    ↓
Result JSON
    ↓
HttpMessageConverter
    ↓
Browser
```
---

# 第十一阶段：日志

## 需要知道的问题

- 为什么企业不用 System.out.println？
- 没有 INFO,WARN,ERROR,DEBUG 的日志等级，且无法控制输出格式或统一导出
- Slf4j 是什么？
- Java 的日志标准接口，打上这个注解Lombok会自动实例化log对象，无需手动实例化
- log.info() 和 log.error() 有什么区别？
- 日志等级不同，后者代表 ERROR 等级，意味着服务出现错误
- 为什么日志重要？
- 日志 = 事故现场监控录像
- 日志什么时候打？
- 关键业务节点，重要状态变化，外部调用，异常，调试阶段会打印日志
```java
@PostMapping("/register")
    @ApiOperation(value = "邮箱/手机号注册", notes = "用户通过邮箱或手机号进行注册")
    public ResponseEntity<CommonResponse<RegisterResponse>> register(
            @ApiParam(value = "注册请求参数", required = true)
            @Valid @RequestBody RegisterRequest registerRequest) {
        
        log.info("收到注册请求：account={}", registerRequest.getAccount());
        
        try {
            RegisterResponse response = authService.register(registerRequest);
            log.info("注册成功：userId={}", response.getUserId());

            return ResponseEntity.ok(CommonResponse.ok(response));
        } catch (Exception e) {
            log.error("注册失败：account={}, error={}", registerRequest.getAccount(), e.getMessage(), e);
            throw e;
        }
    }
```
这里打了三个日志，其中两个info追踪业务逻辑，一个处理异常。但值得注意的是，账号不存在，密码错误这类业务错误不应该使用 ERROR 级别的日志
- 如何通过日志追请求？
- 是靠 TraceId，一次请求的所有日志会带同一个TraceId，线程Id也可以代替TraceId

---

# 第十二阶段：完整请求链路（最终目标）

## 你最终应该能脑补：

```text
SpringBoot 启动
↓
HTTP Request
↓
Tomcat
↓
DispatcherServlet
↓
Controller
↓
@RequestBody JSON -> DTO
↓
Validation 参数校验
↓
Service
↓
@Transactional
↓
AOP代理
↓
Mapper
↓
MyBatis-Plus
↓
SQL
↓
MySQL
↓
返回结果
↓
Jackson对象转JSON
↓
HTTP Response