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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.NodeMetric;
import com.liferay.portal.workflow.metrics.rest.client.http.HttpInvoker;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Page;
import com.liferay.portal.workflow.metrics.rest.client.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.client.resource.v1_0.NodeMetricResource;
import com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0.NodeMetricSerDes;

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
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public abstract class BaseNodeMetricResourceTestCase {

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

		_nodeMetricResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		nodeMetricResource = NodeMetricResource.builder(
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

		NodeMetric nodeMetric1 = randomNodeMetric();

		String json = objectMapper.writeValueAsString(nodeMetric1);

		NodeMetric nodeMetric2 = NodeMetricSerDes.toDTO(json);

		Assert.assertTrue(equals(nodeMetric1, nodeMetric2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		NodeMetric nodeMetric = randomNodeMetric();

		String json1 = objectMapper.writeValueAsString(nodeMetric);
		String json2 = NodeMetricSerDes.toJSON(nodeMetric);

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

		NodeMetric nodeMetric = randomNodeMetric();

		String json = NodeMetricSerDes.toJSON(nodeMetric);

		Assert.assertFalse(json.contains(regex));

		nodeMetric = NodeMetricSerDes.toDTO(json);
	}

	@Test
	public void testGetProcessNodeMetricsPage() throws Exception {
		Long processId = testGetProcessNodeMetricsPage_getProcessId();
		Long irrelevantProcessId =
			testGetProcessNodeMetricsPage_getIrrelevantProcessId();

		Page<NodeMetric> page = nodeMetricResource.getProcessNodeMetricsPage(
			processId, null, RandomTestUtil.nextDate(),
			RandomTestUtil.nextDate(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantProcessId != null) {
			NodeMetric irrelevantNodeMetric =
				testGetProcessNodeMetricsPage_addNodeMetric(
					irrelevantProcessId, randomIrrelevantNodeMetric());

			page = nodeMetricResource.getProcessNodeMetricsPage(
				irrelevantProcessId, null, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantNodeMetric, (List<NodeMetric>)page.getItems());
			assertValid(
				page,
				testGetProcessNodeMetricsPage_getExpectedActions(
					irrelevantProcessId));
		}

		NodeMetric nodeMetric1 = testGetProcessNodeMetricsPage_addNodeMetric(
			processId, randomNodeMetric());

		NodeMetric nodeMetric2 = testGetProcessNodeMetricsPage_addNodeMetric(
			processId, randomNodeMetric());

		page = nodeMetricResource.getProcessNodeMetricsPage(
			processId, null, null, null, null, null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(nodeMetric1, (List<NodeMetric>)page.getItems());
		assertContains(nodeMetric2, (List<NodeMetric>)page.getItems());
		assertValid(
			page, testGetProcessNodeMetricsPage_getExpectedActions(processId));
	}

	protected Map<String, Map<String, String>>
			testGetProcessNodeMetricsPage_getExpectedActions(Long processId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProcessNodeMetricsPageWithPagination() throws Exception {
		Long processId = testGetProcessNodeMetricsPage_getProcessId();

		Page<NodeMetric> nodeMetricsPage =
			nodeMetricResource.getProcessNodeMetricsPage(
				processId, null, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(nodeMetricsPage.getTotalCount());

		NodeMetric nodeMetric1 = testGetProcessNodeMetricsPage_addNodeMetric(
			processId, randomNodeMetric());

		NodeMetric nodeMetric2 = testGetProcessNodeMetricsPage_addNodeMetric(
			processId, randomNodeMetric());

		NodeMetric nodeMetric3 = testGetProcessNodeMetricsPage_addNodeMetric(
			processId, randomNodeMetric());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<NodeMetric> page1 =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(nodeMetric1, (List<NodeMetric>)page1.getItems());

			Page<NodeMetric> page2 =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(nodeMetric2, (List<NodeMetric>)page2.getItems());

			Page<NodeMetric> page3 =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(nodeMetric3, (List<NodeMetric>)page3.getItems());
		}
		else {
			Page<NodeMetric> page1 =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<NodeMetric> nodeMetrics1 = (List<NodeMetric>)page1.getItems();

			Assert.assertEquals(
				nodeMetrics1.toString(), totalCount + 2, nodeMetrics1.size());

			Page<NodeMetric> page2 =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<NodeMetric> nodeMetrics2 = (List<NodeMetric>)page2.getItems();

			Assert.assertEquals(
				nodeMetrics2.toString(), 1, nodeMetrics2.size());

			Page<NodeMetric> page3 =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(nodeMetric1, (List<NodeMetric>)page3.getItems());
			assertContains(nodeMetric2, (List<NodeMetric>)page3.getItems());
			assertContains(nodeMetric3, (List<NodeMetric>)page3.getItems());
		}
	}

	@Test
	public void testGetProcessNodeMetricsPageWithSortDateTime()
		throws Exception {

		testGetProcessNodeMetricsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, nodeMetric1, nodeMetric2) -> {
				BeanTestUtil.setProperty(
					nodeMetric1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProcessNodeMetricsPageWithSortDouble() throws Exception {
		testGetProcessNodeMetricsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, nodeMetric1, nodeMetric2) -> {
				BeanTestUtil.setProperty(
					nodeMetric1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					nodeMetric2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetProcessNodeMetricsPageWithSortInteger()
		throws Exception {

		testGetProcessNodeMetricsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, nodeMetric1, nodeMetric2) -> {
				BeanTestUtil.setProperty(nodeMetric1, entityField.getName(), 0);
				BeanTestUtil.setProperty(nodeMetric2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProcessNodeMetricsPageWithSortString() throws Exception {
		testGetProcessNodeMetricsPageWithSort(
			EntityField.Type.STRING,
			(entityField, nodeMetric1, nodeMetric2) -> {
				Class<?> clazz = nodeMetric1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						nodeMetric1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						nodeMetric2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						nodeMetric1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						nodeMetric2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						nodeMetric1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						nodeMetric2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetProcessNodeMetricsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, NodeMetric, NodeMetric, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long processId = testGetProcessNodeMetricsPage_getProcessId();

		NodeMetric nodeMetric1 = randomNodeMetric();
		NodeMetric nodeMetric2 = randomNodeMetric();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, nodeMetric1, nodeMetric2);
		}

		nodeMetric1 = testGetProcessNodeMetricsPage_addNodeMetric(
			processId, nodeMetric1);

		nodeMetric2 = testGetProcessNodeMetricsPage_addNodeMetric(
			processId, nodeMetric2);

		Page<NodeMetric> page = nodeMetricResource.getProcessNodeMetricsPage(
			processId, null, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<NodeMetric> ascPage =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(nodeMetric1, (List<NodeMetric>)ascPage.getItems());
			assertContains(nodeMetric2, (List<NodeMetric>)ascPage.getItems());

			Page<NodeMetric> descPage =
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, null, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(nodeMetric2, (List<NodeMetric>)descPage.getItems());
			assertContains(nodeMetric1, (List<NodeMetric>)descPage.getItems());
		}
	}

	protected NodeMetric testGetProcessNodeMetricsPage_addNodeMetric(
			Long processId, NodeMetric nodeMetric)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProcessNodeMetricsPage_getProcessId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProcessNodeMetricsPage_getIrrelevantProcessId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		NodeMetric nodeMetric, List<NodeMetric> nodeMetrics) {

		boolean contains = false;

		for (NodeMetric item : nodeMetrics) {
			if (equals(nodeMetric, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			nodeMetrics + " does not contain " + nodeMetric, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		NodeMetric nodeMetric1, NodeMetric nodeMetric2) {

		Assert.assertTrue(
			nodeMetric1 + " does not equal " + nodeMetric2,
			equals(nodeMetric1, nodeMetric2));
	}

	protected void assertEquals(
		List<NodeMetric> nodeMetrics1, List<NodeMetric> nodeMetrics2) {

		Assert.assertEquals(nodeMetrics1.size(), nodeMetrics2.size());

		for (int i = 0; i < nodeMetrics1.size(); i++) {
			NodeMetric nodeMetric1 = nodeMetrics1.get(i);
			NodeMetric nodeMetric2 = nodeMetrics2.get(i);

			assertEquals(nodeMetric1, nodeMetric2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<NodeMetric> nodeMetrics1, List<NodeMetric> nodeMetrics2) {

		Assert.assertEquals(nodeMetrics1.size(), nodeMetrics2.size());

		for (NodeMetric nodeMetric1 : nodeMetrics1) {
			boolean contains = false;

			for (NodeMetric nodeMetric2 : nodeMetrics2) {
				if (equals(nodeMetric1, nodeMetric2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				nodeMetrics2 + " does not contain " + nodeMetric1, contains);
		}
	}

	protected void assertValid(NodeMetric nodeMetric) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"breachedInstanceCount", additionalAssertFieldName)) {

				if (nodeMetric.getBreachedInstanceCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"breachedInstancePercentage", additionalAssertFieldName)) {

				if (nodeMetric.getBreachedInstancePercentage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("durationAvg", additionalAssertFieldName)) {
				if (nodeMetric.getDurationAvg() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("instanceCount", additionalAssertFieldName)) {
				if (nodeMetric.getInstanceCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("node", additionalAssertFieldName)) {
				if (nodeMetric.getNode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"onTimeInstanceCount", additionalAssertFieldName)) {

				if (nodeMetric.getOnTimeInstanceCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"overdueInstanceCount", additionalAssertFieldName)) {

				if (nodeMetric.getOverdueInstanceCount() == null) {
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

	protected void assertValid(Page<NodeMetric> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<NodeMetric> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<NodeMetric> nodeMetrics = page.getItems();

		int size = nodeMetrics.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.portal.workflow.metrics.rest.dto.v1_0.
						NodeMetric.class)) {

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

	protected boolean equals(NodeMetric nodeMetric1, NodeMetric nodeMetric2) {
		if (nodeMetric1 == nodeMetric2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"breachedInstanceCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						nodeMetric1.getBreachedInstanceCount(),
						nodeMetric2.getBreachedInstanceCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"breachedInstancePercentage", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						nodeMetric1.getBreachedInstancePercentage(),
						nodeMetric2.getBreachedInstancePercentage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("durationAvg", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						nodeMetric1.getDurationAvg(),
						nodeMetric2.getDurationAvg())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("instanceCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						nodeMetric1.getInstanceCount(),
						nodeMetric2.getInstanceCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("node", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						nodeMetric1.getNode(), nodeMetric2.getNode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"onTimeInstanceCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						nodeMetric1.getOnTimeInstanceCount(),
						nodeMetric2.getOnTimeInstanceCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"overdueInstanceCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						nodeMetric1.getOverdueInstanceCount(),
						nodeMetric2.getOverdueInstanceCount())) {

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

		if (!(_nodeMetricResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_nodeMetricResource;

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
		EntityField entityField, String operator, NodeMetric nodeMetric) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("breachedInstanceCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("breachedInstancePercentage")) {
			sb.append(
				String.valueOf(nodeMetric.getBreachedInstancePercentage()));

			return sb.toString();
		}

		if (entityFieldName.equals("durationAvg")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("instanceCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("node")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("onTimeInstanceCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("overdueInstanceCount")) {
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

	protected NodeMetric randomNodeMetric() throws Exception {
		return new NodeMetric() {
			{
				breachedInstanceCount = RandomTestUtil.randomLong();
				breachedInstancePercentage = RandomTestUtil.randomDouble();
				durationAvg = RandomTestUtil.randomLong();
				instanceCount = RandomTestUtil.randomLong();
				onTimeInstanceCount = RandomTestUtil.randomLong();
				overdueInstanceCount = RandomTestUtil.randomLong();
			}
		};
	}

	protected NodeMetric randomIrrelevantNodeMetric() throws Exception {
		NodeMetric randomIrrelevantNodeMetric = randomNodeMetric();

		return randomIrrelevantNodeMetric;
	}

	protected NodeMetric randomPatchNodeMetric() throws Exception {
		return randomNodeMetric();
	}

	protected NodeMetricResource nodeMetricResource;
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
		LogFactoryUtil.getLog(BaseNodeMetricResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.portal.workflow.metrics.rest.resource.v1_0.
			NodeMetricResource _nodeMetricResource;

}