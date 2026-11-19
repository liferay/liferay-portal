/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.Acquisition;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAcquisitionsPageResponse;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Caio Pinheiro
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAcquisitionsPageResponse$Composition",
	service = DTOConverter.class
)
public class AcquisitionDTOConverter
	implements DTOConverter
		<GetWorkspaceGroupChannelAcquisitionsPageResponse.Composition,
		 Acquisition> {

	@Override
	public String getContentType() {
		return Acquisition.class.getSimpleName();
	}

	@Override
	public Acquisition toDTO(
		DTOConverterContext dtoConverterContext,
		GetWorkspaceGroupChannelAcquisitionsPageResponse.Composition
			composition) {

		if (composition == null) {
			return null;
		}

		return new Acquisition() {
			{
				setCount(composition::getCount);
				setName(composition::getName);
			}
		};
	}

}