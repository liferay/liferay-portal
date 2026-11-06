/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.internal.helper.IndividualSegmentHelper;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import jakarta.ws.rs.ForbiddenException;

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
	public void testGetWorkspaceGroupIndividualSegment() throws Exception {
		Assert.assertSame(
			_individualSegment,
			_individualSegmentResourceImpl.getWorkspaceGroupIndividualSegment(
				_GROUP_ID, _INDIVIDUAL_SEGMENT_ID_ALL_USERS));

		try {
			_individualSegmentResourceImpl.getWorkspaceGroupIndividualSegment(
				_GROUP_ID, _INDIVIDUAL_SEGMENT_ID_SELECT_USERS);

			Assert.fail();
		}
		catch (ForbiddenException forbiddenException) {
			Mockito.verify(
				_individualSegmentDTOConverter, Mockito.times(1)
			).toDTO(
				Mockito.any(DTOConverterContext.class), Mockito.any()
			);
		}
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final String _INDIVIDUAL_SEGMENT_ID_ALL_USERS =
		RandomTestUtil.randomString();

	private static final String _INDIVIDUAL_SEGMENT_ID_SELECT_USERS =
		RandomTestUtil.randomString();

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