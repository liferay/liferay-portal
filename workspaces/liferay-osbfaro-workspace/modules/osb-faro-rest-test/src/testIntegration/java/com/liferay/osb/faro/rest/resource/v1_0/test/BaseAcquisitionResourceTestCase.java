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

import com.liferay.osb.faro.rest.client.dto.v1_0.Acquisition;
import com.liferay.osb.faro.rest.client.http.HttpInvoker;
import com.liferay.osb.faro.rest.client.pagination.Page;
import com.liferay.osb.faro.rest.client.pagination.Pagination;
import com.liferay.osb.faro.rest.client.resource.v1_0.AcquisitionResource;
import com.liferay.osb.faro.rest.client.serdes.v1_0.AcquisitionSerDes;
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
public abstract class BaseAcquisitionResourceTestCase {

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

		_acquisitionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		acquisitionResource = AcquisitionResource.builder(
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

		Acquisition acquisition1 = randomAcquisition();

		String json = objectMapper.writeValueAsString(acquisition1);

		Acquisition acquisition2 = AcquisitionSerDes.toDTO(json);

		Assert.assertTrue(equals(acquisition1, acquisition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Acquisition acquisition = randomAcquisition();

		String json1 = objectMapper.writeValueAsString(acquisition);
		String json2 = AcquisitionSerDes.toJSON(acquisition);

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

		Acquisition acquisition = randomAcquisition();

		acquisition.setName(regex);

		String json = AcquisitionSerDes.toJSON(acquisition);

		Assert.assertFalse(json.contains(regex));

		acquisition = AcquisitionSerDes.toDTO(json);

		Assert.assertEquals(regex, acquisition.getName());
	}

	@Test
	public void testGetWorkspaceGroupChannelAcquisitionsPage()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAcquisitionsPage_getGroupId();
		Long irrelevantGroupId =
			testGetWorkspaceGroupChannelAcquisitionsPage_getIrrelevantGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAcquisitionsPage_getChannelId();
		String irrelevantChannelId =
			testGetWorkspaceGroupChannelAcquisitionsPage_getIrrelevantChannelId();

		Page<Acquisition> page =
			acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
				groupId, channelId, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantGroupId != null) && (irrelevantChannelId != null)) {
			Acquisition irrelevantAcquisition =
				testGetWorkspaceGroupChannelAcquisitionsPage_addAcquisition(
					irrelevantGroupId, irrelevantChannelId,
					randomIrrelevantAcquisition());

			page = acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
				irrelevantGroupId, irrelevantChannelId, null, null, null, null,
				null, null, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAcquisition, (List<Acquisition>)page.getItems());
			assertValid(
				page,
				testGetWorkspaceGroupChannelAcquisitionsPage_getExpectedActions(
					irrelevantGroupId, irrelevantChannelId));
		}

		Acquisition acquisition1 =
			testGetWorkspaceGroupChannelAcquisitionsPage_addAcquisition(
				groupId, channelId, randomAcquisition());

		Acquisition acquisition2 =
			testGetWorkspaceGroupChannelAcquisitionsPage_addAcquisition(
				groupId, channelId, randomAcquisition());

		page = acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
			groupId, channelId, null, null, null, null, null, null,
			Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(acquisition1, (List<Acquisition>)page.getItems());
		assertContains(acquisition2, (List<Acquisition>)page.getItems());
		assertValid(
			page,
			testGetWorkspaceGroupChannelAcquisitionsPage_getExpectedActions(
				groupId, channelId));
	}

	protected Map<String, Map<String, String>>
			testGetWorkspaceGroupChannelAcquisitionsPage_getExpectedActions(
				Long groupId, String channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWorkspaceGroupChannelAcquisitionsPageWithPagination()
		throws Exception {

		Long groupId =
			testGetWorkspaceGroupChannelAcquisitionsPage_getGroupId();
		String channelId =
			testGetWorkspaceGroupChannelAcquisitionsPage_getChannelId();

		Page<Acquisition> acquisitionsPage =
			acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
				groupId, channelId, null, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			acquisitionsPage.getTotalCount());

		Acquisition acquisition1 =
			testGetWorkspaceGroupChannelAcquisitionsPage_addAcquisition(
				groupId, channelId, randomAcquisition());

		Acquisition acquisition2 =
			testGetWorkspaceGroupChannelAcquisitionsPage_addAcquisition(
				groupId, channelId, randomAcquisition());

		Acquisition acquisition3 =
			testGetWorkspaceGroupChannelAcquisitionsPage_addAcquisition(
				groupId, channelId, randomAcquisition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Acquisition> page1 =
				acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
					groupId, channelId, null, null, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(acquisition1, (List<Acquisition>)page1.getItems());

			Page<Acquisition> page2 =
				acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
					groupId, channelId, null, null, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(acquisition2, (List<Acquisition>)page2.getItems());

			Page<Acquisition> page3 =
				acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
					groupId, channelId, null, null, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(acquisition3, (List<Acquisition>)page3.getItems());
		}
		else {
			Page<Acquisition> page1 =
				acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
					groupId, channelId, null, null, null, null, null, null,
					Pagination.of(1, totalCount + 2));

			List<Acquisition> acquisitions1 =
				(List<Acquisition>)page1.getItems();

			Assert.assertEquals(
				acquisitions1.toString(), totalCount + 2, acquisitions1.size());

			Page<Acquisition> page2 =
				acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
					groupId, channelId, null, null, null, null, null, null,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Acquisition> acquisitions2 =
				(List<Acquisition>)page2.getItems();

			Assert.assertEquals(
				acquisitions2.toString(), 1, acquisitions2.size());

			Page<Acquisition> page3 =
				acquisitionResource.getWorkspaceGroupChannelAcquisitionsPage(
					groupId, channelId, null, null, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(acquisition1, (List<Acquisition>)page3.getItems());
			assertContains(acquisition2, (List<Acquisition>)page3.getItems());
			assertContains(acquisition3, (List<Acquisition>)page3.getItems());
		}
	}

	protected Acquisition
			testGetWorkspaceGroupChannelAcquisitionsPage_addAcquisition(
				Long groupId, String channelId, Acquisition acquisition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWorkspaceGroupChannelAcquisitionsPage_getGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWorkspaceGroupChannelAcquisitionsPage_getIrrelevantGroupId()
		throws Exception {

		return null;
	}

	protected String testGetWorkspaceGroupChannelAcquisitionsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWorkspaceGroupChannelAcquisitionsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		Acquisition acquisition, List<Acquisition> acquisitions) {

		boolean contains = false;

		for (Acquisition item : acquisitions) {
			if (equals(acquisition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			acquisitions + " does not contain " + acquisition, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		Acquisition acquisition1, Acquisition acquisition2) {

		Assert.assertTrue(
			acquisition1 + " does not equal " + acquisition2,
			equals(acquisition1, acquisition2));
	}

	protected void assertEquals(
		List<Acquisition> acquisitions1, List<Acquisition> acquisitions2) {

		Assert.assertEquals(acquisitions1.size(), acquisitions2.size());

		for (int i = 0; i < acquisitions1.size(); i++) {
			Acquisition acquisition1 = acquisitions1.get(i);
			Acquisition acquisition2 = acquisitions2.get(i);

			assertEquals(acquisition1, acquisition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Acquisition> acquisitions1, List<Acquisition> acquisitions2) {

		Assert.assertEquals(acquisitions1.size(), acquisitions2.size());

		for (Acquisition acquisition1 : acquisitions1) {
			boolean contains = false;

			for (Acquisition acquisition2 : acquisitions2) {
				if (equals(acquisition1, acquisition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				acquisitions2 + " does not contain " + acquisition1, contains);
		}
	}

	protected void assertValid(Acquisition acquisition) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("count", additionalAssertFieldName)) {
				if (acquisition.getCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (acquisition.getName() == null) {
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

	protected void assertValid(Page<Acquisition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Acquisition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Acquisition> acquisitions = page.getItems();

		int size = acquisitions.size();

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
					com.liferay.osb.faro.rest.dto.v1_0.Acquisition.class)) {

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
		Acquisition acquisition1, Acquisition acquisition2) {

		if (acquisition1 == acquisition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("count", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						acquisition1.getCount(), acquisition2.getCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						acquisition1.getName(), acquisition2.getName())) {

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

		if (!(_acquisitionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_acquisitionResource;

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
		EntityField entityField, String operator, Acquisition acquisition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("count")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = acquisition.getName();

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

	protected Acquisition randomAcquisition() throws Exception {
		return new Acquisition() {
			{
				count = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Acquisition randomIrrelevantAcquisition() throws Exception {
		Acquisition randomIrrelevantAcquisition = randomAcquisition();

		return randomIrrelevantAcquisition;
	}

	protected Acquisition randomPatchAcquisition() throws Exception {
		return randomAcquisition();
	}

	protected AcquisitionResource acquisitionResource;
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
		LogFactoryUtil.getLog(BaseAcquisitionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.osb.faro.rest.resource.v1_0.AcquisitionResource
		_acquisitionResource;

}
// LIFERAY-REST-BUILDER-HASH:-1681112211