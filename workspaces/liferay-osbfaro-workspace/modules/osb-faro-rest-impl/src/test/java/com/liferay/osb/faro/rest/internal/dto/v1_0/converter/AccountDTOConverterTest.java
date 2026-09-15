/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Account;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class AccountDTOConverterTest {

	@Test
	public void testToDTOMapsEngagementFields() {
		com.liferay.osb.faro.engine.client.model.Account account =
			new com.liferay.osb.faro.engine.client.model.Account();

		account.setAccountName("Acme Corp");
		account.setAccountType("Customer");
		account.setActivitiesCount(42L);
		account.setAnnualRevenue(1200000D);

		Date firstActivityDate = new Date(1700000000000L);
		Date lastActivityDate = new Date(1750000000000L);

		account.setFirstActivityDate(firstActivityDate);
		account.setId("account-1");
		account.setLastActivityDate(lastActivityDate);
		account.setNumberOfEmployees(250);
		account.setWebsite("https://acme.example");

		Account accountDTO = _accountDTOConverter.toDTO(
			new FaroDTOConverterContext(false, "account-1", null), account);

		Assert.assertEquals("Acme Corp", accountDTO.getAccountName());
		Assert.assertEquals("Customer", accountDTO.getAccountType());
		Assert.assertEquals(Long.valueOf(42), accountDTO.getActivitiesCount());
		Assert.assertEquals(
			Double.valueOf(1200000D), accountDTO.getAnnualRevenue());
		Assert.assertEquals(
			firstActivityDate, accountDTO.getFirstActivityDate());
		Assert.assertEquals("account-1", accountDTO.getId());
		Assert.assertEquals(lastActivityDate, accountDTO.getLastActivityDate());
		Assert.assertNull(accountDTO.getLifecycleStage());
		Assert.assertEquals(
			Integer.valueOf(250), accountDTO.getNumberOfEmployees());
		Assert.assertEquals("https://acme.example", accountDTO.getWebsite());
	}

	@Test
	public void testToDTOReturnsNullForNullAccount() {
		Assert.assertNull(
			_accountDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private final AccountDTOConverter _accountDTOConverter =
		new AccountDTOConverter();

}