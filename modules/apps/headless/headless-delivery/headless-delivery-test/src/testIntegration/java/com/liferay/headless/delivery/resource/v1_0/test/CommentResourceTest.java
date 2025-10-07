/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalServiceUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.headless.delivery.client.dto.v1_0.Comment;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;

import java.util.Objects;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@RunWith(Arquillian.class)
public class CommentResourceTest extends BaseCommentResourceTestCase {

	@Override
	@Test
	public void testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode();

		Comment comment =
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// Nonexistent blogs entry

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					comment.getExternalReferenceCode()));

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different blogs entry

		BlogsEntry prevBlogsEntry = _blogsEntry;

		Comment newComment =
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevBlogsEntry.getExternalReferenceCode(),
					newComment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			Comment comment1 =
				testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

			// Nonexistent parent comment

			assertHttpResponseStatusCode(
				404,
				commentResource.
					deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
						testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
						RandomTestUtil.randomString(),
						comment1.getExternalReferenceCode()));

			// Comment associated to a different parent comment with same parent

			Comment comment2 = commentResource.postCommentComment(
				comment1.getId(), randomComment());

			Comment comment3 = commentResource.postCommentComment(
				comment2.getId(), randomComment());

			assertHttpResponseStatusCode(
				404,
				commentResource.
					deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
						testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
						comment1.getExternalReferenceCode(),
						comment3.getExternalReferenceCode()));
		}

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different parent comment with diff parent

		Comment prevParentComment = _parentComment;

		Comment comment4 =
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevParentComment.getExternalReferenceCode(),
					comment4.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode();

		Comment comment =
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// Nonexistent document

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					comment.getExternalReferenceCode()));

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different document

		FileEntry prevFileEntry = _fileEntry;

		Comment newComment =
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevFileEntry.getExternalReferenceCode(),
					newComment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode();

		Comment comment =
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// Nonexistent journal article

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					comment.getExternalReferenceCode()));

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different journal article

		JournalArticle prevJournalArticle = _journalArticle;

		Comment newComment =
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevJournalArticle.getExternalReferenceCode(),
					newComment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode();

		Comment comment =
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// Nonexistent blogs entry

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					comment.getExternalReferenceCode()));

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different blogs entry

		BlogsEntry prevBlogsEntry = _blogsEntry;

		Comment newComment =
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevBlogsEntry.getExternalReferenceCode(),
					newComment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.WARN)) {

			Comment comment1 =
				testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

			// Nonexistent parent comment

			assertHttpResponseStatusCode(
				404,
				commentResource.
					getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
						testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
						RandomTestUtil.randomString(),
						comment1.getExternalReferenceCode()));

			// Comment associated to a different parent comment with same parent

			Comment comment2 = commentResource.postCommentComment(
				comment1.getId(), randomComment());

			Comment comment3 = commentResource.postCommentComment(
				comment2.getId(), randomComment());

			assertHttpResponseStatusCode(
				404,
				commentResource.
					getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
						testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
						comment1.getExternalReferenceCode(),
						comment3.getExternalReferenceCode()));
		}

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different parent comment with diff parent

		Comment prevParentComment = _parentComment;

		Comment comment4 =
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevParentComment.getExternalReferenceCode(),
					comment4.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode();

		Comment comment =
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// Nonexistent document

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					comment.getExternalReferenceCode()));

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different document

		FileEntry prevFileEntry = _fileEntry;

		Comment newComment =
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevFileEntry.getExternalReferenceCode(),
					newComment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode();

		Comment comment =
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		// Nonexistent structured content

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					RandomTestUtil.randomString(),
					comment.getExternalReferenceCode()));

		// Nonexistent comment

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					RandomTestUtil.randomString()));

		// Comment associated to a different journal article

		JournalArticle prevJournalArticle = _journalArticle;

		Comment newComment =
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		assertHttpResponseStatusCode(
			404,
			commentResource.
				getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					prevJournalArticle.getExternalReferenceCode(),
					newComment.getExternalReferenceCode()));
	}

	@Override
	@Test
	public void testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeNotFound()
		throws Exception {

		super.
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeNotFound();

		// Existing BlogPosting but not existing Comment

		testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"blogPostingByExternalReferenceCode" +
							"BlogPostingExternalReferenceCode" +
								"CommentByExternalReferenceCode",
						HashMapBuilder.<String, Object>put(
							"blogPostingExternalReferenceCode",
							"\"" +
								testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode() +
									"\""
						).put(
							"externalReferenceCode",
							"\"" + RandomTestUtil.randomString() + "\""
						).put(
							"siteKey",
							"\"" +
								testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
									"\""
						).build(),
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	@Override
	@Test
	public void testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeNotFound()
		throws Exception {

		super.
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeNotFound();

		// Existing Document but not existing Comment

		testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentByExternalReferenceCodeDocument" +
							"ExternalReferenceCode" +
								"CommentByExternalReferenceCode",
						HashMapBuilder.<String, Object>put(
							"documentExternalReferenceCode",
							"\"" +
								testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() +
									"\""
						).put(
							"externalReferenceCode",
							"\"" + RandomTestUtil.randomString() + "\""
						).put(
							"siteKey",
							"\"" +
								testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
									"\""
						).build(),
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	@Override
	@Test
	public void testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeNotFound()
		throws Exception {

		super.
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeNotFound();

		// Existing StructuredContent but not existing Comment

		testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"structuredContentByExternalReferenceCode" +
							"StructuredContentExternalReferenceCode" +
								"CommentByExternalReferenceCode",
						HashMapBuilder.<String, Object>put(
							"externalReferenceCode",
							"\"" + RandomTestUtil.randomString() + "\""
						).put(
							"siteKey",
							"\"" +
								testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId() +
									"\""
						).put(
							"structuredContentExternalReferenceCode",
							"\"" +
								testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode() +
									"\""
						).build(),
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostBlogPostingComment() throws Exception {
		super.testGraphQLPostBlogPostingComment();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostDocumentComment() throws Exception {
		super.testGraphQLPostDocumentComment();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLPostStructuredContentComment() throws Exception {
		super.testGraphQLPostStructuredContentComment();
	}

	@Override
	@Test
	public void testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode();

		// Existing comment with an ERC associated to a different type of parent

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment postComment =
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment otherComment = _addDocumentComment();

		Comment randomComment = randomComment();

		randomComment.setExternalReferenceCode(
			otherComment.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			400,
			commentResource.
				putSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode(),
					randomComment.getExternalReferenceCode(), randomComment));
	}

	@Override
	@Test
	public void testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode();

		// Existing comment with an ERC associated to a different type of parent

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment postComment =
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment otherComment = _addDocumentComment();

		Comment randomComment = randomComment();

		randomComment.setExternalReferenceCode(
			otherComment.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			400,
			commentResource.
				putSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
					randomComment.getExternalReferenceCode(), randomComment));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.vulcan.internal.jaxrs.exception.mapper." +
					"WebApplicationExceptionMapper",
				LoggerTestUtil.ERROR)) {

			// Comment associated to a different parent comment with same parent

			@SuppressWarnings("PMD.UnusedLocalVariable")
			Comment comment1 =
				testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

			Comment comment2 = commentResource.postCommentComment(
				comment1.getId(), randomComment());

			Comment comment3 = commentResource.postCommentComment(
				comment2.getId(), randomComment());

			assertHttpResponseStatusCode(
				400,
				commentResource.
					putSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
						testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
						testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode(),
						comment3.getExternalReferenceCode(), randomComment));
		}
	}

	@Override
	@Test
	public void testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode();

		// Existing comment with an ERC associated to a different type of parent

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment postComment =
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment otherComment = _addBlogPostingComment();

		Comment randomComment = randomComment();

		randomComment.setExternalReferenceCode(
			otherComment.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			400,
			commentResource.
				putSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode(),
					randomComment.getExternalReferenceCode(), randomComment));
	}

	@Override
	@Test
	public void testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode()
		throws Exception {

		super.
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode();

		// Existing comment with an ERC associated to a different type of parent

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Comment postComment =
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment();

		Comment otherComment = _addBlogPostingComment();

		Comment randomComment = randomComment();

		randomComment.setExternalReferenceCode(
			otherComment.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			400,
			commentResource.
				putSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCodeHttpResponse(
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId(),
					testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode(),
					randomComment.getExternalReferenceCode(), randomComment));
	}

	@Override
	protected boolean equals(Comment comment1, Comment comment2) {
		return Objects.equals(_formatHTML(comment1), _formatHTML(comment2));
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {"text"};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"creatorId"};
	}

	@Override
	protected Comment testDeleteComment_addComment() throws Exception {
		return _addBlogPostingComment();
	}

	@Override
	protected Comment
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addBlogPostingComment();
	}

	@Override
	protected String
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		return _blogsEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addCommentComment();
	}

	@Override
	protected String
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		return _parentComment.getExternalReferenceCode();
	}

	@Override
	protected Long
			testDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addDocumentComment();
	}

	@Override
	protected String
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		return _fileEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addStructuredContentComment();
	}

	@Override
	protected Long
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected String
			testDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		return _journalArticle.getExternalReferenceCode();
	}

	@Override
	protected Long testGetBlogPostingCommentsPage_getBlogPostingId()
		throws Exception {

		BlogsEntry blogsEntry = _addBlogsEntry();

		return blogsEntry.getEntryId();
	}

	@Override
	protected Comment testGetComment_addComment() throws Exception {
		return _addBlogPostingComment();
	}

	@Override
	protected Long testGetCommentCommentsPage_getParentCommentId()
		throws Exception {

		Comment comment = _addBlogPostingComment();

		return comment.getId();
	}

	@Override
	protected Long testGetDocumentCommentsPage_getDocumentId()
		throws Exception {

		FileEntry fileEntry = _addFileEntry();

		return fileEntry.getFileEntryId();
	}

	@Override
	protected Comment
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addBlogPostingComment();
	}

	@Override
	protected String
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		return _blogsEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addCommentComment();
	}

	@Override
	protected String
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		return _parentComment.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addDocumentComment();
	}

	@Override
	protected String
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		return _fileEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addStructuredContentComment();
	}

	@Override
	protected Long
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected String
			testGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		return _journalArticle.getExternalReferenceCode();
	}

	@Override
	protected Long testGetStructuredContentCommentsPage_getStructuredContentId()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		return journalArticle.getResourcePrimKey();
	}

	@Override
	protected Comment testGraphQLComment_addComment() throws Exception {
		return _addBlogPostingComment();
	}

	@Override
	protected String
			testGraphQLDeleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		return _blogsEntry.getExternalReferenceCode();
	}

	@Override
	protected Comment
			testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addCommentComment();
	}

	@Override
	protected String
			testGraphQLDeleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		return _parentComment.getExternalReferenceCode();
	}

	@Override
	protected Comment
			testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addDocumentComment();
	}

	@Override
	protected String
			testGraphQLDeleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		return _fileEntry.getExternalReferenceCode();
	}

	@Override
	protected Comment
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addStructuredContentComment();
	}

	@Override
	protected String
			testGraphQLDeleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		return _journalArticle.getExternalReferenceCode();
	}

	@Override
	protected Comment
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addBlogPostingComment();
	}

	@Override
	protected String
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		return _blogsEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGraphQLGetSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addCommentComment();
	}

	@Override
	protected String
			testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		return _parentComment.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGraphQLGetSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addDocumentComment();
	}

	@Override
	protected String
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode()
		throws Exception {

		return _fileEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testGraphQLGetSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addStructuredContentComment();
	}

	@Override
	protected Long
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected String
			testGraphQLGetSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		return _journalArticle.getExternalReferenceCode();
	}

	@Override
	protected Comment testGraphQLSiteComment_addComment() throws Exception {
		return _addBlogPostingComment();
	}

	@Override
	protected Comment testPutComment_addComment() throws Exception {
		Comment comment = _addBlogPostingComment();

		return commentResource.postCommentComment(
			comment.getId(), randomComment());
	}

	@Override
	protected Comment
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addBlogPostingComment();
	}

	@Override
	protected String
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getBlogPostingExternalReferenceCode()
		throws Exception {

		return _blogsEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testPutSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addCommentComment();
	}

	@Override
	protected String
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getParentCommentExternalReferenceCode()
		throws Exception {

		return _parentComment.getExternalReferenceCode();
	}

	@Override
	protected Long
			testPutSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addDocumentComment();
	}

	@Override
	protected String
		testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getDocumentExternalReferenceCode() {

		return _fileEntry.getExternalReferenceCode();
	}

	@Override
	protected Long
			testPutSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected Comment
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_addComment()
		throws Exception {

		return _addStructuredContentComment();
	}

	@Override
	protected Long
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	@Override
	protected String
			testPutSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode_getStructuredContentExternalReferenceCode()
		throws Exception {

		return _journalArticle.getExternalReferenceCode();
	}

	private Comment _addBlogPostingComment() throws Exception {
		_blogsEntry = _addBlogsEntry();

		return commentResource.postBlogPostingComment(
			_blogsEntry.getEntryId(), randomComment());
	}

	private BlogsEntry _addBlogsEntry() throws Exception {
		ServiceContext serviceContext = new ServiceContext();

		serviceContext.setScopeGroupId(testGroup.getGroupId());

		return BlogsEntryLocalServiceUtil.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);
	}

	private Comment _addCommentComment() throws Exception {
		_parentComment = _addBlogPostingComment();

		return commentResource.postCommentComment(
			_parentComment.getId(), randomComment());
	}

	private Comment _addDocumentComment() throws Exception {
		_fileEntry = _addFileEntry();

		return commentResource.postDocumentComment(
			_fileEntry.getFileEntryId(), randomComment());
	}

	private FileEntry _addFileEntry() throws Exception {
		return DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), testGroup.getGroupId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), true,
			new ServiceContext());
	}

	private JournalArticle _addJournalArticle() throws Exception {
		return JournalTestUtil.addArticle(testGroup.getGroupId(), 0);
	}

	private Comment _addStructuredContentComment() throws Exception {
		_journalArticle = _addJournalArticle();

		return commentResource.postStructuredContentComment(
			_journalArticle.getResourcePrimKey(), randomComment());
	}

	private String _formatHTML(Comment comment) {
		String text = HtmlUtil.stripHtml(comment.getText());

		if (!text.startsWith("<p>")) {
			return StringBundler.concat("<p>", text, "</p>");
		}

		return text;
	}

	private BlogsEntry _blogsEntry;
	private FileEntry _fileEntry;
	private JournalArticle _journalArticle;
	private Comment _parentComment;

}