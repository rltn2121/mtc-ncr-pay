package core.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MtcResultRequest {

    // 1. gojeongslv 에 쌓기 위한 변수 셋팅
    private String acno; // 계좌번호
    private String trxdt; // 거래일자
    private String curC; // 통화코드
    private int upmuG; // 업무구분
    private String aprvSno; // 승인번호 ( 결제요청번호 )
    private Double trxAmt; // 거래금액
    private Double nujkJan; // 누적잔고
    private String errMsg; // 에러메세지

    // 2. 결제->충전->결과로 들어오는 경우 다시 결제를 태우기 위해 해당 정보를 들고다닌다.
    private MtcNcrPayRequest payinfo; //결제정보
    private String payYn ; // 결제여부

    // 3. 로그 처리를 위한 gid
    private String gid; // 글로벌아이디

}

