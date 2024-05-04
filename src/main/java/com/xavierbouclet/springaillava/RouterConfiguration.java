package com.xavierbouclet.springaillava;

import org.apache.catalina.core.ApplicationPart;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatClient;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.springframework.web.servlet.function.RouterFunctions.route;

@Configuration(proxyBeanMethods = false)
public class RouterConfiguration {

    @Bean
    public RouterFunction<ServerResponse> aiRouter(OllamaChatClient ollamaChatClient
    ) {
        return route()
                .POST("/api/ollama/llava", request -> {
                    String message = request.param("message").orElse("What do you see in this image?");
                    byte[] imageData = request.multipartData().getFirst("image")
                            .getInputStream().readAllBytes();

//                    Path path = Path.of("/Users/xavierbouclet/Downloads/nom_du_fichier.png");
//                    Files.write(path, imageData);

                    UserMessage userMessage = new UserMessage(message, List.of(new Media(MimeTypeUtils.IMAGE_PNG, imageData)));
                    var response=ollamaChatClient.call(new Prompt(List.of(userMessage)));
                    return ServerResponse.ok().body(response.getResult().getOutput().getContent());
                })
                .build();
    }
}