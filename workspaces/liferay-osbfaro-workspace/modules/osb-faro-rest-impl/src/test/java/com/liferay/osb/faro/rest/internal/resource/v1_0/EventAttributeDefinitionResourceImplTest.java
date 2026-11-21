/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.EventAttributeDefinition;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventAttributeDefinitionsPageResponse;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @author Caio Pinheiro
 */
public class EventAttributeDefinitionResourceImplTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		Mockito.when(
			_faroGraphQLClient.execute(
				Mockito.eq(
					GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
						class),
				Mockito.eq(_faroProject),
				Mockito.eq("getWorkspaceGroupEventAttributeDefinitionsPage"),
				Mockito.anyMap())
		).thenReturn(
			_getWorkspaceGroupEventAttributeDefinitionsPageResponse
		);

		Mockito.when(
			_eventAttributeDefinitionDTOConverter.toDTO(
				Mockito.any(DTOConverterContext.class),
				Mockito.eq(_responseEventAttributeDefinition))
		).thenReturn(
			_eventAttributeDefinition
		);

		_eventAttributeDefinitionResourceImpl.setContextAcceptLanguage(
			Mockito.mock(AcceptLanguage.class));

		ReflectionTestUtil.setFieldValue(
			_eventAttributeDefinitionResourceImpl,
			"_eventAttributeDefinitionDTOConverter",
			_eventAttributeDefinitionDTOConverter);
		ReflectionTestUtil.setFieldValue(
			_eventAttributeDefinitionResourceImpl, "_faroGraphQLClient",
			_faroGraphQLClient);
		ReflectionTestUtil.setFieldValue(
			_eventAttributeDefinitionResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPage()
		throws Exception {

		int total = RandomTestUtil.randomInt();

		_getWorkspaceGroupEventAttributeDefinitionsPageResponse.
			setEventAttributeDefinitionBag(
				_createEventAttributeDefinitionBag(total));

		String attributeType = RandomTestUtil.randomString();
		String eventDefinitionId = RandomTestUtil.randomString();
		String fieldName = RandomTestUtil.randomString();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String search = RandomTestUtil.randomString();

		Page<EventAttributeDefinition> eventAttributeDefinitionsPage =
			_eventAttributeDefinitionResourceImpl.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					_GROUP_ID, attributeType, eventDefinitionId, search,
					Pagination.of(page, pageSize),
					new Sort[] {new Sort(fieldName, Sort.STRING_TYPE, true)});

		Assert.assertEquals(
			List.of(_eventAttributeDefinition),
			eventAttributeDefinitionsPage.getItems());
		Assert.assertEquals(
			total, eventAttributeDefinitionsPage.getTotalCount());

		Map<String, Object> variables = _getVariables();

		Assert.assertEquals(
			eventDefinitionId, variables.get("eventDefinitionId"));
		Assert.assertEquals(search, variables.get("keyword"));
		Assert.assertEquals(page - 1, variables.get("page"));
		Assert.assertEquals(pageSize, variables.get("size"));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"column", fieldName
			).put(
				"type", "DESC"
			).build(),
			variables.get("sort"));
		Assert.assertEquals(attributeType, variables.get("type"));
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithoutEventAttributeDefinitionBag()
		throws Exception {

		Page<EventAttributeDefinition> eventAttributeDefinitionsPage =
			_eventAttributeDefinitionResourceImpl.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					_GROUP_ID, null, null, null,
					Pagination.of(
						RandomTestUtil.randomInt(), RandomTestUtil.randomInt()),
					null);

		Assert.assertEquals(
			Collections.emptyList(), eventAttributeDefinitionsPage.getItems());
		Assert.assertEquals(0, eventAttributeDefinitionsPage.getTotalCount());
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithoutOptionalParameters()
		throws Exception {

		_getWorkspaceGroupEventAttributeDefinitionsPageResponse.
			setEventAttributeDefinitionBag(
				_createEventAttributeDefinitionBag(null));

		Page<EventAttributeDefinition> eventAttributeDefinitionsPage =
			_eventAttributeDefinitionResourceImpl.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					_GROUP_ID, null, null, null, Pagination.of(1, 20), null);

		Assert.assertEquals(0, eventAttributeDefinitionsPage.getTotalCount());

		Map<String, Object> variables = _getVariables();

		Assert.assertNull(variables.get("eventDefinitionId"));
		Assert.assertNull(variables.get("keyword"));
		Assert.assertEquals(0, variables.get("page"));
		Assert.assertEquals(20, variables.get("size"));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"column", "name"
			).put(
				"type", "ASC"
			).build(),
			variables.get("sort"));
		Assert.assertEquals("ALL", variables.get("type"));
	}

	private GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
		EventAttributeDefinitionBag _createEventAttributeDefinitionBag(
			Integer total) {

		GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinitionBag eventAttributeDefinitionBag =
				new GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
					EventAttributeDefinitionBag();

		eventAttributeDefinitionBag.setEventAttributeDefinitions(
			List.of(_responseEventAttributeDefinition));
		eventAttributeDefinitionBag.setTotal(total);

		return eventAttributeDefinitionBag;
	}

	private Map<String, Object> _getVariables() throws Exception {
		ArgumentCaptor<Map<String, Object>> argumentCaptor =
			ArgumentCaptor.forClass(Map.class);

		Mockito.verify(
			_faroGraphQLClient
		).execute(
			Mockito.eq(
				GetWorkspaceGroupEventAttributeDefinitionsPageResponse.class),
			Mockito.eq(_faroProject),
			Mockito.eq("getWorkspaceGroupEventAttributeDefinitionsPage"),
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final EventAttributeDefinition _eventAttributeDefinition =
		new EventAttributeDefinition();
	private final DTOConverter
		<GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
			EventAttributeDefinition,
		 EventAttributeDefinition> _eventAttributeDefinitionDTOConverter =
			Mockito.mock(DTOConverter.class);
	private final EventAttributeDefinitionResourceImpl
		_eventAttributeDefinitionResourceImpl =
			new EventAttributeDefinitionResourceImpl();
	private final FaroGraphQLClient _faroGraphQLClient = Mockito.mock(
		FaroGraphQLClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final GetWorkspaceGroupEventAttributeDefinitionsPageResponse
		_getWorkspaceGroupEventAttributeDefinitionsPageResponse =
			new GetWorkspaceGroupEventAttributeDefinitionsPageResponse();
	private final GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
		EventAttributeDefinition _responseEventAttributeDefinition =
			new GetWorkspaceGroupEventAttributeDefinitionsPageResponse.
				EventAttributeDefinition();

}