/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.delivery.client.dto.v1_0.ContentElement;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.ContentElementResource;
import com.liferay.headless.delivery.client.serdes.v1_0.ContentElementSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseContentElementResourceTestCase {

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

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_contentElementResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		contentElementResource = ContentElementResource.builder(
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

		ContentElement contentElement1 = randomContentElement();

		String json = objectMapper.writeValueAsString(contentElement1);

		ContentElement contentElement2 = ContentElementSerDes.toDTO(json);

		Assert.assertTrue(equals(contentElement1, contentElement2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ContentElement contentElement = randomContentElement();

		String json1 = objectMapper.writeValueAsString(contentElement);
		String json2 = ContentElementSerDes.toJSON(contentElement);

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

		ContentElement contentElement = randomContentElement();

		contentElement.setContentType(regex);
		contentElement.setTitle(regex);

		String json = ContentElementSerDes.toJSON(contentElement);

		Assert.assertFalse(json.contains(regex));

		contentElement = ContentElementSerDes.toDTO(json);

		Assert.assertEquals(regex, contentElement.getContentType());
		Assert.assertEquals(regex, contentElement.getTitle());
	}

	@Test
	public void testGetAssetLibraryContentElementsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryContentElementsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryContentElementsPage_getIrrelevantAssetLibraryId();

		Page<ContentElement> page =
			contentElementResource.getAssetLibraryContentElementsPage(
				assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			ContentElement irrelevantContentElement =
				testGetAssetLibraryContentElementsPage_addContentElement(
					irrelevantAssetLibraryId, randomIrrelevantContentElement());

			page = contentElementResource.getAssetLibraryContentElementsPage(
				irrelevantAssetLibraryId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentElement,
				(List<ContentElement>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryContentElementsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		ContentElement contentElement1 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, randomContentElement());

		ContentElement contentElement2 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, randomContentElement());

		page = contentElementResource.getAssetLibraryContentElementsPage(
			assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(contentElement1, (List<ContentElement>)page.getItems());
		assertContains(contentElement2, (List<ContentElement>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryContentElementsPage_getExpectedActions(
				assetLibraryId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryContentElementsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentElementsPage_getAssetLibraryId();

		ContentElement contentElement1 = randomContentElement();

		contentElement1 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, contentElement1);

		for (EntityField entityField : entityFields) {
			Page<ContentElement> page =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null,
					getFilterString(entityField, "between", contentElement1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentElement1),
				(List<ContentElement>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryContentElementsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentElementsPage_getAssetLibraryId();

		ContentElement contentElement1 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, randomContentElement());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentElement contentElement2 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, randomContentElement());

		for (EntityField entityField : entityFields) {
			Page<ContentElement> page =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null,
					getFilterString(entityField, operator, contentElement1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentElement1),
				(List<ContentElement>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryContentElementsPage_getAssetLibraryId();

		Page<ContentElement> contentElementsPage =
			contentElementResource.getAssetLibraryContentElementsPage(
				assetLibraryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			contentElementsPage.getTotalCount());

		ContentElement contentElement1 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, randomContentElement());

		ContentElement contentElement2 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, randomContentElement());

		ContentElement contentElement3 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, randomContentElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentElement> page1 =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentElement1, (List<ContentElement>)page1.getItems());

			Page<ContentElement> page2 =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentElement2, (List<ContentElement>)page2.getItems());

			Page<ContentElement> page3 =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentElement3, (List<ContentElement>)page3.getItems());
		}
		else {
			Page<ContentElement> page1 =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ContentElement> contentElements1 =
				(List<ContentElement>)page1.getItems();

			Assert.assertEquals(
				contentElements1.toString(), totalCount + 2,
				contentElements1.size());

			Page<ContentElement> page2 =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentElement> contentElements2 =
				(List<ContentElement>)page2.getItems();

			Assert.assertEquals(
				contentElements2.toString(), 1, contentElements2.size());

			Page<ContentElement> page3 =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				contentElement1, (List<ContentElement>)page3.getItems());
			assertContains(
				contentElement2, (List<ContentElement>)page3.getItems());
			assertContains(
				contentElement3, (List<ContentElement>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, contentElement1, contentElement2) -> {
				BeanTestUtil.setProperty(
					contentElement1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, contentElement1, contentElement2) -> {
				BeanTestUtil.setProperty(
					contentElement1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					contentElement2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentElement1, contentElement2) -> {
				BeanTestUtil.setProperty(
					contentElement1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					contentElement2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryContentElementsPageWithSortString()
		throws Exception {

		testGetAssetLibraryContentElementsPageWithSort(
			EntityField.Type.STRING,
			(entityField, contentElement1, contentElement2) -> {
				Class<?> clazz = contentElement1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						contentElement1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						contentElement2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						contentElement1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						contentElement2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						contentElement1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						contentElement2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryContentElementsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ContentElement, ContentElement, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryContentElementsPage_getAssetLibraryId();

		ContentElement contentElement1 = randomContentElement();
		ContentElement contentElement2 = randomContentElement();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, contentElement1, contentElement2);
		}

		contentElement1 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, contentElement1);

		contentElement2 =
			testGetAssetLibraryContentElementsPage_addContentElement(
				assetLibraryId, contentElement2);

		Page<ContentElement> page =
			contentElementResource.getAssetLibraryContentElementsPage(
				assetLibraryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ContentElement> ascPage =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				contentElement1, (List<ContentElement>)ascPage.getItems());
			assertContains(
				contentElement2, (List<ContentElement>)ascPage.getItems());

			Page<ContentElement> descPage =
				contentElementResource.getAssetLibraryContentElementsPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				contentElement2, (List<ContentElement>)descPage.getItems());
			assertContains(
				contentElement1, (List<ContentElement>)descPage.getItems());
		}
	}

	protected ContentElement
			testGetAssetLibraryContentElementsPage_addContentElement(
				Long assetLibraryId, ContentElement contentElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibraryContentElementsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryContentElementsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGetSiteContentElementsPage() throws Exception {
		Long siteId = testGetSiteContentElementsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteContentElementsPage_getIrrelevantSiteId();

		Page<ContentElement> page =
			contentElementResource.getSiteContentElementsPage(
				siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			ContentElement irrelevantContentElement =
				testGetSiteContentElementsPage_addContentElement(
					irrelevantSiteId, randomIrrelevantContentElement());

			page = contentElementResource.getSiteContentElementsPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentElement,
				(List<ContentElement>)page.getItems());
			assertValid(
				page,
				testGetSiteContentElementsPage_getExpectedActions(
					irrelevantSiteId));
		}

		ContentElement contentElement1 =
			testGetSiteContentElementsPage_addContentElement(
				siteId, randomContentElement());

		ContentElement contentElement2 =
			testGetSiteContentElementsPage_addContentElement(
				siteId, randomContentElement());

		page = contentElementResource.getSiteContentElementsPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(contentElement1, (List<ContentElement>)page.getItems());
		assertContains(contentElement2, (List<ContentElement>)page.getItems());
		assertValid(
			page, testGetSiteContentElementsPage_getExpectedActions(siteId));
	}

	protected Map<String, Map<String, String>>
			testGetSiteContentElementsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteContentElementsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentElementsPage_getSiteId();

		ContentElement contentElement1 = randomContentElement();

		contentElement1 = testGetSiteContentElementsPage_addContentElement(
			siteId, contentElement1);

		for (EntityField entityField : entityFields) {
			Page<ContentElement> page =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null,
					getFilterString(entityField, "between", contentElement1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentElement1),
				(List<ContentElement>)page.getItems());
		}
	}

	@Test
	public void testGetSiteContentElementsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteContentElementsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteContentElementsPageWithFilterStringContains()
		throws Exception {

		testGetSiteContentElementsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteContentElementsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteContentElementsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteContentElementsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteContentElementsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteContentElementsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentElementsPage_getSiteId();

		ContentElement contentElement1 =
			testGetSiteContentElementsPage_addContentElement(
				siteId, randomContentElement());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ContentElement contentElement2 =
			testGetSiteContentElementsPage_addContentElement(
				siteId, randomContentElement());

		for (EntityField entityField : entityFields) {
			Page<ContentElement> page =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null,
					getFilterString(entityField, operator, contentElement1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(contentElement1),
				(List<ContentElement>)page.getItems());
		}
	}

	@Test
	public void testGetSiteContentElementsPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteContentElementsPage_getSiteId();

		Page<ContentElement> contentElementsPage =
			contentElementResource.getSiteContentElementsPage(
				siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			contentElementsPage.getTotalCount());

		ContentElement contentElement1 =
			testGetSiteContentElementsPage_addContentElement(
				siteId, randomContentElement());

		ContentElement contentElement2 =
			testGetSiteContentElementsPage_addContentElement(
				siteId, randomContentElement());

		ContentElement contentElement3 =
			testGetSiteContentElementsPage_addContentElement(
				siteId, randomContentElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentElement> page1 =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentElement1, (List<ContentElement>)page1.getItems());

			Page<ContentElement> page2 =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentElement2, (List<ContentElement>)page2.getItems());

			Page<ContentElement> page3 =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				contentElement3, (List<ContentElement>)page3.getItems());
		}
		else {
			Page<ContentElement> page1 =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<ContentElement> contentElements1 =
				(List<ContentElement>)page1.getItems();

			Assert.assertEquals(
				contentElements1.toString(), totalCount + 2,
				contentElements1.size());

			Page<ContentElement> page2 =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentElement> contentElements2 =
				(List<ContentElement>)page2.getItems();

			Assert.assertEquals(
				contentElements2.toString(), 1, contentElements2.size());

			Page<ContentElement> page3 =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				contentElement1, (List<ContentElement>)page3.getItems());
			assertContains(
				contentElement2, (List<ContentElement>)page3.getItems());
			assertContains(
				contentElement3, (List<ContentElement>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteContentElementsPageWithSortDateTime()
		throws Exception {

		testGetSiteContentElementsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, contentElement1, contentElement2) -> {
				BeanTestUtil.setProperty(
					contentElement1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteContentElementsPageWithSortDouble()
		throws Exception {

		testGetSiteContentElementsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, contentElement1, contentElement2) -> {
				BeanTestUtil.setProperty(
					contentElement1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					contentElement2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteContentElementsPageWithSortInteger()
		throws Exception {

		testGetSiteContentElementsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, contentElement1, contentElement2) -> {
				BeanTestUtil.setProperty(
					contentElement1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					contentElement2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteContentElementsPageWithSortString()
		throws Exception {

		testGetSiteContentElementsPageWithSort(
			EntityField.Type.STRING,
			(entityField, contentElement1, contentElement2) -> {
				Class<?> clazz = contentElement1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						contentElement1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						contentElement2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						contentElement1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						contentElement2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						contentElement1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						contentElement2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteContentElementsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ContentElement, ContentElement, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteContentElementsPage_getSiteId();

		ContentElement contentElement1 = randomContentElement();
		ContentElement contentElement2 = randomContentElement();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, contentElement1, contentElement2);
		}

		contentElement1 = testGetSiteContentElementsPage_addContentElement(
			siteId, contentElement1);

		contentElement2 = testGetSiteContentElementsPage_addContentElement(
			siteId, contentElement2);

		Page<ContentElement> page =
			contentElementResource.getSiteContentElementsPage(
				siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ContentElement> ascPage =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				contentElement1, (List<ContentElement>)ascPage.getItems());
			assertContains(
				contentElement2, (List<ContentElement>)ascPage.getItems());

			Page<ContentElement> descPage =
				contentElementResource.getSiteContentElementsPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				contentElement2, (List<ContentElement>)descPage.getItems());
			assertContains(
				contentElement1, (List<ContentElement>)descPage.getItems());
		}
	}

	protected ContentElement testGetSiteContentElementsPage_addContentElement(
			Long siteId, ContentElement contentElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteContentElementsPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteContentElementsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		ContentElement contentElement, List<ContentElement> contentElements) {

		boolean contains = false;

		for (ContentElement item : contentElements) {
			if (equals(contentElement, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			contentElements + " does not contain " + contentElement, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ContentElement contentElement1, ContentElement contentElement2) {

		Assert.assertTrue(
			contentElement1 + " does not equal " + contentElement2,
			equals(contentElement1, contentElement2));
	}

	protected void assertEquals(
		List<ContentElement> contentElements1,
		List<ContentElement> contentElements2) {

		Assert.assertEquals(contentElements1.size(), contentElements2.size());

		for (int i = 0; i < contentElements1.size(); i++) {
			ContentElement contentElement1 = contentElements1.get(i);
			ContentElement contentElement2 = contentElements2.get(i);

			assertEquals(contentElement1, contentElement2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ContentElement> contentElements1,
		List<ContentElement> contentElements2) {

		Assert.assertEquals(contentElements1.size(), contentElements2.size());

		for (ContentElement contentElement1 : contentElements1) {
			boolean contains = false;

			for (ContentElement contentElement2 : contentElements2) {
				if (equals(contentElement1, contentElement2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				contentElements2 + " does not contain " + contentElement1,
				contains);
		}
	}

	protected void assertValid(ContentElement contentElement) throws Exception {
		boolean valid = true;

		if (contentElement.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (contentElement.getContent() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (contentElement.getContentType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (contentElement.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (contentElement.getTitle_i18n() == null) {
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

	protected void assertValid(Page<ContentElement> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ContentElement> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ContentElement> contentElements = page.getItems();

		int size = contentElements.size();

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
					com.liferay.headless.delivery.dto.v1_0.ContentElement.
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
		ContentElement contentElement1, ContentElement contentElement2) {

		if (contentElement1 == contentElement2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentElement1.getContent(),
						contentElement2.getContent())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentElement1.getContentType(),
						contentElement2.getContentType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentElement1.getId(), contentElement2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentElement1.getTitle(),
						contentElement2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)contentElement1.getTitle_i18n(),
						(Map)contentElement2.getTitle_i18n())) {

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

		if (!(_contentElementResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_contentElementResource;

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
		ContentElement contentElement) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("content")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("contentType")) {
			Object object = contentElement.getContentType();

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

		if (entityFieldName.equals("title")) {
			Object object = contentElement.getTitle();

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

		if (entityFieldName.equals("title_i18n")) {
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

	protected ContentElement randomContentElement() throws Exception {
		return new ContentElement() {
			{
				contentType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ContentElement randomIrrelevantContentElement() throws Exception {
		ContentElement randomIrrelevantContentElement = randomContentElement();

		return randomIrrelevantContentElement;
	}

	protected ContentElement randomPatchContentElement() throws Exception {
		return randomContentElement();
	}

	protected ContentElementResource contentElementResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
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
		LogFactoryUtil.getLog(BaseContentElementResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.ContentElementResource
		_contentElementResource;

}