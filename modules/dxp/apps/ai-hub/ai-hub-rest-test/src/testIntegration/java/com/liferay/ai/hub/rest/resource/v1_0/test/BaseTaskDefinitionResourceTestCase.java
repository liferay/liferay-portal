/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.ai.hub.rest.client.dto.v1_0.TaskDefinition;
import com.liferay.ai.hub.rest.client.http.HttpInvoker;
import com.liferay.ai.hub.rest.client.pagination.Page;
import com.liferay.ai.hub.rest.client.pagination.Pagination;
import com.liferay.ai.hub.rest.client.resource.v1_0.TaskDefinitionResource;
import com.liferay.ai.hub.rest.client.serdes.v1_0.TaskDefinitionSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Feliphe Marinho
 * @generated
 */
@Generated("")
public abstract class BaseTaskDefinitionResourceTestCase {

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

		_taskDefinitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		taskDefinitionResource = TaskDefinitionResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
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

		TaskDefinition taskDefinition1 = randomTaskDefinition();

		String json = objectMapper.writeValueAsString(taskDefinition1);

		TaskDefinition taskDefinition2 = TaskDefinitionSerDes.toDTO(json);

		Assert.assertTrue(equals(taskDefinition1, taskDefinition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		TaskDefinition taskDefinition = randomTaskDefinition();

		String json1 = objectMapper.writeValueAsString(taskDefinition);
		String json2 = TaskDefinitionSerDes.toJSON(taskDefinition);

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

		TaskDefinition taskDefinition = randomTaskDefinition();

		taskDefinition.setDescription(regex);
		taskDefinition.setName(regex);

		String json = TaskDefinitionSerDes.toJSON(taskDefinition);

		Assert.assertFalse(json.contains(regex));

		taskDefinition = TaskDefinitionSerDes.toDTO(json);

		Assert.assertEquals(regex, taskDefinition.getDescription());
		Assert.assertEquals(regex, taskDefinition.getName());
	}

	@Test
	public void testDeleteTaskDefinition() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaskDefinition taskDefinition =
			testDeleteTaskDefinition_addTaskDefinition();

		assertHttpResponseStatusCode(
			204,
			taskDefinitionResource.deleteTaskDefinitionHttpResponse(
				taskDefinition.getId()));
	}

	protected TaskDefinition testDeleteTaskDefinition_addTaskDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteTaskDefinitionBatch() throws Exception {
		TaskDefinition taskDefinition1 =
			testDeleteTaskDefinitionBatch_addTaskDefinition();

		testDeleteTaskDefinitionBatch_deleteTaskDefinition(
			202, null, taskDefinition1.getId());
	}

	protected TaskDefinition testDeleteTaskDefinitionBatch_addTaskDefinition()
		throws Exception {

		return testDeleteTaskDefinition_addTaskDefinition();
	}

	protected void testDeleteTaskDefinitionBatch_deleteTaskDefinition(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			taskDefinitionResource.deleteTaskDefinitionBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetTaskDefinitionsPage() throws Exception {
		Page<TaskDefinition> page =
			taskDefinitionResource.getTaskDefinitionsPage(
				null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		TaskDefinition taskDefinition1 =
			testGetTaskDefinitionsPage_addTaskDefinition(
				randomTaskDefinition());

		TaskDefinition taskDefinition2 =
			testGetTaskDefinitionsPage_addTaskDefinition(
				randomTaskDefinition());

		page = taskDefinitionResource.getTaskDefinitionsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(taskDefinition1, (List<TaskDefinition>)page.getItems());
		assertContains(taskDefinition2, (List<TaskDefinition>)page.getItems());
		assertValid(page, testGetTaskDefinitionsPage_getExpectedActions());

		taskDefinitionResource.deleteTaskDefinition(taskDefinition1.getId());

		taskDefinitionResource.deleteTaskDefinition(taskDefinition2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetTaskDefinitionsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetTaskDefinitionsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		TaskDefinition taskDefinition1 = randomTaskDefinition();

		taskDefinition1 = testGetTaskDefinitionsPage_addTaskDefinition(
			taskDefinition1);

		for (EntityField entityField : entityFields) {
			Page<TaskDefinition> page =
				taskDefinitionResource.getTaskDefinitionsPage(
					null,
					getFilterString(entityField, "between", taskDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taskDefinition1),
				(List<TaskDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetTaskDefinitionsPageWithFilterDoubleEquals()
		throws Exception {

		testGetTaskDefinitionsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetTaskDefinitionsPageWithFilterStringContains()
		throws Exception {

		testGetTaskDefinitionsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaskDefinitionsPageWithFilterStringEquals()
		throws Exception {

		testGetTaskDefinitionsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetTaskDefinitionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetTaskDefinitionsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetTaskDefinitionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		TaskDefinition taskDefinition1 =
			testGetTaskDefinitionsPage_addTaskDefinition(
				randomTaskDefinition());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		TaskDefinition taskDefinition2 =
			testGetTaskDefinitionsPage_addTaskDefinition(
				randomTaskDefinition());

		for (EntityField entityField : entityFields) {
			Page<TaskDefinition> page =
				taskDefinitionResource.getTaskDefinitionsPage(
					null,
					getFilterString(entityField, operator, taskDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(taskDefinition1),
				(List<TaskDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetTaskDefinitionsPageWithPagination() throws Exception {
		Page<TaskDefinition> taskDefinitionsPage =
			taskDefinitionResource.getTaskDefinitionsPage(
				null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			taskDefinitionsPage.getTotalCount());

		TaskDefinition taskDefinition1 =
			testGetTaskDefinitionsPage_addTaskDefinition(
				randomTaskDefinition());

		TaskDefinition taskDefinition2 =
			testGetTaskDefinitionsPage_addTaskDefinition(
				randomTaskDefinition());

		TaskDefinition taskDefinition3 =
			testGetTaskDefinitionsPage_addTaskDefinition(
				randomTaskDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TaskDefinition> page1 =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				taskDefinition1, (List<TaskDefinition>)page1.getItems());

			Page<TaskDefinition> page2 =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				taskDefinition2, (List<TaskDefinition>)page2.getItems());

			Page<TaskDefinition> page3 =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				taskDefinition3, (List<TaskDefinition>)page3.getItems());
		}
		else {
			Page<TaskDefinition> page1 =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<TaskDefinition> taskDefinitions1 =
				(List<TaskDefinition>)page1.getItems();

			Assert.assertEquals(
				taskDefinitions1.toString(), totalCount + 2,
				taskDefinitions1.size());

			Page<TaskDefinition> page2 =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TaskDefinition> taskDefinitions2 =
				(List<TaskDefinition>)page2.getItems();

			Assert.assertEquals(
				taskDefinitions2.toString(), 1, taskDefinitions2.size());

			Page<TaskDefinition> page3 =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				taskDefinition1, (List<TaskDefinition>)page3.getItems());
			assertContains(
				taskDefinition2, (List<TaskDefinition>)page3.getItems());
			assertContains(
				taskDefinition3, (List<TaskDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetTaskDefinitionsPageWithSortDateTime() throws Exception {
		testGetTaskDefinitionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, taskDefinition1, taskDefinition2) -> {
				BeanTestUtil.setProperty(
					taskDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetTaskDefinitionsPageWithSortDouble() throws Exception {
		testGetTaskDefinitionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, taskDefinition1, taskDefinition2) -> {
				BeanTestUtil.setProperty(
					taskDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					taskDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetTaskDefinitionsPageWithSortInteger() throws Exception {
		testGetTaskDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, taskDefinition1, taskDefinition2) -> {
				BeanTestUtil.setProperty(
					taskDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					taskDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetTaskDefinitionsPageWithSortString() throws Exception {
		testGetTaskDefinitionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, taskDefinition1, taskDefinition2) -> {
				Class<?> clazz = taskDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						taskDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						taskDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						taskDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						taskDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						taskDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						taskDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetTaskDefinitionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, TaskDefinition, TaskDefinition, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		TaskDefinition taskDefinition1 = randomTaskDefinition();
		TaskDefinition taskDefinition2 = randomTaskDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, taskDefinition1, taskDefinition2);
		}

		taskDefinition1 = testGetTaskDefinitionsPage_addTaskDefinition(
			taskDefinition1);

		taskDefinition2 = testGetTaskDefinitionsPage_addTaskDefinition(
			taskDefinition2);

		Page<TaskDefinition> page =
			taskDefinitionResource.getTaskDefinitionsPage(
				null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<TaskDefinition> ascPage =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				taskDefinition1, (List<TaskDefinition>)ascPage.getItems());
			assertContains(
				taskDefinition2, (List<TaskDefinition>)ascPage.getItems());

			Page<TaskDefinition> descPage =
				taskDefinitionResource.getTaskDefinitionsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				taskDefinition2, (List<TaskDefinition>)descPage.getItems());
			assertContains(
				taskDefinition1, (List<TaskDefinition>)descPage.getItems());
		}
	}

	protected TaskDefinition testGetTaskDefinitionsPage_addTaskDefinition(
			TaskDefinition taskDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		TaskDefinition taskDefinition1 =
			testBatchEngineDeleteImportTask_addTaskDefinition();

		testBatchEngineDeleteImportTask_deleteTaskDefinition(
			200, null, taskDefinition1.getId());
	}

	protected TaskDefinition testBatchEngineDeleteImportTask_addTaskDefinition()
		throws Exception {

		return testDeleteTaskDefinition_addTaskDefinition();
	}

	protected void testBatchEngineDeleteImportTask_deleteTaskDefinition(
			int expectedStatusCode, String externalReferenceCode, Long id,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.ai.hub.rest.dto.v1_0.TaskDefinition", null, null,
				null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		TaskDefinition taskDefinition, List<TaskDefinition> taskDefinitions) {

		boolean contains = false;

		for (TaskDefinition item : taskDefinitions) {
			if (equals(taskDefinition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			taskDefinitions + " does not contain " + taskDefinition, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		TaskDefinition taskDefinition1, TaskDefinition taskDefinition2) {

		Assert.assertTrue(
			taskDefinition1 + " does not equal " + taskDefinition2,
			equals(taskDefinition1, taskDefinition2));
	}

	protected void assertEquals(
		List<TaskDefinition> taskDefinitions1,
		List<TaskDefinition> taskDefinitions2) {

		Assert.assertEquals(taskDefinitions1.size(), taskDefinitions2.size());

		for (int i = 0; i < taskDefinitions1.size(); i++) {
			TaskDefinition taskDefinition1 = taskDefinitions1.get(i);
			TaskDefinition taskDefinition2 = taskDefinitions2.get(i);

			assertEquals(taskDefinition1, taskDefinition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TaskDefinition> taskDefinitions1,
		List<TaskDefinition> taskDefinitions2) {

		Assert.assertEquals(taskDefinitions1.size(), taskDefinitions2.size());

		for (TaskDefinition taskDefinition1 : taskDefinitions1) {
			boolean contains = false;

			for (TaskDefinition taskDefinition2 : taskDefinitions2) {
				if (equals(taskDefinition1, taskDefinition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				taskDefinitions2 + " does not contain " + taskDefinition1,
				contains);
		}
	}

	protected void assertValid(TaskDefinition taskDefinition) throws Exception {
		boolean valid = true;

		if (taskDefinition.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (taskDefinition.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (taskDefinition.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (taskDefinition.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (taskDefinition.getVersion() == null) {
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

	protected void assertValid(Page<TaskDefinition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<TaskDefinition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<TaskDefinition> taskDefinitions = page.getItems();

		int size = taskDefinitions.size();

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
					com.liferay.ai.hub.rest.dto.v1_0.TaskDefinition.class)) {

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
		TaskDefinition taskDefinition1, TaskDefinition taskDefinition2) {

		if (taskDefinition1 == taskDefinition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)taskDefinition1.getActions(),
						(Map)taskDefinition2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taskDefinition1.getDescription(),
						taskDefinition2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taskDefinition1.getId(), taskDefinition2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taskDefinition1.getName(), taskDefinition2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						taskDefinition1.getVersion(),
						taskDefinition2.getVersion())) {

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

		if (!(_taskDefinitionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taskDefinitionResource;

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
		TaskDefinition taskDefinition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = taskDefinition.getDescription();

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
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = taskDefinition.getName();

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

		if (entityFieldName.equals("version")) {
			sb.append(String.valueOf(taskDefinition.getVersion()));

			return sb.toString();
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
		httpInvoker.path("http://localhost:8080/o/graphql");
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

	protected TaskDefinition randomTaskDefinition() throws Exception {
		return new TaskDefinition() {
			{
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				version = RandomTestUtil.randomInt();
			}
		};
	}

	protected TaskDefinition randomIrrelevantTaskDefinition() throws Exception {
		TaskDefinition randomIrrelevantTaskDefinition = randomTaskDefinition();

		return randomIrrelevantTaskDefinition;
	}

	protected TaskDefinition randomPatchTaskDefinition() throws Exception {
		return randomTaskDefinition();
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected TaskDefinitionResource taskDefinitionResource;
	protected ImportTaskResource importTaskResource;
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
		LogFactoryUtil.getLog(BaseTaskDefinitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.ai.hub.rest.resource.v1_0.TaskDefinitionResource
		_taskDefinitionResource;

}