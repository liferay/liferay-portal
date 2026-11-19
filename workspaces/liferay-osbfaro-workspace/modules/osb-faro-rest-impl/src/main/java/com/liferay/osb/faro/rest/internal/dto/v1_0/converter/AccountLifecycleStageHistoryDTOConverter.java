/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.AccountLifecycleStageStatus;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageHistory;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycleStageStatus",
	service = DTOConverter.class
)
public class AccountLifecycleStageHistoryDTOConverter
	implements DTOConverter
		<AccountLifecycleStageStatus, AccountLifecycleStageHistory> {

	@Override
	public String getContentType() {
		return AccountLifecycleStageHistory.class.getSimpleName();
	}

	@Override
	public AccountLifecycleStageHistory toDTO(
		DTOConverterContext dtoConverterContext,
		AccountLifecycleStageStatus accountLifecycleStageStatus) {

		if (accountLifecycleStageStatus == null) {
			return null;
		}

		return new AccountLifecycleStageHistory() {
			{
				setDescription(accountLifecycleStageStatus::getDescription);
				setEndDate(accountLifecycleStageStatus::getEndDate);
				setId(accountLifecycleStageStatus::getId);
				setMaxDuration(accountLifecycleStageStatus::getMaxDuration);
				setName(accountLifecycleStageStatus::getName);
				setPosition(accountLifecycleStageStatus::getPosition);
				setStartDate(accountLifecycleStageStatus::getStartDate);
			}
		};
	}

}