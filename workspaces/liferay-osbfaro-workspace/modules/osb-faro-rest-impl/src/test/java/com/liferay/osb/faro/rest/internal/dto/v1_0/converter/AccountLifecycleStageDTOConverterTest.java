/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class AccountLifecycleStageDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_accountLifecycleStageDTOConverter.toDTO(null, null));

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
			engineClientAccountLifecycleStage =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStage();

		engineClientAccountLifecycleStage.setDescription("Pipeline");
		engineClientAccountLifecycleStage.setDisplayOrder(3);
		engineClientAccountLifecycleStage.setId("stage-1");
		engineClientAccountLifecycleStage.setMaxDuration(30);
		engineClientAccountLifecycleStage.setStageType("PIPELINE");

		AccountLifecycleStage accountLifecycleStage =
			_accountLifecycleStageDTOConverter.toDTO(
				null, engineClientAccountLifecycleStage);

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

	private final AccountLifecycleStageDTOConverter
		_accountLifecycleStageDTOConverter =
			new AccountLifecycleStageDTOConverter();

}