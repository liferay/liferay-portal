/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.osb.faro.rest.client.dto.v1_0.EventAttributeDefinition;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.EventAttributeDefinitionResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.EventAttributeDefinitionSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public abstract class BaseEventAttributeDefinitionResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_eventAttributeDefinitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		eventAttributeDefinitionResource =
			EventAttributeDefinitionResource.builder(
			).authentication(
				_testCompanyAdminUser.getEmailAddress(),
				PropsValues.DEFAULT_ADMIN_PASSWORD
			).endpoint(
				testCompany.getVirtualHostname(),
				PortalUtil.getPortalServerPort(false), "http"
			).locale(
				LocaleUtil.getDefault()
			).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		EventAttributeDefinition eventAttributeDefinition1 =
			randomEventAttributeDefinition();

		String json = objectMapper.writeValueAsString(
			eventAttributeDefinition1);

		EventAttributeDefinition eventAttributeDefinition2 =
			EventAttributeDefinitionSerDes.toDTO(json);

		Assert.assertTrue(
			equals(eventAttributeDefinition1, eventAttributeDefinition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		EventAttributeDefinition eventAttributeDefinition =
			randomEventAttributeDefinition();

		String json1 = objectMapper.writeValueAsString(
			eventAttributeDefinition);
		String json2 = EventAttributeDefinitionSerDes.toJSON(
			eventAttributeDefinition);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		EventAttributeDefinition eventAttributeDefinition =
			randomEventAttributeDefinition();

		eventAttributeDefinition.setDescription(regex);
		eventAttributeDefinition.setDisplayName(regex);
		eventAttributeDefinition.setId(regex);
		eventAttributeDefinition.setName(regex);
		eventAttributeDefinition.setSampleValue(regex);

		String json = EventAttributeDefinitionSerDes.toJSON(
			eventAttributeDefinition);

		Assert.assertFalse(json.contains(regex));

		eventAttributeDefinition = EventAttributeDefinitionSerDes.toDTO(json);

		Assert.assertEquals(regex, eventAttributeDefinition.getDescription());
		Assert.assertEquals(regex, eventAttributeDefinition.getDisplayName());
		Assert.assertEquals(regex, eventAttributeDefinition.getId());
		Assert.assertEquals(regex, eventAttributeDefinition.getName());
		Assert.assertEquals(regex, eventAttributeDefinition.getSampleValue());
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getIrrelevantGroupId();

		Page<EventAttributeDefinition> page =
			eventAttributeDefinitionResource.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					groupId, RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantGroupId != null) {
			EventAttributeDefinition irrelevantEventAttributeDefinition =
				testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
					irrelevantGroupId,
					randomIrrelevantEventAttributeDefinition());

			page =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						irrelevantGroupId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantEventAttributeDefinition,
				(List<EventAttributeDefinition>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupEventAttributeDefinitionsPage_getExpectedActions(
					irrelevantGroupId));
		}

		EventAttributeDefinition eventAttributeDefinition1 =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				groupId, randomEventAttributeDefinition());

		EventAttributeDefinition eventAttributeDefinition2 =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				groupId, randomEventAttributeDefinition());

		page =
			eventAttributeDefinitionResource.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					groupId, null, null, null,
					Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			eventAttributeDefinition1,
			(List<EventAttributeDefinition>)page.getItems());
		assertContains(
			eventAttributeDefinition2,
			(List<EventAttributeDefinition>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getExpectedActions(
				groupId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getExpectedActions(
				Long groupId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getGroupId();

		Page<EventAttributeDefinition> eventAttributeDefinitionsPage =
			eventAttributeDefinitionResource.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					groupId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			eventAttributeDefinitionsPage.getTotalCount());

		EventAttributeDefinition eventAttributeDefinition1 =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				groupId, randomEventAttributeDefinition());

		EventAttributeDefinition eventAttributeDefinition2 =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				groupId, randomEventAttributeDefinition());

		EventAttributeDefinition eventAttributeDefinition3 =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				groupId, randomEventAttributeDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<EventAttributeDefinition> page1 =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				eventAttributeDefinition1,
				(List<EventAttributeDefinition>)page1.getItems());

			Page<EventAttributeDefinition> page2 =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				eventAttributeDefinition2,
				(List<EventAttributeDefinition>)page2.getItems());

			Page<EventAttributeDefinition> page3 =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				eventAttributeDefinition3,
				(List<EventAttributeDefinition>)page3.getItems());
		}
		else {
			Page<EventAttributeDefinition> page1 =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<EventAttributeDefinition> eventAttributeDefinitions1 =
				(List<EventAttributeDefinition>)page1.getItems();

			Assert.assertEquals(
				eventAttributeDefinitions1.toString(), totalCount + 2,
				eventAttributeDefinitions1.size());

			Page<EventAttributeDefinition> page2 =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<EventAttributeDefinition> eventAttributeDefinitions2 =
				(List<EventAttributeDefinition>)page2.getItems();

			Assert.assertEquals(
				eventAttributeDefinitions2.toString(), 1,
				eventAttributeDefinitions2.size());

			Page<EventAttributeDefinition> page3 =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				eventAttributeDefinition1,
				(List<EventAttributeDefinition>)page3.getItems());
			assertContains(
				eventAttributeDefinition2,
				(List<EventAttributeDefinition>)page3.getItems());
			assertContains(
				eventAttributeDefinition3,
				(List<EventAttributeDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortDateTime()
		throws Exception {

		testGetWorkspaceGroupEventAttributeDefinitionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, eventAttributeDefinition1,
			 eventAttributeDefinition2) -> {

				BeanTestUtil.setProperty(
					eventAttributeDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortDouble()
		throws Exception {

		testGetWorkspaceGroupEventAttributeDefinitionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, eventAttributeDefinition1,
			 eventAttributeDefinition2) -> {

				BeanTestUtil.setProperty(
					eventAttributeDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					eventAttributeDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortInteger()
		throws Exception {

		testGetWorkspaceGroupEventAttributeDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, eventAttributeDefinition1,
			 eventAttributeDefinition2) -> {

				BeanTestUtil.setProperty(
					eventAttributeDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					eventAttributeDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSortString()
		throws Exception {

		testGetWorkspaceGroupEventAttributeDefinitionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, eventAttributeDefinition1,
			 eventAttributeDefinition2) -> {

				Class<?> clazz = eventAttributeDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						eventAttributeDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						eventAttributeDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						eventAttributeDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						eventAttributeDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						eventAttributeDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						eventAttributeDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWorkspaceGroupEventAttributeDefinitionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, EventAttributeDefinition,
				 EventAttributeDefinition, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long groupId =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getGroupId();

		EventAttributeDefinition eventAttributeDefinition1 =
			randomEventAttributeDefinition();
		EventAttributeDefinition eventAttributeDefinition2 =
			randomEventAttributeDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, eventAttributeDefinition1,
				eventAttributeDefinition2);
		}

		eventAttributeDefinition1 =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				groupId, eventAttributeDefinition1);

		eventAttributeDefinition2 =
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				groupId, eventAttributeDefinition2);

		Page<EventAttributeDefinition> page =
			eventAttributeDefinitionResource.
				getWorkspaceGroupEventAttributeDefinitionsPage(
					groupId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<EventAttributeDefinition> ascPage =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				eventAttributeDefinition1,
				(List<EventAttributeDefinition>)ascPage.getItems());
			assertContains(
				eventAttributeDefinition2,
				(List<EventAttributeDefinition>)ascPage.getItems());

			Page<EventAttributeDefinition> descPage =
				eventAttributeDefinitionResource.
					getWorkspaceGroupEventAttributeDefinitionsPage(
						groupId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				eventAttributeDefinition2,
				(List<EventAttributeDefinition>)descPage.getItems());
			assertContains(
				eventAttributeDefinition1,
				(List<EventAttributeDefinition>)descPage.getItems());
		}
	}

	protected EventAttributeDefinition
			testGetWorkspaceGroupEventAttributeDefinitionsPage_addEventAttributeDefinition(
				Long groupId, EventAttributeDefinition eventAttributeDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupEventAttributeDefinitionsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		EventAttributeDefinition eventAttributeDefinition,
		List<EventAttributeDefinition> eventAttributeDefinitions) {

		boolean contains = false;

		for (EventAttributeDefinition item : eventAttributeDefinitions) {
			if (equals(eventAttributeDefinition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			eventAttributeDefinitions + " does not contain " +
				eventAttributeDefinition,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		EventAttributeDefinition eventAttributeDefinition1,
		EventAttributeDefinition eventAttributeDefinition2) {

		Assert.assertTrue(
			eventAttributeDefinition1 + " does not equal " +
				eventAttributeDefinition2,
			equals(eventAttributeDefinition1, eventAttributeDefinition2));
	}

	protected void assertEquals(
		List<EventAttributeDefinition> eventAttributeDefinitions1,
		List<EventAttributeDefinition> eventAttributeDefinitions2) {

		Assert.assertEquals(
			eventAttributeDefinitions1.size(),
			eventAttributeDefinitions2.size());

		for (int i = 0; i < eventAttributeDefinitions1.size(); i++) {
			EventAttributeDefinition eventAttributeDefinition1 =
				eventAttributeDefinitions1.get(i);
			EventAttributeDefinition eventAttributeDefinition2 =
				eventAttributeDefinitions2.get(i);

			assertEquals(eventAttributeDefinition1, eventAttributeDefinition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<EventAttributeDefinition> eventAttributeDefinitions1,
		List<EventAttributeDefinition> eventAttributeDefinitions2) {

		Assert.assertEquals(
			eventAttributeDefinitions1.size(),
			eventAttributeDefinitions2.size());

		for (EventAttributeDefinition eventAttributeDefinition1 :
				eventAttributeDefinitions1) {

			boolean contains = false;

			for (EventAttributeDefinition eventAttributeDefinition2 :
					eventAttributeDefinitions2) {

				if (equals(
						eventAttributeDefinition1, eventAttributeDefinition2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				eventAttributeDefinitions2 + " does not contain " +
					eventAttributeDefinition1,
				contains);
		}
	}

	protected void assertValid(
			EventAttributeDefinition eventAttributeDefinition)
		throws Exception {

		boolean valid = true;

		if (eventAttributeDefinition.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dataType", additionalAssertFieldName)) {
				if (eventAttributeDefinition.getDataType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (eventAttributeDefinition.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayName", additionalAssertFieldName)) {
				if (eventAttributeDefinition.getDisplayName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (eventAttributeDefinition.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sampleValue", additionalAssertFieldName)) {
				if (eventAttributeDefinition.getSampleValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (eventAttributeDefinition.getType() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<EventAttributeDefinition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<EventAttributeDefinition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<EventAttributeDefinition>
			eventAttributeDefinitions = page.getItems();

		int size = eventAttributeDefinitions.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.osb.faro.rest.dto.v1_0.EventAttributeDefinition.
						class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		EventAttributeDefinition eventAttributeDefinition1,
		EventAttributeDefinition eventAttributeDefinition2) {

		if (eventAttributeDefinition1 == eventAttributeDefinition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dataType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						eventAttributeDefinition1.getDataType(),
						eventAttributeDefinition2.getDataType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						eventAttributeDefinition1.getDescription(),
						eventAttributeDefinition2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						eventAttributeDefinition1.getDisplayName(),
						eventAttributeDefinition2.getDisplayName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						eventAttributeDefinition1.getId(),
						eventAttributeDefinition2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						eventAttributeDefinition1.getName(),
						eventAttributeDefinition2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sampleValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						eventAttributeDefinition1.getSampleValue(),
						eventAttributeDefinition2.getSampleValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						eventAttributeDefinition1.getType(),
						eventAttributeDefinition2.getType())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_eventAttributeDefinitionResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_eventAttributeDefinitionResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator,
		EventAttributeDefinition eventAttributeDefinition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dataType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = eventAttributeDefinition.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("displayName")) {
			Object object = eventAttributeDefinition.getDisplayName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			Object object = eventAttributeDefinition.getId();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = eventAttributeDefinition.getName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("sampleValue")) {
			Object object = eventAttributeDefinition.getSampleValue();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("type")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected EventAttributeDefinition randomEventAttributeDefinition()
		throws Exception {

		return new EventAttributeDefinition() {
			{
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				displayName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				sampleValue = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected EventAttributeDefinition
			randomIrrelevantEventAttributeDefinition()
		throws Exception {

		EventAttributeDefinition randomIrrelevantEventAttributeDefinition =
			randomEventAttributeDefinition();

		return randomIrrelevantEventAttributeDefinition;
	}

	protected EventAttributeDefinition randomPatchEventAttributeDefinition()
		throws Exception {

		return randomEventAttributeDefinition();
	}

	protected EventAttributeDefinitionResource eventAttributeDefinitionResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(
			BaseEventAttributeDefinitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.osb.faro.rest.resource.v1_0.EventAttributeDefinitionResource
			_eventAttributeDefinitionResource;

}
// LIFERAY-REST-BUILDER-HASH:1242752701