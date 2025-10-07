/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.ReplenishmentItem;
import com.liferay.headless.commerce.admin.inventory.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.inventory.client.resource.v1_0.ReplenishmentItemResource;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.ReplenishmentItemSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
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
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
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
import java.util.TimeZone;

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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseReplenishmentItemResourceTestCase {

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

		_replenishmentItemResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		replenishmentItemResource = ReplenishmentItemResource.builder(
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

		ReplenishmentItem replenishmentItem1 = randomReplenishmentItem();

		String json = objectMapper.writeValueAsString(replenishmentItem1);

		ReplenishmentItem replenishmentItem2 = ReplenishmentItemSerDes.toDTO(
			json);

		Assert.assertTrue(equals(replenishmentItem1, replenishmentItem2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ReplenishmentItem replenishmentItem = randomReplenishmentItem();

		String json1 = objectMapper.writeValueAsString(replenishmentItem);
		String json2 = ReplenishmentItemSerDes.toJSON(replenishmentItem);

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

		ReplenishmentItem replenishmentItem = randomReplenishmentItem();

		replenishmentItem.setExternalReferenceCode(regex);
		replenishmentItem.setSku(regex);
		replenishmentItem.setUnitOfMeasureKey(regex);

		String json = ReplenishmentItemSerDes.toJSON(replenishmentItem);

		Assert.assertFalse(json.contains(regex));

		replenishmentItem = ReplenishmentItemSerDes.toDTO(json);

		Assert.assertEquals(
			regex, replenishmentItem.getExternalReferenceCode());
		Assert.assertEquals(regex, replenishmentItem.getSku());
		Assert.assertEquals(regex, replenishmentItem.getUnitOfMeasureKey());
	}

	@Test
	public void testDeleteReplenishmentItem() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ReplenishmentItem replenishmentItem =
			testDeleteReplenishmentItem_addReplenishmentItem();

		assertHttpResponseStatusCode(
			204,
			replenishmentItemResource.deleteReplenishmentItemHttpResponse(
				replenishmentItem.getId()));

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem.getId()));
		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(0L));
	}

	protected ReplenishmentItem
			testDeleteReplenishmentItem_addReplenishmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteReplenishmentItem() throws Exception {

		// No namespace

		ReplenishmentItem replenishmentItem1 =
			testGraphQLDeleteReplenishmentItem_addReplenishmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteReplenishmentItem",
						new HashMap<String, Object>() {
							{
								put(
									"replenishmentItemId",
									replenishmentItem1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteReplenishmentItem"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"replenishmentItem",
					new HashMap<String, Object>() {
						{
							put(
								"replenishmentItemId",
								replenishmentItem1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminInventory_v1_0

		ReplenishmentItem replenishmentItem2 =
			testGraphQLDeleteReplenishmentItem_addReplenishmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteReplenishmentItem",
							new HashMap<String, Object>() {
								{
									put(
										"replenishmentItemId",
										replenishmentItem2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteReplenishmentItem"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0",
					new GraphQLField(
						"replenishmentItem",
						new HashMap<String, Object>() {
							{
								put(
									"replenishmentItemId",
									replenishmentItem2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ReplenishmentItem
			testGraphQLDeleteReplenishmentItem_addReplenishmentItem()
		throws Exception {

		return testGraphQLReplenishmentItem_addReplenishmentItem();
	}

	@Test
	public void testDeleteReplenishmentItemBatch() throws Exception {
		ReplenishmentItem replenishmentItem1 =
			testDeleteReplenishmentItemBatch_addReplenishmentItem();

		testDeleteReplenishmentItemBatch_deleteReplenishmentItem(
			202, replenishmentItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem1.getId()));

		replenishmentItem1 =
			testDeleteReplenishmentItemBatch_addReplenishmentItem();

		testDeleteReplenishmentItemBatch_deleteReplenishmentItem(
			202, null, replenishmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem1.getId()));

		replenishmentItem1 =
			testDeleteReplenishmentItemBatch_addReplenishmentItem();
		ReplenishmentItem replenishmentItem2 =
			testDeleteReplenishmentItemBatch_addReplenishmentItem();

		testDeleteReplenishmentItemBatch_deleteReplenishmentItem(
			202, replenishmentItem2.getExternalReferenceCode(),
			replenishmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem2.getId()));

		testDeleteReplenishmentItemBatch_deleteReplenishmentItem(
			202, replenishmentItem2.getExternalReferenceCode(),
			replenishmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem2.getId()));
	}

	protected ReplenishmentItem
			testDeleteReplenishmentItemBatch_addReplenishmentItem()
		throws Exception {

		return testDeleteReplenishmentItem_addReplenishmentItem();
	}

	protected void testDeleteReplenishmentItemBatch_deleteReplenishmentItem(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			replenishmentItemResource.deleteReplenishmentItemBatchHttpResponse(
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
	public void testDeleteReplenishmentItemByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ReplenishmentItem replenishmentItem =
			testDeleteReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		assertHttpResponseStatusCode(
			204,
			replenishmentItemResource.
				deleteReplenishmentItemByExternalReferenceCodeHttpResponse(
					replenishmentItem.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.
				getReplenishmentItemByExternalReferenceCodeHttpResponse(
					replenishmentItem.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.
				getReplenishmentItemByExternalReferenceCodeHttpResponse("-"));
	}

	protected ReplenishmentItem
			testDeleteReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteReplenishmentItemByExternalReferenceCode()
		throws Exception {

		// No namespace

		ReplenishmentItem replenishmentItem1 =
			testGraphQLDeleteReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteReplenishmentItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										replenishmentItem1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteReplenishmentItemByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"replenishmentItemByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									replenishmentItem1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminInventory_v1_0

		ReplenishmentItem replenishmentItem2 =
			testGraphQLDeleteReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteReplenishmentItemByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											replenishmentItem2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteReplenishmentItemByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0",
					new GraphQLField(
						"replenishmentItemByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										replenishmentItem2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ReplenishmentItem
			testGraphQLDeleteReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		return testGraphQLReplenishmentItem_addReplenishmentItem();
	}

	@Test
	public void testGetReplenishmentItem() throws Exception {
		ReplenishmentItem postReplenishmentItem =
			testGetReplenishmentItem_addReplenishmentItem();

		ReplenishmentItem getReplenishmentItem =
			replenishmentItemResource.getReplenishmentItem(
				postReplenishmentItem.getId());

		assertEquals(postReplenishmentItem, getReplenishmentItem);
		assertValid(getReplenishmentItem);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ReplenishmentItem postReplenishmentItem =
			testGetReplenishmentItem_addReplenishmentItem();

		ReplenishmentItem getReplenishmentItem =
			replenishmentItemResource.getReplenishmentItem(
				postReplenishmentItem.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.inventory.dto.v1_0.ReplenishmentItem"
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
			postReplenishmentItem.getId());

		assertEquals(
			getReplenishmentItem,
			ReplenishmentItemSerDes.toDTO(item.toString()));
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

	protected ReplenishmentItem testGetReplenishmentItem_addReplenishmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetReplenishmentItem() throws Exception {
		ReplenishmentItem replenishmentItem =
			testGraphQLGetReplenishmentItem_addReplenishmentItem();

		// No namespace

		Assert.assertTrue(
			equals(
				replenishmentItem,
				ReplenishmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"replenishmentItem",
								new HashMap<String, Object>() {
									{
										put(
											"replenishmentItemId",
											replenishmentItem.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/replenishmentItem"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertTrue(
			equals(
				replenishmentItem,
				ReplenishmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminInventory_v1_0",
								new GraphQLField(
									"replenishmentItem",
									new HashMap<String, Object>() {
										{
											put(
												"replenishmentItemId",
												replenishmentItem.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminInventory_v1_0",
						"Object/replenishmentItem"))));
	}

	@Test
	public void testGraphQLGetReplenishmentItemNotFound() throws Exception {
		Long irrelevantReplenishmentItemId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"replenishmentItem",
						new HashMap<String, Object>() {
							{
								put(
									"replenishmentItemId",
									irrelevantReplenishmentItemId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"replenishmentItem",
							new HashMap<String, Object>() {
								{
									put(
										"replenishmentItemId",
										irrelevantReplenishmentItemId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ReplenishmentItem
			testGraphQLGetReplenishmentItem_addReplenishmentItem()
		throws Exception {

		return testGraphQLReplenishmentItem_addReplenishmentItem();
	}

	@Test
	public void testGetReplenishmentItemByExternalReferenceCode()
		throws Exception {

		ReplenishmentItem postReplenishmentItem =
			testGetReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		ReplenishmentItem getReplenishmentItem =
			replenishmentItemResource.
				getReplenishmentItemByExternalReferenceCode(
					postReplenishmentItem.getExternalReferenceCode());

		assertEquals(postReplenishmentItem, getReplenishmentItem);
		assertValid(getReplenishmentItem);
	}

	protected ReplenishmentItem
			testGetReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetReplenishmentItemByExternalReferenceCode()
		throws Exception {

		ReplenishmentItem replenishmentItem =
			testGraphQLGetReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		// No namespace

		Assert.assertTrue(
			equals(
				replenishmentItem,
				ReplenishmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"replenishmentItemByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												replenishmentItem.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/replenishmentItemByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertTrue(
			equals(
				replenishmentItem,
				ReplenishmentItemSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminInventory_v1_0",
								new GraphQLField(
									"replenishmentItemByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													replenishmentItem.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminInventory_v1_0",
						"Object/replenishmentItemByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetReplenishmentItemByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"replenishmentItemByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminInventory_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"replenishmentItemByExternalReferenceCode",
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

	protected ReplenishmentItem
			testGraphQLGetReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		return testGraphQLReplenishmentItem_addReplenishmentItem();
	}

	@Test
	public void testGetReplenishmentItemsPage() throws Exception {
		String sku = testGetReplenishmentItemsPage_getSku();
		String irrelevantSku = testGetReplenishmentItemsPage_getIrrelevantSku();

		Page<ReplenishmentItem> page =
			replenishmentItemResource.getReplenishmentItemsPage(
				sku, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSku != null) {
			ReplenishmentItem irrelevantReplenishmentItem =
				testGetReplenishmentItemsPage_addReplenishmentItem(
					irrelevantSku, randomIrrelevantReplenishmentItem());

			page = replenishmentItemResource.getReplenishmentItemsPage(
				irrelevantSku, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantReplenishmentItem,
				(List<ReplenishmentItem>)page.getItems());
			assertValid(
				page,
				testGetReplenishmentItemsPage_getExpectedActions(
					irrelevantSku));
		}

		ReplenishmentItem replenishmentItem1 =
			testGetReplenishmentItemsPage_addReplenishmentItem(
				sku, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem2 =
			testGetReplenishmentItemsPage_addReplenishmentItem(
				sku, randomReplenishmentItem());

		page = replenishmentItemResource.getReplenishmentItemsPage(
			sku, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			replenishmentItem1, (List<ReplenishmentItem>)page.getItems());
		assertContains(
			replenishmentItem2, (List<ReplenishmentItem>)page.getItems());
		assertValid(
			page, testGetReplenishmentItemsPage_getExpectedActions(sku));

		replenishmentItemResource.deleteReplenishmentItem(
			replenishmentItem1.getId());

		replenishmentItemResource.deleteReplenishmentItem(
			replenishmentItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetReplenishmentItemsPage_getExpectedActions(String sku)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetReplenishmentItemsPageWithPagination() throws Exception {
		String sku = testGetReplenishmentItemsPage_getSku();

		Page<ReplenishmentItem> replenishmentItemsPage =
			replenishmentItemResource.getReplenishmentItemsPage(sku, null);

		int totalCount = GetterUtil.getInteger(
			replenishmentItemsPage.getTotalCount());

		ReplenishmentItem replenishmentItem1 =
			testGetReplenishmentItemsPage_addReplenishmentItem(
				sku, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem2 =
			testGetReplenishmentItemsPage_addReplenishmentItem(
				sku, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem3 =
			testGetReplenishmentItemsPage_addReplenishmentItem(
				sku, randomReplenishmentItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ReplenishmentItem> page1 =
				replenishmentItemResource.getReplenishmentItemsPage(
					sku,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				replenishmentItem1, (List<ReplenishmentItem>)page1.getItems());

			Page<ReplenishmentItem> page2 =
				replenishmentItemResource.getReplenishmentItemsPage(
					sku,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				replenishmentItem2, (List<ReplenishmentItem>)page2.getItems());

			Page<ReplenishmentItem> page3 =
				replenishmentItemResource.getReplenishmentItemsPage(
					sku,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				replenishmentItem3, (List<ReplenishmentItem>)page3.getItems());
		}
		else {
			Page<ReplenishmentItem> page1 =
				replenishmentItemResource.getReplenishmentItemsPage(
					sku, Pagination.of(1, totalCount + 2));

			List<ReplenishmentItem> replenishmentItems1 =
				(List<ReplenishmentItem>)page1.getItems();

			Assert.assertEquals(
				replenishmentItems1.toString(), totalCount + 2,
				replenishmentItems1.size());

			Page<ReplenishmentItem> page2 =
				replenishmentItemResource.getReplenishmentItemsPage(
					sku, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ReplenishmentItem> replenishmentItems2 =
				(List<ReplenishmentItem>)page2.getItems();

			Assert.assertEquals(
				replenishmentItems2.toString(), 1, replenishmentItems2.size());

			Page<ReplenishmentItem> page3 =
				replenishmentItemResource.getReplenishmentItemsPage(
					sku, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				replenishmentItem1, (List<ReplenishmentItem>)page3.getItems());
			assertContains(
				replenishmentItem2, (List<ReplenishmentItem>)page3.getItems());
			assertContains(
				replenishmentItem3, (List<ReplenishmentItem>)page3.getItems());
		}
	}

	protected ReplenishmentItem
			testGetReplenishmentItemsPage_addReplenishmentItem(
				String sku, ReplenishmentItem replenishmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetReplenishmentItemsPage_getSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetReplenishmentItemsPage_getIrrelevantSku()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetReplenishmentItemsPage() throws Exception {
		String sku = testGetReplenishmentItemsPage_getSku();

		GraphQLField graphQLField = new GraphQLField(
			"replenishmentItems",
			new HashMap<String, Object>() {
				{
					put("sku", "\"" + sku + "\"");
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject replenishmentItemsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/replenishmentItems");

		long totalCount = replenishmentItemsJSONObject.getLong("totalCount");

		ReplenishmentItem replenishmentItem1 =
			testGraphQLGetReplenishmentItemsPageReplenishmentItem_addReplenishmentItem(
				sku, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem2 =
			testGraphQLGetReplenishmentItemsPageReplenishmentItem_addReplenishmentItem(
				sku, randomReplenishmentItem());

		replenishmentItemsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/replenishmentItems");

		Assert.assertEquals(
			totalCount + 2, replenishmentItemsJSONObject.getLong("totalCount"));

		assertContains(
			replenishmentItem1,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					replenishmentItemsJSONObject.getString("items"))));
		assertContains(
			replenishmentItem2,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					replenishmentItemsJSONObject.getString("items"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		replenishmentItemsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminInventory_v1_0",
			"JSONObject/replenishmentItems");

		Assert.assertEquals(
			totalCount + 2, replenishmentItemsJSONObject.getLong("totalCount"));

		assertContains(
			replenishmentItem1,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					replenishmentItemsJSONObject.getString("items"))));
		assertContains(
			replenishmentItem2,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					replenishmentItemsJSONObject.getString("items"))));
	}

	protected ReplenishmentItem
			testGraphQLGetReplenishmentItemsPageReplenishmentItem_addReplenishmentItem(
				String sku, ReplenishmentItem replenishmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetWarehouseIdReplenishmentItemsPage() throws Exception {
		Long warehouseId =
			testGetWarehouseIdReplenishmentItemsPage_getWarehouseId();
		Long irrelevantWarehouseId =
			testGetWarehouseIdReplenishmentItemsPage_getIrrelevantWarehouseId();

		Page<ReplenishmentItem> page =
			replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
				warehouseId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantWarehouseId != null) {
			ReplenishmentItem irrelevantReplenishmentItem =
				testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
					irrelevantWarehouseId, randomIrrelevantReplenishmentItem());

			page =
				replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
					irrelevantWarehouseId,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantReplenishmentItem,
				(List<ReplenishmentItem>)page.getItems());
			assertValid(
				page,
				testGetWarehouseIdReplenishmentItemsPage_getExpectedActions(
					irrelevantWarehouseId));
		}

		ReplenishmentItem replenishmentItem1 =
			testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
				warehouseId, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem2 =
			testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
				warehouseId, randomReplenishmentItem());

		page = replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
			warehouseId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			replenishmentItem1, (List<ReplenishmentItem>)page.getItems());
		assertContains(
			replenishmentItem2, (List<ReplenishmentItem>)page.getItems());
		assertValid(
			page,
			testGetWarehouseIdReplenishmentItemsPage_getExpectedActions(
				warehouseId));

		replenishmentItemResource.deleteReplenishmentItem(
			replenishmentItem1.getId());

		replenishmentItemResource.deleteReplenishmentItem(
			replenishmentItem2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseIdReplenishmentItemsPage_getExpectedActions(
				Long warehouseId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseIdReplenishmentItemsPageWithPagination()
		throws Exception {

		Long warehouseId =
			testGetWarehouseIdReplenishmentItemsPage_getWarehouseId();

		Page<ReplenishmentItem> replenishmentItemsPage =
			replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
				warehouseId, null);

		int totalCount = GetterUtil.getInteger(
			replenishmentItemsPage.getTotalCount());

		ReplenishmentItem replenishmentItem1 =
			testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
				warehouseId, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem2 =
			testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
				warehouseId, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem3 =
			testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
				warehouseId, randomReplenishmentItem());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ReplenishmentItem> page1 =
				replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
					warehouseId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				replenishmentItem1, (List<ReplenishmentItem>)page1.getItems());

			Page<ReplenishmentItem> page2 =
				replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
					warehouseId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				replenishmentItem2, (List<ReplenishmentItem>)page2.getItems());

			Page<ReplenishmentItem> page3 =
				replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
					warehouseId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				replenishmentItem3, (List<ReplenishmentItem>)page3.getItems());
		}
		else {
			Page<ReplenishmentItem> page1 =
				replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
					warehouseId, Pagination.of(1, totalCount + 2));

			List<ReplenishmentItem> replenishmentItems1 =
				(List<ReplenishmentItem>)page1.getItems();

			Assert.assertEquals(
				replenishmentItems1.toString(), totalCount + 2,
				replenishmentItems1.size());

			Page<ReplenishmentItem> page2 =
				replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
					warehouseId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ReplenishmentItem> replenishmentItems2 =
				(List<ReplenishmentItem>)page2.getItems();

			Assert.assertEquals(
				replenishmentItems2.toString(), 1, replenishmentItems2.size());

			Page<ReplenishmentItem> page3 =
				replenishmentItemResource.getWarehouseIdReplenishmentItemsPage(
					warehouseId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				replenishmentItem1, (List<ReplenishmentItem>)page3.getItems());
			assertContains(
				replenishmentItem2, (List<ReplenishmentItem>)page3.getItems());
			assertContains(
				replenishmentItem3, (List<ReplenishmentItem>)page3.getItems());
		}
	}

	protected ReplenishmentItem
			testGetWarehouseIdReplenishmentItemsPage_addReplenishmentItem(
				Long warehouseId, ReplenishmentItem replenishmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWarehouseIdReplenishmentItemsPage_getWarehouseId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetWarehouseIdReplenishmentItemsPage_getIrrelevantWarehouseId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetWarehouseIdReplenishmentItemsPage()
		throws Exception {

		Long warehouseId =
			testGetWarehouseIdReplenishmentItemsPage_getWarehouseId();

		GraphQLField graphQLField = new GraphQLField(
			"warehouseIdReplenishmentItems",
			new HashMap<String, Object>() {
				{
					put("warehouseId", warehouseId);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject warehouseIdReplenishmentItemsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/warehouseIdReplenishmentItems");

		long totalCount = warehouseIdReplenishmentItemsJSONObject.getLong(
			"totalCount");

		ReplenishmentItem replenishmentItem1 =
			testGraphQLGetWarehouseIdReplenishmentItemsPageReplenishmentItem_addReplenishmentItem(
				warehouseId, randomReplenishmentItem());

		ReplenishmentItem replenishmentItem2 =
			testGraphQLGetWarehouseIdReplenishmentItemsPageReplenishmentItem_addReplenishmentItem(
				warehouseId, randomReplenishmentItem());

		warehouseIdReplenishmentItemsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/warehouseIdReplenishmentItems");

		Assert.assertEquals(
			totalCount + 2,
			warehouseIdReplenishmentItemsJSONObject.getLong("totalCount"));

		assertContains(
			replenishmentItem1,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					warehouseIdReplenishmentItemsJSONObject.getString(
						"items"))));
		assertContains(
			replenishmentItem2,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					warehouseIdReplenishmentItemsJSONObject.getString(
						"items"))));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		warehouseIdReplenishmentItemsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminInventory_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessCommerceAdminInventory_v1_0",
			"JSONObject/warehouseIdReplenishmentItems");

		Assert.assertEquals(
			totalCount + 2,
			warehouseIdReplenishmentItemsJSONObject.getLong("totalCount"));

		assertContains(
			replenishmentItem1,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					warehouseIdReplenishmentItemsJSONObject.getString(
						"items"))));
		assertContains(
			replenishmentItem2,
			Arrays.asList(
				ReplenishmentItemSerDes.toDTOs(
					warehouseIdReplenishmentItemsJSONObject.getString(
						"items"))));
	}

	protected ReplenishmentItem
			testGraphQLGetWarehouseIdReplenishmentItemsPageReplenishmentItem_addReplenishmentItem(
				Long warehouseId, ReplenishmentItem replenishmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchReplenishmentItem() throws Exception {
		ReplenishmentItem postReplenishmentItem =
			testPatchReplenishmentItem_addReplenishmentItem();

		ReplenishmentItem randomPatchReplenishmentItem =
			randomPatchReplenishmentItem();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ReplenishmentItem patchReplenishmentItem =
			replenishmentItemResource.patchReplenishmentItem(
				postReplenishmentItem.getId(), randomPatchReplenishmentItem);

		ReplenishmentItem expectedPatchReplenishmentItem =
			postReplenishmentItem.clone();

		BeanTestUtil.copyProperties(
			randomPatchReplenishmentItem, expectedPatchReplenishmentItem);

		ReplenishmentItem getReplenishmentItem =
			replenishmentItemResource.getReplenishmentItem(
				patchReplenishmentItem.getId());

		assertEquals(expectedPatchReplenishmentItem, getReplenishmentItem);
		assertValid(getReplenishmentItem);
	}

	protected ReplenishmentItem
			testPatchReplenishmentItem_addReplenishmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchReplenishmentItemByExternalReferenceCode()
		throws Exception {

		ReplenishmentItem postReplenishmentItem =
			testPatchReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		ReplenishmentItem randomPatchReplenishmentItem =
			randomPatchReplenishmentItem();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ReplenishmentItem patchReplenishmentItem =
			replenishmentItemResource.
				patchReplenishmentItemByExternalReferenceCode(
					postReplenishmentItem.getExternalReferenceCode(),
					randomPatchReplenishmentItem);

		ReplenishmentItem expectedPatchReplenishmentItem =
			postReplenishmentItem.clone();

		BeanTestUtil.copyProperties(
			randomPatchReplenishmentItem, expectedPatchReplenishmentItem);

		ReplenishmentItem getReplenishmentItem =
			replenishmentItemResource.
				getReplenishmentItemByExternalReferenceCode(
					patchReplenishmentItem.getExternalReferenceCode());

		assertEquals(expectedPatchReplenishmentItem, getReplenishmentItem);
		assertValid(getReplenishmentItem);
	}

	protected ReplenishmentItem
			testPatchReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostReplenishmentItem() throws Exception {
		ReplenishmentItem randomReplenishmentItem = randomReplenishmentItem();

		ReplenishmentItem postReplenishmentItem =
			testPostReplenishmentItem_addReplenishmentItem(
				randomReplenishmentItem);

		assertEquals(randomReplenishmentItem, postReplenishmentItem);
		assertValid(postReplenishmentItem);
	}

	protected ReplenishmentItem testPostReplenishmentItem_addReplenishmentItem(
			ReplenishmentItem replenishmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostReplenishmentItem() throws Exception {
		ReplenishmentItem randomReplenishmentItem = randomReplenishmentItem();

		ReplenishmentItem replenishmentItem =
			testGraphQLReplenishmentItem_addReplenishmentItem(
				testGraphQLPostReplenishmentItem_getWarehouseId(
					randomReplenishmentItem),
				testGraphQLPostReplenishmentItem_getSku(
					randomReplenishmentItem),
				randomReplenishmentItem);

		Assert.assertTrue(equals(randomReplenishmentItem, replenishmentItem));
	}

	protected Long testGraphQLPostReplenishmentItem_getWarehouseId(
			ReplenishmentItem replenishmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGraphQLPostReplenishmentItem_getSku(
			ReplenishmentItem replenishmentItem)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutReplenishmentItemByExternalReferenceCode()
		throws Exception {

		ReplenishmentItem postReplenishmentItem =
			testPutReplenishmentItemByExternalReferenceCode_addReplenishmentItem();

		ReplenishmentItem randomReplenishmentItem = randomReplenishmentItem();

		ReplenishmentItem putReplenishmentItem =
			replenishmentItemResource.
				putReplenishmentItemByExternalReferenceCode(
					postReplenishmentItem.getExternalReferenceCode(),
					randomReplenishmentItem);

		assertEquals(randomReplenishmentItem, putReplenishmentItem);
		assertValid(putReplenishmentItem);

		ReplenishmentItem getReplenishmentItem =
			replenishmentItemResource.
				getReplenishmentItemByExternalReferenceCode(
					putReplenishmentItem.getExternalReferenceCode());

		assertEquals(randomReplenishmentItem, getReplenishmentItem);
		assertValid(getReplenishmentItem);

		ReplenishmentItem newReplenishmentItem =
			testPutReplenishmentItemByExternalReferenceCode_createReplenishmentItem();

		putReplenishmentItem =
			replenishmentItemResource.
				putReplenishmentItemByExternalReferenceCode(
					newReplenishmentItem.getExternalReferenceCode(),
					newReplenishmentItem);

		assertEquals(newReplenishmentItem, putReplenishmentItem);
		assertValid(putReplenishmentItem);

		getReplenishmentItem =
			replenishmentItemResource.
				getReplenishmentItemByExternalReferenceCode(
					putReplenishmentItem.getExternalReferenceCode());

		assertEquals(newReplenishmentItem, getReplenishmentItem);

		Assert.assertEquals(
			newReplenishmentItem.getExternalReferenceCode(),
			putReplenishmentItem.getExternalReferenceCode());
	}

	protected ReplenishmentItem
			testPutReplenishmentItemByExternalReferenceCode_addReplenishmentItem()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ReplenishmentItem
			testPutReplenishmentItemByExternalReferenceCode_createReplenishmentItem()
		throws Exception {

		return randomReplenishmentItem();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ReplenishmentItem replenishmentItem1 =
			testBatchEngineDeleteImportTask_addReplenishmentItem();

		testBatchEngineDeleteImportTask_deleteReplenishmentItem(
			200, replenishmentItem1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem1.getId()));

		replenishmentItem1 =
			testBatchEngineDeleteImportTask_addReplenishmentItem();

		testBatchEngineDeleteImportTask_deleteReplenishmentItem(
			200, null, replenishmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem1.getId()));

		replenishmentItem1 =
			testBatchEngineDeleteImportTask_addReplenishmentItem();
		ReplenishmentItem replenishmentItem2 =
			testBatchEngineDeleteImportTask_addReplenishmentItem();

		testBatchEngineDeleteImportTask_deleteReplenishmentItem(
			200, replenishmentItem2.getExternalReferenceCode(),
			replenishmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem1.getId()));
		assertHttpResponseStatusCode(
			200,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem2.getId()));

		testBatchEngineDeleteImportTask_deleteReplenishmentItem(
			200, replenishmentItem2.getExternalReferenceCode(),
			replenishmentItem1.getId());

		assertHttpResponseStatusCode(
			404,
			replenishmentItemResource.getReplenishmentItemHttpResponse(
				replenishmentItem2.getId()));
	}

	protected ReplenishmentItem
			testBatchEngineDeleteImportTask_addReplenishmentItem()
		throws Exception {

		return testDeleteReplenishmentItem_addReplenishmentItem();
	}

	protected void testBatchEngineDeleteImportTask_deleteReplenishmentItem(
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
				"com.liferay.headless.commerce.admin.inventory.dto.v1_0.ReplenishmentItem",
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

	protected ReplenishmentItem
			testGraphQLReplenishmentItem_addReplenishmentItem()
		throws Exception {

		return testGraphQLReplenishmentItem_addReplenishmentItem(
			testGraphQLReplenishmentItem_getWarehouseId(),
			testGraphQLReplenishmentItem_getSku(), randomReplenishmentItem());
	}

	protected Long testGraphQLReplenishmentItem_getWarehouseId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGraphQLReplenishmentItem_getSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ReplenishmentItem
			testGraphQLReplenishmentItem_addReplenishmentItem(
				Long warehouseId, String sku,
				ReplenishmentItem replenishmentItem)
		throws Exception {

		JSONDeserializer<ReplenishmentItem> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ReplenishmentItem.class)) {

			if (getGraphQLValue(field.get(replenishmentItem)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(replenishmentItem)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createReplenishmentItem",
						new HashMap<String, Object>() {
							{
								put("warehouseId", warehouseId);
								put("sku", "\"" + sku + "\"");
								put("replenishmentItem", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createReplenishmentItem"),
			ReplenishmentItem.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		ReplenishmentItem replenishmentItem,
		List<ReplenishmentItem> replenishmentItems) {

		boolean contains = false;

		for (ReplenishmentItem item : replenishmentItems) {
			if (equals(replenishmentItem, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			replenishmentItems + " does not contain " + replenishmentItem,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ReplenishmentItem replenishmentItem1,
		ReplenishmentItem replenishmentItem2) {

		Assert.assertTrue(
			replenishmentItem1 + " does not equal " + replenishmentItem2,
			equals(replenishmentItem1, replenishmentItem2));
	}

	protected void assertEquals(
		List<ReplenishmentItem> replenishmentItems1,
		List<ReplenishmentItem> replenishmentItems2) {

		Assert.assertEquals(
			replenishmentItems1.size(), replenishmentItems2.size());

		for (int i = 0; i < replenishmentItems1.size(); i++) {
			ReplenishmentItem replenishmentItem1 = replenishmentItems1.get(i);
			ReplenishmentItem replenishmentItem2 = replenishmentItems2.get(i);

			assertEquals(replenishmentItem1, replenishmentItem2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ReplenishmentItem> replenishmentItems1,
		List<ReplenishmentItem> replenishmentItems2) {

		Assert.assertEquals(
			replenishmentItems1.size(), replenishmentItems2.size());

		for (ReplenishmentItem replenishmentItem1 : replenishmentItems1) {
			boolean contains = false;

			for (ReplenishmentItem replenishmentItem2 : replenishmentItems2) {
				if (equals(replenishmentItem1, replenishmentItem2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				replenishmentItems2 + " does not contain " + replenishmentItem1,
				contains);
		}
	}

	protected void assertValid(ReplenishmentItem replenishmentItem)
		throws Exception {

		boolean valid = true;

		if (replenishmentItem.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("availabilityDate", additionalAssertFieldName)) {
				if (replenishmentItem.getAvailabilityDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (replenishmentItem.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (replenishmentItem.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (replenishmentItem.getSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (replenishmentItem.getUnitOfMeasureKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (replenishmentItem.getWarehouseId() == null) {
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

	protected void assertValid(Page<ReplenishmentItem> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ReplenishmentItem> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ReplenishmentItem> replenishmentItems =
			page.getItems();

		int size = replenishmentItems.size();

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
					com.liferay.headless.commerce.admin.inventory.dto.v1_0.
						ReplenishmentItem.class)) {

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
		ReplenishmentItem replenishmentItem1,
		ReplenishmentItem replenishmentItem2) {

		if (replenishmentItem1 == replenishmentItem2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("availabilityDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						replenishmentItem1.getAvailabilityDate(),
						replenishmentItem2.getAvailabilityDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						replenishmentItem1.getExternalReferenceCode(),
						replenishmentItem2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						replenishmentItem1.getId(),
						replenishmentItem2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						replenishmentItem1.getQuantity(),
						replenishmentItem2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						replenishmentItem1.getSku(),
						replenishmentItem2.getSku())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						replenishmentItem1.getUnitOfMeasureKey(),
						replenishmentItem2.getUnitOfMeasureKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						replenishmentItem1.getWarehouseId(),
						replenishmentItem2.getWarehouseId())) {

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

		if (!(_replenishmentItemResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_replenishmentItemResource;

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
		ReplenishmentItem replenishmentItem) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("availabilityDate")) {
			if (operator.equals("between")) {
				Date date = replenishmentItem.getAvailabilityDate();

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
					_format.format(replenishmentItem.getAvailabilityDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = replenishmentItem.getExternalReferenceCode();

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

		if (entityFieldName.equals("quantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sku")) {
			Object object = replenishmentItem.getSku();

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
			Object object = replenishmentItem.getUnitOfMeasureKey();

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

		if (entityFieldName.equals("warehouseId")) {
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

	protected ReplenishmentItem randomReplenishmentItem() throws Exception {
		return new ReplenishmentItem() {
			{
				availabilityDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				sku = StringUtil.toLowerCase(RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				warehouseId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ReplenishmentItem randomIrrelevantReplenishmentItem()
		throws Exception {

		ReplenishmentItem randomIrrelevantReplenishmentItem =
			randomReplenishmentItem();

		return randomIrrelevantReplenishmentItem;
	}

	protected ReplenishmentItem randomPatchReplenishmentItem()
		throws Exception {

		return randomReplenishmentItem();
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

	protected ReplenishmentItemResource replenishmentItemResource;
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
		LogFactoryUtil.getLog(BaseReplenishmentItemResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.inventory.resource.v1_0.
		ReplenishmentItemResource _replenishmentItemResource;

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