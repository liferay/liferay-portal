/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageTransition;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.lang.reflect.Field;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class AccountLifecycleStageTransitionDTOConverterTest {

	@Before
	public void setUp() throws Exception {
		Field field =
			AccountLifecycleStageTransitionDTOConverter.class.getDeclaredField(
				"_accountLifecycleStageDTOConverter");

		field.setAccessible(true);

		field.set(
			_accountLifecycleStageTransitionDTOConverter,
			new AccountLifecycleStageDTOConverter());
	}

	@Test
	public void testToDTO() {
		Assert.assertNull(
			_accountLifecycleStageTransitionDTOConverter.toDTO(null, null));

		String accountId = RandomTestUtil.randomString();
		String accountName = RandomTestUtil.randomString();
		String fromAccountLifecycleStageId = RandomTestUtil.randomString();
		String toAccountLifecycleStageId = RandomTestUtil.randomString();
		Date transitionDate = new Date();

		AccountLifecycleStageTransition accountLifecycleStageTransition =
			_toDTO(
				accountId, accountName, fromAccountLifecycleStageId,
				toAccountLifecycleStageId, transitionDate);

		Assert.assertEquals(
			accountId, accountLifecycleStageTransition.getAccountId());
		Assert.assertEquals(
			accountName, accountLifecycleStageTransition.getAccountName());

		AccountLifecycleStage fromAccountLifecycleStage =
			accountLifecycleStageTransition.getFromAccountLifecycleStage();

		Assert.assertEquals(
			fromAccountLifecycleStageId, fromAccountLifecycleStage.getId());

		AccountLifecycleStage toAccountLifecycleStage =
			accountLifecycleStageTransition.getToAccountLifecycleStage();

		Assert.assertEquals(
			toAccountLifecycleStageId, toAccountLifecycleStage.getId());

		Assert.assertEquals(
			transitionDate,
			accountLifecycleStageTransition.getTransitionDate());
	}

	private com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
		_createAccountLifecycleStage(String id) {

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
			accountLifecycleStage =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStage();

		accountLifecycleStage.setId(id);

		return accountLifecycleStage;
	}

	private AccountLifecycleStageTransition _toDTO(
		String accountId, String accountName,
		String fromAccountLifecycleStageId, String toAccountLifecycleStageId,
		Date transitionDate) {

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStageTransition
			accountLifecycleStageTransition =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStageTransition();

		accountLifecycleStageTransition.setAccountId(accountId);
		accountLifecycleStageTransition.setAccountName(accountName);
		accountLifecycleStageTransition.setFromAccountLifecycleStage(
			_createAccountLifecycleStage(fromAccountLifecycleStageId));
		accountLifecycleStageTransition.setToAccountLifecycleStage(
			_createAccountLifecycleStage(toAccountLifecycleStageId));
		accountLifecycleStageTransition.setTransitionDate(transitionDate);

		return _accountLifecycleStageTransitionDTOConverter.toDTO(
			null, accountLifecycleStageTransition);
	}

	private final AccountLifecycleStageTransitionDTOConverter
		_accountLifecycleStageTransitionDTOConverter =
			new AccountLifecycleStageTransitionDTOConverter();

}