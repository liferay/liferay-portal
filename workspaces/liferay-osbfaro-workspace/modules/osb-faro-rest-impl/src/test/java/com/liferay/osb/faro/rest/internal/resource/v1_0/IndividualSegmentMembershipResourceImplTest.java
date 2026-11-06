/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegmentMembership;
import com.liferay.osb.faro.rest.internal.helper.IndividualSegmentHelper;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.ForbiddenException;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class IndividualSegmentMembershipResourceImplTest {

	@Before
	public void setUp() throws Exception {
		com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership
			individualSegmentMembership =
				new com.liferay.osb.faro.engine.client.model.
					IndividualSegmentMembership();

		Mockito.when(
			_contactsEngineClient.getIndividualSegmentMemberships(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_ALL_USERS, 1, 20, null)
		).thenReturn(
			new Results<>(List.of(individualSegmentMembership), 1)
		);

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

		Mockito.when(
			_individualSegmentMembershipDTOConverter.toDTO(
				Mockito.any(DTOConverterContext.class),
				Mockito.eq(individualSegmentMembership))
		).thenReturn(
			_individualSegmentMembership
		);

		ReflectionTestUtil.setFieldValue(
			_individualSegmentMembershipResourceImpl, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentMembershipResourceImpl,
			"_faroProjectLocalService", _faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentMembershipResourceImpl,
			"_individualSegmentHelper", _individualSegmentHelper);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentMembershipResourceImpl,
			"_individualSegmentMembershipDTOConverter",
			_individualSegmentMembershipDTOConverter);

		_individualSegmentMembershipResourceImpl.setContextAcceptLanguage(
			Mockito.mock(AcceptLanguage.class));
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipsPage()
		throws Exception {

		Page<IndividualSegmentMembership> individualSegmentMembershipsPage =
			_individualSegmentMembershipResourceImpl.
				getWorkspaceGroupIndividualSegmentMembershipsPage(
					_GROUP_ID, _INDIVIDUAL_SEGMENT_ID_ALL_USERS,
					Pagination.of(1, 20), null);

		Assert.assertEquals(
			List.of(_individualSegmentMembership),
			individualSegmentMembershipsPage.getItems());
		Assert.assertEquals(
			1, individualSegmentMembershipsPage.getTotalCount());

		Mockito.verify(
			_individualSegmentHelper
		).getIndividualSegment(
			_faroProject, _INDIVIDUAL_SEGMENT_ID_ALL_USERS
		);
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentMembershipsPageWithForbiddenException()
		throws Exception {

		try {
			_individualSegmentMembershipResourceImpl.
				getWorkspaceGroupIndividualSegmentMembershipsPage(
					_GROUP_ID, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS,
					Pagination.of(1, 20), null);

			Assert.fail();
		}
		catch (ForbiddenException forbiddenException) {
			Mockito.verify(
				_contactsEngineClient, Mockito.never()
			).getIndividualSegmentMemberships(
				Mockito.any(), Mockito.anyString(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.any()
			);
		}
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _INDIVIDUAL_SEGMENT_ID_ALL_USERS =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_SELECT_USERS =
		RandomTestUtil.randomString();

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final IndividualSegmentHelper _individualSegmentHelper =
		Mockito.mock(IndividualSegmentHelper.class);
	private final IndividualSegmentMembership _individualSegmentMembership =
		new IndividualSegmentMembership();
	private final DTOConverter
		<com.liferay.osb.faro.engine.client.model.IndividualSegmentMembership,
		 IndividualSegmentMembership> _individualSegmentMembershipDTOConverter =
			Mockito.mock(DTOConverter.class);
	private final IndividualSegmentMembershipResourceImpl
		_individualSegmentMembershipResourceImpl =
			new IndividualSegmentMembershipResourceImpl();

}