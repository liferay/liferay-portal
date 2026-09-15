/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Leslie Wong
 */
@Component(
	property = "dto.class.name=com.liferay.osb.faro.engine.client.model.IndividualSegmentMembershipChange",
	service = DTOConverter.class
)
public class IndividualSegmentMembershipChangeDTOConverter
	implements DTOConverter
		<com.liferay.osb.faro.engine.client.model.
			IndividualSegmentMembershipChange,
		 IndividualSegmentMembershipChange> {

	@Override
	public String getContentType() {
		return IndividualSegmentMembershipChange.class.getSimpleName();
	}

	@Override
	public IndividualSegmentMembershipChange toDTO(
		DTOConverterContext dtoConverterContext,
		com.liferay.osb.faro.engine.client.model.
			IndividualSegmentMembershipChange
				individualSegmentMembershipChange) {

		if (individualSegmentMembershipChange == null) {
			return null;
		}

		return new IndividualSegmentMembershipChange() {
			{
				setDateChanged(
					individualSegmentMembershipChange::getDateChanged);
				setDateFirst(individualSegmentMembershipChange::getDateFirst);
				setId(individualSegmentMembershipChange::getId);
				setIndividualEmail(
					individualSegmentMembershipChange::getIndividualEmail);
				setIndividualId(
					individualSegmentMembershipChange::getIndividualId);
				setIndividualName(
					individualSegmentMembershipChange::getIndividualName);
				setIndividualSegmentId(
					individualSegmentMembershipChange::getIndividualSegmentId);
				setOperation(individualSegmentMembershipChange::getOperation);
			}
		};
	}

}