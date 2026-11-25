/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Account;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class AccountDTOConverterTest {

	@Test
	public void testToDTO() {
		String accountName = RandomTestUtil.randomString();
		String accountType = RandomTestUtil.randomString();
		Long activitiesCount = RandomTestUtil.randomLong();
		Double annualRevenue = RandomTestUtil.randomDouble();
		Date firstActivityDate = RandomTestUtil.nextDate();
		String id = RandomTestUtil.randomString();
		Date lastActivityDate = RandomTestUtil.nextDate();
		Integer numberOfEmployees = RandomTestUtil.randomInt();
		String website = RandomTestUtil.randomString();

		Account account = _toDTO(
			accountName, accountType, activitiesCount, annualRevenue,
			firstActivityDate, id, lastActivityDate, numberOfEmployees,
			website);

		Assert.assertEquals(accountName, account.getAccountName());
		Assert.assertEquals(accountType, account.getAccountType());
		Assert.assertEquals(activitiesCount, account.getActivitiesCount());
		Assert.assertEquals(annualRevenue, account.getAnnualRevenue());
		Assert.assertEquals(firstActivityDate, account.getFirstActivityDate());
		Assert.assertEquals(id, account.getId());
		Assert.assertEquals(lastActivityDate, account.getLastActivityDate());
		Assert.assertNull(account.getLifecycleStage());
		Assert.assertEquals(numberOfEmployees, account.getNumberOfEmployees());
		Assert.assertEquals(website, account.getWebsite());

		Assert.assertNull(
			_accountDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private Account _toDTO(
		String accountName, String accountType, Long activitiesCount,
		Double annualRevenue, Date firstActivityDate, String id,
		Date lastActivityDate, Integer numberOfEmployees, String website) {

		com.liferay.osb.faro.engine.client.model.Account account =
			new com.liferay.osb.faro.engine.client.model.Account();

		account.setAccountName(accountName);
		account.setAccountType(accountType);
		account.setActivitiesCount(activitiesCount);
		account.setAnnualRevenue(annualRevenue);
		account.setFirstActivityDate(firstActivityDate);
		account.setId(id);
		account.setLastActivityDate(lastActivityDate);
		account.setNumberOfEmployees(numberOfEmployees);
		account.setWebsite(website);

		return _accountDTOConverter.toDTO(
			new FaroDTOConverterContext(false, id, null), account);
	}

	private final AccountDTOConverter _accountDTOConverter =
		new AccountDTOConverter();

}