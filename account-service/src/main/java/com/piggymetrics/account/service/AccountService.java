package com.piggymetrics.account.service;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.dto.AccountRequestDTO;
import com.piggymetrics.account.dto.AccountResponseDTO;

public interface AccountService {

	/**
	 * Finds account by given name
	 *
	 * @param accountName
	 * @return found account
	 */
	AccountResponseDTO findByName(String accountName);

	/**
	 * Checks if account with the same name already exists
	 * Invokes Auth Service user creation
	 * Creates new account with default parameters
	 *
	 * @param user
	 * @return created account
	 */
	AccountResponseDTO create(User user);

	/**
	 * Validates and applies incoming account updates
	 * Invokes Statistics Service update
	 *
	 * @param name
	 * @param update
	 */
	AccountResponseDTO saveChanges(String name, AccountRequestDTO update);
}
