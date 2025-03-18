/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.asset.library.client.dto.v1_0.Site;
import com.liferay.headless.asset.library.client.http.HttpInvoker;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.resource.v1_0.SiteResource;
import com.liferay.headless.asset.library.client.serdes.v1_0.SiteSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;
import com.liferay.portal.vulcan.util.TransformUtil;

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

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Roberto Díaz
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
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

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
		site.setLogo(regex);
		site.setName(regex);

		String json = SiteSerDes.toJSON(site);

		Assert.assertFalse(json.contains(regex));

		site = SiteSerDes.toDTO(json);

		Assert.assertEquals(regex, site.getExternalReferenceCode());
		Assert.assertEquals(regex, site.getLogo());
		Assert.assertEquals(regex, site.getName());
	}

	@Test
	public void testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site =
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.
				deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_getSiteExternalReferenceCode()));
	}

	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_getSiteExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Site
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_addSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site =
			testPostAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.
				postAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCodeHttpResponse(
					null, null));

		assertHttpResponseStatusCode(
			404,
			siteResource.
				postAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCodeHttpResponse(
					null, null));
	}

	protected Site
			testPostAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeSiteByExternalReferenceCodeSiteExternalReferenceCode_addSite()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPage()
		throws Exception {

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getIrrelevantExternalReferenceCode();

		Page<Site> page =
			siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
				externalReferenceCode, null, null, null, Pagination.of(1, 10),
				null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Site irrelevantSite =
				testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
					irrelevantExternalReferenceCode, randomIrrelevantSite());

			page = siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
				irrelevantExternalReferenceCode, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantSite, (List<Site>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryByExternalReferenceCodeSitesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Site site1 =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
				externalReferenceCode, randomSite());

		Site site2 =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
				externalReferenceCode, randomSite());

		page = siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
			externalReferenceCode, null, null, null, Pagination.of(1, 10),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(site1, (List<Site>)page.getItems());
		assertContains(site2, (List<Site>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExternalReferenceCode();

		Site site1 = randomSite();

		site1 = testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
			externalReferenceCode, site1);

		for (EntityField entityField : entityFields) {
			Page<Site> page =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null,
					getFilterString(entityField, "between", site1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(site1), (List<Site>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetAssetLibraryByExternalReferenceCodeSitesPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExternalReferenceCode();

		Site site1 =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
				externalReferenceCode, randomSite());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site2 =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
				externalReferenceCode, randomSite());

		for (EntityField entityField : entityFields) {
			Page<Site> page =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null,
					getFilterString(entityField, operator, site1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(site1), (List<Site>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExternalReferenceCode();

		Page<Site> sitePage =
			siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
				externalReferenceCode, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(sitePage.getTotalCount());

		Site site1 =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
				externalReferenceCode, randomSite());

		Site site2 =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
				externalReferenceCode, randomSite());

		Site site3 =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
				externalReferenceCode, randomSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Site> page1 =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(site1, (List<Site>)page1.getItems());

			Page<Site> page2 =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(site2, (List<Site>)page2.getItems());

			Page<Site> page3 =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(site3, (List<Site>)page3.getItems());
		}
		else {
			Page<Site> page1 =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<Site> sites1 = (List<Site>)page1.getItems();

			Assert.assertEquals(
				sites1.toString(), totalCount + 2, sites1.size());

			Page<Site> page2 =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Site> sites2 = (List<Site>)page2.getItems();

			Assert.assertEquals(sites2.toString(), 1, sites2.size());

			Page<Site> page3 =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(site1, (List<Site>)page3.getItems());
			assertContains(site2, (List<Site>)page3.getItems());
			assertContains(site3, (List<Site>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, site1, site2) -> {
				BeanTestUtil.setProperty(
					site1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, site1, site2) -> {
				BeanTestUtil.setProperty(site1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(site2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, site1, site2) -> {
				BeanTestUtil.setProperty(site1, entityField.getName(), 0);
				BeanTestUtil.setProperty(site2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeSitesPageWithSortString()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeSitesPageWithSort(
			EntityField.Type.STRING,
			(entityField, site1, site2) -> {
				Class<?> clazz = site1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						site1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						site2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						site1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						site2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						site1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						site2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryByExternalReferenceCodeSitesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Site, Site, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExternalReferenceCode();

		Site site1 = randomSite();
		Site site2 = randomSite();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, site1, site2);
		}

		site1 = testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
			externalReferenceCode, site1);

		site2 = testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
			externalReferenceCode, site2);

		Page<Site> page =
			siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
				externalReferenceCode, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Site> ascPage =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(site1, (List<Site>)ascPage.getItems());
			assertContains(site2, (List<Site>)ascPage.getItems());

			Page<Site> descPage =
				siteResource.getAssetLibraryByExternalReferenceCodeSitesPage(
					externalReferenceCode, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(site2, (List<Site>)descPage.getItems());
			assertContains(site1, (List<Site>)descPage.getItems());
		}
	}

	protected Site testGetAssetLibraryByExternalReferenceCodeSitesPage_addSite(
			String externalReferenceCode, Site site)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeSitesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAssetLibrarySitesPage() throws Exception {
		Long assetLibraryId = testGetAssetLibrarySitesPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibrarySitesPage_getIrrelevantAssetLibraryId();

		Page<Site> page = siteResource.getAssetLibrarySitesPage(
			assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			Site irrelevantSite = testGetAssetLibrarySitesPage_addSite(
				irrelevantAssetLibraryId, randomIrrelevantSite());

			page = siteResource.getAssetLibrarySitesPage(
				irrelevantAssetLibraryId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantSite, (List<Site>)page.getItems());
			assertValid(
				page,
				testGetAssetLibrarySitesPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		Site site1 = testGetAssetLibrarySitesPage_addSite(
			assetLibraryId, randomSite());

		Site site2 = testGetAssetLibrarySitesPage_addSite(
			assetLibraryId, randomSite());

		page = siteResource.getAssetLibrarySitesPage(
			assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(site1, (List<Site>)page.getItems());
		assertContains(site2, (List<Site>)page.getItems());
		assertValid(
			page,
			testGetAssetLibrarySitesPage_getExpectedActions(assetLibraryId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibrarySitesPage_getExpectedActions(Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibrarySitesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId = testGetAssetLibrarySitesPage_getAssetLibraryId();

		Site site1 = randomSite();

		site1 = testGetAssetLibrarySitesPage_addSite(assetLibraryId, site1);

		for (EntityField entityField : entityFields) {
			Page<Site> page = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null,
				getFilterString(entityField, "between", site1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(site1), (List<Site>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibrarySitesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibrarySitesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibrarySitesPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibrarySitesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibrarySitesPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibrarySitesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibrarySitesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibrarySitesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibrarySitesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId = testGetAssetLibrarySitesPage_getAssetLibraryId();

		Site site1 = testGetAssetLibrarySitesPage_addSite(
			assetLibraryId, randomSite());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site2 = testGetAssetLibrarySitesPage_addSite(
			assetLibraryId, randomSite());

		for (EntityField entityField : entityFields) {
			Page<Site> page = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null,
				getFilterString(entityField, operator, site1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(site1), (List<Site>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibrarySitesPageWithPagination() throws Exception {
		Long assetLibraryId = testGetAssetLibrarySitesPage_getAssetLibraryId();

		Page<Site> sitePage = siteResource.getAssetLibrarySitesPage(
			assetLibraryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(sitePage.getTotalCount());

		Site site1 = testGetAssetLibrarySitesPage_addSite(
			assetLibraryId, randomSite());

		Site site2 = testGetAssetLibrarySitesPage_addSite(
			assetLibraryId, randomSite());

		Site site3 = testGetAssetLibrarySitesPage_addSite(
			assetLibraryId, randomSite());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Site> page1 = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(site1, (List<Site>)page1.getItems());

			Page<Site> page2 = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(site2, (List<Site>)page2.getItems());

			Page<Site> page3 = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(site3, (List<Site>)page3.getItems());
		}
		else {
			Page<Site> page1 = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Site> sites1 = (List<Site>)page1.getItems();

			Assert.assertEquals(
				sites1.toString(), totalCount + 2, sites1.size());

			Page<Site> page2 = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Site> sites2 = (List<Site>)page2.getItems();

			Assert.assertEquals(sites2.toString(), 1, sites2.size());

			Page<Site> page3 = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(site1, (List<Site>)page3.getItems());
			assertContains(site2, (List<Site>)page3.getItems());
			assertContains(site3, (List<Site>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibrarySitesPageWithSortDateTime()
		throws Exception {

		testGetAssetLibrarySitesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, site1, site2) -> {
				BeanTestUtil.setProperty(
					site1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibrarySitesPageWithSortDouble() throws Exception {
		testGetAssetLibrarySitesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, site1, site2) -> {
				BeanTestUtil.setProperty(site1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(site2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibrarySitesPageWithSortInteger() throws Exception {
		testGetAssetLibrarySitesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, site1, site2) -> {
				BeanTestUtil.setProperty(site1, entityField.getName(), 0);
				BeanTestUtil.setProperty(site2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibrarySitesPageWithSortString() throws Exception {
		testGetAssetLibrarySitesPageWithSort(
			EntityField.Type.STRING,
			(entityField, site1, site2) -> {
				Class<?> clazz = site1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						site1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						site2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						site1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						site2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						site1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						site2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibrarySitesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Site, Site, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId = testGetAssetLibrarySitesPage_getAssetLibraryId();

		Site site1 = randomSite();
		Site site2 = randomSite();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, site1, site2);
		}

		site1 = testGetAssetLibrarySitesPage_addSite(assetLibraryId, site1);

		site2 = testGetAssetLibrarySitesPage_addSite(assetLibraryId, site2);

		Page<Site> page = siteResource.getAssetLibrarySitesPage(
			assetLibraryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Site> ascPage = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(site1, (List<Site>)ascPage.getItems());
			assertContains(site2, (List<Site>)ascPage.getItems());

			Page<Site> descPage = siteResource.getAssetLibrarySitesPage(
				assetLibraryId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(site2, (List<Site>)descPage.getItems());
			assertContains(site1, (List<Site>)descPage.getItems());
		}
	}

	protected Site testGetAssetLibrarySitesPage_addSite(
			Long assetLibraryId, Site site)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibrarySitesPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long testGetAssetLibrarySitesPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return null;
	}

	@Test
	public void testDeleteAssetLibrarySite() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testDeleteAssetLibrarySite_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.deleteAssetLibrarySiteHttpResponse(
				testDeleteAssetLibrarySite_getAssetLibraryId(), site.getId()));
	}

	protected Long testDeleteAssetLibrarySite_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Site testDeleteAssetLibrarySite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAssetLibrarySite() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Site site = testPostAssetLibrarySite_addSite();

		assertHttpResponseStatusCode(
			204,
			siteResource.postAssetLibrarySiteHttpResponse(
				testDepotEntry.getDepotEntryId(), site.getId()));

		assertHttpResponseStatusCode(
			404,
			siteResource.postAssetLibrarySiteHttpResponse(
				testDepotEntry.getDepotEntryId(), 0L));
	}

	protected Site testPostAssetLibrarySite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Site testGraphQLSite_addSite() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (site.getExternalReferenceCode() == null) {
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

			if (Objects.equals("searchable", additionalAssertFieldName)) {
				if (site.getSearchable() == null) {
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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.asset.library.dto.v1_0.Site.class)) {

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

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						site1.getExternalReferenceCode(),
						site2.getExternalReferenceCode())) {

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

			if (Objects.equals("logo", additionalAssertFieldName)) {
				if (!Objects.deepEquals(site1.getLogo(), site2.getLogo())) {
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

			if (Objects.equals("searchable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						site1.getSearchable(), site2.getSearchable())) {

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

		if (entityFieldName.equals("id")) {
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

		if (entityFieldName.equals("searchable")) {
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

	protected Site randomSite() throws Exception {
		return new Site() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				logo = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				searchable = RandomTestUtil.randomBoolean();
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

	protected SiteResource siteResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry testDepotEntry;
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
	private com.liferay.headless.asset.library.resource.v1_0.SiteResource
		_siteResource;

}