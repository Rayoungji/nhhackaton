package org.nhhackaton.invest.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nhhackaton.api.easypament.TransferApiService;
import org.nhhackaton.api.easypament.dto.DrawingTransferRequest;
import org.nhhackaton.api.easypament.dto.DrawingTransferResponse;
import org.nhhackaton.api.finaccount.FinAccountApiService;
import org.nhhackaton.api.finaccount.dto.CheckFinAccountRequest;
import org.nhhackaton.api.finaccount.dto.CheckFinAccountResponse;
import org.nhhackaton.api.finaccount.dto.OpenFinAccountRequest;

import org.nhhackaton.api.finaccount.dto.OpenFinAccountResponse;
import org.nhhackaton.api.p2p.P2PApiService;
import org.nhhackaton.api.p2p.dto.VirtualAccountRequest;
import org.nhhackaton.api.p2p.dto.VirtualAccountResponse;
import org.nhhackaton.invest.entity.Invest;
import org.nhhackaton.invest.repository.InvestRepository;
import org.nhhackaton.member.entity.Member;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvestService {
    private final FinAccountApiService finAccountApiService;
    private final TransferApiService transferApiService;
    private final P2PApiService p2PApiService;
    private final InvestRepository investRepository;


    public void applyInvest(Member member, String investPrice) {

        //투자 핀어카운트 -> 핀테크 약정계좌
        DrawingTransferRequest drawingTransferRequest = DrawingTransferRequest.builder()
                .MractOtlt("입금되었습니다")
                .DractOtlt("출금되었습니다")
                .Tram(investPrice)
                .FinAcno(member.getInvestFinAccount()).build();
        ResponseEntity<DrawingTransferResponse> drawingTransferResponse = transferApiService.draw(drawingTransferRequest);
        System.out.println(drawingTransferResponse.getBody().getRfsnYmd());  //투자 등록일자

        if (member.getInvestVirtualAccount() == null) {
            //투자자용 가상계좌 발급
            VirtualAccountRequest virtualAccountRequest = VirtualAccountRequest.builder()
                    .P2PVractUsg("1")
                    .P2PCmtmNo("0000000000")
                    .ChidSqno("0000000000")
                    .InvstBrwNm(member.getName()).build();

            makeInvestVirtualAccount(member, virtualAccountRequest);
        }

        //TODO 핀테크 약정계좌 -> 투자용 가상계좌로 투자금 이체는 현재 테스트 불가

        Invest invest = Invest.builder()
                .isLoan(false)
                .investPrice(investPrice)
                .investMember(member)
                .investDate(drawingTransferResponse.getBody().getRfsnYmd()).build();

        investRepository.save(invest);

    }

    public void makeInvestFinAccount(Member member, OpenFinAccountRequest openFinAccountRequest) {
        ResponseEntity<OpenFinAccountResponse> open = finAccountApiService.open(openFinAccountRequest);
        log.warn(" ========= MAKE INVEST FIN ACCOUNT START =============");
        System.out.println("OpenFin Response: " + open.getBody().getRgno());  //checkFin 요청값

        CheckFinAccountRequest checkFinAccountRequest = CheckFinAccountRequest.builder()
                .Rgno(open.getBody().getRgno())
                .BrdtBrno(member.getBirthday()).build();

        ResponseEntity<CheckFinAccountResponse> check = finAccountApiService.check(checkFinAccountRequest);
        System.out.println("CheckFin Response: " + check.getBody().getFinAcno());  //InvestFin 발급 완료

        member.setInvestFinAccount(check.getBody().getFinAcno());
        log.warn(" ========= MAKE INVEST FIN ACCOUNT END =============");
    }

    public void makeInvestVirtualAccount(Member member, VirtualAccountRequest virtualAccountRequest) {
        log.warn(" ========= MAKE INVEST VIRTUAL ACCOUNT START =============");
        ResponseEntity<VirtualAccountResponse> virtualAccountResponse = p2PApiService.create(virtualAccountRequest);
        System.out.println("VirtualAccount: " + virtualAccountResponse.getBody().getVran()); //가상계좌 발급
        member.setInvestVirtualAccount(virtualAccountResponse.getBody().getVran());
        log.warn(" ========= MAKE INVEST VIRTUAL ACCOUNT START =============");
    }
}
