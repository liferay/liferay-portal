/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class IndividualSegmentMembershipChangeDTOConverterTest {

	@Test
	public void testToDTO() {
		com.liferay.osb.faro.engine.client.model.
			IndividualSegmentMembershipChange
				engineClientIndividualSegmentMembershipChange =
					new com.liferay.osb.faro.engine.client.model.
						IndividualSegmentMembershipChange();

		engineClientIndividualSegmentMembershipChange.setDateChanged(
			RandomTestUtil.nextDate());
		engineClientIndividualSegmentMembershipChange.setDateFirst(
			RandomTestUtil.nextDate());
		engineClientIndividualSegmentMembershipChange.setId(
			RandomTestUtil.randomString());
		engineClientIndividualSegmentMembershipChange.setIndividualEmail(
			RandomTestUtil.randomString());
		engineClientIndividualSegmentMembershipChange.setIndividualId(
			RandomTestUtil.randomString());
		engineClientIndividualSegmentMembershipChange.setIndividualName(
			RandomTestUtil.randomString());
		engineClientIndividualSegmentMembershipChange.setIndividualSegmentId(
			RandomTestUtil.randomString());
		engineClientIndividualSegmentMembershipChange.setOperation(
			RandomTestUtil.randomString());

		IndividualSegmentMembershipChange individualSegmentMembershipChange =
			_individualSegmentMembershipChangeDTOConverter.toDTO(
				new FaroDTOConverterContext(
					false,
					engineClientIndividualSegmentMembershipChange.getId(),
					null),
				engineClientIndividualSegmentMembershipChange);

		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.getDateChanged(),
			individualSegmentMembershipChange.getDateChanged());
		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.getDateFirst(),
			individualSegmentMembershipChange.getDateFirst());
		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.getId(),
			individualSegmentMembershipChange.getId());
		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.getIndividualEmail(),
			individualSegmentMembershipChange.getIndividualEmail());
		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.getIndividualId(),
			individualSegmentMembershipChange.getIndividualId());
		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.getIndividualName(),
			individualSegmentMembershipChange.getIndividualName());
		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.
				getIndividualSegmentId(),
			individualSegmentMembershipChange.getIndividualSegmentId());
		Assert.assertEquals(
			engineClientIndividualSegmentMembershipChange.getOperation(),
			individualSegmentMembershipChange.getOperation());

		Assert.assertNull(
			_individualSegmentMembershipChangeDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private final IndividualSegmentMembershipChangeDTOConverter
		_individualSegmentMembershipChangeDTOConverter =
			new IndividualSegmentMembershipChangeDTOConverter();

}