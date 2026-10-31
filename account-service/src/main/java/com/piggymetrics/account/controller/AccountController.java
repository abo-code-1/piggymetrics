package com.piggymetrics.account.controller;

import com.piggymetrics.account.domain.Account;
import com.piggymetrics.account.domain.User;
import com.piggymetrics.account.dto.AccountRequestDTO;
import com.piggymetrics.account.dto.AccountResponseDTO;
import com.piggymetrics.account.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class AccountController {

	private final AccountService accountService;
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PreAuthorize("#oauth2.hasScope('server') or #name.equals('demo')")
	@GetMapping("/{name}")
	public ResponseEntity<AccountResponseDTO> getAccountByName(@PathVariable String name) {
		return ResponseEntity.ok(accountService.findByName(name));
	}

	@GetMapping("/current")
	public ResponseEntity<AccountResponseDTO> getCurrentAccount(Principal principal) {
		return ResponseEntity.ok(accountService.findByName(principal.getName()));
	}

	@PutMapping("/current")
	public ResponseEntity<AccountResponseDTO> saveCurrentAccount(Principal principal, @Valid @RequestBody AccountRequestDTO account) {
		return ResponseEntity.ok(accountService.saveChanges(principal.getName(), account));
	}

	@PostMapping("/")
	public ResponseEntity<AccountResponseDTO> createNewAccount(@Valid @RequestBody User user) {
		return ResponseEntity.ok(accountService.create(user));
	}
}
