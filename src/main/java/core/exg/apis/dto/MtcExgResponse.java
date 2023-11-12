package core.exg.apis.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MtcExgResponse {

    /* 성공여부 */
    private String result;

    /* 에러CODE */
    private String errCode;

    /* 에러메세지 */
    private String errMsg;
}
