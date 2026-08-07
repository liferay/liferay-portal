/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
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

import com.liferay.headless.admin.site.client.dto.v1_0.Site;
import com.liferay.headless.admin.site.client.http.HttpInvoker;
import com.liferay.headless.admin.site.client.pagination.Page;
import com.liferay.headless.admin.site.client.pagination.Pagination;
import com.liferay.headless.admin.site.client.permission.Permission;
import com.liferay.headless.admin.site.client.resource.v1_0.SiteResource;
import com.liferay.headless.admin.site.client.serdes.v1_0.SiteSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
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

import java.io.File;

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
public abstract class BaseSiteResourceTestCase {

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

		_siteResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		siteResource = SiteResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		permissionsSiteResource = SiteResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).locale(
			LocaleUtil.getDefault()
		).parameter(
			"nestedFields", "permissions"
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

		Site site1 = randomSite();

		String json = objectMapper.writeValueAsString(site1);

		Site site2 = SiteSerDes.toDTO(json);

		Assert.assertTrue(equals(site1, site2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Site site = randomSite();

		String json1 = objectMapper.writeValueAsString(site);
		String json2 = SiteSerDes.toJSON(site);

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

		Site site = randomSite();

		site.setDefaultLanguageId(regex);
		site.setDescription(regex);
		site.setDescriptiveName(regex);
		site.setExternalReferenceCode(regex);
		site.setFriendlyUrlPath(regex);
		site.setKey(regex);
		site.setLogo(regex);
		site.setName(regex);
		site.setParentSiteExternalReferenceCode(regex);
		site.setTemplateKey(regex);

		String json = SiteSerDes.toJSON(site);

		Assert.assertFalse(json.contains(regex));

		site = SiteSerDes.toDTO(json);

		Assert.assertEquals(regex, site.getDefaultLanguageId());
		Assert.assertEquals(regex, site.getDescription());
		Assert.assertEquals(regex, site.getDescriptiveName());
		Assert.assertEquals(regex, site.getExternalReferenceCode());
		Assert.assertEquals(regex, site.getFriendlyUrlPath());
		Assert.assertEquals(regex, site.getKey());
		Assert.assertEquals(regex, site.getLogo());
		Assert.assertEquals(regex, site.getName());
		Assert.assertEquals(regex, site.getParentSiteExternalReferenceCode());
		Assert.assertEquals(regex, site.getTemplateKey());
	}

	@Test
	public void testDeleteSite() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testDeleteSite_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.deleteSiteHttpResponse(
				site.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			siteResource.getSiteHttpResponse(site.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse("-"));
	}

	protected Site testDeleteSite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteSiteBatch() throws Exception {
		Site site1 = testDeleteSiteBatch_addSite();

		testDeleteSiteBatch_deleteSite(
			202, site1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			siteResource.getSiteHttpResponse(site1.getExternalReferenceCode()));
	}

	protected Site testDeleteSiteBatch_addSite() throws Exception {
		return testDeleteSite_addSite();
	}

	protected void testDeleteSiteBatch_deleteSite(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			siteResource.deleteSiteBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetSite() throws Exception {
		Site postSite = testGetSite_addSite();

		Site getSite = siteResource.getSite(
			postSite.getExternalReferenceCode());

		assertEquals(postSite, getSite);
		assertValid(getSite);

		Assert.assertNull(getSite.getPermissions());

		getSite = permissionsSiteResource.getSite(
			postSite.getExternalReferenceCode());

		Assert.assertNotNull(getSite.getPermissions());
	}

	protected Site testGetSite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSitePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site postSite = testGetSitePermissionsPage_addSite();

		Page<Permission> page = siteResource.getSitePermissionsPage(
			postSite.getExternalReferenceCode(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected Site testGetSitePermissionsPage_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteSiteInitializer() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetSitesPage() throws Exception {
		Page<Site> page = siteResource.getSitesPage(
			null, null, null, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		Site site1 = testGetSitesPage_addSite(randomSite());

		Site site2 = testGetSitesPage_addSite(randomSite());

		page = siteResource.getSitesPage(
			null, null, null, null, Pagination.of(1, (int)totalCount + 2));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(site1, (List<Site>)page.getItems());
		assertContains(site2, (List<Site>)page.getItems());
		assertValid(page, testGetSitesPage_getExpectedActions());

		for (Site site : page.getItems()) {
			Assert.assertNull(site.getPermissions());
		}

		page = permissionsSiteResource.getSitesPage(
			null, null, null, null, Pagination.of(1, 10));

		for (Site site : page.getItems()) {
			Assert.assertNotNull(site.getPermissions());
		}

		siteResource.deleteSite(site1.getExternalReferenceCode());

		siteResource.deleteSite(site2.getExternalReferenceCode());
	}

	protected Map<String, Map<String, String>>
			testGetSitesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSitesPageWithPagination() throws Exception {
		Page<Site> sitesPage = siteResource.getSitesPage(
			null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(sitesPage.getTotalCount());

		Site site1 = testGetSitesPage_addSite(randomSite());

		Site site2 = testGetSitesPage_addSite(randomSite());

		Site site3 = testGetSitesPage_addSite(randomSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Site> page1 = siteResource.getSitesPage(
				null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(site1, (List<Site>)page1.getItems());

			Page<Site> page2 = siteResource.getSitesPage(
				null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(site2, (List<Site>)page2.getItems());

			Page<Site> page3 = siteResource.getSitesPage(
				null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(site3, (List<Site>)page3.getItems());
		}
		else {
			Page<Site> page1 = siteResource.getSitesPage(
				null, null, null, null, Pagination.of(1, totalCount + 2));

			List<Site> sites1 = (List<Site>)page1.getItems();

			Assert.assertEquals(
				sites1.toString(), totalCount + 2, sites1.size());

			Page<Site> page2 = siteResource.getSitesPage(
				null, null, null, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Site> sites2 = (List<Site>)page2.getItems();

			Assert.assertEquals(sites2.toString(), 1, sites2.size());

			Page<Site> page3 = siteResource.getSitesPage(
				null, null, null, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(site1, (List<Site>)page3.getItems());
			assertContains(site2, (List<Site>)page3.getItems());
			assertContains(site3, (List<Site>)page3.getItems());
		}
	}

	protected Site testGetSitesPage_addSite(Site site) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSite() throws Exception {
		Site randomSite = randomSite();

		Site postSite = testPostSite_addSite(randomSite);

		assertEquals(randomSite, postSite);
		assertValid(postSite);

		Site randomPermissionsSite1 = randomPermissionsSite();

		Site postPermissionsSite1 = testPostSite_addSite(
			randomPermissionsSite1);

		Assert.assertNull(postPermissionsSite1.getPermissions());

		Site randomPermissionsSite2 = randomPermissionsSite();

		Site postPermissionsSite2 = testPostSite_addPermissionsSite(
			randomPermissionsSite2);

		Assert.assertNotNull(postPermissionsSite2.getPermissions());
	}

	protected Site testPostSite_addSite(Site site) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Site testPostSite_addPermissionsSite(Site site) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteSiteInitializer() throws Exception {
		Site randomSite = randomSite();

		Map<String, File> multipartFiles = getMultipartFiles();

		Site postSite = testPostSiteSiteInitializer_addSite(
			randomSite, multipartFiles);

		assertEquals(randomSite, postSite);
		assertValid(postSite);

		assertValid(postSite, multipartFiles);
	}

	protected Site testPostSiteSiteInitializer_addSite(
			Site site, Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSite() throws Exception {
		Site postSite = testPutSite_addSite();

		Site randomSite = randomSite();

		Site putSite = siteResource.putSite(
			postSite.getExternalReferenceCode(), randomSite);

		assertEquals(randomSite, putSite);
		assertValid(putSite);

		Assert.assertNull(putSite.getPermissions());

		Site getSite = siteResource.getSite(putSite.getExternalReferenceCode());

		assertEquals(randomSite, getSite);
		assertValid(getSite);

		Site randomPermissionsSite = randomPermissionsSite();

		putSite = siteResource.putSite(
			postSite.getExternalReferenceCode(), randomPermissionsSite);

		assertEquals(randomPermissionsSite, putSite);
		assertValid(putSite);

		Assert.assertNull(putSite.getPermissions());

		putSite = permissionsSiteResource.putSite(
			postSite.getExternalReferenceCode(), randomPermissionsSite);

		Assert.assertNotNull(putSite.getPermissions());
	}

	protected Site testPutSite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteActivate() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testPutSiteActivate_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.putSiteActivateHttpResponse(
				site.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404, siteResource.putSiteActivateHttpResponse("-"));
	}

	protected Site testPutSiteActivate_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteDeactivate() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testPutSiteDeactivate_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.putSiteDeactivateHttpResponse(
				site.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404, siteResource.putSiteDeactivateHttpResponse("-"));
	}

	protected Site testPutSiteDeactivate_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSitePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testPutSitePermissionsPage_addSite();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			siteResource.putSitePermissionsPageHttpResponse(
				site.getExternalReferenceCode(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"PERMISSIONS"});
							setRoleName(role.getName());
						}
					}
				}));

		assertHttpResponseStatusCode(
			404,
			siteResource.putSitePermissionsPageHttpResponse(
				site.getExternalReferenceCode(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected Site testPutSitePermissionsPage_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteSiteInitializer() throws Exception {
		Site postSite = testPutSiteSiteInitializer_addSite();

		Site randomSite = randomSite();

		Map<String, File> multipartFiles = getMultipartFiles();

		Site putSite = siteResource.putSiteSiteInitializer(
			postSite.getExternalReferenceCode(), randomSite, multipartFiles);

		assertEquals(randomSite, putSite);
		assertValid(putSite);

		Site getSite = testPutSiteSiteInitializer_getSite(
			putSite.getExternalReferenceCode());

		assertEquals(randomSite, getSite);
		assertValid(getSite);

		assertValid(getSite, multipartFiles);
	}

	protected Site testPutSiteSiteInitializer_getSite(
		String siteExternalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Site testPutSiteSiteInitializer_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Site site1 = testBatchEngineDeleteImportTask_addSite();

		testBatchEngineDeleteImportTask_deleteSite(
			200, site1.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			siteResource.getSiteHttpResponse(site1.getExternalReferenceCode()));
	}

	protected Site testBatchEngineDeleteImportTask_addSite() throws Exception {
		return testDeleteSite_addSite();
	}

	protected void testBatchEngineDeleteImportTask_deleteSite(
			int expectedStatusCode, String externalReferenceCode,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.admin.site.dto.v1_0.Site", null, null,
				null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected void assertContains(Site site, List<Site> sites) {
		boolean contains = false;

		for (Site item : sites) {
			if (equals(site, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(sites + " does not contain " + site, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Site site1, Site site2) {
		Assert.assertTrue(
			site1 + " does not equal " + site2, equals(site1, site2));
	}

	protected void assertEquals(List<Site> sites1, List<Site> sites2) {
		Assert.assertEquals(sites1.size(), sites2.size());

		for (int i = 0; i < sites1.size(); i++) {
			Site site1 = sites1.get(i);
			Site site2 = sites2.get(i);

			assertEquals(site1, site2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Site> sites1, List<Site> sites2) {

		Assert.assertEquals(sites1.size(), sites2.size());

		for (Site site1 : sites1) {
			boolean contains = false;

			for (Site site2 : sites2) {
				if (equals(site1, site2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(sites2 + " does not contain " + site1, contains);
		}
	}

	protected void assertValid(Site site) throws Exception {
		boolean valid = true;

		if (site.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (site.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"analyticsConfiguration", additionalAssertFieldName)) {

				if (site.getAnalyticsConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"assetAutoTaggingEnabled", additionalAssertFieldName)) {

				if (site.getAssetAutoTaggingEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"contentSharingWithChildrenEnabled",
					additionalAssertFieldName)) {

				if (site.getContentSharingWithChildrenEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (site.getDefaultLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (site.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (site.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("descriptiveName", additionalAssertFieldName)) {
				if (site.getDescriptiveName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"descriptiveName_i18n", additionalAssertFieldName)) {

				if (site.getDescriptiveName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"directoryIndexingEnabled", additionalAssertFieldName)) {

				if (site.getDirectoryIndexingEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (site.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (site.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("inheritLocales", additionalAssertFieldName)) {
				if (site.getInheritLocales() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (site.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("locales", additionalAssertFieldName)) {
				if (site.getLocales() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("logo", additionalAssertFieldName)) {
				if (site.getLogo() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("manualMembership", additionalAssertFieldName)) {
				if (site.getManualMembership() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("mapProviderKey", additionalAssertFieldName)) {
				if (site.getMapProviderKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"membershipRestriction", additionalAssertFieldName)) {

				if (site.getMembershipRestriction() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("membershipType", additionalAssertFieldName)) {
				if (site.getMembershipType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("mentionsEnabled", additionalAssertFieldName)) {
				if (site.getMentionsEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (site.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (site.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentSiteExternalReferenceCode",
					additionalAssertFieldName)) {

				if (site.getParentSiteExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (site.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ratingsTypes", additionalAssertFieldName)) {
				if (site.getRatingsTypes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sharingEnabled", additionalAssertFieldName)) {
				if (site.getSharingEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("templateKey", additionalAssertFieldName)) {
				if (site.getTemplateKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("templateType", additionalAssertFieldName)) {
				if (site.getTemplateType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("trashEnabled", additionalAssertFieldName)) {
				if (site.getTrashEnabled() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"trashEntriesMaxAge", additionalAssertFieldName)) {

				if (site.getTrashEntriesMaxAge() == null) {
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

	protected void assertValid(Site site, Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<Site> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Site> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Site> sites = page.getItems();

		int size = sites.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.site.dto.v1_0.Site.class)) {

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

	protected boolean equals(Site site1, Site site2) {
		if (site1 == site2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(site1.getActive(), site2.getActive())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"analyticsConfiguration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getAnalyticsConfiguration(),
						site2.getAnalyticsConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"assetAutoTaggingEnabled", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getAssetAutoTaggingEnabled(),
						site2.getAssetAutoTaggingEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"contentSharingWithChildrenEnabled",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getContentSharingWithChildrenEnabled(),
						site2.getContentSharingWithChildrenEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getDefaultLanguageId(),
						site2.getDefaultLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getDescription(), site2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)site1.getDescription_i18n(),
						(Map)site2.getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("descriptiveName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getDescriptiveName(),
						site2.getDescriptiveName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"descriptiveName_i18n", additionalAssertFieldName)) {

				if (!equals(
						(Map)site1.getDescriptiveName_i18n(),
						(Map)site2.getDescriptiveName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"directoryIndexingEnabled", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getDirectoryIndexingEnabled(),
						site2.getDirectoryIndexingEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getExternalReferenceCode(),
						site2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getFriendlyUrlPath(),
						site2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(site1.getId(), site2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("inheritLocales", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getInheritLocales(), site2.getInheritLocales())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(site1.getKey(), site2.getKey())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("locales", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getLocales(), site2.getLocales())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("logo", additionalAssertFieldName)) {
				if (!Objects.deepEquals(site1.getLogo(), site2.getLogo())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("manualMembership", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getManualMembership(),
						site2.getManualMembership())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("mapProviderKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getMapProviderKey(), site2.getMapProviderKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"membershipRestriction", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getMembershipRestriction(),
						site2.getMembershipRestriction())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("membershipType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getMembershipType(), site2.getMembershipType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("mentionsEnabled", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getMentionsEnabled(),
						site2.getMentionsEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(site1.getName(), site2.getName())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)site1.getName_i18n(), (Map)site2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentSiteExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getParentSiteExternalReferenceCode(),
						site2.getParentSiteExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getPermissions(), site2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("ratingsTypes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getRatingsTypes(), site2.getRatingsTypes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sharingEnabled", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getSharingEnabled(), site2.getSharingEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("templateKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getTemplateKey(), site2.getTemplateKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("templateType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getTemplateType(), site2.getTemplateType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("trashEnabled", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getTrashEnabled(), site2.getTrashEnabled())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"trashEntriesMaxAge", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getTrashEntriesMaxAge(),
						site2.getTrashEntriesMaxAge())) {

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

		if (!(_siteResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_siteResource;

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
		EntityField entityField, String operator, Site site) {

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

		if (entityFieldName.equals("analyticsConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetAutoTaggingEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("contentSharingWithChildrenEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("defaultLanguageId")) {
			Object object = site.getDefaultLanguageId();

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

		if (entityFieldName.equals("description")) {
			Object object = site.getDescription();

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

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("descriptiveName")) {
			Object object = site.getDescriptiveName();

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

		if (entityFieldName.equals("descriptiveName_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("directoryIndexingEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = site.getExternalReferenceCode();

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

		if (entityFieldName.equals("friendlyUrlPath")) {
			Object object = site.getFriendlyUrlPath();

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

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("inheritLocales")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("key")) {
			Object object = site.getKey();

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

		if (entityFieldName.equals("locales")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("logo")) {
			Object object = site.getLogo();

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

		if (entityFieldName.equals("manualMembership")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("mapProviderKey")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("membershipRestriction")) {
			sb.append(String.valueOf(site.getMembershipRestriction()));

			return sb.toString();
		}

		if (entityFieldName.equals("membershipType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("mentionsEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = site.getName();

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

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentSiteExternalReferenceCode")) {
			Object object = site.getParentSiteExternalReferenceCode();

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

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("ratingsTypes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sharingEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("templateKey")) {
			Object object = site.getTemplateKey();

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

		if (entityFieldName.equals("templateType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("trashEnabled")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("trashEntriesMaxAge")) {
			sb.append(String.valueOf(site.getTrashEntriesMaxAge()));

			return sb.toString();
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected Map<String, File> getMultipartFiles() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

	protected Site randomSite() throws Exception {
		return new Site() {
			{
				active = RandomTestUtil.randomBoolean();
				assetAutoTaggingEnabled = RandomTestUtil.randomBoolean();
				contentSharingWithChildrenEnabled =
					RandomTestUtil.randomBoolean();
				defaultLanguageId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				descriptiveName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				directoryIndexingEnabled = RandomTestUtil.randomBoolean();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				inheritLocales = RandomTestUtil.randomBoolean();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				logo = StringUtil.toLowerCase(RandomTestUtil.randomString());
				manualMembership = RandomTestUtil.randomBoolean();
				membershipRestriction = RandomTestUtil.randomInt();
				mentionsEnabled = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				parentSiteExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				sharingEnabled = RandomTestUtil.randomBoolean();
				templateKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				trashEnabled = RandomTestUtil.randomBoolean();
				trashEntriesMaxAge = RandomTestUtil.randomInt();
			}
		};
	}

	protected Site randomIrrelevantSite() throws Exception {
		Site randomIrrelevantSite = randomSite();

		return randomIrrelevantSite;
	}

	protected Site randomPatchSite() throws Exception {
		return randomSite();
	}

	protected Site randomPermissionsSite() throws Exception {
		Site site = randomSite();

		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		site.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {"VIEW"});
						setRoleName(role.getName());
					}
				}
			});

		return site;
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected SiteResource siteResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected SiteResource permissionsSiteResource;
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
		LogFactoryUtil.getLog(BaseSiteResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.site.resource.v1_0.SiteResource
		_siteResource;

}
// LIFERAY-REST-BUILDER-HASH:1982169433