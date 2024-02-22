package com.meteor.chatgpt;

import com.meteor.chatgpt.gpt.listener.GptService;
import com.meteor.wechatbc.impl.plugin.BasePlugin;

public class Claptrap extends BasePlugin {
    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        GptService.init(this);
    }

}
