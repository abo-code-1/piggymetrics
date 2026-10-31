package com.piggymetrics.account.dto;


import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Item;
import com.piggymetrics.account.domain.Saving;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class AccountResponseDTO {
    private String name;
    private Date lastSeen;
    private List<Item> incomes;
    private List<Item> expenses;
    private Saving saving;
    private String note;

    public AccountResponseDTO(Account account) {
        this.name = account.getName();
        this.lastSeen = account.getLastSeen();
        this.incomes = account.getIncomes();
        this.expenses = account.getExpenses();
        this.saving = account.getSaving();
        this.note = account.getNote();
    }
}
