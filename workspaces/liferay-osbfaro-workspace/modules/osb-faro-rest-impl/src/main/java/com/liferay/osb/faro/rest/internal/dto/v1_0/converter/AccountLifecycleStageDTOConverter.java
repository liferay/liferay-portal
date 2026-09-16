/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.AccountLifecycleStage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.AccountLifecycleStage",
	service = DTOConverter.class
)
public class AccountLifecycleStageDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.AccountLifecycleStage,
		 AccountLifecycleStage> {

	@Override
	public String getContentType() {
		return AccountLifecycleStage.class.getSimpleName();
	}

	@Override
	public AccountLifecycleStage toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.AccountLifecycleStage
			accountLifecycleStage) {

		if (accountLifecycleStage == null) {
			return null;
		}

		return new AccountLifecycleStage() {
			{
				setDescription(accountLifecycleStage::getDescription);
				setDisplayOrder(accountLifecycleStage::getDisplayOrder);
				setId(accountLifecycleStage::getId);
				setMaxDuration(accountLifecycleStage::getMaxDuration);
				setStageType(accountLifecycleStage::getStageType);
			}
		};
	}

}