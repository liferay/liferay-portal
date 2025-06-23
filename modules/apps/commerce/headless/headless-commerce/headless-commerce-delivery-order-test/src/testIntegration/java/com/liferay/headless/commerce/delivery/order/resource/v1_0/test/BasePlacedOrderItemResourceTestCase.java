/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.order.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderItem;
import com.liferay.headless.commerce.delivery.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.order.client.pagination.Page;
import com.liferay.headless.commerce.delivery.order.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.order.client.resource.v1_0.PlacedOrderItemResource;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.PlacedOrderItemSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
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
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
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
public abstract class BasePlacedOrderItemResourceTestCase {

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

		_placedOrderItemResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		placedOrderItemResource = PlacedOrderItemResource.builder(
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

		PlacedOrderItem placedOrderItem1 = randomPlacedOrderItem();

		String json = objectMapper.writeValueAsString(placedOrderItem1);

		PlacedOrderItem placedOrderItem2 = PlacedOrderItemSerDes.toDTO(json);

		Assert.assertTrue(equals(placedOrderItem1, placedOrderItem2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PlacedOrderItem placedOrderItem = randomPlacedOrderItem();

		String json1 = objectMapper.writeValueAsString(placedOrderItem);
		String json2 = PlacedOrderItemSerDes.toJSON(placedOrderItem);

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

		PlacedOrderItem placedOrderItem = randomPlacedOrderItem();

		placedOrderItem.setAdaptiveMediaImageHTMLTag(regex);
		placedOrderItem.setDeliveryGroup(regex);
		placedOrderItem.setDeliveryGroupName(regex);
		placedOrderItem.setExternalReferenceCode(regex);
		placedOrderItem.setName(regex);
		placedOrderItem.setOptions(regex);
		placedOrderItem.setReplacedSku(regex);
		placedOrderItem.setShippingAddressExternalReferenceCode(regex);
		placedOrderItem.setSku(regex);
		placedOrderItem.setThumbnail(regex);
		placedOrderItem.setUnitOfMeasure(regex);
		placedOrderItem.setUnitOfMeasureKey(regex);

		String json = PlacedOrderItemSerDes.toJSON(placedOrderItem);

		Assert.assertFalse(json.contains(regex));

		placedOrderItem = PlacedOrderItemSerDes.toDTO(json);

		Assert.assertEquals(
			regex, placedOrderItem.getAdaptiveMediaImageHTMLTag());
		Assert.assertEquals(regex, placedOrderItem.getDeliveryGroup());
		Assert.assertEquals(regex, placedOrderItem.getDeliveryGroupName());
		Assert.assertEquals(regex, placedOrderItem.getExternalReferenceCode());
		Assert.assertEquals(regex, placedOrderItem.getName());
		Assert.assertEquals(regex, placedOrderItem.getOptions());
		Assert.assertEquals(regex, placedOrderItem.getReplacedSku());
		Assert.assertEquals(
			regex, placedOrderItem.getShippingAddressExternalReferenceCode());
		Assert.assertEquals(regex, placedOrderItem.getSku());
		Assert.assertEquals(regex, placedOrderItem.getThumbnail());
		Assert.assertEquals(regex, placedOrderItem.getUnitOfMeasure());
		Assert.assertEquals(regex, placedOrderItem.getUnitOfMeasureKey());
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage()
		throws Exception {

		String externalReferenceCode =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getIrrelevantExternalReferenceCode();

		Page<PlacedOrderItem> page =
			placedOrderItemResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PlacedOrderItem irrelevantPlacedOrderItem =
				testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
					irrelevantExternalReferenceCode,
					randomIrrelevantPlacedOrderItem());

			page =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrderItem,
				(List<PlacedOrderItem>)page.getItems());
			assertValid(
				page,
				testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PlacedOrderItem placedOrderItem1 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				externalReferenceCode, randomPlacedOrderItem());

		PlacedOrderItem placedOrderItem2 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				externalReferenceCode, randomPlacedOrderItem());

		page =
			placedOrderItemResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			placedOrderItem1, (List<PlacedOrderItem>)page.getItems());
		assertContains(
			placedOrderItem2, (List<PlacedOrderItem>)page.getItems());
		assertValid(
			page,
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getExternalReferenceCode();

		Page<PlacedOrderItem> placedOrderItemsPage =
			placedOrderItemResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			placedOrderItemsPage.getTotalCount());

		PlacedOrderItem placedOrderItem1 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				externalReferenceCode, randomPlacedOrderItem());

		PlacedOrderItem placedOrderItem2 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				externalReferenceCode, randomPlacedOrderItem());

		PlacedOrderItem placedOrderItem3 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				externalReferenceCode, randomPlacedOrderItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrderItem> page1 =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)page1.getItems());

			Page<PlacedOrderItem> page2 =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)page2.getItems());

			Page<PlacedOrderItem> page3 =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				placedOrderItem3, (List<PlacedOrderItem>)page3.getItems());
		}
		else {
			Page<PlacedOrderItem> page1 =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<PlacedOrderItem> placedOrderItems1 =
				(List<PlacedOrderItem>)page1.getItems();

			Assert.assertEquals(
				placedOrderItems1.toString(), totalCount + 2,
				placedOrderItems1.size());

			Page<PlacedOrderItem> page2 =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrderItem> placedOrderItems2 =
				(List<PlacedOrderItem>)page2.getItems();

			Assert.assertEquals(
				placedOrderItems2.toString(), 1, placedOrderItems2.size());

			Page<PlacedOrderItem> page3 =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)page3.getItems());
			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)page3.getItems());
			assertContains(
				placedOrderItem3, (List<PlacedOrderItem>)page3.getItems());
		}
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSortDateTime()
		throws Exception {

		testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				BeanTestUtil.setProperty(
					placedOrderItem1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSortDouble()
		throws Exception {

		testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				BeanTestUtil.setProperty(
					placedOrderItem1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					placedOrderItem2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSortInteger()
		throws Exception {

		testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				BeanTestUtil.setProperty(
					placedOrderItem1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					placedOrderItem2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSortString()
		throws Exception {

		testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSort(
			EntityField.Type.STRING,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				Class<?> clazz = placedOrderItem1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						placedOrderItem1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						placedOrderItem2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						placedOrderItem1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						placedOrderItem2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						placedOrderItem1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						placedOrderItem2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, PlacedOrderItem, PlacedOrderItem, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getExternalReferenceCode();

		PlacedOrderItem placedOrderItem1 = randomPlacedOrderItem();
		PlacedOrderItem placedOrderItem2 = randomPlacedOrderItem();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, placedOrderItem1, placedOrderItem2);
		}

		placedOrderItem1 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				externalReferenceCode, placedOrderItem1);

		placedOrderItem2 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				externalReferenceCode, placedOrderItem2);

		Page<PlacedOrderItem> page =
			placedOrderItemResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrderItem> ascPage =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)ascPage.getItems());
			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)ascPage.getItems());

			Page<PlacedOrderItem> descPage =
				placedOrderItemResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderItemsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)descPage.getItems());
			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)descPage.getItems());
		}
	}

	protected PlacedOrderItem
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_addPlacedOrderItem(
				String externalReferenceCode, PlacedOrderItem placedOrderItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderItemsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPlacedOrderItem() throws Exception {
		PlacedOrderItem postPlacedOrderItem =
			testGetPlacedOrderItem_addPlacedOrderItem();

		PlacedOrderItem getPlacedOrderItem =
			placedOrderItemResource.getPlacedOrderItem(
				postPlacedOrderItem.getId());

		assertEquals(postPlacedOrderItem, getPlacedOrderItem);
		assertValid(getPlacedOrderItem);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PlacedOrderItem postPlacedOrderItem =
			testGetPlacedOrderItem_addPlacedOrderItem();

		PlacedOrderItem getPlacedOrderItem =
			placedOrderItemResource.getPlacedOrderItem(
				postPlacedOrderItem.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderItem"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postPlacedOrderItem.getId());

		assertEquals(
			getPlacedOrderItem, PlacedOrderItemSerDes.toDTO(item.toString()));
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

	protected PlacedOrderItem testGetPlacedOrderItem_addPlacedOrderItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderItem() throws Exception {
		PlacedOrderItem placedOrderItem =
			testGraphQLGetPlacedOrderItem_addPlacedOrderItem();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderItem,
				PlacedOrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderItem",
								new HashMap<String, Object>() {
									{
										put(
											"placedOrderItemId",
											placedOrderItem.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/placedOrderItem"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderItem,
				PlacedOrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderItem",
									new HashMap<String, Object>() {
										{
											put(
												"placedOrderItemId",
												placedOrderItem.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderItem"))));
	}

	@Test
	public void testGraphQLGetPlacedOrderItemNotFound() throws Exception {
		Long irrelevantPlacedOrderItemId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderItem",
						new HashMap<String, Object>() {
							{
								put(
									"placedOrderItemId",
									irrelevantPlacedOrderItemId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrderItem",
							new HashMap<String, Object>() {
								{
									put(
										"placedOrderItemId",
										irrelevantPlacedOrderItemId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PlacedOrderItem testGraphQLGetPlacedOrderItem_addPlacedOrderItem()
		throws Exception {

		return testGraphQLPlacedOrderItem_addPlacedOrderItem();
	}

	@Test
	public void testGetPlacedOrderItemByExternalReferenceCode()
		throws Exception {

		PlacedOrderItem postPlacedOrderItem =
			testGetPlacedOrderItemByExternalReferenceCode_addPlacedOrderItem();

		PlacedOrderItem getPlacedOrderItem =
			placedOrderItemResource.getPlacedOrderItemByExternalReferenceCode(
				postPlacedOrderItem.getExternalReferenceCode());

		assertEquals(postPlacedOrderItem, getPlacedOrderItem);
		assertValid(getPlacedOrderItem);
	}

	protected PlacedOrderItem
			testGetPlacedOrderItemByExternalReferenceCode_addPlacedOrderItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderItemByExternalReferenceCode()
		throws Exception {

		PlacedOrderItem placedOrderItem =
			testGraphQLGetPlacedOrderItemByExternalReferenceCode_addPlacedOrderItem();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderItem,
				PlacedOrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderItemByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												placedOrderItem.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/placedOrderItemByExternalReferenceCode"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderItem,
				PlacedOrderItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderItemByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													placedOrderItem.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderItemByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPlacedOrderItemByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryOrder_v1_0",
						new GraphQLField(
							"placedOrderItemByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PlacedOrderItem
			testGraphQLGetPlacedOrderItemByExternalReferenceCode_addPlacedOrderItem()
		throws Exception {

		return testGraphQLPlacedOrderItem_addPlacedOrderItem();
	}

	@Test
	public void testGetPlacedOrderPlacedOrderItemsPage() throws Exception {
		Long placedOrderId =
			testGetPlacedOrderPlacedOrderItemsPage_getPlacedOrderId();
		Long irrelevantPlacedOrderId =
			testGetPlacedOrderPlacedOrderItemsPage_getIrrelevantPlacedOrderId();

		Page<PlacedOrderItem> page =
			placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
				placedOrderId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantPlacedOrderId != null) {
			PlacedOrderItem irrelevantPlacedOrderItem =
				testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
					irrelevantPlacedOrderId, randomIrrelevantPlacedOrderItem());

			page = placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
				irrelevantPlacedOrderId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrderItem,
				(List<PlacedOrderItem>)page.getItems());
			assertValid(
				page,
				testGetPlacedOrderPlacedOrderItemsPage_getExpectedActions(
					irrelevantPlacedOrderId));
		}

		PlacedOrderItem placedOrderItem1 =
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				placedOrderId, randomPlacedOrderItem());

		PlacedOrderItem placedOrderItem2 =
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				placedOrderId, randomPlacedOrderItem());

		page = placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
			placedOrderId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			placedOrderItem1, (List<PlacedOrderItem>)page.getItems());
		assertContains(
			placedOrderItem2, (List<PlacedOrderItem>)page.getItems());
		assertValid(
			page,
			testGetPlacedOrderPlacedOrderItemsPage_getExpectedActions(
				placedOrderId));
	}

	protected Map<String, Map<String, String>>
			testGetPlacedOrderPlacedOrderItemsPage_getExpectedActions(
				Long placedOrderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPlacedOrderPlacedOrderItemsPageWithPagination()
		throws Exception {

		Long placedOrderId =
			testGetPlacedOrderPlacedOrderItemsPage_getPlacedOrderId();

		Page<PlacedOrderItem> placedOrderItemsPage =
			placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
				placedOrderId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			placedOrderItemsPage.getTotalCount());

		PlacedOrderItem placedOrderItem1 =
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				placedOrderId, randomPlacedOrderItem());

		PlacedOrderItem placedOrderItem2 =
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				placedOrderId, randomPlacedOrderItem());

		PlacedOrderItem placedOrderItem3 =
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				placedOrderId, randomPlacedOrderItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrderItem> page1 =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)page1.getItems());

			Page<PlacedOrderItem> page2 =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)page2.getItems());

			Page<PlacedOrderItem> page3 =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				placedOrderItem3, (List<PlacedOrderItem>)page3.getItems());
		}
		else {
			Page<PlacedOrderItem> page1 =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<PlacedOrderItem> placedOrderItems1 =
				(List<PlacedOrderItem>)page1.getItems();

			Assert.assertEquals(
				placedOrderItems1.toString(), totalCount + 2,
				placedOrderItems1.size());

			Page<PlacedOrderItem> page2 =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrderItem> placedOrderItems2 =
				(List<PlacedOrderItem>)page2.getItems();

			Assert.assertEquals(
				placedOrderItems2.toString(), 1, placedOrderItems2.size());

			Page<PlacedOrderItem> page3 =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)page3.getItems());
			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)page3.getItems());
			assertContains(
				placedOrderItem3, (List<PlacedOrderItem>)page3.getItems());
		}
	}

	@Test
	public void testGetPlacedOrderPlacedOrderItemsPageWithSortDateTime()
		throws Exception {

		testGetPlacedOrderPlacedOrderItemsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				BeanTestUtil.setProperty(
					placedOrderItem1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPlacedOrderPlacedOrderItemsPageWithSortDouble()
		throws Exception {

		testGetPlacedOrderPlacedOrderItemsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				BeanTestUtil.setProperty(
					placedOrderItem1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					placedOrderItem2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPlacedOrderPlacedOrderItemsPageWithSortInteger()
		throws Exception {

		testGetPlacedOrderPlacedOrderItemsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				BeanTestUtil.setProperty(
					placedOrderItem1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					placedOrderItem2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPlacedOrderPlacedOrderItemsPageWithSortString()
		throws Exception {

		testGetPlacedOrderPlacedOrderItemsPageWithSort(
			EntityField.Type.STRING,
			(entityField, placedOrderItem1, placedOrderItem2) -> {
				Class<?> clazz = placedOrderItem1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						placedOrderItem1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						placedOrderItem2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						placedOrderItem1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						placedOrderItem2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						placedOrderItem1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						placedOrderItem2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPlacedOrderPlacedOrderItemsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PlacedOrderItem, PlacedOrderItem, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long placedOrderId =
			testGetPlacedOrderPlacedOrderItemsPage_getPlacedOrderId();

		PlacedOrderItem placedOrderItem1 = randomPlacedOrderItem();
		PlacedOrderItem placedOrderItem2 = randomPlacedOrderItem();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, placedOrderItem1, placedOrderItem2);
		}

		placedOrderItem1 =
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				placedOrderId, placedOrderItem1);

		placedOrderItem2 =
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				placedOrderId, placedOrderItem2);

		Page<PlacedOrderItem> page =
			placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
				placedOrderId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PlacedOrderItem> ascPage =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)ascPage.getItems());
			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)ascPage.getItems());

			Page<PlacedOrderItem> descPage =
				placedOrderItemResource.getPlacedOrderPlacedOrderItemsPage(
					placedOrderId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				placedOrderItem2, (List<PlacedOrderItem>)descPage.getItems());
			assertContains(
				placedOrderItem1, (List<PlacedOrderItem>)descPage.getItems());
		}
	}

	protected PlacedOrderItem
			testGetPlacedOrderPlacedOrderItemsPage_addPlacedOrderItem(
				Long placedOrderId, PlacedOrderItem placedOrderItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPlacedOrderPlacedOrderItemsPage_getPlacedOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPlacedOrderPlacedOrderItemsPage_getIrrelevantPlacedOrderId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected PlacedOrderItem testGraphQLPlacedOrderItem_addPlacedOrderItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PlacedOrderItem placedOrderItem,
		List<PlacedOrderItem> placedOrderItems) {

		boolean contains = false;

		for (PlacedOrderItem item : placedOrderItems) {
			if (equals(placedOrderItem, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			placedOrderItems + " does not contain " + placedOrderItem,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PlacedOrderItem placedOrderItem1, PlacedOrderItem placedOrderItem2) {

		Assert.assertTrue(
			placedOrderItem1 + " does not equal " + placedOrderItem2,
			equals(placedOrderItem1, placedOrderItem2));
	}

	protected void assertEquals(
		List<PlacedOrderItem> placedOrderItems1,
		List<PlacedOrderItem> placedOrderItems2) {

		Assert.assertEquals(placedOrderItems1.size(), placedOrderItems2.size());

		for (int i = 0; i < placedOrderItems1.size(); i++) {
			PlacedOrderItem placedOrderItem1 = placedOrderItems1.get(i);
			PlacedOrderItem placedOrderItem2 = placedOrderItems2.get(i);

			assertEquals(placedOrderItem1, placedOrderItem2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PlacedOrderItem> placedOrderItems1,
		List<PlacedOrderItem> placedOrderItems2) {

		Assert.assertEquals(placedOrderItems1.size(), placedOrderItems2.size());

		for (PlacedOrderItem placedOrderItem1 : placedOrderItems1) {
			boolean contains = false;

			for (PlacedOrderItem placedOrderItem2 : placedOrderItems2) {
				if (equals(placedOrderItem1, placedOrderItem2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				placedOrderItems2 + " does not contain " + placedOrderItem1,
				contains);
		}
	}

	protected void assertValid(PlacedOrderItem placedOrderItem)
		throws Exception {

		boolean valid = true;

		if (placedOrderItem.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"adaptiveMediaImageHTMLTag", additionalAssertFieldName)) {

				if (placedOrderItem.getAdaptiveMediaImageHTMLTag() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (placedOrderItem.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("deliveryGroup", additionalAssertFieldName)) {
				if (placedOrderItem.getDeliveryGroup() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryGroupName", additionalAssertFieldName)) {

				if (placedOrderItem.getDeliveryGroupName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (placedOrderItem.getErrorMessages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (placedOrderItem.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (placedOrderItem.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("options", additionalAssertFieldName)) {
				if (placedOrderItem.getOptions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentOrderItemId", additionalAssertFieldName)) {

				if (placedOrderItem.getParentOrderItemId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderItemShipments", additionalAssertFieldName)) {

				if (placedOrderItem.getPlacedOrderItemShipments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("placedOrderItems", additionalAssertFieldName)) {
				if (placedOrderItem.getPlacedOrderItems() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (placedOrderItem.getPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (placedOrderItem.getProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productURLs", additionalAssertFieldName)) {
				if (placedOrderItem.getProductURLs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (placedOrderItem.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("replacedSku", additionalAssertFieldName)) {
				if (placedOrderItem.getReplacedSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (placedOrderItem.getRequestedDeliveryDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("settings", additionalAssertFieldName)) {
				if (placedOrderItem.getSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (placedOrderItem.getShippingAddressExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (placedOrderItem.getShippingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (placedOrderItem.getSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (placedOrderItem.getSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscription", additionalAssertFieldName)) {
				if (placedOrderItem.getSubscription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (placedOrderItem.getThumbnail() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasure", additionalAssertFieldName)) {
				if (placedOrderItem.getUnitOfMeasure() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (placedOrderItem.getUnitOfMeasureKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("valid", additionalAssertFieldName)) {
				if (placedOrderItem.getValid() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("virtualItemURLs", additionalAssertFieldName)) {
				if (placedOrderItem.getVirtualItemURLs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("virtualItems", additionalAssertFieldName)) {
				if (placedOrderItem.getVirtualItems() == null) {
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

	protected void assertValid(Page<PlacedOrderItem> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PlacedOrderItem> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PlacedOrderItem> placedOrderItems =
			page.getItems();

		int size = placedOrderItems.size();

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
					com.liferay.headless.commerce.delivery.order.dto.v1_0.
						PlacedOrderItem.class)) {

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
		PlacedOrderItem placedOrderItem1, PlacedOrderItem placedOrderItem2) {

		if (placedOrderItem1 == placedOrderItem2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"adaptiveMediaImageHTMLTag", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.getAdaptiveMediaImageHTMLTag(),
						placedOrderItem2.getAdaptiveMediaImageHTMLTag())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)placedOrderItem1.getCustomFields(),
						(Map)placedOrderItem2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("deliveryGroup", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getDeliveryGroup(),
						placedOrderItem2.getDeliveryGroup())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"deliveryGroupName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.getDeliveryGroupName(),
						placedOrderItem2.getDeliveryGroupName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorMessages", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getErrorMessages(),
						placedOrderItem2.getErrorMessages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.getExternalReferenceCode(),
						placedOrderItem2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getId(), placedOrderItem2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getName(),
						placedOrderItem2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("options", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getOptions(),
						placedOrderItem2.getOptions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentOrderItemId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.getParentOrderItemId(),
						placedOrderItem2.getParentOrderItemId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"placedOrderItemShipments", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.getPlacedOrderItemShipments(),
						placedOrderItem2.getPlacedOrderItemShipments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("placedOrderItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getPlacedOrderItems(),
						placedOrderItem2.getPlacedOrderItems())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getPrice(),
						placedOrderItem2.getPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getProductId(),
						placedOrderItem2.getProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productURLs", additionalAssertFieldName)) {
				if (!equals(
						(Map)placedOrderItem1.getProductURLs(),
						(Map)placedOrderItem2.getProductURLs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getQuantity(),
						placedOrderItem2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("replacedSku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getReplacedSku(),
						placedOrderItem2.getReplacedSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"requestedDeliveryDate", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.getRequestedDeliveryDate(),
						placedOrderItem2.getRequestedDeliveryDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("settings", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getSettings(),
						placedOrderItem2.getSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.
							getShippingAddressExternalReferenceCode(),
						placedOrderItem2.
							getShippingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderItem1.getShippingAddressId(),
						placedOrderItem2.getShippingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getSku(), placedOrderItem2.getSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getSkuId(),
						placedOrderItem2.getSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscription", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getSubscription(),
						placedOrderItem2.getSubscription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("thumbnail", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getThumbnail(),
						placedOrderItem2.getThumbnail())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasure", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getUnitOfMeasure(),
						placedOrderItem2.getUnitOfMeasure())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getUnitOfMeasureKey(),
						placedOrderItem2.getUnitOfMeasureKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("valid", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getValid(),
						placedOrderItem2.getValid())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("virtualItemURLs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getVirtualItemURLs(),
						placedOrderItem2.getVirtualItemURLs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("virtualItems", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderItem1.getVirtualItems(),
						placedOrderItem2.getVirtualItems())) {

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

		if (!(_placedOrderItemResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_placedOrderItemResource;

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
		PlacedOrderItem placedOrderItem) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("adaptiveMediaImageHTMLTag")) {
			Object object = placedOrderItem.getAdaptiveMediaImageHTMLTag();

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("deliveryGroup")) {
			Object object = placedOrderItem.getDeliveryGroup();

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

		if (entityFieldName.equals("deliveryGroupName")) {
			Object object = placedOrderItem.getDeliveryGroupName();

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

		if (entityFieldName.equals("errorMessages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = placedOrderItem.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = placedOrderItem.getName();

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

		if (entityFieldName.equals("options")) {
			Object object = placedOrderItem.getOptions();

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

		if (entityFieldName.equals("parentOrderItemId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("placedOrderItemShipments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("placedOrderItems")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("price")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productURLs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("replacedSku")) {
			Object object = placedOrderItem.getReplacedSku();

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

		if (entityFieldName.equals("requestedDeliveryDate")) {
			if (operator.equals("between")) {
				Date date = placedOrderItem.getRequestedDeliveryDate();

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
					_format.format(placedOrderItem.getRequestedDeliveryDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("settings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingAddressExternalReferenceCode")) {
			Object object =
				placedOrderItem.getShippingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("shippingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sku")) {
			Object object = placedOrderItem.getSku();

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

		if (entityFieldName.equals("skuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscription")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("thumbnail")) {
			Object object = placedOrderItem.getThumbnail();

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

		if (entityFieldName.equals("unitOfMeasure")) {
			Object object = placedOrderItem.getUnitOfMeasure();

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

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = placedOrderItem.getUnitOfMeasureKey();

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

		if (entityFieldName.equals("valid")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("virtualItemURLs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("virtualItems")) {
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

	protected PlacedOrderItem randomPlacedOrderItem() throws Exception {
		return new PlacedOrderItem() {
			{
				adaptiveMediaImageHTMLTag = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				deliveryGroup = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				deliveryGroupName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				options = StringUtil.toLowerCase(RandomTestUtil.randomString());
				parentOrderItemId = RandomTestUtil.randomLong();
				productId = RandomTestUtil.randomLong();
				replacedSku = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				requestedDeliveryDate = RandomTestUtil.nextDate();
				shippingAddressExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingAddressId = RandomTestUtil.randomLong();
				sku = StringUtil.toLowerCase(RandomTestUtil.randomString());
				skuId = RandomTestUtil.randomLong();
				subscription = RandomTestUtil.randomBoolean();
				thumbnail = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unitOfMeasure = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				valid = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected PlacedOrderItem randomIrrelevantPlacedOrderItem()
		throws Exception {

		PlacedOrderItem randomIrrelevantPlacedOrderItem =
			randomPlacedOrderItem();

		return randomIrrelevantPlacedOrderItem;
	}

	protected PlacedOrderItem randomPatchPlacedOrderItem() throws Exception {
		return randomPlacedOrderItem();
	}

	protected PlacedOrderItemResource placedOrderItemResource;
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
		LogFactoryUtil.getLog(BasePlacedOrderItemResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.order.resource.v1_0.
		PlacedOrderItemResource _placedOrderItemResource;

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