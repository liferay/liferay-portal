/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycle;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class AccountLifecycleDTOConverterTest {

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_accountLifecycleDTOConverter, "_accountLifecycleStageDTOConverter",
			new AccountLifecycleStageDTOConverter());
	}

	@Test
	public void testToDTO() {
		Assert.assertNull(_accountLifecycleDTOConverter.toDTO(null, null));

		com.liferay.osb.faro.engine.client.model.AccountLifecycle
			engineClientAccountLifecycle =
				new com.liferay.osb.faro.engine.client.model.AccountLifecycle();

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
			engineClientAccountLifecycleStage =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStage();

		engineClientAccountLifecycleStage.setDescription(
			RandomTestUtil.randomString());
		engineClientAccountLifecycleStage.setId(RandomTestUtil.randomString());
		engineClientAccountLifecycleStage.setMaxDuration(
			RandomTestUtil.randomInt());
		engineClientAccountLifecycleStage.setName(
			RandomTestUtil.randomString());
		engineClientAccountLifecycleStage.setPosition(
			RandomTestUtil.randomInt());

		engineClientAccountLifecycle.setAccountLifecycleStages(
			Collections.singletonList(engineClientAccountLifecycleStage));

		engineClientAccountLifecycle.setDefaultAccountLifecycle(
			RandomTestUtil.randomBoolean());
		engineClientAccountLifecycle.setDescription(
			RandomTestUtil.randomString());
		engineClientAccountLifecycle.setId(RandomTestUtil.randomString());
		engineClientAccountLifecycle.setModifiedDate(RandomTestUtil.nextDate());
		engineClientAccountLifecycle.setName(RandomTestUtil.randomString());

		AccountLifecycle accountLifecycle = _accountLifecycleDTOConverter.toDTO(
			null, engineClientAccountLifecycle);

		AccountLifecycleStage[] accountLifecycleStages =
			accountLifecycle.getAccountLifecycleStages();

		Assert.assertEquals(
			accountLifecycleStages.toString(), 1,
			accountLifecycleStages.length);

		AccountLifecycleStage accountLifecycleStage = accountLifecycleStages[0];

		Assert.assertEquals(
			engineClientAccountLifecycleStage.getId(),
			accountLifecycleStage.getId());
		Assert.assertEquals(
			engineClientAccountLifecycleStage.getName(),
			accountLifecycleStage.getName());
		Assert.assertEquals(
			engineClientAccountLifecycleStage.getPosition(),
			accountLifecycleStage.getPosition());

		Assert.assertEquals(
			engineClientAccountLifecycle.getModifiedDate(),
			accountLifecycle.getDateModified());
		Assert.assertEquals(
			engineClientAccountLifecycle.isDefaultAccountLifecycle(),
			accountLifecycle.getDefaultAccountLifecycle());
		Assert.assertEquals(
			engineClientAccountLifecycle.getDescription(),
			accountLifecycle.getDescription());
		Assert.assertEquals(
			engineClientAccountLifecycle.getId(), accountLifecycle.getId());
		Assert.assertEquals(
			engineClientAccountLifecycle.getName(), accountLifecycle.getName());
	}

	private final AccountLifecycleDTOConverter _accountLifecycleDTOConverter =
		new AccountLifecycleDTOConverter();

}