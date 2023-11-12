package core.pay.apis.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MtcNcrPayRequest {
    //고객번호 , 통화코드 , 거래처 , 금액 , 일시
    private String acno ;
    private String curC;
    private String trxPlace;
    private Double trxAmt ;
    private String trxDt;
    private String payAcser  ;
}
