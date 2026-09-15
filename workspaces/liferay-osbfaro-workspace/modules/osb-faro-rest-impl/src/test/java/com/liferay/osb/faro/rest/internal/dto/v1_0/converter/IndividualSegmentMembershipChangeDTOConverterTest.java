/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class IndividualSegmentMembershipChangeDTOConverterTest {

	@Test
	public void testToDTOMapsEveryField() {
		com.liferay.osb.faro.engine.client.model.
			IndividualSegmentMembershipChange
				individualSegmentMembershipChange =
					new com.liferay.osb.faro.engine.client.model.
						IndividualSegmentMembershipChange();

		Date dateChanged = new Date(1725000000000L);
		Date dateFirst = new Date(1700000000000L);

		individualSegmentMembershipChange.setDateChanged(dateChanged);
		individualSegmentMembershipChange.setDateFirst(dateFirst);
		individualSegmentMembershipChange.setId("change-1");
		individualSegmentMembershipChange.setIndividualEmail(
			"jane.doe@acme.example");
		individualSegmentMembershipChange.setIndividualId("individual-1");
		individualSegmentMembershipChange.setIndividualName("Jane Doe");
		individualSegmentMembershipChange.setIndividualSegmentId("segment-1");
		individualSegmentMembershipChange.setOperation("REMOVED");

		IndividualSegmentMembershipChange individualSegmentMembershipChangeDTO =
			_individualSegmentMembershipChangeDTOConverter.toDTO(
				new FaroDTOConverterContext(false, "change-1", null),
				individualSegmentMembershipChange);

		Assert.assertEquals(
			dateChanged, individualSegmentMembershipChangeDTO.getDateChanged());
		Assert.assertEquals(
			dateFirst, individualSegmentMembershipChangeDTO.getDateFirst());
		Assert.assertEquals(
			"change-1", individualSegmentMembershipChangeDTO.getId());
		Assert.assertEquals(
			"jane.doe@acme.example",
			individualSegmentMembershipChangeDTO.getIndividualEmail());
		Assert.assertEquals(
			"individual-1",
			individualSegmentMembershipChangeDTO.getIndividualId());
		Assert.assertEquals(
			"Jane Doe",
			individualSegmentMembershipChangeDTO.getIndividualName());
		Assert.assertEquals(
			"segment-1",
			individualSegmentMembershipChangeDTO.getIndividualSegmentId());
		Assert.assertEquals(
			"REMOVED", individualSegmentMembershipChangeDTO.getOperation());
	}

	@Test
	public void testToDTOReturnsNullForNullChange() {
		Assert.assertNull(
			_individualSegmentMembershipChangeDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private final IndividualSegmentMembershipChangeDTOConverter
		_individualSegmentMembershipChangeDTOConverter =
			new IndividualSegmentMembershipChangeDTOConverter();

}