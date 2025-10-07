/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

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
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.WishListResource;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.WishListSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseWishListResourceTestCase {

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

		_wishListResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		wishListResource = WishListResource.builder(
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

		WishList wishList1 = randomWishList();

		String json = objectMapper.writeValueAsString(wishList1);

		WishList wishList2 = WishListSerDes.toDTO(json);

		Assert.assertTrue(equals(wishList1, wishList2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WishList wishList = randomWishList();

		String json1 = objectMapper.writeValueAsString(wishList);
		String json2 = WishListSerDes.toJSON(wishList);

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

		WishList wishList = randomWishList();

		wishList.setName(regex);

		String json = WishListSerDes.toJSON(wishList);

		Assert.assertFalse(json.contains(regex));

		wishList = WishListSerDes.toDTO(json);

		Assert.assertEquals(regex, wishList.getName());
	}

	@Test
	public void testDeleteWishList() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WishList wishList = testDeleteWishList_addWishList();

		assertHttpResponseStatusCode(
			204, wishListResource.deleteWishListHttpResponse(wishList.getId()));

		assertHttpResponseStatusCode(
			404, wishListResource.getWishListHttpResponse(wishList.getId()));
		assertHttpResponseStatusCode(
			404, wishListResource.getWishListHttpResponse(0L));
	}

	protected WishList testDeleteWishList_addWishList() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWishList() throws Exception {

		// No namespace

		WishList wishList1 = testGraphQLDeleteWishList_addWishList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWishList",
						new HashMap<String, Object>() {
							{
								put("wishListId", wishList1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWishList"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"wishList",
					new HashMap<String, Object>() {
						{
							put("wishListId", wishList1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		WishList wishList2 = testGraphQLDeleteWishList_addWishList();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceDeliveryCatalog_v1_0",
						new GraphQLField(
							"deleteWishList",
							new HashMap<String, Object>() {
								{
									put("wishListId", wishList2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceDeliveryCatalog_v1_0",
				"Object/deleteWishList"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceDeliveryCatalog_v1_0",
					new GraphQLField(
						"wishList",
						new HashMap<String, Object>() {
							{
								put("wishListId", wishList2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WishList testGraphQLDeleteWishList_addWishList()
		throws Exception {

		return testGraphQLWishList_addWishList();
	}

	@Test
	public void testDeleteWishListBatch() throws Exception {
		WishList wishList1 = testDeleteWishListBatch_addWishList();

		testDeleteWishListBatch_deleteWishList(202, null, wishList1.getId());

		assertHttpResponseStatusCode(
			404, wishListResource.getWishListHttpResponse(wishList1.getId()));
	}

	protected WishList testDeleteWishListBatch_addWishList() throws Exception {
		return testDeleteWishList_addWishList();
	}

	protected void testDeleteWishListBatch_deleteWishList(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			wishListResource.deleteWishListBatchHttpResponse(
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
	public void testGetChannelByExternalReferenceCodeWishListsPage()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeWishListsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetChannelByExternalReferenceCodeWishListsPage_getIrrelevantExternalReferenceCode();

		Page<WishList> page =
			wishListResource.getChannelByExternalReferenceCodeWishListsPage(
				externalReferenceCode, null, RandomTestUtil.randomString(),
				Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WishList irrelevantWishList =
				testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
					irrelevantExternalReferenceCode,
					randomIrrelevantWishList());

			page =
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					irrelevantExternalReferenceCode, null, null,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWishList, (List<WishList>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeWishListsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WishList wishList1 =
			testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
				externalReferenceCode, randomWishList());

		WishList wishList2 =
			testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
				externalReferenceCode, randomWishList());

		page = wishListResource.getChannelByExternalReferenceCodeWishListsPage(
			externalReferenceCode, null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(wishList1, (List<WishList>)page.getItems());
		assertContains(wishList2, (List<WishList>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeWishListsPage_getExpectedActions(
				externalReferenceCode));

		wishListResource.deleteWishList(wishList1.getId());

		wishListResource.deleteWishList(wishList2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeWishListsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeWishListsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetChannelByExternalReferenceCodeWishListsPage_getExternalReferenceCode();

		Page<WishList> wishListsPage =
			wishListResource.getChannelByExternalReferenceCodeWishListsPage(
				externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(wishListsPage.getTotalCount());

		WishList wishList1 =
			testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
				externalReferenceCode, randomWishList());

		WishList wishList2 =
			testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
				externalReferenceCode, randomWishList());

		WishList wishList3 =
			testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
				externalReferenceCode, randomWishList());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WishList> page1 =
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					externalReferenceCode, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(wishList1, (List<WishList>)page1.getItems());

			Page<WishList> page2 =
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					externalReferenceCode, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(wishList2, (List<WishList>)page2.getItems());

			Page<WishList> page3 =
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					externalReferenceCode, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(wishList3, (List<WishList>)page3.getItems());
		}
		else {
			Page<WishList> page1 =
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					externalReferenceCode, null, null,
					Pagination.of(1, totalCount + 2));

			List<WishList> wishLists1 = (List<WishList>)page1.getItems();

			Assert.assertEquals(
				wishLists1.toString(), totalCount + 2, wishLists1.size());

			Page<WishList> page2 =
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					externalReferenceCode, null, null,
					Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WishList> wishLists2 = (List<WishList>)page2.getItems();

			Assert.assertEquals(wishLists2.toString(), 1, wishLists2.size());

			Page<WishList> page3 =
				wishListResource.getChannelByExternalReferenceCodeWishListsPage(
					externalReferenceCode, null, null,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(wishList1, (List<WishList>)page3.getItems());
			assertContains(wishList2, (List<WishList>)page3.getItems());
			assertContains(wishList3, (List<WishList>)page3.getItems());
		}
	}

	protected WishList
			testGetChannelByExternalReferenceCodeWishListsPage_addWishList(
				String externalReferenceCode, WishList wishList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeWishListsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeWishListsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelWishListsPage() throws Exception {
		Long channelId = testGetChannelWishListsPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelWishListsPage_getIrrelevantChannelId();

		Page<WishList> page = wishListResource.getChannelWishListsPage(
			channelId, null, RandomTestUtil.randomString(),
			Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantChannelId != null) {
			WishList irrelevantWishList =
				testGetChannelWishListsPage_addWishList(
					irrelevantChannelId, randomIrrelevantWishList());

			page = wishListResource.getChannelWishListsPage(
				irrelevantChannelId, null, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWishList, (List<WishList>)page.getItems());
			assertValid(
				page,
				testGetChannelWishListsPage_getExpectedActions(
					irrelevantChannelId));
		}

		WishList wishList1 = testGetChannelWishListsPage_addWishList(
			channelId, randomWishList());

		WishList wishList2 = testGetChannelWishListsPage_addWishList(
			channelId, randomWishList());

		page = wishListResource.getChannelWishListsPage(
			channelId, null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(wishList1, (List<WishList>)page.getItems());
		assertContains(wishList2, (List<WishList>)page.getItems());
		assertValid(
			page, testGetChannelWishListsPage_getExpectedActions(channelId));

		wishListResource.deleteWishList(wishList1.getId());

		wishListResource.deleteWishList(wishList2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetChannelWishListsPage_getExpectedActions(Long channelId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelWishListsPageWithPagination() throws Exception {
		Long channelId = testGetChannelWishListsPage_getChannelId();

		Page<WishList> wishListsPage = wishListResource.getChannelWishListsPage(
			channelId, null, null, null);

		int totalCount = GetterUtil.getInteger(wishListsPage.getTotalCount());

		WishList wishList1 = testGetChannelWishListsPage_addWishList(
			channelId, randomWishList());

		WishList wishList2 = testGetChannelWishListsPage_addWishList(
			channelId, randomWishList());

		WishList wishList3 = testGetChannelWishListsPage_addWishList(
			channelId, randomWishList());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WishList> page1 = wishListResource.getChannelWishListsPage(
				channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(wishList1, (List<WishList>)page1.getItems());

			Page<WishList> page2 = wishListResource.getChannelWishListsPage(
				channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(wishList2, (List<WishList>)page2.getItems());

			Page<WishList> page3 = wishListResource.getChannelWishListsPage(
				channelId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(wishList3, (List<WishList>)page3.getItems());
		}
		else {
			Page<WishList> page1 = wishListResource.getChannelWishListsPage(
				channelId, null, null, Pagination.of(1, totalCount + 2));

			List<WishList> wishLists1 = (List<WishList>)page1.getItems();

			Assert.assertEquals(
				wishLists1.toString(), totalCount + 2, wishLists1.size());

			Page<WishList> page2 = wishListResource.getChannelWishListsPage(
				channelId, null, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WishList> wishLists2 = (List<WishList>)page2.getItems();

			Assert.assertEquals(wishLists2.toString(), 1, wishLists2.size());

			Page<WishList> page3 = wishListResource.getChannelWishListsPage(
				channelId, null, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(wishList1, (List<WishList>)page3.getItems());
			assertContains(wishList2, (List<WishList>)page3.getItems());
			assertContains(wishList3, (List<WishList>)page3.getItems());
		}
	}

	protected WishList testGetChannelWishListsPage_addWishList(
			Long channelId, WishList wishList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelWishListsPage_getChannelId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelWishListsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWishList() throws Exception {
		WishList postWishList = testGetWishList_addWishList();

		WishList getWishList = wishListResource.getWishList(
			postWishList.getId());

		assertEquals(postWishList, getWishList);
		assertValid(getWishList);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WishList postWishList = testGetWishList_addWishList();

		WishList getWishList = wishListResource.getWishList(
			postWishList.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList"
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

		Object item = vulcanCRUDItemDelegate.getItem(postWishList.getId());

		assertEquals(getWishList, WishListSerDes.toDTO(item.toString()));
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

	protected WishList testGetWishList_addWishList() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWishList() throws Exception {
		WishList wishList = testGraphQLGetWishList_addWishList();

		// No namespace

		Assert.assertTrue(
			equals(
				wishList,
				WishListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"wishList",
								new HashMap<String, Object>() {
									{
										put("wishListId", wishList.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/wishList"))));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertTrue(
			equals(
				wishList,
				WishListSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCatalog_v1_0",
								new GraphQLField(
									"wishList",
									new HashMap<String, Object>() {
										{
											put("wishListId", wishList.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCatalog_v1_0",
						"Object/wishList"))));
	}

	@Test
	public void testGraphQLGetWishListNotFound() throws Exception {
		Long irrelevantWishListId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"wishList",
						new HashMap<String, Object>() {
							{
								put("wishListId", irrelevantWishListId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCatalog_v1_0",
						new GraphQLField(
							"wishList",
							new HashMap<String, Object>() {
								{
									put("wishListId", irrelevantWishListId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WishList testGraphQLGetWishList_addWishList() throws Exception {
		return testGraphQLWishList_addWishList();
	}

	@Test
	public void testPatchWishList() throws Exception {
		WishList postWishList = testPatchWishList_addWishList();

		WishList randomPatchWishList = randomPatchWishList();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WishList patchWishList = wishListResource.patchWishList(
			postWishList.getId(), testPatchWishList_getAccountId(),
			randomPatchWishList);

		WishList expectedPatchWishList = postWishList.clone();

		BeanTestUtil.copyProperties(randomPatchWishList, expectedPatchWishList);

		WishList getWishList = wishListResource.getWishList(
			patchWishList.getId());

		assertEquals(expectedPatchWishList, getWishList);
		assertValid(getWishList);
	}

	protected WishList testPatchWishList_addWishList() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testPatchWishList_getAccountId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelByExternalReferenceCodeWishList()
		throws Exception {

		WishList randomWishList = randomWishList();

		WishList postWishList =
			testPostChannelByExternalReferenceCodeWishList_addWishList(
				randomWishList);

		assertEquals(randomWishList, postWishList);
		assertValid(postWishList);
	}

	protected WishList
			testPostChannelByExternalReferenceCodeWishList_addWishList(
				WishList wishList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelWishList() throws Exception {
		WishList randomWishList = randomWishList();

		WishList postWishList = testPostChannelWishList_addWishList(
			randomWishList);

		assertEquals(randomWishList, postWishList);
		assertValid(postWishList);
	}

	protected WishList testPostChannelWishList_addWishList(WishList wishList)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WishList wishList1 = testBatchEngineDeleteImportTask_addWishList();

		testBatchEngineDeleteImportTask_deleteWishList(
			200, null, wishList1.getId());

		assertHttpResponseStatusCode(
			404, wishListResource.getWishListHttpResponse(wishList1.getId()));
	}

	protected WishList testBatchEngineDeleteImportTask_addWishList()
		throws Exception {

		return testDeleteWishList_addWishList();
	}

	protected void testBatchEngineDeleteImportTask_deleteWishList(
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
				"com.liferay.headless.commerce.delivery.catalog.dto.v1_0.WishList",
				null, null, null, null,
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

	protected WishList testGraphQLWishList_addWishList() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(WishList wishList, List<WishList> wishLists) {
		boolean contains = false;

		for (WishList item : wishLists) {
			if (equals(wishList, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			wishLists + " does not contain " + wishList, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(WishList wishList1, WishList wishList2) {
		Assert.assertTrue(
			wishList1 + " does not equal " + wishList2,
			equals(wishList1, wishList2));
	}

	protected void assertEquals(
		List<WishList> wishLists1, List<WishList> wishLists2) {

		Assert.assertEquals(wishLists1.size(), wishLists2.size());

		for (int i = 0; i < wishLists1.size(); i++) {
			WishList wishList1 = wishLists1.get(i);
			WishList wishList2 = wishLists2.get(i);

			assertEquals(wishList1, wishList2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WishList> wishLists1, List<WishList> wishLists2) {

		Assert.assertEquals(wishLists1.size(), wishLists2.size());

		for (WishList wishList1 : wishLists1) {
			boolean contains = false;

			for (WishList wishList2 : wishLists2) {
				if (equals(wishList1, wishList2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				wishLists2 + " does not contain " + wishList1, contains);
		}
	}

	protected void assertValid(WishList wishList) throws Exception {
		boolean valid = true;

		if (wishList.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("defaultWishList", additionalAssertFieldName)) {
				if (wishList.getDefaultWishList() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (wishList.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("wishListItems", additionalAssertFieldName)) {
				if (wishList.getWishListItems() == null) {
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

	protected void assertValid(Page<WishList> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WishList> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WishList> wishLists = page.getItems();

		int size = wishLists.size();

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
					com.liferay.headless.commerce.delivery.catalog.dto.v1_0.
						WishList.class)) {

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

	protected boolean equals(WishList wishList1, WishList wishList2) {
		if (wishList1 == wishList2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("defaultWishList", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wishList1.getDefaultWishList(),
						wishList2.getDefaultWishList())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(wishList1.getId(), wishList2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wishList1.getName(), wishList2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("wishListItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wishList1.getWishListItems(),
						wishList2.getWishListItems())) {

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

		if (!(_wishListResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_wishListResource;

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
		EntityField entityField, String operator, WishList wishList) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("defaultWishList")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = wishList.getName();

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

		if (entityFieldName.equals("wishListItems")) {
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

	protected WishList randomWishList() throws Exception {
		return new WishList() {
			{
				defaultWishList = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected WishList randomIrrelevantWishList() throws Exception {
		WishList randomIrrelevantWishList = randomWishList();

		return randomIrrelevantWishList;
	}

	protected WishList randomPatchWishList() throws Exception {
		return randomWishList();
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

	protected WishListResource wishListResource;
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
		LogFactoryUtil.getLog(BaseWishListResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.catalog.resource.v1_0.
		WishListResource _wishListResource;

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