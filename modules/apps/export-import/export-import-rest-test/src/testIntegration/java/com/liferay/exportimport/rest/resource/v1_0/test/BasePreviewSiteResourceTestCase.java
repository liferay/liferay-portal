/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.exportimport.rest.client.dto.v1_0.PreviewSite;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
import com.liferay.exportimport.rest.client.http.HttpInvoker;
import com.liferay.exportimport.rest.client.pagination.Page;
import com.liferay.exportimport.rest.client.pagination.Pagination;
import com.liferay.exportimport.rest.client.resource.v1_0.PreviewSiteResource;
import com.liferay.exportimport.rest.client.serdes.v1_0.PreviewSiteSerDes;
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
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public abstract class BasePreviewSiteResourceTestCase {

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

		_previewSiteResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		previewSiteResource = PreviewSiteResource.builder(
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

		PreviewSite previewSite1 = randomPreviewSite();

		String json = objectMapper.writeValueAsString(previewSite1);

		PreviewSite previewSite2 = PreviewSiteSerDes.toDTO(json);

		Assert.assertTrue(equals(previewSite1, previewSite2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PreviewSite previewSite = randomPreviewSite();

		String json1 = objectMapper.writeValueAsString(previewSite);
		String json2 = PreviewSiteSerDes.toJSON(previewSite);

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

		PreviewSite previewSite = randomPreviewSite();

		previewSite.setDescriptiveName(regex);
		previewSite.setExternalReferenceCode(regex);
		previewSite.setPath(regex);

		String json = PreviewSiteSerDes.toJSON(previewSite);

		Assert.assertFalse(json.contains(regex));

		previewSite = PreviewSiteSerDes.toDTO(json);

		Assert.assertEquals(regex, previewSite.getDescriptiveName());
		Assert.assertEquals(regex, previewSite.getExternalReferenceCode());
		Assert.assertEquals(regex, previewSite.getPath());
	}

	@Test
	public void testGetExportPreviewPreviewSitesPage() throws Exception {
		Page<PreviewSite> page =
			previewSiteResource.getExportPreviewPreviewSitesPage(
				null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		PreviewSite previewSite1 =
			testGetExportPreviewPreviewSitesPage_addPreviewSite(
				randomPreviewSite());

		PreviewSite previewSite2 =
			testGetExportPreviewPreviewSitesPage_addPreviewSite(
				randomPreviewSite());

		page = previewSiteResource.getExportPreviewPreviewSitesPage(
			null, Pagination.of(1, (int)totalCount + 2), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(previewSite1, (List<PreviewSite>)page.getItems());
		assertContains(previewSite2, (List<PreviewSite>)page.getItems());
		assertValid(
			page, testGetExportPreviewPreviewSitesPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetExportPreviewPreviewSitesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetExportPreviewPreviewSitesPageWithPagination()
		throws Exception {

		Page<PreviewSite> previewSitesPage =
			previewSiteResource.getExportPreviewPreviewSitesPage(
				null, null, null);

		int totalCount = GetterUtil.getInteger(
			previewSitesPage.getTotalCount());

		PreviewSite previewSite1 =
			testGetExportPreviewPreviewSitesPage_addPreviewSite(
				randomPreviewSite());

		PreviewSite previewSite2 =
			testGetExportPreviewPreviewSitesPage_addPreviewSite(
				randomPreviewSite());

		PreviewSite previewSite3 =
			testGetExportPreviewPreviewSitesPage_addPreviewSite(
				randomPreviewSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PreviewSite> page1 =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(previewSite1, (List<PreviewSite>)page1.getItems());

			Page<PreviewSite> page2 =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(previewSite2, (List<PreviewSite>)page2.getItems());

			Page<PreviewSite> page3 =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(previewSite3, (List<PreviewSite>)page3.getItems());
		}
		else {
			Page<PreviewSite> page1 =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null, Pagination.of(1, totalCount + 2), null);

			List<PreviewSite> previewSites1 =
				(List<PreviewSite>)page1.getItems();

			Assert.assertEquals(
				previewSites1.toString(), totalCount + 2, previewSites1.size());

			Page<PreviewSite> page2 =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PreviewSite> previewSites2 =
				(List<PreviewSite>)page2.getItems();

			Assert.assertEquals(
				previewSites2.toString(), 1, previewSites2.size());

			Page<PreviewSite> page3 =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(previewSite1, (List<PreviewSite>)page3.getItems());
			assertContains(previewSite2, (List<PreviewSite>)page3.getItems());
			assertContains(previewSite3, (List<PreviewSite>)page3.getItems());
		}
	}

	@Test
	public void testGetExportPreviewPreviewSitesPageWithSortDateTime()
		throws Exception {

		testGetExportPreviewPreviewSitesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, previewSite1, previewSite2) -> {
				BeanTestUtil.setProperty(
					previewSite1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetExportPreviewPreviewSitesPageWithSortDouble()
		throws Exception {

		testGetExportPreviewPreviewSitesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, previewSite1, previewSite2) -> {
				BeanTestUtil.setProperty(
					previewSite1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					previewSite2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetExportPreviewPreviewSitesPageWithSortInteger()
		throws Exception {

		testGetExportPreviewPreviewSitesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, previewSite1, previewSite2) -> {
				BeanTestUtil.setProperty(
					previewSite1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					previewSite2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetExportPreviewPreviewSitesPageWithSortString()
		throws Exception {

		testGetExportPreviewPreviewSitesPageWithSort(
			EntityField.Type.STRING,
			(entityField, previewSite1, previewSite2) -> {
				Class<?> clazz = previewSite1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						previewSite1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						previewSite2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						previewSite1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						previewSite2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						previewSite1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						previewSite2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetExportPreviewPreviewSitesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, PreviewSite, PreviewSite, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		PreviewSite previewSite1 = randomPreviewSite();
		PreviewSite previewSite2 = randomPreviewSite();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, previewSite1, previewSite2);
		}

		previewSite1 = testGetExportPreviewPreviewSitesPage_addPreviewSite(
			previewSite1);

		previewSite2 = testGetExportPreviewPreviewSitesPage_addPreviewSite(
			previewSite2);

		Page<PreviewSite> page =
			previewSiteResource.getExportPreviewPreviewSitesPage(
				null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PreviewSite> ascPage =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(previewSite1, (List<PreviewSite>)ascPage.getItems());
			assertContains(previewSite2, (List<PreviewSite>)ascPage.getItems());

			Page<PreviewSite> descPage =
				previewSiteResource.getExportPreviewPreviewSitesPage(
					null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				previewSite2, (List<PreviewSite>)descPage.getItems());
			assertContains(
				previewSite1, (List<PreviewSite>)descPage.getItems());
		}
	}

	protected PreviewSite testGetExportPreviewPreviewSitesPage_addPreviewSite(
			PreviewSite previewSite)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		PreviewSite previewSite, List<PreviewSite> previewSites) {

		boolean contains = false;

		for (PreviewSite item : previewSites) {
			if (equals(previewSite, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			previewSites + " does not contain " + previewSite, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PreviewSite previewSite1, PreviewSite previewSite2) {

		Assert.assertTrue(
			previewSite1 + " does not equal " + previewSite2,
			equals(previewSite1, previewSite2));
	}

	protected void assertEquals(
		List<PreviewSite> previewSites1, List<PreviewSite> previewSites2) {

		Assert.assertEquals(previewSites1.size(), previewSites2.size());

		for (int i = 0; i < previewSites1.size(); i++) {
			PreviewSite previewSite1 = previewSites1.get(i);
			PreviewSite previewSite2 = previewSites2.get(i);

			assertEquals(previewSite1, previewSite2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PreviewSite> previewSites1, List<PreviewSite> previewSites2) {

		Assert.assertEquals(previewSites1.size(), previewSites2.size());

		for (PreviewSite previewSite1 : previewSites1) {
			boolean contains = false;

			for (PreviewSite previewSite2 : previewSites2) {
				if (equals(previewSite1, previewSite2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				previewSites2 + " does not contain " + previewSite1, contains);
		}
	}

	protected void assertValid(PreviewSite previewSite) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("childSitesCount", additionalAssertFieldName)) {
				if (previewSite.getChildSitesCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("descriptiveName", additionalAssertFieldName)) {
				if (previewSite.getDescriptiveName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("existsInInstance", additionalAssertFieldName)) {
				if (previewSite.getExistsInInstance() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (previewSite.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("path", additionalAssertFieldName)) {
				if (previewSite.getPath() == null) {
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

	protected void assertValid(Page<PreviewSite> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PreviewSite> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PreviewSite> previewSites = page.getItems();

		int size = previewSites.size();

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
					com.liferay.exportimport.rest.dto.v1_0.PreviewSite.class)) {

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
		PreviewSite previewSite1, PreviewSite previewSite2) {

		if (previewSite1 == previewSite2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("childSitesCount", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						previewSite1.getChildSitesCount(),
						previewSite2.getChildSitesCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("descriptiveName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						previewSite1.getDescriptiveName(),
						previewSite2.getDescriptiveName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("existsInInstance", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						previewSite1.getExistsInInstance(),
						previewSite2.getExistsInInstance())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						previewSite1.getExternalReferenceCode(),
						previewSite2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("path", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						previewSite1.getPath(), previewSite2.getPath())) {

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

		if (!(_previewSiteResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_previewSiteResource;

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
		EntityField entityField, String operator, PreviewSite previewSite) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("childSitesCount")) {
			sb.append(String.valueOf(previewSite.getChildSitesCount()));

			return sb.toString();
		}

		if (entityFieldName.equals("descriptiveName")) {
			Object object = previewSite.getDescriptiveName();

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

		if (entityFieldName.equals("existsInInstance")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = previewSite.getExternalReferenceCode();

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

		if (entityFieldName.equals("path")) {
			Object object = previewSite.getPath();

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

	protected PreviewSite randomPreviewSite() throws Exception {
		return new PreviewSite() {
			{
				childSitesCount = RandomTestUtil.randomInt();
				descriptiveName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				existsInInstance = RandomTestUtil.randomBoolean();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				path = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected PreviewSite randomIrrelevantPreviewSite() throws Exception {
		PreviewSite randomIrrelevantPreviewSite = randomPreviewSite();

		return randomIrrelevantPreviewSite;
	}

	protected PreviewSite randomPatchPreviewSite() throws Exception {
		return randomPreviewSite();
	}

	protected PreviewSiteResource previewSiteResource;
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
		LogFactoryUtil.getLog(BasePreviewSiteResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.exportimport.rest.resource.v1_0.PreviewSiteResource
		_previewSiteResource;

}
// LIFERAY-REST-BUILDER-HASH:432997891