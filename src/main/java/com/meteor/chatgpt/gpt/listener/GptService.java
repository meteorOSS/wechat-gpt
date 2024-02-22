package com.meteor.chatgpt.gpt.listener;

import com.meteor.chatgpt.Claptrap;
import dev.ai4j.openai4j.OpenAiClient;
import dev.ai4j.openai4j.chat.ChatCompletionModel;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.Duration.ofSeconds;

public class GptService {

    private String KEY;
    private Claptrap plugin;


    public void setKEY(String KEY) {
        this.KEY = KEY;
    }

    public void setBASE_URL(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    private String KEY_40;
    private String BASE_URL;

    /**
     * 每个微信用户单独开一个会话
     */
    private static Map<String,OpenAiClient> openAiClientHashMap = new ConcurrentHashMap<>();

    public static Map<String, ChatCompletionModel> enable40Map = new ConcurrentHashMap<>();

    private OpenAiClient buildClient(String userName){

        return OpenAiClient.builder()
                .baseUrl(BASE_URL)
                .openAiApiKey(KEY)
                .callTimeout(Duration.ofSeconds(60))
                .connectTimeout(ofSeconds(60))
                .readTimeout(ofSeconds(60))
                .organizationId(UUID.randomUUID().toString())
                .writeTimeout(ofSeconds(60)).build();

    }

    public OpenAiClient getClient(String userName){
        openAiClientHashMap.putIfAbsent(userName,buildClient(userName));
        return openAiClientHashMap.get(userName);
    }


    public static GptService INSTANCE;

    public void clear(String userName){
        openAiClientHashMap.put(userName,buildClient(userName));
    }

    public static void init(Claptrap plugin){
        INSTANCE = new GptService(plugin);
        (new GptListener(plugin)).register();
        plugin.getCommand("gpt").setCommandExecutor(new GptCommandExecutor());
    }

    private GptService(Claptrap plugin){
        this.plugin = plugin;
        this.BASE_URL = plugin.getConfig().getString("baseUrl");
        this.KEY = plugin.getConfig().getString("key");
        this.KEY_40 = plugin.getConfig().getString("key4");
    }

}
