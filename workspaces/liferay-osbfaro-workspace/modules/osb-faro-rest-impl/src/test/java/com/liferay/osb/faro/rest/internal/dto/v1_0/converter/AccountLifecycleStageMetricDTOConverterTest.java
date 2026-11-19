/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageMetric;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Nilton Vieira
 */
public class AccountLifecycleStageMetricDTOConverterTest {

	@Test
	public void testToDTO() {
		Assert.assertNull(
			_accountLifecycleStageMetricDTOConverter.toDTO(null, null));

		com.liferay.osb.faro.engine.client.model.AccountLifecycleStageMetric
			engineClientAccountLifecycleStageMetric =
				new com.liferay.osb.faro.engine.client.model.
					AccountLifecycleStageMetric();

		engineClientAccountLifecycleStageMetric.setAccountsCount(
			RandomTestUtil.randomLong());
		engineClientAccountLifecycleStageMetric.setAverageStageDuration(
			RandomTestUtil.randomDouble());
		engineClientAccountLifecycleStageMetric.setConversionRateToNextStage(
			RandomTestUtil.randomDouble());
		engineClientAccountLifecycleStageMetric.setDescription(
			RandomTestUtil.randomString());
		engineClientAccountLifecycleStageMetric.setName(
			RandomTestUtil.randomString());
		engineClientAccountLifecycleStageMetric.setPercentage(
			RandomTestUtil.randomDouble());
		engineClientAccountLifecycleStageMetric.setPosition(
			RandomTestUtil.randomInt());

		AccountLifecycleStageMetric accountLifecycleStageMetric =
			_accountLifecycleStageMetricDTOConverter.toDTO(
				null, engineClientAccountLifecycleStageMetric);

		Assert.assertEquals(
			Long.valueOf(
				engineClientAccountLifecycleStageMetric.getAccountsCount()),
			accountLifecycleStageMetric.getAccountsCount());
		Assert.assertEquals(
			Double.valueOf(
				engineClientAccountLifecycleStageMetric.
					getAverageStageDuration()),
			accountLifecycleStageMetric.getAverageStageDuration());
		Assert.assertEquals(
			engineClientAccountLifecycleStageMetric.
				getConversionRateToNextStage(),
			accountLifecycleStageMetric.getConversionRateToNextStage());
		Assert.assertEquals(
			engineClientAccountLifecycleStageMetric.getDescription(),
			accountLifecycleStageMetric.getDescription());
		Assert.assertEquals(
			engineClientAccountLifecycleStageMetric.getName(),
			accountLifecycleStageMetric.getName());
		Assert.assertEquals(
			Double.valueOf(
				engineClientAccountLifecycleStageMetric.getPercentage()),
			accountLifecycleStageMetric.getPercentage());
		Assert.assertEquals(
			engineClientAccountLifecycleStageMetric.getPosition(),
			accountLifecycleStageMetric.getPosition());
	}

	private final AccountLifecycleStageMetricDTOConverter
		_accountLifecycleStageMetricDTOConverter =
			new AccountLifecycleStageMetricDTOConverter();

}