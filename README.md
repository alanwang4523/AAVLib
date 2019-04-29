## AAVLib (Alan Audio & Video Library)


## modules
**modules 目录下是各个功能模块的集合，可单独对 modules 封装一个 APP, 其依赖于 libraries 下面的 AAVCore **
### ALVideoEditor:
视频编辑模块:
- 相机预览、录制
- 视频播放预览
- 视频编辑
- 添加视频滤镜、特效
- 视频效果保存


### audio 目录:
音频库，主要包含以下内容：
- AudioRecorder: 包括 java 层的 Recorder 和底层的 OpenSL 两种实现；
- AudioPlayer: 包括 AudioTrack 和 OpenSL 两种实现；
---

### image 目录：

#### filters:
视频效果库，主要包含：
- **视频滤镜：**
    - LUT 类滤镜；
    - ToneCurve 滤镜；
    - 贴纸、水印；
---


### media 目录:
音视频核心库，包含以下内容：
- 音频编解码；
- 视频编解码；
- 音频裁剪；
- 视频裁剪；
---


### opengl 目录:
EGL 核心库，主要包含：
- EGL 常用工具类；
- EGL 环境搭建；
- 常用的 Render;
---


### utils 目录:
Android 常用工具库：

---

### video 目录:
视频库，主要包含以下内容：
- VideoPlayer：视频播放器；
- Camera：相机相关；
- 视频相关的控制器；
---


### 参考项目：
[grafika](https://github.com/google/grafika)


### 个人联系方式：
email: alanwang4523@gmail.com

### LICENSE：
```
Copyright (c) 2019-present AlanWang4523 <alanwang4523@gmail.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```