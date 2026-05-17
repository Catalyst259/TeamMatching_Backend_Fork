package club.boyuan.official.teammatching.controller;


import club.boyuan.official.teammatching.common.annotation.NeedLogin;
import club.boyuan.official.teammatching.common.utils.JwtUtils;
import club.boyuan.official.teammatching.common.utils.UserContextUtil;
import club.boyuan.official.teammatching.dto.request.auth.ChangePasswordRequest;
import club.boyuan.official.teammatching.dto.request.auth.ForgotPasswordRequest;
import club.boyuan.official.teammatching.dto.request.auth.LoginRequest;
import club.boyuan.official.teammatching.dto.request.auth.RegisterRequest;
import club.boyuan.official.teammatching.dto.request.auth.SendVerifyCodeRequest;
import club.boyuan.official.teammatching.dto.request.auth.SubmitAuthRequest;
import club.boyuan.official.teammatching.dto.request.auth.WxLoginRequest;
import club.boyuan.official.teammatching.dto.request.auth.VerifyStudentEmailRequest;
import club.boyuan.official.teammatching.dto.response.CommonResponse;
import club.boyuan.official.teammatching.dto.response.auth.RegisterResponse;
import club.boyuan.official.teammatching.dto.response.auth.SendVerifyCodeResponse;
import club.boyuan.official.teammatching.dto.response.auth.SubmitAuthResponse;
import club.boyuan.official.teammatching.dto.response.auth.AuthStatusResponse;
import club.boyuan.official.teammatching.dto.response.auth.WxLoginResponse;
import club.boyuan.official.teammatching.exception.BusinessException;
import club.boyuan.official.teammatching.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证相关控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@Api(tags = "认证接口")
@Validated
@RequiredArgsConstructor // 自动生成构造器
public class AuthController {
    
    // Dependency Injection
    private final AuthService authService;
    
    /**
     * 用户注册接口
     * @param registerRequest 注册请求参数
     * @return 注册响应信息
     */
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
    
    /**
     * 发送验证码接口
     * @param sendVerifyCodeRequest 发送验证码请求参数
     * @return 发送验证码响应信息
     */
    @PostMapping("/send-code")
    @ApiOperation(value = "发送验证码", notes = "向邮箱或手机号发送验证码")
    public ResponseEntity<CommonResponse<SendVerifyCodeResponse>> sendVerifyCode(
            @ApiParam(value = "发送验证码请求参数", required = true)
            @Valid @RequestBody SendVerifyCodeRequest sendVerifyCodeRequest) {
        
        log.info("收到发送验证码请求：target={}, type={}", 
                sendVerifyCodeRequest.getTarget(), sendVerifyCodeRequest.getType());
        
        try {
            authService.sendVerifyCode(sendVerifyCodeRequest);
            log.info("验证码发送成功：target={}", sendVerifyCodeRequest.getTarget());
            
            SendVerifyCodeResponse response = new SendVerifyCodeResponse();
            response.setMessage("验证码发送成功");
            response.setExpireIn(300); // 5 分钟过期
            
            return ResponseEntity.ok(CommonResponse.ok("验证码发送成功", response));
        } catch (Exception e) {
            log.error("发送验证码失败：target={}, error={}", 
                    sendVerifyCodeRequest.getTarget(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 微信一键登录接口
     * @param wxLoginRequest 微信登录请求参数
     * @return 微信登录响应信息
     */
    @PostMapping("/wx-login")
    @ApiOperation(value = "微信一键登录", notes = "通过微信临时登录凭证进行一键登录")
    public ResponseEntity<CommonResponse<WxLoginResponse>> wxLogin(
            @ApiParam(value = "微信登录请求参数", required = true)
            @Valid @RequestBody WxLoginRequest wxLoginRequest) {
        
        log.info("收到微信登录请求：code={}", wxLoginRequest.getCode());
        
        try {
            WxLoginResponse response = authService.wxLogin(wxLoginRequest);
            log.info("微信登录成功：userId={}", response.getUserId());
            return ResponseEntity.ok(CommonResponse.ok(response));
        } catch (Exception e) {
            log.error("微信登录失败：code={}, error={}", wxLoginRequest.getCode(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 密码登录接口
     * @param loginRequest 登录请求参数
     * @return 登录响应信息
     */
    @PostMapping("/login")
    @ApiOperation(value = "密码登录", notes = "通过账号和密码进行登录")
    public ResponseEntity<CommonResponse<RegisterResponse>> login(
            @ApiParam(value = "登录请求参数", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        
        log.info("收到登录请求：account={}", loginRequest.getAccount());
        
        try {
            RegisterResponse response = authService.login(loginRequest);
            log.info("登录成功：userId={}", response.getUserId());
            return ResponseEntity.ok(CommonResponse.ok(response));
        } catch (Exception e) {
            log.error("登录失败：account={}, error={}", loginRequest.getAccount(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 找回密码接口
     * @param forgotPasswordRequest 找回密码请求参数
     * @return 响应信息
     */
    @PostMapping("/forgot-password")
    @ApiOperation(value = "找回密码", notes = "通过邮箱或手机号验证码找回密码")
    public ResponseEntity<CommonResponse<Void>> forgotPassword(
            @ApiParam(value = "找回密码请求参数", required = true)
            @Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        
        log.info("收到找回密码请求：account={}", forgotPasswordRequest.getAccount());
        
        try {
            authService.forgotPassword(forgotPasswordRequest);
            log.info("密码重置成功：account={}", forgotPasswordRequest.getAccount());
            
            return ResponseEntity.ok(CommonResponse.ok("密码重置成功", null));
        } catch (Exception e) {
            log.error("找回密码失败：account={}, error={}", forgotPasswordRequest.getAccount(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 修改密码接口
     * @param changePasswordRequest 修改密码请求参数
     * @return 响应信息
     */
    @PutMapping("/password")
    @ApiOperation(value = "修改密码", notes = "登录后修改密码，需要验证旧密码")
    @NeedLogin
    public ResponseEntity<CommonResponse<Void>> changePassword(
            @ApiParam(value = "修改密码请求参数", required = true)
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        
        log.info("收到修改密码请求");
        
        try {
            // 从上下文中获取当前登录用户 ID
            Integer userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException("用户未登录");
            }
            
            authService.changePassword(changePasswordRequest, userId);
            log.info("密码修改成功：userId={}", userId);
            
            return ResponseEntity.ok(CommonResponse.ok("密码修改成功", null));
        } catch (Exception e) {
            log.error("修改密码失败：userId={}, error={}", UserContextUtil.getCurrentUserId(), e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 提交身份认证接口
     * @param submitAuthRequest 提交认证请求参数
     * @return 提交响应信息
     */
    @PostMapping("/verify")
    @ApiOperation(value = "提交身份认证", notes = "提交校园身份认证信息（包含证明材料）")
    @NeedLogin
    public ResponseEntity<CommonResponse<SubmitAuthResponse>> submitAuth(
            @ApiParam(value = "提交认证请求参数", required = true)
            @Valid @RequestBody SubmitAuthRequest submitAuthRequest) {
        
        log.info("收到提交身份认证请求");
        
        try {
            // 从上下文中获取当前登录用户 ID
            Integer userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException("用户未登录");
            }
            
            SubmitAuthResponse response = authService.submitAuth(submitAuthRequest, userId);
            log.info("身份认证提交成功：userId={}", userId);
            
            return ResponseEntity.ok(CommonResponse.ok(response));
        } catch (Exception e) {
            log.error("身份认证提交失败：error={}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 学生邮箱认证接口
     * @param request 学生邮箱认证请求参数
     * @return 提交响应信息
     */
    @PostMapping("/verify-email")
    @ApiOperation(value = "学生邮箱认证", notes = "通过学生邮箱验证码完成身份认证，仅支持 *.edu.cn 邮箱")
    @NeedLogin
    public ResponseEntity<CommonResponse<SubmitAuthResponse>> verifyByStudentEmail(
            @ApiParam(value = "学生邮箱认证请求参数", required = true)
            @Valid @RequestBody VerifyStudentEmailRequest request) {

        log.info("收到学生邮箱认证请求");

        try {
            Integer userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException("用户未登录");
            }

            SubmitAuthResponse response = authService.verifyByStudentEmail(request, userId);
            log.info("学生邮箱认证成功：userId={}", userId);

            return ResponseEntity.ok(CommonResponse.ok(response));
        } catch (Exception e) {
            log.error("学生邮箱认证失败：error={}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 查询认证状态接口
     * @return 认证状态信息
     */
    @GetMapping("/status")
    @ApiOperation(value = "查询认证状态", notes = "查询当前用户的身份认证状态及材料信息")
    @NeedLogin
    public ResponseEntity<CommonResponse<AuthStatusResponse>> getAuthStatus() {
        
        log.info("收到查询认证状态请求");
        
        try {
            // 从上下文中获取当前登录用户 ID
            Integer userId = UserContextUtil.getCurrentUserId();
            if (userId == null) {
                throw new BusinessException("用户未登录");
            }
            
            AuthStatusResponse response = authService.getAuthStatus(userId);
            log.info("认证状态查询成功：userId={}, authStatus={}", userId, response.getAuthStatus());
            
            return ResponseEntity.ok(CommonResponse.ok(response));
        } catch (Exception e) {
            log.error("认证状态查询失败：error={}", e.getMessage(), e);
            throw e;
        }
    }
    
    // 其他认证相关 API
}
