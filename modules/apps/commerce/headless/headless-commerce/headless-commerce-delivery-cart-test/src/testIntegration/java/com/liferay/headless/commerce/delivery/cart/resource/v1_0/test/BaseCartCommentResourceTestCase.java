/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.resource.v1_0.test;

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
import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.CartComment;
import com.liferay.headless.commerce.delivery.cart.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Page;
import com.liferay.headless.commerce.delivery.cart.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.cart.client.resource.v1_0.CartCommentResource;
import com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0.CartCommentSerDes;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseCartCommentResourceTestCase {

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

		_cartCommentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		cartCommentResource = CartCommentResource.builder(
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

		CartComment cartComment1 = randomCartComment();

		String json = objectMapper.writeValueAsString(cartComment1);

		CartComment cartComment2 = CartCommentSerDes.toDTO(json);

		Assert.assertTrue(equals(cartComment1, cartComment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		CartComment cartComment = randomCartComment();

		String json1 = objectMapper.writeValueAsString(cartComment);
		String json2 = CartCommentSerDes.toJSON(cartComment);

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

		CartComment cartComment = randomCartComment();

		cartComment.setAuthor(regex);
		cartComment.setAuthorPortraitURL(regex);
		cartComment.setContent(regex);
		cartComment.setExternalReferenceCode(regex);

		String json = CartCommentSerDes.toJSON(cartComment);

		Assert.assertFalse(json.contains(regex));

		cartComment = CartCommentSerDes.toDTO(json);

		Assert.assertEquals(regex, cartComment.getAuthor());
		Assert.assertEquals(regex, cartComment.getAuthorPortraitURL());
		Assert.assertEquals(regex, cartComment.getContent());
		Assert.assertEquals(regex, cartComment.getExternalReferenceCode());
	}

	@Test
	public void testDeleteCartComment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		CartComment cartComment = testDeleteCartComment_addCartComment();

		assertHttpResponseStatusCode(
			204,
			cartCommentResource.deleteCartCommentHttpResponse(
				cartComment.getId()));

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment.getId()));
		assertHttpResponseStatusCode(
			404, cartCommentResource.getCartCommentHttpResponse(0L));
	}

	protected CartComment testDeleteCartComment_addCartComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCartComment() throws Exception {

		// No namespace

		CartComment cartComment1 =
			testGraphQLDeleteCartComment_addCartComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCartComment",
						new HashMap<String, Object>() {
							{
								put("cartCommentId", cartComment1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteCartComment"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"cartComment",
					new HashMap<String, Object>() {
						{
							put("cartCommentId", cartComment1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		CartComment cartComment2 =
			testGraphQLDeleteCartComment_addCartComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"deleteCartComment",
							new HashMap<String, Object>() {
								{
									put("cartCommentId", cartComment2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceDeliveryCart_v1_0",
				"Object/deleteCartComment"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceDeliveryCart_v1_0",
					new GraphQLField(
						"cartComment",
						new HashMap<String, Object>() {
							{
								put("cartCommentId", cartComment2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected CartComment testGraphQLDeleteCartComment_addCartComment()
		throws Exception {

		return testGraphQLCartComment_addCartComment();
	}

	@Test
	public void testDeleteCartCommentBatch() throws Exception {
		CartComment cartComment1 = testDeleteCartCommentBatch_addCartComment();

		testDeleteCartCommentBatch_deleteCartComment(
			202, cartComment1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment1.getId()));

		cartComment1 = testDeleteCartCommentBatch_addCartComment();

		testDeleteCartCommentBatch_deleteCartComment(
			202, null, cartComment1.getId());

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment1.getId()));

		cartComment1 = testDeleteCartCommentBatch_addCartComment();
		CartComment cartComment2 = testDeleteCartCommentBatch_addCartComment();

		testDeleteCartCommentBatch_deleteCartComment(
			202, cartComment2.getExternalReferenceCode(), cartComment1.getId());

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment1.getId()));
		assertHttpResponseStatusCode(
			200,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment2.getId()));

		testDeleteCartCommentBatch_deleteCartComment(
			202, cartComment2.getExternalReferenceCode(), cartComment1.getId());

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment2.getId()));
	}

	protected CartComment testDeleteCartCommentBatch_addCartComment()
		throws Exception {

		return testDeleteCartComment_addCartComment();
	}

	protected void testDeleteCartCommentBatch_deleteCartComment(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			cartCommentResource.deleteCartCommentBatchHttpResponse(
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
	public void testDeleteCartCommentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CartComment cartComment =
			testDeleteCartCommentByExternalReferenceCode_addCartComment();

		assertHttpResponseStatusCode(
			204,
			cartCommentResource.
				deleteCartCommentByExternalReferenceCodeHttpResponse(
					cartComment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.
				getCartCommentByExternalReferenceCodeHttpResponse(
					cartComment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			cartCommentResource.
				getCartCommentByExternalReferenceCodeHttpResponse("-"));
	}

	protected CartComment
			testDeleteCartCommentByExternalReferenceCode_addCartComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteCartCommentByExternalReferenceCode()
		throws Exception {

		// No namespace

		CartComment cartComment1 =
			testGraphQLDeleteCartCommentByExternalReferenceCode_addCartComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteCartCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										cartComment1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteCartCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"cartCommentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + cartComment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		CartComment cartComment2 =
			testGraphQLDeleteCartCommentByExternalReferenceCode_addCartComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"deleteCartCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											cartComment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceDeliveryCart_v1_0",
				"Object/deleteCartCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceDeliveryCart_v1_0",
					new GraphQLField(
						"cartCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										cartComment2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected CartComment
			testGraphQLDeleteCartCommentByExternalReferenceCode_addCartComment()
		throws Exception {

		return testGraphQLCartComment_addCartComment();
	}

	@Test
	public void testGetCartByExternalReferenceCodeCommentsPage()
		throws Exception {

		String externalReferenceCode =
			testGetCartByExternalReferenceCodeCommentsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetCartByExternalReferenceCodeCommentsPage_getIrrelevantExternalReferenceCode();

		Page<CartComment> page =
			cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
				externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			CartComment irrelevantCartComment =
				testGetCartByExternalReferenceCodeCommentsPage_addCartComment(
					irrelevantExternalReferenceCode,
					randomIrrelevantCartComment());

			page =
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					irrelevantExternalReferenceCode,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantCartComment, (List<CartComment>)page.getItems());
			assertValid(
				page,
				testGetCartByExternalReferenceCodeCommentsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		CartComment cartComment1 =
			testGetCartByExternalReferenceCodeCommentsPage_addCartComment(
				externalReferenceCode, randomCartComment());

		CartComment cartComment2 =
			testGetCartByExternalReferenceCodeCommentsPage_addCartComment(
				externalReferenceCode, randomCartComment());

		page = cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
			externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(cartComment1, (List<CartComment>)page.getItems());
		assertContains(cartComment2, (List<CartComment>)page.getItems());
		assertValid(
			page,
			testGetCartByExternalReferenceCodeCommentsPage_getExpectedActions(
				externalReferenceCode));

		cartCommentResource.deleteCartComment(cartComment1.getId());

		cartCommentResource.deleteCartComment(cartComment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetCartByExternalReferenceCodeCommentsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCartByExternalReferenceCodeCommentsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetCartByExternalReferenceCodeCommentsPage_getExternalReferenceCode();

		Page<CartComment> cartCommentsPage =
			cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			cartCommentsPage.getTotalCount());

		CartComment cartComment1 =
			testGetCartByExternalReferenceCodeCommentsPage_addCartComment(
				externalReferenceCode, randomCartComment());

		CartComment cartComment2 =
			testGetCartByExternalReferenceCodeCommentsPage_addCartComment(
				externalReferenceCode, randomCartComment());

		CartComment cartComment3 =
			testGetCartByExternalReferenceCodeCommentsPage_addCartComment(
				externalReferenceCode, randomCartComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<CartComment> page1 =
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(cartComment1, (List<CartComment>)page1.getItems());

			Page<CartComment> page2 =
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(cartComment2, (List<CartComment>)page2.getItems());

			Page<CartComment> page3 =
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(cartComment3, (List<CartComment>)page3.getItems());
		}
		else {
			Page<CartComment> page1 =
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					externalReferenceCode, Pagination.of(1, totalCount + 2));

			List<CartComment> cartComments1 =
				(List<CartComment>)page1.getItems();

			Assert.assertEquals(
				cartComments1.toString(), totalCount + 2, cartComments1.size());

			Page<CartComment> page2 =
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					externalReferenceCode, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<CartComment> cartComments2 =
				(List<CartComment>)page2.getItems();

			Assert.assertEquals(
				cartComments2.toString(), 1, cartComments2.size());

			Page<CartComment> page3 =
				cartCommentResource.getCartByExternalReferenceCodeCommentsPage(
					externalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(cartComment1, (List<CartComment>)page3.getItems());
			assertContains(cartComment2, (List<CartComment>)page3.getItems());
			assertContains(cartComment3, (List<CartComment>)page3.getItems());
		}
	}

	protected CartComment
			testGetCartByExternalReferenceCodeCommentsPage_addCartComment(
				String externalReferenceCode, CartComment cartComment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetCartByExternalReferenceCodeCommentsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetCartByExternalReferenceCodeCommentsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetCartComment() throws Exception {
		CartComment postCartComment = testGetCartComment_addCartComment();

		CartComment getCartComment = cartCommentResource.getCartComment(
			postCartComment.getId());

		assertEquals(postCartComment, getCartComment);
		assertValid(getCartComment);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		CartComment postCartComment = testGetCartComment_addCartComment();

		CartComment getCartComment = cartCommentResource.getCartComment(
			postCartComment.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartComment"
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

		Object item = vulcanCRUDItemDelegate.getItem(postCartComment.getId());

		assertEquals(getCartComment, CartCommentSerDes.toDTO(item.toString()));
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

	protected CartComment testGetCartComment_addCartComment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartComment() throws Exception {
		CartComment cartComment = testGraphQLGetCartComment_addCartComment();

		// No namespace

		Assert.assertTrue(
			equals(
				cartComment,
				CartCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cartComment",
								new HashMap<String, Object>() {
									{
										put(
											"cartCommentId",
											cartComment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/cartComment"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				cartComment,
				CartCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cartComment",
									new HashMap<String, Object>() {
										{
											put(
												"cartCommentId",
												cartComment.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cartComment"))));
	}

	@Test
	public void testGraphQLGetCartCommentNotFound() throws Exception {
		Long irrelevantCartCommentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cartComment",
						new HashMap<String, Object>() {
							{
								put("cartCommentId", irrelevantCartCommentId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"cartComment",
							new HashMap<String, Object>() {
								{
									put(
										"cartCommentId",
										irrelevantCartCommentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected CartComment testGraphQLGetCartComment_addCartComment()
		throws Exception {

		return testGraphQLCartComment_addCartComment();
	}

	@Test
	public void testGetCartCommentByExternalReferenceCode() throws Exception {
		CartComment postCartComment =
			testGetCartCommentByExternalReferenceCode_addCartComment();

		CartComment getCartComment =
			cartCommentResource.getCartCommentByExternalReferenceCode(
				postCartComment.getExternalReferenceCode());

		assertEquals(postCartComment, getCartComment);
		assertValid(getCartComment);
	}

	protected CartComment
			testGetCartCommentByExternalReferenceCode_addCartComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetCartCommentByExternalReferenceCode()
		throws Exception {

		CartComment cartComment =
			testGraphQLGetCartCommentByExternalReferenceCode_addCartComment();

		// No namespace

		Assert.assertTrue(
			equals(
				cartComment,
				CartCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"cartCommentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												cartComment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/cartCommentByExternalReferenceCode"))));

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertTrue(
			equals(
				cartComment,
				CartCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCart_v1_0",
								new GraphQLField(
									"cartCommentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													cartComment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCart_v1_0",
						"Object/cartCommentByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetCartCommentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"cartCommentByExternalReferenceCode",
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

		// Using the namespace headlessCommerceDeliveryCart_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCart_v1_0",
						new GraphQLField(
							"cartCommentByExternalReferenceCode",
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

	protected CartComment
			testGraphQLGetCartCommentByExternalReferenceCode_addCartComment()
		throws Exception {

		return testGraphQLCartComment_addCartComment();
	}

	@Test
	public void testGetCartCommentsPage() throws Exception {
		Long cartId = testGetCartCommentsPage_getCartId();
		Long irrelevantCartId = testGetCartCommentsPage_getIrrelevantCartId();

		Page<CartComment> page = cartCommentResource.getCartCommentsPage(
			cartId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantCartId != null) {
			CartComment irrelevantCartComment =
				testGetCartCommentsPage_addCartComment(
					irrelevantCartId, randomIrrelevantCartComment());

			page = cartCommentResource.getCartCommentsPage(
				irrelevantCartId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantCartComment, (List<CartComment>)page.getItems());
			assertValid(
				page,
				testGetCartCommentsPage_getExpectedActions(irrelevantCartId));
		}

		CartComment cartComment1 = testGetCartCommentsPage_addCartComment(
			cartId, randomCartComment());

		CartComment cartComment2 = testGetCartCommentsPage_addCartComment(
			cartId, randomCartComment());

		page = cartCommentResource.getCartCommentsPage(
			cartId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(cartComment1, (List<CartComment>)page.getItems());
		assertContains(cartComment2, (List<CartComment>)page.getItems());
		assertValid(page, testGetCartCommentsPage_getExpectedActions(cartId));

		cartCommentResource.deleteCartComment(cartComment1.getId());

		cartCommentResource.deleteCartComment(cartComment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetCartCommentsPage_getExpectedActions(Long cartId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCartCommentsPageWithPagination() throws Exception {
		Long cartId = testGetCartCommentsPage_getCartId();

		Page<CartComment> cartCommentsPage =
			cartCommentResource.getCartCommentsPage(cartId, null);

		int totalCount = GetterUtil.getInteger(
			cartCommentsPage.getTotalCount());

		CartComment cartComment1 = testGetCartCommentsPage_addCartComment(
			cartId, randomCartComment());

		CartComment cartComment2 = testGetCartCommentsPage_addCartComment(
			cartId, randomCartComment());

		CartComment cartComment3 = testGetCartCommentsPage_addCartComment(
			cartId, randomCartComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<CartComment> page1 = cartCommentResource.getCartCommentsPage(
				cartId,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(cartComment1, (List<CartComment>)page1.getItems());

			Page<CartComment> page2 = cartCommentResource.getCartCommentsPage(
				cartId,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(cartComment2, (List<CartComment>)page2.getItems());

			Page<CartComment> page3 = cartCommentResource.getCartCommentsPage(
				cartId,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(cartComment3, (List<CartComment>)page3.getItems());
		}
		else {
			Page<CartComment> page1 = cartCommentResource.getCartCommentsPage(
				cartId, Pagination.of(1, totalCount + 2));

			List<CartComment> cartComments1 =
				(List<CartComment>)page1.getItems();

			Assert.assertEquals(
				cartComments1.toString(), totalCount + 2, cartComments1.size());

			Page<CartComment> page2 = cartCommentResource.getCartCommentsPage(
				cartId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<CartComment> cartComments2 =
				(List<CartComment>)page2.getItems();

			Assert.assertEquals(
				cartComments2.toString(), 1, cartComments2.size());

			Page<CartComment> page3 = cartCommentResource.getCartCommentsPage(
				cartId, Pagination.of(1, (int)totalCount + 3));

			assertContains(cartComment1, (List<CartComment>)page3.getItems());
			assertContains(cartComment2, (List<CartComment>)page3.getItems());
			assertContains(cartComment3, (List<CartComment>)page3.getItems());
		}
	}

	protected CartComment testGetCartCommentsPage_addCartComment(
			Long cartId, CartComment cartComment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetCartCommentsPage_getCartId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetCartCommentsPage_getIrrelevantCartId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchCartComment() throws Exception {
		CartComment postCartComment = testPatchCartComment_addCartComment();

		CartComment randomPatchCartComment = randomPatchCartComment();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CartComment patchCartComment = cartCommentResource.patchCartComment(
			postCartComment.getId(), randomPatchCartComment);

		CartComment expectedPatchCartComment = postCartComment.clone();

		BeanTestUtil.copyProperties(
			randomPatchCartComment, expectedPatchCartComment);

		CartComment getCartComment = cartCommentResource.getCartComment(
			patchCartComment.getId());

		assertEquals(expectedPatchCartComment, getCartComment);
		assertValid(getCartComment);
	}

	protected CartComment testPatchCartComment_addCartComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchCartCommentByExternalReferenceCode() throws Exception {
		CartComment postCartComment =
			testPatchCartCommentByExternalReferenceCode_addCartComment();

		CartComment randomPatchCartComment = randomPatchCartComment();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		CartComment patchCartComment =
			cartCommentResource.patchCartCommentByExternalReferenceCode(
				postCartComment.getExternalReferenceCode(),
				randomPatchCartComment);

		CartComment expectedPatchCartComment = postCartComment.clone();

		BeanTestUtil.copyProperties(
			randomPatchCartComment, expectedPatchCartComment);

		CartComment getCartComment =
			cartCommentResource.getCartCommentByExternalReferenceCode(
				patchCartComment.getExternalReferenceCode());

		assertEquals(expectedPatchCartComment, getCartComment);
		assertValid(getCartComment);
	}

	protected CartComment
			testPatchCartCommentByExternalReferenceCode_addCartComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCartByExternalReferenceCodeComment() throws Exception {
		CartComment randomCartComment = randomCartComment();

		CartComment postCartComment =
			testPostCartByExternalReferenceCodeComment_addCartComment(
				randomCartComment);

		assertEquals(randomCartComment, postCartComment);
		assertValid(postCartComment);
	}

	protected CartComment
			testPostCartByExternalReferenceCodeComment_addCartComment(
				CartComment cartComment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCartComment() throws Exception {
		CartComment randomCartComment = randomCartComment();

		CartComment postCartComment = testPostCartComment_addCartComment(
			randomCartComment);

		assertEquals(randomCartComment, postCartComment);
		assertValid(postCartComment);
	}

	protected CartComment testPostCartComment_addCartComment(
			CartComment cartComment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutCartComment() throws Exception {
		CartComment postCartComment = testPutCartComment_addCartComment();

		CartComment randomCartComment = randomCartComment();

		CartComment putCartComment = cartCommentResource.putCartComment(
			postCartComment.getId(), randomCartComment);

		assertEquals(randomCartComment, putCartComment);
		assertValid(putCartComment);

		CartComment getCartComment = cartCommentResource.getCartComment(
			putCartComment.getId());

		assertEquals(randomCartComment, getCartComment);
		assertValid(getCartComment);
	}

	protected CartComment testPutCartComment_addCartComment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutCartCommentByExternalReferenceCode() throws Exception {
		CartComment postCartComment =
			testPutCartCommentByExternalReferenceCode_addCartComment();

		CartComment randomCartComment = randomCartComment();

		CartComment putCartComment =
			cartCommentResource.putCartCommentByExternalReferenceCode(
				postCartComment.getExternalReferenceCode(), randomCartComment);

		assertEquals(randomCartComment, putCartComment);
		assertValid(putCartComment);

		CartComment getCartComment =
			cartCommentResource.getCartCommentByExternalReferenceCode(
				putCartComment.getExternalReferenceCode());

		assertEquals(randomCartComment, getCartComment);
		assertValid(getCartComment);

		CartComment newCartComment =
			testPutCartCommentByExternalReferenceCode_createCartComment();

		putCartComment =
			cartCommentResource.putCartCommentByExternalReferenceCode(
				newCartComment.getExternalReferenceCode(), newCartComment);

		assertEquals(newCartComment, putCartComment);
		assertValid(putCartComment);

		getCartComment =
			cartCommentResource.getCartCommentByExternalReferenceCode(
				putCartComment.getExternalReferenceCode());

		assertEquals(newCartComment, getCartComment);

		Assert.assertEquals(
			newCartComment.getExternalReferenceCode(),
			putCartComment.getExternalReferenceCode());
	}

	protected CartComment
			testPutCartCommentByExternalReferenceCode_addCartComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected CartComment
			testPutCartCommentByExternalReferenceCode_createCartComment()
		throws Exception {

		return randomCartComment();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		CartComment cartComment1 =
			testBatchEngineDeleteImportTask_addCartComment();

		testBatchEngineDeleteImportTask_deleteCartComment(
			200, cartComment1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment1.getId()));

		cartComment1 = testBatchEngineDeleteImportTask_addCartComment();

		testBatchEngineDeleteImportTask_deleteCartComment(
			200, null, cartComment1.getId());

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment1.getId()));

		cartComment1 = testBatchEngineDeleteImportTask_addCartComment();
		CartComment cartComment2 =
			testBatchEngineDeleteImportTask_addCartComment();

		testBatchEngineDeleteImportTask_deleteCartComment(
			200, cartComment2.getExternalReferenceCode(), cartComment1.getId());

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment1.getId()));
		assertHttpResponseStatusCode(
			200,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment2.getId()));

		testBatchEngineDeleteImportTask_deleteCartComment(
			200, cartComment2.getExternalReferenceCode(), cartComment1.getId());

		assertHttpResponseStatusCode(
			404,
			cartCommentResource.getCartCommentHttpResponse(
				cartComment2.getId()));
	}

	protected CartComment testBatchEngineDeleteImportTask_addCartComment()
		throws Exception {

		return testDeleteCartComment_addCartComment();
	}

	protected void testBatchEngineDeleteImportTask_deleteCartComment(
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
				"com.liferay.headless.commerce.delivery.cart.dto.v1_0.CartComment",
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

	protected CartComment testGraphQLCartComment_addCartComment()
		throws Exception {

		return testGraphQLCartComment_addCartComment(
			testGraphQLCartComment_getCartId(), randomCartComment());
	}

	protected Long testGraphQLCartComment_getCartId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected CartComment testGraphQLCartComment_addCartComment(
			Long cartId, CartComment cartComment)
		throws Exception {

		JSONDeserializer<CartComment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(CartComment.class)) {

			if (getGraphQLValue(field.get(cartComment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(cartComment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createCartCartComment",
						new HashMap<String, Object>() {
							{
								put("cartId", cartId);
								put("cartComment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createCartCartComment"),
			CartComment.class);
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
		CartComment cartComment, List<CartComment> cartComments) {

		boolean contains = false;

		for (CartComment item : cartComments) {
			if (equals(cartComment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			cartComments + " does not contain " + cartComment, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		CartComment cartComment1, CartComment cartComment2) {

		Assert.assertTrue(
			cartComment1 + " does not equal " + cartComment2,
			equals(cartComment1, cartComment2));
	}

	protected void assertEquals(
		List<CartComment> cartComments1, List<CartComment> cartComments2) {

		Assert.assertEquals(cartComments1.size(), cartComments2.size());

		for (int i = 0; i < cartComments1.size(); i++) {
			CartComment cartComment1 = cartComments1.get(i);
			CartComment cartComment2 = cartComments2.get(i);

			assertEquals(cartComment1, cartComment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<CartComment> cartComments1, List<CartComment> cartComments2) {

		Assert.assertEquals(cartComments1.size(), cartComments2.size());

		for (CartComment cartComment1 : cartComments1) {
			boolean contains = false;

			for (CartComment cartComment2 : cartComments2) {
				if (equals(cartComment1, cartComment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				cartComments2 + " does not contain " + cartComment1, contains);
		}
	}

	protected void assertValid(CartComment cartComment) throws Exception {
		boolean valid = true;

		if (cartComment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (cartComment.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("authorId", additionalAssertFieldName)) {
				if (cartComment.getAuthorId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"authorPortraitURL", additionalAssertFieldName)) {

				if (cartComment.getAuthorPortraitURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (cartComment.getContent() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (cartComment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (cartComment.getModifiedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (cartComment.getOrderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (cartComment.getRestricted() == null) {
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

	protected void assertValid(Page<CartComment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<CartComment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<CartComment> cartComments = page.getItems();

		int size = cartComments.size();

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
					com.liferay.headless.commerce.delivery.cart.dto.v1_0.
						CartComment.class)) {

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
		CartComment cartComment1, CartComment cartComment2) {

		if (cartComment1 == cartComment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cartComment1.getAuthor(), cartComment2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("authorId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cartComment1.getAuthorId(),
						cartComment2.getAuthorId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"authorPortraitURL", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cartComment1.getAuthorPortraitURL(),
						cartComment2.getAuthorPortraitURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cartComment1.getContent(), cartComment2.getContent())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						cartComment1.getExternalReferenceCode(),
						cartComment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cartComment1.getId(), cartComment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cartComment1.getModifiedDate(),
						cartComment2.getModifiedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cartComment1.getOrderId(), cartComment2.getOrderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						cartComment1.getRestricted(),
						cartComment2.getRestricted())) {

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

		if (!(_cartCommentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_cartCommentResource;

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
		EntityField entityField, String operator, CartComment cartComment) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("author")) {
			Object object = cartComment.getAuthor();

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

		if (entityFieldName.equals("authorId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("authorPortraitURL")) {
			Object object = cartComment.getAuthorPortraitURL();

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

		if (entityFieldName.equals("content")) {
			Object object = cartComment.getContent();

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

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = cartComment.getExternalReferenceCode();

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

		if (entityFieldName.equals("modifiedDate")) {
			if (operator.equals("between")) {
				Date date = cartComment.getModifiedDate();

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

				sb.append(_format.format(cartComment.getModifiedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("orderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("restricted")) {
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

	protected CartComment randomCartComment() throws Exception {
		return new CartComment() {
			{
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				authorId = RandomTestUtil.randomLong();
				authorPortraitURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				content = StringUtil.toLowerCase(RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiedDate = RandomTestUtil.nextDate();
				orderId = RandomTestUtil.randomLong();
				restricted = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected CartComment randomIrrelevantCartComment() throws Exception {
		CartComment randomIrrelevantCartComment = randomCartComment();

		return randomIrrelevantCartComment;
	}

	protected CartComment randomPatchCartComment() throws Exception {
		return randomCartComment();
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

	protected CartCommentResource cartCommentResource;
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
		LogFactoryUtil.getLog(BaseCartCommentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.cart.resource.v1_0.
		CartCommentResource _cartCommentResource;

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