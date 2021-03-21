package com.nanugi.api.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public JsonNode sendVerificationEmail(String to, String code) throws UnirestException {

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
                                URL+code+"\">"
                                +"        인증 완료\n" +
                                "    </a>\n" +
                                "  </div>\n" +
                                "</div>"
                )
                .asJson();

        return request.getBody();
    }

    @Async
    public JsonNode sendCertifyEmail(String to, String code) throws UnirestException {

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", "Nanugi Team <no-reply@nanugi.ml>")
                .field("to", to)
                .field("subject", "[나누기] 비밀번호 찾기 인증 코드 입니다.")
                .field("html",
                    "<div style=\"background-color:#fff; margin:0; padding:0; font-size:14px;\">\n" +
                            "  <div style=\"width: 480px; margin: 0 auto; padding: 24px 12px 12px; border: none; background: #ffffff; text-align: center;\">\n" +
                            "    <div style=\"max-width: 480px; margin-bottom: 30px; padding: 10px; background-color: #f7c46a; text-align: center;\">\n" +
                            "      <div style=\"width: 100%; color: #fff; font-weight: bold; font-size: 25px;\">비밀번호 변경</div>\n" +
                            "    </div>\n" +
                            "    <p style=\"margin-bottom: 30px; font-size: 15px; line-height: 1.6;\">\n" +
                            "      해당 회원의 비밀번호를 변경 시도를 했습니다. <br />비밀번호를 변경하기 위한 인증코드 입니다.<br /><br />비밀번호 변경을 완료하려면 아래 <strong>인증 코드</strong>를 입력하세요.\n" +
                            "    </p>\n" +
                            "\n" +
                            "    <div style=\"width: 480px; background-color: #B3A6B3; height: 1px; margin-bottom: 30px;\"></div>\n" +
                            "\n" +
                            "    <a style=\"padding: 10px 30px; margin: 0px auto; background-color: #F2BB63; font-size: 20px; font-weight: bold;\n" +
                            "    color: #fff; border-radius: 10px; margin-bottom: 20px; text-decoration: none;\">\n" +
                            code + "\n" +
                            "    </a>\n" +
                            "  </div>\n" +
                            "</div>"
                )
                .asJson();

        return request.getBody();
    }

    @Async
    public JsonNode sendCsEmail(String id, String email, String phone_number, String content) throws UnirestException {

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", "나누기 고객센터 <no-reply@nanugi.ml>")
                .field("to", "division.foreveryoung@gmail.com")
                .field("subject", "[나누기 새로운 고객문의] 새로운 고객문의 알림입니다.")
                .field("text",
                        "보낸이의 나누기 아이디 : " + id + "\n" +
                        "답변 받을 연락처 : " + email + " / " + phone_number + "\n"
                        + "문의 내용 : [" + content + "]\n"
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
