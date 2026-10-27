/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembership;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroEnumUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership",
	service = DTOConverter.class
)
public class IndividualSegmentMembershipDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership,
		 IndividualSegmentMembership> {

	@Override
	public String getContentType() {
		return IndividualSegmentMembership.class.getSimpleName();
	}

	@Override
	public IndividualSegmentMembership toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership
			individualSegmentMembership) {

		if (individualSegmentMembership == null) {
			return null;
		}

		return new IndividualSegmentMembership() {
			{
				setDateCreated(individualSegmentMembership::getDateCreated);
				setDateRemoved(individualSegmentMembership::getDateRemoved);
				setIndividualId(individualSegmentMembership::getIndividualId);
				setIndividualSegmentId(
					individualSegmentMembership::getIndividualSegmentId);
				setStatus(
					() -> FaroEnumUtil.create(
						Status::create,
						StringUtil.toUpperCase(
							individualSegmentMembership.getStatus())));
			}
		};
	}

}