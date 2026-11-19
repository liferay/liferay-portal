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

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
		).thenAnswer(
			invocation -> {
				_variables = invocation.getArgument(3);

				return _getWorkspaceGroupEventDefinitionsPageResponse;
			}
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
		boolean blocked = RandomTestUtil.randomBoolean();
		String eventType = RandomTestUtil.randomString();
		String fieldName = RandomTestUtil.randomString();
		boolean hidden = RandomTestUtil.randomBoolean();
		int page = RandomTestUtil.randomInt();
		int pageSize = RandomTestUtil.randomInt();
		String search = RandomTestUtil.randomString();

		int total = RandomTestUtil.randomInt();

		_getWorkspaceGroupEventDefinitionsPageResponse.setEventDefinitionBag(
			_createEventDefinitionBag(total));

		Page<EventDefinition> eventDefinitionsPage =
			_eventDefinitionResourceImpl.getWorkspaceGroupEventDefinitionsPage(
				_GROUP_ID, blocked, eventType, hidden, search,
				Pagination.of(page, pageSize),
				new Sort[] {new Sort(fieldName, Sort.STRING_TYPE, true)});

		Assert.assertEquals(
			List.of(_eventDefinition), eventDefinitionsPage.getItems());
		Assert.assertEquals(total, eventDefinitionsPage.getTotalCount());

		Assert.assertEquals(blocked, _variables.get("blocked"));
		Assert.assertEquals(eventType, _variables.get("eventType"));
		Assert.assertEquals(hidden, _variables.get("hidden"));
		Assert.assertEquals(search, _variables.get("keyword"));
		Assert.assertEquals(page - 1, _variables.get("page"));
		Assert.assertEquals(pageSize, _variables.get("size"));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"column", fieldName
			).put(
				"type", "DESC"
			).build(),
			_variables.get("sort"));
	}

	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithoutEventDefinitionBag()
		throws Exception {

		Page<EventDefinition> eventDefinitionsPage =
			_eventDefinitionResourceImpl.getWorkspaceGroupEventDefinitionsPage(
				_GROUP_ID, null, null, null, null, Pagination.of(1, 20), null);

		Assert.assertEquals(
			List.of(), (List<?>)eventDefinitionsPage.getItems());
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

		Assert.assertEquals("ALL", _variables.get("eventType"));
		Assert.assertEquals(0, _variables.get("page"));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"column", "name"
			).put(
				"type", "ASC"
			).build(),
			_variables.get("sort"));
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
	private Map<String, Object> _variables;

}