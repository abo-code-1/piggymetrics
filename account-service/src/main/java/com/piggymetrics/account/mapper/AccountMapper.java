package com.piggymetrics.account.mapper;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.dto.AccountRequestDTO;
import com.piggymetrics.account.dto.AccountResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountResponseDTO toResponseDTO(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "lastSeen", ignore = true)
    Account toEntity(AccountRequestDTO accountRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "lastSeen", ignore = true)
    void updateEntity(AccountRequestDTO accountRequestDTO, @MappingTarget Account account);
}
