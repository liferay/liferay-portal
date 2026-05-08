/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.resource.v1_0.test;

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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.SiteVisitorBehaviorMetric;
import com.liferay.site.dsr.analytics.rest.client.http.HttpInvoker;
import com.liferay.site.dsr.analytics.rest.client.pagination.Page;
import com.liferay.site.dsr.analytics.rest.client.resource.v1_0.SiteVisitorBehaviorMetricResource;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.SiteVisitorBehaviorMetricSerDes;

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
 * @author Gianmarco Brunialti
 * @generated
 */
@Generated("")
public abstract class BaseSiteVisitorBehaviorMetricResourceTestCase {

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

		_siteVisitorBehaviorMetricResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		siteVisitorBehaviorMetricResource =
			SiteVisitorBehaviorMetricResource.builder(
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

		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric1 =
			randomSiteVisitorBehaviorMetric();

		String json = objectMapper.writeValueAsString(
			siteVisitorBehaviorMetric1);

		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric2 =
			SiteVisitorBehaviorMetricSerDes.toDTO(json);

		Assert.assertTrue(
			equals(siteVisitorBehaviorMetric1, siteVisitorBehaviorMetric2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric =
			randomSiteVisitorBehaviorMetric();

		String json1 = objectMapper.writeValueAsString(
			siteVisitorBehaviorMetric);
		String json2 = SiteVisitorBehaviorMetricSerDes.toJSON(
			siteVisitorBehaviorMetric);

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

		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric =
			randomSiteVisitorBehaviorMetric();

		String json = SiteVisitorBehaviorMetricSerDes.toJSON(
			siteVisitorBehaviorMetric);

		Assert.assertFalse(json.contains(regex));

		siteVisitorBehaviorMetric = SiteVisitorBehaviorMetricSerDes.toDTO(json);
	}

	@Test
	public void testGetSiteVisitorBehaviorMetric() throws Exception {
		Assert.assertTrue(false);
	}

	protected void assertContains(
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric,
		List<SiteVisitorBehaviorMetric> siteVisitorBehaviorMetrics) {

		boolean contains = false;

		for (SiteVisitorBehaviorMetric item : siteVisitorBehaviorMetrics) {
			if (equals(siteVisitorBehaviorMetric, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			siteVisitorBehaviorMetrics + " does not contain " +
				siteVisitorBehaviorMetric,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric1,
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric2) {

		Assert.assertTrue(
			siteVisitorBehaviorMetric1 + " does not equal " +
				siteVisitorBehaviorMetric2,
			equals(siteVisitorBehaviorMetric1, siteVisitorBehaviorMetric2));
	}

	protected void assertEquals(
		List<SiteVisitorBehaviorMetric> siteVisitorBehaviorMetrics1,
		List<SiteVisitorBehaviorMetric> siteVisitorBehaviorMetrics2) {

		Assert.assertEquals(
			siteVisitorBehaviorMetrics1.size(),
			siteVisitorBehaviorMetrics2.size());

		for (int i = 0; i < siteVisitorBehaviorMetrics1.size(); i++) {
			SiteVisitorBehaviorMetric siteVisitorBehaviorMetric1 =
				siteVisitorBehaviorMetrics1.get(i);
			SiteVisitorBehaviorMetric siteVisitorBehaviorMetric2 =
				siteVisitorBehaviorMetrics2.get(i);

			assertEquals(
				siteVisitorBehaviorMetric1, siteVisitorBehaviorMetric2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SiteVisitorBehaviorMetric> siteVisitorBehaviorMetrics1,
		List<SiteVisitorBehaviorMetric> siteVisitorBehaviorMetrics2) {

		Assert.assertEquals(
			siteVisitorBehaviorMetrics1.size(),
			siteVisitorBehaviorMetrics2.size());

		for (SiteVisitorBehaviorMetric siteVisitorBehaviorMetric1 :
				siteVisitorBehaviorMetrics1) {

			boolean contains = false;

			for (SiteVisitorBehaviorMetric siteVisitorBehaviorMetric2 :
					siteVisitorBehaviorMetrics2) {

				if (equals(
						siteVisitorBehaviorMetric1,
						siteVisitorBehaviorMetric2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				siteVisitorBehaviorMetrics2 + " does not contain " +
					siteVisitorBehaviorMetric1,
				contains);
		}
	}

	protected void assertValid(
			SiteVisitorBehaviorMetric siteVisitorBehaviorMetric)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"averageSessionDuration", additionalAssertFieldName)) {

				if (siteVisitorBehaviorMetric.getAverageSessionDuration() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("knownVisitors", additionalAssertFieldName)) {
				if (siteVisitorBehaviorMetric.getKnownVisitors() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"totalSessionDuration", additionalAssertFieldName)) {

				if (siteVisitorBehaviorMetric.getTotalSessionDuration() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("visitors", additionalAssertFieldName)) {
				if (siteVisitorBehaviorMetric.getVisitors() == null) {
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

	protected void assertValid(Page<SiteVisitorBehaviorMetric> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SiteVisitorBehaviorMetric> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SiteVisitorBehaviorMetric>
			siteVisitorBehaviorMetrics = page.getItems();

		int size = siteVisitorBehaviorMetrics.size();

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
					com.liferay.site.dsr.analytics.rest.dto.v1_0.
						SiteVisitorBehaviorMetric.class)) {

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
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric1,
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric2) {

		if (siteVisitorBehaviorMetric1 == siteVisitorBehaviorMetric2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"averageSessionDuration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						siteVisitorBehaviorMetric1.getAverageSessionDuration(),
						siteVisitorBehaviorMetric2.
							getAverageSessionDuration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("knownVisitors", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						siteVisitorBehaviorMetric1.getKnownVisitors(),
						siteVisitorBehaviorMetric2.getKnownVisitors())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"totalSessionDuration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						siteVisitorBehaviorMetric1.getTotalSessionDuration(),
						siteVisitorBehaviorMetric2.getTotalSessionDuration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("visitors", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						siteVisitorBehaviorMetric1.getVisitors(),
						siteVisitorBehaviorMetric2.getVisitors())) {

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

		if (!(_siteVisitorBehaviorMetricResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_siteVisitorBehaviorMetricResource;

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
		SiteVisitorBehaviorMetric siteVisitorBehaviorMetric) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("averageSessionDuration")) {
			sb.append(
				String.valueOf(
					siteVisitorBehaviorMetric.getAverageSessionDuration()));

			return sb.toString();
		}

		if (entityFieldName.equals("knownVisitors")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalSessionDuration")) {
			sb.append(
				String.valueOf(
					siteVisitorBehaviorMetric.getTotalSessionDuration()));

			return sb.toString();
		}

		if (entityFieldName.equals("visitors")) {
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

	protected SiteVisitorBehaviorMetric randomSiteVisitorBehaviorMetric()
		throws Exception {

		return new SiteVisitorBehaviorMetric() {
			{
				averageSessionDuration = RandomTestUtil.randomDouble();
				knownVisitors = RandomTestUtil.randomLong();
				totalSessionDuration = RandomTestUtil.randomDouble();
				visitors = RandomTestUtil.randomLong();
			}
		};
	}

	protected SiteVisitorBehaviorMetric
			randomIrrelevantSiteVisitorBehaviorMetric()
		throws Exception {

		SiteVisitorBehaviorMetric randomIrrelevantSiteVisitorBehaviorMetric =
			randomSiteVisitorBehaviorMetric();

		return randomIrrelevantSiteVisitorBehaviorMetric;
	}

	protected SiteVisitorBehaviorMetric randomPatchSiteVisitorBehaviorMetric()
		throws Exception {

		return randomSiteVisitorBehaviorMetric();
	}

	protected SiteVisitorBehaviorMetricResource
		siteVisitorBehaviorMetricResource;
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
			BaseSiteVisitorBehaviorMetricResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.site.dsr.analytics.rest.resource.v1_0.
		SiteVisitorBehaviorMetricResource _siteVisitorBehaviorMetricResource;

}
// LIFERAY-REST-BUILDER-HASH:1974019214