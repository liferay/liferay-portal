/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.internal.helper.IndividualSegmentHelper;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;

import jakarta.ws.rs.ForbiddenException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class IndividualSegmentMembershipChangeMetricResourceImplTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		Mockito.when(
			_individualSegmentHelper.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS)
		).thenThrow(
			new ForbiddenException()
		);

		ReflectionTestUtil.setFieldValue(
			_individualSegmentMembershipChangeMetricResourceImpl,
			"_contactsEngineClient", _contactsEngineClient);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentMembershipChangeMetricResourceImpl,
			"_faroProjectLocalService", _faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentMembershipChangeMetricResourceImpl,
			"_individualSegmentHelper", _individualSegmentHelper);
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipChangeMetricWithForbiddenException()
		throws Exception {

		try {
			_individualSegmentMembershipChangeMetricResourceImpl.
				getWorkspaceGroupIndividualSegmentMembershipChangeMetric(
					_GROUP_ID, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS, null);

			Assert.fail();
		}
		catch (ForbiddenException forbiddenException) {
			Mockito.verify(
				_contactsEngineClient, Mockito.never()
			).getIndividualSegmentMembershipChangeAggregations(
				Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyInt()
			);
		}
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _INDIVIDUAL_SEGMENT_ID_SELECT_USERS =
		RandomTestUtil.randomString();

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final IndividualSegmentHelper _individualSegmentHelper =
		Mockito.mock(IndividualSegmentHelper.class);
	private final IndividualSegmentMembershipChangeMetricResourceImpl
		_individualSegmentMembershipChangeMetricResourceImpl =
			new IndividualSegmentMembershipChangeMetricResourceImpl();

}