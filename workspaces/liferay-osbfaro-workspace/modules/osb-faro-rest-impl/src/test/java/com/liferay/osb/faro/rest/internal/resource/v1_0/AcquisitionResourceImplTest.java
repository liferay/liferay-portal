/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.Acquisition;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupChannelAcquisitionsPageResponse;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Caio Pinheiro
 */
public class AcquisitionResourceImplTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_acquisitionDTOConverter.toDTO(
				Mockito.any(DTOConverterContext.class),
				Mockito.eq(_composition))
		).thenReturn(
			_acquisition
		);

		Mockito.when(
			_faroGraphQLClient.execute(
				Mockito.eq(
					GetWorkspaceGroupChannelAcquisitionsPageResponse.class),
				Mockito.eq(_faroProject),
				Mockito.eq("getWorkspaceGroupChannelAcquisitionsPage"),
				Mockito.anyMap())
		).thenAnswer(
			invocation -> {
				_variables = invocation.getArgument(3);

				return _getWorkspaceGroupChannelAcquisitionsPageResponse;
			}
		);

		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		_acquisitionResourceImpl.setContextAcceptLanguage(
			Mockito.mock(AcceptLanguage.class));

		ReflectionTestUtil.setFieldValue(
			_acquisitionResourceImpl, "_acquisitionDTOConverter",
			_acquisitionDTOConverter);
		ReflectionTestUtil.setFieldValue(
			_acquisitionResourceImpl, "_faroGraphQLClient", _faroGraphQLClient);
		ReflectionTestUtil.setFieldValue(
			_acquisitionResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetWorkspaceGroupChannelAcquisitionsPage()
		throws Exception {

		String accountId = RandomTestUtil.randomString();
		String channelId = RandomTestUtil.randomString();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String rangeEnd = RandomTestUtil.randomString();
		String rangeStart = RandomTestUtil.randomString();
		String segmentId = RandomTestUtil.randomString();
		long totalCount = RandomTestUtil.randomLong();
		String type = RandomTestUtil.randomString();

		_getWorkspaceGroupChannelAcquisitionsPageResponse.setCompositionBag(
			_createCompositionBag(totalCount));

		Page<Acquisition> acquisitionsPage =
			_acquisitionResourceImpl.getWorkspaceGroupChannelAcquisitionsPage(
				_GROUP_ID, channelId, accountId, rangeEnd, "LAST_7_DAYS",
				rangeStart, segmentId, type, Pagination.of(page, pageSize));

		Assert.assertEquals(List.of(_acquisition), acquisitionsPage.getItems());
		Assert.assertEquals(totalCount, acquisitionsPage.getTotalCount());

		Assert.assertEquals(accountId, _variables.get("accountId"));
		Assert.assertEquals(type, _variables.get("acquisitionType"));
		Assert.assertEquals(channelId, _variables.get("channelId"));
		Assert.assertEquals(rangeEnd, _variables.get("rangeEnd"));
		Assert.assertEquals(7, _variables.get("rangeKey"));
		Assert.assertEquals(rangeStart, _variables.get("rangeStart"));
		Assert.assertEquals(segmentId, _variables.get("segmentId"));
		Assert.assertEquals(pageSize, _variables.get("size"));
		Assert.assertEquals(page - 1, _variables.get("start"));
	}

	@Test
	public void testGetWorkspaceGroupChannelAcquisitionsPageWithoutCompositionBag()
		throws Exception {

		Page<Acquisition> acquisitionsPage =
			_acquisitionResourceImpl.getWorkspaceGroupChannelAcquisitionsPage(
				_GROUP_ID, RandomTestUtil.randomString(), null, null, null,
				null, null, null, Pagination.of(1, 20));

		Assert.assertEquals(List.of(), (List<?>)acquisitionsPage.getItems());
		Assert.assertEquals(0, acquisitionsPage.getTotalCount());
	}

	@Test
	public void testGetWorkspaceGroupChannelAcquisitionsPageWithoutOptionalParameters()
		throws Exception {

		_getWorkspaceGroupChannelAcquisitionsPageResponse.setCompositionBag(
			_createCompositionBag(null));

		Page<Acquisition> acquisitionsPage =
			_acquisitionResourceImpl.getWorkspaceGroupChannelAcquisitionsPage(
				_GROUP_ID, RandomTestUtil.randomString(), null, null, null,
				null, null, null, Pagination.of(1, 20));

		Assert.assertEquals(0, acquisitionsPage.getTotalCount());

		Assert.assertEquals("CHANNEL", _variables.get("acquisitionType"));
		Assert.assertNull(_variables.get("rangeKey"));
		Assert.assertEquals(0, _variables.get("start"));
	}

	private GetWorkspaceGroupChannelAcquisitionsPageResponse.CompositionBag
		_createCompositionBag(Long totalCount) {

		GetWorkspaceGroupChannelAcquisitionsPageResponse.CompositionBag
			compositionBag =
				new GetWorkspaceGroupChannelAcquisitionsPageResponse.
					CompositionBag();

		compositionBag.setCompositions(List.of(_composition));
		compositionBag.setTotalCount(totalCount);

		return compositionBag;
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final Acquisition _acquisition = new Acquisition();
	private final DTOConverter
		<GetWorkspaceGroupChannelAcquisitionsPageResponse.Composition,
		 Acquisition> _acquisitionDTOConverter = Mockito.mock(
			DTOConverter.class);
	private final AcquisitionResourceImpl _acquisitionResourceImpl =
		new AcquisitionResourceImpl();
	private final GetWorkspaceGroupChannelAcquisitionsPageResponse.Composition
		_composition =
			new GetWorkspaceGroupChannelAcquisitionsPageResponse.Composition();
	private final FaroGraphQLClient _faroGraphQLClient = Mockito.mock(
		FaroGraphQLClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final GetWorkspaceGroupChannelAcquisitionsPageResponse
		_getWorkspaceGroupChannelAcquisitionsPageResponse =
			new GetWorkspaceGroupChannelAcquisitionsPageResponse();
	private Map<String, Object> _variables;

}