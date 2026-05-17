package club.boyuan.official.teammatching.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.io.Serial;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author dhy
 * @since 2026-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
@ApiModel(value="User对象", description="用户表")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID，主键")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    @ApiModelProperty(value = "学号（支持字母+数字）")
    private String studentId;

    @ApiModelProperty(value = "用户姓名，真实姓名")
    private String username;

    @ApiModelProperty(value = "用户昵称，可重复")
    private String nickname;

    @ApiModelProperty(value = "微信OpenID（用于一键登录）")
    private String openid;

    @ApiModelProperty(value = "微信UnionID（多平台关联）")
    private String unionid;

    @ApiModelProperty(value = "微信原始昵称")
    private String wechatNickname;

    @ApiModelProperty(value = "手机号，加密存储")
    private String phone;

    @ApiModelProperty(value = "邮箱地址")
    private String email;

    @ApiModelProperty(value = "联系方式（邀请场景展示，如微信号）")
    private String contactInfo;

    @ApiModelProperty(value = "密码，加密存储（BCrypt）")
    private String password;

    @ApiModelProperty(value = "头像文件ID，关联file_resource表")
    private Long avatarFileId;

    @ApiModelProperty(value = "性别：0-未知 1-男 2-女")
    private Integer gender;

    @ApiModelProperty(value = "出生日期")
    private LocalDate birthday;

    @ApiModelProperty(value = "学校名称")
    private String school;

    @ApiModelProperty(value = "专业")
    private String major;

    @ApiModelProperty(value = "年级（如：2021级）")
    private String grade;

    @ApiModelProperty(value = "技术栈")
    private String techStack;

    @ApiModelProperty(value = "个人简介")
    private String personalIntro;

    @ApiModelProperty(value = "获奖经历")
    private String awardExperience;

    @ApiModelProperty(value = "角色：admin/student")
    private String role;

    @ApiModelProperty(value = "认证状态：0-待审核 1-已通过 2-已驳回")
    private Integer authStatus;

    @ApiModelProperty(value = "审核时间")
    private LocalDateTime auditTime;

    @ApiModelProperty(value = "审核人ID，关联user表")
    private Integer auditorUserId;

    @ApiModelProperty(value = "审核备注/驳回原因")
    private String remark;

    @ApiModelProperty(value = "人才卡片是否可见（快捷开关）")
    private Boolean isTalentVisible;

    @ApiModelProperty(value = "当前使用的人才卡片ID")
    private Integer talentCardId;

    @ApiModelProperty(value = "新消息通知")
    private Boolean messageNotify;

    @ApiModelProperty(value = "项目状态更新通知")
    private Boolean projectUpdateNotify;

    @ApiModelProperty(value = "组队邀请通知")
    private Boolean invitationNotify;

    @ApiModelProperty(value = "系统通知")
    private Boolean systemNotify;

    @ApiModelProperty(value = "是否冻结：TRUE-冻结 FALSE-正常")
    private Boolean status;

    @ApiModelProperty(value = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty(value = "登录次数")
    private Integer loginCount;

    @ApiModelProperty(value = "注册时间")
    private LocalDateTime createdTime;

    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;


}
