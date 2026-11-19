/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.engine.client.model.AccountLifecycleStageStatus;
import com.liferay.osb.faro.engine.client.model.AccountLifecycleStatus;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleHistory;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageHistory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nilton Vieira
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycleStatus",
	service = DTOConverter.class
)
public class AccountLifecycleHistoryDTOConverter
	implements DTOConverter<AccountLifecycleStatus, AccountLifecycleHistory> {

	@Override
	public String getContentType() {
		return AccountLifecycleHistory.class.getSimpleName();
	}

	@Override
	public AccountLifecycleHistory toDTO(
		DTOConverterContext dtoConverterContext,
		AccountLifecycleStatus accountLifecycleStatus) {

		if (accountLifecycleStatus == null) {
			return null;
		}

		return new AccountLifecycleHistory() {
			{
				setAccountLifecycleStageHistories(
					() -> TransformUtil.transformToArray(
						accountLifecycleStatus.
							getAccountLifecycleStageStatuses(),
						accountLifecycleStageStatus ->
							_accountLifecycleStageHistoryDTOConverter.toDTO(
								dtoConverterContext,
								accountLifecycleStageStatus),
						AccountLifecycleStageHistory.class));
				setId(accountLifecycleStatus::getId);
				setName(accountLifecycleStatus::getName);
			}
		};
	}

	@Reference(
		target = "(dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycleStageStatus)"
	)
	private DTOConverter
		<AccountLifecycleStageStatus, AccountLifecycleStageHistory>
			_accountLifecycleStageHistoryDTOConverter;

}