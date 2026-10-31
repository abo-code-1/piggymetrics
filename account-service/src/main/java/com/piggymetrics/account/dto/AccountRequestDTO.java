package com.piggymetrics.account.dto;

import com.piggymetrics.account.domain.Item;
import com.piggymetrics.account.domain.Saving;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountRequestDTO {

    @Valid
    private List<Item> incomes;

    @Valid
    private List<Item> expenses;

    @Valid
    @NotNull(message = "Saving is required")
    private Saving saving;

    @Size(max = 20000, message = "Note cannot exceed 20000 characters")
    private String note;
}
