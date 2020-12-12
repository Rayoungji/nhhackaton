package org.nhhackaton.loan.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nhhackaton.loan.entity.Loan;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class LoanExecuteRequest {
    private String studentIdentity;
    private String loanAmount;
    private String term;
    private String studentBankCode;
    private String studentAccount;
    private String studentName;

    public Loan of(){
        return Loan.builder()
                .loanDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(Long.parseLong(term)))
                .isRepay(false)
                .repayCount(0)
                .receiver(studentName)
                .receiverBankCode(studentBankCode)
                .receiverAccount(studentAccount)
                .receiverIdentity(studentIdentity)
                .build();
    }
}
