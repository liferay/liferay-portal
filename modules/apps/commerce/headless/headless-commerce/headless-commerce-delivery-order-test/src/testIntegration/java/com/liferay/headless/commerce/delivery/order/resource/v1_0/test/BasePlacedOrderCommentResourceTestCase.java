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

import com.liferay.headless.commerce.delivery.order.client.dto.v1_0.PlacedOrderComment;
import com.liferay.headless.commerce.delivery.order.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.order.client.pagination.Page;
import com.liferay.headless.commerce.delivery.order.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.order.client.resource.v1_0.PlacedOrderCommentResource;
import com.liferay.headless.commerce.delivery.order.client.serdes.v1_0.PlacedOrderCommentSerDes;
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
public abstract class BasePlacedOrderCommentResourceTestCase {

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

		_placedOrderCommentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		placedOrderCommentResource = PlacedOrderCommentResource.builder(
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

		PlacedOrderComment placedOrderComment1 = randomPlacedOrderComment();

		String json = objectMapper.writeValueAsString(placedOrderComment1);

		PlacedOrderComment placedOrderComment2 = PlacedOrderCommentSerDes.toDTO(
			json);

		Assert.assertTrue(equals(placedOrderComment1, placedOrderComment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PlacedOrderComment placedOrderComment = randomPlacedOrderComment();

		String json1 = objectMapper.writeValueAsString(placedOrderComment);
		String json2 = PlacedOrderCommentSerDes.toJSON(placedOrderComment);

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

		PlacedOrderComment placedOrderComment = randomPlacedOrderComment();

		placedOrderComment.setAuthor(regex);
		placedOrderComment.setContent(regex);
		placedOrderComment.setExternalReferenceCode(regex);

		String json = PlacedOrderCommentSerDes.toJSON(placedOrderComment);

		Assert.assertFalse(json.contains(regex));

		placedOrderComment = PlacedOrderCommentSerDes.toDTO(json);

		Assert.assertEquals(regex, placedOrderComment.getAuthor());
		Assert.assertEquals(regex, placedOrderComment.getContent());
		Assert.assertEquals(
			regex, placedOrderComment.getExternalReferenceCode());
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage()
		throws Exception {

		String externalReferenceCode =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getIrrelevantExternalReferenceCode();

		Page<PlacedOrderComment> page =
			placedOrderCommentResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PlacedOrderComment irrelevantPlacedOrderComment =
				testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_addPlacedOrderComment(
					irrelevantExternalReferenceCode,
					randomIrrelevantPlacedOrderComment());

			page =
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrderComment,
				(List<PlacedOrderComment>)page.getItems());
			assertValid(
				page,
				testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PlacedOrderComment placedOrderComment1 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_addPlacedOrderComment(
				externalReferenceCode, randomPlacedOrderComment());

		PlacedOrderComment placedOrderComment2 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_addPlacedOrderComment(
				externalReferenceCode, randomPlacedOrderComment());

		page =
			placedOrderCommentResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			placedOrderComment1, (List<PlacedOrderComment>)page.getItems());
		assertContains(
			placedOrderComment2, (List<PlacedOrderComment>)page.getItems());
		assertValid(
			page,
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getExternalReferenceCode();

		Page<PlacedOrderComment> placedOrderCommentsPage =
			placedOrderCommentResource.
				getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			placedOrderCommentsPage.getTotalCount());

		PlacedOrderComment placedOrderComment1 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_addPlacedOrderComment(
				externalReferenceCode, randomPlacedOrderComment());

		PlacedOrderComment placedOrderComment2 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_addPlacedOrderComment(
				externalReferenceCode, randomPlacedOrderComment());

		PlacedOrderComment placedOrderComment3 =
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_addPlacedOrderComment(
				externalReferenceCode, randomPlacedOrderComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrderComment> page1 =
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				placedOrderComment1,
				(List<PlacedOrderComment>)page1.getItems());

			Page<PlacedOrderComment> page2 =
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				placedOrderComment2,
				(List<PlacedOrderComment>)page2.getItems());

			Page<PlacedOrderComment> page3 =
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				placedOrderComment3,
				(List<PlacedOrderComment>)page3.getItems());
		}
		else {
			Page<PlacedOrderComment> page1 =
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<PlacedOrderComment> placedOrderComments1 =
				(List<PlacedOrderComment>)page1.getItems();

			Assert.assertEquals(
				placedOrderComments1.toString(), totalCount + 2,
				placedOrderComments1.size());

			Page<PlacedOrderComment> page2 =
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrderComment> placedOrderComments2 =
				(List<PlacedOrderComment>)page2.getItems();

			Assert.assertEquals(
				placedOrderComments2.toString(), 1,
				placedOrderComments2.size());

			Page<PlacedOrderComment> page3 =
				placedOrderCommentResource.
					getPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				placedOrderComment1,
				(List<PlacedOrderComment>)page3.getItems());
			assertContains(
				placedOrderComment2,
				(List<PlacedOrderComment>)page3.getItems());
			assertContains(
				placedOrderComment3,
				(List<PlacedOrderComment>)page3.getItems());
		}
	}

	protected PlacedOrderComment
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_addPlacedOrderComment(
				String externalReferenceCode,
				PlacedOrderComment placedOrderComment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPlacedOrderByExternalReferenceCodePlacedOrderCommentsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPlacedOrderComment() throws Exception {
		PlacedOrderComment postPlacedOrderComment =
			testGetPlacedOrderComment_addPlacedOrderComment();

		PlacedOrderComment getPlacedOrderComment =
			placedOrderCommentResource.getPlacedOrderComment(
				postPlacedOrderComment.getId());

		assertEquals(postPlacedOrderComment, getPlacedOrderComment);
		assertValid(getPlacedOrderComment);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		PlacedOrderComment postPlacedOrderComment =
			testGetPlacedOrderComment_addPlacedOrderComment();

		PlacedOrderComment getPlacedOrderComment =
			placedOrderCommentResource.getPlacedOrderComment(
				postPlacedOrderComment.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.delivery.order.dto.v1_0.PlacedOrderComment"
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
			postPlacedOrderComment.getId());

		assertEquals(
			getPlacedOrderComment,
			PlacedOrderCommentSerDes.toDTO(item.toString()));
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

	protected PlacedOrderComment
			testGetPlacedOrderComment_addPlacedOrderComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderComment() throws Exception {
		PlacedOrderComment placedOrderComment =
			testGraphQLGetPlacedOrderComment_addPlacedOrderComment();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderComment,
				PlacedOrderCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderComment",
								new HashMap<String, Object>() {
									{
										put(
											"placedOrderCommentId",
											placedOrderComment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/placedOrderComment"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderComment,
				PlacedOrderCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderComment",
									new HashMap<String, Object>() {
										{
											put(
												"placedOrderCommentId",
												placedOrderComment.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderComment"))));
	}

	@Test
	public void testGraphQLGetPlacedOrderCommentNotFound() throws Exception {
		Long irrelevantPlacedOrderCommentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderComment",
						new HashMap<String, Object>() {
							{
								put(
									"placedOrderCommentId",
									irrelevantPlacedOrderCommentId);
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
							"placedOrderComment",
							new HashMap<String, Object>() {
								{
									put(
										"placedOrderCommentId",
										irrelevantPlacedOrderCommentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected PlacedOrderComment
			testGraphQLGetPlacedOrderComment_addPlacedOrderComment()
		throws Exception {

		return testGraphQLPlacedOrderComment_addPlacedOrderComment();
	}

	@Test
	public void testGetPlacedOrderCommentByExternalReferenceCode()
		throws Exception {

		PlacedOrderComment postPlacedOrderComment =
			testGetPlacedOrderCommentByExternalReferenceCode_addPlacedOrderComment();

		PlacedOrderComment getPlacedOrderComment =
			placedOrderCommentResource.
				getPlacedOrderCommentByExternalReferenceCode(
					postPlacedOrderComment.getExternalReferenceCode());

		assertEquals(postPlacedOrderComment, getPlacedOrderComment);
		assertValid(getPlacedOrderComment);
	}

	protected PlacedOrderComment
			testGetPlacedOrderCommentByExternalReferenceCode_addPlacedOrderComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPlacedOrderCommentByExternalReferenceCode()
		throws Exception {

		PlacedOrderComment placedOrderComment =
			testGraphQLGetPlacedOrderCommentByExternalReferenceCode_addPlacedOrderComment();

		// No namespace

		Assert.assertTrue(
			equals(
				placedOrderComment,
				PlacedOrderCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"placedOrderCommentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												placedOrderComment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/placedOrderCommentByExternalReferenceCode"))));

		// Using the namespace headlessCommerceDeliveryOrder_v1_0

		Assert.assertTrue(
			equals(
				placedOrderComment,
				PlacedOrderCommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryOrder_v1_0",
								new GraphQLField(
									"placedOrderCommentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													placedOrderComment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryOrder_v1_0",
						"Object/placedOrderCommentByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPlacedOrderCommentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"placedOrderCommentByExternalReferenceCode",
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
							"placedOrderCommentByExternalReferenceCode",
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

	protected PlacedOrderComment
			testGraphQLGetPlacedOrderCommentByExternalReferenceCode_addPlacedOrderComment()
		throws Exception {

		return testGraphQLPlacedOrderComment_addPlacedOrderComment();
	}

	@Test
	public void testGetPlacedOrderPlacedOrderCommentsPage() throws Exception {
		Long placedOrderId =
			testGetPlacedOrderPlacedOrderCommentsPage_getPlacedOrderId();
		Long irrelevantPlacedOrderId =
			testGetPlacedOrderPlacedOrderCommentsPage_getIrrelevantPlacedOrderId();

		Page<PlacedOrderComment> page =
			placedOrderCommentResource.getPlacedOrderPlacedOrderCommentsPage(
				placedOrderId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantPlacedOrderId != null) {
			PlacedOrderComment irrelevantPlacedOrderComment =
				testGetPlacedOrderPlacedOrderCommentsPage_addPlacedOrderComment(
					irrelevantPlacedOrderId,
					randomIrrelevantPlacedOrderComment());

			page =
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						irrelevantPlacedOrderId,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPlacedOrderComment,
				(List<PlacedOrderComment>)page.getItems());
			assertValid(
				page,
				testGetPlacedOrderPlacedOrderCommentsPage_getExpectedActions(
					irrelevantPlacedOrderId));
		}

		PlacedOrderComment placedOrderComment1 =
			testGetPlacedOrderPlacedOrderCommentsPage_addPlacedOrderComment(
				placedOrderId, randomPlacedOrderComment());

		PlacedOrderComment placedOrderComment2 =
			testGetPlacedOrderPlacedOrderCommentsPage_addPlacedOrderComment(
				placedOrderId, randomPlacedOrderComment());

		page = placedOrderCommentResource.getPlacedOrderPlacedOrderCommentsPage(
			placedOrderId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			placedOrderComment1, (List<PlacedOrderComment>)page.getItems());
		assertContains(
			placedOrderComment2, (List<PlacedOrderComment>)page.getItems());
		assertValid(
			page,
			testGetPlacedOrderPlacedOrderCommentsPage_getExpectedActions(
				placedOrderId));
	}

	protected Map<String, Map<String, String>>
			testGetPlacedOrderPlacedOrderCommentsPage_getExpectedActions(
				Long placedOrderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPlacedOrderPlacedOrderCommentsPageWithPagination()
		throws Exception {

		Long placedOrderId =
			testGetPlacedOrderPlacedOrderCommentsPage_getPlacedOrderId();

		Page<PlacedOrderComment> placedOrderCommentsPage =
			placedOrderCommentResource.getPlacedOrderPlacedOrderCommentsPage(
				placedOrderId, null);

		int totalCount = GetterUtil.getInteger(
			placedOrderCommentsPage.getTotalCount());

		PlacedOrderComment placedOrderComment1 =
			testGetPlacedOrderPlacedOrderCommentsPage_addPlacedOrderComment(
				placedOrderId, randomPlacedOrderComment());

		PlacedOrderComment placedOrderComment2 =
			testGetPlacedOrderPlacedOrderCommentsPage_addPlacedOrderComment(
				placedOrderId, randomPlacedOrderComment());

		PlacedOrderComment placedOrderComment3 =
			testGetPlacedOrderPlacedOrderCommentsPage_addPlacedOrderComment(
				placedOrderId, randomPlacedOrderComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PlacedOrderComment> page1 =
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						placedOrderId,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				placedOrderComment1,
				(List<PlacedOrderComment>)page1.getItems());

			Page<PlacedOrderComment> page2 =
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						placedOrderId,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				placedOrderComment2,
				(List<PlacedOrderComment>)page2.getItems());

			Page<PlacedOrderComment> page3 =
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						placedOrderId,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				placedOrderComment3,
				(List<PlacedOrderComment>)page3.getItems());
		}
		else {
			Page<PlacedOrderComment> page1 =
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						placedOrderId, Pagination.of(1, totalCount + 2));

			List<PlacedOrderComment> placedOrderComments1 =
				(List<PlacedOrderComment>)page1.getItems();

			Assert.assertEquals(
				placedOrderComments1.toString(), totalCount + 2,
				placedOrderComments1.size());

			Page<PlacedOrderComment> page2 =
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						placedOrderId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PlacedOrderComment> placedOrderComments2 =
				(List<PlacedOrderComment>)page2.getItems();

			Assert.assertEquals(
				placedOrderComments2.toString(), 1,
				placedOrderComments2.size());

			Page<PlacedOrderComment> page3 =
				placedOrderCommentResource.
					getPlacedOrderPlacedOrderCommentsPage(
						placedOrderId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				placedOrderComment1,
				(List<PlacedOrderComment>)page3.getItems());
			assertContains(
				placedOrderComment2,
				(List<PlacedOrderComment>)page3.getItems());
			assertContains(
				placedOrderComment3,
				(List<PlacedOrderComment>)page3.getItems());
		}
	}

	protected PlacedOrderComment
			testGetPlacedOrderPlacedOrderCommentsPage_addPlacedOrderComment(
				Long placedOrderId, PlacedOrderComment placedOrderComment)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPlacedOrderPlacedOrderCommentsPage_getPlacedOrderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetPlacedOrderPlacedOrderCommentsPage_getIrrelevantPlacedOrderId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected PlacedOrderComment
			testGraphQLPlacedOrderComment_addPlacedOrderComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PlacedOrderComment placedOrderComment,
		List<PlacedOrderComment> placedOrderComments) {

		boolean contains = false;

		for (PlacedOrderComment item : placedOrderComments) {
			if (equals(placedOrderComment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			placedOrderComments + " does not contain " + placedOrderComment,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PlacedOrderComment placedOrderComment1,
		PlacedOrderComment placedOrderComment2) {

		Assert.assertTrue(
			placedOrderComment1 + " does not equal " + placedOrderComment2,
			equals(placedOrderComment1, placedOrderComment2));
	}

	protected void assertEquals(
		List<PlacedOrderComment> placedOrderComments1,
		List<PlacedOrderComment> placedOrderComments2) {

		Assert.assertEquals(
			placedOrderComments1.size(), placedOrderComments2.size());

		for (int i = 0; i < placedOrderComments1.size(); i++) {
			PlacedOrderComment placedOrderComment1 = placedOrderComments1.get(
				i);
			PlacedOrderComment placedOrderComment2 = placedOrderComments2.get(
				i);

			assertEquals(placedOrderComment1, placedOrderComment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PlacedOrderComment> placedOrderComments1,
		List<PlacedOrderComment> placedOrderComments2) {

		Assert.assertEquals(
			placedOrderComments1.size(), placedOrderComments2.size());

		for (PlacedOrderComment placedOrderComment1 : placedOrderComments1) {
			boolean contains = false;

			for (PlacedOrderComment placedOrderComment2 :
					placedOrderComments2) {

				if (equals(placedOrderComment1, placedOrderComment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				placedOrderComments2 + " does not contain " +
					placedOrderComment1,
				contains);
		}
	}

	protected void assertValid(PlacedOrderComment placedOrderComment)
		throws Exception {

		boolean valid = true;

		if (placedOrderComment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (placedOrderComment.getAuthor() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (placedOrderComment.getContent() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (placedOrderComment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (placedOrderComment.getOrderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (placedOrderComment.getRestricted() == null) {
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

	protected void assertValid(Page<PlacedOrderComment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PlacedOrderComment> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PlacedOrderComment> placedOrderComments =
			page.getItems();

		int size = placedOrderComments.size();

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
					com.liferay.headless.commerce.delivery.order.dto.v1_0.
						PlacedOrderComment.class)) {

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
		PlacedOrderComment placedOrderComment1,
		PlacedOrderComment placedOrderComment2) {

		if (placedOrderComment1 == placedOrderComment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("author", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderComment1.getAuthor(),
						placedOrderComment2.getAuthor())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderComment1.getContent(),
						placedOrderComment2.getContent())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						placedOrderComment1.getExternalReferenceCode(),
						placedOrderComment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderComment1.getId(),
						placedOrderComment2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("orderId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderComment1.getOrderId(),
						placedOrderComment2.getOrderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("restricted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						placedOrderComment1.getRestricted(),
						placedOrderComment2.getRestricted())) {

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

		if (!(_placedOrderCommentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_placedOrderCommentResource;

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
		PlacedOrderComment placedOrderComment) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("author")) {
			Object object = placedOrderComment.getAuthor();

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
			Object object = placedOrderComment.getContent();

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
			Object object = placedOrderComment.getExternalReferenceCode();

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

	protected PlacedOrderComment randomPlacedOrderComment() throws Exception {
		return new PlacedOrderComment() {
			{
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				content = StringUtil.toLowerCase(RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				orderId = RandomTestUtil.randomLong();
				restricted = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected PlacedOrderComment randomIrrelevantPlacedOrderComment()
		throws Exception {

		PlacedOrderComment randomIrrelevantPlacedOrderComment =
			randomPlacedOrderComment();

		return randomIrrelevantPlacedOrderComment;
	}

	protected PlacedOrderComment randomPatchPlacedOrderComment()
		throws Exception {

		return randomPlacedOrderComment();
	}

	protected PlacedOrderCommentResource placedOrderCommentResource;
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
		LogFactoryUtil.getLog(BasePlacedOrderCommentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.order.resource.v1_0.
		PlacedOrderCommentResource _placedOrderCommentResource;

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