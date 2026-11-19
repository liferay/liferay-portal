/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.VisitFrequencyMetric;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitFrequencyMetricResponse;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Caio Pinheiro
 */
public class VisitFrequencyMetricResourceImplTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroGraphQLClient.execute(
				Mockito.eq(
					GetWorkspaceGroupChannelVisitFrequencyMetricResponse.class),
				Mockito.eq(_faroProject),
				Mockito.eq("getWorkspaceGroupChannelVisitFrequencyMetric"),
				Mockito.anyMap())
		).thenAnswer(
			invocation -> {
				_variables = invocation.getArgument(3);

				return _getWorkspaceGroupChannelVisitFrequencyMetricResponse;
			}
		);

		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		Mockito.when(
			_visitFrequencyMetricDTOConverter.toDTO(
				Mockito.any(DTOConverterContext.class), Mockito.any())
		).thenReturn(
			_visitFrequencyMetric
		);

		_visitFrequencyMetricResourceImpl.setContextAcceptLanguage(
			Mockito.mock(AcceptLanguage.class));

		ReflectionTestUtil.setFieldValue(
			_visitFrequencyMetricResourceImpl, "_faroGraphQLClient",
			_faroGraphQLClient);
		ReflectionTestUtil.setFieldValue(
			_visitFrequencyMetricResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_visitFrequencyMetricResourceImpl,
			"_visitFrequencyMetricDTOConverter",
			_visitFrequencyMetricDTOConverter);
	}

	@Test
	public void testGetWorkspaceGroupChannelVisitFrequencyMetric()
		throws Exception {

		String channelId = RandomTestUtil.randomString();
		String rangeEnd = RandomTestUtil.randomString();
		String rangeStart = RandomTestUtil.randomString();

		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
			visitFrequencyBag =
				new GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
					VisitFrequencyBag();

		_getWorkspaceGroupChannelVisitFrequencyMetricResponse.
			setVisitFrequencyBag(visitFrequencyBag);

		Assert.assertSame(
			_visitFrequencyMetric,
			_visitFrequencyMetricResourceImpl.
				getWorkspaceGroupChannelVisitFrequencyMetric(
					_GROUP_ID, channelId, rangeEnd, "LAST_7_DAYS", rangeStart));

		Assert.assertSame(visitFrequencyBag, _getVisitFrequencyBag());

		Assert.assertEquals(channelId, _variables.get("channelId"));
		Assert.assertEquals(rangeEnd, _variables.get("rangeEnd"));
		Assert.assertEquals(7, _variables.get("rangeKey"));
		Assert.assertEquals(rangeStart, _variables.get("rangeStart"));
	}

	@Test
	public void testGetWorkspaceGroupChannelVisitFrequencyMetricWithoutVisitFrequencyBag()
		throws Exception {

		Assert.assertSame(
			_visitFrequencyMetric,
			_visitFrequencyMetricResourceImpl.
				getWorkspaceGroupChannelVisitFrequencyMetric(
					_GROUP_ID, RandomTestUtil.randomString(), null, null,
					null));

		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
			visitFrequencyBag = _getVisitFrequencyBag();

		Assert.assertNull(visitFrequencyBag.getTotalCount());
		Assert.assertNull(visitFrequencyBag.getVisitFrequency());
	}

	private
		GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag
				_getVisitFrequencyBag()
			throws Exception {

		ArgumentCaptor
			<GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
				VisitFrequencyBag> argumentCaptor = ArgumentCaptor.forClass(
					GetWorkspaceGroupChannelVisitFrequencyMetricResponse.
						VisitFrequencyBag.class);

		Mockito.verify(
			_visitFrequencyMetricDTOConverter
		).toDTO(
			Mockito.any(DTOConverterContext.class), argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final FaroGraphQLClient _faroGraphQLClient = Mockito.mock(
		FaroGraphQLClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final GetWorkspaceGroupChannelVisitFrequencyMetricResponse
		_getWorkspaceGroupChannelVisitFrequencyMetricResponse =
			new GetWorkspaceGroupChannelVisitFrequencyMetricResponse();
	private Map<String, Object> _variables;
	private final VisitFrequencyMetric _visitFrequencyMetric =
		new VisitFrequencyMetric();
	private final DTOConverter
		<GetWorkspaceGroupChannelVisitFrequencyMetricResponse.VisitFrequencyBag,
		 VisitFrequencyMetric> _visitFrequencyMetricDTOConverter = Mockito.mock(
			DTOConverter.class);
	private final VisitFrequencyMetricResourceImpl
		_visitFrequencyMetricResourceImpl =
			new VisitFrequencyMetricResourceImpl();

}