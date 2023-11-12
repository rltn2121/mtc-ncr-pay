package core.pay.apis.dto;

import lombok.Data;

@Data
public class MtcNcrPayResponse {
    private String payAcser;
    private Integer result;
    private String errStr;
}
