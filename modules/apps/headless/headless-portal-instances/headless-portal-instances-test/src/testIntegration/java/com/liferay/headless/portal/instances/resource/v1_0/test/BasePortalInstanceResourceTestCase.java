/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.client.http.HttpInvoker;
import com.liferay.headless.portal.instances.client.pagination.Page;
import com.liferay.headless.portal.instances.client.resource.v1_0.PortalInstanceResource;
import com.liferay.headless.portal.instances.client.serdes.v1_0.PortalInstanceSerDes;
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
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public abstract class BasePortalInstanceResourceTestCase {

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

		_portalInstanceResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		portalInstanceResource = PortalInstanceResource.builder(
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

		PortalInstance portalInstance1 = randomPortalInstance();

		String json = objectMapper.writeValueAsString(portalInstance1);

		PortalInstance portalInstance2 = PortalInstanceSerDes.toDTO(json);

		Assert.assertTrue(equals(portalInstance1, portalInstance2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PortalInstance portalInstance = randomPortalInstance();

		String json1 = objectMapper.writeValueAsString(portalInstance);
		String json2 = PortalInstanceSerDes.toJSON(portalInstance);

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

		PortalInstance portalInstance = randomPortalInstance();

		portalInstance.setDomain(regex);
		portalInstance.setPortalInstanceId(regex);
		portalInstance.setSiteInitializerKey(regex);
		portalInstance.setVirtualHost(regex);

		String json = PortalInstanceSerDes.toJSON(portalInstance);

		Assert.assertFalse(json.contains(regex));

		portalInstance = PortalInstanceSerDes.toDTO(json);

		Assert.assertEquals(regex, portalInstance.getDomain());
		Assert.assertEquals(regex, portalInstance.getPortalInstanceId());
		Assert.assertEquals(regex, portalInstance.getSiteInitializerKey());
		Assert.assertEquals(regex, portalInstance.getVirtualHost());
	}

	@Test
	public void testDeletePortalInstance() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PortalInstance portalInstance =
			testDeletePortalInstance_addPortalInstance();

		assertHttpResponseStatusCode(
			204,
			portalInstanceResource.deletePortalInstanceHttpResponse(
				portalInstance.getPortalInstanceId()));

		assertHttpResponseStatusCode(
			404,
			portalInstanceResource.getPortalInstanceHttpResponse(
				portalInstance.getPortalInstanceId()));
		assertHttpResponseStatusCode(
			404, portalInstanceResource.getPortalInstanceHttpResponse("-"));
	}

	protected PortalInstance testDeletePortalInstance_addPortalInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetPortalInstance() throws Exception {
		PortalInstance postPortalInstance =
			testGetPortalInstance_addPortalInstance();

		PortalInstance getPortalInstance =
			portalInstanceResource.getPortalInstance(
				postPortalInstance.getPortalInstanceId());

		assertEquals(postPortalInstance, getPortalInstance);
		assertValid(getPortalInstance);
	}

	protected PortalInstance testGetPortalInstance_addPortalInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetPortalInstancesPage() throws Exception {
		Page<PortalInstance> page =
			portalInstanceResource.getPortalInstancesPage(null);

		long totalCount = page.getTotalCount();

		PortalInstance portalInstance1 =
			testGetPortalInstancesPage_addPortalInstance(
				randomPortalInstance());

		PortalInstance portalInstance2 =
			testGetPortalInstancesPage_addPortalInstance(
				randomPortalInstance());

		page = portalInstanceResource.getPortalInstancesPage(null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(portalInstance1, (List<PortalInstance>)page.getItems());
		assertContains(portalInstance2, (List<PortalInstance>)page.getItems());
		assertValid(page, testGetPortalInstancesPage_getExpectedActions());

		portalInstanceResource.deletePortalInstance(
			portalInstance1.getPortalInstanceId());

		portalInstanceResource.deletePortalInstance(
			portalInstance2.getPortalInstanceId());
	}

	protected Map<String, Map<String, String>>
			testGetPortalInstancesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected PortalInstance testGetPortalInstancesPage_addPortalInstance(
			PortalInstance portalInstance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchPortalInstance() throws Exception {
		PortalInstance postPortalInstance =
			testPatchPortalInstance_addPortalInstance();

		PortalInstance randomPatchPortalInstance = randomPatchPortalInstance();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PortalInstance patchPortalInstance =
			portalInstanceResource.patchPortalInstance(
				postPortalInstance.getPortalInstanceId(),
				randomPatchPortalInstance);

		PortalInstance expectedPatchPortalInstance = postPortalInstance.clone();

		BeanTestUtil.copyProperties(
			randomPatchPortalInstance, expectedPatchPortalInstance);

		PortalInstance getPortalInstance =
			portalInstanceResource.getPortalInstance(
				patchPortalInstance.getPortalInstanceId());

		assertEquals(expectedPatchPortalInstance, getPortalInstance);
		assertValid(getPortalInstance);
	}

	protected PortalInstance testPatchPortalInstance_addPortalInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPortalInstance() throws Exception {
		PortalInstance randomPortalInstance = randomPortalInstance();

		PortalInstance postPortalInstance =
			testPostPortalInstance_addPortalInstance(randomPortalInstance);

		assertEquals(randomPortalInstance, postPortalInstance);
		assertValid(postPortalInstance);
	}

	protected PortalInstance testPostPortalInstance_addPortalInstance(
			PortalInstance portalInstance)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutPortalInstanceActivate() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PortalInstance portalInstance =
			testPutPortalInstanceActivate_addPortalInstance();

		assertHttpResponseStatusCode(
			204,
			portalInstanceResource.putPortalInstanceActivateHttpResponse(
				portalInstance.getPortalInstanceId()));

		assertHttpResponseStatusCode(
			404,
			portalInstanceResource.putPortalInstanceActivateHttpResponse("-"));
	}

	protected PortalInstance testPutPortalInstanceActivate_addPortalInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutPortalInstanceDeactivate() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PortalInstance portalInstance =
			testPutPortalInstanceDeactivate_addPortalInstance();

		assertHttpResponseStatusCode(
			204,
			portalInstanceResource.putPortalInstanceDeactivateHttpResponse(
				portalInstance.getPortalInstanceId()));

		assertHttpResponseStatusCode(
			404,
			portalInstanceResource.putPortalInstanceDeactivateHttpResponse(
				"-"));
	}

	protected PortalInstance testPutPortalInstanceDeactivate_addPortalInstance()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PortalInstance portalInstance, List<PortalInstance> portalInstances) {

		boolean contains = false;

		for (PortalInstance item : portalInstances) {
			if (equals(portalInstance, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			portalInstances + " does not contain " + portalInstance, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PortalInstance portalInstance1, PortalInstance portalInstance2) {

		Assert.assertTrue(
			portalInstance1 + " does not equal " + portalInstance2,
			equals(portalInstance1, portalInstance2));
	}

	protected void assertEquals(
		List<PortalInstance> portalInstances1,
		List<PortalInstance> portalInstances2) {

		Assert.assertEquals(portalInstances1.size(), portalInstances2.size());

		for (int i = 0; i < portalInstances1.size(); i++) {
			PortalInstance portalInstance1 = portalInstances1.get(i);
			PortalInstance portalInstance2 = portalInstances2.get(i);

			assertEquals(portalInstance1, portalInstance2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PortalInstance> portalInstances1,
		List<PortalInstance> portalInstances2) {

		Assert.assertEquals(portalInstances1.size(), portalInstances2.size());

		for (PortalInstance portalInstance1 : portalInstances1) {
			boolean contains = false;

			for (PortalInstance portalInstance2 : portalInstances2) {
				if (equals(portalInstance1, portalInstance2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				portalInstances2 + " does not contain " + portalInstance1,
				contains);
		}
	}

	protected void assertValid(PortalInstance portalInstance) throws Exception {
		boolean valid = true;

		if (portalInstance.getPortalInstanceId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (portalInstance.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("admin", additionalAssertFieldName)) {
				if (portalInstance.getAdmin() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("companyId", additionalAssertFieldName)) {
				if (portalInstance.getCompanyId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("domain", additionalAssertFieldName)) {
				if (portalInstance.getDomain() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("portalInstanceId", additionalAssertFieldName)) {
				if (portalInstance.getPortalInstanceId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"siteInitializerKey", additionalAssertFieldName)) {

				if (portalInstance.getSiteInitializerKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("virtualHost", additionalAssertFieldName)) {
				if (portalInstance.getVirtualHost() == null) {
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

	protected void assertValid(Page<PortalInstance> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PortalInstance> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PortalInstance> portalInstances = page.getItems();

		int size = portalInstances.size();

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
					com.liferay.headless.portal.instances.dto.v1_0.
						PortalInstance.class)) {

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
		PortalInstance portalInstance1, PortalInstance portalInstance2) {

		if (portalInstance1 == portalInstance2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						portalInstance1.getActive(),
						portalInstance2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("admin", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						portalInstance1.getAdmin(),
						portalInstance2.getAdmin())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("companyId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						portalInstance1.getCompanyId(),
						portalInstance2.getCompanyId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("domain", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						portalInstance1.getDomain(),
						portalInstance2.getDomain())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("portalInstanceId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						portalInstance1.getPortalInstanceId(),
						portalInstance2.getPortalInstanceId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"siteInitializerKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						portalInstance1.getSiteInitializerKey(),
						portalInstance2.getSiteInitializerKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("virtualHost", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						portalInstance1.getVirtualHost(),
						portalInstance2.getVirtualHost())) {

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

		if (!(_portalInstanceResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_portalInstanceResource;

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
		PortalInstance portalInstance) {

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

		if (entityFieldName.equals("admin")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("companyId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("domain")) {
			Object object = portalInstance.getDomain();

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

		if (entityFieldName.equals("portalInstanceId")) {
			Object object = portalInstance.getPortalInstanceId();

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

		if (entityFieldName.equals("siteInitializerKey")) {
			Object object = portalInstance.getSiteInitializerKey();

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

		if (entityFieldName.equals("virtualHost")) {
			Object object = portalInstance.getVirtualHost();

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

	protected PortalInstance randomPortalInstance() throws Exception {
		return new PortalInstance() {
			{
				active = RandomTestUtil.randomBoolean();
				companyId = RandomTestUtil.randomLong();
				domain = StringUtil.toLowerCase(RandomTestUtil.randomString());
				portalInstanceId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteInitializerKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				virtualHost = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected PortalInstance randomIrrelevantPortalInstance() throws Exception {
		PortalInstance randomIrrelevantPortalInstance = randomPortalInstance();

		return randomIrrelevantPortalInstance;
	}

	protected PortalInstance randomPatchPortalInstance() throws Exception {
		return randomPortalInstance();
	}

	protected PortalInstanceResource portalInstanceResource;
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
		LogFactoryUtil.getLog(BasePortalInstanceResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.portal.instances.resource.v1_0.
			PortalInstanceResource _portalInstanceResource;

}