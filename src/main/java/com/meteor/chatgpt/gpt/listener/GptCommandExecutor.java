package com.meteor.chatgpt.gpt.listener;

import com.meteor.wechatbc.command.CommandExecutor;
import com.meteor.wechatbc.command.sender.CommandSender;
import com.meteor.wechatbc.command.sender.ConsoleSender;
import com.meteor.wechatbc.command.sender.ContactSender;
import com.meteor.wechatbc.entitiy.contact.Contact;
import dev.ai4j.openai4j.chat.ChatCompletionModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GptCommandExecutor implements CommandExecutor {

    private List<String> whiteList = new ArrayList<>();

    public GptCommandExecutor(){
        whiteList.add("zzzsh");
    }

    @Override
    public void onCommand(CommandSender commandSender, String[] strings) {

        if(strings.length<=0){
            commandSender.sendMessage("gpt模块帮助: \n /gpt clear 清除上下文信息 \n /gpt model [model] 指定模型 \n /gpt key [key] 重置令牌 \n /gpt baseURL [url] 重置接口地址");
            return;
        }

        boolean isPass = commandSender instanceof ConsoleSender;

        String opt = strings[0];

        List<String> permissionCmd = Arrays.asList("model","add","baseURL","key");

        if(commandSender instanceof ContactSender){
            ContactSender contactSender = (ContactSender) commandSender;
            Contact contact = contactSender.getContact();
            if(permissionCmd.contains(opt))isPass = whiteList.contains(contact.getNickName());
            else isPass = true;
        }

        if(!isPass){
            commandSender.sendMessage("[wechat-bc] 没有足够的权限");
            return;
        }

        if("clear".equalsIgnoreCase(opt)){

            if(commandSender instanceof ConsoleSender){
                return;
            }

            GptService.INSTANCE.clear(((ContactSender)commandSender).getContact().getUserName());

            commandSender.sendMessage("[wechat-bc] 已清除对话");

        }else if("add".equalsIgnoreCase(opt)){
            whiteList.add(strings[1]);
            commandSender.sendMessage(String.format("[wechat-bc] 已添加白名单 %s",strings[1]));
        }else if("model".equalsIgnoreCase(opt)){
            String model = strings[1];
            try {
                ChatCompletionModel chatCompletionModel = ChatCompletionModel.valueOf(model);
                GptService.enable40Map.put(((ContactSender)commandSender).getContact().getUserName(),chatCompletionModel);
                commandSender.sendMessage("[wechat-bc] 已切换模型");
            }catch (Exception e){
                commandSender.sendMessage("[wechat-bc] 模型不存在");
            }
        } else if ("baseURL".equalsIgnoreCase(opt)) {
            GptService.INSTANCE.setBASE_URL(strings[1]);
            commandSender.sendMessage("[wechat-bc] 已设置api地址为:"+strings[1]);
        } else if("key".equalsIgnoreCase(opt)){
            GptService.INSTANCE.setBASE_URL(strings[1]);
            commandSender.sendMessage("[wechat-bc] 已设置key为:"+strings[1]);
        }

    }
}
