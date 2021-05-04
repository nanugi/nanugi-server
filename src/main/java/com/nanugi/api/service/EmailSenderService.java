package com.nanugi.api.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailSenderService {

    @Value("${mailgun.domain}")
    private String YOUR_DOMAIN_NAME;

    @Value("${mailgun.apikey}")
    private String API_KEY;

    @Value("${mailgun.url.base}")
    private String BASE_URL;

    private String URL = "https://nanugi.github.io/nanugi-web/emailVerification/";

    public JsonNode sendVerificationEmail(String to, String code) throws UnirestException {

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", "Nanugi Team <no-reply@nanugi.ml>")
                .field("to", to)
                .field("subject", "[나누기] 이메일 인증이 필요합니다")
                .field("html",

                        "<div style=\"background-color:#11A656; margin:0; padding:0; border: none; font-size:14px; text-align: center;\">\n" +
                                "  \n" +
                                "  <div style=\"padding: 10px; background-color: #FFFFFF; text-align: center;\">\n" +
                                "    <div style=\"width: 100%; color: #828282; font-size: 15px;\">❌ 나누기에 회원가입 하신 적이 없으시면 링크를 클릭하지 마십시오. ❌</div>\n" +
                                "  </div>\n" +
                                "\n" +
                                "  <div style=\"padding-top: 83px; padding-bottom: 120px; text-align: center;\">\n" +
                                "    <div>\n" +
                                "      <img src=\"https://github.com/nanugi/nanugi-web/blob/master/src/assets/images/wordmark.png?raw=true\">\n" +
                                "    </div>\n" +
                                "\n" +
                                "    <p style=\"margin-bottom: 30px; font-size: 22px; line-height: 1.6; text-align: center; color: #fff;\">\n" +
                                "      안녕하세요\uD83D\uDE00<br />\n" +
                                "      나누기에 회원가입을 해주셔서 감사합니다.<br />\n" +
                                "      <br />\n" +
                                "      가입을 완료하려면 <strong style=\"text-decoration: underline;\">아래 버튼</strong>을 클릭해주세요.<br />\n" +
                                "    </p>\n" +
                                "      \n" +
                                "    <a style=\"padding: 8px 32px; margin: 0px auto; background-color: #FFF; font-size: 28px; font-weight: bold;\n" +
                                "    color: #11A656; border-radius: 10px; cursor: pointer; margin-bottom: 20px; text-decoration: none;\" href=\"" + URL + code + "\">\n" +
                                "      인증 완료\n" +
                                "    </a>\n" +
                                "  </div>\n" +
                                "</div>"
                )
                .asJson();

        return request.getBody();
    }

    public JsonNode sendCertifyEmail(String to, String code) throws UnirestException {

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", "Nanugi Team <no-reply@nanugi.ml>")
                .field("to", to)
                .field("subject", "[나누기] 비밀번호 찾기 인증 코드 입니다.")
                .field("html",
                    "<div style=\"background-color:#FFF; margin:0; padding:0; border: none; font-size:14px; text-align: center;\">\n" +
                            "  \n" +
                            "  <div style=\"padding: 14px; background-color: #11A656; text-align: center;\">\n" +
                            "    <div style=\"width: 100%; color: #FFF; font-size: 15px;\">비밀번호 변경</div>\n" +
                            "  </div>\n" +
                            "\n" +
                            "  <div style=\"padding-top: 83px; padding-bottom: 120px; text-align: center;\">\n" +
                            "    <div>\n" +
                            "      <img src=\"https://github.com/nanugi/nanugi-web/blob/master/src/assets/images/wordmark_.png?raw=true\">\n" +
                            "    </div>\n" +
                            "\n" +
                            "    <p style=\"margin-bottom: 30px; font-size: 22px; line-height: 1.6; text-align: center; color: #000;\">\n" +
                            "      해당 회원의 비밀번호 변경 시도를 했습니다.<br />\n" +
                            "      비밀번호를 변경하기 위한 인증코드입니다.<br />\n" +
                            "      <br />\n" +
                            "      비밀번호 변경을 완료하시려면 <strong style=\"text-decoration: underline;\">아래 인증 코드</strong>를 입력하세요.<br />\n" +
                            "    </p>\n" +
                            "      \n" +
                            "    <a style=\"padding: 8px 32px; margin: 0px auto; background-color: #FFF; font-size: 28px; font-weight: bold;\n" +
                            "    color: #11A656; border-radius: 10px; cursor: pointer; margin-bottom: 20px; text-decoration: none;\" >\n" +
                            "       " + code + "\n" +
                            "    </a>\n" +
                            "  </div>\n" +
                            "</div>"
                )
                .asJson();

        return request.getBody();
    }

    public String getSecretCode(){
        Random random = new Random();
        String code = "";

        for(int i=0; i<24; i++){
            code += (char)(random.nextInt(25)+97);
        }
        return code;
    }
}
