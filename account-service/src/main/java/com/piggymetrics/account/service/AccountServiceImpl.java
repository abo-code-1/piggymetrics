package com.piggymetrics.account.service;

import com.piggymetrics.account.client.AuthServiceClient;
import com.piggymetrics.account.client.StatisticsServiceClient;
import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.Currency;
import com.piggymetrics.account.domain.Saving;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.dto.AccountRequestDTO;
import com.piggymetrics.account.dto.AccountResponseDTO;
import com.piggymetrics.account.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class AccountServiceImpl implements AccountService {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private final StatisticsServiceClient statisticsClient;
	private final  AuthServiceClient authClient;
	private final AccountRepository repository;

	public AccountServiceImpl (@Qualifier("com.piggymetrics.account.client.StatisticsServiceClient") StatisticsServiceClient statisticsClient,
                               AuthServiceClient authClient,
                               AccountRepository repository) {
		this.statisticsClient = statisticsClient;
		this.authClient = authClient;
		this.repository = repository;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountResponseDTO findByName(String accountName) {
		Assert.hasLength(accountName);
		return repository.findById(accountName)
				.map(AccountResponseDTO::new)
				.orElse(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountResponseDTO create(User user) {

		if (repository.existsById(user.getUsername())) {
			throw new IllegalArgumentException("account already exists: " + user.getUsername());
		}
		authClient.createUser(user);

		Saving saving = new Saving();
		saving.setAmount(new BigDecimal(0));
		saving.setCurrency(Currency.getDefault());
		saving.setInterest(new BigDecimal(0));
		saving.setDeposit(false);
		saving.setCapitalization(false);

		Account account = new Account();
		account.setName(user.getUsername());
		account.setLastSeen(new Date());
		account.setSaving(saving);

		repository.save(account);

		log.info("new account has been created: " + account.getName());

		return new AccountResponseDTO(account);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccountResponseDTO saveChanges(String name, AccountRequestDTO update) {
		Account account = repository.findById(name)
				.orElseThrow(() -> {
					log.error("account {} not found", name);
					return new IllegalArgumentException("account not found: " + name);
				});

		account.setIncomes(update.getIncomes());
		account.setExpenses(update.getExpenses());
		account.setSaving(update.getSaving());
		account.setNote(update.getNote());
		account.setLastSeen(new Date());
		repository.save(account);

		log.debug("account {} changes has been saved", name);
		statisticsClient.updateStatistics(name, account);






	}
}
