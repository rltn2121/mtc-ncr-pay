package core.dto;

import lombok.Data;

@Data
public class MtcNcrPayResponse {
    private String payAcser;
    private Integer result;
    private String errStr;
}
