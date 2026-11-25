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
import com.liferay.osb.faro.rest.client.dto.v1_0.EventAttributeDefinition;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.resource.v1_0.EventAttributeDefinitionResource;
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
public class EventAttributeDefinitionResourceTest
	extends BaseEventAttributeDefinitionResourceTestCase {

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
	public void testGetWorkspaceGroupEventAttributeDefinitionsPage()
		throws Exception {

		EventAttributeDefinitionResource eventAttributeDefinitionResource =
			ReflectionTestUtil.getFieldValue(
				this, "_eventAttributeDefinitionResource");

		Object faroGraphQLClient = ReflectionTestUtil.getFieldValue(
			eventAttributeDefinitionResource, "_faroGraphQLClient");

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
			_testGetWorkspaceGroupEventAttributeDefinitionsPage(
				_faroProject.getGroupId());
			_testGetWorkspaceGroupEventAttributeDefinitionsPageWithoutEventAttributeDefinitionBag(
				_faroProject.getGroupId());
			_testGetWorkspaceGroupEventAttributeDefinitionsPageWithoutOptionalParameters(
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
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithPagination()
		throws Exception {

		super.
			testGetWorkspaceGroupEventAttributeDefinitionsPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortDateTime()
		throws Exception {

		super.
			testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortDateTime();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortDouble()
		throws Exception {

		super.
			testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortDouble();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortInteger()
		throws Exception {

		super.
			testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortInteger();
	}

	@Ignore
	@Override
	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortString()
		throws Exception {

		super.
			testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortString();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"dataType", "description", "displayName", "id", "name",
			"sampleValue", "type"
		};
	}

	private void _setEventAttributeDefinitionBag(
		List<EventAttributeDefinition> eventAttributeDefinitions,
		Integer total) {

		_graphQLData = HashMapBuilder.<String, Object>put(
			"eventAttributeDefinitionBag",
			HashMapBuilder.<String, Object>put(
				"eventAttributeDefinitions",
				TransformUtil.transform(
					eventAttributeDefinitions,
					eventAttributeDefinition -> HashMapBuilder.put(
						"dataType",
						eventAttributeDefinition.getDataTypeAsString()
					).put(
						"description", eventAttributeDefinition.getDescription()
					).put(
						"displayName", eventAttributeDefinition.getDisplayName()
					).put(
						"id", eventAttributeDefinition.getId()
					).put(
						"name", eventAttributeDefinition.getName()
					).put(
						"sampleValue", eventAttributeDefinition.getSampleValue()
					).put(
						"type", eventAttributeDefinition.getTypeAsString()
					).build())
			).put(
				"total", total
			).build()
		).build();
	}

	private void _testGetWorkspaceGroupEventAttributeDefinitionsPage(
			long groupId)
		throws Exception {

		EventAttributeDefinition eventAttributeDefinition =
			randomEventAttributeDefinition();

		eventAttributeDefinition.setDataType(
			EventAttributeDefinition.DataType.STRING);
		eventAttributeDefinition.setType(EventAttributeDefinition.Type.LOCAL);

		int total = RandomTestUtil.randomInt();

		_setEventAttributeDefinitionBag(
			List.of(eventAttributeDefinition), total);

		String eventDefinitionId = String.valueOf(RandomTestUtil.randomLong());
		String search = RandomTestUtil.randomString();

		Page<EventAttributeDefinition> eventAttributeDefinitionsPage =
			eventAttributeDefinitionResource.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					groupId, "LOCAL", eventDefinitionId, search,
					Pagination.of(2, 10), "name:desc");

		assertEquals(
			List.of(eventAttributeDefinition),
			(List<EventAttributeDefinition>)
				eventAttributeDefinitionsPage.getItems());
		Assert.assertEquals(
			total, eventAttributeDefinitionsPage.getTotalCount());

		Assert.assertEquals(
			"getWorkspaceGroupEventAttributeDefinitionsPage",
			_graphQLRequest.getOperationName());

		Map<String, Object> variables = _graphQLRequest.getVariables();

		Assert.assertEquals(
			eventDefinitionId, variables.get("eventDefinitionId"));
		Assert.assertEquals(search, variables.get("keyword"));
		Assert.assertEquals(1, variables.get("page"));
		Assert.assertEquals(10, variables.get("size"));
		Assert.assertEquals(
			HashMapBuilder.<String, Object>put(
				"column", "name"
			).put(
				"type", "DESC"
			).build(),
			variables.get("sort"));
		Assert.assertEquals("LOCAL", variables.get("type"));
	}

	private void
			_testGetWorkspaceGroupEventAttributeDefinitionsPageWithoutEventAttributeDefinitionBag(
				long groupId)
		throws Exception {

		_graphQLData = Collections.emptyMap();

		Page<EventAttributeDefinition> eventAttributeDefinitionsPage =
			eventAttributeDefinitionResource.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					groupId, null, null, null, Pagination.of(1, 20), null);

		assertEquals(
			Collections.emptyList(),
			(List<EventAttributeDefinition>)
				eventAttributeDefinitionsPage.getItems());
		Assert.assertEquals(0, eventAttributeDefinitionsPage.getTotalCount());
	}

	private void
			_testGetWorkspaceGroupEventAttributeDefinitionsPageWithoutOptionalParameters(
				long groupId)
		throws Exception {

		_setEventAttributeDefinitionBag(
			List.of(randomEventAttributeDefinition()), null);

		Page<EventAttributeDefinition> eventAttributeDefinitionsPage =
			eventAttributeDefinitionResource.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					groupId, null, null, null, Pagination.of(1, 20), null);

		Assert.assertEquals(0, eventAttributeDefinitionsPage.getTotalCount());

		Map<String, Object> variables = _graphQLRequest.getVariables();

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

	private FaroProject _faroProject;

	@Inject
	private FaroProjectLocalService _faroProjectLocalService;

	private Map<String, Object> _graphQLData;
	private GraphQLRequest _graphQLRequest;

}