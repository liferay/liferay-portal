/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.VisitorMetric;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelVisitorMetricResponse;
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
public class VisitorMetricResourceImplTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroGraphQLClient.execute(
				Mockito.eq(GetWorkspaceGroupChannelVisitorMetricResponse.class),
				Mockito.eq(_faroProject),
				Mockito.eq("getWorkspaceGroupChannelVisitorMetric"),
				Mockito.anyMap())
		).thenAnswer(
			invocation -> {
				_variables = invocation.getArgument(3);

				return _getWorkspaceGroupChannelVisitorMetricResponse;
			}
		);

		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		Mockito.when(
			_visitorMetricDTOConverter.toDTO(
				Mockito.any(DTOConverterContext.class), Mockito.any())
		).thenReturn(
			_visitorMetric
		);

		_visitorMetricResourceImpl.setContextAcceptLanguage(
			Mockito.mock(AcceptLanguage.class));

		ReflectionTestUtil.setFieldValue(
			_visitorMetricResourceImpl, "_faroGraphQLClient",
			_faroGraphQLClient);
		ReflectionTestUtil.setFieldValue(
			_visitorMetricResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_visitorMetricResourceImpl, "_visitorMetricDTOConverter",
			_visitorMetricDTOConverter);
	}

	@Test
	public void testGetWorkspaceGroupChannelVisitorMetric() throws Exception {
		String channelId = RandomTestUtil.randomString();
		String segmentId = RandomTestUtil.randomString();

		GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric
			responseVisitorMetric =
				new GetWorkspaceGroupChannelVisitorMetricResponse.
					VisitorMetric();

		_getWorkspaceGroupChannelVisitorMetricResponse.setVisitorMetric(
			responseVisitorMetric);

		Assert.assertSame(
			_visitorMetric,
			_visitorMetricResourceImpl.getWorkspaceGroupChannelVisitorMetric(
				_GROUP_ID, channelId, "LAST_7_DAYS", segmentId));

		Assert.assertSame(responseVisitorMetric, _getResponseVisitorMetric());

		Assert.assertEquals(channelId, _variables.get("channelId"));
		Assert.assertEquals(7, _variables.get("rangeKey"));
		Assert.assertEquals(segmentId, _variables.get("segmentId"));
	}

	@Test
	public void testGetWorkspaceGroupChannelVisitorMetricWithoutVisitorMetric()
		throws Exception {

		Assert.assertSame(
			_visitorMetric,
			_visitorMetricResourceImpl.getWorkspaceGroupChannelVisitorMetric(
				_GROUP_ID, RandomTestUtil.randomString(), null, null));

		GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric
			responseVisitorMetric = _getResponseVisitorMetric();

		Assert.assertNull(responseVisitorMetric.getAverageSessionDuration());
		Assert.assertNull(responseVisitorMetric.getKnownVisitors());
		Assert.assertNull(responseVisitorMetric.getTotalSessionDuration());
		Assert.assertNull(responseVisitorMetric.getVisitors());
	}

	private GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric
			_getResponseVisitorMetric()
		throws Exception {

		ArgumentCaptor
			<GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric>
				argumentCaptor = ArgumentCaptor.forClass(
					GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric.
						class);

		Mockito.verify(
			_visitorMetricDTOConverter
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
	private final GetWorkspaceGroupChannelVisitorMetricResponse
		_getWorkspaceGroupChannelVisitorMetricResponse =
			new GetWorkspaceGroupChannelVisitorMetricResponse();
	private Map<String, Object> _variables;
	private final VisitorMetric _visitorMetric = new VisitorMetric();
	private final DTOConverter
		<GetWorkspaceGroupChannelVisitorMetricResponse.VisitorMetric,
		 VisitorMetric> _visitorMetricDTOConverter = Mockito.mock(
			DTOConverter.class);
	private final VisitorMetricResourceImpl _visitorMetricResourceImpl =
		new VisitorMetricResourceImpl();

}