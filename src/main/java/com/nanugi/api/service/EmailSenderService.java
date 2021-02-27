package com.nanugi.api.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    @Value("${mailgun.domain}")
    private String YOUR_DOMAIN_NAME;

    @Value("${mailgun.apikey}")
    private String API_KEY;

    @Value("${mailgun.url.base}")
    private String BASE_URL;

    public JsonNode sendSimpleMessage(String to, String code) throws UnirestException {

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", "Nanugi Team <no-reply@nanugi.ml>")
                .field("to", to)
                .field("subject", "[나누기] 이메일 인증이 필요합니다")
                .field("text",
                        "[나누기 회원가입 안내]\n" +
                                "\n" +
                                "\n" +
                                "안전한 회원 가입을 위한 안내 메일입니다.\n" +
                                "이메일 인증을 위하여 아래 링크를 클릭해주세요.\n" +
                                BASE_URL + "/v1/email-verification?code=" +
                                code)
                .asJson();

        return request.getBody();
    }
}
