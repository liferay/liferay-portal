/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageTransition;

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

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStageTransition
			engineClientAccountLifecycleStageTransition =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStageTransition();

		engineClientAccountLifecycleStageTransition.setAccountId("account-1");
		engineClientAccountLifecycleStageTransition.setAccountName("Account 1");
		engineClientAccountLifecycleStageTransition.
			setFromAccountLifecycleStage(
				_createEngineClientAccountLifecycleStage(
					"Pipeline", 3, "stage-3", 30, "PIPELINE"));
		engineClientAccountLifecycleStageTransition.setToAccountLifecycleStage(
			_createEngineClientAccountLifecycleStage(
				"Onboarding", 4, "stage-4", 60, "ONBOARDING"));
		engineClientAccountLifecycleStageTransition.setTransitionDate(
			new Date());

		AccountLifecycleStageTransition accountLifecycleStageTransition =
			_accountLifecycleStageTransitionDTOConverter.toDTO(
				null, engineClientAccountLifecycleStageTransition);

		Assert.assertEquals(
			engineClientAccountLifecycleStageTransition.getAccountId(),
			accountLifecycleStageTransition.getAccountId());
		Assert.assertEquals(
			engineClientAccountLifecycleStageTransition.getAccountName(),
			accountLifecycleStageTransition.getAccountName());
		_assertAccountLifecycleStage(
			engineClientAccountLifecycleStageTransition.
				getFromAccountLifecycleStage(),
			accountLifecycleStageTransition.getFromAccountLifecycleStage());
		_assertAccountLifecycleStage(
			engineClientAccountLifecycleStageTransition.
				getToAccountLifecycleStage(),
			accountLifecycleStageTransition.getToAccountLifecycleStage());
		Assert.assertEquals(
			engineClientAccountLifecycleStageTransition.getTransitionDate(),
			accountLifecycleStageTransition.getTransitionDate());
	}

	private void _assertAccountLifecycleStage(
		com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
			engineClientAccountLifecycleStage,
		AccountLifecycleStage accountLifecycleStage) {

		Assert.assertEquals(
			engineClientAccountLifecycleStage.getDescription(),
			accountLifecycleStage.getDescription());
		Assert.assertEquals(
			engineClientAccountLifecycleStage.getDisplayOrder(),
			accountLifecycleStage.getDisplayOrder());
		Assert.assertEquals(
			engineClientAccountLifecycleStage.getId(),
			accountLifecycleStage.getId());
		Assert.assertEquals(
			engineClientAccountLifecycleStage.getMaxDuration(),
			accountLifecycleStage.getMaxDuration());
		Assert.assertEquals(
			engineClientAccountLifecycleStage.getStageType(),
			accountLifecycleStage.getStageType());
	}

	private com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
		_createEngineClientAccountLifecycleStage(
			String description, int displayOrder, String id, int maxDuration,
			String stageType) {

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
			engineClientAccountLifecycleStage =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStage();

		engineClientAccountLifecycleStage.setDescription(description);
		engineClientAccountLifecycleStage.setDisplayOrder(displayOrder);
		engineClientAccountLifecycleStage.setId(id);
		engineClientAccountLifecycleStage.setMaxDuration(maxDuration);
		engineClientAccountLifecycleStage.setStageType(stageType);

		return engineClientAccountLifecycleStage;
	}

	private final AccountLifecycleStageTransitionDTOConverter
		_accountLifecycleStageTransitionDTOConverter =
			new AccountLifecycleStageTransitionDTOConverter();

}