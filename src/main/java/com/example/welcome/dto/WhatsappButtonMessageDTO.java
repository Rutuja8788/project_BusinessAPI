package com.example.welcome.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WhatsappButtonMessageDTO {
    private String messaging_product;
    private String to;
    private String type;
    private Interactive interactive;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Interactive {
        private String type;
        private Body body;
        private Action action;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Body {
        private String text;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Action {
        private List<Button> buttons;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Button {
        private String type;
        private Reply reply;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reply {
        private String id;
        private String title;
    }
}
