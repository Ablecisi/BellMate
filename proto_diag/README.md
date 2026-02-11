# BellMate 高保真原型使用说明

## 运行方式
1. 直接用浏览器打开 `proto_diag/index.html`。
2. 原型基于 `HTML/CSS/JS + iframe`，无需本地服务。
3. 建议使用 Chromium 内核浏览器（Web Speech API 兼容更好）。

## 核心操作路径
1. 登录：进入登录页，选择账号密码/手机号/微信/QQ。
2. 新增计划：进入“计划”页，点击 FAB 添加安排。
3. 新增闹钟：进入“闹钟”页，点击右上角新增。
4. AI叫醒：编辑闹钟开启 AI，可选语音风格和“关联安排”。
5. 一键关闭今日闹钟：闹钟页红色 FAB -> 确认 -> Snackbar 可撤销。
6. 数据同步：进入“我的”页同步上传或拉取。
7. 设置：进入“设置”页调整通知、语言、深色模式、账号切换、缓存、协议等。

## 账号与设备规则
- 单账号最多允许 2 台设备同时在线。
- 第 3 台设备登录时会被拦截并提示先在其他设备退出。
- 已登录且未过期账号会进入账号池，可在“设置 -> 切换账号”快速切换。

## 与真实 Android 开发对应关系
- `index.html` 对应宿主 Activity + 底部导航 + 内容容器。
- 各 `iframe` 页面对应独立页面模块（登录、闹钟、计划、我的、设置）。
- `localStorage` 模拟本地数据库与云端缓存。
- `postMessage` 模拟页面/模块间通信。
- Web Speech API 用于模拟 AI 语音播报。

## 文件结构
- `proto_diag/index.html`: App 宿主、导航、全局通知、iframe 路由。
- `proto_diag/login.html`: 登录、注册、忘记密码、授权登录。
- `proto_diag/alarm.html`: 闹钟管理、AI叫醒、一键关闭今日闹钟。
- `proto_diag/plan.html`: 计划管理、拖拽排序、完成状态。
- `proto_diag/my.html`: 数据同步与日志。
- `proto_diag/settings.html`: 通知/语言/主题/账号/安全/协议/资料设置。
- `proto_diag/common.js`: 公共状态、鉴权会话、账号池、设备上限逻辑。
- `proto_diag/styles.css`: MD3 风格组件样式与主题变量。
- `proto_diag/RESOURCES.md`: 资源来源与用途。
