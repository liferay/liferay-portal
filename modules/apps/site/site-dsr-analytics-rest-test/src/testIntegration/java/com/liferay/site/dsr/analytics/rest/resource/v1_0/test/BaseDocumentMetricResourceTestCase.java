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
import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.DocumentMetric;
import com.liferay.site.dsr.analytics.rest.client.dto.v1_0.DocumentMetrics;
import com.liferay.site.dsr.analytics.rest.client.http.HttpInvoker;
import com.liferay.site.dsr.analytics.rest.client.pagination.Page;
import com.liferay.site.dsr.analytics.rest.client.resource.v1_0.DocumentMetricResource;
import com.liferay.site.dsr.analytics.rest.client.serdes.v1_0.DocumentMetricSerDes;

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
public abstract class BaseDocumentMetricResourceTestCase {

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

		_documentMetricResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		documentMetricResource = DocumentMetricResource.builder(
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

		DocumentMetric documentMetric1 = randomDocumentMetric();

		String json = objectMapper.writeValueAsString(documentMetric1);

		DocumentMetric documentMetric2 = DocumentMetricSerDes.toDTO(json);

		Assert.assertTrue(equals(documentMetric1, documentMetric2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DocumentMetric documentMetric = randomDocumentMetric();

		String json1 = objectMapper.writeValueAsString(documentMetric);
		String json2 = DocumentMetricSerDes.toJSON(documentMetric);

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

		DocumentMetric documentMetric = randomDocumentMetric();

		documentMetric.setAssetId(regex);
		documentMetric.setAssetTitle(regex);

		String json = DocumentMetricSerDes.toJSON(documentMetric);

		Assert.assertFalse(json.contains(regex));

		documentMetric = DocumentMetricSerDes.toDTO(json);

		Assert.assertEquals(regex, documentMetric.getAssetId());
		Assert.assertEquals(regex, documentMetric.getAssetTitle());
	}

	@Test
	public void testGetDocumentMetric() throws Exception {
		DocumentMetric postDocumentMetric =
			testGetDocumentMetric_addDocumentMetric();

		DocumentMetrics postDocumentMetrics =
			testGetDocumentMetric_addDocumentMetrics(
				postDocumentMetric.getDocumentMetricId(),
				randomDocumentMetrics());

		DocumentMetrics getDocumentMetrics =
			documentMetricResource.getDocumentMetric(
				postDocumentMetric.getDocumentMetricId());

		assertEquals(postDocumentMetrics, getDocumentMetrics);
		assertValid(getDocumentMetrics);
	}

	protected DocumentMetrics testGetDocumentMetric_addDocumentMetrics(
			long documentMetricId, DocumentMetrics documentMetrics)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		DocumentMetric documentMetric, List<DocumentMetric> documentMetrics) {

		boolean contains = false;

		for (DocumentMetric item : documentMetrics) {
			if (equals(documentMetric, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			documentMetrics + " does not contain " + documentMetric, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DocumentMetric documentMetric1, DocumentMetric documentMetric2) {

		Assert.assertTrue(
			documentMetric1 + " does not equal " + documentMetric2,
			equals(documentMetric1, documentMetric2));
	}

	protected void assertEquals(
		List<DocumentMetric> documentMetrics1,
		List<DocumentMetric> documentMetrics2) {

		Assert.assertEquals(documentMetrics1.size(), documentMetrics2.size());

		for (int i = 0; i < documentMetrics1.size(); i++) {
			DocumentMetric documentMetric1 = documentMetrics1.get(i);
			DocumentMetric documentMetric2 = documentMetrics2.get(i);

			assertEquals(documentMetric1, documentMetric2);
		}
	}

	protected void assertEquals(
		DocumentMetrics documentMetrics1, DocumentMetrics documentMetrics2) {

		Assert.assertTrue(
			documentMetrics1 + " does not equal " + documentMetrics2,
			equals(documentMetrics1, documentMetrics2));
	}

	protected void assertEqualsIgnoringOrder(
		List<DocumentMetric> documentMetrics1,
		List<DocumentMetric> documentMetrics2) {

		Assert.assertEquals(documentMetrics1.size(), documentMetrics2.size());

		for (DocumentMetric documentMetric1 : documentMetrics1) {
			boolean contains = false;

			for (DocumentMetric documentMetric2 : documentMetrics2) {
				if (equals(documentMetric1, documentMetric2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				documentMetrics2 + " does not contain " + documentMetric1,
				contains);
		}
	}

	protected void assertValid(DocumentMetric documentMetric) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetId", additionalAssertFieldName)) {
				if (documentMetric.getAssetId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (documentMetric.getAssetTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("commentsMetric", additionalAssertFieldName)) {
				if (documentMetric.getCommentsMetric() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("downloadsMetric", additionalAssertFieldName)) {
				if (documentMetric.getDownloadsMetric() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"impressionMadeMetric", additionalAssertFieldName)) {

				if (documentMetric.getImpressionMadeMetric() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("lastViewedMetric", additionalAssertFieldName)) {
				if (documentMetric.getLastViewedMetric() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ratingsMetric", additionalAssertFieldName)) {
				if (documentMetric.getRatingsMetric() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("urls", additionalAssertFieldName)) {
				if (documentMetric.getUrls() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"usersInvolvedMetric", additionalAssertFieldName)) {

				if (documentMetric.getUsersInvolvedMetric() == null) {
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

	protected void assertValid(Page<DocumentMetric> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DocumentMetric> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DocumentMetric> documentMetrics = page.getItems();

		int size = documentMetrics.size();

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

	protected void assertValid(DocumentMetrics documentMetrics) {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalDocumentMetricsAssertFieldNames()) {

			if (Objects.equals("documentMetrics", additionalAssertFieldName)) {
				if (documentMetrics.getDocumentMetrics() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("total", additionalAssertFieldName)) {
				if (documentMetrics.getTotal() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalDocumentMetricsAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.site.dsr.analytics.rest.dto.v1_0.DocumentMetric.
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
		DocumentMetric documentMetric1, DocumentMetric documentMetric2) {

		if (documentMetric1 == documentMetric2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("assetId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetric1.getAssetId(),
						documentMetric2.getAssetId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetric1.getAssetTitle(),
						documentMetric2.getAssetTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("commentsMetric", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetric1.getCommentsMetric(),
						documentMetric2.getCommentsMetric())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("downloadsMetric", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetric1.getDownloadsMetric(),
						documentMetric2.getDownloadsMetric())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"impressionMadeMetric", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentMetric1.getImpressionMadeMetric(),
						documentMetric2.getImpressionMadeMetric())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("lastViewedMetric", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetric1.getLastViewedMetric(),
						documentMetric2.getLastViewedMetric())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("ratingsMetric", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetric1.getRatingsMetric(),
						documentMetric2.getRatingsMetric())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("urls", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetric1.getUrls(), documentMetric2.getUrls())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"usersInvolvedMetric", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentMetric1.getUsersInvolvedMetric(),
						documentMetric2.getUsersInvolvedMetric())) {

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

	protected boolean equals(
		DocumentMetrics documentMetrics1, DocumentMetrics documentMetrics2) {

		if (documentMetrics1 == documentMetrics2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalDocumentMetricsAssertFieldNames()) {

			if (Objects.equals("documentMetrics", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetrics1.getDocumentMetrics(),
						documentMetrics2.getDocumentMetrics())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("total", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentMetrics1.getTotal(),
						documentMetrics2.getTotal())) {

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

		if (!(_documentMetricResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_documentMetricResource;

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
		DocumentMetric documentMetric) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("assetId")) {
			Object object = documentMetric.getAssetId();

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

		if (entityFieldName.equals("assetTitle")) {
			Object object = documentMetric.getAssetTitle();

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

		if (entityFieldName.equals("commentsMetric")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("downloadsMetric")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("impressionMadeMetric")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("lastViewedMetric")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("ratingsMetric")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("urls")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("usersInvolvedMetric")) {
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

	protected DocumentMetric randomDocumentMetric() throws Exception {
		return new DocumentMetric() {
			{
				assetId = StringUtil.toLowerCase(RandomTestUtil.randomString());
				assetTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected DocumentMetric randomIrrelevantDocumentMetric() throws Exception {
		DocumentMetric randomIrrelevantDocumentMetric = randomDocumentMetric();

		return randomIrrelevantDocumentMetric;
	}

	protected DocumentMetric randomPatchDocumentMetric() throws Exception {
		return randomDocumentMetric();
	}

	protected DocumentMetrics randomDocumentMetrics() throws Exception {
		return new DocumentMetrics() {
			{
				total = RandomTestUtil.randomLong();
			}
		};
	}

	protected DocumentMetricResource documentMetricResource;
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
		LogFactoryUtil.getLog(BaseDocumentMetricResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.site.dsr.analytics.rest.resource.v1_0.DocumentMetricResource
			_documentMetricResource;

}
// LIFERAY-REST-BUILDER-HASH:-972957331