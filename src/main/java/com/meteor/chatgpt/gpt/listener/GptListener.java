package com.meteor.chatgpt.gpt.listener;


import com.meteor.chatgpt.Claptrap;
import com.meteor.wechatbc.entitiy.message.Message;
import com.meteor.wechatbc.event.EventHandler;
import com.meteor.wechatbc.impl.event.Listener;
import com.meteor.wechatbc.impl.event.sub.ReceiveMessageEvent;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionRequest;
import dev.ai4j.openai4j.image.GenerateImagesRequest;
import dev.ai4j.openai4j.image.GenerateImagesResponse;

import java.util.Optional;

import static dev.ai4j.openai4j.chat.ChatCompletionModel.GPT_3_5_TURBO;

public class GptListener implements Listener {

    private Claptrap plugin;

    public GptListener(Claptrap plugin){
        this.plugin = plugin;
    }

    /**
     * 注册监听器
     */
    public void register(){
        plugin.getWeChatClient().getEventManager().registerPluginListener(plugin,this);
    }

    private String getMsg(String content){
        return (content.startsWith("ai ") ? content.replace("ai ","") : null);
    }

    public enum AnswerType{
        TEXT,IMAGE;
    }

    public static class Request{
        private AnswerType answerType;
        private String prompt;

        public Request(AnswerType answerType, String prompt) {
            this.answerType = answerType;
            this.prompt = prompt;
        }

        public AnswerType getAnswerType() {
            return answerType;
        }

        public void setAnswerType(AnswerType answerType) {
            this.answerType = answerType;
        }

        public String getPrompt() {
            return prompt;
        }

        public void setPrompt(String prompt) {
            this.prompt = prompt;
        }
    }

    public Request getRequest(String mes){

        if(mes.startsWith("ai ")){
            return new Request(AnswerType.TEXT,mes.replace("ai ",""));
        }else if(mes.startsWith("draw ")){
            return new Request(AnswerType.IMAGE,mes.replace("draw ",""));
        }

        return null;

    }

    @EventHandler
    public void onReceiveMessage(ReceiveMessageEvent receiveMessageEvent){
        Message message = receiveMessageEvent.getMessage();
        String content = receiveMessageEvent.getContent();

        String senderUserName = message.getSenderUserName();
        if(senderUserName == null ) return;

        Optional<Request> optionalRequest = Optional.ofNullable(getRequest(content));

        optionalRequest.ifPresent(request -> {
            OpenAiClient client = GptService.INSTANCE.getClient(senderUserName);
            if(request.getAnswerType() == AnswerType.TEXT){

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                        .model(GptService.enable40Map.getOrDefault(senderUserName,GPT_3_5_TURBO))
                        .addUserMessage(request.getPrompt())
                        .build();

                String execute = client.chatCompletion(chatCompletionRequest).execute().content();

                plugin.getWeChatClient().getWeChatCore().getHttpAPI().sendMessage(message.getFromUserName(),execute);
            }else {
                GenerateImagesRequest generateImagesRequest = GenerateImagesRequest.builder()
                        .prompt(request.getPrompt())
                        .build();
                client.imagesGeneration(generateImagesRequest)
                        .onResponse(generateImagesResponse -> {
                            for (GenerateImagesResponse.ImageData datum : generateImagesResponse.data()) {
                                System.out.println(datum.b64Json());
                            }
                        });
            }
        });


    }

}
