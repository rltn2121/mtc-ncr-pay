package core.exg.apis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferWiseDto {

    private String sourceCurrency;
    private String targetCurrency;
    private Long sourceAmount;
    private Long targetAmount;
}
