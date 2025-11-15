
# Spring Security JWT 认证系统

## 项目简介
一个基于 Spring Boot 3 + Spring Security 6 + JWT 的现代化认证授权系统，提供完整的用户管理、权限控制和安全的 API 访问机制。

## 核心特性
- JWT 无状态认证 - 基于 Token 的轻量级认证方案
- RBAC 权限控制 - 基于角色的访问控制
- Redis 缓存支持 - 快速用户会话管理
- 统一异常处理 - 规范的错误响应格式
- 自动 Token 刷新 - 无缝的认证续期体验

## 架构设计哲学

### 核心设计原则
- **无状态优先**：JWT + Redis，支持水平扩展
- **关注点分离**：认证、授权、业务逻辑清晰分离
- **契约驱动**：严格遵循 Spring Security 接口契约
- **枚举驱动**：所有状态使用枚举，避免魔法数字
- **统一异常**：完整的异常处理链路

## 核心流程设计

### 登录认证流程
```
1. 认证入口 → AuthController.login()
2. 安全认证 → Spring Security AuthenticationManager
3. 用户加载 → UserDetailsServiceImpl (支持用户名/邮箱登录)
4. 状态验证 → 检查用户状态、密码匹配
5. Token生成 → JWT + 用户ID
6. 会话缓存 → Redis 存储用户信息
7. 响应返回 → 统一的Result格式
```

### 请求认证流程
```
1. 请求拦截 → JwtAuthenticationTokenFilter
2. Token提取 → 从Authorization头提取Bearer Token
3. 用户验证 → Redis查询 + 状态检查
4. 权限构建 → 基于角色生成Security权限
5. 上下文设置 → SecurityContextHolder
6. 请求放行 → 执行业务逻辑
```

## 系统架构

### 分层架构
```
表现层 (Controller)
    ↓
业务层 (Service) 
    ↓
数据层 (Mapper)
    ↓
安全层 (Security) ← 横切关注点
```

### 安全模块设计
```
SecurityConfig (配置中心)
    ├── JwtAuthenticationTokenFilter (请求认证)
    ├── UserDetailsServiceImpl (用户加载)
    ├── LoginSysUser (用户详情封装)
    └── 异常处理链 (统一错误响应)
```

### 核心类职责
| 类名 | 职责 | 设计亮点 |
|------|------|----------|
| `SecurityConfig` | 安全配置中心 | 无状态会话、异常处理、跨域配置 |
| `JwtAuthenticationTokenFilter` | 请求认证 | Token自动刷新、白名单机制 |
| `UserDetailsServiceImpl` | 用户加载 | 多字段登录、状态验证 |
| `LoginSysUser` | 用户详情 | UserDetails契约实现、权限封装 |
| `GlobalExceptionHandler` | 异常处理 | Spring Security异常统一转换 |

## 安全设计详解

### 认证体系
```java
// 用户详情契约实现
public class LoginSysUser implements UserDetails {
    private SysUser sysUser;  // 原始用户实体
    private List<String> permissions;  // 权限列表
    
    // Spring Security合约方法
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 基于角色生成权限：ROLE_ADMIN, ROLE_USER等
    }
}
```

### 权限模型
```java
// 基于角色的权限控制
public enum RoleEnum {
    USER(0, "普通用户"),    // 基础权限
    VIP(1, "VIP用户"),      // 增值权限  
    ADMIN(2, "管理员");     // 系统管理权限
}

// 方法级权限控制
@PreAuthorize("hasRole('ADMIN')")
public Result adminOperation() { ... }
```

### 无状态会话
```java
// JWT + Redis 实现无状态
public class JwtAuthenticationTokenFilter {
    // Token验证 → Redis用户查询 → 权限构建 → 上下文设置
    // 支持Token自动刷新机制
}
```

## 核心特性

### 1. 多字段登录支持
- 支持用户名、邮箱登录
- 统一的认证入口
- 智能字段识别

### 2. 完整的异常处理
```java
// 覆盖所有安全相关异常
@ExceptionHandler(UsernameNotFoundException.class)    // 用户不存在
@ExceptionHandler(DisabledException.class)           // 用户禁用  
@ExceptionHandler(BadCredentialsException.class)      // 凭证错误
@ExceptionHandler(AccessDeniedException.class)        // 权限不足
```

### 3. 自动Token刷新
- 接近过期时自动刷新
- 响应头返回新Token
- 无缝用户体验

### 4. 生产级安全配置
```java
// 密码加密
@Bean PasswordEncoder → BCryptPasswordEncoder

// 无状态会话
sessionCreationPolicy(SessionCreationPolicy.STATELESS)

// 跨域安全配置
CORS + 凭证控制
```

## 设计亮点

### 1. 契约驱动开发
严格实现 `UserDetails`、`UserDetailsService` 等 Spring Security 核心接口，确保框架兼容性。

### 2. 状态机设计
```java
// 枚举驱动状态管理
public enum UserStatusEnum {
    NORMAL(0, "正常"),
    DISABLED(1, "禁用");
    // 编译期安全，避免魔法数字
}
```

### 3. 关注点分离
- **过滤器**：只处理认证逻辑
- **Service**：处理业务认证
- **Controller**：处理HTTP交互

### 4. 可扩展架构
```java
// 易于添加新权限模型
public class LoginSysUser {
    // 现有：基于角色的权限
    // 未来：可添加基于权限字符串的细粒度控制
}
```

## 快速开始

### 环境要求
- JDK 17+
- Redis 6.0+
- Spring Boot 3.x

### 启动步骤
1. 配置数据库和Redis连接
2. 启动应用

## 适用场景

- 需要完整认证授权的后台管理系统
- 多角色权限控制的业务系统
- 需要无状态扩展的微服务架构
- Spring Security 学习参考项目

## 扩展方向

### 短期优化
- 细粒度权限控制
- 多端登录管理
- 安全审计日志

### 长期演进
- OAuth 2.0 集成
- 多因子认证
- 分布式会话管理
