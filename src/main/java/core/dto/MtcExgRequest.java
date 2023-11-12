package core.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MtcExgRequest {

    /* 업무구분             */
    /* 1 : 고객이 충전 요청  */
    /* 2 : 결제 중 충전 요청 */
    private Long upmuG;

    /* 계좌번호 (고객번호) */
    private String acno;

    /* 통화코드 */
    private String curC;

    /* 충전 금액 */
    private Double trxAmt;

    /* 일련번호 */
    /* 결제 일련번호는 결제에서 충전 요청이 들어온 경우에 필수 사항 */
    /* 충전 일련번호는 화면 충전 요청들어올 때 채번              */
    private String acser;
}
