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

import com.liferay.petra.function.UnsafeTriConsumer;
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
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.client.http.HttpInvoker;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.client.resource.v1_0.InstanceResource;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.InstanceSerDes;

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
public abstract class BaseInstanceResourceTestCase {

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

		_instanceResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		instanceResource = InstanceResource.builder(
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

		Instance instance1 = randomInstance();

		String json = objectMapper.writeValueAsString(instance1);

		Instance instance2 = InstanceSerDes.toDTO(json);

		Assert.assertTrue(equals(instance1, instance2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Instance instance = randomInstance();

		String json1 = objectMapper.writeValueAsString(instance);
		String json2 = InstanceSerDes.toJSON(instance);

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

		Instance instance = randomInstance();

		instance.setAssetTitle(regex);
		instance.setAssetType(regex);
		instance.setClassName(regex);
		instance.setProcessVersion(regex);

		String json = InstanceSerDes.toJSON(instance);

		Assert.assertFalse(json.contains(regex));

		instance = InstanceSerDes.toDTO(json);

		Assert.assertEquals(regex, instance.getAssetTitle());
		Assert.assertEquals(regex, instance.getAssetType());
		Assert.assertEquals(regex, instance.getClassName());
		Assert.assertEquals(regex, instance.getProcessVersion());
	}

	@Test
	public void testDeleteProcessInstance() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Instance instance = testDeleteProcessInstance_addInstance();

		assertHttpResponseStatusCode(
			204,
			instanceResource.deleteProcessInstanceHttpResponse(
				testDeleteProcessInstance_getProcessId(instance),
				instance.getId()));

		assertHttpResponseStatusCode(
			404,
			instanceResource.getProcessInstanceHttpResponse(
				testDeleteProcessInstance_getProcessId(instance),
				instance.getId()));
		assertHttpResponseStatusCode(
			404,
			instanceResource.getProcessInstanceHttpResponse(
				testDeleteProcessInstance_getProcessId(instance), 0L));
	}

	protected Instance testDeleteProcessInstance_addInstance()
		throws Exception {

		return testPostProcessInstance_addInstance(randomInstance());
	}

	protected Long testDeleteProcessInstance_getProcessId(Instance instance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProcessInstance() throws Exception {

		// No namespace

		Instance instance1 = testGraphQLDeleteProcessInstance_addInstance();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProcessInstance",
						new HashMap<String, Object>() {
							{
								put(
									"processId",
									testGraphQLDeleteProcessInstance_getProcessId(
										instance1));
								put("instanceId", instance1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProcessInstance"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"processInstance",
					new HashMap<String, Object>() {
						{
							put(
								"processId",
								testGraphQLDeleteProcessInstance_getProcessId(
									instance1));
							put("instanceId", instance1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace portalWorkflowMetrics_v1_0

		Instance instance2 = testGraphQLDeleteProcessInstance_addInstance();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"portalWorkflowMetrics_v1_0",
						new GraphQLField(
							"deleteProcessInstance",
							new HashMap<String, Object>() {
								{
									put(
										"processId",
										testGraphQLDeleteProcessInstance_getProcessId(
											instance2));
									put("instanceId", instance2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/portalWorkflowMetrics_v1_0",
				"Object/deleteProcessInstance"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"portalWorkflowMetrics_v1_0",
					new GraphQLField(
						"processInstance",
						new HashMap<String, Object>() {
							{
								put(
									"processId",
									testGraphQLDeleteProcessInstance_getProcessId(
										instance2));
								put("instanceId", instance2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long testGraphQLDeleteProcessInstance_getProcessId(
			Instance instance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Instance testGraphQLDeleteProcessInstance_addInstance()
		throws Exception {

		return testGraphQLInstance_addInstance();
	}

	@Test
	public void testGetProcessInstance() throws Exception {
		Instance postInstance = testGetProcessInstance_addInstance();

		Instance getInstance = instanceResource.getProcessInstance(
			testGetProcessInstance_getProcessId(postInstance),
			postInstance.getId());

		assertEquals(postInstance, getInstance);
		assertValid(getInstance);
	}

	protected Instance testGetProcessInstance_addInstance() throws Exception {
		return testPostProcessInstance_addInstance(randomInstance());
	}

	protected Long testGetProcessInstance_getProcessId(Instance instance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProcessInstance() throws Exception {
		Instance instance = testGraphQLGetProcessInstance_addInstance();

		// No namespace

		Assert.assertTrue(
			equals(
				instance,
				InstanceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"processInstance",
								new HashMap<String, Object>() {
									{
										put(
											"processId",
											testGraphQLGetProcessInstance_getProcessId(
												instance));
										put("instanceId", instance.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/processInstance"))));

		// Using the namespace portalWorkflowMetrics_v1_0

		Assert.assertTrue(
			equals(
				instance,
				InstanceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"portalWorkflowMetrics_v1_0",
								new GraphQLField(
									"processInstance",
									new HashMap<String, Object>() {
										{
											put(
												"processId",
												testGraphQLGetProcessInstance_getProcessId(
													instance));
											put("instanceId", instance.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/portalWorkflowMetrics_v1_0",
						"Object/processInstance"))));
	}

	protected Long testGraphQLGetProcessInstance_getProcessId(Instance instance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProcessInstanceNotFound() throws Exception {
		Long irrelevantProcessId = RandomTestUtil.randomLong();
		Long irrelevantInstanceId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"processInstance",
						new HashMap<String, Object>() {
							{
								put("processId", irrelevantProcessId);
								put("instanceId", irrelevantInstanceId);
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
							"processInstance",
							new HashMap<String, Object>() {
								{
									put("processId", irrelevantProcessId);
									put("instanceId", irrelevantInstanceId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Instance testGraphQLGetProcessInstance_addInstance()
		throws Exception {

		return testGraphQLInstance_addInstance();
	}

	@Test
	public void testGetProcessInstancesPage() throws Exception {
		Long processId = testGetProcessInstancesPage_getProcessId();
		Long irrelevantProcessId =
			testGetProcessInstancesPage_getIrrelevantProcessId();

		Page<Instance> page = instanceResource.getProcessInstancesPage(
			processId, null, null, RandomTestUtil.nextDate(),
			RandomTestUtil.nextDate(), null, null, null, Pagination.of(1, 10),
			null);

		long totalCount = page.getTotalCount();

		if (irrelevantProcessId != null) {
			Instance irrelevantInstance =
				testGetProcessInstancesPage_addInstance(
					irrelevantProcessId, randomIrrelevantInstance());

			page = instanceResource.getProcessInstancesPage(
				irrelevantProcessId, null, null, null, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantInstance, (List<Instance>)page.getItems());
			assertValid(
				page,
				testGetProcessInstancesPage_getExpectedActions(
					irrelevantProcessId));
		}

		Instance instance1 = testGetProcessInstancesPage_addInstance(
			processId, randomInstance());

		Instance instance2 = testGetProcessInstancesPage_addInstance(
			processId, randomInstance());

		page = instanceResource.getProcessInstancesPage(
			processId, null, null, null, null, null, null, null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(instance1, (List<Instance>)page.getItems());
		assertContains(instance2, (List<Instance>)page.getItems());
		assertValid(
			page, testGetProcessInstancesPage_getExpectedActions(processId));
	}

	protected Map<String, Map<String, String>>
			testGetProcessInstancesPage_getExpectedActions(Long processId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/portal-workflow-metrics/v1.0/processes/{processId}/instances/batch".
				replace("{processId}", String.valueOf(processId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetProcessInstancesPageWithPagination() throws Exception {
		Long processId = testGetProcessInstancesPage_getProcessId();

		Page<Instance> instancesPage = instanceResource.getProcessInstancesPage(
			processId, null, null, null, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(instancesPage.getTotalCount());

		Instance instance1 = testGetProcessInstancesPage_addInstance(
			processId, randomInstance());

		Instance instance2 = testGetProcessInstancesPage_addInstance(
			processId, randomInstance());

		Instance instance3 = testGetProcessInstancesPage_addInstance(
			processId, randomInstance());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Instance> page1 = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(instance1, (List<Instance>)page1.getItems());

			Page<Instance> page2 = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(instance2, (List<Instance>)page2.getItems());

			Page<Instance> page3 = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(instance3, (List<Instance>)page3.getItems());
		}
		else {
			Page<Instance> page1 = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Instance> instances1 = (List<Instance>)page1.getItems();

			Assert.assertEquals(
				instances1.toString(), totalCount + 2, instances1.size());

			Page<Instance> page2 = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Instance> instances2 = (List<Instance>)page2.getItems();

			Assert.assertEquals(instances2.toString(), 1, instances2.size());

			Page<Instance> page3 = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(instance1, (List<Instance>)page3.getItems());
			assertContains(instance2, (List<Instance>)page3.getItems());
			assertContains(instance3, (List<Instance>)page3.getItems());
		}
	}

	@Test
	public void testGetProcessInstancesPageWithSortDateTime() throws Exception {
		testGetProcessInstancesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, instance1, instance2) -> {
				BeanTestUtil.setProperty(
					instance1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProcessInstancesPageWithSortDouble() throws Exception {
		testGetProcessInstancesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, instance1, instance2) -> {
				BeanTestUtil.setProperty(instance1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(instance2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProcessInstancesPageWithSortInteger() throws Exception {
		testGetProcessInstancesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, instance1, instance2) -> {
				BeanTestUtil.setProperty(instance1, entityField.getName(), 0);
				BeanTestUtil.setProperty(instance2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProcessInstancesPageWithSortString() throws Exception {
		testGetProcessInstancesPageWithSort(
			EntityField.Type.STRING,
			(entityField, instance1, instance2) -> {
				Class<?> clazz = instance1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						instance1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						instance2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						instance1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						instance2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						instance1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						instance2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetProcessInstancesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Instance, Instance, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long processId = testGetProcessInstancesPage_getProcessId();

		Instance instance1 = randomInstance();
		Instance instance2 = randomInstance();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, instance1, instance2);
		}

		instance1 = testGetProcessInstancesPage_addInstance(
			processId, instance1);

		instance2 = testGetProcessInstancesPage_addInstance(
			processId, instance2);

		Page<Instance> page = instanceResource.getProcessInstancesPage(
			processId, null, null, null, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Instance> ascPage = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(instance1, (List<Instance>)ascPage.getItems());
			assertContains(instance2, (List<Instance>)ascPage.getItems());

			Page<Instance> descPage = instanceResource.getProcessInstancesPage(
				processId, null, null, null, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(instance2, (List<Instance>)descPage.getItems());
			assertContains(instance1, (List<Instance>)descPage.getItems());
		}
	}

	protected Instance testGetProcessInstancesPage_addInstance(
			Long processId, Instance instance)
		throws Exception {

		return instanceResource.postProcessInstance(processId, instance);
	}

	protected Long testGetProcessInstancesPage_getProcessId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProcessInstancesPage_getIrrelevantProcessId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetProcessInstancesPage() throws Exception {
		Long processId = testGetProcessInstancesPage_getProcessId();

		GraphQLField graphQLField = new GraphQLField(
			"processInstances",
			new HashMap<String, Object>() {
				{
					put("processId", processId);
					put("dateEnd", getGraphQLValue(RandomTestUtil.nextDate()));
					put(
						"dateStart",
						getGraphQLValue(RandomTestUtil.nextDate()));
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject processInstancesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/processInstances");

		long totalCount = processInstancesJSONObject.getLong("totalCount");

		Instance instance1 = testGraphQLProcessInstance_addInstance(
			processId, randomInstance());

		Instance instance2 = testGraphQLProcessInstance_addInstance(
			processId, randomInstance());

		processInstancesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/processInstances");

		Assert.assertEquals(
			totalCount + 2, processInstancesJSONObject.getLong("totalCount"));

		assertContains(
			instance1,
			Arrays.asList(
				InstanceSerDes.toDTOs(
					processInstancesJSONObject.getString("items"))));
		assertContains(
			instance2,
			Arrays.asList(
				InstanceSerDes.toDTOs(
					processInstancesJSONObject.getString("items"))));

		// Using the namespace portalWorkflowMetrics_v1_0

		processInstancesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("portalWorkflowMetrics_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/portalWorkflowMetrics_v1_0",
			"JSONObject/processInstances");

		Assert.assertEquals(
			totalCount + 2, processInstancesJSONObject.getLong("totalCount"));

		assertContains(
			instance1,
			Arrays.asList(
				InstanceSerDes.toDTOs(
					processInstancesJSONObject.getString("items"))));
		assertContains(
			instance2,
			Arrays.asList(
				InstanceSerDes.toDTOs(
					processInstancesJSONObject.getString("items"))));
	}

	@Test
	public void testPatchProcessInstance() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Instance instance = testPatchProcessInstance_addInstance();

		assertHttpResponseStatusCode(
			204,
			instanceResource.patchProcessInstanceHttpResponse(
				testPatchProcessInstance_getProcessId(instance),
				instance.getId(), instance));

		assertHttpResponseStatusCode(
			404,
			instanceResource.patchProcessInstanceHttpResponse(
				testPatchProcessInstance_getProcessId(instance), 0L, instance));
	}

	protected Long testPatchProcessInstance_getProcessId(Instance instance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Instance testPatchProcessInstance_addInstance() throws Exception {
		return testPostProcessInstance_addInstance(randomInstance());
	}

	@Test
	public void testPatchProcessInstanceComplete() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Instance instance = testPatchProcessInstanceComplete_addInstance();

		assertHttpResponseStatusCode(
			204,
			instanceResource.patchProcessInstanceCompleteHttpResponse(
				testPatchProcessInstanceComplete_getProcessId(instance),
				instance.getId(), instance));

		assertHttpResponseStatusCode(
			404,
			instanceResource.patchProcessInstanceCompleteHttpResponse(
				testPatchProcessInstanceComplete_getProcessId(instance), 0L,
				instance));
	}

	protected Long testPatchProcessInstanceComplete_getProcessId(
			Instance instance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Instance testPatchProcessInstanceComplete_addInstance()
		throws Exception {

		return testPostProcessInstance_addInstance(randomInstance());
	}

	@Test
	public void testPostProcessInstance() throws Exception {
		Instance randomInstance = randomInstance();

		Instance postInstance = testPostProcessInstance_addInstance(
			randomInstance);

		assertEquals(randomInstance, postInstance);
		assertValid(postInstance);
	}

	protected Instance testPostProcessInstance_addInstance(Instance instance)
		throws Exception {

		return instanceResource.postProcessInstance(
			testGetProcessInstancesPage_getProcessId(), instance);
	}

	@Test
	public void testGraphQLPostProcessInstance() throws Exception {
		Instance randomInstance = randomInstance();

		Instance instance = testGraphQLProcessInstance_addInstance(
			testGraphQLPostProcessInstance_getProcessId(randomInstance),
			randomInstance);

		Assert.assertTrue(equals(randomInstance, instance));
	}

	protected Long testGraphQLPostProcessInstance_getProcessId(
			Instance instance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected Instance testGraphQLInstance_addInstance() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Instance testGraphQLProcessInstance_addInstance()
		throws Exception {

		return testGraphQLProcessInstance_addInstance(
			testGraphQLProcessInstance_getProcessId(), randomInstance());
	}

	protected Long testGraphQLProcessInstance_getProcessId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Instance testGraphQLProcessInstance_addInstance(
			Long processId, Instance instance)
		throws Exception {

		JSONDeserializer<Instance> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(Instance.class)) {

			if (getGraphQLValue(field.get(instance)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(instance)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createProcessInstance",
						new HashMap<String, Object>() {
							{
								put("processId", processId);
								put("instance", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createProcessInstance"),
			Instance.class);
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

	protected void assertContains(Instance instance, List<Instance> instances) {
		boolean contains = false;

		for (Instance item : instances) {
			if (equals(instance, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			instances + " does not contain " + instance, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Instance instance1, Instance instance2) {
		Assert.assertTrue(
			instance1 + " does not equal " + instance2,
			equals(instance1, instance2));
	}

	protected void assertEquals(
		List<Instance> instances1, List<Instance> instances2) {

		Assert.assertEquals(instances1.size(), instances2.size());

		for (int i = 0; i < instances1.size(); i++) {
			Instance instance1 = instances1.get(i);
			Instance instance2 = instances2.get(i);

			assertEquals(instance1, instance2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Instance> instances1, List<Instance> instances2) {

		Assert.assertEquals(instances1.size(), instances2.size());

		for (Instance instance1 : instances1) {
			boolean contains = false;

			for (Instance instance2 : instances2) {
				if (equals(instance1, instance2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				instances2 + " does not contain " + instance1, contains);
		}
	}

	protected void assertValid(Instance instance) throws Exception {
		boolean valid = true;

		if (instance.getDateCreated() == null) {
			valid = false;
		}

		if (instance.getDateModified() == null) {
			valid = false;
		}

		if (instance.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (instance.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (instance.getAssetTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetTitle_i18n", additionalAssertFieldName)) {
				if (instance.getAssetTitle_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (instance.getAssetType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetType_i18n", additionalAssertFieldName)) {
				if (instance.getAssetType_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assignees", additionalAssertFieldName)) {
				if (instance.getAssignees() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (instance.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (instance.getClassPK() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (instance.getCompleted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (instance.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (instance.getDateCompletion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("duration", additionalAssertFieldName)) {
				if (instance.getDuration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("processId", additionalAssertFieldName)) {
				if (instance.getProcessId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("processVersion", additionalAssertFieldName)) {
				if (instance.getProcessVersion() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("slaResults", additionalAssertFieldName)) {
				if (instance.getSlaResults() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("slaStatus", additionalAssertFieldName)) {
				if (instance.getSLAStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("taskNames", additionalAssertFieldName)) {
				if (instance.getTaskNames() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("transitions", additionalAssertFieldName)) {
				if (instance.getTransitions() == null) {
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

	protected void assertValid(Page<Instance> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Instance> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Instance> instances = page.getItems();

		int size = instances.size();

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
					com.liferay.portal.workflow.metrics.rest.dto.v1_0.Instance.
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

	protected boolean equals(Instance instance1, Instance instance2) {
		if (instance1 == instance2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getActive(), instance2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getAssetTitle(), instance2.getAssetTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetTitle_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)instance1.getAssetTitle_i18n(),
						(Map)instance2.getAssetTitle_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getAssetType(), instance2.getAssetType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetType_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)instance1.getAssetType_i18n(),
						(Map)instance2.getAssetType_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assignees", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getAssignees(), instance2.getAssignees())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getClassName(), instance2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("classPK", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getClassPK(), instance2.getClassPK())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("completed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getCompleted(), instance2.getCompleted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getCreator(), instance2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCompletion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getDateCompletion(),
						instance2.getDateCompletion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getDateCreated(),
						instance2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getDateModified(),
						instance2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("duration", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getDuration(), instance2.getDuration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(instance1.getId(), instance2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("processId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getProcessId(), instance2.getProcessId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("processVersion", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getProcessVersion(),
						instance2.getProcessVersion())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("slaResults", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getSlaResults(), instance2.getSlaResults())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("slaStatus", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getSLAStatus(), instance2.getSLAStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("taskNames", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getTaskNames(), instance2.getTaskNames())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("transitions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						instance1.getTransitions(),
						instance2.getTransitions())) {

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

		if (!(_instanceResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_instanceResource;

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
		EntityField entityField, String operator, Instance instance) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetTitle")) {
			Object object = instance.getAssetTitle();

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
			Object object = instance.getAssetType();

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

		if (entityFieldName.equals("assignees")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("className")) {
			Object object = instance.getClassName();

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCompletion")) {
			if (operator.equals("between")) {
				Date date = instance.getDateCompletion();

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

				sb.append(_format.format(instance.getDateCompletion()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = instance.getDateCreated();

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

				sb.append(_format.format(instance.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = instance.getDateModified();

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

				sb.append(_format.format(instance.getDateModified()));
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

		if (entityFieldName.equals("processId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("processVersion")) {
			Object object = instance.getProcessVersion();

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

		if (entityFieldName.equals("slaResults")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("slaStatus")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taskNames")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("transitions")) {
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

	protected Instance randomInstance() throws Exception {
		return new Instance() {
			{
				active = RandomTestUtil.randomBoolean();
				assetTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				assetType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				classPK = RandomTestUtil.randomLong();
				completed = RandomTestUtil.randomBoolean();
				dateCompletion = RandomTestUtil.nextDate();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				duration = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				processId = RandomTestUtil.randomLong();
				processVersion = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected Instance randomIrrelevantInstance() throws Exception {
		Instance randomIrrelevantInstance = randomInstance();

		return randomIrrelevantInstance;
	}

	protected Instance randomPatchInstance() throws Exception {
		return randomInstance();
	}

	protected InstanceResource instanceResource;
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
		LogFactoryUtil.getLog(BaseInstanceResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.portal.workflow.metrics.rest.resource.v1_0.InstanceResource
			_instanceResource;

}