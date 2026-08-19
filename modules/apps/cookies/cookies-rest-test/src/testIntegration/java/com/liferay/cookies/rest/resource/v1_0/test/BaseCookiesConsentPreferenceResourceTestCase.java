/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.cookies.rest.client.dto.v1_0.CookiesConsentPreference;
import com.liferay.cookies.rest.client.http.HttpInvoker;
import com.liferay.cookies.rest.client.pagination.Page;
import com.liferay.cookies.rest.client.resource.v1_0.CookiesConsentPreferenceResource;
import com.liferay.cookies.rest.client.serdes.v1_0.CookiesConsentPreferenceSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
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
 * @author Christopher Kian
 * @generated
 */
@Generated("")
public abstract class BaseCookiesConsentPreferenceResourceTestCase {

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

		_cookiesConsentPreferenceResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		cookiesConsentPreferenceResource =
			CookiesConsentPreferenceResource.builder(
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

		CookiesConsentPreference cookiesConsentPreference1 =
			randomCookiesConsentPreference();

		String json = objectMapper.writeValueAsString(
			cookiesConsentPreference1);

		CookiesConsentPreference cookiesConsentPreference2 =
			CookiesConsentPreferenceSerDes.toDTO(json);

		Assert.assertTrue(
			equals(cookiesConsentPreference1, cookiesConsentPreference2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		CookiesConsentPreference cookiesConsentPreference =
			randomCookiesConsentPreference();

		String json1 = objectMapper.writeValueAsString(
			cookiesConsentPreference);
		String json2 = CookiesConsentPreferenceSerDes.toJSON(
			cookiesConsentPreference);

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

		CookiesConsentPreference cookiesConsentPreference =
			randomCookiesConsentPreference();

		cookiesConsentPreference.setDomain(regex);
		cookiesConsentPreference.setName(regex);
		cookiesConsentPreference.setValue(regex);

		String json = CookiesConsentPreferenceSerDes.toJSON(
			cookiesConsentPreference);

		Assert.assertFalse(json.contains(regex));

		cookiesConsentPreference = CookiesConsentPreferenceSerDes.toDTO(json);

		Assert.assertEquals(regex, cookiesConsentPreference.getDomain());
		Assert.assertEquals(regex, cookiesConsentPreference.getName());
		Assert.assertEquals(regex, cookiesConsentPreference.getValue());
	}

	@Test
	public void testDeleteCookiesConsentPreference() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CookiesConsentPreference cookiesConsentPreference =
			testDeleteCookiesConsentPreference_addCookiesConsentPreference();

		assertHttpResponseStatusCode(
			204,
			cookiesConsentPreferenceResource.
				deleteCookiesConsentPreferenceHttpResponse());
	}

	protected CookiesConsentPreference
			testDeleteCookiesConsentPreference_addCookiesConsentPreference()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCookiesConsentPreference() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testDeleteCookiesConsentPreferenceByName() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CookiesConsentPreference cookiesConsentPreference =
			testDeleteCookiesConsentPreferenceByName_addCookiesConsentPreference();

		assertHttpResponseStatusCode(
			204,
			cookiesConsentPreferenceResource.
				deleteCookiesConsentPreferenceByNameHttpResponse(
					cookiesConsentPreference.getName()));

		assertHttpResponseStatusCode(
			404,
			cookiesConsentPreferenceResource.
				getCookiesConsentPreferenceByNameHttpResponse(
					cookiesConsentPreference.getName()));
		assertHttpResponseStatusCode(
			404,
			cookiesConsentPreferenceResource.
				getCookiesConsentPreferenceByNameHttpResponse(
					cookiesConsentPreference.getName()));
	}

	protected CookiesConsentPreference
			testDeleteCookiesConsentPreferenceByName_addCookiesConsentPreference()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCookiesConsentPreferenceByName()
		throws Exception {

		// No namespace

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CookiesConsentPreference cookiesConsentPreference1 =
			testGraphQLDeleteCookiesConsentPreferenceByName_addCookiesConsentPreference();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCookiesConsentPreferenceByName",
						new HashMap<String, Object>() {
							{
								put(
									"name",
									"\"" + cookiesConsentPreference1.getName() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteCookiesConsentPreferenceByName"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"cookiesConsentPreferenceByName",
					new HashMap<String, Object>() {
						{
							put(
								"name",
								"\"" + cookiesConsentPreference1.getName() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace cookies_v1_0

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CookiesConsentPreference cookiesConsentPreference2 =
			testGraphQLDeleteCookiesConsentPreferenceByName_addCookiesConsentPreference();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"cookies_v1_0",
						new GraphQLField(
							"deleteCookiesConsentPreferenceByName",
							new HashMap<String, Object>() {
								{
									put(
										"name",
										"\"" +
											cookiesConsentPreference2.
												getName() + "\"");
								}
							}))),
				"JSONObject/data", "JSONObject/cookies_v1_0",
				"Object/deleteCookiesConsentPreferenceByName"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"cookies_v1_0",
					new GraphQLField(
						"cookiesConsentPreferenceByName",
						new HashMap<String, Object>() {
							{
								put(
									"name",
									"\"" + cookiesConsentPreference2.getName() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected CookiesConsentPreference
			testGraphQLDeleteCookiesConsentPreferenceByName_addCookiesConsentPreference()
		throws Exception {

		return testGraphQLCookiesConsentPreference_addCookiesConsentPreference();
	}

	@Test
	public void testGetCookiesConsentPreferenceByName() throws Exception {
		CookiesConsentPreference postCookiesConsentPreference =
			testGetCookiesConsentPreferenceByName_addCookiesConsentPreference();

		CookiesConsentPreference getCookiesConsentPreference =
			cookiesConsentPreferenceResource.getCookiesConsentPreferenceByName(
				postCookiesConsentPreference.getName());

		assertEquals(postCookiesConsentPreference, getCookiesConsentPreference);
		assertValid(getCookiesConsentPreference);
	}

	protected CookiesConsentPreference
			testGetCookiesConsentPreferenceByName_addCookiesConsentPreference()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCookiesConsentPreferenceByName()
		throws Exception {

		CookiesConsentPreference cookiesConsentPreference =
			testGraphQLGetCookiesConsentPreferenceByName_addCookiesConsentPreference();

		// No namespace

		Assert.assertTrue(
			equals(
				cookiesConsentPreference,
				CookiesConsentPreferenceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cookiesConsentPreferenceByName",
								new HashMap<String, Object>() {
									{
										put(
											"name",
											"\"" +
												cookiesConsentPreference.
													getName() + "\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/cookiesConsentPreferenceByName"))));

		// Using the namespace cookies_v1_0

		Assert.assertTrue(
			equals(
				cookiesConsentPreference,
				CookiesConsentPreferenceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cookies_v1_0",
								new GraphQLField(
									"cookiesConsentPreferenceByName",
									new HashMap<String, Object>() {
										{
											put(
												"name",
												"\"" +
													cookiesConsentPreference.
														getName() + "\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/cookies_v1_0",
						"Object/cookiesConsentPreferenceByName"))));
	}

	@Test
	public void testGraphQLGetCookiesConsentPreferenceByNameNotFound()
		throws Exception {

		String irrelevantName = "\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cookiesConsentPreferenceByName",
						new HashMap<String, Object>() {
							{
								put("name", irrelevantName);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace cookies_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cookies_v1_0",
						new GraphQLField(
							"cookiesConsentPreferenceByName",
							new HashMap<String, Object>() {
								{
									put("name", irrelevantName);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected CookiesConsentPreference
			testGraphQLGetCookiesConsentPreferenceByName_addCookiesConsentPreference()
		throws Exception {

		return testGraphQLCookiesConsentPreference_addCookiesConsentPreference();
	}

	@Test
	public void testPutCookiesConsentPreference() throws Exception {
		CookiesConsentPreference postCookiesConsentPreference =
			testPutCookiesConsentPreference_addCookiesConsentPreference();

		CookiesConsentPreference randomCookiesConsentPreference =
			randomCookiesConsentPreference();

		CookiesConsentPreference putCookiesConsentPreference =
			cookiesConsentPreferenceResource.putCookiesConsentPreference(
				randomCookiesConsentPreference);

		assertEquals(
			randomCookiesConsentPreference, putCookiesConsentPreference);
		assertValid(putCookiesConsentPreference);

		CookiesConsentPreference getCookiesConsentPreference =
			testPutCookiesConsentPreference_getCookiesConsentPreference();

		assertEquals(
			randomCookiesConsentPreference, getCookiesConsentPreference);
		assertValid(getCookiesConsentPreference);
	}

	protected CookiesConsentPreference
		testPutCookiesConsentPreference_getCookiesConsentPreference() {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected CookiesConsentPreference
			testPutCookiesConsentPreference_addCookiesConsentPreference()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected CookiesConsentPreference
			testGraphQLCookiesConsentPreference_addCookiesConsentPreference()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		CookiesConsentPreference cookiesConsentPreference,
		List<CookiesConsentPreference> cookiesConsentPreferences) {

		boolean contains = false;

		for (CookiesConsentPreference item : cookiesConsentPreferences) {
			if (equals(cookiesConsentPreference, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			cookiesConsentPreferences + " does not contain " +
				cookiesConsentPreference,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		CookiesConsentPreference cookiesConsentPreference1,
		CookiesConsentPreference cookiesConsentPreference2) {

		Assert.assertTrue(
			cookiesConsentPreference1 + " does not equal " +
				cookiesConsentPreference2,
			equals(cookiesConsentPreference1, cookiesConsentPreference2));
	}

	protected void assertEquals(
		List<CookiesConsentPreference> cookiesConsentPreferences1,
		List<CookiesConsentPreference> cookiesConsentPreferences2) {

		Assert.assertEquals(
			cookiesConsentPreferences1.size(),
			cookiesConsentPreferences2.size());

		for (int i = 0; i < cookiesConsentPreferences1.size(); i++) {
			CookiesConsentPreference cookiesConsentPreference1 =
				cookiesConsentPreferences1.get(i);
			CookiesConsentPreference cookiesConsentPreference2 =
				cookiesConsentPreferences2.get(i);

			assertEquals(cookiesConsentPreference1, cookiesConsentPreference2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<CookiesConsentPreference> cookiesConsentPreferences1,
		List<CookiesConsentPreference> cookiesConsentPreferences2) {

		Assert.assertEquals(
			cookiesConsentPreferences1.size(),
			cookiesConsentPreferences2.size());

		for (CookiesConsentPreference cookiesConsentPreference1 :
				cookiesConsentPreferences1) {

			boolean contains = false;

			for (CookiesConsentPreference cookiesConsentPreference2 :
					cookiesConsentPreferences2) {

				if (equals(
						cookiesConsentPreference1, cookiesConsentPreference2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				cookiesConsentPreferences2 + " does not contain " +
					cookiesConsentPreference1,
				contains);
		}
	}

	protected void assertValid(
			CookiesConsentPreference cookiesConsentPreference)
		throws Exception {

		boolean valid = true;

		if (cookiesConsentPreference.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("domain", additionalAssertFieldName)) {
				if (cookiesConsentPreference.getDomain() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (cookiesConsentPreference.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (cookiesConsentPreference.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (cookiesConsentPreference.getUserId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("value", additionalAssertFieldName)) {
				if (cookiesConsentPreference.getValue() == null) {
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

	protected void assertValid(Page<CookiesConsentPreference> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<CookiesConsentPreference> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<CookiesConsentPreference>
			cookiesConsentPreferences = page.getItems();

		int size = cookiesConsentPreferences.size();

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
					com.liferay.cookies.rest.dto.v1_0.CookiesConsentPreference.
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
		CookiesConsentPreference cookiesConsentPreference1,
		CookiesConsentPreference cookiesConsentPreference2) {

		if (cookiesConsentPreference1 == cookiesConsentPreference2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("domain", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cookiesConsentPreference1.getDomain(),
						cookiesConsentPreference2.getDomain())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cookiesConsentPreference1.getExpirationDate(),
						cookiesConsentPreference2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cookiesConsentPreference1.getId(),
						cookiesConsentPreference2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cookiesConsentPreference1.getName(),
						cookiesConsentPreference2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cookiesConsentPreference1.getUserId(),
						cookiesConsentPreference2.getUserId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("value", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cookiesConsentPreference1.getValue(),
						cookiesConsentPreference2.getValue())) {

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

		if (!(_cookiesConsentPreferenceResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_cookiesConsentPreferenceResource;

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
		CookiesConsentPreference cookiesConsentPreference) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("domain")) {
			Object object = cookiesConsentPreference.getDomain();

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

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = cookiesConsentPreference.getExpirationDate();

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

				sb.append(
					_format.format(
						cookiesConsentPreference.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = cookiesConsentPreference.getName();

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

		if (entityFieldName.equals("value")) {
			Object object = cookiesConsentPreference.getValue();

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

	protected CookiesConsentPreference randomCookiesConsentPreference()
		throws Exception {

		return new CookiesConsentPreference() {
			{
				domain = StringUtil.toLowerCase(RandomTestUtil.randomString());
				expirationDate = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				userId = RandomTestUtil.randomLong();
				value = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected CookiesConsentPreference
			randomIrrelevantCookiesConsentPreference()
		throws Exception {

		CookiesConsentPreference randomIrrelevantCookiesConsentPreference =
			randomCookiesConsentPreference();

		return randomIrrelevantCookiesConsentPreference;
	}

	protected CookiesConsentPreference randomPatchCookiesConsentPreference()
		throws Exception {

		return randomCookiesConsentPreference();
	}

	protected CookiesConsentPreferenceResource cookiesConsentPreferenceResource;
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
			BaseCookiesConsentPreferenceResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.cookies.rest.resource.v1_0.CookiesConsentPreferenceResource
			_cookiesConsentPreferenceResource;

}
// LIFERAY-REST-BUILDER-HASH:-1877775746