/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.client.http.HttpInvoker;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.resource.v1_0.TaskResource;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.TaskSerDes;

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
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public abstract class BaseTaskResourceTestCase {

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

		_taskResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		taskResource = TaskResource.builder(
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

		Task task1 = randomTask();

		String json = objectMapper.writeValueAsString(task1);

		Task task2 = TaskSerDes.toDTO(json);

		Assert.assertTrue(equals(task1, task2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Task task = randomTask();

		String json1 = objectMapper.writeValueAsString(task);
		String json2 = TaskSerDes.toJSON(task);

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

		Task task = randomTask();

		task.setAssetTitle(regex);
		task.setAssetType(regex);
		task.setClassName(regex);
		task.setLabel(regex);
		task.setName(regex);
		task.setProcessVersion(regex);

		String json = TaskSerDes.toJSON(task);

		Assert.assertFalse(json.contains(regex));

		task = TaskSerDes.toDTO(json);

		Assert.assertEquals(regex, task.getAssetTitle());
		Assert.assertEquals(regex, task.getAssetType());
		Assert.assertEquals(regex, task.getClassName());
		Assert.assertEquals(regex, task.getLabel());
		Assert.assertEquals(regex, task.getName());
		Assert.assertEquals(regex, task.getProcessVersion());
	}

	@Test
	public void testDeleteProcessTask() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Task task = testDeleteProcessTask_addTask();

		assertHttpResponseStatusCode(
			204,
			taskResource.deleteProcessTaskHttpResponse(
				testDeleteProcessTask_getProcessId(task), task.getId()));

		assertHttpResponseStatusCode(
			404,
			taskResource.getProcessTaskHttpResponse(
				testDeleteProcessTask_getProcessId(task), task.getId()));
		assertHttpResponseStatusCode(
			404,
			taskResource.getProcessTaskHttpResponse(
				testDeleteProcessTask_getProcessId(task), 0L));
	}

	protected Task testDeleteProcessTask_addTask() throws Exception {
		return testPostProcessTask_addTask(randomTask());
	}

	protected Long testDeleteProcessTask_getProcessId(Task task)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProcessTask() throws Exception {

		// No namespace

		Task task1 = testGraphQLDeleteProcessTask_addTask();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProcessTask",
						new HashMap<String, Object>() {
							{
								put(
									"processId",
									testGraphQLDeleteProcessTask_getProcessId(
										task1));
								put("taskId", task1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProcessTask"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"processTask",
					new HashMap<String, Object>() {
						{
							put(
								"processId",
								testGraphQLDeleteProcessTask_getProcessId(
									task1));
							put("taskId", task1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace portalWorkflowMetrics_v1_0

		Task task2 = testGraphQLDeleteProcessTask_addTask();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"portalWorkflowMetrics_v1_0",
						new GraphQLField(
							"deleteProcessTask",
							new HashMap<String, Object>() {
								{
									put(
										"processId",
										testGraphQLDeleteProcessTask_getProcessId(
											task2));
									put("taskId", task2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/portalWorkflowMetrics_v1_0",
				"Object/deleteProcessTask"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"portalWorkflowMetrics_v1_0",
					new GraphQLField(
						"processTask",
						new HashMap<String, Object>() {
							{
								put(
									"processId",
									testGraphQLDeleteProcessTask_getProcessId(
										task2));
								put("taskId", task2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long testGraphQLDeleteProcessTask_getProcessId(Task task)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Task testGraphQLDeleteProcessTask_addTask() throws Exception {
		return testGraphQLTask_addTask();
	}

	@Test
	public void testGetProcessTask() throws Exception {
		Task postTask = testGetProcessTask_addTask();

		Task getTask = taskResource.getProcessTask(
			testGetProcessTask_getProcessId(postTask), postTask.getId());

		assertEquals(postTask, getTask);
		assertValid(getTask);
	}

	protected Task testGetProcessTask_addTask() throws Exception {
		return testPostProcessTask_addTask(randomTask());
	}

	protected Long testGetProcessTask_getProcessId(Task task) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProcessTask() throws Exception {
		Task task = testGraphQLGetProcessTask_addTask();

		// No namespace

		Assert.assertTrue(
			equals(
				task,
				TaskSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"processTask",
								new HashMap<String, Object>() {
									{
										put(
											"processId",
											testGraphQLGetProcessTask_getProcessId(
												task));
										put("taskId", task.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/processTask"))));

		// Using the namespace portalWorkflowMetrics_v1_0

		Assert.assertTrue(
			equals(
				task,
				TaskSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"portalWorkflowMetrics_v1_0",
								new GraphQLField(
									"processTask",
									new HashMap<String, Object>() {
										{
											put(
												"processId",
												testGraphQLGetProcessTask_getProcessId(
													task));
											put("taskId", task.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/portalWorkflowMetrics_v1_0",
						"Object/processTask"))));
	}

	protected Long testGraphQLGetProcessTask_getProcessId(Task task)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProcessTaskNotFound() throws Exception {
		Long irrelevantProcessId = RandomTestUtil.randomLong();
		Long irrelevantTaskId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"processTask",
						new HashMap<String, Object>() {
							{
								put("processId", irrelevantProcessId);
								put("taskId", irrelevantTaskId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace portalWorkflowMetrics_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"portalWorkflowMetrics_v1_0",
						new GraphQLField(
							"processTask",
							new HashMap<String, Object>() {
								{
									put("processId", irrelevantProcessId);
									put("taskId", irrelevantTaskId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Task testGraphQLGetProcessTask_addTask() throws Exception {
		return testGraphQLTask_addTask();
	}

	@Test
	public void testGetProcessTasksPage() throws Exception {
		Long processId = testGetProcessTasksPage_getProcessId();
		Long irrelevantProcessId =
			testGetProcessTasksPage_getIrrelevantProcessId();

		Page<Task> page = taskResource.getProcessTasksPage(processId);

		long totalCount = page.getTotalCount();

		if (irrelevantProcessId != null) {
			Task irrelevantTask = testGetProcessTasksPage_addTask(
				irrelevantProcessId, randomIrrelevantTask());

			page = taskResource.getProcessTasksPage(irrelevantProcessId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantTask, (List<Task>)page.getItems());
			assertValid(
				page,
				testGetProcessTasksPage_getExpectedActions(
					irrelevantProcessId));
		}

		Task task1 = testGetProcessTasksPage_addTask(processId, randomTask());

		Task task2 = testGetProcessTasksPage_addTask(processId, randomTask());

		page = taskResource.getProcessTasksPage(processId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(task1, (List<Task>)page.getItems());
		assertContains(task2, (List<Task>)page.getItems());
		assertValid(
			page, testGetProcessTasksPage_getExpectedActions(processId));
	}

	protected Map<String, Map<String, String>>
			testGetProcessTasksPage_getExpectedActions(Long processId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/portal-workflow-metrics/v1.0/processes/{processId}/tasks/batch".
				replace("{processId}", String.valueOf(processId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	protected Task testGetProcessTasksPage_addTask(Long processId, Task task)
		throws Exception {

		return taskResource.postProcessTask(processId, task);
	}

	protected Long testGetProcessTasksPage_getProcessId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProcessTasksPage_getIrrelevantProcessId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetProcessTasksPage() throws Exception {
		Long processId = testGetProcessTasksPage_getProcessId();

		GraphQLField graphQLField = new GraphQLField(
			"processTasks",
			new HashMap<String, Object>() {
				{
					put("processId", processId);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject processTasksJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/processTasks");

		long totalCount = processTasksJSONObject.getLong("totalCount");

		Task task1 = testGraphQLProcessTask_addTask(processId, randomTask());

		Task task2 = testGraphQLProcessTask_addTask(processId, randomTask());

		processTasksJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/processTasks");

		Assert.assertEquals(
			totalCount + 2, processTasksJSONObject.getLong("totalCount"));

		assertContains(
			task1,
			Arrays.asList(
				TaskSerDes.toDTOs(processTasksJSONObject.getString("items"))));
		assertContains(
			task2,
			Arrays.asList(
				TaskSerDes.toDTOs(processTasksJSONObject.getString("items"))));

		// Using the namespace portalWorkflowMetrics_v1_0

		processTasksJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("portalWorkflowMetrics_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/portalWorkflowMetrics_v1_0",
			"JSONObject/processTasks");

		Assert.assertEquals(
			totalCount + 2, processTasksJSONObject.getLong("totalCount"));

		assertContains(
			task1,
			Arrays.asList(
				TaskSerDes.toDTOs(processTasksJSONObject.getString("items"))));
		assertContains(
			task2,
			Arrays.asList(
				TaskSerDes.toDTOs(processTasksJSONObject.getString("items"))));
	}

	@Test
	public void testPatchProcessTask() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Task task = testPatchProcessTask_addTask();

		assertHttpResponseStatusCode(
			204,
			taskResource.patchProcessTaskHttpResponse(
				testPatchProcessTask_getProcessId(task), task.getId(), task));

		assertHttpResponseStatusCode(
			404,
			taskResource.patchProcessTaskHttpResponse(
				testPatchProcessTask_getProcessId(task), 0L, task));
	}

	protected Long testPatchProcessTask_getProcessId(Task task)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Task testPatchProcessTask_addTask() throws Exception {
		return testPostProcessTask_addTask(randomTask());
	}

	@Test
	public void testPatchProcessTaskComplete() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Task task = testPatchProcessTaskComplete_addTask();

		assertHttpResponseStatusCode(
			204,
			taskResource.patchProcessTaskCompleteHttpResponse(
				testPatchProcessTaskComplete_getProcessId(task), task.getId(),
				task));

		assertHttpResponseStatusCode(
			404,
			taskResource.patchProcessTaskCompleteHttpResponse(
				testPatchProcessTaskComplete_getProcessId(task), 0L, task));
	}

	protected Long testPatchProcessTaskComplete_getProcessId(Task task)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Task testPatchProcessTaskComplete_addTask() throws Exception {
		return testPostProcessTask_addTask(randomTask());
	}

	@Test
	public void testPostProcessTask() throws Exception {
		Task randomTask = randomTask();

		Task postTask = testPostProcessTask_addTask(randomTask);

		assertEquals(randomTask, postTask);
		assertValid(postTask);
	}

	protected Task testPostProcessTask_addTask(Task task) throws Exception {
		return taskResource.postProcessTask(
			testGetProcessTasksPage_getProcessId(), task);
	}

	@Test
	public void testGraphQLPostProcessTask() throws Exception {
		Task randomTask = randomTask();

		Task task = testGraphQLProcessTask_addTask(
			testGraphQLPostProcessTask_getProcessId(randomTask), randomTask);

		Assert.assertTrue(equals(randomTask, task));
	}

	protected Long testGraphQLPostProcessTask_getProcessId(Task task)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostTasksPage() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected Task testGraphQLTask_addTask() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Task testGraphQLProcessTask_addTask() throws Exception {
		return testGraphQLProcessTask_addTask(
			testGraphQLProcessTask_getProcessId(), randomTask());
	}

	protected Long testGraphQLProcessTask_getProcessId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Task testGraphQLProcessTask_addTask(Long processId, Task task)
		throws Exception {

		JSONDeserializer<Task> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Task.class)) {
			if (getGraphQLValue(field.get(task)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(task)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createProcessTask",
						new HashMap<String, Object>() {
							{
								put("processId", processId);
								put("task", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createProcessTask"),
			Task.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(Task task, List<Task> tasks) {
		boolean contains = false;

		for (Task item : tasks) {
			if (equals(task, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(tasks + " does not contain " + task, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Task task1, Task task2) {
		Assert.assertTrue(
			task1 + " does not equal " + task2, equals(task1, task2));
	}

	protected void assertEquals(List<Task> tasks1, List<Task> tasks2) {
		Assert.assertEquals(tasks1.size(), tasks2.size());

		for (int i = 0; i < tasks1.size(); i++) {
			Task task1 = tasks1.get(i);
			Task task2 = tasks2.get(i);

			assertEquals(task1, task2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Task> tasks1, List<Task> tasks2) {

		Assert.assertEquals(tasks1.size(), tasks2.size());

		for (Task task1 : tasks1) {
			boolean contains = false;

			for (Task task2 : tasks2) {
				if (equals(task1, task2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(tasks2 + " does not contain " + task1, contains);
		}
	}

	protected void assertValid(Task task) throws Exception {
		boolean valid = true;

		if (task.getDateCreated() == null) {
			valid = false;
		}

		if (task.getDateModified() == null) {
			valid = false;
		}

		if (task.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (task.getAssetTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetTitle_i18n", additionalAssertFieldName)) {
				if (task.getAssetTitle_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (task.getAssetType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetType_i18n", additionalAssertFieldName)) {
				if (task.getAssetType_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assignee", additionalAssertFieldName)) {
				if (task.getAssignee() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (task.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (task.getClassPK() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (task.getCompleted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completionUserId", additionalAssertFieldName)) {
				if (task.getCompletionUserId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (task.getDateCompletion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("duration", additionalAssertFieldName)) {
				if (task.getDuration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("instanceId", additionalAssertFieldName)) {
				if (task.getInstanceId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (task.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (task.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("nodeId", additionalAssertFieldName)) {
				if (task.getNodeId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("processId", additionalAssertFieldName)) {
				if (task.getProcessId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("processVersion", additionalAssertFieldName)) {
				if (task.getProcessVersion() == null) {
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

	protected void assertValid(Page<Task> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Task> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Task> tasks = page.getItems();

		int size = tasks.size();

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
					com.liferay.portal.workflow.metrics.rest.dto.v1_0.Task.
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

	protected boolean equals(Task task1, Task task2) {
		if (task1 == task2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getAssetTitle(), task2.getAssetTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetTitle_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)task1.getAssetTitle_i18n(),
						(Map)task2.getAssetTitle_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getAssetType(), task2.getAssetType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetType_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)task1.getAssetType_i18n(),
						(Map)task2.getAssetType_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assignee", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getAssignee(), task2.getAssignee())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getClassName(), task2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getClassPK(), task2.getClassPK())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getCompleted(), task2.getCompleted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completionUserId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getCompletionUserId(),
						task2.getCompletionUserId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDateCompletion(), task2.getDateCompletion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDateCreated(), task2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDateModified(), task2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("duration", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getDuration(), task2.getDuration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getId(), task2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("instanceId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getInstanceId(), task2.getInstanceId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getLabel(), task2.getLabel())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getName(), task2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("nodeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(task1.getNodeId(), task2.getNodeId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("processId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getProcessId(), task2.getProcessId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("processVersion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						task1.getProcessVersion(), task2.getProcessVersion())) {

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

		if (!(_taskResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_taskResource;

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
		EntityField entityField, String operator, Task task) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetTitle")) {
			Object object = task.getAssetTitle();

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

		if (entityFieldName.equals("assetTitle_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetType")) {
			Object object = task.getAssetType();

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

		if (entityFieldName.equals("assetType_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assignee")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("className")) {
			Object object = task.getClassName();

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

		if (entityFieldName.equals("classPK")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("completed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("completionUserId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCompletion")) {
			if (operator.equals("between")) {
				Date date = task.getDateCompletion();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(task.getDateCompletion()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = task.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(task.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = task.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(task.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("duration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("instanceId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("label")) {
			Object object = task.getLabel();

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
			Object object = task.getName();

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

		if (entityFieldName.equals("nodeId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("processId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("processVersion")) {
			Object object = task.getProcessVersion();

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

	protected Task randomTask() throws Exception {
		return new Task() {
			{
				assetTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				assetType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				classPK = RandomTestUtil.randomLong();
				completed = RandomTestUtil.randomBoolean();
				completionUserId = RandomTestUtil.randomLong();
				dateCompletion = RandomTestUtil.nextDate();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				duration = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				instanceId = RandomTestUtil.randomLong();
				label = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				nodeId = RandomTestUtil.randomLong();
				processId = RandomTestUtil.randomLong();
				processVersion = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Task randomIrrelevantTask() throws Exception {
		Task randomIrrelevantTask = randomTask();

		return randomIrrelevantTask;
	}

	protected Task randomPatchTask() throws Exception {
		return randomTask();
	}

	protected TaskResource taskResource;
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
		LogFactoryUtil.getLog(BaseTaskResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.workflow.metrics.rest.resource.v1_0.TaskResource
		_taskResource;

}