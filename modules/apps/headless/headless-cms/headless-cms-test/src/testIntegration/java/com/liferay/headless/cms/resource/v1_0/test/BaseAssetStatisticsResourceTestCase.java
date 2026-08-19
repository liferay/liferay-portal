/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.cms.client.dto.v1_0.AssetStatistics;
import com.liferay.headless.cms.client.http.HttpInvoker;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.resource.v1_0.AssetStatisticsResource;
import com.liferay.headless.cms.client.serdes.v1_0.AssetStatisticsSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
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
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public abstract class BaseAssetStatisticsResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_assetStatisticsResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		assetStatisticsResource = AssetStatisticsResource.builder(
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

		AssetStatistics assetStatistics1 = randomAssetStatistics();

		String json = objectMapper.writeValueAsString(assetStatistics1);

		AssetStatistics assetStatistics2 = AssetStatisticsSerDes.toDTO(json);

		Assert.assertTrue(equals(assetStatistics1, assetStatistics2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AssetStatistics assetStatistics = randomAssetStatistics();

		String json1 = objectMapper.writeValueAsString(assetStatistics);
		String json2 = AssetStatisticsSerDes.toJSON(assetStatistics);

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

		AssetStatistics assetStatistics = randomAssetStatistics();

		String json = AssetStatisticsSerDes.toJSON(assetStatistics);

		Assert.assertFalse(json.contains(regex));

		assetStatistics = AssetStatisticsSerDes.toDTO(json);
	}

	@Test
	public void testGetAssetStatistics() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGraphQLGetAssetStatistics() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGraphQLGetAssetStatisticsNotFound() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		AssetStatistics assetStatistics,
		List<AssetStatistics> assetStatisticses) {

		boolean contains = false;

		for (AssetStatistics item : assetStatisticses) {
			if (equals(assetStatistics, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			assetStatisticses + " does not contain " + assetStatistics,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AssetStatistics assetStatistics1, AssetStatistics assetStatistics2) {

		Assert.assertTrue(
			assetStatistics1 + " does not equal " + assetStatistics2,
			equals(assetStatistics1, assetStatistics2));
	}

	protected void assertEquals(
		List<AssetStatistics> assetStatisticses1,
		List<AssetStatistics> assetStatisticses2) {

		Assert.assertEquals(
			assetStatisticses1.size(), assetStatisticses2.size());

		for (int i = 0; i < assetStatisticses1.size(); i++) {
			AssetStatistics assetStatistics1 = assetStatisticses1.get(i);
			AssetStatistics assetStatistics2 = assetStatisticses2.get(i);

			assertEquals(assetStatistics1, assetStatistics2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AssetStatistics> assetStatisticses1,
		List<AssetStatistics> assetStatisticses2) {

		Assert.assertEquals(
			assetStatisticses1.size(), assetStatisticses2.size());

		for (AssetStatistics assetStatistics1 : assetStatisticses1) {
			boolean contains = false;

			for (AssetStatistics assetStatistics2 : assetStatisticses2) {
				if (equals(assetStatistics1, assetStatistics2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				assetStatisticses2 + " does not contain " + assetStatistics1,
				contains);
		}
	}

	protected void assertValid(AssetStatistics assetStatistics)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("approvedCount", additionalAssertFieldName)) {
				if (assetStatistics.getApprovedCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("brokenLinksCount", additionalAssertFieldName)) {
				if (assetStatistics.getBrokenLinksCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expiredCount", additionalAssertFieldName)) {
				if (assetStatistics.getExpiredCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"expiringSoonCount", additionalAssertFieldName)) {

				if (assetStatistics.getExpiringSoonCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("inDraftCount", additionalAssertFieldName)) {
				if (assetStatistics.getInDraftCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pendingCount", additionalAssertFieldName)) {
				if (assetStatistics.getPendingCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"reviewDateOverdueCount", additionalAssertFieldName)) {

				if (assetStatistics.getReviewDateOverdueCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("scheduledCount", additionalAssertFieldName)) {
				if (assetStatistics.getScheduledCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("totalCount", additionalAssertFieldName)) {
				if (assetStatistics.getTotalCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"upcomingReviewCount", additionalAssertFieldName)) {

				if (assetStatistics.getUpcomingReviewCount() == null) {
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

	protected void assertValid(Page<AssetStatistics> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AssetStatistics> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AssetStatistics> assetStatisticses =
			page.getItems();

		int size = assetStatisticses.size();

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
					com.liferay.headless.cms.dto.v1_0.AssetStatistics.class)) {

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
		AssetStatistics assetStatistics1, AssetStatistics assetStatistics2) {

		if (assetStatistics1 == assetStatistics2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("approvedCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetStatistics1.getApprovedCount(),
						assetStatistics2.getApprovedCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("brokenLinksCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetStatistics1.getBrokenLinksCount(),
						assetStatistics2.getBrokenLinksCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expiredCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetStatistics1.getExpiredCount(),
						assetStatistics2.getExpiredCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"expiringSoonCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetStatistics1.getExpiringSoonCount(),
						assetStatistics2.getExpiringSoonCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("inDraftCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetStatistics1.getInDraftCount(),
						assetStatistics2.getInDraftCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pendingCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetStatistics1.getPendingCount(),
						assetStatistics2.getPendingCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"reviewDateOverdueCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetStatistics1.getReviewDateOverdueCount(),
						assetStatistics2.getReviewDateOverdueCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("scheduledCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetStatistics1.getScheduledCount(),
						assetStatistics2.getScheduledCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("totalCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetStatistics1.getTotalCount(),
						assetStatistics2.getTotalCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"upcomingReviewCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						assetStatistics1.getUpcomingReviewCount(),
						assetStatistics2.getUpcomingReviewCount())) {

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

		if (!(_assetStatisticsResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_assetStatisticsResource;

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
		AssetStatistics assetStatistics) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("approvedCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("brokenLinksCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("expiredCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("expiringSoonCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("inDraftCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pendingCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("reviewDateOverdueCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("scheduledCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalCount")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("upcomingReviewCount")) {
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

	protected AssetStatistics randomAssetStatistics() throws Exception {
		return new AssetStatistics() {
			{
				approvedCount = RandomTestUtil.randomLong();
				brokenLinksCount = RandomTestUtil.randomLong();
				expiredCount = RandomTestUtil.randomLong();
				expiringSoonCount = RandomTestUtil.randomLong();
				inDraftCount = RandomTestUtil.randomLong();
				pendingCount = RandomTestUtil.randomLong();
				reviewDateOverdueCount = RandomTestUtil.randomLong();
				scheduledCount = RandomTestUtil.randomLong();
				totalCount = RandomTestUtil.randomLong();
				upcomingReviewCount = RandomTestUtil.randomLong();
			}
		};
	}

	protected AssetStatistics randomIrrelevantAssetStatistics()
		throws Exception {

		AssetStatistics randomIrrelevantAssetStatistics =
			randomAssetStatistics();

		return randomIrrelevantAssetStatistics;
	}

	protected AssetStatistics randomPatchAssetStatistics() throws Exception {
		return randomAssetStatistics();
	}

	protected AssetStatisticsResource assetStatisticsResource;
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
		LogFactoryUtil.getLog(BaseAssetStatisticsResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.cms.resource.v1_0.AssetStatisticsResource
		_assetStatisticsResource;

}
// LIFERAY-REST-BUILDER-HASH:-1448042726