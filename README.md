# 🏥 医学信息管理系统 (Hospital Information System)

一个基于 **Spring Boot 3 + Vue 3** 的前后端分离医院信息系统，涵盖病人管理、电子病历、药品目录、医生排班、检查项目管理、数据备份等核心医院业务模块。

---

## 📋 项目简介

本系统是一个功能完整的医院信息管理系统（HIS），旨在为中小型医疗机构提供数字化管理解决方案。系统支持病人信息登记、电子病历编写与模板管理、药品与检查项目维护、医生排班调度、患者多次就医记录追踪以及数据备份导出等功能。

---

## 🛠️ 技术栈

### 后端 (Backend)
| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.5.5 | 主框架 |
| Spring Boot Starter Web | - | RESTful API |
| MyBatis-Plus | 3.5.12 | ORM 框架 |
| MySQL Connector-J | - | 数据库驱动 |
| Apache POI | 5.2.4 | Excel 导入/导出 |
| SpringDoc OpenAPI | 2.2.0 | API 文档 (Swagger UI) |
| Lombok | - | 简化代码 |
| FreeMarker | - | 代码生成模板引擎 |

### 前端 (Frontend)
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.5.18 | 前端框架 (Composition API) |
| Vite | 7.1.2 | 构建工具 |
| Element Plus | 2.11.4 | UI 组件库 |
| Vue Router | 4.5.1 | 路由管理 |
| Axios | 1.12.2 | HTTP 客户端 |
| @element-plus/icons-vue | 2.3.2 | 图标库 |

### 数据库
- **MySQL** — 关系型数据库，数据库名 `keshe`

---

## 📦 功能模块

### 1. 🔐 用户认证
- 用户注册 / 登录
- 基于 localStorage 的会话管理
- 路由守卫（未登录自动跳转登录页）

### 2. 👤 病人信息管理
- 病人信息的增删改查
- 自动生成住院号（格式：`YYYYMMDD` + 3 位序号）
- 按姓名模糊搜索
- 科室与医生下拉关联

### 3. 📝 电子病历管理 (EMR)
- 病历的创建、编辑、查询
- 丰富的病历字段：主诉、现病史、既往史、婚育史、月经史、家族史、体格检查、专科检查、辅助检查、鉴别诊断、初步诊断、诊疗计划
- 关联患者与医生信息

### 4. 📄 病历模板管理
- 模板的增删改查
- **Excel 批量导入/导出**（Apache POI 实现，带专业格式化样式）
- 支持多选批量操作

### 5. 💊 药品信息管理
- 药品目录维护（2900+ 药品预置数据）
- 模糊搜索、分页查询
- 逻辑删除（软删除）
- 药品编码、名称、规格、价格、厂家等完整信息

### 6. 🔬 检查项目管理
- 化验/检查项目的增删改查
- 按科室筛选
- 拼音助记码支持快速检索

### 7. 📅 医生排班管理
- 排班日历视图
- 按科室 / 医生筛选
- 支持上午、下午、全天三种班次
- 过去日期自动禁用

### 8. 🔄 多次就医记录
- 按患者住院号查询所有就诊记录
- 就诊类型：门诊 / 住院 / 复诊
- 就诊状态：已完成 / 治疗中 / 已出院
- 关联医生与电子病历

### 9. 💾 数据备份
- 全量备份 / 选择性模块备份 / 按时间范围备份
- 导出为 JSON 文件下载
- 本地备份历史记录管理

### 10. 📊 报表输出（规划中）
- 病人统计报表
- 诊疗情况报表
- 工作量统计报表

---

## 🏗️ 项目结构

```
D:\课设1
├── idea/                                # 后端 Spring Boot 项目
│   ├── pom.xml                          # Maven 依赖配置
│   └── src/main/
│       ├── java/com/example/keshe/
│       │   ├── KesheApplication.java    # Spring Boot 启动类
│       │   ├── config/
│       │   │   └── CorsConfig.java      # CORS 跨域配置
│       │   ├── controller/              # 控制器层 (10个)
│       │   │   ├── TUserController.java         # 用户登录/注册
│       │   │   ├── PatientinfoController.java   # 病人管理
│       │   │   ├── MedrecordinfoController.java # 电子病历
│       │   │   ├── TemplateinfoController.java  # 病历模板
│       │   │   ├── DrugsController.java         # 药品管理
│       │   │   ├── FmeditemController.java      # 检查项目
│       │   │   ├── DoctorScheduleController.java # 医生排班
│       │   │   ├── VisitRecordController.java   # 多次就医记录
│       │   │   ├── DepartinfoController.java    # 科室信息
│       │   │   └── DoctorinfoController.java    # 医生信息
│       │   ├── dto/                     # 数据传输对象
│       │   │   ├── Result.java          # 统一响应包装
│       │   │   ├── LoginDTO.java        # 登录请求体
│       │   │   └── RegisterDTO.java     # 注册请求体
│       │   ├── entity/                  # 实体类 (10个)
│       │   ├── mapper/                  # MyBatis-Plus Mapper 接口
│       │   ├── service/                 # 服务接口与实现
│       │   └── tool/
│       │       └── CodeGenerator.java   # MyBatis-Plus 代码生成器
│       └── resources/
│           ├── application.yml          # 应用配置
│           ├── application.properties   # 端口配置
│           └── mapper/                  # MyBatis XML 映射文件
├── vue/                                 # 前端 Vue 3 项目
│   ├── package.json                     # Node 依赖配置
│   ├── vite.config.js                   # Vite 构建配置
│   └── src/
│       ├── main.js                      # Vue 应用入口
│       ├── App.vue                      # 根组件（布局切换）
│       ├── router.js                    # 路由配置与守卫
│       ├── style.css                    # 全局样式
│       ├── views/
│       │   └── Login.vue               # 登录/注册页面
│       ├── components/
│       │   ├── Header.vue              # 顶栏（含实时时钟）
│       │   ├── Aside.vue               # 侧边栏导航
│       │   ├── Patient.vue             # 病人管理页面
│       │   ├── MedicalRecord.vue       # 电子病历编辑器
│       │   ├── Template.vue            # 病历模板管理
│       │   ├── Drugs.vue               # 药品管理页面
│       │   ├── Inspection.vue          # 检查项目管理
│       │   ├── DoctorSchedule.vue      # 医生排班管理
│       │   ├── MultipleVisit.vue       # 多次就医记录
│       │   ├── Backup.vue              # 数据备份页面
│       │   └── Report.vue              # 报表输出（规划中）
│       └── assets/                     # 静态资源（背景图片等）
├── 数据库备份文件.sql                    # MySQL 完整数据库备份
└── visit_record表建表语句.sql             # 多次就医记录表 DDL
```

---

## 🚀 快速开始

### 环境要求

- **JDK 17** 或更高版本
- **Maven 3.6** 或更高版本
- **Node.js 18** 或更高版本
- **MySQL 8.0** 或更高版本
- （推荐）**IntelliJ IDEA** + **VS Code**

### 1. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE IF NOT EXISTS keshe DEFAULT CHARACTER SET utf8mb4;

# 导入数据库备份
mysql -u root -p keshe < 数据库备份文件.sql

# （可选）如果使用多次就医记录功能，还需导入：
mysql -u root -p keshe < visit_record表建表语句.sql
```

> ⚠️ 默认数据库用户 `root`，密码 `123456`。如不一致，请修改 `idea/src/main/resources/application.yml` 中的配置。

### 2. 启动后端

```bash
cd idea

# Maven 安装依赖并启动
mvn spring-boot:run

# 或者使用 Maven Wrapper
./mvnw spring-boot:run
```

后端启动后访问地址：`http://localhost:8080`

API 文档地址（Swagger UI）：`http://localhost:8080/swagger-ui.html`

### 3. 启动前端

```bash
cd vue

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后访问地址：`http://localhost:5173`

### 4. 访问系统

1. 浏览器打开 `http://localhost:5173`
2. 注册新账号或使用已有账号登录
3. 开始使用各功能模块

---

## 📡 API 接口概览

| 模块 | 基础路径 | 主要功能 |
|------|----------|----------|
| 用户认证 | `/tUser` | 登录、注册 |
| 病人管理 | `/patientinfo` | CRUD、按姓名搜索 |
| 电子病历 | `/medrecordinfo` | 保存、更新、模板化 |
| 病历模板 | `/templateinfo` | CRUD、Excel 导入导出 |
| 药品管理 | `/drugs` | 分页、搜索、逻辑删除 |
| 检查项目 | `/api/fmeditem` | CRUD、按科室查询 |
| 医生排班 | `/doctorSchedule` | 按科室/日期筛选 |
| 多次就医 | `/visitRecord` | 按患者查询、CRUD |
| 科室信息 | `/departinfo` | 列表查询 |
| 医生信息 | `/doctorinfo` | 列表查询 |

---

## 📊 数据库表

| 表名 | 说明 | 数据量 |
|------|------|--------|
| `t_user` | 用户账户 | - |
| `patientinfo` | 患者信息 | - |
| `departinfo` | 科室信息 | 16 个科室 |
| `doctorinfo` | 医生信息 | 11 位医生 |
| `doctor_schedule` | 医生排班 | - |
| `drugs` | 药品目录 | 2900+ 种 |
| `fmeditem` | 检查项目 | - |
| `medrecordinfo` | 电子病历 | - |
| `templateinfo` | 病历模板 | - |
| `visit_record` | 多次就医记录 | 需手动建表 |

---

## 🎯 架构设计

```
┌─────────────────────────────────────────────────┐
│                    前端 (Vue 3)                   │
│   Element Plus UI  │  Vue Router  │  Axios       │
└──────────────────────┬──────────────────────────┘
                       │ HTTP / JSON
                       │
┌──────────────────────▼──────────────────────────┐
│                后端 (Spring Boot 3)               │
│   Controller → Service → Mapper (MyBatis-Plus)   │
└──────────────────────┬──────────────────────────┘
                       │ JDBC
                       │
┌──────────────────────▼──────────────────────────┐
│                  MySQL 数据库                      │
│               Database: keshe                     │
└─────────────────────────────────────────────────┘
```

- **分层架构**：Controller（接收请求）→ Service（业务逻辑）→ Mapper（数据访问）
- **统一响应**：使用 `Result<T>` 包装所有 API 返回，格式为 `{code, msg, data}`
- **前后端分离**：前端独立部署（端口 5173），后端独立部署（端口 8080），通过 CORS 通信

---

## ⚠️ 注意事项

1. **密码安全**：当前用户密码采用明文存储，生产环境建议使用 BCrypt 等加密方案。
2. **多次就医记录**：`visit_record` 表需单独执行建表 SQL（已提供 `visit_record表建表语句.sql`）。
3. **数据库配置**：默认连接 `localhost:3306/keshe`，用户名 `root`，密码 `123456`，请根据实际环境修改。
4. **报表模块**：`Report.vue` 为占位页面，功能尚未实现。
5. **CORS 配置**：后端仅允许 `http://localhost:5173` 跨域访问，部署时需调整。

---

## 📝 开发说明

- 实体类使用 Lombok `@Data` 注解，IDE 需安装 Lombok 插件
- MyBatis-Plus 代码生成器位于 `tool/CodeGenerator.java`，可根据数据库表自动生成样板代码
- 前端使用 `<script setup>` 语法（Vue 3 Composition API）
- 药品和检查项目支持逻辑删除（`del_mark` 字段标记）

---

## 📄 License

本项目为课程设计项目，仅供学习参考。

---

*最后更新：2025年9月*
