package core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MtcExgResponse {

    /* 충전 일련번호 */
    private String exgAcser;

    /* 결과 */
    private Integer result;

    /* 에러메세지 */
    private String errStr;
}
