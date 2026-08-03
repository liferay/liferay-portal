/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.configuration.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.configuration.client.dto.v1_0.InstanceConfiguration;
import com.liferay.headless.admin.configuration.client.http.HttpInvoker;
import com.liferay.headless.admin.configuration.client.pagination.Page;
import com.liferay.headless.admin.configuration.client.pagination.Pagination;
import com.liferay.headless.admin.configuration.client.resource.v1_0.InstanceConfigurationResource;
import com.liferay.headless.admin.configuration.client.serdes.v1_0.InstanceConfigurationSerDes;
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
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public abstract class BaseInstanceConfigurationResourceTestCase {

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

		_instanceConfigurationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		instanceConfigurationResource = InstanceConfigurationResource.builder(
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

		InstanceConfiguration instanceConfiguration1 =
			randomInstanceConfiguration();

		String json = objectMapper.writeValueAsString(instanceConfiguration1);

		InstanceConfiguration instanceConfiguration2 =
			InstanceConfigurationSerDes.toDTO(json);

		Assert.assertTrue(
			equals(instanceConfiguration1, instanceConfiguration2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		InstanceConfiguration instanceConfiguration =
			randomInstanceConfiguration();

		String json1 = objectMapper.writeValueAsString(instanceConfiguration);
		String json2 = InstanceConfigurationSerDes.toJSON(
			instanceConfiguration);

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

		InstanceConfiguration instanceConfiguration =
			randomInstanceConfiguration();

		instanceConfiguration.setExternalReferenceCode(regex);

		String json = InstanceConfigurationSerDes.toJSON(instanceConfiguration);

		Assert.assertFalse(json.contains(regex));

		instanceConfiguration = InstanceConfigurationSerDes.toDTO(json);

		Assert.assertEquals(
			regex, instanceConfiguration.getExternalReferenceCode());
	}

	@Test
	public void testGetInstanceConfiguration() throws Exception {
		InstanceConfiguration postInstanceConfiguration =
			testGetInstanceConfiguration_addInstanceConfiguration();

		InstanceConfiguration getInstanceConfiguration =
			instanceConfigurationResource.getInstanceConfiguration(
				postInstanceConfiguration.getExternalReferenceCode());

		assertEquals(postInstanceConfiguration, getInstanceConfiguration);
		assertValid(getInstanceConfiguration);
	}

	protected InstanceConfiguration
			testGetInstanceConfiguration_addInstanceConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetInstanceConfigurationsPage() throws Exception {
		Page<InstanceConfiguration> page =
			instanceConfigurationResource.getInstanceConfigurationsPage(
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		InstanceConfiguration instanceConfiguration1 =
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				randomInstanceConfiguration());

		InstanceConfiguration instanceConfiguration2 =
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				randomInstanceConfiguration());

		page = instanceConfigurationResource.getInstanceConfigurationsPage(
			Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			instanceConfiguration1,
			(List<InstanceConfiguration>)page.getItems());
		assertContains(
			instanceConfiguration2,
			(List<InstanceConfiguration>)page.getItems());
		assertValid(
			page, testGetInstanceConfigurationsPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetInstanceConfigurationsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetInstanceConfigurationsPageWithPagination()
		throws Exception {

		Page<InstanceConfiguration> instanceConfigurationsPage =
			instanceConfigurationResource.getInstanceConfigurationsPage(null);

		int totalCount = GetterUtil.getInteger(
			instanceConfigurationsPage.getTotalCount());

		InstanceConfiguration instanceConfiguration1 =
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				randomInstanceConfiguration());

		InstanceConfiguration instanceConfiguration2 =
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				randomInstanceConfiguration());

		InstanceConfiguration instanceConfiguration3 =
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				randomInstanceConfiguration());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<InstanceConfiguration> page1 =
				instanceConfigurationResource.getInstanceConfigurationsPage(
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				instanceConfiguration1,
				(List<InstanceConfiguration>)page1.getItems());

			Page<InstanceConfiguration> page2 =
				instanceConfigurationResource.getInstanceConfigurationsPage(
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				instanceConfiguration2,
				(List<InstanceConfiguration>)page2.getItems());

			Page<InstanceConfiguration> page3 =
				instanceConfigurationResource.getInstanceConfigurationsPage(
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				instanceConfiguration3,
				(List<InstanceConfiguration>)page3.getItems());
		}
		else {
			Page<InstanceConfiguration> page1 =
				instanceConfigurationResource.getInstanceConfigurationsPage(
					Pagination.of(1, totalCount + 2));

			List<InstanceConfiguration> instanceConfigurations1 =
				(List<InstanceConfiguration>)page1.getItems();

			Assert.assertEquals(
				instanceConfigurations1.toString(), totalCount + 2,
				instanceConfigurations1.size());

			Page<InstanceConfiguration> page2 =
				instanceConfigurationResource.getInstanceConfigurationsPage(
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<InstanceConfiguration> instanceConfigurations2 =
				(List<InstanceConfiguration>)page2.getItems();

			Assert.assertEquals(
				instanceConfigurations2.toString(), 1,
				instanceConfigurations2.size());

			Page<InstanceConfiguration> page3 =
				instanceConfigurationResource.getInstanceConfigurationsPage(
					Pagination.of(1, (int)totalCount + 3));

			assertContains(
				instanceConfiguration1,
				(List<InstanceConfiguration>)page3.getItems());
			assertContains(
				instanceConfiguration2,
				(List<InstanceConfiguration>)page3.getItems());
			assertContains(
				instanceConfiguration3,
				(List<InstanceConfiguration>)page3.getItems());
		}
	}

	protected InstanceConfiguration
			testGetInstanceConfigurationsPage_addInstanceConfiguration(
				InstanceConfiguration instanceConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostInstanceConfiguration() throws Exception {
		InstanceConfiguration randomInstanceConfiguration =
			randomInstanceConfiguration();

		InstanceConfiguration postInstanceConfiguration =
			testPostInstanceConfiguration_addInstanceConfiguration(
				randomInstanceConfiguration);

		assertEquals(randomInstanceConfiguration, postInstanceConfiguration);
		assertValid(postInstanceConfiguration);
	}

	protected InstanceConfiguration
			testPostInstanceConfiguration_addInstanceConfiguration(
				InstanceConfiguration instanceConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutInstanceConfiguration() throws Exception {
		InstanceConfiguration postInstanceConfiguration =
			testPutInstanceConfiguration_addInstanceConfiguration();

		InstanceConfiguration randomInstanceConfiguration =
			randomInstanceConfiguration();

		InstanceConfiguration putInstanceConfiguration =
			instanceConfigurationResource.putInstanceConfiguration(
				postInstanceConfiguration.getExternalReferenceCode(),
				randomInstanceConfiguration);

		assertEquals(randomInstanceConfiguration, putInstanceConfiguration);
		assertValid(putInstanceConfiguration);

		InstanceConfiguration getInstanceConfiguration =
			instanceConfigurationResource.getInstanceConfiguration(
				putInstanceConfiguration.getExternalReferenceCode());

		assertEquals(randomInstanceConfiguration, getInstanceConfiguration);
		assertValid(getInstanceConfiguration);
	}

	protected InstanceConfiguration
			testPutInstanceConfiguration_addInstanceConfiguration()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		InstanceConfiguration instanceConfiguration,
		List<InstanceConfiguration> instanceConfigurations) {

		boolean contains = false;

		for (InstanceConfiguration item : instanceConfigurations) {
			if (equals(instanceConfiguration, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			instanceConfigurations + " does not contain " +
				instanceConfiguration,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		InstanceConfiguration instanceConfiguration1,
		InstanceConfiguration instanceConfiguration2) {

		Assert.assertTrue(
			instanceConfiguration1 + " does not equal " +
				instanceConfiguration2,
			equals(instanceConfiguration1, instanceConfiguration2));
	}

	protected void assertEquals(
		List<InstanceConfiguration> instanceConfigurations1,
		List<InstanceConfiguration> instanceConfigurations2) {

		Assert.assertEquals(
			instanceConfigurations1.size(), instanceConfigurations2.size());

		for (int i = 0; i < instanceConfigurations1.size(); i++) {
			InstanceConfiguration instanceConfiguration1 =
				instanceConfigurations1.get(i);
			InstanceConfiguration instanceConfiguration2 =
				instanceConfigurations2.get(i);

			assertEquals(instanceConfiguration1, instanceConfiguration2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<InstanceConfiguration> instanceConfigurations1,
		List<InstanceConfiguration> instanceConfigurations2) {

		Assert.assertEquals(
			instanceConfigurations1.size(), instanceConfigurations2.size());

		for (InstanceConfiguration instanceConfiguration1 :
				instanceConfigurations1) {

			boolean contains = false;

			for (InstanceConfiguration instanceConfiguration2 :
					instanceConfigurations2) {

				if (equals(instanceConfiguration1, instanceConfiguration2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				instanceConfigurations2 + " does not contain " +
					instanceConfiguration1,
				contains);
		}
	}

	protected void assertValid(InstanceConfiguration instanceConfiguration)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (instanceConfiguration.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("properties", additionalAssertFieldName)) {
				if (instanceConfiguration.getProperties() == null) {
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

	protected void assertValid(Page<InstanceConfiguration> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<InstanceConfiguration> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<InstanceConfiguration> instanceConfigurations =
			page.getItems();

		int size = instanceConfigurations.size();

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

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.configuration.dto.v1_0.
						InstanceConfiguration.class)) {

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
		InstanceConfiguration instanceConfiguration1,
		InstanceConfiguration instanceConfiguration2) {

		if (instanceConfiguration1 == instanceConfiguration2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						instanceConfiguration1.getExternalReferenceCode(),
						instanceConfiguration2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("properties", additionalAssertFieldName)) {
				if (!equals(
						(Map)instanceConfiguration1.getProperties(),
						(Map)instanceConfiguration2.getProperties())) {

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

		if (!(_instanceConfigurationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_instanceConfigurationResource;

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
		InstanceConfiguration instanceConfiguration) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = instanceConfiguration.getExternalReferenceCode();

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

		if (entityFieldName.equals("properties")) {
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

	protected InstanceConfiguration randomInstanceConfiguration()
		throws Exception {

		return new InstanceConfiguration() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected InstanceConfiguration randomIrrelevantInstanceConfiguration()
		throws Exception {

		InstanceConfiguration randomIrrelevantInstanceConfiguration =
			randomInstanceConfiguration();

		return randomIrrelevantInstanceConfiguration;
	}

	protected InstanceConfiguration randomPatchInstanceConfiguration()
		throws Exception {

		return randomInstanceConfiguration();
	}

	protected InstanceConfigurationResource instanceConfigurationResource;
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
		LogFactoryUtil.getLog(BaseInstanceConfigurationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.configuration.resource.v1_0.
		InstanceConfigurationResource _instanceConfigurationResource;

}
// LIFERAY-REST-BUILDER-HASH:796340588