/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.dto.v1_0.converter;

import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Leslie Wong
 */
public class IndividualSegmentMembershipChangeDTOConverterTest {

	@Test
	public void testToDTO() {
		Date dateChanged = RandomTestUtil.nextDate();
		Date dateFirst = RandomTestUtil.nextDate();
		String id = RandomTestUtil.randomString();
		String individualEmail = RandomTestUtil.randomString();
		String individualId = RandomTestUtil.randomString();
		String individualName = RandomTestUtil.randomString();
		String individualSegmentId = RandomTestUtil.randomString();

		IndividualSegmentMembershipChange individualSegmentMembershipChange =
			_toDTO(
				dateChanged, dateFirst, id, individualEmail, individualId,
				individualName, individualSegmentId);

		Assert.assertEquals(
			dateChanged, individualSegmentMembershipChange.getDateChanged());
		Assert.assertEquals(
			dateFirst, individualSegmentMembershipChange.getDateFirst());
		Assert.assertEquals(id, individualSegmentMembershipChange.getId());
		Assert.assertEquals(
			individualEmail,
			individualSegmentMembershipChange.getIndividualEmail());
		Assert.assertEquals(
			individualId, individualSegmentMembershipChange.getIndividualId());
		Assert.assertEquals(
			individualName,
			individualSegmentMembershipChange.getIndividualName());
		Assert.assertEquals(
			individualSegmentId,
			individualSegmentMembershipChange.getIndividualSegmentId());

		Assert.assertNull(
			_individualSegmentMembershipChangeDTOConverter.toDTO(
				new FaroDTOConverterContext(false, null, null), null));
	}

	private IndividualSegmentMembershipChange _toDTO(
		Date dateChanged, Date dateFirst, String id, String individualEmail,
		String individualId, String individualName,
		String individualSegmentId) {

		com.liferay.osb.faro.engine.client.model.
			IndividualSegmentMembershipChange
				individualSegmentMembershipChange =
					new com.liferay.osb.faro.engine.client.model.
						IndividualSegmentMembershipChange();

		individualSegmentMembershipChange.setDateChanged(dateChanged);
		individualSegmentMembershipChange.setDateFirst(dateFirst);
		individualSegmentMembershipChange.setId(id);
		individualSegmentMembershipChange.setIndividualEmail(individualEmail);
		individualSegmentMembershipChange.setIndividualId(individualId);
		individualSegmentMembershipChange.setIndividualName(individualName);
		individualSegmentMembershipChange.setIndividualSegmentId(
			individualSegmentId);

		return _individualSegmentMembershipChangeDTOConverter.toDTO(
			new FaroDTOConverterContext(false, id, null),
			individualSegmentMembershipChange);
	}

	private final IndividualSegmentMembershipChangeDTOConverter
		_individualSegmentMembershipChangeDTOConverter =
			new IndividualSegmentMembershipChangeDTOConverter();

}