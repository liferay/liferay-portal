/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.engine.client.model.GraphQLRequest;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.client.dto.v1_0.EventDefinition;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.resource.v1_0.EventDefinitionResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class EventDefinitionResourceTest
	extends BaseEventDefinitionResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_faroProject = _faroProjectLocalService.addFaroProject(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(), null,
			null, null, null, Collections.emptyList(), null, null, null, null,
			null, FaroProjectConstants.STATE_READY, "{}", "UTC", null);
	}

	@After
	@Override
	public void tearDown() throws Exception {
		_faroProjectLocalService.deleteFaroProjectByGroupId(
			_faroProject.getGroupId());

		super.tearDown();
	}

	@Override
	@Test
	public void testGetWorkspaceGroupEventDefinitionsPage() throws Exception {
		EventDefinitionResource eventDefinitionResource =
			ReflectionTestUtil.getFieldValue(this, "_eventDefinitionResource");

		Object faroGraphQLClient = ReflectionTestUtil.getFieldValue(
			eventDefinitionResource, "_faroGraphQLClient");

		ContactsEngineClient contactsEngineClient =
			ReflectionTestUtil.getAndSetFieldValue(
				faroGraphQLClient, "_contactsEngineClient",
				(ContactsEngineClient)ProxyUtil.newProxyInstance(
					ContactsEngineClient.class.getClassLoader(),
					new Class<?>[] {ContactsEngineClient.class},
					(proxy, method, arguments) -> {
						_graphQLRequest = (GraphQLRequest)arguments[4];

						return Collections.singletonMap("data", _graphQLData);
					}));

		try {
			_testGetWorkspaceGroupEventDefinitionsPage(
				_faroProject.getGroupId());
			_testGetWorkspaceGroupEventDefinitionsPageWithoutEventDefinitionBag(
				_faroProject.getGroupId());
			_testGetWorkspaceGroupEventDefinitionsPageWithoutOptionalParameters(
				_faroProject.getGroupId());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				faroGraphQLClient, "_contactsEngineClient",
				contactsEngineClient);
		}
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithPagination()
		throws Exception {

		super.testGetWorkspaceGroupEventDefinitionsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithSortDateTime()
		throws Exception {

		super.testGetWorkspaceGroupEventDefinitionsPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithSortDouble()
		throws Exception {

		super.testGetWorkspaceGroupEventDefinitionsPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithSortInteger()
		throws Exception {

		super.testGetWorkspaceGroupEventDefinitionsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventDefinitionsPageWithSortString()
		throws Exception {

		super.testGetWorkspaceGroupEventDefinitionsPageWithSortString();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"description", "displayName", "hidden", "id", "name", "type"
		};
	}

	private void _setEventDefinitionBag(
		List<EventDefinition> eventDefinitions, Integer total) {

		_graphQLData = HashMapBuilder.<String, Object>put(
			"eventDefinitionBag",
			HashMapBuilder.<String, Object>put(
				"eventDefinitions",
				TransformUtil.transform(
					eventDefinitions,
					eventDefinition -> HashMapBuilder.<String, Object>put(
						"description", eventDefinition.getDescription()
					).put(
						"displayName", eventDefinition.getDisplayName()
					).put(
						"hidden", eventDefinition.getHidden()
					).put(
						"id", eventDefinition.getId()
					).put(
						"name", eventDefinition.getName()
					).put(
						"type", eventDefinition.getTypeAsString()
					).build())
			).put(
				"total", total
			).build()
		).build();
	}

	private void _testGetWorkspaceGroupEventDefinitionsPage(long groupId)
		throws Exception {

		EventDefinition eventDefinition = randomEventDefinition();

		eventDefinition.setType(EventDefinition.Type.CUSTOM);

		int total = RandomTestUtil.randomInt();

		_setEventDefinitionBag(List.of(eventDefinition), total);

		boolean blocked = RandomTestUtil.randomBoolean();
		boolean hidden = RandomTestUtil.randomBoolean();
		String search = RandomTestUtil.randomString();

		Page<EventDefinition> eventDefinitionsPage =
			eventDefinitionResource.getWorkspaceGroupEventDefinitionsPage(
				groupId, blocked, "CUSTOM", hidden, search,
				Pagination.of(2, 10), "displayName:desc");

		assertEquals(
			List.of(eventDefinition),
			(List<EventDefinition>)eventDefinitionsPage.getItems());
		Assert.assertEquals(total, eventDefinitionsPage.getTotalCount());

		Assert.assertEquals(
			"getWorkspaceGroupEventDefinitionsPage",
			_graphQLRequest.getOperationName());

		Map<String, Object> variables = _graphQLRequest.getVariables();

		Assert.assertEquals(blocked, variables.get("blocked"));
		Assert.assertEquals("CUSTOM", variables.get("eventType"));
		Assert.assertEquals(hidden, variables.get("hidden"));
		Assert.assertEquals(search, variables.get("keyword"));
		Assert.assertEquals(1, variables.get("page"));
		Assert.assertEquals(10, variables.get("size"));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"column", "displayName"
			).put(
				"type", "DESC"
			).build(),
			variables.get("sort"));
	}

	private void
			_testGetWorkspaceGroupEventDefinitionsPageWithoutEventDefinitionBag(
				long groupId)
		throws Exception {

		_graphQLData = Collections.emptyMap();

		Page<EventDefinition> eventDefinitionsPage =
			eventDefinitionResource.getWorkspaceGroupEventDefinitionsPage(
				groupId, null, null, null, null, Pagination.of(1, 20), null);

		assertEquals(
			Collections.emptyList(),
			(List<EventDefinition>)eventDefinitionsPage.getItems());
		Assert.assertEquals(0, eventDefinitionsPage.getTotalCount());
	}

	private void
			_testGetWorkspaceGroupEventDefinitionsPageWithoutOptionalParameters(
				long groupId)
		throws Exception {

		_setEventDefinitionBag(List.of(randomEventDefinition()), null);

		Page<EventDefinition> eventDefinitionsPage =
			eventDefinitionResource.getWorkspaceGroupEventDefinitionsPage(
				groupId, null, null, null, null, Pagination.of(1, 20), null);

		Assert.assertEquals(0, eventDefinitionsPage.getTotalCount());

		Map<String, Object> variables = _graphQLRequest.getVariables();

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

	private FaroProject _faroProject;

	@Inject
	private FaroProjectLocalService _faroProjectLocalService;

	private Map<String, Object> _graphQLData;
	private GraphQLRequest _graphQLRequest;

}