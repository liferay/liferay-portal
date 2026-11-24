/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Campaign;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Nilton Vieira
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.Campaign",
	service = DTOConverter.class
)
public class CampaignDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.Campaign, Campaign> {

	@Override
	public String getContentType() {
		return Campaign.class.getSimpleName();
	}

	@Override
	public Campaign toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.Campaign campaign) {

		if (campaign == null) {
			return null;
		}

		return new Campaign() {
			{
				setAccountsTouched(campaign::getAccountsTouched);
				setCampaignType(campaign::getCampaignType);
				setEndDate(campaign::getEndDate);
				setId(campaign::getId);
				setIndividualsTouched(campaign::getIndividualsTouched);
				setLastTouchDate(campaign::getLastTouchDate);
				setName(campaign::getCampaignName);
				setOrigin(campaign::getOrigin);
				setStartDate(campaign::getStartDate);
				setStatus(campaign::getStatus);
			}
		};
	}

}