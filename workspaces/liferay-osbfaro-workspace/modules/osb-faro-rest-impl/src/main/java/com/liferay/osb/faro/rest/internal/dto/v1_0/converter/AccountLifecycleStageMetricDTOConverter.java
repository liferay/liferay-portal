/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageMetric;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycleStageMetric",
	service = DTOConverter.class
)
public class AccountLifecycleStageMetricDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.AccountLifecycleStageMetric,
		 AccountLifecycleStageMetric> {

	@Override
	public String getContentType() {
		return AccountLifecycleStageMetric.class.getSimpleName();
	}

	@Override
	public AccountLifecycleStageMetric toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.AccountLifecycleStageMetric
			accountLifecycleStageMetric) {

		if (accountLifecycleStageMetric == null) {
			return null;
		}

		return new AccountLifecycleStageMetric() {
			{
				setAccountsCount(accountLifecycleStageMetric::getAccountsCount);
				setAverageStageDuration(
					accountLifecycleStageMetric::getAverageStageDuration);
				setConversionRateToNextStage(
					accountLifecycleStageMetric::getConversionRateToNextStage);
				setDescription(accountLifecycleStageMetric::getDescription);
				setName(accountLifecycleStageMetric::getName);
				setPercentage(accountLifecycleStageMetric::getPercentage);
				setPosition(accountLifecycleStageMetric::getPosition);
			}
		};
	}

}