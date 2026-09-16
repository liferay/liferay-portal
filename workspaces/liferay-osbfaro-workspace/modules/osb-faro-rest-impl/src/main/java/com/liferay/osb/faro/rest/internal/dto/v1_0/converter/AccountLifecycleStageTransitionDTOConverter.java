/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStageTransition;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycleStageTransition",
	service = DTOConverter.class
)
public class AccountLifecycleStageTransitionDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.
			AccountLifecycleStageTransition,
		 AccountLifecycleStageTransition> {

	@Override
	public String getContentType() {
		return AccountLifecycleStageTransition.class.getSimpleName();
	}

	@Override
	public AccountLifecycleStageTransition toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.AccountLifecycleStageTransition
			accountLifecycleStageTransition) {

		if (accountLifecycleStageTransition == null) {
			return null;
		}

		return new AccountLifecycleStageTransition() {
			{
				setAccountId(accountLifecycleStageTransition::getAccountId);
				setAccountName(accountLifecycleStageTransition::getAccountName);
				setFromAccountLifecycleStage(
					() -> _accountLifecycleStageDTOConverter.toDTO(
						dtoConverterContext,
						accountLifecycleStageTransition.
							getFromAccountLifecycleStage()));
				setToAccountLifecycleStage(
					() -> _accountLifecycleStageDTOConverter.toDTO(
						dtoConverterContext,
						accountLifecycleStageTransition.
							getToAccountLifecycleStage()));
				setTransitionDate(
					accountLifecycleStageTransition::getTransitionDate);
			}
		};
	}

	@Reference(
		target = "(dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycleStage)"
	)
	private DTOConverter
		<com.liferay.osb.faro.engine.client.model.AccountLifecycleStage,
		 AccountLifecycleStage> _accountLifecycleStageDTOConverter;

}