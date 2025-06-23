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

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.delivery.client.dto.v1_0.ContentSetElement;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.ContentSetElementResource;
import com.liferay.headless.delivery.client.serdes.v1_0.ContentSetElementSerDes;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseContentSetElementResourceTestCase {

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
			null,
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
			null,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_contentSetElementResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		contentSetElementResource = ContentSetElementResource.builder(
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

		ContentSetElement contentSetElement1 = randomContentSetElement();

		String json = objectMapper.writeValueAsString(contentSetElement1);

		ContentSetElement contentSetElement2 = ContentSetElementSerDes.toDTO(
			json);

		Assert.assertTrue(equals(contentSetElement1, contentSetElement2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ContentSetElement contentSetElement = randomContentSetElement();

		String json1 = objectMapper.writeValueAsString(contentSetElement);
		String json2 = ContentSetElementSerDes.toJSON(contentSetElement);

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

		ContentSetElement contentSetElement = randomContentSetElement();

		contentSetElement.setContentType(regex);
		contentSetElement.setTitle(regex);

		String json = ContentSetElementSerDes.toJSON(contentSetElement);

		Assert.assertFalse(json.contains(regex));

		contentSetElement = ContentSetElementSerDes.toDTO(json);

		Assert.assertEquals(regex, contentSetElement.getContentType());
		Assert.assertEquals(regex, contentSetElement.getTitle());
	}

	@Test
	public void testGetAssetLibraryContentSetByKeyContentSetElementsPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getIrrelevantAssetLibraryId();
		String key =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getKey();
		String irrelevantKey =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getIrrelevantKey();

		Page<ContentSetElement> page =
			contentSetElementResource.
				getAssetLibraryContentSetByKeyContentSetElementsPage(
					assetLibraryId, key, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantAssetLibraryId != null) && (irrelevantKey != null)) {
			ContentSetElement irrelevantContentSetElement =
				testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
					irrelevantAssetLibraryId, irrelevantKey,
					randomIrrelevantContentSetElement());

			page =
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						irrelevantAssetLibraryId, irrelevantKey,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentSetElement,
				(List<ContentSetElement>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryContentSetByKeyContentSetElementsPage_getExpectedActions(
					irrelevantAssetLibraryId, irrelevantKey));
		}

		ContentSetElement contentSetElement1 =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
				assetLibraryId, key, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
				assetLibraryId, key, randomContentSetElement());

		page =
			contentSetElementResource.
				getAssetLibraryContentSetByKeyContentSetElementsPage(
					assetLibraryId, key, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentSetElement1, (List<ContentSetElement>)page.getItems());
		assertContains(
			contentSetElement2, (List<ContentSetElement>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getExpectedActions(
				assetLibraryId, key));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getExpectedActions(
				Long assetLibraryId, String key)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryContentSetByKeyContentSetElementsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getAssetLibraryId();
		String key =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getKey();

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.
				getAssetLibraryContentSetByKeyContentSetElementsPage(
					assetLibraryId, key, null);

		int totalCount = GetterUtil.getInteger(
			contentSetElementsPage.getTotalCount());

		ContentSetElement contentSetElement1 =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
				assetLibraryId, key, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
				assetLibraryId, key, randomContentSetElement());

		ContentSetElement contentSetElement3 =
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
				assetLibraryId, key, randomContentSetElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						assetLibraryId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page1.getItems());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						assetLibraryId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement2, (List<ContentSetElement>)page2.getItems());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						assetLibraryId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
		else {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						assetLibraryId, key, Pagination.of(1, totalCount + 2));

			List<ContentSetElement> contentSetElements1 =
				(List<ContentSetElement>)page1.getItems();

			Assert.assertEquals(
				contentSetElements1.toString(), totalCount + 2,
				contentSetElements1.size());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						assetLibraryId, key, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentSetElement> contentSetElements2 =
				(List<ContentSetElement>)page2.getItems();

			Assert.assertEquals(
				contentSetElements2.toString(), 1, contentSetElements2.size());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						assetLibraryId, key,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement2, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
	}

	protected ContentSetElement
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_addContentSetElement(
				Long assetLibraryId, String key,
				ContentSetElement contentSetElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	protected String
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryContentSetByKeyContentSetElementsPage_getIrrelevantKey()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAssetLibraryContentSetByUuidContentSetElementsPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getIrrelevantAssetLibraryId();
		String uuid =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getUuid();
		String irrelevantUuid =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getIrrelevantUuid();

		Page<ContentSetElement> page =
			contentSetElementResource.
				getAssetLibraryContentSetByUuidContentSetElementsPage(
					assetLibraryId, uuid, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantAssetLibraryId != null) && (irrelevantUuid != null)) {
			ContentSetElement irrelevantContentSetElement =
				testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
					irrelevantAssetLibraryId, irrelevantUuid,
					randomIrrelevantContentSetElement());

			page =
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						irrelevantAssetLibraryId, irrelevantUuid,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentSetElement,
				(List<ContentSetElement>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryContentSetByUuidContentSetElementsPage_getExpectedActions(
					irrelevantAssetLibraryId, irrelevantUuid));
		}

		ContentSetElement contentSetElement1 =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
				assetLibraryId, uuid, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
				assetLibraryId, uuid, randomContentSetElement());

		page =
			contentSetElementResource.
				getAssetLibraryContentSetByUuidContentSetElementsPage(
					assetLibraryId, uuid, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentSetElement1, (List<ContentSetElement>)page.getItems());
		assertContains(
			contentSetElement2, (List<ContentSetElement>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getExpectedActions(
				assetLibraryId, uuid));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getExpectedActions(
				Long assetLibraryId, String uuid)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryContentSetByUuidContentSetElementsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getAssetLibraryId();
		String uuid =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getUuid();

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.
				getAssetLibraryContentSetByUuidContentSetElementsPage(
					assetLibraryId, uuid, null);

		int totalCount = GetterUtil.getInteger(
			contentSetElementsPage.getTotalCount());

		ContentSetElement contentSetElement1 =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
				assetLibraryId, uuid, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
				assetLibraryId, uuid, randomContentSetElement());

		ContentSetElement contentSetElement3 =
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
				assetLibraryId, uuid, randomContentSetElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						assetLibraryId, uuid,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page1.getItems());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						assetLibraryId, uuid,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement2, (List<ContentSetElement>)page2.getItems());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						assetLibraryId, uuid,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
		else {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						assetLibraryId, uuid, Pagination.of(1, totalCount + 2));

			List<ContentSetElement> contentSetElements1 =
				(List<ContentSetElement>)page1.getItems();

			Assert.assertEquals(
				contentSetElements1.toString(), totalCount + 2,
				contentSetElements1.size());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						assetLibraryId, uuid, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentSetElement> contentSetElements2 =
				(List<ContentSetElement>)page2.getItems();

			Assert.assertEquals(
				contentSetElements2.toString(), 1, contentSetElements2.size());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						assetLibraryId, uuid,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement2, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
	}

	protected ContentSetElement
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_addContentSetElement(
				Long assetLibraryId, String uuid,
				ContentSetElement contentSetElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	protected String
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getUuid()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryContentSetByUuidContentSetElementsPage_getIrrelevantUuid()
		throws Exception {

		return null;
	}

	@Test
	public void testGetContentSetContentSetElementsPage() throws Exception {
		Long contentSetId =
			testGetContentSetContentSetElementsPage_getContentSetId();
		Long irrelevantContentSetId =
			testGetContentSetContentSetElementsPage_getIrrelevantContentSetId();

		Page<ContentSetElement> page =
			contentSetElementResource.getContentSetContentSetElementsPage(
				contentSetId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantContentSetId != null) {
			ContentSetElement irrelevantContentSetElement =
				testGetContentSetContentSetElementsPage_addContentSetElement(
					irrelevantContentSetId,
					randomIrrelevantContentSetElement());

			page =
				contentSetElementResource.getContentSetContentSetElementsPage(
					irrelevantContentSetId,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentSetElement,
				(List<ContentSetElement>)page.getItems());
			assertValid(
				page,
				testGetContentSetContentSetElementsPage_getExpectedActions(
					irrelevantContentSetId));
		}

		ContentSetElement contentSetElement1 =
			testGetContentSetContentSetElementsPage_addContentSetElement(
				contentSetId, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetContentSetContentSetElementsPage_addContentSetElement(
				contentSetId, randomContentSetElement());

		page = contentSetElementResource.getContentSetContentSetElementsPage(
			contentSetId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentSetElement1, (List<ContentSetElement>)page.getItems());
		assertContains(
			contentSetElement2, (List<ContentSetElement>)page.getItems());
		assertValid(
			page,
			testGetContentSetContentSetElementsPage_getExpectedActions(
				contentSetId));
	}

	protected Map<String, Map<String, String>>
			testGetContentSetContentSetElementsPage_getExpectedActions(
				Long contentSetId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetContentSetContentSetElementsPageWithPagination()
		throws Exception {

		Long contentSetId =
			testGetContentSetContentSetElementsPage_getContentSetId();

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.getContentSetContentSetElementsPage(
				contentSetId, null);

		int totalCount = GetterUtil.getInteger(
			contentSetElementsPage.getTotalCount());

		ContentSetElement contentSetElement1 =
			testGetContentSetContentSetElementsPage_addContentSetElement(
				contentSetId, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetContentSetContentSetElementsPage_addContentSetElement(
				contentSetId, randomContentSetElement());

		ContentSetElement contentSetElement3 =
			testGetContentSetContentSetElementsPage_addContentSetElement(
				contentSetId, randomContentSetElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentSetElement> page1 =
				contentSetElementResource.getContentSetContentSetElementsPage(
					contentSetId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page1.getItems());

			Page<ContentSetElement> page2 =
				contentSetElementResource.getContentSetContentSetElementsPage(
					contentSetId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				contentSetElement2, (List<ContentSetElement>)page2.getItems());

			Page<ContentSetElement> page3 =
				contentSetElementResource.getContentSetContentSetElementsPage(
					contentSetId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
		else {
			Page<ContentSetElement> page1 =
				contentSetElementResource.getContentSetContentSetElementsPage(
					contentSetId, Pagination.of(1, totalCount + 2));

			List<ContentSetElement> contentSetElements1 =
				(List<ContentSetElement>)page1.getItems();

			Assert.assertEquals(
				contentSetElements1.toString(), totalCount + 2,
				contentSetElements1.size());

			Page<ContentSetElement> page2 =
				contentSetElementResource.getContentSetContentSetElementsPage(
					contentSetId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentSetElement> contentSetElements2 =
				(List<ContentSetElement>)page2.getItems();

			Assert.assertEquals(
				contentSetElements2.toString(), 1, contentSetElements2.size());

			Page<ContentSetElement> page3 =
				contentSetElementResource.getContentSetContentSetElementsPage(
					contentSetId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement2, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
	}

	protected ContentSetElement
			testGetContentSetContentSetElementsPage_addContentSetElement(
				Long contentSetId, ContentSetElement contentSetElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetContentSetContentSetElementsPage_getContentSetId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetContentSetContentSetElementsPage_getIrrelevantContentSetId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteContentSetByKeyContentSetElementsPage()
		throws Exception {

		Long siteId =
			testGetSiteContentSetByKeyContentSetElementsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteContentSetByKeyContentSetElementsPage_getIrrelevantSiteId();
		String key = testGetSiteContentSetByKeyContentSetElementsPage_getKey();
		String irrelevantKey =
			testGetSiteContentSetByKeyContentSetElementsPage_getIrrelevantKey();

		Page<ContentSetElement> page =
			contentSetElementResource.
				getSiteContentSetByKeyContentSetElementsPage(
					siteId, key, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteId != null) && (irrelevantKey != null)) {
			ContentSetElement irrelevantContentSetElement =
				testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
					irrelevantSiteId, irrelevantKey,
					randomIrrelevantContentSetElement());

			page =
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						irrelevantSiteId, irrelevantKey,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentSetElement,
				(List<ContentSetElement>)page.getItems());
			assertValid(
				page,
				testGetSiteContentSetByKeyContentSetElementsPage_getExpectedActions(
					irrelevantSiteId, irrelevantKey));
		}

		ContentSetElement contentSetElement1 =
			testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		page =
			contentSetElementResource.
				getSiteContentSetByKeyContentSetElementsPage(
					siteId, key, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentSetElement1, (List<ContentSetElement>)page.getItems());
		assertContains(
			contentSetElement2, (List<ContentSetElement>)page.getItems());
		assertValid(
			page,
			testGetSiteContentSetByKeyContentSetElementsPage_getExpectedActions(
				siteId, key));
	}

	protected Map<String, Map<String, String>>
			testGetSiteContentSetByKeyContentSetElementsPage_getExpectedActions(
				Long siteId, String key)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteContentSetByKeyContentSetElementsPageWithPagination()
		throws Exception {

		Long siteId =
			testGetSiteContentSetByKeyContentSetElementsPage_getSiteId();
		String key = testGetSiteContentSetByKeyContentSetElementsPage_getKey();

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.
				getSiteContentSetByKeyContentSetElementsPage(siteId, key, null);

		int totalCount = GetterUtil.getInteger(
			contentSetElementsPage.getTotalCount());

		ContentSetElement contentSetElement1 =
			testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		ContentSetElement contentSetElement3 =
			testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						siteId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page1.getItems());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						siteId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement2, (List<ContentSetElement>)page2.getItems());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						siteId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
		else {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						siteId, key, Pagination.of(1, totalCount + 2));

			List<ContentSetElement> contentSetElements1 =
				(List<ContentSetElement>)page1.getItems();

			Assert.assertEquals(
				contentSetElements1.toString(), totalCount + 2,
				contentSetElements1.size());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						siteId, key, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentSetElement> contentSetElements2 =
				(List<ContentSetElement>)page2.getItems();

			Assert.assertEquals(
				contentSetElements2.toString(), 1, contentSetElements2.size());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						siteId, key, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement2, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
	}

	protected ContentSetElement
			testGetSiteContentSetByKeyContentSetElementsPage_addContentSetElement(
				Long siteId, String key, ContentSetElement contentSetElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteContentSetByKeyContentSetElementsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long
			testGetSiteContentSetByKeyContentSetElementsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	protected String testGetSiteContentSetByKeyContentSetElementsPage_getKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteContentSetByKeyContentSetElementsPage_getIrrelevantKey()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteContentSetByUuidContentSetElementsPage()
		throws Exception {

		Long siteId =
			testGetSiteContentSetByUuidContentSetElementsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteContentSetByUuidContentSetElementsPage_getIrrelevantSiteId();
		String uuid =
			testGetSiteContentSetByUuidContentSetElementsPage_getUuid();
		String irrelevantUuid =
			testGetSiteContentSetByUuidContentSetElementsPage_getIrrelevantUuid();

		Page<ContentSetElement> page =
			contentSetElementResource.
				getSiteContentSetByUuidContentSetElementsPage(
					siteId, uuid, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteId != null) && (irrelevantUuid != null)) {
			ContentSetElement irrelevantContentSetElement =
				testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
					irrelevantSiteId, irrelevantUuid,
					randomIrrelevantContentSetElement());

			page =
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						irrelevantSiteId, irrelevantUuid,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentSetElement,
				(List<ContentSetElement>)page.getItems());
			assertValid(
				page,
				testGetSiteContentSetByUuidContentSetElementsPage_getExpectedActions(
					irrelevantSiteId, irrelevantUuid));
		}

		ContentSetElement contentSetElement1 =
			testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
				siteId, uuid, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
				siteId, uuid, randomContentSetElement());

		page =
			contentSetElementResource.
				getSiteContentSetByUuidContentSetElementsPage(
					siteId, uuid, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentSetElement1, (List<ContentSetElement>)page.getItems());
		assertContains(
			contentSetElement2, (List<ContentSetElement>)page.getItems());
		assertValid(
			page,
			testGetSiteContentSetByUuidContentSetElementsPage_getExpectedActions(
				siteId, uuid));
	}

	protected Map<String, Map<String, String>>
			testGetSiteContentSetByUuidContentSetElementsPage_getExpectedActions(
				Long siteId, String uuid)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteContentSetByUuidContentSetElementsPageWithPagination()
		throws Exception {

		Long siteId =
			testGetSiteContentSetByUuidContentSetElementsPage_getSiteId();
		String uuid =
			testGetSiteContentSetByUuidContentSetElementsPage_getUuid();

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.
				getSiteContentSetByUuidContentSetElementsPage(
					siteId, uuid, null);

		int totalCount = GetterUtil.getInteger(
			contentSetElementsPage.getTotalCount());

		ContentSetElement contentSetElement1 =
			testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
				siteId, uuid, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
				siteId, uuid, randomContentSetElement());

		ContentSetElement contentSetElement3 =
			testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
				siteId, uuid, randomContentSetElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						siteId, uuid,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page1.getItems());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						siteId, uuid,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement2, (List<ContentSetElement>)page2.getItems());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						siteId, uuid,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
		else {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						siteId, uuid, Pagination.of(1, totalCount + 2));

			List<ContentSetElement> contentSetElements1 =
				(List<ContentSetElement>)page1.getItems();

			Assert.assertEquals(
				contentSetElements1.toString(), totalCount + 2,
				contentSetElements1.size());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						siteId, uuid, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentSetElement> contentSetElements2 =
				(List<ContentSetElement>)page2.getItems();

			Assert.assertEquals(
				contentSetElements2.toString(), 1, contentSetElements2.size());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						siteId, uuid, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement2, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
	}

	protected ContentSetElement
			testGetSiteContentSetByUuidContentSetElementsPage_addContentSetElement(
				Long siteId, String uuid, ContentSetElement contentSetElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteContentSetByUuidContentSetElementsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long
			testGetSiteContentSetByUuidContentSetElementsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	protected String testGetSiteContentSetByUuidContentSetElementsPage_getUuid()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteContentSetByUuidContentSetElementsPage_getIrrelevantUuid()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteContentSetProviderByKeyContentSetElementsPage()
		throws Exception {

		Long siteId =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getIrrelevantSiteId();
		String key =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getKey();
		String irrelevantKey =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getIrrelevantKey();

		Page<ContentSetElement> page =
			contentSetElementResource.
				getSiteContentSetProviderByKeyContentSetElementsPage(
					siteId, key, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantSiteId != null) && (irrelevantKey != null)) {
			ContentSetElement irrelevantContentSetElement =
				testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
					irrelevantSiteId, irrelevantKey,
					randomIrrelevantContentSetElement());

			page =
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						irrelevantSiteId, irrelevantKey,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantContentSetElement,
				(List<ContentSetElement>)page.getItems());
			assertValid(
				page,
				testGetSiteContentSetProviderByKeyContentSetElementsPage_getExpectedActions(
					irrelevantSiteId, irrelevantKey));
		}

		ContentSetElement contentSetElement1 =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		page =
			contentSetElementResource.
				getSiteContentSetProviderByKeyContentSetElementsPage(
					siteId, key, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			contentSetElement1, (List<ContentSetElement>)page.getItems());
		assertContains(
			contentSetElement2, (List<ContentSetElement>)page.getItems());
		assertValid(
			page,
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getExpectedActions(
				siteId, key));
	}

	protected Map<String, Map<String, String>>
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getExpectedActions(
				Long siteId, String key)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteContentSetProviderByKeyContentSetElementsPageWithPagination()
		throws Exception {

		Long siteId =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getSiteId();
		String key =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getKey();

		Page<ContentSetElement> contentSetElementsPage =
			contentSetElementResource.
				getSiteContentSetProviderByKeyContentSetElementsPage(
					siteId, key, null);

		int totalCount = GetterUtil.getInteger(
			contentSetElementsPage.getTotalCount());

		ContentSetElement contentSetElement1 =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		ContentSetElement contentSetElement2 =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		ContentSetElement contentSetElement3 =
			testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
				siteId, key, randomContentSetElement());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						siteId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page1.getItems());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						siteId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement2, (List<ContentSetElement>)page2.getItems());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						siteId, key,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
		else {
			Page<ContentSetElement> page1 =
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						siteId, key, Pagination.of(1, totalCount + 2));

			List<ContentSetElement> contentSetElements1 =
				(List<ContentSetElement>)page1.getItems();

			Assert.assertEquals(
				contentSetElements1.toString(), totalCount + 2,
				contentSetElements1.size());

			Page<ContentSetElement> page2 =
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						siteId, key, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ContentSetElement> contentSetElements2 =
				(List<ContentSetElement>)page2.getItems();

			Assert.assertEquals(
				contentSetElements2.toString(), 1, contentSetElements2.size());

			Page<ContentSetElement> page3 =
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						siteId, key, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				contentSetElement1, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement2, (List<ContentSetElement>)page3.getItems());
			assertContains(
				contentSetElement3, (List<ContentSetElement>)page3.getItems());
		}
	}

	protected ContentSetElement
			testGetSiteContentSetProviderByKeyContentSetElementsPage_addContentSetElement(
				Long siteId, String key, ContentSetElement contentSetElement)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	protected String
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getKey()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetSiteContentSetProviderByKeyContentSetElementsPage_getIrrelevantKey()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		ContentSetElement contentSetElement,
		List<ContentSetElement> contentSetElements) {

		boolean contains = false;

		for (ContentSetElement item : contentSetElements) {
			if (equals(contentSetElement, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			contentSetElements + " does not contain " + contentSetElement,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ContentSetElement contentSetElement1,
		ContentSetElement contentSetElement2) {

		Assert.assertTrue(
			contentSetElement1 + " does not equal " + contentSetElement2,
			equals(contentSetElement1, contentSetElement2));
	}

	protected void assertEquals(
		List<ContentSetElement> contentSetElements1,
		List<ContentSetElement> contentSetElements2) {

		Assert.assertEquals(
			contentSetElements1.size(), contentSetElements2.size());

		for (int i = 0; i < contentSetElements1.size(); i++) {
			ContentSetElement contentSetElement1 = contentSetElements1.get(i);
			ContentSetElement contentSetElement2 = contentSetElements2.get(i);

			assertEquals(contentSetElement1, contentSetElement2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ContentSetElement> contentSetElements1,
		List<ContentSetElement> contentSetElements2) {

		Assert.assertEquals(
			contentSetElements1.size(), contentSetElements2.size());

		for (ContentSetElement contentSetElement1 : contentSetElements1) {
			boolean contains = false;

			for (ContentSetElement contentSetElement2 : contentSetElements2) {
				if (equals(contentSetElement1, contentSetElement2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				contentSetElements2 + " does not contain " + contentSetElement1,
				contains);
		}
	}

	protected void assertValid(ContentSetElement contentSetElement)
		throws Exception {

		boolean valid = true;

		if (contentSetElement.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (contentSetElement.getContent() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (contentSetElement.getContentType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (contentSetElement.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (contentSetElement.getTitle_i18n() == null) {
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

	protected void assertValid(Page<ContentSetElement> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ContentSetElement> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ContentSetElement> contentSetElements =
			page.getItems();

		int size = contentSetElements.size();

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
					com.liferay.headless.delivery.dto.v1_0.ContentSetElement.
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
		ContentSetElement contentSetElement1,
		ContentSetElement contentSetElement2) {

		if (contentSetElement1 == contentSetElement2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentSetElement1.getContent(),
						contentSetElement2.getContent())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentSetElement1.getContentType(),
						contentSetElement2.getContentType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentSetElement1.getId(),
						contentSetElement2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						contentSetElement1.getTitle(),
						contentSetElement2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)contentSetElement1.getTitle_i18n(),
						(Map)contentSetElement2.getTitle_i18n())) {

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

		if (!(_contentSetElementResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_contentSetElementResource;

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
		ContentSetElement contentSetElement) {

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
			Object object = contentSetElement.getContentType();

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
			Object object = contentSetElement.getTitle();

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

	protected ContentSetElement randomContentSetElement() throws Exception {
		return new ContentSetElement() {
			{
				contentType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected ContentSetElement randomIrrelevantContentSetElement()
		throws Exception {

		ContentSetElement randomIrrelevantContentSetElement =
			randomContentSetElement();

		return randomIrrelevantContentSetElement;
	}

	protected ContentSetElement randomPatchContentSetElement()
		throws Exception {

		return randomContentSetElement();
	}

	protected ContentSetElementResource contentSetElementResource;
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
		LogFactoryUtil.getLog(BaseContentSetElementResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.delivery.resource.v1_0.ContentSetElementResource
			_contentSetElementResource;

}