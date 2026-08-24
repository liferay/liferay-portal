/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecificationVersion;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.resource.v1_0.PageSpecificationVersionResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.PageSpecificationVersionSerDes;
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
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class BasePageSpecificationVersionResourceTestCase {

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

		_pageSpecificationVersionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		pageSpecificationVersionResource =
			PageSpecificationVersionResource.builder(
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

		PageSpecificationVersion pageSpecificationVersion1 =
			randomPageSpecificationVersion();

		String json = objectMapper.writeValueAsString(
			pageSpecificationVersion1);

		PageSpecificationVersion pageSpecificationVersion2 =
			PageSpecificationVersionSerDes.toDTO(json);

		Assert.assertTrue(
			equals(pageSpecificationVersion1, pageSpecificationVersion2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PageSpecificationVersion pageSpecificationVersion =
			randomPageSpecificationVersion();

		String json1 = objectMapper.writeValueAsString(
			pageSpecificationVersion);
		String json2 = PageSpecificationVersionSerDes.toJSON(
			pageSpecificationVersion);

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

		PageSpecificationVersion pageSpecificationVersion =
			randomPageSpecificationVersion();

		pageSpecificationVersion.setExternalReferenceCode(regex);
		pageSpecificationVersion.setName(regex);

		String json = PageSpecificationVersionSerDes.toJSON(
			pageSpecificationVersion);

		Assert.assertFalse(json.contains(regex));

		pageSpecificationVersion = PageSpecificationVersionSerDes.toDTO(json);

		Assert.assertEquals(
			regex, pageSpecificationVersion.getExternalReferenceCode());
		Assert.assertEquals(regex, pageSpecificationVersion.getName());
	}

	@Test
	public void testDeleteSiteSitePagePageSpecificationVersion()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PageSpecificationVersion pageSpecificationVersion =
			testDeleteSiteSitePagePageSpecificationVersion_addPageSpecificationVersion();

		assertHttpResponseStatusCode(
			204,
			pageSpecificationVersionResource.
				deleteSiteSitePagePageSpecificationVersionHttpResponse(
					testDeleteSiteSitePagePageSpecificationVersion_getSiteExternalReferenceCode(),
					testDeleteSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersionHttpResponse(
					testDeleteSiteSitePagePageSpecificationVersion_getSiteExternalReferenceCode(),
					testDeleteSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode(),
					pageSpecificationVersion.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersionHttpResponse(
					testDeleteSiteSitePagePageSpecificationVersion_getSiteExternalReferenceCode(),
					testDeleteSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode(),
					"-"));
	}

	protected PageSpecificationVersion
			testDeleteSiteSitePagePageSpecificationVersion_addPageSpecificationVersion()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteSiteSitePagePageSpecificationVersion_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testDeleteSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSitePagePageSpecificationVersion() throws Exception {
		PageSpecificationVersion postPageSpecificationVersion =
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion();

		PageSpecificationVersion getPageSpecificationVersion =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersion(
					testGetSiteSitePagePageSpecificationVersion_getSiteExternalReferenceCode(),
					testGetSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode(),
					postPageSpecificationVersion.getExternalReferenceCode());

		assertEquals(postPageSpecificationVersion, getPageSpecificationVersion);
		assertValid(getPageSpecificationVersion);
	}

	protected PageSpecificationVersion
			testGetSiteSitePagePageSpecificationVersion_addPageSpecificationVersion()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSitePagePageSpecificationVersion_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSitePagePageSpecificationVersion_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSitePagePageSpecificationVersionsPage()
		throws Exception {

		String siteExternalReferenceCode =
			testGetSiteSitePagePageSpecificationVersionsPage_getSiteExternalReferenceCode();
		String irrelevantSiteExternalReferenceCode =
			testGetSiteSitePagePageSpecificationVersionsPage_getIrrelevantSiteExternalReferenceCode();
		String sitePageExternalReferenceCode =
			testGetSiteSitePagePageSpecificationVersionsPage_getSitePageExternalReferenceCode();
		String irrelevantSitePageExternalReferenceCode =
			testGetSiteSitePagePageSpecificationVersionsPage_getIrrelevantSitePageExternalReferenceCode();

		Page<PageSpecificationVersion> page =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersionsPage(
					siteExternalReferenceCode, sitePageExternalReferenceCode);

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteExternalReferenceCode != null) &&
			(irrelevantSitePageExternalReferenceCode != null)) {

			PageSpecificationVersion irrelevantPageSpecificationVersion =
				testGetSiteSitePagePageSpecificationVersionsPage_addPageSpecificationVersion(
					irrelevantSiteExternalReferenceCode,
					irrelevantSitePageExternalReferenceCode,
					randomIrrelevantPageSpecificationVersion());

			page =
				pageSpecificationVersionResource.
					getSiteSitePagePageSpecificationVersionsPage(
						irrelevantSiteExternalReferenceCode,
						irrelevantSitePageExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPageSpecificationVersion,
				(List<PageSpecificationVersion>)page.getItems());
			assertValid(
				page,
				testGetSiteSitePagePageSpecificationVersionsPage_getExpectedActions(
					irrelevantSiteExternalReferenceCode,
					irrelevantSitePageExternalReferenceCode));
		}

		PageSpecificationVersion pageSpecificationVersion1 =
			testGetSiteSitePagePageSpecificationVersionsPage_addPageSpecificationVersion(
				siteExternalReferenceCode, sitePageExternalReferenceCode,
				randomPageSpecificationVersion());

		PageSpecificationVersion pageSpecificationVersion2 =
			testGetSiteSitePagePageSpecificationVersionsPage_addPageSpecificationVersion(
				siteExternalReferenceCode, sitePageExternalReferenceCode,
				randomPageSpecificationVersion());

		page =
			pageSpecificationVersionResource.
				getSiteSitePagePageSpecificationVersionsPage(
					siteExternalReferenceCode, sitePageExternalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			pageSpecificationVersion1,
			(List<PageSpecificationVersion>)page.getItems());
		assertContains(
			pageSpecificationVersion2,
			(List<PageSpecificationVersion>)page.getItems());
		assertValid(
			page,
			testGetSiteSitePagePageSpecificationVersionsPage_getExpectedActions(
				siteExternalReferenceCode, sitePageExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetSiteSitePagePageSpecificationVersionsPage_getExpectedActions(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected PageSpecificationVersion
			testGetSiteSitePagePageSpecificationVersionsPage_addPageSpecificationVersion(
				String siteExternalReferenceCode,
				String sitePageExternalReferenceCode,
				PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getSiteExternalReferenceCode()
		throws Exception {

		return testGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getIrrelevantSiteExternalReferenceCode()
		throws Exception {

		return irrelevantGroup.getExternalReferenceCode();
	}

	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getSitePageExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteSitePagePageSpecificationVersionsPage_getIrrelevantSitePageExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPostSiteSitePagePageSpecificationVersionRestore()
		throws Exception {

		Assert.assertTrue(true);
	}

	protected void assertContains(
		PageSpecificationVersion pageSpecificationVersion,
		List<PageSpecificationVersion> pageSpecificationVersions) {

		boolean contains = false;

		for (PageSpecificationVersion item : pageSpecificationVersions) {
			if (equals(pageSpecificationVersion, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			pageSpecificationVersions + " does not contain " +
				pageSpecificationVersion,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PageSpecificationVersion pageSpecificationVersion1,
		PageSpecificationVersion pageSpecificationVersion2) {

		Assert.assertTrue(
			pageSpecificationVersion1 + " does not equal " +
				pageSpecificationVersion2,
			equals(pageSpecificationVersion1, pageSpecificationVersion2));
	}

	protected void assertEquals(
		List<PageSpecificationVersion> pageSpecificationVersions1,
		List<PageSpecificationVersion> pageSpecificationVersions2) {

		Assert.assertEquals(
			pageSpecificationVersions1.size(),
			pageSpecificationVersions2.size());

		for (int i = 0; i < pageSpecificationVersions1.size(); i++) {
			PageSpecificationVersion pageSpecificationVersion1 =
				pageSpecificationVersions1.get(i);
			PageSpecificationVersion pageSpecificationVersion2 =
				pageSpecificationVersions2.get(i);

			assertEquals(pageSpecificationVersion1, pageSpecificationVersion2);
		}
	}

	protected void assertEquals(
		PageSpecification pageSpecification1,
		PageSpecification pageSpecification2) {

		Assert.assertTrue(
			pageSpecification1 + " does not equal " + pageSpecification2,
			equals(pageSpecification1, pageSpecification2));
	}

	protected void assertEqualsIgnoringOrder(
		List<PageSpecificationVersion> pageSpecificationVersions1,
		List<PageSpecificationVersion> pageSpecificationVersions2) {

		Assert.assertEquals(
			pageSpecificationVersions1.size(),
			pageSpecificationVersions2.size());

		for (PageSpecificationVersion pageSpecificationVersion1 :
				pageSpecificationVersions1) {

			boolean contains = false;

			for (PageSpecificationVersion pageSpecificationVersion2 :
					pageSpecificationVersions2) {

				if (equals(
						pageSpecificationVersion1, pageSpecificationVersion2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				pageSpecificationVersions2 + " does not contain " +
					pageSpecificationVersion1,
				contains);
		}
	}

	protected void assertValid(
			PageSpecificationVersion pageSpecificationVersion)
		throws Exception {

		boolean valid = true;

		if (pageSpecificationVersion.getDateCreated() == null) {
			valid = false;
		}

		if (pageSpecificationVersion.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (pageSpecificationVersion.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (pageSpecificationVersion.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (pageSpecificationVersion.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (pageSpecificationVersion.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecification", additionalAssertFieldName)) {

				if (pageSpecificationVersion.getPageSpecification() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecificationVersionPageExperiences",
					additionalAssertFieldName)) {

				if (pageSpecificationVersion.
						getPageSpecificationVersionPageExperiences() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (pageSpecificationVersion.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("statusDate", additionalAssertFieldName)) {
				if (pageSpecificationVersion.getStatusDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (pageSpecificationVersion.getVersion() == null) {
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

	protected void assertValid(Page<PageSpecificationVersion> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PageSpecificationVersion> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PageSpecificationVersion>
			pageSpecificationVersions = page.getItems();

		int size = pageSpecificationVersions.size();

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

	protected void assertValid(PageSpecification pageSpecification) {
		boolean valid = true;

		if (pageSpecification.getExternalReferenceCode() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalPageSpecificationAssertFieldNames()) {

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (pageSpecification.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (pageSpecification.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"siteTemplatePageSpecificationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (pageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (pageSpecification.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (pageSpecification.getType() == null) {
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

	protected String[] getAdditionalPageSpecificationAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.site.dto.v1_0.
						PageSpecificationVersion.class)) {

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
		PageSpecificationVersion pageSpecificationVersion1,
		PageSpecificationVersion pageSpecificationVersion2) {

		if (pageSpecificationVersion1 == pageSpecificationVersion2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)pageSpecificationVersion1.getActions(),
						(Map)pageSpecificationVersion2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecificationVersion1.getCreator(),
						pageSpecificationVersion2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecificationVersion1.getDateCreated(),
						pageSpecificationVersion2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecificationVersion1.getDateModified(),
						pageSpecificationVersion2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageSpecificationVersion1.getExternalReferenceCode(),
						pageSpecificationVersion2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecificationVersion1.getName(),
						pageSpecificationVersion2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecification", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageSpecificationVersion1.getPageSpecification(),
						pageSpecificationVersion2.getPageSpecification())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"pageSpecificationVersionPageExperiences",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageSpecificationVersion1.
							getPageSpecificationVersionPageExperiences(),
						pageSpecificationVersion2.
							getPageSpecificationVersionPageExperiences())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecificationVersion1.getStatus(),
						pageSpecificationVersion2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("statusDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecificationVersion1.getStatusDate(),
						pageSpecificationVersion2.getStatusDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("version", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecificationVersion1.getVersion(),
						pageSpecificationVersion2.getVersion())) {

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
		PageSpecification pageSpecification1,
		PageSpecification pageSpecification2) {

		if (pageSpecification1 == pageSpecification2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalPageSpecificationAssertFieldNames()) {

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecification1.getCustomFields(),
						pageSpecification2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageSpecification1.getExternalReferenceCode(),
						pageSpecification2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"siteTemplatePageSpecificationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						pageSpecification1.
							getSiteTemplatePageSpecificationExternalReferenceCode(),
						pageSpecification2.
							getSiteTemplatePageSpecificationExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecification1.getStatus(),
						pageSpecification2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						pageSpecification1.getType(),
						pageSpecification2.getType())) {

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

		if (!(_pageSpecificationVersionResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_pageSpecificationVersionResource;

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
		PageSpecificationVersion pageSpecificationVersion) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = pageSpecificationVersion.getDateCreated();

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
					_format.format(pageSpecificationVersion.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = pageSpecificationVersion.getDateModified();

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
					_format.format(pageSpecificationVersion.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = pageSpecificationVersion.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = pageSpecificationVersion.getName();

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

		if (entityFieldName.equals("pageSpecification")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pageSpecificationVersionPageExperiences")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("statusDate")) {
			if (operator.equals("between")) {
				Date date = pageSpecificationVersion.getStatusDate();

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
					_format.format(pageSpecificationVersion.getStatusDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("version")) {
			sb.append(String.valueOf(pageSpecificationVersion.getVersion()));

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

	protected PageSpecificationVersion randomPageSpecificationVersion()
		throws Exception {

		return new PageSpecificationVersion() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				statusDate = RandomTestUtil.nextDate();
				version = RandomTestUtil.randomInt();
			}
		};
	}

	protected PageSpecificationVersion
			randomIrrelevantPageSpecificationVersion()
		throws Exception {

		PageSpecificationVersion randomIrrelevantPageSpecificationVersion =
			randomPageSpecificationVersion();

		return randomIrrelevantPageSpecificationVersion;
	}

	protected PageSpecificationVersion randomPatchPageSpecificationVersion()
		throws Exception {

		return randomPageSpecificationVersion();
	}

	protected PageSpecification randomPageSpecification() throws Exception {
		return new PageSpecification() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
				siteTemplatePageSpecificationExternalReferenceCode =
					RandomTestUtil.randomString();
			}
		};
	}

	protected PageSpecificationVersionResource pageSpecificationVersionResource;
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
			BasePageSpecificationVersionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.
		PageSpecificationVersionResource _pageSpecificationVersionResource;

}
// LIFERAY-REST-BUILDER-HASH:354550438