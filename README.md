基于 [WeChatBc](https://github.com/meteorOSS/WeChatBc) 实现

## 功能

### 每个用户单独会话，千人千面;模型切换

### 自定义接口地址，支持中转key

![image](https://github.com/meteorOSS/wechat-gpt/assets/61687266/f07a3725-b2a8-450f-aa6c-2fe24ac4b7e0)



### 指令

/gpt model [模型] 切换模型
/gpt clear 清除对话记录 (单个用户)
/gpt baseURL [url] 切换接口地址
/gpt key [key] 重置令牌

### 配置文件

``` yaml
## 接口地址
baseUrl: 'https://api.openai.com/v1/'
## 令牌
key: "不告诉你"
```

## 使用方法
放入wechatbc的plugins文件夹，随后重启服务
