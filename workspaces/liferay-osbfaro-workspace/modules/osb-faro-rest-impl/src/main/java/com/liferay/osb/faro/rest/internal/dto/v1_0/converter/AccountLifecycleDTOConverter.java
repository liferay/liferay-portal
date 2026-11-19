/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycle;
import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nilton Vieira
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycle",
	service = DTOConverter.class
)
public class AccountLifecycleDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.AccountLifecycle,
		 AccountLifecycle> {

	@Override
	public String getContentType() {
		return AccountLifecycle.class.getSimpleName();
	}

	@Override
	public AccountLifecycle toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.AccountLifecycle
			accountLifecycle) {

		if (accountLifecycle == null) {
			return null;
		}

		return new AccountLifecycle() {
			{
				setAccountLifecycleStages(
					() -> TransformUtil.transformToArray(
						accountLifecycle.getAccountLifecycleStages(),
						accountLifecycleStage ->
							_accountLifecycleStageDTOConverter.toDTO(
								dtoConverterContext, accountLifecycleStage),
						AccountLifecycleStage.class));
				setDateModified(accountLifecycle::getModifiedDate);
				setDefaultAccountLifecycle(
					accountLifecycle::isDefaultAccountLifecycle);
				setDescription(accountLifecycle::getDescription);
				setId(accountLifecycle::getId);
				setName(accountLifecycle::getName);
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