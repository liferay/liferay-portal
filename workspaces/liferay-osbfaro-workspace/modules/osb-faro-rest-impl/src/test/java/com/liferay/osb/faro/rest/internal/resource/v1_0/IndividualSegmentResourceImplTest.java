/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.Results;
import com.liferay.osb.faro.model.FaroChannel;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.internal.helper.IndividualSegmentHelper;
import com.liferay.osb.faro.service.FaroChannelLocalService;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Pagination;

import jakarta.ws.rs.ForbiddenException;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class IndividualSegmentResourceImplTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProject.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		com.liferay.osb.faro.engine.client.model.IndividualSegment
			individualSegment =
				new com.liferay.osb.faro.engine.client.model.
					IndividualSegment();

		Mockito.when(
			_individualSegmentDTOConverter.toDTO(
				Mockito.any(DTOConverterContext.class),
				Mockito.eq(individualSegment))
		).thenReturn(
			_individualSegment
		);

		Mockito.when(
			_individualSegmentHelper.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_ALL_USERS)
		).thenReturn(
			individualSegment
		);

		Mockito.when(
			_individualSegmentHelper.getIndividualSegment(
				_faroProject, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS)
		).thenThrow(
			new ForbiddenException()
		);

		ReflectionTestUtil.setFieldValue(
			_individualSegmentResourceImpl, "_contactsEngineClient",
			_contactsEngineClient);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentResourceImpl, "_faroChannelLocalService",
			_faroChannelLocalService);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentResourceImpl, "_individualSegmentDTOConverter",
			_individualSegmentDTOConverter);
		ReflectionTestUtil.setFieldValue(
			_individualSegmentResourceImpl, "_individualSegmentHelper",
			_individualSegmentHelper);

		_individualSegmentResourceImpl.setContextAcceptLanguage(
			Mockito.mock(AcceptLanguage.class));
	}

	@Test
	public void testGetWorkspaceGroupIndividualIndividualSegmentsPage()
		throws Exception {

		Mockito.when(
			_contactsEngineClient.getIndividualIndividualSegments(
				Mockito.eq(_faroProject), Mockito.any(), Mockito.anyList(),
				Mockito.eq(_INDIVIDUAL_ID), Mockito.any(), Mockito.any(),
				Mockito.anyInt(), Mockito.anyInt(), Mockito.any())
		).thenReturn(
			new Results<>(Collections.emptyList(), 0)
		);

		FaroChannel accessibleFaroChannel = Mockito.mock(FaroChannel.class);

		Mockito.when(
			accessibleFaroChannel.getChannelId()
		).thenReturn(
			_CHANNEL_ID_ALL_USERS
		);

		FaroChannel inaccessibleFaroChannel = Mockito.mock(FaroChannel.class);

		Mockito.when(
			inaccessibleFaroChannel.getChannelId()
		).thenReturn(
			_CHANNEL_ID_SELECT_USERS
		);

		Mockito.doReturn(
			List.of(accessibleFaroChannel, inaccessibleFaroChannel)
		).when(
			_faroChannelLocalService
		).dslQuery(
			Mockito.any(DSLQuery.class)
		);

		Mockito.when(
			_faroChannelLocalService.search(
				_GROUP_ID, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)
		).thenReturn(
			List.of(accessibleFaroChannel)
		);

		_individualSegmentResourceImpl.
			getWorkspaceGroupIndividualIndividualSegmentsPage(
				_GROUP_ID, _INDIVIDUAL_ID, _CHANNEL_ID_ALL_USERS, null, null,
				Pagination.of(1, 20));

		Mockito.verify(
			_contactsEngineClient
		).getIndividualIndividualSegments(
			_faroProject, _CHANNEL_ID_ALL_USERS, Collections.emptyList(),
			_INDIVIDUAL_ID, null, null, 1, 20, null
		);

		_individualSegmentResourceImpl.
			getWorkspaceGroupIndividualIndividualSegmentsPage(
				_GROUP_ID, _INDIVIDUAL_ID, null, null, null,
				Pagination.of(1, 20));

		Mockito.verify(
			_contactsEngineClient
		).getIndividualIndividualSegments(
			_faroProject, null, List.of(_CHANNEL_ID_SELECT_USERS),
			_INDIVIDUAL_ID, null, null, 1, 20, null
		);

		Mockito.verify(
			_faroChannelLocalService
		).dslQuery(
			Mockito.any(DSLQuery.class)
		);

		Mockito.verify(
			_faroChannelLocalService
		).search(
			_GROUP_ID, null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null
		);
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegment() throws Exception {
		Assert.assertSame(
			_individualSegment,
			_individualSegmentResourceImpl.getWorkspaceGroupIndividualSegment(
				_GROUP_ID, _INDIVIDUAL_SEGMENT_ID_ALL_USERS));
	}

	@Test
	public void testGetWorkspaceGroupIndividualSegmentWithForbiddenException()
		throws Exception {

		try {
			_individualSegmentResourceImpl.getWorkspaceGroupIndividualSegment(
				_GROUP_ID, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS);

			Assert.fail();
		}
		catch (ForbiddenException forbiddenException) {
			Mockito.verify(
				_individualSegmentDTOConverter, Mockito.never()
			).toDTO(
				Mockito.any(DTOConverterContext.class), Mockito.any()
			);
		}
	}

	private static final String _CHANNEL_ID_ALL_USERS =
		RandomTestUtil.randomString();

	private static final String _CHANNEL_ID_SELECT_USERS =
		RandomTestUtil.randomString();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _INDIVIDUAL_ID = RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_ALL_USERS =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_SELECT_USERS =
		RandomTestUtil.randomString();

	private final ContactsEngineClient _contactsEngineClient = Mockito.mock(
		ContactsEngineClient.class);
	private final FaroChannelLocalService _faroChannelLocalService =
		Mockito.mock(FaroChannelLocalService.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final IndividualSegment _individualSegment =
		new IndividualSegment();
	private final DTOConverter
		<com.liferay.osb.faro.engine.client.model.IndividualSegment,
		 IndividualSegment> _individualSegmentDTOConverter = Mockito.mock(
			DTOConverter.class);
	private final IndividualSegmentHelper _individualSegmentHelper =
		Mockito.mock(IndividualSegmentHelper.class);
	private final IndividualSegmentResourceImpl _individualSegmentResourceImpl =
		new IndividualSegmentResourceImpl();

}