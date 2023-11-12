package core.service;

import core.domain.SdaMainMas;
import lombok.RequiredArgsConstructor;
import core.Repository.SdaMainMasRepository;
import core.dto.MtcNcrPayRequest;
import core.dto.MtcNcrPayResponse;
import core.domain.SdaMainMasId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MtcPayService {
    private final SdaMainMasRepository sdaMainMasRepository;
    private static final Logger log = LoggerFactory.getLogger(MtcPayService.class);
    public MtcNcrPayResponse withdraw(MtcNcrPayRequest requestInfo)
    {
        MtcNcrPayResponse payResponse = new MtcNcrPayResponse();
        try
        {
            //계좌원장에서 읽어와서 잔액을 업데이트 한다.
            SdaMainMas sdaMainMas = this.sdaMainMasRepository
                    .findById(new SdaMainMasId(requestInfo.getAcno(), requestInfo.getCurC())).get();
           sdaMainMas.setAc_jan(sdaMainMas.getAc_jan() - requestInfo.getTrxAmt());
           sdaMainMas = this.sdaMainMasRepository.save(sdaMainMas);
           log.info("$$$결제성공 {} " , sdaMainMas.toString());
           payResponse.setResult(1);
        }
        catch( Exception e)
        {
            payResponse.setResult(-1);
            payResponse.setErrStr(e.toString());
            payResponse.setPayAcser(requestInfo.getPayAcser());
            log.info("$$$$$결제 실패함 : [{}]" , payResponse.toString());
        }
        return payResponse;
    }
}
