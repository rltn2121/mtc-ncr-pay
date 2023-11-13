package core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MtcResultRequest {
    private String acno;
    private String trxdt;
    private String curC;
    private int upmuG;
    private String aprvSno;
    private Double trxAmt;
    private Double nujkJan;
    private String errMsg;
}
