后端 API URL 清单（前端已对接）
Base URL：http://127.0.0.1:8080（见 bellmate.uts）

POST /api/auth/login/account 账号密码登录

POST /api/auth/login/phone 手机号验证码登录

POST /api/auth/login/social 社交登录

POST /api/auth/register 注册

POST /api/auth/password/reset 重置密码

GET /api/alarms 获取闹钟列表

POST /api/alarms 新增/编辑闹钟

DELETE /api/alarms/{id} 删除单个闹钟

POST /api/alarms/batch-delete 批量删除闹钟

POST /api/alarms/pause-today 一键暂停今日闹钟

POST /api/alarms/restore-today 恢复今日闹钟

GET /api/plans 获取计划列表

POST /api/plans 新增/编辑计划

DELETE /api/plans/{id} 删除计划

POST /api/plans/reorder 计划排序更新

GET /api/user/profile 获取个人资料

PUT /api/user/profile 保存个人资料

GET /api/user/preferences 获取偏好设置

PUT /api/user/preferences 保存偏好设置

GET /api/accounts 账号池列表

POST /api/accounts/switch 切换账号

DELETE /api/accounts/{account} 删除账号

POST /api/sync/upload 同步上传

GET /api/sync/pull 云端拉取