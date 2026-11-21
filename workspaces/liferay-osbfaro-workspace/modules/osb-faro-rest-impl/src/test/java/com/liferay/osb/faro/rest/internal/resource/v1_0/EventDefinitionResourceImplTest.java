/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.EventDefinition;
import com.liferay.osb.faro.rest.internal.graphql.client.FaroGraphQLClient;
import com.liferay.osb.faro.rest.internal.graphql.dto.GetWorkspaceGroupEventDefinitionsPageResponse;
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
public class EventDefinitionResourceImplTest {

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			_faroProjectLocalService.getFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			_faroProject
		);

		Mockito.when(
			_faroGraphQLClient.execute(
				Mockito.eq(GetWorkspaceGroupEventDefinitionsPageResponse.class),
				Mockito.eq(_faroProject),
				Mockito.eq("getWorkspaceGroupEventDefinitionsPage"),
				Mockito.anyMap())
		).thenReturn(
			_getWorkspaceGroupEventDefinitionsPageResponse
		);

		Mockito.when(
			_eventDefinitionDTOConverter.toDTO(
				Mockito.any(DTOConverterContext.class),
				Mockito.eq(_responseEventDefinition))
		).thenReturn(
			_eventDefinition
		);

		_eventDefinitionResourceImpl.setContextAcceptLanguage(
			Mockito.mock(AcceptLanguage.class));

		ReflectionTestUtil.setFieldValue(
			_eventDefinitionResourceImpl, "_eventDefinitionDTOConverter",
			_eventDefinitionDTOConverter);
		ReflectionTestUtil.setFieldValue(
			_eventDefinitionResourceImpl, "_faroGraphQLClient",
			_faroGraphQLClient);
		ReflectionTestUtil.setFieldValue(
			_eventDefinitionResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
	}

	@Test
	public void testGetWorkspaceGroupEventDefinitionsPage() throws Exception {
		int total = RandomTestUtil.randomInt();

		_getWorkspaceGroupEventDefinitionsPageResponse.setEventDefinitionBag(
			_createEventDefinitionBag(total));

		boolean blocked = RandomTestUtil.randomBoolean();
		String eventType = RandomTestUtil.randomString();
		String fieldName = RandomTestUtil.randomString();
		boolean hidden = RandomTestUtil.randomBoolean();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String search = RandomTestUtil.randomString();

		Page<EventDefinition> eventDefinitionsPage =
			_eventDefinitionResourceImpl.getWorkspaceGroupEventDefinitionsPage(
				_GROUP_ID, blocked, eventType, hidden, search,
				Pagination.of(page, pageSize),
				new Sort[] {new Sort(fieldName, Sort.STRING_TYPE, true)});

		Assert.assertEquals(
			List.of(_eventDefinition), eventDefinitionsPage.getItems());
		Assert.assertEquals(total, eventDefinitionsPage.getTotalCount());

		Map<String, Object> variables = _getVariables();

		Assert.assertEquals(blocked, variables.get("blocked"));
		Assert.assertEquals(eventType, variables.get("eventType"));
		Assert.assertEquals(hidden, variables.get("hidden"));
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
	}

	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithoutEventDefinitionBag()
		throws Exception {

		Page<EventDefinition> eventDefinitionsPage =
			_eventDefinitionResourceImpl.getWorkspaceGroupEventDefinitionsPage(
				_GROUP_ID, null, null, null, null,
				Pagination.of(
					RandomTestUtil.randomInt(), RandomTestUtil.randomInt()),
				null);

		Assert.assertEquals(
			Collections.emptyList(), eventDefinitionsPage.getItems());
		Assert.assertEquals(0, eventDefinitionsPage.getTotalCount());
	}

	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithoutOptionalParameters()
		throws Exception {

		_getWorkspaceGroupEventDefinitionsPageResponse.setEventDefinitionBag(
			_createEventDefinitionBag(null));

		Page<EventDefinition> eventDefinitionsPage =
			_eventDefinitionResourceImpl.getWorkspaceGroupEventDefinitionsPage(
				_GROUP_ID, null, null, null, null, Pagination.of(1, 20), null);

		Assert.assertEquals(0, eventDefinitionsPage.getTotalCount());

		Map<String, Object> variables = _getVariables();

		Assert.assertNull(variables.get("blocked"));
		Assert.assertEquals("ALL", variables.get("eventType"));
		Assert.assertNull(variables.get("hidden"));
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
	}

	private GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinitionBag
		_createEventDefinitionBag(Integer total) {

		GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinitionBag
			eventDefinitionBag =
				new GetWorkspaceGroupEventDefinitionsPageResponse.
					EventDefinitionBag();

		eventDefinitionBag.setEventDefinitions(
			List.of(_responseEventDefinition));
		eventDefinitionBag.setTotal(total);

		return eventDefinitionBag;
	}

	private Map<String, Object> _getVariables() throws Exception {
		ArgumentCaptor<Map<String, Object>> argumentCaptor =
			ArgumentCaptor.forClass(Map.class);

		Mockito.verify(
			_faroGraphQLClient
		).execute(
			Mockito.eq(GetWorkspaceGroupEventDefinitionsPageResponse.class),
			Mockito.eq(_faroProject),
			Mockito.eq("getWorkspaceGroupEventDefinitionsPage"),
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final EventDefinition _eventDefinition = new EventDefinition();
	private final DTOConverter
		<GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition,
		 EventDefinition> _eventDefinitionDTOConverter = Mockito.mock(
			DTOConverter.class);
	private final EventDefinitionResourceImpl _eventDefinitionResourceImpl =
		new EventDefinitionResourceImpl();
	private final FaroGraphQLClient _faroGraphQLClient = Mockito.mock(
		FaroGraphQLClient.class);
	private final FaroProject _faroProject = Mockito.mock(FaroProject.class);
	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final GetWorkspaceGroupEventDefinitionsPageResponse
		_getWorkspaceGroupEventDefinitionsPageResponse =
			new GetWorkspaceGroupEventDefinitionsPageResponse();
	private final GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition
		_responseEventDefinition =
			new GetWorkspaceGroupEventDefinitionsPageResponse.EventDefinition();

}