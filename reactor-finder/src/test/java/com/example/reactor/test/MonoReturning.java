package com.example.reactor.test;

import java.util.HashMap;
import java.util.function.Supplier;

import reactor.core.publisher.Mono;

public class MonoReturning {
	
	private static final HashMap<String,String> accounts;
	
	static {
		accounts = new HashMap<>();
		accounts.put("Doug", "Doug's Savings");
		accounts.put("Mary", "Mary's Checking");
		accounts.put("Tom", "Tom's CD");
		
	}
	
	public Mono<String> findAccountName(String customer) {
		Mono<String> mono = Mono.fromSupplier(new AccountSupplier(customer));
		return mono;
	}

	private class AccountSupplier implements Supplier<String> {
		
		private String customer = null;
		
		public AccountSupplier(String c) {
			customer = c;
		}

		@Override
		public String get() {
			return accounts.get(customer);
		}
		
	}
}
