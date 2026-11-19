/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.AccountLifecycleStageStatus;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageHistory;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class AccountLifecycleStageHistoryDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(
			_accountLifecycleStageHistoryDTOConverter.toDTO(null, null));

		AccountLifecycleStageStatus accountLifecycleStageStatus =
			new AccountLifecycleStageStatus();

		accountLifecycleStageStatus.setDescription(
			RandomTestUtil.randomString());
		accountLifecycleStageStatus.setEndDate(RandomTestUtil.nextDate());
		accountLifecycleStageStatus.setId(RandomTestUtil.randomString());
		accountLifecycleStageStatus.setMaxDuration(RandomTestUtil.randomInt());
		accountLifecycleStageStatus.setName(RandomTestUtil.randomString());
		accountLifecycleStageStatus.setPosition(RandomTestUtil.randomInt());
		accountLifecycleStageStatus.setStartDate(RandomTestUtil.nextDate());

		AccountLifecycleStageHistory accountLifecycleStageHistory =
			_accountLifecycleStageHistoryDTOConverter.toDTO(
				null, accountLifecycleStageStatus);

		Assert.assertEquals(
			accountLifecycleStageStatus.getDescription(),
			accountLifecycleStageHistory.getDescription());
		Assert.assertEquals(
			accountLifecycleStageStatus.getEndDate(),
			accountLifecycleStageHistory.getEndDate());
		Assert.assertEquals(
			accountLifecycleStageStatus.getId(),
			accountLifecycleStageHistory.getId());
		Assert.assertEquals(
			accountLifecycleStageStatus.getMaxDuration(),
			accountLifecycleStageHistory.getMaxDuration());
		Assert.assertEquals(
			accountLifecycleStageStatus.getName(),
			accountLifecycleStageHistory.getName());
		Assert.assertEquals(
			accountLifecycleStageStatus.getPosition(),
			accountLifecycleStageHistory.getPosition());
		Assert.assertEquals(
			accountLifecycleStageStatus.getStartDate(),
			accountLifecycleStageHistory.getStartDate());
	}

	private final AccountLifecycleStageHistoryDTOConverter
		_accountLifecycleStageHistoryDTOConverter =
			new AccountLifecycleStageHistoryDTOConverter();

}