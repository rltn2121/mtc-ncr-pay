package core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties (ignoreUnknown = true)
public class MtcExgRequest{

    /* 결제여부             */
    /* Y : 결제 중 충전 요청 */
    /* N : 고객이 충전 요청  */
    private String payYn;

    /* 계좌번호 (고객번호) */
    private String acno;

    /* 통화코드 */
    private String curC;

    /* 충전 금액 */
    private Double trxAmt;

    /* 충전 일련번호 : 화면 충전 요청들어올 때 채번 */
    private String acser;

    /* 결제 정보 */
    private MtcNcrPayRequest payInfo;

}
