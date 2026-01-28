# music-website-v1/README.md

# 🎵 音乐网站后端项目 v1.0

Music Website Backend - v1.0 是一个音乐网站的后端最小可用版本，聚焦核心音乐播放相关接口开发，采用前后端分离架构，提供音乐上传、列表查询、音频播放、切歌等基础功能，为前端提供稳定、规范的API服务。

## 📋 项目概述

### 核心目标

实现音乐资源的后端管理与播放支撑，搭建标准化分层架构，为后续版本（用户管理、收藏、歌单等）迭代奠定基础，同时遵循企业后端开发规范，保证代码可维护性与扩展性。

### 架构设计

采用 **前后端分离** 模式，后端基于Spring Boot分层架构开发，职责划分清晰：

Controller（接口层）→ Service（业务逻辑层）→ Mapper（数据访问层）→ Entity（实体层）

前端可独立开发，通过调用后端API实现交互，后端提供统一响应格式与跨域支持。

## 🔧 技术栈

|类别|技术选型|版本要求|
|---|---|---|
|核心框架|Spring Boot|4.0.2|
|编程语言|Java|JDK 17+|
|数据库|MySQL|8.0+|
|ORM框架|MyBatis-Plus|3.5.5|
|开发工具|Lombok|最新稳定版|
|构建工具|Maven|3.6+|
## ⚙️ 环境准备

### 1. 基础环境安装

1. 安装 JDK 17+，配置环境变量 `JAVA_HOME`；

2. 安装 MySQL 8.0+，启动MySQL服务，记录用户名与密码；

3. 安装 Maven 3.6+，配置镜像源（推荐阿里云镜像）以提升依赖下载速度；

4. 推荐IDE：IntelliJ IDEA，安装 Lombok 插件（Settings → Plugins → 搜索Lombok）。

### 2. 数据库初始化

1. 打开MySQL客户端（命令行/Navicat/IDEA Database），创建数据库：
   `CREATE DATABASE IF NOT EXISTS music_website DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;`

2. 切换至目标数据库，执行建表SQL（位于项目 `docs/sql/` 目录，或直接复制下方SQL）：
```sql
USE music_website;
CREATE TABLE IF NOT EXISTS `music` (    
`id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '音乐主键ID（自增）',   
`title` VARCHAR(50) NOT NULL COMMENT '歌曲名称',    
`artist` VARCHAR(30) NOT NULL COMMENT '歌手名称',   
`tags` VARCHAR(100) DEFAULT '' COMMENT '歌曲标签（逗号分隔，如：流行,华语）',    
`file_path` VARCHAR(255) NOT NULL COMMENT '音乐文件存储完整路径', 
`upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间（默认当前时间）',
PRIMARY KEY (`id`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐信息表';
```

## 🚀 项目部署与运行

### 1. 项目克隆与配置

1. 克隆项目至本地（或直接导入IDE）；

2. 打开项目根目录下的 `src/main/resources/application.properties`，修改数据库配置与文件存储路径：
   ```
   ///数据库配置（替换为你的MySQL信息）
   spring.datasource.url=jdbc:mysql://localhost:3306/music_website?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
   spring.datasource.username=root  # 你的MySQL用户名
   spring.datasource.password=123456  # 你的MySQL密码
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ///音乐文件存储路径（确保目录存在，不存在则手动创建）
   music.upload.path=src/main/resources/static/upload/music/
   ///其他基础配置（无需修改）
   server.port=8080    
   server.servlet.encoding.force=true  
   server.servlet.encoding.charset=utf-8   
   spring.servlet.multipart.max-file-size=50MB 
   spring.servlet.multipart.max-request-size=50MB  
   mybatis-plus.configuration.map-underscore-to-camel-case=true  
   ```


### 2. 启动项目

1. IDE启动：打开项目，找到启动类 `com.example.musicweb.MusicWebApplication.java`，    
右键选择 `Run 'MusicWebApplication'`；

2. Maven命令启动：进入项目根目录，执行命令 `mvn spring-boot:run`；

3. 启动验证：控制台输出 `Started MusicWebApplication in XXX seconds` 即为启动成功，服务运行在 `http://localhost:8080`。

## 📌 核心功能

v1.0 聚焦音乐播放核心场景，实现以下功能：

- **音乐上传**：支持MP3格式文件上传，自动生成唯一文件名，存储至本地指定目录并入库；

- **音乐列表查询**：查询所有已上传音乐的基础信息（ID、标题、歌手、标签等），为前端列表渲染提供数据；

- **音乐播放**：根据音乐ID返回音频文件流，支持前端播放器直接解析播放；

- **切歌功能**：支持上一首、下一首、随机切歌，自动计算目标音乐并返回信息。

## 🔌 核心API接口说明

所有接口统一前缀`/api/music`，返回格式统一为 JSON（播放接口除外，返回音频流），响应格式如下：

```json

{
  "code": 200,
  "msg": "操作成功",
  "data": {} 
}
```
```json
{
  "code": 400/404/500,
  "msg": "错误提示信息",
  "data": null
}
```

### 接口详情

|接口路径|请求方式|功能描述|请求参数|返回内容|
|---|---|---|---|---|
|/api/music/upload|POST|上传MP3音乐|FormData格式：file（MP3文件）、title（歌曲名）、artist（歌手名）、tags（标签，可选）|上传成功的音乐完整信息（含ID、路径等）|
|/api/music/list|GET|查询所有音乐列表|无|音乐列表集合（含每首音乐基础信息）|
|/api/music/play/{id}|GET|播放指定ID音乐|路径参数：id（音乐ID）|MP3音频文件流（前端可直接播放）|
|/api/music/switch|GET|切歌（上一首/下一首/随机）|查询参数：currentId（当前音乐ID）、type（切歌类型：prev/next/random）|切歌后的音乐完整信息|
## ⚠️ 注意事项

- 文件存储路径：确保 `music.upload.path` 配置的目录存在，且项目运行时有读写权限，否则会导致音乐上传失败；

- 跨域支持：项目已配置跨域拦截器，允许前端 `http://localhost:8080` 域名请求，若前端端口变更，需修改 `config/CorsConfig.java` 中的 `allowedOrigins`；

- 接口测试：推荐使用 Postman 测试接口，上传接口需选择 `form-data` 格式；

- 异常处理：接口已覆盖常见异常场景（文件格式错误、资源不存在、参数缺失等），可根据控制台日志定位问题。

## 🔄 后续迭代计划

v1.0 为基础版本，后续将逐步迭代以下功能：

- v2.0：引入用户管理模块（注册、登录、个人信息管理），基于JWT实现无状态认证；

- v3.0：实现用户与音乐交互（收藏、点赞、创建歌单）；

- v4.0：优化文件存储（对接OSS云存储，替代本地存储），增加音乐分类、搜索功能。

## 📞 联系方式
2123668062@qq.com

若遇到部署或接口调用问题，可联系开发者排查，后续将持续优化项目功能与文档。

版本：v1.0.0 | 更新时间：2026-01-28