/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.site.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.site.client.dto.v1_0.Site;
import com.liferay.headless.site.client.http.HttpInvoker;
import com.liferay.headless.site.client.pagination.Page;
import com.liferay.headless.site.client.pagination.Pagination;
import com.liferay.headless.site.client.resource.v1_0.SiteResource;
import com.liferay.headless.site.client.serdes.v1_0.SiteSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.io.File;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public abstract class BaseSiteResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

		_siteResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		siteResource = SiteResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
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

		site.setExternalReferenceCode(regex);
		site.setFriendlyUrlPath(regex);
		site.setKey(regex);
		site.setName(regex);
		site.setParentSiteExternalReferenceCode(regex);
		site.setParentSiteKey(regex);
		site.setTemplateKey(regex);

		String json = SiteSerDes.toJSON(site);

		Assert.assertFalse(json.contains(regex));

		site = SiteSerDes.toDTO(json);

		Assert.assertEquals(regex, site.getExternalReferenceCode());
		Assert.assertEquals(regex, site.getFriendlyUrlPath());
		Assert.assertEquals(regex, site.getKey());
		Assert.assertEquals(regex, site.getName());
		Assert.assertEquals(regex, site.getParentSiteExternalReferenceCode());
		Assert.assertEquals(regex, site.getParentSiteKey());
		Assert.assertEquals(regex, site.getTemplateKey());
	}

	@Test
	public void testDeleteSite() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testDeleteSite_addSite();

		assertHttpResponseStatusCode(
			204, siteResource.deleteSiteHttpResponse(site.getId()));

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site.getId()));
		assertHttpResponseStatusCode(404, siteResource.getSiteHttpResponse(0L));
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
			404, siteResource.getSiteHttpResponse(site1.getId()));

		site1 = testDeleteSiteBatch_addSite();

		testDeleteSiteBatch_deleteSite(202, null, site1.getId());

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site1.getId()));

		site1 = testDeleteSiteBatch_addSite();
		Site site2 = testDeleteSiteBatch_addSite();

		testDeleteSiteBatch_deleteSite(
			202, site2.getExternalReferenceCode(), site1.getId());

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site1.getId()));
		assertHttpResponseStatusCode(
			200, siteResource.getSiteHttpResponse(site2.getId()));

		testDeleteSiteBatch_deleteSite(
			202, site2.getExternalReferenceCode(), site1.getId());

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site2.getId()));
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
	public void testDeleteSiteByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testDeleteSiteByExternalReferenceCode_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.deleteSiteByExternalReferenceCodeHttpResponse(
				site.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			siteResource.getSiteByExternalReferenceCodeHttpResponse(
				site.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404, siteResource.getSiteByExternalReferenceCodeHttpResponse("-"));
	}

	protected Site testDeleteSiteByExternalReferenceCode_addSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSite() throws Exception {
		Site postSite = testGetSite_addSite();

		Site getSite = siteResource.getSite(postSite.getId());

		assertEquals(postSite, getSite);
		assertValid(getSite);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Site postSite = testGetSite_addSite();

		Site getSite = siteResource.getSite(postSite.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.site.dto.v1_0.Site"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(postSite.getId());

		assertEquals(getSite, SiteSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected Site testGetSite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteByExternalReferenceCode() throws Exception {
		Site postSite = testGetSiteByExternalReferenceCode_addSite();

		Site getSite = siteResource.getSiteByExternalReferenceCode(
			postSite.getExternalReferenceCode());

		assertEquals(postSite, getSite);
		assertValid(getSite);
	}

	protected Site testGetSiteByExternalReferenceCode_addSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteByExternalReferenceCodeSiteInitializer()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetSitesPage() throws Exception {
		Page<Site> page = siteResource.getSitesPage(
			null, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		Site site1 = testGetSitesPage_addSite(randomSite());

		Site site2 = testGetSitesPage_addSite(randomSite());

		page = siteResource.getSitesPage(null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(site1, (List<Site>)page.getItems());
		assertContains(site2, (List<Site>)page.getItems());
		assertValid(page, testGetSitesPage_getExpectedActions());

		siteResource.deleteSite(site1.getId());

		siteResource.deleteSite(site2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSitesPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSitesPageWithPagination() throws Exception {
		Page<Site> sitesPage = siteResource.getSitesPage(null, null, null);

		int totalCount = GetterUtil.getInteger(sitesPage.getTotalCount());

		Site site1 = testGetSitesPage_addSite(randomSite());

		Site site2 = testGetSitesPage_addSite(randomSite());

		Site site3 = testGetSitesPage_addSite(randomSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Site> page1 = siteResource.getSitesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(site1, (List<Site>)page1.getItems());

			Page<Site> page2 = siteResource.getSitesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(site2, (List<Site>)page2.getItems());

			Page<Site> page3 = siteResource.getSitesPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(site3, (List<Site>)page3.getItems());
		}
		else {
			Page<Site> page1 = siteResource.getSitesPage(
				null, null, Pagination.of(1, totalCount + 2));

			List<Site> sites1 = (List<Site>)page1.getItems();

			Assert.assertEquals(
				sites1.toString(), totalCount + 2, sites1.size());

			Page<Site> page2 = siteResource.getSitesPage(
				null, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Site> sites2 = (List<Site>)page2.getItems();

			Assert.assertEquals(sites2.toString(), 1, sites2.size());

			Page<Site> page3 = siteResource.getSitesPage(
				null, null, Pagination.of(1, (int)totalCount + 3));

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
	}

	protected Site testPostSite_addSite(Site site) throws Exception {
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

		Site putSite = siteResource.putSite(randomSite);

		assertEquals(randomSite, putSite);
		assertValid(putSite);

		Site getSite = siteResource.getSite(putSite.getId());

		assertEquals(randomSite, getSite);
		assertValid(getSite);
	}

	protected Site testPutSite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSiteByExternalReferenceCode() throws Exception {
		Site postSite = testPutSiteByExternalReferenceCode_addSite();

		Site randomSite = randomSite();

		Map<String, File> multipartFiles = getMultipartFiles();

		Site putSite = siteResource.putSiteByExternalReferenceCode(
			postSite.getExternalReferenceCode(), randomSite, multipartFiles);

		assertEquals(randomSite, putSite);
		assertValid(putSite);

		Site getSite = siteResource.getSiteByExternalReferenceCode(
			putSite.getExternalReferenceCode());

		assertEquals(randomSite, getSite);
		assertValid(getSite);

		assertValid(getSite, multipartFiles);

		Site newSite = testPutSiteByExternalReferenceCode_createSite();

		putSite = siteResource.putSiteByExternalReferenceCode(
			newSite.getExternalReferenceCode(), newSite, getMultipartFiles());

		assertEquals(newSite, putSite);
		assertValid(putSite);

		getSite = siteResource.getSiteByExternalReferenceCode(
			putSite.getExternalReferenceCode());

		assertEquals(newSite, getSite);

		Assert.assertEquals(
			newSite.getExternalReferenceCode(),
			putSite.getExternalReferenceCode());
	}

	protected Site testPutSiteByExternalReferenceCode_addSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Site testPutSiteByExternalReferenceCode_createSite()
		throws Exception {

		return randomSite();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Site site1 = testBatchEngineDeleteImportTask_addSite();

		testBatchEngineDeleteImportTask_deleteSite(
			200, site1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site1.getId()));

		site1 = testBatchEngineDeleteImportTask_addSite();

		testBatchEngineDeleteImportTask_deleteSite(200, null, site1.getId());

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site1.getId()));

		site1 = testBatchEngineDeleteImportTask_addSite();
		Site site2 = testBatchEngineDeleteImportTask_addSite();

		testBatchEngineDeleteImportTask_deleteSite(
			200, site2.getExternalReferenceCode(), site1.getId());

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site1.getId()));
		assertHttpResponseStatusCode(
			200, siteResource.getSiteHttpResponse(site2.getId()));

		testBatchEngineDeleteImportTask_deleteSite(
			200, site2.getExternalReferenceCode(), site1.getId());

		assertHttpResponseStatusCode(
			404, siteResource.getSiteHttpResponse(site2.getId()));
	}

	protected Site testBatchEngineDeleteImportTask_addSite() throws Exception {
		return testDeleteSite_addSite();
	}

	protected void testBatchEngineDeleteImportTask_deleteSite(
			int expectedStatusCode, String externalReferenceCode, Long id,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.headless.site.dto.v1_0.Site", null, null, null,
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

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

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (site.getDescription() == null) {
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

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (site.getKey() == null) {
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

			if (Objects.equals("parentSiteKey", additionalAssertFieldName)) {
				if (site.getParentSiteKey() == null) {
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

			if (Objects.equals("typeSettings", additionalAssertFieldName)) {
				if (site.getTypeSettings() == null) {
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
					com.liferay.headless.site.dto.v1_0.Site.class)) {

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

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)site1.getDescription(),
						(Map)site2.getDescription())) {

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

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(site1.getKey(), site2.getKey())) {
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

			if (Objects.equals("parentSiteKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getParentSiteKey(), site2.getParentSiteKey())) {

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

			if (Objects.equals("typeSettings", additionalAssertFieldName)) {
				if (!equals(
						(Map)site1.getTypeSettings(),
						(Map)site2.getTypeSettings())) {

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

		if (entityFieldName.equals("description")) {
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

		if (entityFieldName.equals("manualMembership")) {
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

		if (entityFieldName.equals("parentSiteKey")) {
			Object object = site.getParentSiteKey();

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

		if (entityFieldName.equals("typeSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

	protected Site randomSite() throws Exception {
		return new Site() {
			{
				active = RandomTestUtil.randomBoolean();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				manualMembership = RandomTestUtil.randomBoolean();
				membershipRestriction = RandomTestUtil.randomInt();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				parentSiteExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				parentSiteKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				templateKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
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
	private com.liferay.headless.site.resource.v1_0.SiteResource _siteResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}