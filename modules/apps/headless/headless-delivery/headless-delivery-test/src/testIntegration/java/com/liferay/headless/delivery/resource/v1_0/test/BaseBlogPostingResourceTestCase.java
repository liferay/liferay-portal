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

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.delivery.client.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.BlogPostingResource;
import com.liferay.headless.delivery.client.serdes.v1_0.BlogPostingSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
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
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseBlogPostingResourceTestCase {

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

		_blogPostingResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		blogPostingResource = BlogPostingResource.builder(
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

		BlogPosting blogPosting1 = randomBlogPosting();

		String json = objectMapper.writeValueAsString(blogPosting1);

		BlogPosting blogPosting2 = BlogPostingSerDes.toDTO(json);

		Assert.assertTrue(equals(blogPosting1, blogPosting2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		BlogPosting blogPosting = randomBlogPosting();

		String json1 = objectMapper.writeValueAsString(blogPosting);
		String json2 = BlogPostingSerDes.toJSON(blogPosting);

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

		BlogPosting blogPosting = randomBlogPosting();

		blogPosting.setAlternativeHeadline(regex);
		blogPosting.setArticleBody(regex);
		blogPosting.setDescription(regex);
		blogPosting.setEncodingFormat(regex);
		blogPosting.setExternalReferenceCode(regex);
		blogPosting.setFriendlyUrlPath(regex);
		blogPosting.setHeadline(regex);

		String json = BlogPostingSerDes.toJSON(blogPosting);

		Assert.assertFalse(json.contains(regex));

		blogPosting = BlogPostingSerDes.toDTO(json);

		Assert.assertEquals(regex, blogPosting.getAlternativeHeadline());
		Assert.assertEquals(regex, blogPosting.getArticleBody());
		Assert.assertEquals(regex, blogPosting.getDescription());
		Assert.assertEquals(regex, blogPosting.getEncodingFormat());
		Assert.assertEquals(regex, blogPosting.getExternalReferenceCode());
		Assert.assertEquals(regex, blogPosting.getFriendlyUrlPath());
		Assert.assertEquals(regex, blogPosting.getHeadline());
	}

	@Test
	public void testDeleteBlogPosting() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting = testDeleteBlogPosting_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.deleteBlogPostingHttpResponse(
				blogPosting.getId()));

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.getBlogPostingHttpResponse(
				blogPosting.getId()));
		assertHttpResponseStatusCode(
			404, blogPostingResource.getBlogPostingHttpResponse(0L));
	}

	protected BlogPosting testDeleteBlogPosting_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testGraphQLDeleteBlogPosting() throws Exception {

		// No namespace

		BlogPosting blogPosting1 =
			testGraphQLDeleteBlogPosting_addBlogPosting();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteBlogPosting",
						new HashMap<String, Object>() {
							{
								put("blogPostingId", blogPosting1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteBlogPosting"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"blogPosting",
					new HashMap<String, Object>() {
						{
							put("blogPostingId", blogPosting1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		BlogPosting blogPosting2 =
			testGraphQLDeleteBlogPosting_addBlogPosting();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteBlogPosting",
							new HashMap<String, Object>() {
								{
									put("blogPostingId", blogPosting2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteBlogPosting"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"blogPosting",
						new HashMap<String, Object>() {
							{
								put("blogPostingId", blogPosting2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected BlogPosting testGraphQLDeleteBlogPosting_addBlogPosting()
		throws Exception {

		return testGraphQLBlogPosting_addBlogPosting();
	}

	@Test
	public void testDeleteBlogPostingBatch() throws Exception {
		BlogPosting blogPosting1 = testDeleteBlogPostingBatch_addBlogPosting();

		testDeleteBlogPostingBatch_deleteBlogPosting(
			202, null, blogPosting1.getId());

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.getBlogPostingHttpResponse(
				blogPosting1.getId()));
	}

	protected BlogPosting testDeleteBlogPostingBatch_addBlogPosting()
		throws Exception {

		return testDeleteBlogPosting_addBlogPosting();
	}

	protected void testDeleteBlogPostingBatch_deleteBlogPosting(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			blogPostingResource.deleteBlogPostingBatchHttpResponse(
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
	public void testDeleteBlogPostingMyRating() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting =
			testDeleteBlogPostingMyRating_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.deleteBlogPostingMyRatingHttpResponse(
				blogPosting.getId()));

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.getBlogPostingMyRatingHttpResponse(
				blogPosting.getId()));
		assertHttpResponseStatusCode(
			404, blogPostingResource.getBlogPostingMyRatingHttpResponse(0L));
	}

	protected BlogPosting testDeleteBlogPostingMyRating_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testGraphQLDeleteBlogPostingMyRating() throws Exception {

		// No namespace

		BlogPosting blogPosting1 =
			testGraphQLDeleteBlogPostingMyRating_addBlogPosting();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteBlogPostingMyRating",
						new HashMap<String, Object>() {
							{
								put("blogPostingId", blogPosting1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteBlogPostingMyRating"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"blogPostingMyRating",
					new HashMap<String, Object>() {
						{
							put("blogPostingId", blogPosting1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		BlogPosting blogPosting2 =
			testGraphQLDeleteBlogPostingMyRating_addBlogPosting();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteBlogPostingMyRating",
							new HashMap<String, Object>() {
								{
									put("blogPostingId", blogPosting2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteBlogPostingMyRating"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"blogPostingMyRating",
						new HashMap<String, Object>() {
							{
								put("blogPostingId", blogPosting2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected BlogPosting testGraphQLDeleteBlogPostingMyRating_addBlogPosting()
		throws Exception {

		return testGraphQLBlogPosting_addBlogPosting();
	}

	@Test
	public void testDeleteSiteBlogPostingByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting =
			testDeleteSiteBlogPostingByExternalReferenceCode_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.
				deleteSiteBlogPostingByExternalReferenceCodeHttpResponse(
					blogPosting.getSiteId(),
					blogPosting.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.
				getSiteBlogPostingByExternalReferenceCodeHttpResponse(
					blogPosting.getSiteId(),
					blogPosting.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			blogPostingResource.
				getSiteBlogPostingByExternalReferenceCodeHttpResponse(
					blogPosting.getSiteId(), "-"));
	}

	protected BlogPosting
			testDeleteSiteBlogPostingByExternalReferenceCode_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testGraphQLDeleteSiteBlogPostingByExternalReferenceCode()
		throws Exception {

		// No namespace

		BlogPosting blogPosting1 =
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCode_addBlogPosting();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteBlogPostingByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + blogPosting1.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										blogPosting1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteBlogPostingByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"blogPostingByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + blogPosting1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" + blogPosting1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		BlogPosting blogPosting2 =
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCode_addBlogPosting();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteBlogPostingByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + blogPosting2.getSiteId() + "\"");
									put(
										"externalReferenceCode",
										"\"" +
											blogPosting2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteBlogPostingByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"blogPostingByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + blogPosting2.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										blogPosting2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected BlogPosting
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCode_addBlogPosting()
		throws Exception {

		return testGraphQLSiteBlogPosting_addBlogPosting();
	}

	@Test
	public void testGetBlogPosting() throws Exception {
		BlogPosting postBlogPosting = testGetBlogPosting_addBlogPosting();

		BlogPosting getBlogPosting = blogPostingResource.getBlogPosting(
			postBlogPosting.getId());

		assertEquals(postBlogPosting, getBlogPosting);
		assertValid(getBlogPosting);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		BlogPosting postBlogPosting = testGetBlogPosting_addBlogPosting();

		BlogPosting getBlogPosting = blogPostingResource.getBlogPosting(
			postBlogPosting.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.BlogPosting"
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

		Object item = vulcanCRUDItemDelegate.getItem(postBlogPosting.getId());

		assertEquals(getBlogPosting, BlogPostingSerDes.toDTO(item.toString()));
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

	protected BlogPosting testGetBlogPosting_addBlogPosting() throws Exception {
		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testGraphQLGetBlogPosting() throws Exception {
		BlogPosting blogPosting = testGraphQLGetBlogPosting_addBlogPosting();

		// No namespace

		Assert.assertTrue(
			equals(
				blogPosting,
				BlogPostingSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"blogPosting",
								new HashMap<String, Object>() {
									{
										put(
											"blogPostingId",
											blogPosting.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/blogPosting"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				blogPosting,
				BlogPostingSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"blogPosting",
									new HashMap<String, Object>() {
										{
											put(
												"blogPostingId",
												blogPosting.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/blogPosting"))));
	}

	@Test
	public void testGraphQLGetBlogPostingNotFound() throws Exception {
		Long irrelevantBlogPostingId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"blogPosting",
						new HashMap<String, Object>() {
							{
								put("blogPostingId", irrelevantBlogPostingId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"blogPosting",
							new HashMap<String, Object>() {
								{
									put(
										"blogPostingId",
										irrelevantBlogPostingId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected BlogPosting testGraphQLGetBlogPosting_addBlogPosting()
		throws Exception {

		return testGraphQLBlogPosting_addBlogPosting();
	}

	@Test
	public void testGetBlogPostingPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting postBlogPosting =
			testGetBlogPostingPermissionsPage_addBlogPosting();

		Page<Permission> page =
			blogPostingResource.getBlogPostingPermissionsPage(
				postBlogPosting.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected BlogPosting testGetBlogPostingPermissionsPage_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testGraphQLGetBlogPostingPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting postBlogPosting =
			testGraphQLGetBlogPostingPermissionsPage_addBlogPosting();

		GraphQLField graphQLField = new GraphQLField(
			"blogPostingPermissions",
			new HashMap<String, Object>() {
				{
					put("blogPostingId", postBlogPosting.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject blogPostingPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/blogPostingPermissions");

		Assert.assertNotNull(blogPostingPermissionsJSONObject);
	}

	protected BlogPosting
			testGraphQLGetBlogPostingPermissionsPage_addBlogPosting()
		throws Exception {

		return testGraphQLBlogPosting_addBlogPosting();
	}

	@Test
	public void testGetBlogPostingRenderedContentByDisplayPageDisplayPageKey()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetSiteBlogPostingByExternalReferenceCode()
		throws Exception {

		BlogPosting postBlogPosting =
			testGetSiteBlogPostingByExternalReferenceCode_addBlogPosting();

		BlogPosting getBlogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				postBlogPosting.getSiteId(),
				postBlogPosting.getExternalReferenceCode());

		assertEquals(postBlogPosting, getBlogPosting);
		assertValid(getBlogPosting);
	}

	protected BlogPosting
			testGetSiteBlogPostingByExternalReferenceCode_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testGraphQLGetSiteBlogPostingByExternalReferenceCode()
		throws Exception {

		BlogPosting blogPosting =
			testGraphQLGetSiteBlogPostingByExternalReferenceCode_addBlogPosting();

		// No namespace

		Assert.assertTrue(
			equals(
				blogPosting,
				BlogPostingSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"blogPostingByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + blogPosting.getSiteId() +
												"\"");
										put(
											"externalReferenceCode",
											"\"" +
												blogPosting.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/blogPostingByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				blogPosting,
				BlogPostingSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"blogPostingByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" + blogPosting.getSiteId() +
													"\"");
											put(
												"externalReferenceCode",
												"\"" +
													blogPosting.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/blogPostingByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteBlogPostingByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"blogPostingByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"blogPostingByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected BlogPosting
			testGraphQLGetSiteBlogPostingByExternalReferenceCode_addBlogPosting()
		throws Exception {

		return testGraphQLSiteBlogPosting_addBlogPosting();
	}

	@Test
	public void testGetSiteBlogPostingPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting postBlogPosting =
			testGetSiteBlogPostingPermissionsPage_addBlogPosting();

		Page<Permission> page =
			blogPostingResource.getSiteBlogPostingPermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected BlogPosting testGetSiteBlogPostingPermissionsPage_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testGraphQLGetSiteBlogPostingPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting postBlogPosting =
			testGraphQLGetSiteBlogPostingPermissionsPage_addBlogPosting();

		GraphQLField graphQLField = new GraphQLField(
			"siteBlogPostingPermissions",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + postBlogPosting.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteBlogPostingPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteBlogPostingPermissions");

		Assert.assertNotNull(siteBlogPostingPermissionsJSONObject);
	}

	protected BlogPosting
			testGraphQLGetSiteBlogPostingPermissionsPage_addBlogPosting()
		throws Exception {

		return testGraphQLSiteBlogPosting_addBlogPosting();
	}

	@Test
	public void testGetSiteBlogPostingsPage() throws Exception {
		Long siteId = testGetSiteBlogPostingsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteBlogPostingsPage_getIrrelevantSiteId();

		Page<BlogPosting> page = blogPostingResource.getSiteBlogPostingsPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			BlogPosting irrelevantBlogPosting =
				testGetSiteBlogPostingsPage_addBlogPosting(
					irrelevantSiteId, randomIrrelevantBlogPosting());

			page = blogPostingResource.getSiteBlogPostingsPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantBlogPosting, (List<BlogPosting>)page.getItems());
			assertValid(
				page,
				testGetSiteBlogPostingsPage_getExpectedActions(
					irrelevantSiteId));
		}

		BlogPosting blogPosting1 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, randomBlogPosting());

		BlogPosting blogPosting2 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, randomBlogPosting());

		page = blogPostingResource.getSiteBlogPostingsPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(blogPosting1, (List<BlogPosting>)page.getItems());
		assertContains(blogPosting2, (List<BlogPosting>)page.getItems());
		assertValid(
			page, testGetSiteBlogPostingsPage_getExpectedActions(siteId));

		blogPostingResource.deleteBlogPosting(blogPosting1.getId());

		blogPostingResource.deleteBlogPosting(blogPosting2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteBlogPostingsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/blog-postings/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteBlogPostingsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteBlogPostingsPage_getSiteId();

		BlogPosting blogPosting1 = randomBlogPosting();

		blogPosting1 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, blogPosting1);

		for (EntityField entityField : entityFields) {
			Page<BlogPosting> page =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null,
					getFilterString(entityField, "between", blogPosting1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(blogPosting1),
				(List<BlogPosting>)page.getItems());
		}
	}

	@Test
	public void testGetSiteBlogPostingsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteBlogPostingsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteBlogPostingsPageWithFilterStringContains()
		throws Exception {

		testGetSiteBlogPostingsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteBlogPostingsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteBlogPostingsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteBlogPostingsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteBlogPostingsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteBlogPostingsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteBlogPostingsPage_getSiteId();

		BlogPosting blogPosting1 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, randomBlogPosting());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting2 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, randomBlogPosting());

		for (EntityField entityField : entityFields) {
			Page<BlogPosting> page =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null,
					getFilterString(entityField, operator, blogPosting1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(blogPosting1),
				(List<BlogPosting>)page.getItems());
		}
	}

	@Test
	public void testGetSiteBlogPostingsPageWithPagination() throws Exception {
		Long siteId = testGetSiteBlogPostingsPage_getSiteId();

		Page<BlogPosting> blogPostingsPage =
			blogPostingResource.getSiteBlogPostingsPage(
				siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			blogPostingsPage.getTotalCount());

		BlogPosting blogPosting1 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, randomBlogPosting());

		BlogPosting blogPosting2 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, randomBlogPosting());

		BlogPosting blogPosting3 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, randomBlogPosting());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<BlogPosting> page1 =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(blogPosting1, (List<BlogPosting>)page1.getItems());

			Page<BlogPosting> page2 =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(blogPosting2, (List<BlogPosting>)page2.getItems());

			Page<BlogPosting> page3 =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(blogPosting3, (List<BlogPosting>)page3.getItems());
		}
		else {
			Page<BlogPosting> page1 =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<BlogPosting> blogPostings1 =
				(List<BlogPosting>)page1.getItems();

			Assert.assertEquals(
				blogPostings1.toString(), totalCount + 2, blogPostings1.size());

			Page<BlogPosting> page2 =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<BlogPosting> blogPostings2 =
				(List<BlogPosting>)page2.getItems();

			Assert.assertEquals(
				blogPostings2.toString(), 1, blogPostings2.size());

			Page<BlogPosting> page3 =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(blogPosting1, (List<BlogPosting>)page3.getItems());
			assertContains(blogPosting2, (List<BlogPosting>)page3.getItems());
			assertContains(blogPosting3, (List<BlogPosting>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteBlogPostingsPageWithSortDateTime() throws Exception {
		testGetSiteBlogPostingsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, blogPosting1, blogPosting2) -> {
				BeanTestUtil.setProperty(
					blogPosting1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteBlogPostingsPageWithSortDouble() throws Exception {
		testGetSiteBlogPostingsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, blogPosting1, blogPosting2) -> {
				BeanTestUtil.setProperty(
					blogPosting1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					blogPosting2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteBlogPostingsPageWithSortInteger() throws Exception {
		testGetSiteBlogPostingsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, blogPosting1, blogPosting2) -> {
				BeanTestUtil.setProperty(
					blogPosting1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					blogPosting2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteBlogPostingsPageWithSortString() throws Exception {
		testGetSiteBlogPostingsPageWithSort(
			EntityField.Type.STRING,
			(entityField, blogPosting1, blogPosting2) -> {
				Class<?> clazz = blogPosting1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						blogPosting1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						blogPosting2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						blogPosting1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						blogPosting2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						blogPosting1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						blogPosting2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteBlogPostingsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, BlogPosting, BlogPosting, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteBlogPostingsPage_getSiteId();

		BlogPosting blogPosting1 = randomBlogPosting();
		BlogPosting blogPosting2 = randomBlogPosting();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, blogPosting1, blogPosting2);
		}

		blogPosting1 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, blogPosting1);

		blogPosting2 = testGetSiteBlogPostingsPage_addBlogPosting(
			siteId, blogPosting2);

		Page<BlogPosting> page = blogPostingResource.getSiteBlogPostingsPage(
			siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<BlogPosting> ascPage =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(blogPosting1, (List<BlogPosting>)ascPage.getItems());
			assertContains(blogPosting2, (List<BlogPosting>)ascPage.getItems());

			Page<BlogPosting> descPage =
				blogPostingResource.getSiteBlogPostingsPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				blogPosting2, (List<BlogPosting>)descPage.getItems());
			assertContains(
				blogPosting1, (List<BlogPosting>)descPage.getItems());
		}
	}

	protected BlogPosting testGetSiteBlogPostingsPage_addBlogPosting(
			Long siteId, BlogPosting blogPosting)
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(siteId, blogPosting);
	}

	protected Long testGetSiteBlogPostingsPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteBlogPostingsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteBlogPostingsPage() throws Exception {
		Long siteId = testGetSiteBlogPostingsPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"blogPostings",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject blogPostingsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/blogPostings");

		long totalCount = blogPostingsJSONObject.getLong("totalCount");

		BlogPosting blogPosting1 = testGraphQLSiteBlogPosting_addBlogPosting(
			siteId, randomBlogPosting());

		BlogPosting blogPosting2 = testGraphQLSiteBlogPosting_addBlogPosting(
			siteId, randomBlogPosting());

		blogPostingsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/blogPostings");

		Assert.assertEquals(
			totalCount + 2, blogPostingsJSONObject.getLong("totalCount"));

		assertContains(
			blogPosting1,
			Arrays.asList(
				BlogPostingSerDes.toDTOs(
					blogPostingsJSONObject.getString("items"))));
		assertContains(
			blogPosting2,
			Arrays.asList(
				BlogPostingSerDes.toDTOs(
					blogPostingsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		blogPostingsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/blogPostings");

		Assert.assertEquals(
			totalCount + 2, blogPostingsJSONObject.getLong("totalCount"));

		assertContains(
			blogPosting1,
			Arrays.asList(
				BlogPostingSerDes.toDTOs(
					blogPostingsJSONObject.getString("items"))));
		assertContains(
			blogPosting2,
			Arrays.asList(
				BlogPostingSerDes.toDTOs(
					blogPostingsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchBlogPosting() throws Exception {
		BlogPosting postBlogPosting = testPatchBlogPosting_addBlogPosting();

		BlogPosting randomPatchBlogPosting = randomPatchBlogPosting();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting patchBlogPosting = blogPostingResource.patchBlogPosting(
			postBlogPosting.getId(), randomPatchBlogPosting);

		BlogPosting expectedPatchBlogPosting = postBlogPosting.clone();

		BeanTestUtil.copyProperties(
			randomPatchBlogPosting, expectedPatchBlogPosting);

		BlogPosting getBlogPosting = blogPostingResource.getBlogPosting(
			patchBlogPosting.getId());

		assertEquals(expectedPatchBlogPosting, getBlogPosting);
		assertValid(getBlogPosting);
	}

	protected BlogPosting testPatchBlogPosting_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testPostSiteBlogPosting() throws Exception {
		BlogPosting randomBlogPosting = randomBlogPosting();

		BlogPosting postBlogPosting = testPostSiteBlogPosting_addBlogPosting(
			randomBlogPosting);

		assertEquals(randomBlogPosting, postBlogPosting);
		assertValid(postBlogPosting);
	}

	protected BlogPosting testPostSiteBlogPosting_addBlogPosting(
			BlogPosting blogPosting)
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGetSiteBlogPostingsPage_getSiteId(), blogPosting);
	}

	@Test
	public void testGraphQLPostSiteBlogPosting() throws Exception {
		BlogPosting randomBlogPosting = randomBlogPosting();

		BlogPosting blogPosting = testGraphQLSiteBlogPosting_addBlogPosting(
			testGroup.getGroupId(), randomBlogPosting);

		Assert.assertTrue(equals(randomBlogPosting, blogPosting));
	}

	@Test
	public void testPutBlogPosting() throws Exception {
		BlogPosting postBlogPosting = testPutBlogPosting_addBlogPosting();

		BlogPosting randomBlogPosting = randomBlogPosting();

		BlogPosting putBlogPosting = blogPostingResource.putBlogPosting(
			postBlogPosting.getId(), randomBlogPosting);

		assertEquals(randomBlogPosting, putBlogPosting);
		assertValid(putBlogPosting);

		BlogPosting getBlogPosting = blogPostingResource.getBlogPosting(
			putBlogPosting.getId());

		assertEquals(randomBlogPosting, getBlogPosting);
		assertValid(getBlogPosting);
	}

	protected BlogPosting testPutBlogPosting_addBlogPosting() throws Exception {
		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testPutBlogPostingPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting =
			testPutBlogPostingPermissionsPage_addBlogPosting();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			blogPostingResource.putBlogPostingPermissionsPageHttpResponse(
				blogPosting.getId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"VIEW"});
							setRoleName(role.getName());
						}
					}
				}));

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.putBlogPostingPermissionsPageHttpResponse(
				0L,
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected BlogPosting testPutBlogPostingPermissionsPage_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testPutSiteBlogPostingByExternalReferenceCode()
		throws Exception {

		BlogPosting postBlogPosting =
			testPutSiteBlogPostingByExternalReferenceCode_addBlogPosting();

		BlogPosting randomBlogPosting = randomBlogPosting();

		BlogPosting putBlogPosting =
			blogPostingResource.putSiteBlogPostingByExternalReferenceCode(
				postBlogPosting.getSiteId(),
				postBlogPosting.getExternalReferenceCode(), randomBlogPosting);

		assertEquals(randomBlogPosting, putBlogPosting);
		assertValid(putBlogPosting);

		BlogPosting getBlogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				putBlogPosting.getSiteId(),
				putBlogPosting.getExternalReferenceCode());

		assertEquals(randomBlogPosting, getBlogPosting);
		assertValid(getBlogPosting);

		BlogPosting newBlogPosting =
			testPutSiteBlogPostingByExternalReferenceCode_createBlogPosting();

		putBlogPosting =
			blogPostingResource.putSiteBlogPostingByExternalReferenceCode(
				newBlogPosting.getSiteId(),
				newBlogPosting.getExternalReferenceCode(), newBlogPosting);

		assertEquals(newBlogPosting, putBlogPosting);
		assertValid(putBlogPosting);

		getBlogPosting =
			blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
				putBlogPosting.getSiteId(),
				putBlogPosting.getExternalReferenceCode());

		assertEquals(newBlogPosting, getBlogPosting);

		Assert.assertEquals(
			newBlogPosting.getExternalReferenceCode(),
			putBlogPosting.getExternalReferenceCode());
	}

	protected BlogPosting
			testPutSiteBlogPostingByExternalReferenceCode_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	protected BlogPosting
			testPutSiteBlogPostingByExternalReferenceCode_createBlogPosting()
		throws Exception {

		return randomBlogPosting();
	}

	@Test
	public void testPutSiteBlogPostingPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting =
			testPutSiteBlogPostingPermissionsPage_addBlogPosting();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			blogPostingResource.putSiteBlogPostingPermissionsPageHttpResponse(
				testGroup.getGroupId(),
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
			blogPostingResource.putSiteBlogPostingPermissionsPageHttpResponse(
				testGroup.getGroupId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected BlogPosting testPutSiteBlogPostingPermissionsPage_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testPutSiteBlogPostingSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting =
			testPutSiteBlogPostingSubscribe_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.putSiteBlogPostingSubscribeHttpResponse(
				blogPosting.getSiteId()));

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.putSiteBlogPostingSubscribeHttpResponse(
				blogPosting.getSiteId()));
	}

	protected BlogPosting testPutSiteBlogPostingSubscribe_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testPutSiteBlogPostingUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPosting blogPosting =
			testPutSiteBlogPostingUnsubscribe_addBlogPosting();

		assertHttpResponseStatusCode(
			204,
			blogPostingResource.putSiteBlogPostingUnsubscribeHttpResponse(
				blogPosting.getSiteId()));

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.putSiteBlogPostingUnsubscribeHttpResponse(
				blogPosting.getSiteId()));
	}

	protected BlogPosting testPutSiteBlogPostingUnsubscribe_addBlogPosting()
		throws Exception {

		return blogPostingResource.postSiteBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		BlogPosting blogPosting1 =
			testBatchEngineDeleteImportTask_addBlogPosting();

		testBatchEngineDeleteImportTask_deleteBlogPosting(
			200, null, blogPosting1.getId());

		assertHttpResponseStatusCode(
			404,
			blogPostingResource.getBlogPostingHttpResponse(
				blogPosting1.getId()));
	}

	protected BlogPosting testBatchEngineDeleteImportTask_addBlogPosting()
		throws Exception {

		return testDeleteBlogPosting_addBlogPosting();
	}

	protected void testBatchEngineDeleteImportTask_deleteBlogPosting(
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
				"com.liferay.headless.delivery.dto.v1_0.BlogPosting", null,
				null, null, null,
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Test
	public void testGetBlogPostingMyRating() throws Exception {
		BlogPosting postBlogPosting = testGetBlogPosting_addBlogPosting();

		Rating postRating = testGetBlogPostingMyRating_addRating(
			postBlogPosting.getId(), randomRating());

		Rating getRating = blogPostingResource.getBlogPostingMyRating(
			postBlogPosting.getId());

		assertEquals(postRating, getRating);
		assertValid(getRating);
	}

	protected Rating testGetBlogPostingMyRating_addRating(
			long blogPostingId, Rating rating)
		throws Exception {

		return blogPostingResource.postBlogPostingMyRating(
			blogPostingId, rating);
	}

	@Test
	public void testPostBlogPostingMyRating() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPutBlogPostingMyRating() throws Exception {
		BlogPosting postBlogPosting = testPutBlogPosting_addBlogPosting();

		testPutBlogPostingMyRating_addRating(
			postBlogPosting.getId(), randomRating());

		Rating randomRating = randomRating();

		Rating putRating = blogPostingResource.putBlogPostingMyRating(
			postBlogPosting.getId(), randomRating);

		assertEquals(randomRating, putRating);
		assertValid(putRating);
	}

	protected Rating testPutBlogPostingMyRating_addRating(
			long blogPostingId, Rating rating)
		throws Exception {

		return blogPostingResource.postBlogPostingMyRating(
			blogPostingId, rating);
	}

	protected BlogPosting testGraphQLBlogPosting_addBlogPosting()
		throws Exception {

		return testGraphQLBlogPosting_addBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	protected BlogPosting testGraphQLBlogPosting_addBlogPosting(
			Long siteId, BlogPosting blogPosting)
		throws Exception {

		JSONDeserializer<BlogPosting> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(BlogPosting.class)) {

			if (getGraphQLValue(field.get(blogPosting)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(blogPosting)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteBlogPosting",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("blogPosting", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteBlogPosting"),
			BlogPosting.class);
	}

	protected BlogPosting testGraphQLSiteBlogPosting_addBlogPosting()
		throws Exception {

		return testGraphQLSiteBlogPosting_addBlogPosting(
			testGroup.getGroupId(), randomBlogPosting());
	}

	protected BlogPosting testGraphQLSiteBlogPosting_addBlogPosting(
			Long siteId, BlogPosting blogPosting)
		throws Exception {

		JSONDeserializer<BlogPosting> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(BlogPosting.class)) {

			if (getGraphQLValue(field.get(blogPosting)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(blogPosting)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteBlogPosting",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("blogPosting", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteBlogPosting"),
			BlogPosting.class);
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
		BlogPosting blogPosting, List<BlogPosting> blogPostings) {

		boolean contains = false;

		for (BlogPosting item : blogPostings) {
			if (equals(blogPosting, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			blogPostings + " does not contain " + blogPosting, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		BlogPosting blogPosting1, BlogPosting blogPosting2) {

		Assert.assertTrue(
			blogPosting1 + " does not equal " + blogPosting2,
			equals(blogPosting1, blogPosting2));
	}

	protected void assertEquals(
		List<BlogPosting> blogPostings1, List<BlogPosting> blogPostings2) {

		Assert.assertEquals(blogPostings1.size(), blogPostings2.size());

		for (int i = 0; i < blogPostings1.size(); i++) {
			BlogPosting blogPosting1 = blogPostings1.get(i);
			BlogPosting blogPosting2 = blogPostings2.get(i);

			assertEquals(blogPosting1, blogPosting2);
		}
	}

	protected void assertEquals(Rating rating1, Rating rating2) {
		Assert.assertTrue(
			rating1 + " does not equal " + rating2, equals(rating1, rating2));
	}

	protected void assertEqualsIgnoringOrder(
		List<BlogPosting> blogPostings1, List<BlogPosting> blogPostings2) {

		Assert.assertEquals(blogPostings1.size(), blogPostings2.size());

		for (BlogPosting blogPosting1 : blogPostings1) {
			boolean contains = false;

			for (BlogPosting blogPosting2 : blogPostings2) {
				if (equals(blogPosting1, blogPosting2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				blogPostings2 + " does not contain " + blogPosting1, contains);
		}
	}

	protected void assertValid(BlogPosting blogPosting) throws Exception {
		boolean valid = true;

		if (blogPosting.getDateCreated() == null) {
			valid = false;
		}

		if (blogPosting.getDateModified() == null) {
			valid = false;
		}

		if (blogPosting.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(blogPosting.getSiteId(), testGroup.getGroupId())) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (blogPosting.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (blogPosting.getAggregateRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"alternativeHeadline", additionalAssertFieldName)) {

				if (blogPosting.getAlternativeHeadline() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (blogPosting.getArticleBody() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (blogPosting.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (blogPosting.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (blogPosting.getDatePublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (blogPosting.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (blogPosting.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (blogPosting.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (blogPosting.getFriendlyUrlPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (blogPosting.getHeadline() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("image", additionalAssertFieldName)) {
				if (blogPosting.getImage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (blogPosting.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (blogPosting.getNumberOfComments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (blogPosting.getRelatedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("renderedContents", additionalAssertFieldName)) {
				if (blogPosting.getRenderedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (blogPosting.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (blogPosting.getTaxonomyCategoryIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (blogPosting.getViewableBy() == null) {
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

	protected void assertValid(Page<BlogPosting> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<BlogPosting> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<BlogPosting> blogPostings = page.getItems();

		int size = blogPostings.size();

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

	protected void assertValid(Rating rating) {
		boolean valid = true;

		if (rating.getDateCreated() == null) {
			valid = false;
		}

		if (rating.getDateModified() == null) {
			valid = false;
		}

		if (rating.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (rating.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (rating.getBestRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (rating.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (rating.getRatingValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (rating.getWorstRating() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalRatingAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.BlogPosting.class)) {

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
		BlogPosting blogPosting1, BlogPosting blogPosting2) {

		if (blogPosting1 == blogPosting2) {
			return true;
		}

		if (!Objects.equals(
				blogPosting1.getSiteId(), blogPosting2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)blogPosting1.getActions(),
						(Map)blogPosting2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getAggregateRating(),
						blogPosting2.getAggregateRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"alternativeHeadline", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						blogPosting1.getAlternativeHeadline(),
						blogPosting2.getAlternativeHeadline())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("articleBody", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getArticleBody(),
						blogPosting2.getArticleBody())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getCreator(), blogPosting2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getCustomFields(),
						blogPosting2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getDateCreated(),
						blogPosting2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getDateModified(),
						blogPosting2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("datePublished", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getDatePublished(),
						blogPosting2.getDatePublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getDescription(),
						blogPosting2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getEncodingFormat(),
						blogPosting2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						blogPosting1.getExternalReferenceCode(),
						blogPosting2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("friendlyUrlPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getFriendlyUrlPath(),
						blogPosting2.getFriendlyUrlPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getHeadline(),
						blogPosting2.getHeadline())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getId(), blogPosting2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("image", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getImage(), blogPosting2.getImage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getKeywords(),
						blogPosting2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getNumberOfComments(),
						blogPosting2.getNumberOfComments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getRelatedContents(),
						blogPosting2.getRelatedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("renderedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getRenderedContents(),
						blogPosting2.getRenderedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						blogPosting1.getTaxonomyCategoryBriefs(),
						blogPosting2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						blogPosting1.getTaxonomyCategoryIds(),
						blogPosting2.getTaxonomyCategoryIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPosting1.getViewableBy(),
						blogPosting2.getViewableBy())) {

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

	protected boolean equals(Rating rating1, Rating rating2) {
		if (rating1 == rating2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getActions(), rating2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getBestRating(), rating2.getBestRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getCreator(), rating2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateCreated(), rating2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateModified(), rating2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(rating1.getId(), rating2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getRatingValue(), rating2.getRatingValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getWorstRating(), rating2.getWorstRating())) {

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

		if (!(_blogPostingResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_blogPostingResource;

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
		EntityField entityField, String operator, BlogPosting blogPosting) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("aggregateRating")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("alternativeHeadline")) {
			Object object = blogPosting.getAlternativeHeadline();

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

		if (entityFieldName.equals("articleBody")) {
			Object object = blogPosting.getArticleBody();

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = blogPosting.getDateCreated();

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

				sb.append(_format.format(blogPosting.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = blogPosting.getDateModified();

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

				sb.append(_format.format(blogPosting.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("datePublished")) {
			if (operator.equals("between")) {
				Date date = blogPosting.getDatePublished();

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

				sb.append(_format.format(blogPosting.getDatePublished()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = blogPosting.getDescription();

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

		if (entityFieldName.equals("encodingFormat")) {
			Object object = blogPosting.getEncodingFormat();

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
			Object object = blogPosting.getExternalReferenceCode();

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
			Object object = blogPosting.getFriendlyUrlPath();

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

		if (entityFieldName.equals("headline")) {
			Object object = blogPosting.getHeadline();

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

		if (entityFieldName.equals("image")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfComments")) {
			sb.append(String.valueOf(blogPosting.getNumberOfComments()));

			return sb.toString();
		}

		if (entityFieldName.equals("relatedContents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("renderedContents")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("viewableBy")) {
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

	protected BlogPosting randomBlogPosting() throws Exception {
		return new BlogPosting() {
			{
				alternativeHeadline = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				articleBody = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				datePublished = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				friendlyUrlPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				headline = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				numberOfComments = RandomTestUtil.randomInt();
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected BlogPosting randomIrrelevantBlogPosting() throws Exception {
		BlogPosting randomIrrelevantBlogPosting = randomBlogPosting();

		randomIrrelevantBlogPosting.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantBlogPosting;
	}

	protected BlogPosting randomPatchBlogPosting() throws Exception {
		return randomBlogPosting();
	}

	protected Rating randomRating() throws Exception {
		return new Rating() {
			{
				bestRating = RandomTestUtil.randomDouble();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				ratingValue = RandomTestUtil.randomDouble();
				worstRating = RandomTestUtil.randomDouble();
			}
		};
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

	protected BlogPostingResource blogPostingResource;
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
		LogFactoryUtil.getLog(BaseBlogPostingResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.BlogPostingResource
		_blogPostingResource;

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