/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.AccountLifecycleStageStatus;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStatus;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleHistory;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageHistory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class AccountLifecycleHistoryDTOConverterTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_accountLifecycleHistoryDTOConverter,
			"_accountLifecycleStageHistoryDTOConverter",
			new AccountLifecycleStageHistoryDTOConverter());
	}

	@Test
	public void testToDTO() {
		Assert.assertNull(
			_accountLifecycleHistoryDTOConverter.toDTO(null, null));

		AccountLifecycleStatus accountLifecycleStatus =
			new AccountLifecycleStatus();

		AccountLifecycleStageStatus accountLifecycleStageStatus =
			new AccountLifecycleStageStatus();

		accountLifecycleStageStatus.setId(RandomTestUtil.randomString());
		accountLifecycleStageStatus.setName(RandomTestUtil.randomString());
		accountLifecycleStageStatus.setStartDate(RandomTestUtil.nextDate());

		accountLifecycleStatus.setAccountLifecycleStageStatuses(
			Collections.singletonList(accountLifecycleStageStatus));

		accountLifecycleStatus.setId(RandomTestUtil.randomString());
		accountLifecycleStatus.setName(RandomTestUtil.randomString());

		AccountLifecycleHistory accountLifecycleHistory =
			_accountLifecycleHistoryDTOConverter.toDTO(
				null, accountLifecycleStatus);

		AccountLifecycleStageHistory[] accountLifecycleStageHistories =
			accountLifecycleHistory.getAccountLifecycleStageHistories();

		Assert.assertEquals(
			accountLifecycleStageHistories.toString(), 1,
			accountLifecycleStageHistories.length);

		AccountLifecycleStageHistory accountLifecycleStageHistory =
			accountLifecycleStageHistories[0];

		Assert.assertNull(accountLifecycleStageHistory.getEndDate());
		Assert.assertEquals(
			accountLifecycleStageStatus.getId(),
			accountLifecycleStageHistory.getId());
		Assert.assertEquals(
			accountLifecycleStageStatus.getName(),
			accountLifecycleStageHistory.getName());
		Assert.assertEquals(
			accountLifecycleStageStatus.getStartDate(),
			accountLifecycleStageHistory.getStartDate());

		Assert.assertEquals(
			accountLifecycleStatus.getId(), accountLifecycleHistory.getId());
		Assert.assertEquals(
			accountLifecycleStatus.getName(),
			accountLifecycleHistory.getName());
	}

	private final AccountLifecycleHistoryDTOConverter
		_accountLifecycleHistoryDTOConverter =
			new AccountLifecycleHistoryDTOConverter();

}