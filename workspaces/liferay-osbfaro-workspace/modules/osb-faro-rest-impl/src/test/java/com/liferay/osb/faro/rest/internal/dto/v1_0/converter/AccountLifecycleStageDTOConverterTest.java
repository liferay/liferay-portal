/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class AccountLifecycleStageDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(_accountLifecycleStageDTOConverter.toDTO(null, null));

		String description = RandomTestUtil.randomString();
		String id = RandomTestUtil.randomString();
		Integer maxDuration = RandomTestUtil.randomInt();
		String name = RandomTestUtil.randomString();
		Integer position = RandomTestUtil.randomInt();

		AccountLifecycleStage accountLifecycleStage = _toDTO(
			description, id, maxDuration, name, position);

		Assert.assertEquals(
			description, accountLifecycleStage.getDescription());
		Assert.assertEquals(id, accountLifecycleStage.getId());
		Assert.assertEquals(
			maxDuration, accountLifecycleStage.getMaxDuration());
		Assert.assertEquals(name, accountLifecycleStage.getName());
		Assert.assertEquals(position, accountLifecycleStage.getPosition());
	}

	private AccountLifecycleStage _toDTO(
		String description, String id, Integer maxDuration, String name,
		Integer position) {

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
			accountLifecycleStage =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStage();

		accountLifecycleStage.setDescription(description);
		accountLifecycleStage.setId(id);
		accountLifecycleStage.setMaxDuration(maxDuration);
		accountLifecycleStage.setName(name);
		accountLifecycleStage.setPosition(position);

		return _accountLifecycleStageDTOConverter.toDTO(
			null, accountLifecycleStage);
	}

	private final AccountLifecycleStageDTOConverter
		_accountLifecycleStageDTOConverter =
			new AccountLifecycleStageDTOConverter();

}