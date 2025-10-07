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
import com.liferay.headless.delivery.client.dto.v1_0.Comment;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.CommentResource;
import com.liferay.headless.delivery.client.serdes.v1_0.CommentSerDes;
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
public abstract class BaseCommentResourceTestCase {

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

		_commentResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		commentResource = CommentResource.builder(
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

		Comment comment1 = randomComment();

		String json = objectMapper.writeValueAsString(comment1);

		Comment comment2 = CommentSerDes.toDTO(json);

		Assert.assertTrue(equals(comment1, comment2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Comment comment = randomComment();

		String json1 = objectMapper.writeValueAsString(comment);
		String json2 = CommentSerDes.toJSON(comment);

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

		Comment comment = randomComment();

		comment.setExternalReferenceCode(regex);
		comment.setText(regex);

		String json = CommentSerDes.toJSON(comment);

		Assert.assertFalse(json.contains(regex));

		comment = CommentSerDes.toDTO(json);

		Assert.assertEquals(regex, comment.getExternalReferenceCode());
		Assert.assertEquals(regex, comment.getText());
	}

	@Test
	public void testDeleteComment() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment = testDeleteComment_addComment();

		assertHttpResponseStatusCode(
			204, commentResource.deleteCommentHttpResponse(comment.getId()));

		assertHttpResponseStatusCode(
			404, commentResource.getCommentHttpResponse(comment.getId()));
		assertHttpResponseStatusCode(
			404, commentResource.getCommentHttpResponse(0L));
	}

	protected Comment testDeleteComment_addComment() throws Exception {
		return testPostCommentComment_addComment(randomComment());
	}

	@Test
	public void testGraphQLDeleteComment() throws Exception {

		// No namespace

		Comment comment1 = testGraphQLDeleteComment_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteComment",
						new HashMap<String, Object>() {
							{
								put("commentId", comment1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteComment"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"comment",
					new HashMap<String, Object>() {
						{
							put("commentId", comment1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Comment comment2 = testGraphQLDeleteComment_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteComment",
							new HashMap<String, Object>() {
								{
									put("commentId", comment2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteComment"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"comment",
						new HashMap<String, Object>() {
							{
								put("commentId", comment2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Comment testGraphQLDeleteComment_addComment() throws Exception {
		return testGraphQLComment_addComment();
	}

	@Test
	public void testDeleteCommentBatch() throws Exception {
		Comment comment1 = testDeleteCommentBatch_addComment();

		testDeleteCommentBatch_deleteComment(202, null, comment1.getId());

		assertHttpResponseStatusCode(
			404, commentResource.getCommentHttpResponse(comment1.getId()));
	}

	protected Comment testDeleteCommentBatch_addComment() throws Exception {
		return testDeleteComment_addComment();
	}

	protected void testDeleteCommentBatch_deleteComment(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			commentResource.deleteCommentBatchHttpResponse(
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
	public void testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment =
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			204,
			commentResource.
				deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					comment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					comment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					"-"));
	}

	protected Comment
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Comment comment1 =
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"blogPostingExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
										"\"");

							put(
								"blogPostingExternalReferenceCode",
								"\"" +
									testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" + comment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Comment comment2 =
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
												"\"");

									put(
										"blogPostingExternalReferenceCode",
										"\"" +
											testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											comment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"blogPostingExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment =
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			204,
			commentResource.
				deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					comment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					comment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					"-"));
	}

	protected Comment
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Comment comment1 =
			testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"parentCommentExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
										"\"");

							put(
								"parentCommentExternalReferenceCode",
								"\"" +
									testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" + comment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Comment comment2 =
			testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
												"\"");

									put(
										"parentCommentExternalReferenceCode",
										"\"" +
											testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											comment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"parentCommentExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment =
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			204,
			commentResource.
				deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					comment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					comment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					"-"));
	}

	protected Comment
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Comment comment1 =
			testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"documentExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
										"\"");

							put(
								"documentExternalReferenceCode",
								"\"" +
									testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" + comment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Comment comment2 =
			testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
												"\"");

									put(
										"documentExternalReferenceCode",
										"\"" +
											testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											comment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"documentExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment =
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			204,
			commentResource.
				deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					comment.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					comment.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					"-"));
	}

	protected Comment
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		// No namespace

		Comment comment1 =
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"structuredContentExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" +
									testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
										"\"");

							put(
								"structuredContentExternalReferenceCode",
								"\"" +
									testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" + comment1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		Comment comment2 =
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" +
											testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
												"\"");

									put(
										"structuredContentExternalReferenceCode",
										"\"" +
											testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											comment2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" +
										testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
											"\"");

								put(
									"structuredContentExternalReferenceCode",
									"\"" +
										testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" + comment2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testGetBlogPostingCommentsPage() throws Exception {
		Long blogPostingId = testGetBlogPostingCommentsPage_getBlogPostingId();
		Long irrelevantBlogPostingId =
			testGetBlogPostingCommentsPage_getIrrelevantBlogPostingId();

		Page<Comment> page = commentResource.getBlogPostingCommentsPage(
			blogPostingId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantBlogPostingId != null) {
			Comment irrelevantComment =
				testGetBlogPostingCommentsPage_addComment(
					irrelevantBlogPostingId, randomIrrelevantComment());

			page = commentResource.getBlogPostingCommentsPage(
				irrelevantBlogPostingId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantComment, (List<Comment>)page.getItems());
			assertValid(
				page,
				testGetBlogPostingCommentsPage_getExpectedActions(
					irrelevantBlogPostingId));
		}

		Comment comment1 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, randomComment());

		Comment comment2 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, randomComment());

		page = commentResource.getBlogPostingCommentsPage(
			blogPostingId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(comment1, (List<Comment>)page.getItems());
		assertContains(comment2, (List<Comment>)page.getItems());
		assertValid(
			page,
			testGetBlogPostingCommentsPage_getExpectedActions(blogPostingId));

		commentResource.deleteComment(comment1.getId());

		commentResource.deleteComment(comment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetBlogPostingCommentsPage_getExpectedActions(
				Long blogPostingId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/blog-postings/{blogPostingId}/comments/batch".
				replace("{blogPostingId}", String.valueOf(blogPostingId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetBlogPostingCommentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long blogPostingId = testGetBlogPostingCommentsPage_getBlogPostingId();

		Comment comment1 = randomComment();

		comment1 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, comment1);

		for (EntityField entityField : entityFields) {
			Page<Comment> page = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null,
				getFilterString(entityField, "between", comment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetBlogPostingCommentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetBlogPostingCommentsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetBlogPostingCommentsPageWithFilterStringContains()
		throws Exception {

		testGetBlogPostingCommentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetBlogPostingCommentsPageWithFilterStringEquals()
		throws Exception {

		testGetBlogPostingCommentsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetBlogPostingCommentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetBlogPostingCommentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetBlogPostingCommentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long blogPostingId = testGetBlogPostingCommentsPage_getBlogPostingId();

		Comment comment1 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, randomComment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment2 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, randomComment());

		for (EntityField entityField : entityFields) {
			Page<Comment> page = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null,
				getFilterString(entityField, operator, comment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetBlogPostingCommentsPageWithPagination()
		throws Exception {

		Long blogPostingId = testGetBlogPostingCommentsPage_getBlogPostingId();

		Page<Comment> commentsPage = commentResource.getBlogPostingCommentsPage(
			blogPostingId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(commentsPage.getTotalCount());

		Comment comment1 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, randomComment());

		Comment comment2 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, randomComment());

		Comment comment3 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, randomComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Comment> page1 = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(comment1, (List<Comment>)page1.getItems());

			Page<Comment> page2 = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(comment2, (List<Comment>)page2.getItems());

			Page<Comment> page3 = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(comment3, (List<Comment>)page3.getItems());
		}
		else {
			Page<Comment> page1 = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Comment> comments1 = (List<Comment>)page1.getItems();

			Assert.assertEquals(
				comments1.toString(), totalCount + 2, comments1.size());

			Page<Comment> page2 = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Comment> comments2 = (List<Comment>)page2.getItems();

			Assert.assertEquals(comments2.toString(), 1, comments2.size());

			Page<Comment> page3 = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(comment1, (List<Comment>)page3.getItems());
			assertContains(comment2, (List<Comment>)page3.getItems());
			assertContains(comment3, (List<Comment>)page3.getItems());
		}
	}

	@Test
	public void testGetBlogPostingCommentsPageWithSortDateTime()
		throws Exception {

		testGetBlogPostingCommentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(
					comment1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetBlogPostingCommentsPageWithSortDouble()
		throws Exception {

		testGetBlogPostingCommentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetBlogPostingCommentsPageWithSortInteger()
		throws Exception {

		testGetBlogPostingCommentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetBlogPostingCommentsPageWithSortString()
		throws Exception {

		testGetBlogPostingCommentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, comment1, comment2) -> {
				Class<?> clazz = comment1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetBlogPostingCommentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Comment, Comment, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long blogPostingId = testGetBlogPostingCommentsPage_getBlogPostingId();

		Comment comment1 = randomComment();
		Comment comment2 = randomComment();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, comment1, comment2);
		}

		comment1 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, comment1);

		comment2 = testGetBlogPostingCommentsPage_addComment(
			blogPostingId, comment2);

		Page<Comment> page = commentResource.getBlogPostingCommentsPage(
			blogPostingId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Comment> ascPage = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(comment1, (List<Comment>)ascPage.getItems());
			assertContains(comment2, (List<Comment>)ascPage.getItems());

			Page<Comment> descPage = commentResource.getBlogPostingCommentsPage(
				blogPostingId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(comment2, (List<Comment>)descPage.getItems());
			assertContains(comment1, (List<Comment>)descPage.getItems());
		}
	}

	protected Comment testGetBlogPostingCommentsPage_addComment(
			Long blogPostingId, Comment comment)
		throws Exception {

		return commentResource.postBlogPostingComment(blogPostingId, comment);
	}

	protected Long testGetBlogPostingCommentsPage_getBlogPostingId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetBlogPostingCommentsPage_getIrrelevantBlogPostingId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetBlogPostingCommentsPage() throws Exception {
		Long blogPostingId = testGetBlogPostingCommentsPage_getBlogPostingId();

		GraphQLField graphQLField = new GraphQLField(
			"blogPostingComments",
			new HashMap<String, Object>() {
				{
					put("blogPostingId", blogPostingId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject blogPostingCommentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/blogPostingComments");

		long totalCount = blogPostingCommentsJSONObject.getLong("totalCount");

		Comment comment1 = testGraphQLBlogPostingComment_addComment(
			blogPostingId, randomComment());

		Comment comment2 = testGraphQLBlogPostingComment_addComment(
			blogPostingId, randomComment());

		blogPostingCommentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/blogPostingComments");

		Assert.assertEquals(
			totalCount + 2,
			blogPostingCommentsJSONObject.getLong("totalCount"));

		assertContains(
			comment1,
			Arrays.asList(
				CommentSerDes.toDTOs(
					blogPostingCommentsJSONObject.getString("items"))));
		assertContains(
			comment2,
			Arrays.asList(
				CommentSerDes.toDTOs(
					blogPostingCommentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		blogPostingCommentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/blogPostingComments");

		Assert.assertEquals(
			totalCount + 2,
			blogPostingCommentsJSONObject.getLong("totalCount"));

		assertContains(
			comment1,
			Arrays.asList(
				CommentSerDes.toDTOs(
					blogPostingCommentsJSONObject.getString("items"))));
		assertContains(
			comment2,
			Arrays.asList(
				CommentSerDes.toDTOs(
					blogPostingCommentsJSONObject.getString("items"))));
	}

	@Test
	public void testGetComment() throws Exception {
		Comment postComment = testGetComment_addComment();

		Comment getComment = commentResource.getComment(postComment.getId());

		assertEquals(postComment, getComment);
		assertValid(getComment);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Comment postComment = testGetComment_addComment();

		Comment getComment = commentResource.getComment(postComment.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.delivery.dto.v1_0.Comment"
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

		Object item = vulcanCRUDItemDelegate.getItem(postComment.getId());

		assertEquals(getComment, CommentSerDes.toDTO(item.toString()));
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

	protected Comment testGetComment_addComment() throws Exception {
		return testPostCommentComment_addComment(randomComment());
	}

	@Test
	public void testGraphQLGetComment() throws Exception {
		Comment comment = testGraphQLGetComment_addComment();

		// No namespace

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"comment",
								new HashMap<String, Object>() {
									{
										put("commentId", comment.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/comment"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"comment",
									new HashMap<String, Object>() {
										{
											put("commentId", comment.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/comment"))));
	}

	@Test
	public void testGraphQLGetCommentNotFound() throws Exception {
		Long irrelevantCommentId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"comment",
						new HashMap<String, Object>() {
							{
								put("commentId", irrelevantCommentId);
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
							"comment",
							new HashMap<String, Object>() {
								{
									put("commentId", irrelevantCommentId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Comment testGraphQLGetComment_addComment() throws Exception {
		return testGraphQLComment_addComment();
	}

	@Test
	public void testGetCommentCommentsPage() throws Exception {
		Long parentCommentId = testGetCommentCommentsPage_getParentCommentId();
		Long irrelevantParentCommentId =
			testGetCommentCommentsPage_getIrrelevantParentCommentId();

		Page<Comment> page = commentResource.getCommentCommentsPage(
			parentCommentId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantParentCommentId != null) {
			Comment irrelevantComment = testGetCommentCommentsPage_addComment(
				irrelevantParentCommentId, randomIrrelevantComment());

			page = commentResource.getCommentCommentsPage(
				irrelevantParentCommentId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantComment, (List<Comment>)page.getItems());
			assertValid(
				page,
				testGetCommentCommentsPage_getExpectedActions(
					irrelevantParentCommentId));
		}

		Comment comment1 = testGetCommentCommentsPage_addComment(
			parentCommentId, randomComment());

		Comment comment2 = testGetCommentCommentsPage_addComment(
			parentCommentId, randomComment());

		page = commentResource.getCommentCommentsPage(
			parentCommentId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(comment1, (List<Comment>)page.getItems());
		assertContains(comment2, (List<Comment>)page.getItems());
		assertValid(
			page,
			testGetCommentCommentsPage_getExpectedActions(parentCommentId));

		commentResource.deleteComment(comment1.getId());

		commentResource.deleteComment(comment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetCommentCommentsPage_getExpectedActions(Long parentCommentId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetCommentCommentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentCommentId = testGetCommentCommentsPage_getParentCommentId();

		Comment comment1 = randomComment();

		comment1 = testGetCommentCommentsPage_addComment(
			parentCommentId, comment1);

		for (EntityField entityField : entityFields) {
			Page<Comment> page = commentResource.getCommentCommentsPage(
				parentCommentId, null, null,
				getFilterString(entityField, "between", comment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetCommentCommentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetCommentCommentsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetCommentCommentsPageWithFilterStringContains()
		throws Exception {

		testGetCommentCommentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetCommentCommentsPageWithFilterStringEquals()
		throws Exception {

		testGetCommentCommentsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetCommentCommentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetCommentCommentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetCommentCommentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentCommentId = testGetCommentCommentsPage_getParentCommentId();

		Comment comment1 = testGetCommentCommentsPage_addComment(
			parentCommentId, randomComment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment2 = testGetCommentCommentsPage_addComment(
			parentCommentId, randomComment());

		for (EntityField entityField : entityFields) {
			Page<Comment> page = commentResource.getCommentCommentsPage(
				parentCommentId, null, null,
				getFilterString(entityField, operator, comment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetCommentCommentsPageWithPagination() throws Exception {
		Long parentCommentId = testGetCommentCommentsPage_getParentCommentId();

		Page<Comment> commentsPage = commentResource.getCommentCommentsPage(
			parentCommentId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(commentsPage.getTotalCount());

		Comment comment1 = testGetCommentCommentsPage_addComment(
			parentCommentId, randomComment());

		Comment comment2 = testGetCommentCommentsPage_addComment(
			parentCommentId, randomComment());

		Comment comment3 = testGetCommentCommentsPage_addComment(
			parentCommentId, randomComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Comment> page1 = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(comment1, (List<Comment>)page1.getItems());

			Page<Comment> page2 = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(comment2, (List<Comment>)page2.getItems());

			Page<Comment> page3 = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(comment3, (List<Comment>)page3.getItems());
		}
		else {
			Page<Comment> page1 = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Comment> comments1 = (List<Comment>)page1.getItems();

			Assert.assertEquals(
				comments1.toString(), totalCount + 2, comments1.size());

			Page<Comment> page2 = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Comment> comments2 = (List<Comment>)page2.getItems();

			Assert.assertEquals(comments2.toString(), 1, comments2.size());

			Page<Comment> page3 = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(comment1, (List<Comment>)page3.getItems());
			assertContains(comment2, (List<Comment>)page3.getItems());
			assertContains(comment3, (List<Comment>)page3.getItems());
		}
	}

	@Test
	public void testGetCommentCommentsPageWithSortDateTime() throws Exception {
		testGetCommentCommentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(
					comment1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetCommentCommentsPageWithSortDouble() throws Exception {
		testGetCommentCommentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetCommentCommentsPageWithSortInteger() throws Exception {
		testGetCommentCommentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetCommentCommentsPageWithSortString() throws Exception {
		testGetCommentCommentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, comment1, comment2) -> {
				Class<?> clazz = comment1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetCommentCommentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Comment, Comment, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentCommentId = testGetCommentCommentsPage_getParentCommentId();

		Comment comment1 = randomComment();
		Comment comment2 = randomComment();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, comment1, comment2);
		}

		comment1 = testGetCommentCommentsPage_addComment(
			parentCommentId, comment1);

		comment2 = testGetCommentCommentsPage_addComment(
			parentCommentId, comment2);

		Page<Comment> page = commentResource.getCommentCommentsPage(
			parentCommentId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Comment> ascPage = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(comment1, (List<Comment>)ascPage.getItems());
			assertContains(comment2, (List<Comment>)ascPage.getItems());

			Page<Comment> descPage = commentResource.getCommentCommentsPage(
				parentCommentId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(comment2, (List<Comment>)descPage.getItems());
			assertContains(comment1, (List<Comment>)descPage.getItems());
		}
	}

	protected Comment testGetCommentCommentsPage_addComment(
			Long parentCommentId, Comment comment)
		throws Exception {

		return commentResource.postCommentComment(parentCommentId, comment);
	}

	protected Long testGetCommentCommentsPage_getParentCommentId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetCommentCommentsPage_getIrrelevantParentCommentId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetDocumentCommentsPage() throws Exception {
		Long documentId = testGetDocumentCommentsPage_getDocumentId();
		Long irrelevantDocumentId =
			testGetDocumentCommentsPage_getIrrelevantDocumentId();

		Page<Comment> page = commentResource.getDocumentCommentsPage(
			documentId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantDocumentId != null) {
			Comment irrelevantComment = testGetDocumentCommentsPage_addComment(
				irrelevantDocumentId, randomIrrelevantComment());

			page = commentResource.getDocumentCommentsPage(
				irrelevantDocumentId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantComment, (List<Comment>)page.getItems());
			assertValid(
				page,
				testGetDocumentCommentsPage_getExpectedActions(
					irrelevantDocumentId));
		}

		Comment comment1 = testGetDocumentCommentsPage_addComment(
			documentId, randomComment());

		Comment comment2 = testGetDocumentCommentsPage_addComment(
			documentId, randomComment());

		page = commentResource.getDocumentCommentsPage(
			documentId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(comment1, (List<Comment>)page.getItems());
		assertContains(comment2, (List<Comment>)page.getItems());
		assertValid(
			page, testGetDocumentCommentsPage_getExpectedActions(documentId));

		commentResource.deleteComment(comment1.getId());

		commentResource.deleteComment(comment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDocumentCommentsPage_getExpectedActions(Long documentId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/documents/{documentId}/comments/batch".
				replace("{documentId}", String.valueOf(documentId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetDocumentCommentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long documentId = testGetDocumentCommentsPage_getDocumentId();

		Comment comment1 = randomComment();

		comment1 = testGetDocumentCommentsPage_addComment(documentId, comment1);

		for (EntityField entityField : entityFields) {
			Page<Comment> page = commentResource.getDocumentCommentsPage(
				documentId, null, null,
				getFilterString(entityField, "between", comment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetDocumentCommentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetDocumentCommentsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetDocumentCommentsPageWithFilterStringContains()
		throws Exception {

		testGetDocumentCommentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetDocumentCommentsPageWithFilterStringEquals()
		throws Exception {

		testGetDocumentCommentsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetDocumentCommentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetDocumentCommentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetDocumentCommentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long documentId = testGetDocumentCommentsPage_getDocumentId();

		Comment comment1 = testGetDocumentCommentsPage_addComment(
			documentId, randomComment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment2 = testGetDocumentCommentsPage_addComment(
			documentId, randomComment());

		for (EntityField entityField : entityFields) {
			Page<Comment> page = commentResource.getDocumentCommentsPage(
				documentId, null, null,
				getFilterString(entityField, operator, comment1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetDocumentCommentsPageWithPagination() throws Exception {
		Long documentId = testGetDocumentCommentsPage_getDocumentId();

		Page<Comment> commentsPage = commentResource.getDocumentCommentsPage(
			documentId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(commentsPage.getTotalCount());

		Comment comment1 = testGetDocumentCommentsPage_addComment(
			documentId, randomComment());

		Comment comment2 = testGetDocumentCommentsPage_addComment(
			documentId, randomComment());

		Comment comment3 = testGetDocumentCommentsPage_addComment(
			documentId, randomComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Comment> page1 = commentResource.getDocumentCommentsPage(
				documentId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(comment1, (List<Comment>)page1.getItems());

			Page<Comment> page2 = commentResource.getDocumentCommentsPage(
				documentId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(comment2, (List<Comment>)page2.getItems());

			Page<Comment> page3 = commentResource.getDocumentCommentsPage(
				documentId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(comment3, (List<Comment>)page3.getItems());
		}
		else {
			Page<Comment> page1 = commentResource.getDocumentCommentsPage(
				documentId, null, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<Comment> comments1 = (List<Comment>)page1.getItems();

			Assert.assertEquals(
				comments1.toString(), totalCount + 2, comments1.size());

			Page<Comment> page2 = commentResource.getDocumentCommentsPage(
				documentId, null, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Comment> comments2 = (List<Comment>)page2.getItems();

			Assert.assertEquals(comments2.toString(), 1, comments2.size());

			Page<Comment> page3 = commentResource.getDocumentCommentsPage(
				documentId, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(comment1, (List<Comment>)page3.getItems());
			assertContains(comment2, (List<Comment>)page3.getItems());
			assertContains(comment3, (List<Comment>)page3.getItems());
		}
	}

	@Test
	public void testGetDocumentCommentsPageWithSortDateTime() throws Exception {
		testGetDocumentCommentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(
					comment1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDocumentCommentsPageWithSortDouble() throws Exception {
		testGetDocumentCommentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDocumentCommentsPageWithSortInteger() throws Exception {
		testGetDocumentCommentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDocumentCommentsPageWithSortString() throws Exception {
		testGetDocumentCommentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, comment1, comment2) -> {
				Class<?> clazz = comment1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDocumentCommentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Comment, Comment, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long documentId = testGetDocumentCommentsPage_getDocumentId();

		Comment comment1 = randomComment();
		Comment comment2 = randomComment();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, comment1, comment2);
		}

		comment1 = testGetDocumentCommentsPage_addComment(documentId, comment1);

		comment2 = testGetDocumentCommentsPage_addComment(documentId, comment2);

		Page<Comment> page = commentResource.getDocumentCommentsPage(
			documentId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Comment> ascPage = commentResource.getDocumentCommentsPage(
				documentId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(comment1, (List<Comment>)ascPage.getItems());
			assertContains(comment2, (List<Comment>)ascPage.getItems());

			Page<Comment> descPage = commentResource.getDocumentCommentsPage(
				documentId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(comment2, (List<Comment>)descPage.getItems());
			assertContains(comment1, (List<Comment>)descPage.getItems());
		}
	}

	protected Comment testGetDocumentCommentsPage_addComment(
			Long documentId, Comment comment)
		throws Exception {

		return commentResource.postDocumentComment(documentId, comment);
	}

	protected Long testGetDocumentCommentsPage_getDocumentId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetDocumentCommentsPage_getIrrelevantDocumentId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetDocumentCommentsPage() throws Exception {
		Long documentId = testGetDocumentCommentsPage_getDocumentId();

		GraphQLField graphQLField = new GraphQLField(
			"documentComments",
			new HashMap<String, Object>() {
				{
					put("documentId", documentId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject documentCommentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentComments");

		long totalCount = documentCommentsJSONObject.getLong("totalCount");

		Comment comment1 = testGraphQLDocumentComment_addComment(
			documentId, randomComment());

		Comment comment2 = testGraphQLDocumentComment_addComment(
			documentId, randomComment());

		documentCommentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentComments");

		Assert.assertEquals(
			totalCount + 2, documentCommentsJSONObject.getLong("totalCount"));

		assertContains(
			comment1,
			Arrays.asList(
				CommentSerDes.toDTOs(
					documentCommentsJSONObject.getString("items"))));
		assertContains(
			comment2,
			Arrays.asList(
				CommentSerDes.toDTOs(
					documentCommentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentCommentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentComments");

		Assert.assertEquals(
			totalCount + 2, documentCommentsJSONObject.getLong("totalCount"));

		assertContains(
			comment1,
			Arrays.asList(
				CommentSerDes.toDTOs(
					documentCommentsJSONObject.getString("items"))));
		assertContains(
			comment2,
			Arrays.asList(
				CommentSerDes.toDTOs(
					documentCommentsJSONObject.getString("items"))));
	}

	@Test
	public void testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment getComment =
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
					testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					postComment.getExternalReferenceCode());

		assertEquals(postComment, getComment);
		assertValid(getComment);
	}

	protected Comment
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment comment =
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// No namespace

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
													"\"");

										put(
											"blogPostingExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												comment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
														"\"");

											put(
												"blogPostingExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													comment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantBlogPostingExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"blogPostingExternalReferenceCode",
									irrelevantBlogPostingExternalReferenceCode);
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
							"blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"blogPostingExternalReferenceCode",
										irrelevantBlogPostingExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Comment
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment getComment =
			commentResource.
				getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
					testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					postComment.getExternalReferenceCode());

		assertEquals(postComment, getComment);
		assertValid(getComment);
	}

	protected Comment
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment comment =
			testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// No namespace

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
													"\"");

										put(
											"parentCommentExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												comment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
														"\"");

											put(
												"parentCommentExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													comment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantParentCommentExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"parentCommentExternalReferenceCode",
									irrelevantParentCommentExternalReferenceCode);
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
							"commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"parentCommentExternalReferenceCode",
										irrelevantParentCommentExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Comment
			testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment getComment =
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
					testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					postComment.getExternalReferenceCode());

		assertEquals(postComment, getComment);
		assertValid(getComment);
	}

	protected Comment
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment comment =
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// No namespace

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
													"\"");

										put(
											"documentExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												comment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
														"\"");

											put(
												"documentExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													comment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantDocumentExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"documentExternalReferenceCode",
									irrelevantDocumentExternalReferenceCode);
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
							"documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"documentExternalReferenceCode",
										irrelevantDocumentExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Comment
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment getComment =
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
					testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					postComment.getExternalReferenceCode());

		assertEquals(postComment, getComment);
		assertValid(getComment);
	}

	protected Comment
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment comment =
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// No namespace

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
													"\"");

										put(
											"structuredContentExternalReferenceCode",
											"\"" +
												testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												comment.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				comment,
				CommentSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
														"\"");

											put(
												"structuredContentExternalReferenceCode",
												"\"" +
													testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													comment.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantStructuredContentExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"structuredContentExternalReferenceCode",
									irrelevantStructuredContentExternalReferenceCode);
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
							"structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"structuredContentExternalReferenceCode",
										irrelevantStructuredContentExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Comment
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return testGraphQLSiteComment_addComment();
	}

	@Test
	public void testGetStructuredContentCommentsPage() throws Exception {
		Long structuredContentId =
			testGetStructuredContentCommentsPage_getStructuredContentId();
		Long irrelevantStructuredContentId =
			testGetStructuredContentCommentsPage_getIrrelevantStructuredContentId();

		Page<Comment> page = commentResource.getStructuredContentCommentsPage(
			structuredContentId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantStructuredContentId != null) {
			Comment irrelevantComment =
				testGetStructuredContentCommentsPage_addComment(
					irrelevantStructuredContentId, randomIrrelevantComment());

			page = commentResource.getStructuredContentCommentsPage(
				irrelevantStructuredContentId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantComment, (List<Comment>)page.getItems());
			assertValid(
				page,
				testGetStructuredContentCommentsPage_getExpectedActions(
					irrelevantStructuredContentId));
		}

		Comment comment1 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, randomComment());

		Comment comment2 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, randomComment());

		page = commentResource.getStructuredContentCommentsPage(
			structuredContentId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(comment1, (List<Comment>)page.getItems());
		assertContains(comment2, (List<Comment>)page.getItems());
		assertValid(
			page,
			testGetStructuredContentCommentsPage_getExpectedActions(
				structuredContentId));

		commentResource.deleteComment(comment1.getId());

		commentResource.deleteComment(comment2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetStructuredContentCommentsPage_getExpectedActions(
				Long structuredContentId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/structured-contents/{structuredContentId}/comments/batch".
				replace(
					"{structuredContentId}",
					String.valueOf(structuredContentId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetStructuredContentCommentsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long structuredContentId =
			testGetStructuredContentCommentsPage_getStructuredContentId();

		Comment comment1 = randomComment();

		comment1 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, comment1);

		for (EntityField entityField : entityFields) {
			Page<Comment> page =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null,
					getFilterString(entityField, "between", comment1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetStructuredContentCommentsPageWithFilterDoubleEquals()
		throws Exception {

		testGetStructuredContentCommentsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetStructuredContentCommentsPageWithFilterStringContains()
		throws Exception {

		testGetStructuredContentCommentsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetStructuredContentCommentsPageWithFilterStringEquals()
		throws Exception {

		testGetStructuredContentCommentsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetStructuredContentCommentsPageWithFilterStringStartsWith()
		throws Exception {

		testGetStructuredContentCommentsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetStructuredContentCommentsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long structuredContentId =
			testGetStructuredContentCommentsPage_getStructuredContentId();

		Comment comment1 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, randomComment());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment comment2 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, randomComment());

		for (EntityField entityField : entityFields) {
			Page<Comment> page =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null,
					getFilterString(entityField, operator, comment1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(comment1),
				(List<Comment>)page.getItems());
		}
	}

	@Test
	public void testGetStructuredContentCommentsPageWithPagination()
		throws Exception {

		Long structuredContentId =
			testGetStructuredContentCommentsPage_getStructuredContentId();

		Page<Comment> commentsPage =
			commentResource.getStructuredContentCommentsPage(
				structuredContentId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(commentsPage.getTotalCount());

		Comment comment1 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, randomComment());

		Comment comment2 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, randomComment());

		Comment comment3 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, randomComment());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Comment> page1 =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(comment1, (List<Comment>)page1.getItems());

			Page<Comment> page2 =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(comment2, (List<Comment>)page2.getItems());

			Page<Comment> page3 =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(comment3, (List<Comment>)page3.getItems());
		}
		else {
			Page<Comment> page1 =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<Comment> comments1 = (List<Comment>)page1.getItems();

			Assert.assertEquals(
				comments1.toString(), totalCount + 2, comments1.size());

			Page<Comment> page2 =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Comment> comments2 = (List<Comment>)page2.getItems();

			Assert.assertEquals(comments2.toString(), 1, comments2.size());

			Page<Comment> page3 =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(comment1, (List<Comment>)page3.getItems());
			assertContains(comment2, (List<Comment>)page3.getItems());
			assertContains(comment3, (List<Comment>)page3.getItems());
		}
	}

	@Test
	public void testGetStructuredContentCommentsPageWithSortDateTime()
		throws Exception {

		testGetStructuredContentCommentsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(
					comment1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetStructuredContentCommentsPageWithSortDouble()
		throws Exception {

		testGetStructuredContentCommentsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetStructuredContentCommentsPageWithSortInteger()
		throws Exception {

		testGetStructuredContentCommentsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, comment1, comment2) -> {
				BeanTestUtil.setProperty(comment1, entityField.getName(), 0);
				BeanTestUtil.setProperty(comment2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetStructuredContentCommentsPageWithSortString()
		throws Exception {

		testGetStructuredContentCommentsPageWithSort(
			EntityField.Type.STRING,
			(entityField, comment1, comment2) -> {
				Class<?> clazz = comment1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						comment1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						comment2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetStructuredContentCommentsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Comment, Comment, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long structuredContentId =
			testGetStructuredContentCommentsPage_getStructuredContentId();

		Comment comment1 = randomComment();
		Comment comment2 = randomComment();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, comment1, comment2);
		}

		comment1 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, comment1);

		comment2 = testGetStructuredContentCommentsPage_addComment(
			structuredContentId, comment2);

		Page<Comment> page = commentResource.getStructuredContentCommentsPage(
			structuredContentId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Comment> ascPage =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(comment1, (List<Comment>)ascPage.getItems());
			assertContains(comment2, (List<Comment>)ascPage.getItems());

			Page<Comment> descPage =
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(comment2, (List<Comment>)descPage.getItems());
			assertContains(comment1, (List<Comment>)descPage.getItems());
		}
	}

	protected Comment testGetStructuredContentCommentsPage_addComment(
			Long structuredContentId, Comment comment)
		throws Exception {

		return commentResource.postStructuredContentComment(
			structuredContentId, comment);
	}

	protected Long testGetStructuredContentCommentsPage_getStructuredContentId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetStructuredContentCommentsPage_getIrrelevantStructuredContentId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetStructuredContentCommentsPage() throws Exception {
		Long structuredContentId =
			testGetStructuredContentCommentsPage_getStructuredContentId();

		GraphQLField graphQLField = new GraphQLField(
			"structuredContentComments",
			new HashMap<String, Object>() {
				{
					put("structuredContentId", structuredContentId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject structuredContentCommentsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/structuredContentComments");

		long totalCount = structuredContentCommentsJSONObject.getLong(
			"totalCount");

		Comment comment1 = testGraphQLStructuredContentComment_addComment(
			structuredContentId, randomComment());

		Comment comment2 = testGraphQLStructuredContentComment_addComment(
			structuredContentId, randomComment());

		structuredContentCommentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/structuredContentComments");

		Assert.assertEquals(
			totalCount + 2,
			structuredContentCommentsJSONObject.getLong("totalCount"));

		assertContains(
			comment1,
			Arrays.asList(
				CommentSerDes.toDTOs(
					structuredContentCommentsJSONObject.getString("items"))));
		assertContains(
			comment2,
			Arrays.asList(
				CommentSerDes.toDTOs(
					structuredContentCommentsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		structuredContentCommentsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/structuredContentComments");

		Assert.assertEquals(
			totalCount + 2,
			structuredContentCommentsJSONObject.getLong("totalCount"));

		assertContains(
			comment1,
			Arrays.asList(
				CommentSerDes.toDTOs(
					structuredContentCommentsJSONObject.getString("items"))));
		assertContains(
			comment2,
			Arrays.asList(
				CommentSerDes.toDTOs(
					structuredContentCommentsJSONObject.getString("items"))));
	}

	@Test
	public void testPostBlogPostingComment() throws Exception {
		Comment randomComment = randomComment();

		Comment postComment = testPostBlogPostingComment_addComment(
			randomComment);

		assertEquals(randomComment, postComment);
		assertValid(postComment);
	}

	protected Comment testPostBlogPostingComment_addComment(Comment comment)
		throws Exception {

		return commentResource.postBlogPostingComment(
			testGetBlogPostingCommentsPage_getBlogPostingId(), comment);
	}

	@Test
	public void testGraphQLPostBlogPostingComment() throws Exception {
		Comment randomComment = randomComment();

		Comment comment = testGraphQLBlogPostingComment_addComment(
			testGraphQLPostBlogPostingComment_getBlogPostingId(),
			randomComment);

		Assert.assertTrue(equals(randomComment, comment));
	}

	protected Long testGraphQLPostBlogPostingComment_getBlogPostingId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostCommentComment() throws Exception {
		Comment randomComment = randomComment();

		Comment postComment = testPostCommentComment_addComment(randomComment);

		assertEquals(randomComment, postComment);
		assertValid(postComment);
	}

	protected Comment testPostCommentComment_addComment(Comment comment)
		throws Exception {

		return commentResource.postCommentComment(
			testGetCommentCommentsPage_getParentCommentId(), comment);
	}

	@Test
	public void testPostDocumentComment() throws Exception {
		Comment randomComment = randomComment();

		Comment postComment = testPostDocumentComment_addComment(randomComment);

		assertEquals(randomComment, postComment);
		assertValid(postComment);
	}

	protected Comment testPostDocumentComment_addComment(Comment comment)
		throws Exception {

		return commentResource.postDocumentComment(
			testGetDocumentCommentsPage_getDocumentId(), comment);
	}

	@Test
	public void testGraphQLPostDocumentComment() throws Exception {
		Comment randomComment = randomComment();

		Comment comment = testGraphQLDocumentComment_addComment(
			testGraphQLPostDocumentComment_getDocumentId(), randomComment);

		Assert.assertTrue(equals(randomComment, comment));
	}

	protected Long testGraphQLPostDocumentComment_getDocumentId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostStructuredContentComment() throws Exception {
		Comment randomComment = randomComment();

		Comment postComment = testPostStructuredContentComment_addComment(
			randomComment);

		assertEquals(randomComment, postComment);
		assertValid(postComment);
	}

	protected Comment testPostStructuredContentComment_addComment(
			Comment comment)
		throws Exception {

		return commentResource.postStructuredContentComment(
			testGetStructuredContentCommentsPage_getStructuredContentId(),
			comment);
	}

	@Test
	public void testGraphQLPostStructuredContentComment() throws Exception {
		Comment randomComment = randomComment();

		Comment comment = testGraphQLStructuredContentComment_addComment(
			testGraphQLPostStructuredContentComment_getStructuredContentId(),
			randomComment);

		Assert.assertTrue(equals(randomComment, comment));
	}

	protected Long
			testGraphQLPostStructuredContentComment_getStructuredContentId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutComment() throws Exception {
		Comment postComment = testPutComment_addComment();

		Comment randomComment = randomComment();

		Comment putComment = commentResource.putComment(
			postComment.getId(), randomComment);

		assertEquals(randomComment, putComment);
		assertValid(putComment);

		Comment getComment = commentResource.getComment(putComment.getId());

		assertEquals(randomComment, getComment);
		assertValid(getComment);
	}

	protected Comment testPutComment_addComment() throws Exception {
		return testPostCommentComment_addComment(randomComment());
	}

	@Test
	public void testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment randomComment = randomComment();

		Comment putComment =
			commentResource.
				putSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					postComment.getExternalReferenceCode(), randomComment);

		assertEquals(randomComment, putComment);
		assertValid(putComment);

		Comment getComment =
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(randomComment, getComment);
		assertValid(getComment);

		Comment newComment =
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_createComment();

		putComment =
			commentResource.
				putSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					newComment.getExternalReferenceCode(), newComment);

		assertEquals(newComment, putComment);
		assertValid(putComment);

		getComment =
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(newComment, getComment);

		Assert.assertEquals(
			newComment.getExternalReferenceCode(),
			putComment.getExternalReferenceCode());
	}

	protected Comment
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_createComment()
		throws Exception {

		return randomComment();
	}

	@Test
	public void testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment randomComment = randomComment();

		Comment putComment =
			commentResource.
				putSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					postComment.getExternalReferenceCode(), randomComment);

		assertEquals(randomComment, putComment);
		assertValid(putComment);

		Comment getComment =
			commentResource.
				getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(randomComment, getComment);
		assertValid(getComment);

		Comment newComment =
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_createComment();

		putComment =
			commentResource.
				putSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					newComment.getExternalReferenceCode(), newComment);

		assertEquals(newComment, putComment);
		assertValid(putComment);

		getComment =
			commentResource.
				getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(newComment, getComment);

		Assert.assertEquals(
			newComment.getExternalReferenceCode(),
			putComment.getExternalReferenceCode());
	}

	protected Comment
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_createComment()
		throws Exception {

		return randomComment();
	}

	@Test
	public void testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment randomComment = randomComment();

		Comment putComment =
			commentResource.
				putSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					postComment.getExternalReferenceCode(), randomComment);

		assertEquals(randomComment, putComment);
		assertValid(putComment);

		Comment getComment =
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(randomComment, getComment);
		assertValid(getComment);

		Comment newComment =
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_createComment();

		putComment =
			commentResource.
				putSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					newComment.getExternalReferenceCode(), newComment);

		assertEquals(newComment, putComment);
		assertValid(putComment);

		getComment =
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(newComment, getComment);

		Assert.assertEquals(
			newComment.getExternalReferenceCode(),
			putComment.getExternalReferenceCode());
	}

	protected Comment
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_createComment()
		throws Exception {

		return randomComment();
	}

	@Test
	public void testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		Comment postComment =
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment randomComment = randomComment();

		Comment putComment =
			commentResource.
				putSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					postComment.getExternalReferenceCode(), randomComment);

		assertEquals(randomComment, putComment);
		assertValid(putComment);

		Comment getComment =
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(randomComment, getComment);
		assertValid(getComment);

		Comment newComment =
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_createComment();

		putComment =
			commentResource.
				putSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					newComment.getExternalReferenceCode(), newComment);

		assertEquals(newComment, putComment);
		assertValid(putComment);

		getComment =
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					putComment.getExternalReferenceCode());

		assertEquals(newComment, getComment);

		Assert.assertEquals(
			newComment.getExternalReferenceCode(),
			putComment.getExternalReferenceCode());
	}

	protected Comment
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected String
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_createComment()
		throws Exception {

		return randomComment();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Comment comment1 = testBatchEngineDeleteImportTask_addComment();

		testBatchEngineDeleteImportTask_deleteComment(
			200, null, comment1.getId());

		assertHttpResponseStatusCode(
			404, commentResource.getCommentHttpResponse(comment1.getId()));
	}

	protected Comment testBatchEngineDeleteImportTask_addComment()
		throws Exception {

		return testDeleteComment_addComment();
	}

	protected void testBatchEngineDeleteImportTask_deleteComment(
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
				"com.liferay.headless.delivery.dto.v1_0.Comment", null, null,
				null, null,
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

	protected Comment testGraphQLComment_addComment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment testGraphQLSiteComment_addComment() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment testGraphQLBlogPostingComment_addComment()
		throws Exception {

		return testGraphQLBlogPostingComment_addComment(
			testGraphQLBlogPostingComment_getBlogPostingId(), randomComment());
	}

	protected Long testGraphQLBlogPostingComment_getBlogPostingId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment testGraphQLBlogPostingComment_addComment(
			Long blogPostingId, Comment comment)
		throws Exception {

		JSONDeserializer<Comment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Comment.class)) {
			if (getGraphQLValue(field.get(comment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(comment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createBlogPostingComment",
						new HashMap<String, Object>() {
							{
								put("blogPostingId", blogPostingId);
								put("comment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createBlogPostingComment"),
			Comment.class);
	}

	protected Comment testGraphQLDocumentComment_addComment() throws Exception {
		return testGraphQLDocumentComment_addComment(
			testGraphQLDocumentComment_getDocumentId(), randomComment());
	}

	protected Long testGraphQLDocumentComment_getDocumentId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment testGraphQLDocumentComment_addComment(
			Long documentId, Comment comment)
		throws Exception {

		JSONDeserializer<Comment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Comment.class)) {
			if (getGraphQLValue(field.get(comment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(comment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createDocumentComment",
						new HashMap<String, Object>() {
							{
								put("documentId", documentId);
								put("comment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createDocumentComment"),
			Comment.class);
	}

	protected Comment testGraphQLStructuredContentComment_addComment()
		throws Exception {

		return testGraphQLStructuredContentComment_addComment(
			testGraphQLStructuredContentComment_getStructuredContentId(),
			randomComment());
	}

	protected Long testGraphQLStructuredContentComment_getStructuredContentId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Comment testGraphQLStructuredContentComment_addComment(
			Long structuredContentId, Comment comment)
		throws Exception {

		JSONDeserializer<Comment> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Comment.class)) {
			if (getGraphQLValue(field.get(comment)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(comment)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createStructuredContentComment",
						new HashMap<String, Object>() {
							{
								put("structuredContentId", structuredContentId);
								put("comment", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createStructuredContentComment"),
			Comment.class);
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

	protected void assertContains(Comment comment, List<Comment> comments) {
		boolean contains = false;

		for (Comment item : comments) {
			if (equals(comment, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(comments + " does not contain " + comment, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Comment comment1, Comment comment2) {
		Assert.assertTrue(
			comment1 + " does not equal " + comment2,
			equals(comment1, comment2));
	}

	protected void assertEquals(
		List<Comment> comments1, List<Comment> comments2) {

		Assert.assertEquals(comments1.size(), comments2.size());

		for (int i = 0; i < comments1.size(); i++) {
			Comment comment1 = comments1.get(i);
			Comment comment2 = comments2.get(i);

			assertEquals(comment1, comment2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Comment> comments1, List<Comment> comments2) {

		Assert.assertEquals(comments1.size(), comments2.size());

		for (Comment comment1 : comments1) {
			boolean contains = false;

			for (Comment comment2 : comments2) {
				if (equals(comment1, comment2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				comments2 + " does not contain " + comment1, contains);
		}
	}

	protected void assertValid(Comment comment) throws Exception {
		boolean valid = true;

		if (comment.getDateCreated() == null) {
			valid = false;
		}

		if (comment.getDateModified() == null) {
			valid = false;
		}

		if (comment.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (comment.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (comment.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (comment.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (comment.getNumberOfComments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("parentCommentId", additionalAssertFieldName)) {
				if (comment.getParentCommentId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("text", additionalAssertFieldName)) {
				if (comment.getText() == null) {
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

	protected void assertValid(Page<Comment> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Comment> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Comment> comments = page.getItems();

		int size = comments.size();

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
					com.liferay.headless.delivery.dto.v1_0.Comment.class)) {

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

	protected boolean equals(Comment comment1, Comment comment2) {
		if (comment1 == comment2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)comment1.getActions(),
						(Map)comment2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						comment1.getCreator(), comment2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						comment1.getDateCreated(), comment2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						comment1.getDateModified(),
						comment2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						comment1.getExternalReferenceCode(),
						comment2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(comment1.getId(), comment2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("numberOfComments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						comment1.getNumberOfComments(),
						comment2.getNumberOfComments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("parentCommentId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						comment1.getParentCommentId(),
						comment2.getParentCommentId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("text", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						comment1.getText(), comment2.getText())) {

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

		if (!(_commentResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_commentResource;

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
		EntityField entityField, String operator, Comment comment) {

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = comment.getDateCreated();

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

				sb.append(_format.format(comment.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = comment.getDateModified();

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

				sb.append(_format.format(comment.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = comment.getExternalReferenceCode();

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

		if (entityFieldName.equals("numberOfComments")) {
			sb.append(String.valueOf(comment.getNumberOfComments()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentCommentId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("text")) {
			Object object = comment.getText();

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

	protected Comment randomComment() throws Exception {
		return new Comment() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				numberOfComments = RandomTestUtil.randomInt();
				parentCommentId = RandomTestUtil.randomLong();
				text = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Comment randomIrrelevantComment() throws Exception {
		Comment randomIrrelevantComment = randomComment();

		return randomIrrelevantComment;
	}

	protected Comment randomPatchComment() throws Exception {
		return randomComment();
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

	protected CommentResource commentResource;
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
		LogFactoryUtil.getLog(BaseCommentResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.CommentResource
		_commentResource;

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