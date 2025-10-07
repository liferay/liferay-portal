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
import com.liferay.headless.delivery.client.dto.v1_0.BlogPostingImage;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.BlogPostingImageResource;
import com.liferay.headless.delivery.client.serdes.v1_0.BlogPostingImageSerDes;
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
public abstract class BaseBlogPostingImageResourceTestCase {

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

		_blogPostingImageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		blogPostingImageResource = BlogPostingImageResource.builder(
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

		BlogPostingImage blogPostingImage1 = randomBlogPostingImage();

		String json = objectMapper.writeValueAsString(blogPostingImage1);

		BlogPostingImage blogPostingImage2 = BlogPostingImageSerDes.toDTO(json);

		Assert.assertTrue(equals(blogPostingImage1, blogPostingImage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		BlogPostingImage blogPostingImage = randomBlogPostingImage();

		String json1 = objectMapper.writeValueAsString(blogPostingImage);
		String json2 = BlogPostingImageSerDes.toJSON(blogPostingImage);

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

		BlogPostingImage blogPostingImage = randomBlogPostingImage();

		blogPostingImage.setContentUrl(regex);
		blogPostingImage.setContentValue(regex);
		blogPostingImage.setEncodingFormat(regex);
		blogPostingImage.setExternalReferenceCode(regex);
		blogPostingImage.setFileExtension(regex);
		blogPostingImage.setTitle(regex);

		String json = BlogPostingImageSerDes.toJSON(blogPostingImage);

		Assert.assertFalse(json.contains(regex));

		blogPostingImage = BlogPostingImageSerDes.toDTO(json);

		Assert.assertEquals(regex, blogPostingImage.getContentUrl());
		Assert.assertEquals(regex, blogPostingImage.getContentValue());
		Assert.assertEquals(regex, blogPostingImage.getEncodingFormat());
		Assert.assertEquals(regex, blogPostingImage.getExternalReferenceCode());
		Assert.assertEquals(regex, blogPostingImage.getFileExtension());
		Assert.assertEquals(regex, blogPostingImage.getTitle());
	}

	@Test
	public void testDeleteBlogPostingImage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPostingImage blogPostingImage =
			testDeleteBlogPostingImage_addBlogPostingImage();

		assertHttpResponseStatusCode(
			204,
			blogPostingImageResource.deleteBlogPostingImageHttpResponse(
				blogPostingImage.getId()));

		assertHttpResponseStatusCode(
			404,
			blogPostingImageResource.getBlogPostingImageHttpResponse(
				blogPostingImage.getId()));
		assertHttpResponseStatusCode(
			404, blogPostingImageResource.getBlogPostingImageHttpResponse(0L));
	}

	protected BlogPostingImage testDeleteBlogPostingImage_addBlogPostingImage()
		throws Exception {

		return blogPostingImageResource.postSiteBlogPostingImage(
			testGroup.getGroupId(), randomBlogPostingImage(),
			getMultipartFiles());
	}

	@Test
	public void testGraphQLDeleteBlogPostingImage() throws Exception {

		// No namespace

		BlogPostingImage blogPostingImage1 =
			testGraphQLDeleteBlogPostingImage_addBlogPostingImage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteBlogPostingImage",
						new HashMap<String, Object>() {
							{
								put(
									"blogPostingImageId",
									blogPostingImage1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteBlogPostingImage"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"blogPostingImage",
					new HashMap<String, Object>() {
						{
							put(
								"blogPostingImageId",
								blogPostingImage1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		BlogPostingImage blogPostingImage2 =
			testGraphQLDeleteBlogPostingImage_addBlogPostingImage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteBlogPostingImage",
							new HashMap<String, Object>() {
								{
									put(
										"blogPostingImageId",
										blogPostingImage2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteBlogPostingImage"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"blogPostingImage",
						new HashMap<String, Object>() {
							{
								put(
									"blogPostingImageId",
									blogPostingImage2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected BlogPostingImage
			testGraphQLDeleteBlogPostingImage_addBlogPostingImage()
		throws Exception {

		return testGraphQLBlogPostingImage_addBlogPostingImage();
	}

	@Test
	public void testDeleteBlogPostingImageBatch() throws Exception {
		BlogPostingImage blogPostingImage1 =
			testDeleteBlogPostingImageBatch_addBlogPostingImage();

		testDeleteBlogPostingImageBatch_deleteBlogPostingImage(
			202, null, blogPostingImage1.getId());

		assertHttpResponseStatusCode(
			404,
			blogPostingImageResource.getBlogPostingImageHttpResponse(
				blogPostingImage1.getId()));
	}

	protected BlogPostingImage
			testDeleteBlogPostingImageBatch_addBlogPostingImage()
		throws Exception {

		return testDeleteBlogPostingImage_addBlogPostingImage();
	}

	protected void testDeleteBlogPostingImageBatch_deleteBlogPostingImage(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			blogPostingImageResource.deleteBlogPostingImageBatchHttpResponse(
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
	public void testDeleteSiteBlogPostingImageByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPostingImage blogPostingImage =
			testDeleteSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage();

		assertHttpResponseStatusCode(
			204,
			blogPostingImageResource.
				deleteSiteBlogPostingImageByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId(),
					blogPostingImage.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			blogPostingImageResource.
				getSiteBlogPostingImageByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId(),
					blogPostingImage.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			blogPostingImageResource.
				getSiteBlogPostingImageByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId(),
					"-"));
	}

	protected BlogPostingImage
			testDeleteSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage()
		throws Exception {

		return blogPostingImageResource.postSiteBlogPostingImage(
			testGroup.getGroupId(), randomBlogPostingImage(),
			getMultipartFiles());
	}

	protected Long
			testDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Test
	public void testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode()
		throws Exception {

		// No namespace

		BlogPostingImage blogPostingImage1 =
			testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteBlogPostingImageByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										blogPostingImage1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteBlogPostingImageByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"blogPostingImageByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" +
									blogPostingImage1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		BlogPostingImage blogPostingImage2 =
			testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteBlogPostingImageByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											blogPostingImage2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteBlogPostingImageByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"blogPostingImageByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										blogPostingImage2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected BlogPostingImage
			testGraphQLDeleteSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage()
		throws Exception {

		return testGraphQLSiteBlogPostingImage_addBlogPostingImage();
	}

	@Test
	public void testGetBlogPostingImage() throws Exception {
		BlogPostingImage postBlogPostingImage =
			testGetBlogPostingImage_addBlogPostingImage();

		BlogPostingImage getBlogPostingImage =
			blogPostingImageResource.getBlogPostingImage(
				postBlogPostingImage.getId());

		assertEquals(postBlogPostingImage, getBlogPostingImage);
		assertValid(getBlogPostingImage);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		BlogPostingImage postBlogPostingImage =
			testGetBlogPostingImage_addBlogPostingImage();

		BlogPostingImage getBlogPostingImage =
			blogPostingImageResource.getBlogPostingImage(
				postBlogPostingImage.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.BlogPostingImage"
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
			postBlogPostingImage.getId());

		assertEquals(
			getBlogPostingImage, BlogPostingImageSerDes.toDTO(item.toString()));
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

	protected BlogPostingImage testGetBlogPostingImage_addBlogPostingImage()
		throws Exception {

		return blogPostingImageResource.postSiteBlogPostingImage(
			testGroup.getGroupId(), randomBlogPostingImage(),
			getMultipartFiles());
	}

	@Test
	public void testGraphQLGetBlogPostingImage() throws Exception {
		BlogPostingImage blogPostingImage =
			testGraphQLGetBlogPostingImage_addBlogPostingImage();

		// No namespace

		Assert.assertTrue(
			equals(
				blogPostingImage,
				BlogPostingImageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"blogPostingImage",
								new HashMap<String, Object>() {
									{
										put(
											"blogPostingImageId",
											blogPostingImage.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/blogPostingImage"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				blogPostingImage,
				BlogPostingImageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"blogPostingImage",
									new HashMap<String, Object>() {
										{
											put(
												"blogPostingImageId",
												blogPostingImage.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/blogPostingImage"))));
	}

	@Test
	public void testGraphQLGetBlogPostingImageNotFound() throws Exception {
		Long irrelevantBlogPostingImageId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"blogPostingImage",
						new HashMap<String, Object>() {
							{
								put(
									"blogPostingImageId",
									irrelevantBlogPostingImageId);
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
							"blogPostingImage",
							new HashMap<String, Object>() {
								{
									put(
										"blogPostingImageId",
										irrelevantBlogPostingImageId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected BlogPostingImage
			testGraphQLGetBlogPostingImage_addBlogPostingImage()
		throws Exception {

		return testGraphQLBlogPostingImage_addBlogPostingImage();
	}

	@Test
	public void testGetSiteBlogPostingImageByExternalReferenceCode()
		throws Exception {

		BlogPostingImage postBlogPostingImage =
			testGetSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage();

		BlogPostingImage getBlogPostingImage =
			blogPostingImageResource.
				getSiteBlogPostingImageByExternalReferenceCode(
					testGetSiteBlogPostingImageByExternalReferenceCode_getSiteId(),
					postBlogPostingImage.getExternalReferenceCode());

		assertEquals(postBlogPostingImage, getBlogPostingImage);
		assertValid(getBlogPostingImage);
	}

	protected BlogPostingImage
			testGetSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage()
		throws Exception {

		return blogPostingImageResource.postSiteBlogPostingImage(
			testGroup.getGroupId(), randomBlogPostingImage(),
			getMultipartFiles());
	}

	protected Long
			testGetSiteBlogPostingImageByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteBlogPostingImageByExternalReferenceCode()
		throws Exception {

		BlogPostingImage blogPostingImage =
			testGraphQLGetSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage();

		// No namespace

		Assert.assertTrue(
			equals(
				blogPostingImage,
				BlogPostingImageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"blogPostingImageByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteBlogPostingImageByExternalReferenceCode_getSiteId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												blogPostingImage.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/blogPostingImageByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				blogPostingImage,
				BlogPostingImageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"blogPostingImageByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteBlogPostingImageByExternalReferenceCode_getSiteId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													blogPostingImage.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/blogPostingImageByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteBlogPostingImageByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteBlogPostingImageByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"blogPostingImageByExternalReferenceCode",
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
							"blogPostingImageByExternalReferenceCode",
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

	protected BlogPostingImage
			testGraphQLGetSiteBlogPostingImageByExternalReferenceCode_addBlogPostingImage()
		throws Exception {

		return testGraphQLSiteBlogPostingImage_addBlogPostingImage();
	}

	@Test
	public void testGetSiteBlogPostingImagesPage() throws Exception {
		Long siteId = testGetSiteBlogPostingImagesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteBlogPostingImagesPage_getIrrelevantSiteId();

		Page<BlogPostingImage> page =
			blogPostingImageResource.getSiteBlogPostingImagesPage(
				siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			BlogPostingImage irrelevantBlogPostingImage =
				testGetSiteBlogPostingImagesPage_addBlogPostingImage(
					irrelevantSiteId, randomIrrelevantBlogPostingImage());

			page = blogPostingImageResource.getSiteBlogPostingImagesPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantBlogPostingImage,
				(List<BlogPostingImage>)page.getItems());
			assertValid(
				page,
				testGetSiteBlogPostingImagesPage_getExpectedActions(
					irrelevantSiteId));
		}

		BlogPostingImage blogPostingImage1 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		BlogPostingImage blogPostingImage2 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		page = blogPostingImageResource.getSiteBlogPostingImagesPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			blogPostingImage1, (List<BlogPostingImage>)page.getItems());
		assertContains(
			blogPostingImage2, (List<BlogPostingImage>)page.getItems());
		assertValid(
			page, testGetSiteBlogPostingImagesPage_getExpectedActions(siteId));

		blogPostingImageResource.deleteBlogPostingImage(
			blogPostingImage1.getId());

		blogPostingImageResource.deleteBlogPostingImage(
			blogPostingImage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteBlogPostingImagesPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/blog-posting-images/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteBlogPostingImagesPage_getSiteId();

		BlogPostingImage blogPostingImage1 = randomBlogPostingImage();

		blogPostingImage1 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, blogPostingImage1);

		for (EntityField entityField : entityFields) {
			Page<BlogPostingImage> page =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null,
					getFilterString(entityField, "between", blogPostingImage1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(blogPostingImage1),
				(List<BlogPostingImage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithFilterStringContains()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteBlogPostingImagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteBlogPostingImagesPage_getSiteId();

		BlogPostingImage blogPostingImage1 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		BlogPostingImage blogPostingImage2 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		for (EntityField entityField : entityFields) {
			Page<BlogPostingImage> page =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null,
					getFilterString(entityField, operator, blogPostingImage1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(blogPostingImage1),
				(List<BlogPostingImage>)page.getItems());
		}
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteBlogPostingImagesPage_getSiteId();

		Page<BlogPostingImage> blogPostingImagesPage =
			blogPostingImageResource.getSiteBlogPostingImagesPage(
				siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			blogPostingImagesPage.getTotalCount());

		BlogPostingImage blogPostingImage1 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		BlogPostingImage blogPostingImage2 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		BlogPostingImage blogPostingImage3 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<BlogPostingImage> page1 =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				blogPostingImage1, (List<BlogPostingImage>)page1.getItems());

			Page<BlogPostingImage> page2 =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				blogPostingImage2, (List<BlogPostingImage>)page2.getItems());

			Page<BlogPostingImage> page3 =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				blogPostingImage3, (List<BlogPostingImage>)page3.getItems());
		}
		else {
			Page<BlogPostingImage> page1 =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<BlogPostingImage> blogPostingImages1 =
				(List<BlogPostingImage>)page1.getItems();

			Assert.assertEquals(
				blogPostingImages1.toString(), totalCount + 2,
				blogPostingImages1.size());

			Page<BlogPostingImage> page2 =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<BlogPostingImage> blogPostingImages2 =
				(List<BlogPostingImage>)page2.getItems();

			Assert.assertEquals(
				blogPostingImages2.toString(), 1, blogPostingImages2.size());

			Page<BlogPostingImage> page3 =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				blogPostingImage1, (List<BlogPostingImage>)page3.getItems());
			assertContains(
				blogPostingImage2, (List<BlogPostingImage>)page3.getItems());
			assertContains(
				blogPostingImage3, (List<BlogPostingImage>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithSortDateTime()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, blogPostingImage1, blogPostingImage2) -> {
				BeanTestUtil.setProperty(
					blogPostingImage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithSortDouble()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, blogPostingImage1, blogPostingImage2) -> {
				BeanTestUtil.setProperty(
					blogPostingImage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					blogPostingImage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithSortInteger()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, blogPostingImage1, blogPostingImage2) -> {
				BeanTestUtil.setProperty(
					blogPostingImage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					blogPostingImage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteBlogPostingImagesPageWithSortString()
		throws Exception {

		testGetSiteBlogPostingImagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, blogPostingImage1, blogPostingImage2) -> {
				Class<?> clazz = blogPostingImage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						blogPostingImage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						blogPostingImage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						blogPostingImage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						blogPostingImage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						blogPostingImage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						blogPostingImage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteBlogPostingImagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, BlogPostingImage, BlogPostingImage, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteBlogPostingImagesPage_getSiteId();

		BlogPostingImage blogPostingImage1 = randomBlogPostingImage();
		BlogPostingImage blogPostingImage2 = randomBlogPostingImage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, blogPostingImage1, blogPostingImage2);
		}

		blogPostingImage1 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, blogPostingImage1);

		blogPostingImage2 =
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				siteId, blogPostingImage2);

		Page<BlogPostingImage> page =
			blogPostingImageResource.getSiteBlogPostingImagesPage(
				siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<BlogPostingImage> ascPage =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				blogPostingImage1, (List<BlogPostingImage>)ascPage.getItems());
			assertContains(
				blogPostingImage2, (List<BlogPostingImage>)ascPage.getItems());

			Page<BlogPostingImage> descPage =
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					siteId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				blogPostingImage2, (List<BlogPostingImage>)descPage.getItems());
			assertContains(
				blogPostingImage1, (List<BlogPostingImage>)descPage.getItems());
		}
	}

	protected BlogPostingImage
			testGetSiteBlogPostingImagesPage_addBlogPostingImage(
				Long siteId, BlogPostingImage blogPostingImage)
		throws Exception {

		return blogPostingImageResource.postSiteBlogPostingImage(
			siteId, blogPostingImage, getMultipartFiles());
	}

	protected Long testGetSiteBlogPostingImagesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteBlogPostingImagesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteBlogPostingImagesPage() throws Exception {
		Long siteId = testGetSiteBlogPostingImagesPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"blogPostingImages",
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

		JSONObject blogPostingImagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/blogPostingImages");

		long totalCount = blogPostingImagesJSONObject.getLong("totalCount");

		BlogPostingImage blogPostingImage1 =
			testGraphQLSiteBlogPostingImage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		BlogPostingImage blogPostingImage2 =
			testGraphQLSiteBlogPostingImage_addBlogPostingImage(
				siteId, randomBlogPostingImage());

		blogPostingImagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/blogPostingImages");

		Assert.assertEquals(
			totalCount + 2, blogPostingImagesJSONObject.getLong("totalCount"));

		assertContains(
			blogPostingImage1,
			Arrays.asList(
				BlogPostingImageSerDes.toDTOs(
					blogPostingImagesJSONObject.getString("items"))));
		assertContains(
			blogPostingImage2,
			Arrays.asList(
				BlogPostingImageSerDes.toDTOs(
					blogPostingImagesJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		blogPostingImagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/blogPostingImages");

		Assert.assertEquals(
			totalCount + 2, blogPostingImagesJSONObject.getLong("totalCount"));

		assertContains(
			blogPostingImage1,
			Arrays.asList(
				BlogPostingImageSerDes.toDTOs(
					blogPostingImagesJSONObject.getString("items"))));
		assertContains(
			blogPostingImage2,
			Arrays.asList(
				BlogPostingImageSerDes.toDTOs(
					blogPostingImagesJSONObject.getString("items"))));
	}

	@Test
	public void testPostSiteBlogPostingImage() throws Exception {
		BlogPostingImage randomBlogPostingImage = randomBlogPostingImage();

		Map<String, File> multipartFiles = getMultipartFiles();

		BlogPostingImage postBlogPostingImage =
			testPostSiteBlogPostingImage_addBlogPostingImage(
				randomBlogPostingImage, multipartFiles);

		assertEquals(randomBlogPostingImage, postBlogPostingImage);
		assertValid(postBlogPostingImage);

		assertValid(postBlogPostingImage, multipartFiles);
	}

	protected BlogPostingImage testPostSiteBlogPostingImage_addBlogPostingImage(
			BlogPostingImage blogPostingImage, Map<String, File> multipartFiles)
		throws Exception {

		return blogPostingImageResource.postSiteBlogPostingImage(
			testGetSiteBlogPostingImagesPage_getSiteId(), blogPostingImage,
			multipartFiles);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		BlogPostingImage blogPostingImage1 =
			testBatchEngineDeleteImportTask_addBlogPostingImage();

		testBatchEngineDeleteImportTask_deleteBlogPostingImage(
			200, null, blogPostingImage1.getId());

		assertHttpResponseStatusCode(
			404,
			blogPostingImageResource.getBlogPostingImageHttpResponse(
				blogPostingImage1.getId()));
	}

	protected BlogPostingImage
			testBatchEngineDeleteImportTask_addBlogPostingImage()
		throws Exception {

		return testDeleteBlogPostingImage_addBlogPostingImage();
	}

	protected void testBatchEngineDeleteImportTask_deleteBlogPostingImage(
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
				"com.liferay.headless.delivery.dto.v1_0.BlogPostingImage", null,
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

	protected BlogPostingImage testGraphQLBlogPostingImage_addBlogPostingImage()
		throws Exception {

		return testGraphQLBlogPostingImage_addBlogPostingImage(
			testGroup.getGroupId(), randomBlogPostingImage());
	}

	protected BlogPostingImage testGraphQLBlogPostingImage_addBlogPostingImage(
			Long siteId, BlogPostingImage blogPostingImage)
		throws Exception {

		JSONDeserializer<BlogPostingImage> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(BlogPostingImage.class)) {

			if (getGraphQLValue(field.get(blogPostingImage)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(blogPostingImage)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteBlogPostingImage",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("blogPostingImage", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteBlogPostingImage"),
			BlogPostingImage.class);
	}

	protected BlogPostingImage
			testGraphQLSiteBlogPostingImage_addBlogPostingImage()
		throws Exception {

		return testGraphQLSiteBlogPostingImage_addBlogPostingImage(
			testGroup.getGroupId(), randomBlogPostingImage());
	}

	protected BlogPostingImage
			testGraphQLSiteBlogPostingImage_addBlogPostingImage(
				Long siteId, BlogPostingImage blogPostingImage)
		throws Exception {

		JSONDeserializer<BlogPostingImage> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(BlogPostingImage.class)) {

			if (getGraphQLValue(field.get(blogPostingImage)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(blogPostingImage)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteBlogPostingImage",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("blogPostingImage", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteBlogPostingImage"),
			BlogPostingImage.class);
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
		BlogPostingImage blogPostingImage,
		List<BlogPostingImage> blogPostingImages) {

		boolean contains = false;

		for (BlogPostingImage item : blogPostingImages) {
			if (equals(blogPostingImage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			blogPostingImages + " does not contain " + blogPostingImage,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		BlogPostingImage blogPostingImage1,
		BlogPostingImage blogPostingImage2) {

		Assert.assertTrue(
			blogPostingImage1 + " does not equal " + blogPostingImage2,
			equals(blogPostingImage1, blogPostingImage2));
	}

	protected void assertEquals(
		List<BlogPostingImage> blogPostingImages1,
		List<BlogPostingImage> blogPostingImages2) {

		Assert.assertEquals(
			blogPostingImages1.size(), blogPostingImages2.size());

		for (int i = 0; i < blogPostingImages1.size(); i++) {
			BlogPostingImage blogPostingImage1 = blogPostingImages1.get(i);
			BlogPostingImage blogPostingImage2 = blogPostingImages2.get(i);

			assertEquals(blogPostingImage1, blogPostingImage2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<BlogPostingImage> blogPostingImages1,
		List<BlogPostingImage> blogPostingImages2) {

		Assert.assertEquals(
			blogPostingImages1.size(), blogPostingImages2.size());

		for (BlogPostingImage blogPostingImage1 : blogPostingImages1) {
			boolean contains = false;

			for (BlogPostingImage blogPostingImage2 : blogPostingImages2) {
				if (equals(blogPostingImage1, blogPostingImage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				blogPostingImages2 + " does not contain " + blogPostingImage1,
				contains);
		}
	}

	protected void assertValid(BlogPostingImage blogPostingImage)
		throws Exception {

		boolean valid = true;

		if (blogPostingImage.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (blogPostingImage.getContentUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (blogPostingImage.getContentValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (blogPostingImage.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (blogPostingImage.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (blogPostingImage.getFileExtension() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (blogPostingImage.getSizeInBytes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (blogPostingImage.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (blogPostingImage.getViewableBy() == null) {
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

	protected void assertValid(
			BlogPostingImage blogPostingImage, Map<String, File> multipartFiles)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertValid(Page<BlogPostingImage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<BlogPostingImage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<BlogPostingImage> blogPostingImages =
			page.getItems();

		int size = blogPostingImages.size();

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
					com.liferay.headless.delivery.dto.v1_0.BlogPostingImage.
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
		BlogPostingImage blogPostingImage1,
		BlogPostingImage blogPostingImage2) {

		if (blogPostingImage1 == blogPostingImage2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("contentUrl", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getContentUrl(),
						blogPostingImage2.getContentUrl())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("contentValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getContentValue(),
						blogPostingImage2.getContentValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getEncodingFormat(),
						blogPostingImage2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						blogPostingImage1.getExternalReferenceCode(),
						blogPostingImage2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fileExtension", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getFileExtension(),
						blogPostingImage2.getFileExtension())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getId(), blogPostingImage2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sizeInBytes", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getSizeInBytes(),
						blogPostingImage2.getSizeInBytes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getTitle(),
						blogPostingImage2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						blogPostingImage1.getViewableBy(),
						blogPostingImage2.getViewableBy())) {

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

		if (!(_blogPostingImageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_blogPostingImageResource;

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
		BlogPostingImage blogPostingImage) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("contentUrl")) {
			Object object = blogPostingImage.getContentUrl();

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

		if (entityFieldName.equals("contentValue")) {
			Object object = blogPostingImage.getContentValue();

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
			Object object = blogPostingImage.getEncodingFormat();

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
			Object object = blogPostingImage.getExternalReferenceCode();

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

		if (entityFieldName.equals("fileExtension")) {
			Object object = blogPostingImage.getFileExtension();

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

		if (entityFieldName.equals("sizeInBytes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = blogPostingImage.getTitle();

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

		if (entityFieldName.equals("viewableBy")) {
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

	protected BlogPostingImage randomBlogPostingImage() throws Exception {
		return new BlogPostingImage() {
			{
				contentUrl = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				contentValue = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fileExtension = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				sizeInBytes = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected BlogPostingImage randomIrrelevantBlogPostingImage()
		throws Exception {

		BlogPostingImage randomIrrelevantBlogPostingImage =
			randomBlogPostingImage();

		return randomIrrelevantBlogPostingImage;
	}

	protected BlogPostingImage randomPatchBlogPostingImage() throws Exception {
		return randomBlogPostingImage();
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

	protected BlogPostingImageResource blogPostingImageResource;
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
		LogFactoryUtil.getLog(BaseBlogPostingImageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.BlogPostingImageResource
		_blogPostingImageResource;

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