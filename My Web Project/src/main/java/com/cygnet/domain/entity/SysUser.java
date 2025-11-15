package com.cygnet.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.cygnet.domain.enums.GenderEnum;
import com.cygnet.domain.enums.RegisterTypeEnum;
import com.cygnet.domain.enums.RoleEnum;
import com.cygnet.domain.enums.UserStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false) //自动生成equal hashCode
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    //昵称
    private String nickname;

    private String email;

    private GenderEnum sex;

    //用户简介
    private String introduction;

    //头像
    private String avatar;

    private UserStatusEnum status;

    private RegisterTypeEnum registerType;

    private String registerIp;

    private String registerAddress;

    private RegisterTypeEnum loginType;

    private String loginIp;

    private String loginAddress;

    private LocalDateTime loginTime;

    private RoleEnum role;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;

}
