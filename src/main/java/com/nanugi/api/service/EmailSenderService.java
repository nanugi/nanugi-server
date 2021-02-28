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
                .field("html",

                        "<div style=\"background-color:#fff; margin:0; padding:0; font-size:14px;\">\n" +
                                "  <div style=\"width: 480px; margin: 0 auto; padding: 24px 12px 12px; border: none; background: #ffffff; text-align: center;\">\n" +
                                "    <div style=\"max-width: 480px; margin-bottom: 30px; padding: 10px; background-color: #f7c46a; text-align: center;\">\n" +
                                "        <div style=\"width: 100%; color: #fff;\">⚠️ 나누기에 회원가입 하신 적이 없으시면 링크를 클릭하지 마십시오.</div>\n" +
                                "    </div>\n" +
                                "    <p style=\"margin-bottom: 30px; font-size: 15px; line-height: 1.6;\">\n" +
                                "      안녕하세요.<br />나누기에 회원가입을 해주셔서 감사합니다.<br /><br />가입을 완료하려면 아래 <strong>버튼</strong>을 클릭해주세요.\n" +
                                "    </p>\n" +
                                "\n" +
                                "    <div style=\"width: 480px; background-color: #B3A6B3; height: 1px; margin-bottom: 30px;\"></div>\n" +
                                "\n" +
                                "    <a style=\"padding: 10px 30px; margin: 0px auto; background-color: #F2BB63; font-size: 20px; font-weight: bold;\n" +
                                "    color: #fff; border-radius: 10px; cursor: pointer; margin-bottom: 20px; text-decoration: none;\" href=\"" +
                                BASE_URL + "/v1/email-verification?code="+code+"\">"
                                +"        인증 완료\n" +
                                "    </a>\n" +
                                "  </div>\n" +
                                "</div>"
                )
                .asJson();

        return request.getBody();
    }
}
