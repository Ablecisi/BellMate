# BellMate Android API URL 清单

Base URL（Android 模拟器）: `http://10.0.2.2:8080/`

## 认证模块
- POST `api/auth/login/account` 账号密码登录
- POST `api/auth/login/phone` 手机号验证码登录
- POST `api/auth/login/social` 社交登录（微信/QQ）
- POST `api/auth/register` 注册
- POST `api/auth/password/reset` 重置密码

## 闹钟模块
- GET `api/alarms` 获取闹钟列表
- POST `api/alarms` 新增或编辑闹钟
- DELETE `api/alarms/{id}` 删除闹钟
- POST `api/alarms/batch-delete` 批量删除闹钟
- POST `api/alarms/pause-today` 一键暂停今日闹钟
- POST `api/alarms/restore-today` 恢复今日闹钟

## 计划模块
- GET `api/plans` 获取计划列表
- POST `api/plans` 新增或编辑计划
- DELETE `api/plans/{id}` 删除计划
- POST `api/plans/reorder` 更新计划排序

## 用户模块
- GET `api/user/profile` 获取个人资料
- PUT `api/user/profile` 保存个人资料
- GET `api/user/preferences` 获取偏好设置
- PUT `api/user/preferences` 保存偏好设置

## 账号池模块
- GET `api/accounts` 获取账号池
- POST `api/accounts/switch` 切换账号
- DELETE `api/accounts/{account}` 删除账号

## 同步模块
- POST `api/sync/upload` 同步上传
- GET `api/sync/pull` 云端拉取
