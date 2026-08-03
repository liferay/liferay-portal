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

import com.liferay.headless.admin.configuration.client.dto.v1_0.SiteConfiguration;
import com.liferay.headless.admin.configuration.client.http.HttpInvoker;
import com.liferay.headless.admin.configuration.client.pagination.Page;
import com.liferay.headless.admin.configuration.client.pagination.Pagination;
import com.liferay.headless.admin.configuration.client.resource.v1_0.SiteConfigurationResource;
import com.liferay.headless.admin.configuration.client.serdes.v1_0.SiteConfigurationSerDes;
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
 * @author Thiago Buarque
 * @generated
 */
@Generated("")
public abstract class BaseSiteConfigurationResourceTestCase {

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

		_siteConfigurationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		siteConfigurationResource = SiteConfigurationResource.builder(
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

		SiteConfiguration siteConfiguration1 = randomSiteConfiguration();

		String json = objectMapper.writeValueAsString(siteConfiguration1);

		SiteConfiguration siteConfiguration2 = SiteConfigurationSerDes.toDTO(
			json);

		Assert.assertTrue(equals(siteConfiguration1, siteConfiguration2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SiteConfiguration siteConfiguration = randomSiteConfiguration();

		String json1 = objectMapper.writeValueAsString(siteConfiguration);
		String json2 = SiteConfigurationSerDes.toJSON(siteConfiguration);

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

		SiteConfiguration siteConfiguration = randomSiteConfiguration();

		siteConfiguration.setExternalReferenceCode(regex);

		String json = SiteConfigurationSerDes.toJSON(siteConfiguration);

		Assert.assertFalse(json.contains(regex));

		siteConfiguration = SiteConfigurationSerDes.toDTO(json);

		Assert.assertEquals(
			regex, siteConfiguration.getExternalReferenceCode());
	}

	@Test
	public void testGetSiteSiteConfiguration() throws Exception {
		SiteConfiguration postSiteConfiguration =
			testGetSiteSiteConfiguration_addSiteConfiguration();

		SiteConfiguration getSiteConfiguration =
			siteConfigurationResource.getSiteSiteConfiguration(
				testGetSiteSiteConfiguration_getSiteExternalReferenceCode(),
				postSiteConfiguration.getExternalReferenceCode());

		assertEquals(postSiteConfiguration, getSiteConfiguration);
		assertValid(getSiteConfiguration);
	}

	protected SiteConfiguration
			testGetSiteSiteConfiguration_addSiteConfiguration()
		throws Exception {

		return siteConfigurationResource.postSiteSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomSiteConfiguration());
	}

	protected String testGetSiteSiteConfiguration_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetSiteSiteConfigurationsPage() throws Exception {
		String siteExternalReferenceCode =
			testGetSiteSiteConfigurationsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSiteConfigurationsPage_getIrrelevantSiteExternalReferenceCode();

		Page<SiteConfiguration> page =
			siteConfigurationResource.getSiteSiteConfigurationsPage(
				siteExternalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteExternalReferenceCode != null) {
			SiteConfiguration irrelevantSiteConfiguration =
				testGetSiteSiteConfigurationsPage_addSiteConfiguration(
					irrelevantSiteExternalReferenceCode,
					randomIrrelevantSiteConfiguration());

			page = siteConfigurationResource.getSiteSiteConfigurationsPage(
				irrelevantSiteExternalReferenceCode,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantSiteConfiguration,
				(List<SiteConfiguration>)page.getItems());
			assertValid(
				page,
				testGetSiteSiteConfigurationsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode));
		}

		SiteConfiguration siteConfiguration1 =
			testGetSiteSiteConfigurationsPage_addSiteConfiguration(
				siteExternalReferenceCode, randomSiteConfiguration());

		SiteConfiguration siteConfiguration2 =
			testGetSiteSiteConfigurationsPage_addSiteConfiguration(
				siteExternalReferenceCode, randomSiteConfiguration());

		page = siteConfigurationResource.getSiteSiteConfigurationsPage(
			siteExternalReferenceCode, Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			siteConfiguration1, (List<SiteConfiguration>)page.getItems());
		assertContains(
			siteConfiguration2, (List<SiteConfiguration>)page.getItems());
		assertValid(
			page,
			testGetSiteSiteConfigurationsPage_getExpectedActions(
				siteExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSiteConfigurationsPage_getExpectedActions(
				String siteExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			("http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/headless-admin-configuration/v1.0/sites/{siteExternalReferenceCode}/site-configurations/batch").
					replace(
						"{siteExternalReferenceCode}",
						String.valueOf(siteExternalReferenceCode)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteSiteConfigurationsPageWithPagination()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSiteConfigurationsPage_getSiteExternalReferenceCode();

		Page<SiteConfiguration> siteConfigurationsPage =
			siteConfigurationResource.getSiteSiteConfigurationsPage(
				siteExternalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			siteConfigurationsPage.getTotalCount());

		SiteConfiguration siteConfiguration1 =
			testGetSiteSiteConfigurationsPage_addSiteConfiguration(
				siteExternalReferenceCode, randomSiteConfiguration());

		SiteConfiguration siteConfiguration2 =
			testGetSiteSiteConfigurationsPage_addSiteConfiguration(
				siteExternalReferenceCode, randomSiteConfiguration());

		SiteConfiguration siteConfiguration3 =
			testGetSiteSiteConfigurationsPage_addSiteConfiguration(
				siteExternalReferenceCode, randomSiteConfiguration());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SiteConfiguration> page1 =
				siteConfigurationResource.getSiteSiteConfigurationsPage(
					siteExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				siteConfiguration1, (List<SiteConfiguration>)page1.getItems());

			Page<SiteConfiguration> page2 =
				siteConfigurationResource.getSiteSiteConfigurationsPage(
					siteExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				siteConfiguration2, (List<SiteConfiguration>)page2.getItems());

			Page<SiteConfiguration> page3 =
				siteConfigurationResource.getSiteSiteConfigurationsPage(
					siteExternalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				siteConfiguration3, (List<SiteConfiguration>)page3.getItems());
		}
		else {
			Page<SiteConfiguration> page1 =
				siteConfigurationResource.getSiteSiteConfigurationsPage(
					siteExternalReferenceCode,
					Pagination.of(1, totalCount + 2));

			List<SiteConfiguration> siteConfigurations1 =
				(List<SiteConfiguration>)page1.getItems();

			Assert.assertEquals(
				siteConfigurations1.toString(), totalCount + 2,
				siteConfigurations1.size());

			Page<SiteConfiguration> page2 =
				siteConfigurationResource.getSiteSiteConfigurationsPage(
					siteExternalReferenceCode,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SiteConfiguration> siteConfigurations2 =
				(List<SiteConfiguration>)page2.getItems();

			Assert.assertEquals(
				siteConfigurations2.toString(), 1, siteConfigurations2.size());

			Page<SiteConfiguration> page3 =
				siteConfigurationResource.getSiteSiteConfigurationsPage(
					siteExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(
				siteConfiguration1, (List<SiteConfiguration>)page3.getItems());
			assertContains(
				siteConfiguration2, (List<SiteConfiguration>)page3.getItems());
			assertContains(
				siteConfiguration3, (List<SiteConfiguration>)page3.getItems());
		}
	}

	protected SiteConfiguration
			testGetSiteSiteConfigurationsPage_addSiteConfiguration(
				String siteExternalReferenceCode,
				SiteConfiguration siteConfiguration)
		throws Exception {

		return siteConfigurationResource.postSiteSiteConfiguration(
			siteExternalReferenceCode, siteConfiguration);
	}

	protected String
			testGetSiteSiteConfigurationsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSiteConfigurationsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	@Test
	public void testPostSiteSiteConfiguration() throws Exception {
		SiteConfiguration randomSiteConfiguration = randomSiteConfiguration();

		SiteConfiguration postSiteConfiguration =
			testPostSiteSiteConfiguration_addSiteConfiguration(
				randomSiteConfiguration);

		assertEquals(randomSiteConfiguration, postSiteConfiguration);
		assertValid(postSiteConfiguration);
	}

	protected SiteConfiguration
			testPostSiteSiteConfiguration_addSiteConfiguration(
				SiteConfiguration siteConfiguration)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteConfiguration() throws Exception {
		SiteConfiguration postSiteConfiguration =
			testPutSiteSiteConfiguration_addSiteConfiguration();

		SiteConfiguration randomSiteConfiguration = randomSiteConfiguration();

		SiteConfiguration putSiteConfiguration =
			siteConfigurationResource.putSiteSiteConfiguration(
				testPutSiteSiteConfiguration_getSiteExternalReferenceCode(),
				postSiteConfiguration.getExternalReferenceCode(),
				randomSiteConfiguration);

		assertEquals(randomSiteConfiguration, putSiteConfiguration);
		assertValid(putSiteConfiguration);

		SiteConfiguration getSiteConfiguration =
			siteConfigurationResource.getSiteSiteConfiguration(
				testPutSiteSiteConfiguration_getSiteExternalReferenceCode(),
				putSiteConfiguration.getExternalReferenceCode());

		assertEquals(randomSiteConfiguration, getSiteConfiguration);
		assertValid(getSiteConfiguration);
	}

	protected SiteConfiguration
			testPutSiteSiteConfiguration_addSiteConfiguration()
		throws Exception {

		return siteConfigurationResource.postSiteSiteConfiguration(
			testGroup.getExternalReferenceCode(), randomSiteConfiguration());
	}

	protected String testPutSiteSiteConfiguration_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		SiteConfiguration siteConfiguration,
		List<SiteConfiguration> siteConfigurations) {

		boolean contains = false;

		for (SiteConfiguration item : siteConfigurations) {
			if (equals(siteConfiguration, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			siteConfigurations + " does not contain " + siteConfiguration,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SiteConfiguration siteConfiguration1,
		SiteConfiguration siteConfiguration2) {

		Assert.assertTrue(
			siteConfiguration1 + " does not equal " + siteConfiguration2,
			equals(siteConfiguration1, siteConfiguration2));
	}

	protected void assertEquals(
		List<SiteConfiguration> siteConfigurations1,
		List<SiteConfiguration> siteConfigurations2) {

		Assert.assertEquals(
			siteConfigurations1.size(), siteConfigurations2.size());

		for (int i = 0; i < siteConfigurations1.size(); i++) {
			SiteConfiguration siteConfiguration1 = siteConfigurations1.get(i);
			SiteConfiguration siteConfiguration2 = siteConfigurations2.get(i);

			assertEquals(siteConfiguration1, siteConfiguration2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SiteConfiguration> siteConfigurations1,
		List<SiteConfiguration> siteConfigurations2) {

		Assert.assertEquals(
			siteConfigurations1.size(), siteConfigurations2.size());

		for (SiteConfiguration siteConfiguration1 : siteConfigurations1) {
			boolean contains = false;

			for (SiteConfiguration siteConfiguration2 : siteConfigurations2) {
				if (equals(siteConfiguration1, siteConfiguration2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				siteConfigurations2 + " does not contain " + siteConfiguration1,
				contains);
		}
	}

	protected void assertValid(SiteConfiguration siteConfiguration)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (siteConfiguration.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("properties", additionalAssertFieldName)) {
				if (siteConfiguration.getProperties() == null) {
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

	protected void assertValid(Page<SiteConfiguration> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SiteConfiguration> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SiteConfiguration> siteConfigurations =
			page.getItems();

		int size = siteConfigurations.size();

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
						SiteConfiguration.class)) {

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
		SiteConfiguration siteConfiguration1,
		SiteConfiguration siteConfiguration2) {

		if (siteConfiguration1 == siteConfiguration2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						siteConfiguration1.getExternalReferenceCode(),
						siteConfiguration2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("properties", additionalAssertFieldName)) {
				if (!equals(
						(Map)siteConfiguration1.getProperties(),
						(Map)siteConfiguration2.getProperties())) {

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

		if (!(_siteConfigurationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_siteConfigurationResource;

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
		SiteConfiguration siteConfiguration) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = siteConfiguration.getExternalReferenceCode();

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

	protected SiteConfiguration randomSiteConfiguration() throws Exception {
		return new SiteConfiguration() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected SiteConfiguration randomIrrelevantSiteConfiguration()
		throws Exception {

		SiteConfiguration randomIrrelevantSiteConfiguration =
			randomSiteConfiguration();

		return randomIrrelevantSiteConfiguration;
	}

	protected SiteConfiguration randomPatchSiteConfiguration()
		throws Exception {

		return randomSiteConfiguration();
	}

	protected SiteConfigurationResource siteConfigurationResource;
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
		LogFactoryUtil.getLog(BaseSiteConfigurationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.configuration.resource.v1_0.
		SiteConfigurationResource _siteConfigurationResource;

}
// LIFERAY-REST-BUILDER-HASH:-1358724