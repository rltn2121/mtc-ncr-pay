package core.exg.service;

import core.Repository.SdaMainMasRepository;
import core.domain.SdaMainMas;
import core.domain.SdaMainMasId;
import core.dto.MtcExgRequest;
import core.dto.MtcExgResponse;
import core.exg.apis.controller.MtcExgController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MtcExgService {

    private final static Logger log = LoggerFactory.getLogger(MtcExgController.class);
    private final SdaMainMasRepository sdaMainMasRepository;

    public MtcExgResponse exchangeService(MtcExgRequest exgRequest) {

        // 충전 결과
        MtcExgResponse exgResponse = new MtcExgResponse();

        try
        {
            /* main_mas 충전 요청 들어온 잔액 추가해주기 */
            // main_mas 원장 읽기
            SdaMainMas TrxMainMas = this.sdaMainMasRepository
                                        .findById(new SdaMainMasId(exgRequest.getAcno(), exgRequest.getCurC())).get();

            log.info("@@영은 충전 전 외화({}) main_mas --> {} ",TrxMainMas.getCur_c(), TrxMainMas.toString());

            TrxMainMas.setAc_jan(TrxMainMas.getAc_jan() + exgRequest.getTrxAmt());

            // main_mas 업데이트
            TrxMainMas = this.sdaMainMasRepository.save(TrxMainMas);

            log.info("@@영은 충전 후 외화({}) main_mas --> {} ",TrxMainMas.getCur_c(), TrxMainMas.toString());

            /* main_mas KRW 금액 빼주기 */
            SdaMainMas KRWMainMas = this.sdaMainMasRepository
                                        .findById(new SdaMainMasId(exgRequest.getAcno(), "KRW")).get();

            log.info("@@영은 충전 전 원화(KRW) main_mas --> {} ", KRWMainMas.toString());

            KRWMainMas.setAc_jan(KRWMainMas.getAc_jan() + exgRequest.getTrxAmt());

            // main_mas 업데이트
            KRWMainMas = this.sdaMainMasRepository.save(KRWMainMas);

            log.info("@@영은 충전 후 원화(KRW) main_mas --> {} ", KRWMainMas.toString());

            // 충전 완료!!!
            log.info("@@영은 충전 완료!!!");
        }
        catch( Exception e)
        {
            log.info("@@영은 충전 실패함 --> [{}]" , e.toString());
        }

        return exgResponse;
    }
}
