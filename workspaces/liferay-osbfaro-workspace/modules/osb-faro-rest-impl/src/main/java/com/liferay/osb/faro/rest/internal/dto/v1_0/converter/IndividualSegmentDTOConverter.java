/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.internal.dto.v1_0.util.FaroEnumUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.IndividualSegment",
	service = DTOConverter.class
)
public class IndividualSegmentDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.IndividualSegment,
		 IndividualSegment> {

	@Override
	public String getContentType() {
		return IndividualSegment.class.getSimpleName();
	}

	@Override
	public IndividualSegment toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.IndividualSegment
			individualSegment) {

		if (individualSegment == null) {
			return null;
		}

		return new IndividualSegment() {
			{
				setActiveIndividualCount(
					individualSegment::getActiveIndividualCount);
				setAnonymousIndividualCount(
					individualSegment::getAnonymousIndividualCount);
				setChannelId(individualSegment::getChannelId);
				setDateCreated(individualSegment::getDateCreated);
				setDateModified(individualSegment::getDateModified);
				setFilter(individualSegment::getFilterString);
				setId(individualSegment::getId);
				setIncludeAnonymousUsers(
					individualSegment::isIncludeAnonymousUsers);
				setIndividualCount(individualSegment::getIndividualCount);
				setKnownIndividualCount(
					individualSegment::getKnownIndividualCount);
				setLastActivityDate(individualSegment::getLastActivityDate);
				setName(individualSegment::getName);
				setSegmentType(
					() -> FaroEnumUtil.create(
						SegmentType::create,
						StringUtil.toUpperCase(
							individualSegment.getSegmentType())));
				setState(
					() -> FaroEnumUtil.create(
						State::create, individualSegment.getState()));
				setStatus(
					() -> FaroEnumUtil.create(
						Status::create, individualSegment.getStatus()));
			}
		};
	}

}