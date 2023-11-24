package core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class MtcNcrUpdateMainMasRequest {
    private String acno;
    private String gid;
    private List<MtcNcrUpdateMainMasRequestSub> requestSubList;
    private String svcId;
}
