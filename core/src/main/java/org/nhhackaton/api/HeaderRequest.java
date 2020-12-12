package org.nhhackaton.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.text.SimpleDateFormat;
import java.util.Locale;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeaderRequest {

    @JsonProperty("ApiNm")
    private String ApiNm;

    @JsonProperty("Tsymd")
    private String Tsymd;

    @JsonProperty("Trtm")
    private String Trtm;

    @JsonProperty("Iscd")
    private String Iscd;

    @JsonProperty("FintechApsno")
    private String FintechApsno;

    @JsonProperty("ApiSvcCd")
    private String ApiSvcCd;

    @JsonProperty("IsTuno")
    private String IsTuno;

    @JsonProperty("AccessToken")
    private String AccessToken;

    public static HeaderRequest of(String api, String[] date) {
        return HeaderRequest.builder()
                .ApiNm(api)
                .Tsymd(date[0].replaceAll("-", ""))
                .Trtm(date[1].replaceAll(":", ""))
                .IsTuno(String.format("%09f", Math.random()*10000000))
                .Iscd("000744")
                .FintechApsno("001")
                .ApiSvcCd("DrawingTransferA")
                .AccessToken("e68a48e64028dd7734440f828a8c6361a25f1cd4ee8f801c1762fb4a6461bbdf")
                .build();
    }
}
