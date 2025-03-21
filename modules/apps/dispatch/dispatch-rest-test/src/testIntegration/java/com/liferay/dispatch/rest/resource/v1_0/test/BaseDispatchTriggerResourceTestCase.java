/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.dispatch.rest.client.dto.v1_0.DispatchTrigger;
import com.liferay.dispatch.rest.client.http.HttpInvoker;
import com.liferay.dispatch.rest.client.pagination.Page;
import com.liferay.dispatch.rest.client.resource.v1_0.DispatchTriggerResource;
import com.liferay.dispatch.rest.client.serdes.v1_0.DispatchTriggerSerDes;
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
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
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
 * @author Nilton Vieira
 * @generated
 */
@Generated("")
public abstract class BaseDispatchTriggerResourceTestCase {

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

		_dispatchTriggerResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		dispatchTriggerResource = DispatchTriggerResource.builder(
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

		DispatchTrigger dispatchTrigger1 = randomDispatchTrigger();

		String json = objectMapper.writeValueAsString(dispatchTrigger1);

		DispatchTrigger dispatchTrigger2 = DispatchTriggerSerDes.toDTO(json);

		Assert.assertTrue(equals(dispatchTrigger1, dispatchTrigger2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DispatchTrigger dispatchTrigger = randomDispatchTrigger();

		String json1 = objectMapper.writeValueAsString(dispatchTrigger);
		String json2 = DispatchTriggerSerDes.toJSON(dispatchTrigger);

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

		DispatchTrigger dispatchTrigger = randomDispatchTrigger();

		dispatchTrigger.setCronExpression(regex);
		dispatchTrigger.setDispatchTaskExecutorType(regex);
		dispatchTrigger.setExternalReferenceCode(regex);
		dispatchTrigger.setName(regex);
		dispatchTrigger.setTimeZoneId(regex);

		String json = DispatchTriggerSerDes.toJSON(dispatchTrigger);

		Assert.assertFalse(json.contains(regex));

		dispatchTrigger = DispatchTriggerSerDes.toDTO(json);

		Assert.assertEquals(regex, dispatchTrigger.getCronExpression());
		Assert.assertEquals(
			regex, dispatchTrigger.getDispatchTaskExecutorType());
		Assert.assertEquals(regex, dispatchTrigger.getExternalReferenceCode());
		Assert.assertEquals(regex, dispatchTrigger.getName());
		Assert.assertEquals(regex, dispatchTrigger.getTimeZoneId());
	}

	@Test
	public void testGetDispatchTriggersPage() throws Exception {
		Page<DispatchTrigger> page =
			dispatchTriggerResource.getDispatchTriggersPage();

		long totalCount = page.getTotalCount();

		DispatchTrigger dispatchTrigger1 =
			testGetDispatchTriggersPage_addDispatchTrigger(
				randomDispatchTrigger());

		DispatchTrigger dispatchTrigger2 =
			testGetDispatchTriggersPage_addDispatchTrigger(
				randomDispatchTrigger());

		page = dispatchTriggerResource.getDispatchTriggersPage();

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			dispatchTrigger1, (List<DispatchTrigger>)page.getItems());
		assertContains(
			dispatchTrigger2, (List<DispatchTrigger>)page.getItems());
		assertValid(page, testGetDispatchTriggersPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetDispatchTriggersPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected DispatchTrigger testGetDispatchTriggersPage_addDispatchTrigger(
			DispatchTrigger dispatchTrigger)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDispatchTriggersPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"dispatchTriggers",
			new HashMap<String, Object>() {
				{
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject dispatchTriggersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/dispatchTriggers");

		long totalCount = dispatchTriggersJSONObject.getLong("totalCount");

		DispatchTrigger dispatchTrigger1 =
			testGraphQLGetDispatchTriggersPage_addDispatchTrigger();
		DispatchTrigger dispatchTrigger2 =
			testGraphQLGetDispatchTriggersPage_addDispatchTrigger();

		dispatchTriggersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/dispatchTriggers");

		Assert.assertEquals(
			totalCount + 2, dispatchTriggersJSONObject.getLong("totalCount"));

		assertContains(
			dispatchTrigger1,
			Arrays.asList(
				DispatchTriggerSerDes.toDTOs(
					dispatchTriggersJSONObject.getString("items"))));
		assertContains(
			dispatchTrigger2,
			Arrays.asList(
				DispatchTriggerSerDes.toDTOs(
					dispatchTriggersJSONObject.getString("items"))));

		// Using the namespace dispatch_v1_0

		dispatchTriggersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(new GraphQLField("dispatch_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/dispatch_v1_0",
			"JSONObject/dispatchTriggers");

		Assert.assertEquals(
			totalCount + 2, dispatchTriggersJSONObject.getLong("totalCount"));

		assertContains(
			dispatchTrigger1,
			Arrays.asList(
				DispatchTriggerSerDes.toDTOs(
					dispatchTriggersJSONObject.getString("items"))));
		assertContains(
			dispatchTrigger2,
			Arrays.asList(
				DispatchTriggerSerDes.toDTOs(
					dispatchTriggersJSONObject.getString("items"))));
	}

	protected DispatchTrigger
			testGraphQLGetDispatchTriggersPage_addDispatchTrigger()
		throws Exception {

		return testGraphQLDispatchTrigger_addDispatchTrigger();
	}

	@Test
	public void testPostDispatchTrigger() throws Exception {
		DispatchTrigger randomDispatchTrigger = randomDispatchTrigger();

		DispatchTrigger postDispatchTrigger =
			testPostDispatchTrigger_addDispatchTrigger(randomDispatchTrigger);

		assertEquals(randomDispatchTrigger, postDispatchTrigger);
		assertValid(postDispatchTrigger);
	}

	protected DispatchTrigger testPostDispatchTrigger_addDispatchTrigger(
			DispatchTrigger dispatchTrigger)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDispatchTriggerRun() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DispatchTrigger dispatchTrigger =
			testPostDispatchTriggerRun_addDispatchTrigger();

		assertHttpResponseStatusCode(
			204,
			dispatchTriggerResource.postDispatchTriggerRunHttpResponse(
				dispatchTrigger.getId()));

		assertHttpResponseStatusCode(
			404,
			dispatchTriggerResource.postDispatchTriggerRunHttpResponse(0L));
	}

	protected DispatchTrigger testPostDispatchTriggerRun_addDispatchTrigger()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DispatchTrigger testGraphQLDispatchTrigger_addDispatchTrigger()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DispatchTrigger dispatchTrigger,
		List<DispatchTrigger> dispatchTriggers) {

		boolean contains = false;

		for (DispatchTrigger item : dispatchTriggers) {
			if (equals(dispatchTrigger, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			dispatchTriggers + " does not contain " + dispatchTrigger,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DispatchTrigger dispatchTrigger1, DispatchTrigger dispatchTrigger2) {

		Assert.assertTrue(
			dispatchTrigger1 + " does not equal " + dispatchTrigger2,
			equals(dispatchTrigger1, dispatchTrigger2));
	}

	protected void assertEquals(
		List<DispatchTrigger> dispatchTriggers1,
		List<DispatchTrigger> dispatchTriggers2) {

		Assert.assertEquals(dispatchTriggers1.size(), dispatchTriggers2.size());

		for (int i = 0; i < dispatchTriggers1.size(); i++) {
			DispatchTrigger dispatchTrigger1 = dispatchTriggers1.get(i);
			DispatchTrigger dispatchTrigger2 = dispatchTriggers2.get(i);

			assertEquals(dispatchTrigger1, dispatchTrigger2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DispatchTrigger> dispatchTriggers1,
		List<DispatchTrigger> dispatchTriggers2) {

		Assert.assertEquals(dispatchTriggers1.size(), dispatchTriggers2.size());

		for (DispatchTrigger dispatchTrigger1 : dispatchTriggers1) {
			boolean contains = false;

			for (DispatchTrigger dispatchTrigger2 : dispatchTriggers2) {
				if (equals(dispatchTrigger1, dispatchTrigger2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				dispatchTriggers2 + " does not contain " + dispatchTrigger1,
				contains);
		}
	}

	protected void assertValid(DispatchTrigger dispatchTrigger)
		throws Exception {

		boolean valid = true;

		if (dispatchTrigger.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (dispatchTrigger.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("companyId", additionalAssertFieldName)) {
				if (dispatchTrigger.getCompanyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("cronExpression", additionalAssertFieldName)) {
				if (dispatchTrigger.getCronExpression() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dispatchTaskClusterMode", additionalAssertFieldName)) {

				if (dispatchTrigger.getDispatchTaskClusterMode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dispatchTaskExecutorType", additionalAssertFieldName)) {

				if (dispatchTrigger.getDispatchTaskExecutorType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dispatchTaskSettings", additionalAssertFieldName)) {

				if (dispatchTrigger.getDispatchTaskSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("endDate", additionalAssertFieldName)) {
				if (dispatchTrigger.getEndDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (dispatchTrigger.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (dispatchTrigger.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("overlapAllowed", additionalAssertFieldName)) {
				if (dispatchTrigger.getOverlapAllowed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("startDate", additionalAssertFieldName)) {
				if (dispatchTrigger.getStartDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (dispatchTrigger.getSystem() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("timeZoneId", additionalAssertFieldName)) {
				if (dispatchTrigger.getTimeZoneId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (dispatchTrigger.getUserId() == null) {
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

	protected void assertValid(Page<DispatchTrigger> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DispatchTrigger> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DispatchTrigger> dispatchTriggers =
			page.getItems();

		int size = dispatchTriggers.size();

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
					com.liferay.dispatch.rest.dto.v1_0.DispatchTrigger.class)) {

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
		DispatchTrigger dispatchTrigger1, DispatchTrigger dispatchTrigger2) {

		if (dispatchTrigger1 == dispatchTrigger2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getActive(),
						dispatchTrigger2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("companyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getCompanyId(),
						dispatchTrigger2.getCompanyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("cronExpression", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getCronExpression(),
						dispatchTrigger2.getCronExpression())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dispatchTaskClusterMode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dispatchTrigger1.getDispatchTaskClusterMode(),
						dispatchTrigger2.getDispatchTaskClusterMode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dispatchTaskExecutorType", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dispatchTrigger1.getDispatchTaskExecutorType(),
						dispatchTrigger2.getDispatchTaskExecutorType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dispatchTaskSettings", additionalAssertFieldName)) {

				if (!equals(
						(Map)dispatchTrigger1.getDispatchTaskSettings(),
						(Map)dispatchTrigger2.getDispatchTaskSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("endDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getEndDate(),
						dispatchTrigger2.getEndDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dispatchTrigger1.getExternalReferenceCode(),
						dispatchTrigger2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getId(), dispatchTrigger2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getName(),
						dispatchTrigger2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("overlapAllowed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getOverlapAllowed(),
						dispatchTrigger2.getOverlapAllowed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("startDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getStartDate(),
						dispatchTrigger2.getStartDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getSystem(),
						dispatchTrigger2.getSystem())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("timeZoneId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getTimeZoneId(),
						dispatchTrigger2.getTimeZoneId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dispatchTrigger1.getUserId(),
						dispatchTrigger2.getUserId())) {

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

		if (!(_dispatchTriggerResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_dispatchTriggerResource;

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
		DispatchTrigger dispatchTrigger) {

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

		if (entityFieldName.equals("companyId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("cronExpression")) {
			Object object = dispatchTrigger.getCronExpression();

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

		if (entityFieldName.equals("dispatchTaskClusterMode")) {
			sb.append(
				String.valueOf(dispatchTrigger.getDispatchTaskClusterMode()));

			return sb.toString();
		}

		if (entityFieldName.equals("dispatchTaskExecutorType")) {
			Object object = dispatchTrigger.getDispatchTaskExecutorType();

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

		if (entityFieldName.equals("dispatchTaskSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("endDate")) {
			if (operator.equals("between")) {
				Date date = dispatchTrigger.getEndDate();

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

				sb.append(_format.format(dispatchTrigger.getEndDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = dispatchTrigger.getExternalReferenceCode();

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
			Object object = dispatchTrigger.getName();

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

		if (entityFieldName.equals("overlapAllowed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("startDate")) {
			if (operator.equals("between")) {
				Date date = dispatchTrigger.getStartDate();

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

				sb.append(_format.format(dispatchTrigger.getStartDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("system")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("timeZoneId")) {
			Object object = dispatchTrigger.getTimeZoneId();

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

		if (entityFieldName.equals("userId")) {
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

	protected DispatchTrigger randomDispatchTrigger() throws Exception {
		return new DispatchTrigger() {
			{
				active = RandomTestUtil.randomBoolean();
				companyId = RandomTestUtil.randomLong();
				cronExpression = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dispatchTaskClusterMode = RandomTestUtil.randomInt();
				dispatchTaskExecutorType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				endDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				overlapAllowed = RandomTestUtil.randomBoolean();
				startDate = RandomTestUtil.nextDate();
				system = RandomTestUtil.randomBoolean();
				timeZoneId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userId = RandomTestUtil.randomLong();
			}
		};
	}

	protected DispatchTrigger randomIrrelevantDispatchTrigger()
		throws Exception {

		DispatchTrigger randomIrrelevantDispatchTrigger =
			randomDispatchTrigger();

		return randomIrrelevantDispatchTrigger;
	}

	protected DispatchTrigger randomPatchDispatchTrigger() throws Exception {
		return randomDispatchTrigger();
	}

	protected DispatchTriggerResource dispatchTriggerResource;
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
		LogFactoryUtil.getLog(BaseDispatchTriggerResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.dispatch.rest.resource.v1_0.DispatchTriggerResource
		_dispatchTriggerResource;

}