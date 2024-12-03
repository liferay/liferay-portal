/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.graphql.query.v1_0;

import com.liferay.headless.delivery.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.dto.v1_0.BlogPostingImage;
import com.liferay.headless.delivery.dto.v1_0.Comment;
import com.liferay.headless.delivery.dto.v1_0.ContentElement;
import com.liferay.headless.delivery.dto.v1_0.ContentSetElement;
import com.liferay.headless.delivery.dto.v1_0.ContentStructure;
import com.liferay.headless.delivery.dto.v1_0.ContentTemplate;
import com.liferay.headless.delivery.dto.v1_0.Document;
import com.liferay.headless.delivery.dto.v1_0.DocumentDataDefinitionType;
import com.liferay.headless.delivery.dto.v1_0.DocumentFolder;
import com.liferay.headless.delivery.dto.v1_0.DocumentMetadataSet;
import com.liferay.headless.delivery.dto.v1_0.DocumentShortcut;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseAttachment;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseFolder;
import com.liferay.headless.delivery.dto.v1_0.Language;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardAttachment;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardSection;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardThread;
import com.liferay.headless.delivery.dto.v1_0.NavigationMenu;
import com.liferay.headless.delivery.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.delivery.dto.v1_0.Rating;
import com.liferay.headless.delivery.dto.v1_0.SitePage;
import com.liferay.headless.delivery.dto.v1_0.StructuredContent;
import com.liferay.headless.delivery.dto.v1_0.StructuredContentFolder;
import com.liferay.headless.delivery.dto.v1_0.WikiNode;
import com.liferay.headless.delivery.dto.v1_0.WikiPage;
import com.liferay.headless.delivery.dto.v1_0.WikiPageAttachment;
import com.liferay.headless.delivery.resource.v1_0.BlogPostingImageResource;
import com.liferay.headless.delivery.resource.v1_0.BlogPostingResource;
import com.liferay.headless.delivery.resource.v1_0.CommentResource;
import com.liferay.headless.delivery.resource.v1_0.ContentElementResource;
import com.liferay.headless.delivery.resource.v1_0.ContentSetElementResource;
import com.liferay.headless.delivery.resource.v1_0.ContentStructureResource;
import com.liferay.headless.delivery.resource.v1_0.ContentTemplateResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentDataDefinitionTypeResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentFolderResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentMetadataSetResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentResource;
import com.liferay.headless.delivery.resource.v1_0.DocumentShortcutResource;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseArticleResource;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseAttachmentResource;
import com.liferay.headless.delivery.resource.v1_0.KnowledgeBaseFolderResource;
import com.liferay.headless.delivery.resource.v1_0.LanguageResource;
import com.liferay.headless.delivery.resource.v1_0.MessageBoardAttachmentResource;
import com.liferay.headless.delivery.resource.v1_0.MessageBoardMessageResource;
import com.liferay.headless.delivery.resource.v1_0.MessageBoardSectionResource;
import com.liferay.headless.delivery.resource.v1_0.MessageBoardThreadResource;
import com.liferay.headless.delivery.resource.v1_0.NavigationMenuResource;
import com.liferay.headless.delivery.resource.v1_0.SitePageResource;
import com.liferay.headless.delivery.resource.v1_0.StructuredContentFolderResource;
import com.liferay.headless.delivery.resource.v1_0.StructuredContentResource;
import com.liferay.headless.delivery.resource.v1_0.WikiNodeResource;
import com.liferay.headless.delivery.resource.v1_0.WikiPageAttachmentResource;
import com.liferay.headless.delivery.resource.v1_0.WikiPageResource;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.filter.Filter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.aggregation.Aggregation;
import com.liferay.portal.vulcan.aggregation.Facet;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.validation.constraints.NotEmpty;

import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setBlogPostingResourceComponentServiceObjects(
		ComponentServiceObjects<BlogPostingResource>
			blogPostingResourceComponentServiceObjects) {

		_blogPostingResourceComponentServiceObjects =
			blogPostingResourceComponentServiceObjects;
	}

	public static void setBlogPostingImageResourceComponentServiceObjects(
		ComponentServiceObjects<BlogPostingImageResource>
			blogPostingImageResourceComponentServiceObjects) {

		_blogPostingImageResourceComponentServiceObjects =
			blogPostingImageResourceComponentServiceObjects;
	}

	public static void setCommentResourceComponentServiceObjects(
		ComponentServiceObjects<CommentResource>
			commentResourceComponentServiceObjects) {

		_commentResourceComponentServiceObjects =
			commentResourceComponentServiceObjects;
	}

	public static void setContentElementResourceComponentServiceObjects(
		ComponentServiceObjects<ContentElementResource>
			contentElementResourceComponentServiceObjects) {

		_contentElementResourceComponentServiceObjects =
			contentElementResourceComponentServiceObjects;
	}

	public static void setContentSetElementResourceComponentServiceObjects(
		ComponentServiceObjects<ContentSetElementResource>
			contentSetElementResourceComponentServiceObjects) {

		_contentSetElementResourceComponentServiceObjects =
			contentSetElementResourceComponentServiceObjects;
	}

	public static void setContentStructureResourceComponentServiceObjects(
		ComponentServiceObjects<ContentStructureResource>
			contentStructureResourceComponentServiceObjects) {

		_contentStructureResourceComponentServiceObjects =
			contentStructureResourceComponentServiceObjects;
	}

	public static void setContentTemplateResourceComponentServiceObjects(
		ComponentServiceObjects<ContentTemplateResource>
			contentTemplateResourceComponentServiceObjects) {

		_contentTemplateResourceComponentServiceObjects =
			contentTemplateResourceComponentServiceObjects;
	}

	public static void setDocumentResourceComponentServiceObjects(
		ComponentServiceObjects<DocumentResource>
			documentResourceComponentServiceObjects) {

		_documentResourceComponentServiceObjects =
			documentResourceComponentServiceObjects;
	}

	public static void
		setDocumentDataDefinitionTypeResourceComponentServiceObjects(
			ComponentServiceObjects<DocumentDataDefinitionTypeResource>
				documentDataDefinitionTypeResourceComponentServiceObjects) {

		_documentDataDefinitionTypeResourceComponentServiceObjects =
			documentDataDefinitionTypeResourceComponentServiceObjects;
	}

	public static void setDocumentFolderResourceComponentServiceObjects(
		ComponentServiceObjects<DocumentFolderResource>
			documentFolderResourceComponentServiceObjects) {

		_documentFolderResourceComponentServiceObjects =
			documentFolderResourceComponentServiceObjects;
	}

	public static void setDocumentMetadataSetResourceComponentServiceObjects(
		ComponentServiceObjects<DocumentMetadataSetResource>
			documentMetadataSetResourceComponentServiceObjects) {

		_documentMetadataSetResourceComponentServiceObjects =
			documentMetadataSetResourceComponentServiceObjects;
	}

	public static void setDocumentShortcutResourceComponentServiceObjects(
		ComponentServiceObjects<DocumentShortcutResource>
			documentShortcutResourceComponentServiceObjects) {

		_documentShortcutResourceComponentServiceObjects =
			documentShortcutResourceComponentServiceObjects;
	}

	public static void setKnowledgeBaseArticleResourceComponentServiceObjects(
		ComponentServiceObjects<KnowledgeBaseArticleResource>
			knowledgeBaseArticleResourceComponentServiceObjects) {

		_knowledgeBaseArticleResourceComponentServiceObjects =
			knowledgeBaseArticleResourceComponentServiceObjects;
	}

	public static void
		setKnowledgeBaseAttachmentResourceComponentServiceObjects(
			ComponentServiceObjects<KnowledgeBaseAttachmentResource>
				knowledgeBaseAttachmentResourceComponentServiceObjects) {

		_knowledgeBaseAttachmentResourceComponentServiceObjects =
			knowledgeBaseAttachmentResourceComponentServiceObjects;
	}

	public static void setKnowledgeBaseFolderResourceComponentServiceObjects(
		ComponentServiceObjects<KnowledgeBaseFolderResource>
			knowledgeBaseFolderResourceComponentServiceObjects) {

		_knowledgeBaseFolderResourceComponentServiceObjects =
			knowledgeBaseFolderResourceComponentServiceObjects;
	}

	public static void setLanguageResourceComponentServiceObjects(
		ComponentServiceObjects<LanguageResource>
			languageResourceComponentServiceObjects) {

		_languageResourceComponentServiceObjects =
			languageResourceComponentServiceObjects;
	}

	public static void setMessageBoardAttachmentResourceComponentServiceObjects(
		ComponentServiceObjects<MessageBoardAttachmentResource>
			messageBoardAttachmentResourceComponentServiceObjects) {

		_messageBoardAttachmentResourceComponentServiceObjects =
			messageBoardAttachmentResourceComponentServiceObjects;
	}

	public static void setMessageBoardMessageResourceComponentServiceObjects(
		ComponentServiceObjects<MessageBoardMessageResource>
			messageBoardMessageResourceComponentServiceObjects) {

		_messageBoardMessageResourceComponentServiceObjects =
			messageBoardMessageResourceComponentServiceObjects;
	}

	public static void setMessageBoardSectionResourceComponentServiceObjects(
		ComponentServiceObjects<MessageBoardSectionResource>
			messageBoardSectionResourceComponentServiceObjects) {

		_messageBoardSectionResourceComponentServiceObjects =
			messageBoardSectionResourceComponentServiceObjects;
	}

	public static void setMessageBoardThreadResourceComponentServiceObjects(
		ComponentServiceObjects<MessageBoardThreadResource>
			messageBoardThreadResourceComponentServiceObjects) {

		_messageBoardThreadResourceComponentServiceObjects =
			messageBoardThreadResourceComponentServiceObjects;
	}

	public static void setNavigationMenuResourceComponentServiceObjects(
		ComponentServiceObjects<NavigationMenuResource>
			navigationMenuResourceComponentServiceObjects) {

		_navigationMenuResourceComponentServiceObjects =
			navigationMenuResourceComponentServiceObjects;
	}

	public static void setSitePageResourceComponentServiceObjects(
		ComponentServiceObjects<SitePageResource>
			sitePageResourceComponentServiceObjects) {

		_sitePageResourceComponentServiceObjects =
			sitePageResourceComponentServiceObjects;
	}

	public static void setStructuredContentResourceComponentServiceObjects(
		ComponentServiceObjects<StructuredContentResource>
			structuredContentResourceComponentServiceObjects) {

		_structuredContentResourceComponentServiceObjects =
			structuredContentResourceComponentServiceObjects;
	}

	public static void
		setStructuredContentFolderResourceComponentServiceObjects(
			ComponentServiceObjects<StructuredContentFolderResource>
				structuredContentFolderResourceComponentServiceObjects) {

		_structuredContentFolderResourceComponentServiceObjects =
			structuredContentFolderResourceComponentServiceObjects;
	}

	public static void setWikiNodeResourceComponentServiceObjects(
		ComponentServiceObjects<WikiNodeResource>
			wikiNodeResourceComponentServiceObjects) {

		_wikiNodeResourceComponentServiceObjects =
			wikiNodeResourceComponentServiceObjects;
	}

	public static void setWikiPageResourceComponentServiceObjects(
		ComponentServiceObjects<WikiPageResource>
			wikiPageResourceComponentServiceObjects) {

		_wikiPageResourceComponentServiceObjects =
			wikiPageResourceComponentServiceObjects;
	}

	public static void setWikiPageAttachmentResourceComponentServiceObjects(
		ComponentServiceObjects<WikiPageAttachmentResource>
			wikiPageAttachmentResourceComponentServiceObjects) {

		_wikiPageAttachmentResourceComponentServiceObjects =
			wikiPageAttachmentResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPosting(blogPostingId: ___){actions, aggregateRating, alternativeHeadline, articleBody, creator, customFields, dateCreated, dateModified, datePublished, description, encodingFormat, externalReferenceCode, friendlyUrlPath, headline, id, image, keywords, numberOfComments, relatedContents, renderedContents, siteId, taxonomyCategoryBriefs, taxonomyCategoryIds, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the blog post.")
	public BlogPosting blogPosting(
			@GraphQLName("blogPostingId") Long blogPostingId)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.getBlogPosting(
				blogPostingId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingMyRating(blogPostingId: ___){actions, bestRating, creator, dateCreated, dateModified, id, ratingValue, worstRating}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the blog post rating of the user who authenticated the request."
	)
	public Rating blogPostingMyRating(
			@GraphQLName("blogPostingId") Long blogPostingId)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.getBlogPostingMyRating(
				blogPostingId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingPermissions(blogPostingId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BlogPostingPage blogPostingPermissions(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> new BlogPostingPage(
				blogPostingResource.getBlogPostingPermissionsPage(
					blogPostingId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingRenderedContentByDisplayPageDisplayPageKey(blogPostingId: ___, displayPageKey: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the blog post's rendered display page"
	)
	public String blogPostingRenderedContentByDisplayPageDisplayPageKey(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("displayPageKey") String displayPageKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.
					getBlogPostingRenderedContentByDisplayPageDisplayPageKey(
						blogPostingId, displayPageKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostings(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's blog postings. Results can be paginated, filtered, searched, and sorted."
	)
	public BlogPostingPage blogPostings(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> new BlogPostingPage(
				blogPostingResource.getSiteBlogPostingsPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						blogPostingResource, aggregations),
					_filterBiFunction.apply(blogPostingResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(blogPostingResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, aggregateRating, alternativeHeadline, articleBody, creator, customFields, dateCreated, dateModified, datePublished, description, encodingFormat, externalReferenceCode, friendlyUrlPath, headline, id, image, keywords, numberOfComments, relatedContents, renderedContents, siteId, taxonomyCategoryBriefs, taxonomyCategoryIds, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's blog post by external reference code."
	)
	public BlogPosting blogPostingByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.getSiteBlogPostingByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteBlogPostingPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public BlogPostingPage siteBlogPostingPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> new BlogPostingPage(
				blogPostingResource.getSiteBlogPostingPermissionsPage(
					Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingImage(blogPostingImageId: ___){contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the blog post's image. The binary image is returned as a relative URL to the image itself."
	)
	public BlogPostingImage blogPostingImage(
			@GraphQLName("blogPostingImageId") Long blogPostingImageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.getBlogPostingImage(
					blogPostingImageId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingImages(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's blog post images. Results can be paginated, filtered, searched, and sorted."
	)
	public BlogPostingImagePage blogPostingImages(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource -> new BlogPostingImagePage(
				blogPostingImageResource.getSiteBlogPostingImagesPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						blogPostingImageResource, aggregations),
					_filterBiFunction.apply(
						blogPostingImageResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						blogPostingImageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingImageByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's blog post image by external reference code. The binary image is returned as a relative URL to the image itself."
	)
	public BlogPostingImage blogPostingImageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.
					getSiteBlogPostingImageByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingComments(aggregation: ___, blogPostingId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the blog post's comments in a list. Results can be paginated, filtered, searched, and sorted."
	)
	public CommentPage blogPostingComments(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> new CommentPage(
				commentResource.getBlogPostingCommentsPage(
					blogPostingId, search,
					_aggregationBiFunction.apply(commentResource, aggregations),
					_filterBiFunction.apply(commentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(commentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {comment(commentId: ___){actions, creator, dateCreated, dateModified, externalReferenceCode, id, numberOfComments, parentCommentId, text}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the comment.")
	public Comment comment(@GraphQLName("commentId") Long commentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.getComment(commentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {commentComments(aggregation: ___, filter: ___, page: ___, pageSize: ___, parentCommentId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent comment's child comments. Results can be paginated, filtered, searched, and sorted."
	)
	public CommentPage commentComments(
			@GraphQLName("parentCommentId") Long parentCommentId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> new CommentPage(
				commentResource.getCommentCommentsPage(
					parentCommentId, search,
					_aggregationBiFunction.apply(commentResource, aggregations),
					_filterBiFunction.apply(commentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(commentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentComments(aggregation: ___, documentId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the document's comments. Results can be paginated, filtered, searched, and sorted."
	)
	public CommentPage documentComments(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> new CommentPage(
				commentResource.getDocumentCommentsPage(
					documentId, search,
					_aggregationBiFunction.apply(commentResource, aggregations),
					_filterBiFunction.apply(commentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(commentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(blogPostingExternalReferenceCode: ___, externalReferenceCode: ___, siteKey: ___){actions, creator, dateCreated, dateModified, externalReferenceCode, id, numberOfComments, parentCommentId, text}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the blog posting's comment by blog posting's and comment's external reference codes."
	)
	public Comment
			blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("blogPostingExternalReferenceCode") String
					blogPostingExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey), blogPostingExternalReferenceCode,
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(externalReferenceCode: ___, parentCommentExternalReferenceCode: ___, siteKey: ___){actions, creator, dateCreated, dateModified, externalReferenceCode, id, numberOfComments, parentCommentId, text}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent comment's comment by its parent comment's and comment's external reference codes."
	)
	public Comment
			commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("parentCommentExternalReferenceCode") String
					parentCommentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey),
						parentCommentExternalReferenceCode,
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(documentExternalReferenceCode: ___, externalReferenceCode: ___, siteKey: ___){actions, creator, dateCreated, dateModified, externalReferenceCode, id, numberOfComments, parentCommentId, text}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the document's comment by document's and comment's external reference codes."
	)
	public Comment
			documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("documentExternalReferenceCode") String
					documentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey), documentExternalReferenceCode,
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___, structuredContentExternalReferenceCode: ___){actions, creator, dateCreated, dateModified, externalReferenceCode, id, numberOfComments, parentCommentId, text}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the structured content's comment by structured content's and comment's external reference codes."
	)
	public Comment
			structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("structuredContentExternalReferenceCode") String
					structuredContentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey),
						structuredContentExternalReferenceCode,
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentComments(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___, structuredContentId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the structured content's comments. Results can be paginated, filtered, searched, and sorted."
	)
	public CommentPage structuredContentComments(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> new CommentPage(
				commentResource.getStructuredContentCommentsPage(
					structuredContentId, search,
					_aggregationBiFunction.apply(commentResource, aggregations),
					_filterBiFunction.apply(commentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(commentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryContentElements(aggregation: ___, assetLibraryId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentElementPage assetLibraryContentElements(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentElementResource -> new ContentElementPage(
				contentElementResource.getAssetLibraryContentElementsPage(
					Long.valueOf(assetLibraryId), search,
					_aggregationBiFunction.apply(
						contentElementResource, aggregations),
					_filterBiFunction.apply(
						contentElementResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contentElementResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentElements(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentElementPage contentElements(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentElementResource -> new ContentElementPage(
				contentElementResource.getSiteContentElementsPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						contentElementResource, aggregations),
					_filterBiFunction.apply(
						contentElementResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contentElementResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryContentSetByKeyContentSetElements(assetLibraryId: ___, key: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentSetElementPage assetLibraryContentSetByKeyContentSetElements(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("key") String key,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentSetElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentSetElementResource -> new ContentSetElementPage(
				contentSetElementResource.
					getAssetLibraryContentSetByKeyContentSetElementsPage(
						Long.valueOf(assetLibraryId), key,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryContentSetByUuidContentSetElements(assetLibraryId: ___, page: ___, pageSize: ___, uuid: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentSetElementPage assetLibraryContentSetByUuidContentSetElements(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("uuid") String uuid,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentSetElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentSetElementResource -> new ContentSetElementPage(
				contentSetElementResource.
					getAssetLibraryContentSetByUuidContentSetElementsPage(
						Long.valueOf(assetLibraryId), uuid,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentSetContentSetElements(contentSetId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the content set's elements (e.g., structured content, blogs, etc.). Results can be paginated. The set of available headers are: Accept-Language (string), Host (string), User-Agent (string), X-Browser (string), X-Cookies (collection string), X-Device-Brand (string), X-Device-Model (string), X-Device-Screen-Resolution-Height (double), X-Device-Screen-Resolution-Width (double), X-Last-Sign-In-Date-Time (date time) and X-Signed-In (boolean). Local date will be always present in the request."
	)
	public ContentSetElementPage contentSetContentSetElements(
			@GraphQLName("contentSetId") Long contentSetId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentSetElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentSetElementResource -> new ContentSetElementPage(
				contentSetElementResource.getContentSetContentSetElementsPage(
					contentSetId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentSetProviderByKeyContentSetElements(key: ___, page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentSetElementPage contentSetProviderByKeyContentSetElements(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("key") String key,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentSetElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentSetElementResource -> new ContentSetElementPage(
				contentSetElementResource.
					getSiteContentSetProviderByKeyContentSetElementsPage(
						Long.valueOf(siteKey), key,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentSetByKeyContentSetElements(key: ___, page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the content set elements by key. Results can be paginated."
	)
	public ContentSetElementPage contentSetByKeyContentSetElements(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("key") String key,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentSetElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentSetElementResource -> new ContentSetElementPage(
				contentSetElementResource.
					getSiteContentSetByKeyContentSetElementsPage(
						Long.valueOf(siteKey), key,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentSetByUuidContentSetElements(page: ___, pageSize: ___, siteKey: ___, uuid: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the content set elements by UUID. Results can be paginated."
	)
	public ContentSetElementPage contentSetByUuidContentSetElements(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("uuid") String uuid,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentSetElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentSetElementResource -> new ContentSetElementPage(
				contentSetElementResource.
					getSiteContentSetByUuidContentSetElementsPage(
						Long.valueOf(siteKey), uuid,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryContentStructures(aggregation: ___, assetLibraryId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentStructurePage assetLibraryContentStructures(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> new ContentStructurePage(
				contentStructureResource.getAssetLibraryContentStructuresPage(
					Long.valueOf(assetLibraryId), search,
					_aggregationBiFunction.apply(
						contentStructureResource, aggregations),
					_filterBiFunction.apply(
						contentStructureResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contentStructureResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryContentStructurePermissions(assetLibraryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentStructurePage assetLibraryContentStructurePermissions(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> new ContentStructurePage(
				contentStructureResource.
					getAssetLibraryContentStructurePermissionsPage(
						Long.valueOf(assetLibraryId), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentStructure(contentStructureId: ___){assetLibraryKey, availableLanguages, contentStructureFields, creator, dateCreated, dateModified, description, description_i18n, id, name, name_i18n, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the content structure.")
	public ContentStructure contentStructure(
			@GraphQLName("contentStructureId") Long contentStructureId)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource ->
				contentStructureResource.getContentStructure(
					contentStructureId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentStructurePermissions(contentStructureId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentStructurePage contentStructurePermissions(
			@GraphQLName("contentStructureId") Long contentStructureId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> new ContentStructurePage(
				contentStructureResource.getContentStructurePermissionsPage(
					contentStructureId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentStructures(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's content structures. Results can be paginated, filtered, searched, and sorted."
	)
	public ContentStructurePage contentStructures(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> new ContentStructurePage(
				contentStructureResource.getSiteContentStructuresPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						contentStructureResource, aggregations),
					_filterBiFunction.apply(
						contentStructureResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contentStructureResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteContentStructurePermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentStructurePage siteContentStructurePermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> new ContentStructurePage(
				contentStructureResource.getSiteContentStructurePermissionsPage(
					Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryContentTemplates(aggregation: ___, assetLibraryId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentTemplatePage assetLibraryContentTemplates(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentTemplateResource -> new ContentTemplatePage(
				contentTemplateResource.getAssetLibraryContentTemplatesPage(
					Long.valueOf(assetLibraryId), search,
					_aggregationBiFunction.apply(
						contentTemplateResource, aggregations),
					_filterBiFunction.apply(
						contentTemplateResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contentTemplateResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentTemplates(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentTemplatePage contentTemplates(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentTemplateResource -> new ContentTemplatePage(
				contentTemplateResource.getSiteContentTemplatesPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						contentTemplateResource, aggregations),
					_filterBiFunction.apply(
						contentTemplateResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						contentTemplateResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentTemplate(contentTemplateId: ___, siteKey: ___){actions, assetLibraryKey, availableLanguages, contentStructureId, creator, dateCreated, dateModified, description, description_i18n, id, name, name_i18n, programmingLanguage, siteId, templateScript}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ContentTemplate contentTemplate(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("contentTemplateId") String contentTemplateId)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentTemplateResource ->
				contentTemplateResource.getSiteContentTemplate(
					Long.valueOf(siteKey), contentTemplateId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocuments(aggregation: ___, assetLibraryId: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentPage assetLibraryDocuments(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getAssetLibraryDocumentsPage(
					Long.valueOf(assetLibraryId), flatten, search,
					_aggregationBiFunction.apply(
						documentResource, aggregations),
					_filterBiFunction.apply(documentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(documentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){actions, adaptedImages, aggregateRating, assetLibraryKey, contentUrl, contentValue, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, documentFolderExternalReferenceCode, documentFolderId, documentType, encodingFormat, externalReferenceCode, fileExtension, fileName, friendlyUrlPath, id, keywords, numberOfComments, relatedContents, renderedContents, siteId, sizeInBytes, taxonomyCategoryBriefs, taxonomyCategoryIds, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the asset library's document by external reference code."
	)
	public Document assetLibraryDocumentByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.getAssetLibraryDocumentByExternalReferenceCode(
					Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentPermissions(assetLibraryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentPage assetLibraryDocumentPermissions(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getAssetLibraryDocumentPermissionsPage(
					Long.valueOf(assetLibraryId), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentsRatedByMe(assetLibraryId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the documents rated by the user.")
	public DocumentPage assetLibraryDocumentsRatedByMe(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getAssetLibraryDocumentsRatedByMePage(
					Long.valueOf(assetLibraryId),
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentFolderDocuments(aggregation: ___, documentFolderId: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the folder's documents. Results can be paginated, filtered, searched, and sorted."
	)
	public DocumentPage documentFolderDocuments(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getDocumentFolderDocumentsPage(
					documentFolderId, flatten, search,
					_aggregationBiFunction.apply(
						documentResource, aggregations),
					_filterBiFunction.apply(documentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(documentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {document(documentId: ___){actions, adaptedImages, aggregateRating, assetLibraryKey, contentUrl, contentValue, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, documentFolderExternalReferenceCode, documentFolderId, documentType, encodingFormat, externalReferenceCode, fileExtension, fileName, friendlyUrlPath, id, keywords, numberOfComments, relatedContents, renderedContents, siteId, sizeInBytes, taxonomyCategoryBriefs, taxonomyCategoryIds, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the document.")
	public Document document(@GraphQLName("documentId") Long documentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.getDocument(documentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentMyRating(documentId: ___){actions, bestRating, creator, dateCreated, dateModified, id, ratingValue, worstRating}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the document's rating.")
	public Rating documentMyRating(@GraphQLName("documentId") Long documentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.getDocumentMyRating(
				documentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentPermissions(documentId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentPage documentPermissions(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getDocumentPermissionsPage(
					documentId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentRenderedContentByDisplayPageDisplayPageKey(displayPageKey: ___, documentId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the document's rendered display page"
	)
	public String documentRenderedContentByDisplayPageDisplayPageKey(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("displayPageKey") String displayPageKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.
					getDocumentRenderedContentByDisplayPageDisplayPageKey(
						documentId, displayPageKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documents(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the documents in the site's root folder. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public DocumentPage documents(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getSiteDocumentsPage(
					Long.valueOf(siteKey), flatten, search,
					_aggregationBiFunction.apply(
						documentResource, aggregations),
					_filterBiFunction.apply(documentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(documentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, adaptedImages, aggregateRating, assetLibraryKey, contentUrl, contentValue, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, documentFolderExternalReferenceCode, documentFolderId, documentType, encodingFormat, externalReferenceCode, fileExtension, fileName, friendlyUrlPath, id, keywords, numberOfComments, relatedContents, renderedContents, siteId, sizeInBytes, taxonomyCategoryBriefs, taxonomyCategoryIds, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's document by external reference code."
	)
	public Document documentByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.getSiteDocumentByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteDocumentPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentPage siteDocumentPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getSiteDocumentPermissionsPage(
					Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentsRatedByMe(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the documents rated by the user.")
	public DocumentPage documentsRatedByMe(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> new DocumentPage(
				documentResource.getSiteDocumentsRatedByMePage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentDataDefinitionTypes(aggregation: ___, assetLibraryId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentDataDefinitionTypePage
			assetLibraryDocumentDataDefinitionTypes(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				new DocumentDataDefinitionTypePage(
					documentDataDefinitionTypeResource.
						getAssetLibraryDocumentDataDefinitionTypesPage(
							Long.valueOf(assetLibraryId), search,
							_aggregationBiFunction.apply(
								documentDataDefinitionTypeResource,
								aggregations),
							_filterBiFunction.apply(
								documentDataDefinitionTypeResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								documentDataDefinitionTypeResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentDataDefinitionType(documentDataDefinitionTypeId: ___){actions, assetLibraryKey, availableLanguages, creator, dataDefinitionFields, dataLayout, dateCreated, dateModified, description, description_i18n, documentMetadataSetIds, externalReferenceCode, id, name, name_i18n, siteId, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentDataDefinitionType documentDataDefinitionType(
			@GraphQLName("documentDataDefinitionTypeId") Long
				documentDataDefinitionTypeId)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					getDocumentDataDefinitionType(
						documentDataDefinitionTypeId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentDataDefinitionTypes(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentDataDefinitionTypePage documentDataDefinitionTypes(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				new DocumentDataDefinitionTypePage(
					documentDataDefinitionTypeResource.
						getSiteDocumentDataDefinitionTypesPage(
							Long.valueOf(siteKey), search,
							_aggregationBiFunction.apply(
								documentDataDefinitionTypeResource,
								aggregations),
							_filterBiFunction.apply(
								documentDataDefinitionTypeResource,
								filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								documentDataDefinitionTypeResource,
								sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentFolders(aggregation: ___, assetLibraryId: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentFolderPage assetLibraryDocumentFolders(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					Long.valueOf(assetLibraryId), flatten, search,
					_aggregationBiFunction.apply(
						documentFolderResource, aggregations),
					_filterBiFunction.apply(
						documentFolderResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						documentFolderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentFolderPermissions(assetLibraryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentFolderPage assetLibraryDocumentFolderPermissions(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.
					getAssetLibraryDocumentFolderPermissionsPage(
						Long.valueOf(assetLibraryId), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentFoldersRatedByMe(assetLibraryId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the document folders rated by the user."
	)
	public DocumentFolderPage assetLibraryDocumentFoldersRatedByMe(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						Long.valueOf(assetLibraryId),
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentFolder(documentFolderId: ___){actions, assetLibraryKey, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfDocumentFolders, numberOfDocuments, parentDocumentFolderId, siteId, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the document folder.")
	public DocumentFolder documentFolder(
			@GraphQLName("documentFolderId") Long documentFolderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> documentFolderResource.getDocumentFolder(
				documentFolderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentFolderMyRating(documentFolderId: ___){actions, bestRating, creator, dateCreated, dateModified, id, ratingValue, worstRating}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the document folder's rating.")
	public Rating documentFolderMyRating(
			@GraphQLName("documentFolderId") Long documentFolderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.getDocumentFolderMyRating(
					documentFolderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentFolderPermissions(documentFolderId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentFolderPage documentFolderPermissions(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.getDocumentFolderPermissionsPage(
					documentFolderId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentFolderDocumentFolders(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, parentDocumentFolderId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the folder's subfolders. Results can be paginated, filtered, searched, and sorted."
	)
	public DocumentFolderPage documentFolderDocumentFolders(
			@GraphQLName("parentDocumentFolderId") Long parentDocumentFolderId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, flatten, search,
					_aggregationBiFunction.apply(
						documentFolderResource, aggregations),
					_filterBiFunction.apply(
						documentFolderResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						documentFolderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentFolders(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's document folders. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public DocumentFolderPage documentFolders(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.getSiteDocumentFoldersPage(
					Long.valueOf(siteKey), flatten, search,
					_aggregationBiFunction.apply(
						documentFolderResource, aggregations),
					_filterBiFunction.apply(
						documentFolderResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						documentFolderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteDocumentFolderPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentFolderPage siteDocumentFolderPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.getSiteDocumentFolderPermissionsPage(
					Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentFoldersRatedByMe(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the document folders rated by the user."
	)
	public DocumentFolderPage documentFoldersRatedByMe(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> new DocumentFolderPage(
				documentFolderResource.getSiteDocumentFoldersRatedByMePage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentsFolderByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, assetLibraryKey, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfDocumentFolders, numberOfDocuments, parentDocumentFolderId, siteId, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's document folder by external reference code."
	)
	public DocumentFolder documentsFolderByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.
					getSiteDocumentsFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentMetadataSets(assetLibraryId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentMetadataSetPage assetLibraryDocumentMetadataSets(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource -> new DocumentMetadataSetPage(
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetsPage(
						Long.valueOf(assetLibraryId),
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentMetadataSetByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){actions, assetLibraryKey, availableLanguages, dataDefinitionFields, dataLayout, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the asset library's Document Metadata Set by external reference code."
	)
	public DocumentMetadataSet
			assetLibraryDocumentMetadataSetByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					getAssetLibraryDocumentMetadataSetByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentMetadataSet(documentMetadataSetId: ___){actions, assetLibraryKey, availableLanguages, dataDefinitionFields, dataLayout, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the document metadata set.")
	public DocumentMetadataSet documentMetadataSet(
			@GraphQLName("documentMetadataSetId") Long documentMetadataSetId)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.getDocumentMetadataSet(
					documentMetadataSetId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentMetadataSets(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentMetadataSetPage documentMetadataSets(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource -> new DocumentMetadataSetPage(
				documentMetadataSetResource.getSiteDocumentMetadataSetsPage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentMetadataSetByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, assetLibraryKey, availableLanguages, dataDefinitionFields, dataLayout, dateCreated, dateModified, description, description_i18n, externalReferenceCode, id, name, name_i18n, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's Document Metadata Set by external reference code."
	)
	public DocumentMetadataSet documentMetadataSetByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					getSiteDocumentMetadataSetByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryDocumentShortcuts(assetLibraryId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentShortcutPage assetLibraryDocumentShortcuts(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource -> new DocumentShortcutPage(
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					Long.valueOf(assetLibraryId),
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentShortcut(documentShortcutId: ___){actions, assetLibraryKey, dateCreated, dateModified, externalReferenceCode, folderId, id, siteId, targetDocumentId, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the document shortcut.")
	public DocumentShortcut documentShortcut(
			@GraphQLName("documentShortcutId") Long documentShortcutId)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.getDocumentShortcut(
					documentShortcutId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentShortcuts(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public DocumentShortcutPage documentShortcuts(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource -> new DocumentShortcutPage(
				documentShortcutResource.getSiteDocumentShortcutsPage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {documentShortcutByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, assetLibraryKey, dateCreated, dateModified, externalReferenceCode, folderId, id, siteId, targetDocumentId, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's document shortcut by external reference code."
	)
	public DocumentShortcut documentShortcutByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.
					getSiteDocumentShortcutByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticle(knowledgeBaseArticleId: ___){actions, aggregateRating, articleBody, creator, customFields, dateCreated, dateModified, datePublished, description, encodingFormat, externalReferenceCode, friendlyUrlPath, id, keywords, numberOfAttachments, numberOfKnowledgeBaseArticles, parentKnowledgeBaseArticleId, parentKnowledgeBaseFolder, parentKnowledgeBaseFolderId, relatedContents, siteId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the knowledge base article.")
	public KnowledgeBaseArticle knowledgeBaseArticle(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.getKnowledgeBaseArticle(
					knowledgeBaseArticleId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticleMyRating(knowledgeBaseArticleId: ___){actions, bestRating, creator, dateCreated, dateModified, id, ratingValue, worstRating}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the knowledge base article's rating."
	)
	public Rating knowledgeBaseArticleMyRating(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.getKnowledgeBaseArticleMyRating(
					knowledgeBaseArticleId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticlePermissions(knowledgeBaseArticleId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KnowledgeBaseArticlePage knowledgeBaseArticlePermissions(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticlePermissionsPage(
						knowledgeBaseArticleId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticleKnowledgeBaseArticles(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, parentKnowledgeBaseArticleId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent knowledge base article's child knowledge base articles. Results can be paginated, filtered, searched, and sorted."
	)
	public KnowledgeBaseArticlePage knowledgeBaseArticleKnowledgeBaseArticles(
			@GraphQLName("parentKnowledgeBaseArticleId") Long
				parentKnowledgeBaseArticleId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
				knowledgeBaseArticleResource.
					getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
						parentKnowledgeBaseArticleId, flatten, search,
						_aggregationBiFunction.apply(
							knowledgeBaseArticleResource, aggregations),
						_filterBiFunction.apply(
							knowledgeBaseArticleResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							knowledgeBaseArticleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseFolderKnowledgeBaseArticles(aggregation: ___, filter: ___, flatten: ___, knowledgeBaseFolderId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the folder's knowledge base articles. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public KnowledgeBaseArticlePage knowledgeBaseFolderKnowledgeBaseArticles(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
				knowledgeBaseArticleResource.
					getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
						knowledgeBaseFolderId, flatten, search,
						_aggregationBiFunction.apply(
							knowledgeBaseArticleResource, aggregations),
						_filterBiFunction.apply(
							knowledgeBaseArticleResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							knowledgeBaseArticleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticles(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's knowledge base articles. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public KnowledgeBaseArticlePage knowledgeBaseArticles(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
				knowledgeBaseArticleResource.getSiteKnowledgeBaseArticlesPage(
					Long.valueOf(siteKey), flatten, search,
					_aggregationBiFunction.apply(
						knowledgeBaseArticleResource, aggregations),
					_filterBiFunction.apply(
						knowledgeBaseArticleResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						knowledgeBaseArticleResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticleByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, aggregateRating, articleBody, creator, customFields, dateCreated, dateModified, datePublished, description, encodingFormat, externalReferenceCode, friendlyUrlPath, id, keywords, numberOfAttachments, numberOfKnowledgeBaseArticles, parentKnowledgeBaseArticleId, parentKnowledgeBaseFolder, parentKnowledgeBaseFolderId, relatedContents, siteId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's knowledge base article by external reference code."
	)
	public KnowledgeBaseArticle knowledgeBaseArticleByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					getSiteKnowledgeBaseArticleByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteKnowledgeBaseArticlePermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KnowledgeBaseArticlePage siteKnowledgeBaseArticlePermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
				knowledgeBaseArticleResource.
					getSiteKnowledgeBaseArticlePermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticleKnowledgeBaseAttachments(knowledgeBaseArticleId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the knowledge base article's attachments."
	)
	public KnowledgeBaseAttachmentPage
			knowledgeBaseArticleKnowledgeBaseAttachments(
				@GraphQLName("knowledgeBaseArticleId") Long
					knowledgeBaseArticleId)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource -> new KnowledgeBaseAttachmentPage(
				knowledgeBaseAttachmentResource.
					getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage(
						knowledgeBaseArticleId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseAttachment(knowledgeBaseAttachmentId: ___){contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the knowledge base attachment.")
	public KnowledgeBaseAttachment knowledgeBaseAttachment(
			@GraphQLName("knowledgeBaseAttachmentId") Long
				knowledgeBaseAttachmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.getKnowledgeBaseAttachment(
					knowledgeBaseAttachmentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(externalReferenceCode: ___, knowledgeBaseArticleExternalReferenceCode: ___, siteKey: ___){contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the knowledge base article attachment by external reference code."
	)
	public KnowledgeBaseAttachment
			knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("knowledgeBaseArticleExternalReferenceCode") String
					knowledgeBaseArticleExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.
					getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(
						Long.valueOf(siteKey),
						knowledgeBaseArticleExternalReferenceCode,
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseFolder(knowledgeBaseFolderId: ___){actions, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfKnowledgeBaseArticles, numberOfKnowledgeBaseFolders, parentKnowledgeBaseFolder, parentKnowledgeBaseFolderId, siteId, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the knowledge base folder.")
	public KnowledgeBaseFolder knowledgeBaseFolder(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.getKnowledgeBaseFolder(
					knowledgeBaseFolderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseFolderPermissions(knowledgeBaseFolderId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KnowledgeBaseFolderPage knowledgeBaseFolderPermissions(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource -> new KnowledgeBaseFolderPage(
				knowledgeBaseFolderResource.
					getKnowledgeBaseFolderPermissionsPage(
						knowledgeBaseFolderId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseFolderKnowledgeBaseFolders(page: ___, pageSize: ___, parentKnowledgeBaseFolderId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the knowledge base folder's subfolders."
	)
	public KnowledgeBaseFolderPage knowledgeBaseFolderKnowledgeBaseFolders(
			@GraphQLName("parentKnowledgeBaseFolderId") Long
				parentKnowledgeBaseFolderId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource -> new KnowledgeBaseFolderPage(
				knowledgeBaseFolderResource.
					getKnowledgeBaseFolderKnowledgeBaseFoldersPage(
						parentKnowledgeBaseFolderId,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseFolders(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's knowledge base folders. Results can be paginated."
	)
	public KnowledgeBaseFolderPage knowledgeBaseFolders(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource -> new KnowledgeBaseFolderPage(
				knowledgeBaseFolderResource.getSiteKnowledgeBaseFoldersPage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {knowledgeBaseFolderByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfKnowledgeBaseArticles, numberOfKnowledgeBaseFolders, parentKnowledgeBaseFolder, parentKnowledgeBaseFolderId, siteId, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's knowledge base folder by external reference code."
	)
	public KnowledgeBaseFolder knowledgeBaseFolderByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.
					getSiteKnowledgeBaseFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteKnowledgeBaseFolderPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public KnowledgeBaseFolderPage siteKnowledgeBaseFolderPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource -> new KnowledgeBaseFolderPage(
				knowledgeBaseFolderResource.
					getSiteKnowledgeBaseFolderPermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryLanguages(assetLibraryId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the asset libraries languages.")
	public LanguagePage assetLibraryLanguages(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageResource -> new LanguagePage(
				languageResource.getAssetLibraryLanguagesPage(
					Long.valueOf(assetLibraryId))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {languages(siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the site's languages.")
	public LanguagePage languages(
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageResource -> new LanguagePage(
				languageResource.getSiteLanguagesPage(Long.valueOf(siteKey))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardAttachment(messageBoardAttachmentId: ___){actions, contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the message board attachment.")
	public MessageBoardAttachment messageBoardAttachment(
			@GraphQLName("messageBoardAttachmentId") Long
				messageBoardAttachmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.getMessageBoardAttachment(
					messageBoardAttachmentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessageMessageBoardAttachments(messageBoardMessageId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the message board message's attachments."
	)
	public MessageBoardAttachmentPage
			messageBoardMessageMessageBoardAttachments(
				@GraphQLName("messageBoardMessageId") Long
					messageBoardMessageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource -> new MessageBoardAttachmentPage(
				messageBoardAttachmentResource.
					getMessageBoardMessageMessageBoardAttachmentsPage(
						messageBoardMessageId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThreadMessageBoardAttachments(messageBoardThreadId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the message board thread's attachments."
	)
	public MessageBoardAttachmentPage messageBoardThreadMessageBoardAttachments(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource -> new MessageBoardAttachmentPage(
				messageBoardAttachmentResource.
					getMessageBoardThreadMessageBoardAttachmentsPage(
						messageBoardThreadId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(externalReferenceCode: ___, messageBoardMessageExternalReferenceCode: ___, siteKey: ___){actions, contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the message board attachment by message board message's and message board attachment's external reference codes."
	)
	public MessageBoardAttachment
			messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("messageBoardMessageExternalReferenceCode") String
					messageBoardMessageExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					getSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(
						Long.valueOf(siteKey),
						messageBoardMessageExternalReferenceCode,
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessage(messageBoardMessageId: ___){actions, aggregateRating, anonymous, articleBody, creator, creatorStatistics, customFields, dateCreated, dateModified, encodingFormat, externalReferenceCode, friendlyUrlPath, hasCompanyMx, headline, id, keywords, messageBoardSectionId, messageBoardThreadId, modified, numberOfMessageBoardAttachments, numberOfMessageBoardMessages, parentMessageBoardMessageId, relatedContents, showAsAnswer, siteId, status, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the message board message.")
	public MessageBoardMessage messageBoardMessage(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.getMessageBoardMessage(
					messageBoardMessageId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessageMyRating(messageBoardMessageId: ___){actions, bestRating, creator, dateCreated, dateModified, id, ratingValue, worstRating}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the message board message's rating.")
	public Rating messageBoardMessageMyRating(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.getMessageBoardMessageMyRating(
					messageBoardMessageId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessagePermissions(messageBoardMessageId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardMessagePage messageBoardMessagePermissions(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> new MessageBoardMessagePage(
				messageBoardMessageResource.
					getMessageBoardMessagePermissionsPage(
						messageBoardMessageId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessageMessageBoardMessages(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, parentMessageBoardMessageId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent message board message's child messages. Results can be paginated, filtered, searched, and sorted."
	)
	public MessageBoardMessagePage messageBoardMessageMessageBoardMessages(
			@GraphQLName("parentMessageBoardMessageId") Long
				parentMessageBoardMessageId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> new MessageBoardMessagePage(
				messageBoardMessageResource.
					getMessageBoardMessageMessageBoardMessagesPage(
						parentMessageBoardMessageId, flatten, search,
						_aggregationBiFunction.apply(
							messageBoardMessageResource, aggregations),
						_filterBiFunction.apply(
							messageBoardMessageResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							messageBoardMessageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThreadMessageBoardMessages(aggregation: ___, filter: ___, messageBoardThreadId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the message board thread's messages. Results can be paginated, filtered, searched, and sorted."
	)
	public MessageBoardMessagePage messageBoardThreadMessageBoardMessages(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> new MessageBoardMessagePage(
				messageBoardMessageResource.
					getMessageBoardThreadMessageBoardMessagesPage(
						messageBoardThreadId, search,
						_aggregationBiFunction.apply(
							messageBoardMessageResource, aggregations),
						_filterBiFunction.apply(
							messageBoardMessageResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							messageBoardMessageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessages(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the site's message board messages.")
	public MessageBoardMessagePage messageBoardMessages(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> new MessageBoardMessagePage(
				messageBoardMessageResource.getSiteMessageBoardMessagesPage(
					Long.valueOf(siteKey), flatten, search,
					_aggregationBiFunction.apply(
						messageBoardMessageResource, aggregations),
					_filterBiFunction.apply(
						messageBoardMessageResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						messageBoardMessageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessageByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, aggregateRating, anonymous, articleBody, creator, creatorStatistics, customFields, dateCreated, dateModified, encodingFormat, externalReferenceCode, friendlyUrlPath, hasCompanyMx, headline, id, keywords, messageBoardSectionId, messageBoardThreadId, modified, numberOfMessageBoardAttachments, numberOfMessageBoardMessages, parentMessageBoardMessageId, relatedContents, showAsAnswer, siteId, status, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's message board message by external reference code."
	)
	public MessageBoardMessage messageBoardMessageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					getSiteMessageBoardMessageByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardMessageByFriendlyUrlPath(friendlyUrlPath: ___, siteKey: ___){actions, aggregateRating, anonymous, articleBody, creator, creatorStatistics, customFields, dateCreated, dateModified, encodingFormat, externalReferenceCode, friendlyUrlPath, hasCompanyMx, headline, id, keywords, messageBoardSectionId, messageBoardThreadId, modified, numberOfMessageBoardAttachments, numberOfMessageBoardMessages, parentMessageBoardMessageId, relatedContents, showAsAnswer, siteId, status, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardMessage messageBoardMessageByFriendlyUrlPath(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					getSiteMessageBoardMessageByFriendlyUrlPath(
						Long.valueOf(siteKey), friendlyUrlPath));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteMessageBoardMessagePermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardMessagePage siteMessageBoardMessagePermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> new MessageBoardMessagePage(
				messageBoardMessageResource.
					getSiteMessageBoardMessagePermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {userMessageBoardMessagesActivity(page: ___, pageSize: ___, siteKey: ___, userId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's message board messages user's activity."
	)
	public MessageBoardMessagePage userMessageBoardMessagesActivity(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("userId") Long userId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> new MessageBoardMessagePage(
				messageBoardMessageResource.
					getSiteUserMessageBoardMessagesActivityPage(
						Long.valueOf(siteKey), userId,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardSection(messageBoardSectionId: ___){actions, creator, customFields, dateCreated, dateModified, description, friendlyUrlPath, id, numberOfMessageBoardSections, numberOfMessageBoardThreads, parentMessageBoardSectionId, siteId, subscribed, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the message board section.")
	public MessageBoardSection messageBoardSection(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.getMessageBoardSection(
					messageBoardSectionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardSectionPermissions(messageBoardSectionId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardSectionPage messageBoardSectionPermissions(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource -> new MessageBoardSectionPage(
				messageBoardSectionResource.
					getMessageBoardSectionPermissionsPage(
						messageBoardSectionId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardSectionMessageBoardSections(aggregation: ___, filter: ___, page: ___, pageSize: ___, parentMessageBoardSectionId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent message board section's subsections. Results can be paginated, filtered, searched, and sorted."
	)
	public MessageBoardSectionPage messageBoardSectionMessageBoardSections(
			@GraphQLName("parentMessageBoardSectionId") Long
				parentMessageBoardSectionId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource -> new MessageBoardSectionPage(
				messageBoardSectionResource.
					getMessageBoardSectionMessageBoardSectionsPage(
						parentMessageBoardSectionId, search,
						_aggregationBiFunction.apply(
							messageBoardSectionResource, aggregations),
						_filterBiFunction.apply(
							messageBoardSectionResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							messageBoardSectionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardSectionByFriendlyUrlPath(friendlyUrlPath: ___, siteKey: ___){actions, creator, customFields, dateCreated, dateModified, description, friendlyUrlPath, id, numberOfMessageBoardSections, numberOfMessageBoardThreads, parentMessageBoardSectionId, siteId, subscribed, title, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardSection messageBoardSectionByFriendlyUrlPath(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.
					getSiteMessageBoardSectionByFriendlyUrlPath(
						Long.valueOf(siteKey), friendlyUrlPath));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardSections(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's message board sections. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public MessageBoardSectionPage messageBoardSections(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource -> new MessageBoardSectionPage(
				messageBoardSectionResource.getSiteMessageBoardSectionsPage(
					Long.valueOf(siteKey), flatten, search,
					_aggregationBiFunction.apply(
						messageBoardSectionResource, aggregations),
					_filterBiFunction.apply(
						messageBoardSectionResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						messageBoardSectionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteMessageBoardSectionPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardSectionPage siteMessageBoardSectionPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource -> new MessageBoardSectionPage(
				messageBoardSectionResource.
					getSiteMessageBoardSectionPermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardSectionMessageBoardThreads(aggregation: ___, filter: ___, messageBoardSectionId: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the message board section's threads. Results can be paginated, filtered, searched, and sorted."
	)
	public MessageBoardThreadPage messageBoardSectionMessageBoardThreads(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource -> new MessageBoardThreadPage(
				messageBoardThreadResource.
					getMessageBoardSectionMessageBoardThreadsPage(
						messageBoardSectionId, search,
						_aggregationBiFunction.apply(
							messageBoardThreadResource, aggregations),
						_filterBiFunction.apply(
							messageBoardThreadResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							messageBoardThreadResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThreadsRanked(dateCreated: ___, dateModified: ___, messageBoardSectionId: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardThreadPage messageBoardThreadsRanked(
			@GraphQLName("dateCreated") Date dateCreated,
			@GraphQLName("dateModified") Date dateModified,
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource -> new MessageBoardThreadPage(
				messageBoardThreadResource.getMessageBoardThreadsRankedPage(
					dateCreated, dateModified, messageBoardSectionId,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						messageBoardThreadResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThread(messageBoardThreadId: ___){actions, aggregateRating, articleBody, creator, creatorStatistics, customFields, dateCreated, dateModified, encodingFormat, friendlyUrlPath, hasValidAnswer, headline, id, keywords, lastPostDate, locked, messageBoardRootMessageId, messageBoardSectionId, numberOfMessageBoardAttachments, numberOfMessageBoardMessages, relatedContents, seen, showAsQuestion, siteId, status, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, threadType, viewCount, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the message board thread.")
	public MessageBoardThread messageBoardThread(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.getMessageBoardThread(
					messageBoardThreadId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThreadMyRating(messageBoardThreadId: ___){actions, bestRating, creator, dateCreated, dateModified, id, ratingValue, worstRating}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the message board thread's rating.")
	public Rating messageBoardThreadMyRating(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.getMessageBoardThreadMyRating(
					messageBoardThreadId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThreadPermissions(messageBoardThreadId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardThreadPage messageBoardThreadPermissions(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource -> new MessageBoardThreadPage(
				messageBoardThreadResource.getMessageBoardThreadPermissionsPage(
					messageBoardThreadId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThreads(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's message board threads. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public MessageBoardThreadPage messageBoardThreads(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource -> new MessageBoardThreadPage(
				messageBoardThreadResource.getSiteMessageBoardThreadsPage(
					Long.valueOf(siteKey), flatten, search,
					_aggregationBiFunction.apply(
						messageBoardThreadResource, aggregations),
					_filterBiFunction.apply(
						messageBoardThreadResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						messageBoardThreadResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {messageBoardThreadByFriendlyUrlPath(friendlyUrlPath: ___, siteKey: ___){actions, aggregateRating, articleBody, creator, creatorStatistics, customFields, dateCreated, dateModified, encodingFormat, friendlyUrlPath, hasValidAnswer, headline, id, keywords, lastPostDate, locked, messageBoardRootMessageId, messageBoardSectionId, numberOfMessageBoardAttachments, numberOfMessageBoardMessages, relatedContents, seen, showAsQuestion, siteId, status, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, threadType, viewCount, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardThread messageBoardThreadByFriendlyUrlPath(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.
					getSiteMessageBoardThreadByFriendlyUrlPath(
						Long.valueOf(siteKey), friendlyUrlPath));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteMessageBoardThreadPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public MessageBoardThreadPage siteMessageBoardThreadPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource -> new MessageBoardThreadPage(
				messageBoardThreadResource.
					getSiteMessageBoardThreadPermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {navigationMenu(navigationMenuId: ___){actions, creator, dateCreated, dateModified, id, name, navigationMenuItems, navigationType, siteId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NavigationMenu navigationMenu(
			@GraphQLName("navigationMenuId") Long navigationMenuId)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource -> navigationMenuResource.getNavigationMenu(
				navigationMenuId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {navigationMenuPermissions(navigationMenuId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NavigationMenuPage navigationMenuPermissions(
			@GraphQLName("navigationMenuId") Long navigationMenuId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource -> new NavigationMenuPage(
				navigationMenuResource.getNavigationMenuPermissionsPage(
					navigationMenuId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {navigationMenus(page: ___, pageSize: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NavigationMenuPage navigationMenus(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource -> new NavigationMenuPage(
				navigationMenuResource.getSiteNavigationMenusPage(
					Long.valueOf(siteKey), Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteNavigationMenuPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NavigationMenuPage siteNavigationMenuPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource -> new NavigationMenuPage(
				navigationMenuResource.getSiteNavigationMenuPermissionsPage(
					Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sitePages(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the public pages of the site")
	public SitePagePage sitePages(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource -> new SitePagePage(
				sitePageResource.getSiteSitePagesPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						sitePageResource, aggregations),
					_filterBiFunction.apply(sitePageResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(sitePageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sitePage(friendlyUrlPath: ___, siteKey: ___){actions, aggregateRating, availableLanguages, creator, customFields, dateCreated, dateModified, datePublished, experience, friendlyUrlPath, friendlyUrlPath_i18n, id, keywords, pageDefinition, pagePermissions, pageSettings, pageType, parentSitePage, renderedPage, siteId, taxonomyCategoryBriefs, taxonomyCategoryIds, title, title_i18n, uuid, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a specific public page of a site")
	public SitePage sitePage(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource -> sitePageResource.getSiteSitePage(
				Long.valueOf(siteKey), friendlyUrlPath));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sitePagesExperiences(friendlyUrlPath: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the experiences of a given Page")
	public SitePagePage sitePagesExperiences(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource -> new SitePagePage(
				sitePageResource.getSiteSitePagesExperiencesPage(
					Long.valueOf(siteKey), friendlyUrlPath)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sitePageExperienceExperienceKey(experienceKey: ___, friendlyUrlPath: ___, siteKey: ___){actions, aggregateRating, availableLanguages, creator, customFields, dateCreated, dateModified, datePublished, experience, friendlyUrlPath, friendlyUrlPath_i18n, id, keywords, pageDefinition, pagePermissions, pageSettings, pageType, parentSitePage, renderedPage, siteId, taxonomyCategoryBriefs, taxonomyCategoryIds, title, title_i18n, uuid, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a specific public page of a site for a given experience"
	)
	public SitePage sitePageExperienceExperienceKey(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath,
			@GraphQLName("experienceKey") String experienceKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.getSiteSitePageExperienceExperienceKey(
					Long.valueOf(siteKey), friendlyUrlPath, experienceKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sitePageExperienceExperienceKeyRenderedPage(experienceKey: ___, friendlyUrlPath: ___, siteKey: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the rendered content of a given public page for a given experience."
	)
	public String sitePageExperienceExperienceKeyRenderedPage(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath,
			@GraphQLName("experienceKey") String experienceKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.
					getSiteSitePageExperienceExperienceKeyRenderedPage(
						Long.valueOf(siteKey), friendlyUrlPath, experienceKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sitePageRenderedPage(friendlyUrlPath: ___, siteKey: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the rendered content of a given public page."
	)
	public String sitePageRenderedPage(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("friendlyUrlPath") String friendlyUrlPath)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource -> sitePageResource.getSiteSitePageRenderedPage(
				Long.valueOf(siteKey), friendlyUrlPath));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStructuredContents(aggregation: ___, assetLibraryId: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentPage assetLibraryStructuredContents(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> new StructuredContentPage(
				structuredContentResource.getAssetLibraryStructuredContentsPage(
					Long.valueOf(assetLibraryId), flatten, search,
					_aggregationBiFunction.apply(
						structuredContentResource, aggregations),
					_filterBiFunction.apply(
						structuredContentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						structuredContentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStructuredContentByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){actions, aggregateRating, assetLibraryKey, availableLanguages, contentFields, contentStructureId, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, description_i18n, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, key, keywords, neverExpire, numberOfComments, permissions, priority, relatedContents, renderedContents, siteId, structuredContentFolderId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, title, title_i18n, uuid, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContent
			assetLibraryStructuredContentByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					getAssetLibraryStructuredContentByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStructuredContentPermissions(assetLibraryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentPage assetLibraryStructuredContentPermissions(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> new StructuredContentPage(
				structuredContentResource.
					getAssetLibraryStructuredContentPermissionsPage(
						Long.valueOf(assetLibraryId), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {contentStructureStructuredContents(aggregation: ___, contentStructureId: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a list of the content structure's structured content. Results can be paginated, filtered, searched, and sorted."
	)
	public StructuredContentPage contentStructureStructuredContents(
			@GraphQLName("contentStructureId") Long contentStructureId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> new StructuredContentPage(
				structuredContentResource.
					getContentStructureStructuredContentsPage(
						contentStructureId, search,
						_aggregationBiFunction.apply(
							structuredContentResource, aggregations),
						_filterBiFunction.apply(
							structuredContentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							structuredContentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContents(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's structured content. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public StructuredContentPage structuredContents(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> new StructuredContentPage(
				structuredContentResource.getSiteStructuredContentsPage(
					Long.valueOf(siteKey), flatten, search,
					_aggregationBiFunction.apply(
						structuredContentResource, aggregations),
					_filterBiFunction.apply(
						structuredContentResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						structuredContentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, aggregateRating, assetLibraryKey, availableLanguages, contentFields, contentStructureId, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, description_i18n, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, key, keywords, neverExpire, numberOfComments, permissions, priority, relatedContents, renderedContents, siteId, structuredContentFolderId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, title, title_i18n, uuid, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's structured content by external reference code."
	)
	public StructuredContent structuredContentByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					getSiteStructuredContentByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentByKey(key: ___, siteKey: ___){actions, aggregateRating, assetLibraryKey, availableLanguages, contentFields, contentStructureId, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, description_i18n, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, key, keywords, neverExpire, numberOfComments, permissions, priority, relatedContents, renderedContents, siteId, structuredContentFolderId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, title, title_i18n, uuid, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves a structured content by its key (`articleKey`)."
	)
	public StructuredContent structuredContentByKey(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("key") String key)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.getSiteStructuredContentByKey(
					Long.valueOf(siteKey), key));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentByUuid(siteKey: ___, uuid: ___){actions, aggregateRating, assetLibraryKey, availableLanguages, contentFields, contentStructureId, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, description_i18n, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, key, keywords, neverExpire, numberOfComments, permissions, priority, relatedContents, renderedContents, siteId, structuredContentFolderId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, title, title_i18n, uuid, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves a structured content by its UUID.")
	public StructuredContent structuredContentByUuid(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("uuid") String uuid)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.getSiteStructuredContentByUuid(
					Long.valueOf(siteKey), uuid));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteStructuredContentPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentPage siteStructuredContentPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> new StructuredContentPage(
				structuredContentResource.
					getSiteStructuredContentPermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentFolderStructuredContents(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___, structuredContentFolderId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the folder's structured content. Results can be paginated, filtered, searched, and sorted."
	)
	public StructuredContentPage structuredContentFolderStructuredContents(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> new StructuredContentPage(
				structuredContentResource.
					getStructuredContentFolderStructuredContentsPage(
						structuredContentFolderId, flatten, search,
						_aggregationBiFunction.apply(
							structuredContentResource, aggregations),
						_filterBiFunction.apply(
							structuredContentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							structuredContentResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContent(structuredContentId: ___){actions, aggregateRating, assetLibraryKey, availableLanguages, contentFields, contentStructureId, creator, customFields, dateCreated, dateExpired, dateModified, datePublished, description, description_i18n, externalReferenceCode, friendlyUrlPath, friendlyUrlPath_i18n, id, key, keywords, neverExpire, numberOfComments, permissions, priority, relatedContents, renderedContents, siteId, structuredContentFolderId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, title, title_i18n, uuid, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the structured content via its ID.")
	public StructuredContent structuredContent(
			@GraphQLName("structuredContentId") Long structuredContentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.getStructuredContent(
					structuredContentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentMyRating(structuredContentId: ___){actions, bestRating, creator, dateCreated, dateModified, id, ratingValue, worstRating}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the structured content's rating.")
	public Rating structuredContentMyRating(
			@GraphQLName("structuredContentId") Long structuredContentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.getStructuredContentMyRating(
					structuredContentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentPermissions(roleNames: ___, structuredContentId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentPage structuredContentPermissions(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> new StructuredContentPage(
				structuredContentResource.getStructuredContentPermissionsPage(
					structuredContentId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentRenderedContentByDisplayPageDisplayPageKey(displayPageKey: ___, structuredContentId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the structured content's rendered display page"
	)
	public String structuredContentRenderedContentByDisplayPageDisplayPageKey(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("displayPageKey") String displayPageKey)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					getStructuredContentRenderedContentByDisplayPageDisplayPageKey(
						structuredContentId, displayPageKey));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentRenderedContentContentTemplate(contentTemplateId: ___, structuredContentId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the structured content's rendered template (the result of applying the structure's values to a template)."
	)
	public String structuredContentRenderedContentContentTemplate(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("contentTemplateId") String contentTemplateId)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					getStructuredContentRenderedContentContentTemplate(
						structuredContentId, contentTemplateId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStructuredContentFolders(aggregation: ___, assetLibraryId: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentFolderPage assetLibraryStructuredContentFolders(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> new StructuredContentFolderPage(
				structuredContentFolderResource.
					getAssetLibraryStructuredContentFoldersPage(
						Long.valueOf(assetLibraryId), flatten, search,
						_aggregationBiFunction.apply(
							structuredContentFolderResource, aggregations),
						_filterBiFunction.apply(
							structuredContentFolderResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							structuredContentFolderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStructuredContentFolderByExternalReferenceCode(assetLibraryId: ___, externalReferenceCode: ___){actions, assetLibraryKey, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfStructuredContentFolders, numberOfStructuredContents, parentStructuredContentFolderId, siteId, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the asset library's structured content folder by external reference code."
	)
	public StructuredContentFolder
			assetLibraryStructuredContentFolderByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					getAssetLibraryStructuredContentFolderByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {assetLibraryStructuredContentFolderPermissions(assetLibraryId: ___, roleNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentFolderPage
			assetLibraryStructuredContentFolderPermissions(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> new StructuredContentFolderPage(
				structuredContentFolderResource.
					getAssetLibraryStructuredContentFolderPermissionsPage(
						Long.valueOf(assetLibraryId), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentFolders(aggregation: ___, filter: ___, flatten: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's structured content folders. Results can be paginated, filtered, searched, flattened, and sorted."
	)
	public StructuredContentFolderPage structuredContentFolders(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("flatten") Boolean flatten,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> new StructuredContentFolderPage(
				structuredContentFolderResource.
					getSiteStructuredContentFoldersPage(
						Long.valueOf(siteKey), flatten, search,
						_aggregationBiFunction.apply(
							structuredContentFolderResource, aggregations),
						_filterBiFunction.apply(
							structuredContentFolderResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							structuredContentFolderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentFolderByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, assetLibraryKey, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfStructuredContentFolders, numberOfStructuredContents, parentStructuredContentFolderId, siteId, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentFolder
			structuredContentFolderByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					getSiteStructuredContentFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteStructuredContentFolderPermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentFolderPage siteStructuredContentFolderPermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> new StructuredContentFolderPage(
				structuredContentFolderResource.
					getSiteStructuredContentFolderPermissionsPage(
						Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentFolderPermissions(roleNames: ___, structuredContentFolderId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public StructuredContentFolderPage structuredContentFolderPermissions(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> new StructuredContentFolderPage(
				structuredContentFolderResource.
					getStructuredContentFolderPermissionsPage(
						structuredContentFolderId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentFolderStructuredContentFolders(aggregation: ___, filter: ___, page: ___, pageSize: ___, parentStructuredContentFolderId: ___, search: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the parent structured content folder's subfolders. Results can be paginated, filtered, searched, and sorted."
	)
	public StructuredContentFolderPage
			structuredContentFolderStructuredContentFolders(
				@GraphQLName("parentStructuredContentFolderId") Long
					parentStructuredContentFolderId,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> new StructuredContentFolderPage(
				structuredContentFolderResource.
					getStructuredContentFolderStructuredContentFoldersPage(
						parentStructuredContentFolderId, search,
						_aggregationBiFunction.apply(
							structuredContentFolderResource, aggregations),
						_filterBiFunction.apply(
							structuredContentFolderResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							structuredContentFolderResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {structuredContentFolder(structuredContentFolderId: ___){actions, assetLibraryKey, creator, customFields, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfStructuredContentFolders, numberOfStructuredContents, parentStructuredContentFolderId, siteId, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the structured content folder.")
	public StructuredContentFolder structuredContentFolder(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.getStructuredContentFolder(
					structuredContentFolderId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiNodes(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, siteKey: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the wiki node's of a site. Results can be paginated, filtered, searched, and sorted."
	)
	public WikiNodePage wikiNodes(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> new WikiNodePage(
				wikiNodeResource.getSiteWikiNodesPage(
					Long.valueOf(siteKey), search,
					_aggregationBiFunction.apply(
						wikiNodeResource, aggregations),
					_filterBiFunction.apply(wikiNodeResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(wikiNodeResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiNodeByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, creator, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfWikiPages, siteId, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the site's wiki node by external reference code."
	)
	public WikiNode wikiNodeByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource ->
				wikiNodeResource.getSiteWikiNodeByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {siteWikiNodePermissions(roleNames: ___, siteKey: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WikiNodePage siteWikiNodePermissions(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> new WikiNodePage(
				wikiNodeResource.getSiteWikiNodePermissionsPage(
					Long.valueOf(siteKey), roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiNode(wikiNodeId: ___){actions, creator, dateCreated, dateModified, description, externalReferenceCode, id, name, numberOfWikiPages, siteId, subscribed, viewableBy}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the wiki node")
	public WikiNode wikiNode(@GraphQLName("wikiNodeId") Long wikiNodeId)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.getWikiNode(wikiNodeId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiNodePermissions(roleNames: ___, wikiNodeId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WikiNodePage wikiNodePermissions(
			@GraphQLName("wikiNodeId") Long wikiNodeId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> new WikiNodePage(
				wikiNodeResource.getWikiNodePermissionsPage(
					wikiNodeId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiPageByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___){actions, aggregateRating, content, creator, customFields, dateCreated, dateModified, description, encodingFormat, externalReferenceCode, headline, id, keywords, numberOfAttachments, numberOfWikiPages, parentWikiPageId, relatedContents, siteId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, viewableBy, wikiNodeId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the wiki page by external reference code"
	)
	public WikiPage wikiPageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource ->
				wikiPageResource.getSiteWikiPageByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiNodeWikiPages(aggregation: ___, filter: ___, page: ___, pageSize: ___, search: ___, sorts: ___, wikiNodeId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the wiki page's of a node. Results can be paginated, filtered, searched, and sorted."
	)
	public WikiPagePage wikiNodeWikiPages(
			@GraphQLName("wikiNodeId") Long wikiNodeId,
			@GraphQLName("search") String search,
			@GraphQLName("aggregation") List<String> aggregations,
			@GraphQLName("filter") String filterString,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> new WikiPagePage(
				wikiPageResource.getWikiNodeWikiPagesPage(
					wikiNodeId, search,
					_aggregationBiFunction.apply(
						wikiPageResource, aggregations),
					_filterBiFunction.apply(wikiPageResource, filterString),
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(wikiPageResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiPageWikiPages(parentWikiPageId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the child wiki page's of a wiki page."
	)
	public WikiPagePage wikiPageWikiPages(
			@GraphQLName("parentWikiPageId") Long parentWikiPageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> new WikiPagePage(
				wikiPageResource.getWikiPageWikiPagesPage(parentWikiPageId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiPage(wikiPageId: ___){actions, aggregateRating, content, creator, customFields, dateCreated, dateModified, description, encodingFormat, externalReferenceCode, headline, id, keywords, numberOfAttachments, numberOfWikiPages, parentWikiPageId, relatedContents, siteId, subscribed, taxonomyCategoryBriefs, taxonomyCategoryIds, viewableBy, wikiNodeId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the wiki page")
	public WikiPage wikiPage(@GraphQLName("wikiPageId") Long wikiPageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.getWikiPage(wikiPageId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiPagePermissions(roleNames: ___, wikiPageId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WikiPagePage wikiPagePermissions(
			@GraphQLName("wikiPageId") Long wikiPageId,
			@GraphQLName("roleNames") String roleNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> new WikiPagePage(
				wikiPageResource.getWikiPagePermissionsPage(
					wikiPageId, roleNames)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(externalReferenceCode: ___, siteKey: ___, wikiPageExternalReferenceCode: ___){actions, contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(
		description = "Retrieves the wiki page attachment by wiki page's and wiki page attachment's external reference codes."
	)
	public WikiPageAttachment
			wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("wikiPageExternalReferenceCode") String
					wikiPageExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.
					getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(
						Long.valueOf(siteKey), wikiPageExternalReferenceCode,
						externalReferenceCode));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiPageAttachment(wikiPageAttachmentId: ___){actions, contentUrl, contentValue, encodingFormat, externalReferenceCode, fileExtension, id, sizeInBytes, title}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the wiki page attachment.")
	public WikiPageAttachment wikiPageAttachment(
			@GraphQLName("wikiPageAttachmentId") Long wikiPageAttachmentId)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.getWikiPageAttachment(
					wikiPageAttachmentId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {wikiPageWikiPageAttachments(wikiPageId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField(description = "Retrieves the wiki page's attachments.")
	public WikiPageAttachmentPage wikiPageWikiPageAttachments(
			@GraphQLName("wikiPageId") Long wikiPageId)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource -> new WikiPageAttachmentPage(
				wikiPageAttachmentResource.getWikiPageWikiPageAttachmentsPage(
					wikiPageId)));
	}

	@GraphQLTypeExtension(Document.class)
	public class GetDocumentFolderTypeExtension {

		public GetDocumentFolderTypeExtension(Document document) {
			_document = document;
		}

		@GraphQLField(description = "Retrieves the document folder.")
		public DocumentFolder folder() throws Exception {
			return _applyComponentServiceObjects(
				_documentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentFolderResource ->
					documentFolderResource.getDocumentFolder(
						_document.getDocumentFolderId()));
		}

		private Document _document;

	}

	@GraphQLTypeExtension(MessageBoardThread.class)
	public class GetMessageBoardSectionTypeExtension {

		public GetMessageBoardSectionTypeExtension(
			MessageBoardThread messageBoardThread) {

			_messageBoardThread = messageBoardThread;
		}

		@GraphQLField(description = "Retrieves the message board section.")
		public MessageBoardSection messageBoardSection() throws Exception {
			return _applyComponentServiceObjects(
				_messageBoardSectionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardSectionResource ->
					messageBoardSectionResource.getMessageBoardSection(
						_messageBoardThread.getMessageBoardSectionId()));
		}

		private MessageBoardThread _messageBoardThread;

	}

	@GraphQLTypeExtension(Document.class)
	public class GetDocumentMyRatingTypeExtension {

		public GetDocumentMyRatingTypeExtension(Document document) {
			_document = document;
		}

		@GraphQLField(description = "Retrieves the document's rating.")
		public Rating myRating() throws Exception {
			return _applyComponentServiceObjects(
				_documentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentResource -> documentResource.getDocumentMyRating(
					_document.getId()));
		}

		private Document _document;

	}

	@GraphQLTypeExtension(WikiPage.class)
	public class GetWikiPagePermissionsPageTypeExtension {

		public GetWikiPagePermissionsPageTypeExtension(WikiPage wikiPage) {
			_wikiPage = wikiPage;
		}

		@GraphQLField
		public WikiPagePage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_wikiPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wikiPageResource -> new WikiPagePage(
					wikiPageResource.getWikiPagePermissionsPage(
						_wikiPage.getId(), roleNames)));
		}

		private WikiPage _wikiPage;

	}

	@GraphQLTypeExtension(MessageBoardMessage.class)
	public class
		GetMessageBoardMessageMessageBoardAttachmentsPageTypeExtension {

		public GetMessageBoardMessageMessageBoardAttachmentsPageTypeExtension(
			MessageBoardMessage messageBoardMessage) {

			_messageBoardMessage = messageBoardMessage;
		}

		@GraphQLField(
			description = "Retrieves the message board message's attachments."
		)
		public MessageBoardAttachmentPage messageBoardAttachments()
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardAttachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardAttachmentResource ->
					new MessageBoardAttachmentPage(
						messageBoardAttachmentResource.
							getMessageBoardMessageMessageBoardAttachmentsPage(
								_messageBoardMessage.getId())));
		}

		private MessageBoardMessage _messageBoardMessage;

	}

	@GraphQLTypeExtension(DocumentFolder.class)
	public class GetDocumentFolderDocumentsPageTypeExtension {

		public GetDocumentFolderDocumentsPageTypeExtension(
			DocumentFolder documentFolder) {

			_documentFolder = documentFolder;
		}

		@GraphQLField(
			description = "Retrieves the folder's documents. Results can be paginated, filtered, searched, and sorted."
		)
		public DocumentPage documents(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_documentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentResource -> new DocumentPage(
					documentResource.getDocumentFolderDocumentsPage(
						_documentFolder.getId(), flatten, search,
						_aggregationBiFunction.apply(
							documentResource, aggregations),
						_filterBiFunction.apply(documentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							documentResource, sortsString))));
		}

		private DocumentFolder _documentFolder;

	}

	@GraphQLTypeExtension(StructuredContentFolder.class)
	public class GetStructuredContentFolderPermissionsPageTypeExtension {

		public GetStructuredContentFolderPermissionsPageTypeExtension(
			StructuredContentFolder structuredContentFolder) {

			_structuredContentFolder = structuredContentFolder;
		}

		@GraphQLField
		public StructuredContentFolderPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_structuredContentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentFolderResource ->
					new StructuredContentFolderPage(
						structuredContentFolderResource.
							getStructuredContentFolderPermissionsPage(
								_structuredContentFolder.getId(), roleNames)));
		}

		private StructuredContentFolder _structuredContentFolder;

	}

	@GraphQLTypeExtension(WikiPage.class)
	public class GetWikiPageWikiPageAttachmentsPageTypeExtension {

		public GetWikiPageWikiPageAttachmentsPageTypeExtension(
			WikiPage wikiPage) {

			_wikiPage = wikiPage;
		}

		@GraphQLField(description = "Retrieves the wiki page's attachments.")
		public WikiPageAttachmentPage wikiPageAttachments() throws Exception {
			return _applyComponentServiceObjects(
				_wikiPageAttachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wikiPageAttachmentResource -> new WikiPageAttachmentPage(
					wikiPageAttachmentResource.
						getWikiPageWikiPageAttachmentsPage(_wikiPage.getId())));
		}

		private WikiPage _wikiPage;

	}

	@GraphQLTypeExtension(StructuredContent.class)
	public class
		GetStructuredContentRenderedContentContentTemplateTypeExtension {

		public GetStructuredContentRenderedContentContentTemplateTypeExtension(
			StructuredContent structuredContent) {

			_structuredContent = structuredContent;
		}

		@GraphQLField(
			description = "Retrieves the structured content's rendered template (the result of applying the structure's values to a template)."
		)
		public String renderedContentContentTemplate(
				@GraphQLName("contentTemplateId") String contentTemplateId)
			throws Exception {

			return _applyComponentServiceObjects(
				_structuredContentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentResource ->
					structuredContentResource.
						getStructuredContentRenderedContentContentTemplate(
							_structuredContent.getId(), contentTemplateId));
		}

		private StructuredContent _structuredContent;

	}

	@GraphQLTypeExtension(WikiNode.class)
	public class GetWikiNodeWikiPagesPageTypeExtension {

		public GetWikiNodeWikiPagesPageTypeExtension(WikiNode wikiNode) {
			_wikiNode = wikiNode;
		}

		@GraphQLField(
			description = "Retrieves the wiki page's of a node. Results can be paginated, filtered, searched, and sorted."
		)
		public WikiPagePage wikiPages(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_wikiPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wikiPageResource -> new WikiPagePage(
					wikiPageResource.getWikiNodeWikiPagesPage(
						_wikiNode.getId(), search,
						_aggregationBiFunction.apply(
							wikiPageResource, aggregations),
						_filterBiFunction.apply(wikiPageResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							wikiPageResource, sortsString))));
		}

		private WikiNode _wikiNode;

	}

	@GraphQLTypeExtension(BlogPosting.class)
	public class GetBlogPostingPermissionsPageTypeExtension {

		public GetBlogPostingPermissionsPageTypeExtension(
			BlogPosting blogPosting) {

			_blogPosting = blogPosting;
		}

		@GraphQLField
		public BlogPostingPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_blogPostingResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				blogPostingResource -> new BlogPostingPage(
					blogPostingResource.getBlogPostingPermissionsPage(
						_blogPosting.getId(), roleNames)));
		}

		private BlogPosting _blogPosting;

	}

	@GraphQLTypeExtension(DocumentFolder.class)
	public class GetDocumentFolderDocumentFoldersPageTypeExtension {

		public GetDocumentFolderDocumentFoldersPageTypeExtension(
			DocumentFolder documentFolder) {

			_documentFolder = documentFolder;
		}

		@GraphQLField(
			description = "Retrieves the folder's subfolders. Results can be paginated, filtered, searched, and sorted."
		)
		public DocumentFolderPage documentFolders(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_documentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentFolderResource -> new DocumentFolderPage(
					documentFolderResource.getDocumentFolderDocumentFoldersPage(
						_documentFolder.getId(), flatten, search,
						_aggregationBiFunction.apply(
							documentFolderResource, aggregations),
						_filterBiFunction.apply(
							documentFolderResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							documentFolderResource, sortsString))));
		}

		private DocumentFolder _documentFolder;

	}

	@GraphQLTypeExtension(KnowledgeBaseFolder.class)
	public class GetKnowledgeBaseFolderKnowledgeBaseArticlesPageTypeExtension {

		public GetKnowledgeBaseFolderKnowledgeBaseArticlesPageTypeExtension(
			KnowledgeBaseFolder knowledgeBaseFolder) {

			_knowledgeBaseFolder = knowledgeBaseFolder;
		}

		@GraphQLField(
			description = "Retrieves the folder's knowledge base articles. Results can be paginated, filtered, searched, flattened, and sorted."
		)
		public KnowledgeBaseArticlePage knowledgeBaseArticles(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_knowledgeBaseArticleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
					knowledgeBaseArticleResource.
						getKnowledgeBaseFolderKnowledgeBaseArticlesPage(
							_knowledgeBaseFolder.getId(), flatten, search,
							_aggregationBiFunction.apply(
								knowledgeBaseArticleResource, aggregations),
							_filterBiFunction.apply(
								knowledgeBaseArticleResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								knowledgeBaseArticleResource, sortsString))));
		}

		private KnowledgeBaseFolder _knowledgeBaseFolder;

	}

	@GraphQLTypeExtension(StructuredContent.class)
	public class GetStructuredContentMyRatingTypeExtension {

		public GetStructuredContentMyRatingTypeExtension(
			StructuredContent structuredContent) {

			_structuredContent = structuredContent;
		}

		@GraphQLField(
			description = "Retrieves the structured content's rating."
		)
		public Rating myRating() throws Exception {
			return _applyComponentServiceObjects(
				_structuredContentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentResource ->
					structuredContentResource.getStructuredContentMyRating(
						_structuredContent.getId()));
		}

		private StructuredContent _structuredContent;

	}

	@GraphQLTypeExtension(KnowledgeBaseArticle.class)
	public class
		GetKnowledgeBaseArticleKnowledgeBaseAttachmentsPageTypeExtension {

		public GetKnowledgeBaseArticleKnowledgeBaseAttachmentsPageTypeExtension(
			KnowledgeBaseArticle knowledgeBaseArticle) {

			_knowledgeBaseArticle = knowledgeBaseArticle;
		}

		@GraphQLField(
			description = "Retrieves the knowledge base article's attachments."
		)
		public KnowledgeBaseAttachmentPage knowledgeBaseAttachments()
			throws Exception {

			return _applyComponentServiceObjects(
				_knowledgeBaseAttachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseAttachmentResource ->
					new KnowledgeBaseAttachmentPage(
						knowledgeBaseAttachmentResource.
							getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage(
								_knowledgeBaseArticle.getId())));
		}

		private KnowledgeBaseArticle _knowledgeBaseArticle;

	}

	@GraphQLTypeExtension(StructuredContent.class)
	public class
		GetStructuredContentRenderedContentByDisplayPageDisplayPageKeyTypeExtension {

		public GetStructuredContentRenderedContentByDisplayPageDisplayPageKeyTypeExtension(
			StructuredContent structuredContent) {

			_structuredContent = structuredContent;
		}

		@GraphQLField(
			description = "Retrieves the structured content's rendered display page"
		)
		public String renderedContentByDisplayPageDisplayPageKey(
				@GraphQLName("displayPageKey") String displayPageKey)
			throws Exception {

			return _applyComponentServiceObjects(
				_structuredContentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentResource ->
					structuredContentResource.
						getStructuredContentRenderedContentByDisplayPageDisplayPageKey(
							_structuredContent.getId(), displayPageKey));
		}

		private StructuredContent _structuredContent;

	}

	@GraphQLTypeExtension(MessageBoardMessage.class)
	public class GetMessageBoardMessageMyRatingTypeExtension {

		public GetMessageBoardMessageMyRatingTypeExtension(
			MessageBoardMessage messageBoardMessage) {

			_messageBoardMessage = messageBoardMessage;
		}

		@GraphQLField(
			description = "Retrieves the message board message's rating."
		)
		public Rating myRating() throws Exception {
			return _applyComponentServiceObjects(
				_messageBoardMessageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardMessageResource ->
					messageBoardMessageResource.getMessageBoardMessageMyRating(
						_messageBoardMessage.getId()));
		}

		private MessageBoardMessage _messageBoardMessage;

	}

	@GraphQLTypeExtension(DocumentFolder.class)
	public class GetDocumentFolderMyRatingTypeExtension {

		public GetDocumentFolderMyRatingTypeExtension(
			DocumentFolder documentFolder) {

			_documentFolder = documentFolder;
		}

		@GraphQLField(description = "Retrieves the document folder's rating.")
		public Rating myRating() throws Exception {
			return _applyComponentServiceObjects(
				_documentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentFolderResource ->
					documentFolderResource.getDocumentFolderMyRating(
						_documentFolder.getId()));
		}

		private DocumentFolder _documentFolder;

	}

	@GraphQLTypeExtension(Comment.class)
	public class GetCommentCommentsPageTypeExtension {

		public GetCommentCommentsPageTypeExtension(Comment comment) {
			_comment = comment;
		}

		@GraphQLField(
			description = "Retrieves the parent comment's child comments. Results can be paginated, filtered, searched, and sorted."
		)
		public CommentPage comments(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_commentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				commentResource -> new CommentPage(
					commentResource.getCommentCommentsPage(
						_comment.getId(), search,
						_aggregationBiFunction.apply(
							commentResource, aggregations),
						_filterBiFunction.apply(commentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(commentResource, sortsString))));
		}

		private Comment _comment;

	}

	@GraphQLTypeExtension(StructuredContent.class)
	public class GetContentStructureTypeExtension {

		public GetContentStructureTypeExtension(
			StructuredContent structuredContent) {

			_structuredContent = structuredContent;
		}

		@GraphQLField(description = "Retrieves the content structure.")
		public ContentStructure contentStructure() throws Exception {
			return _applyComponentServiceObjects(
				_contentStructureResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				contentStructureResource ->
					contentStructureResource.getContentStructure(
						_structuredContent.getContentStructureId()));
		}

		private StructuredContent _structuredContent;

	}

	@GraphQLTypeExtension(StructuredContent.class)
	public class GetStructuredContentFolderTypeExtension {

		public GetStructuredContentFolderTypeExtension(
			StructuredContent structuredContent) {

			_structuredContent = structuredContent;
		}

		@GraphQLField(description = "Retrieves the structured content folder.")
		public StructuredContentFolder folder() throws Exception {
			return _applyComponentServiceObjects(
				_structuredContentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentFolderResource ->
					structuredContentFolderResource.getStructuredContentFolder(
						_structuredContent.getStructuredContentFolderId()));
		}

		private StructuredContent _structuredContent;

	}

	@GraphQLTypeExtension(MessageBoardMessage.class)
	public class GetMessageBoardMessagePermissionsPageTypeExtension {

		public GetMessageBoardMessagePermissionsPageTypeExtension(
			MessageBoardMessage messageBoardMessage) {

			_messageBoardMessage = messageBoardMessage;
		}

		@GraphQLField
		public MessageBoardMessagePage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardMessageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardMessageResource -> new MessageBoardMessagePage(
					messageBoardMessageResource.
						getMessageBoardMessagePermissionsPage(
							_messageBoardMessage.getId(), roleNames)));
		}

		private MessageBoardMessage _messageBoardMessage;

	}

	@GraphQLTypeExtension(KnowledgeBaseArticle.class)
	public class GetKnowledgeBaseArticlePermissionsPageTypeExtension {

		public GetKnowledgeBaseArticlePermissionsPageTypeExtension(
			KnowledgeBaseArticle knowledgeBaseArticle) {

			_knowledgeBaseArticle = knowledgeBaseArticle;
		}

		@GraphQLField
		public KnowledgeBaseArticlePage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_knowledgeBaseArticleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
					knowledgeBaseArticleResource.
						getKnowledgeBaseArticlePermissionsPage(
							_knowledgeBaseArticle.getId(), roleNames)));
		}

		private KnowledgeBaseArticle _knowledgeBaseArticle;

	}

	@GraphQLTypeExtension(DocumentFolder.class)
	public class GetDocumentFolderPermissionsPageTypeExtension {

		public GetDocumentFolderPermissionsPageTypeExtension(
			DocumentFolder documentFolder) {

			_documentFolder = documentFolder;
		}

		@GraphQLField
		public DocumentFolderPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_documentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentFolderResource -> new DocumentFolderPage(
					documentFolderResource.getDocumentFolderPermissionsPage(
						_documentFolder.getId(), roleNames)));
		}

		private DocumentFolder _documentFolder;

	}

	@GraphQLTypeExtension(KnowledgeBaseFolder.class)
	public class GetKnowledgeBaseFolderPermissionsPageTypeExtension {

		public GetKnowledgeBaseFolderPermissionsPageTypeExtension(
			KnowledgeBaseFolder knowledgeBaseFolder) {

			_knowledgeBaseFolder = knowledgeBaseFolder;
		}

		@GraphQLField
		public KnowledgeBaseFolderPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_knowledgeBaseFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseFolderResource -> new KnowledgeBaseFolderPage(
					knowledgeBaseFolderResource.
						getKnowledgeBaseFolderPermissionsPage(
							_knowledgeBaseFolder.getId(), roleNames)));
		}

		private KnowledgeBaseFolder _knowledgeBaseFolder;

	}

	@GraphQLTypeExtension(WikiPage.class)
	public class GetWikiNodeTypeExtension {

		public GetWikiNodeTypeExtension(WikiPage wikiPage) {
			_wikiPage = wikiPage;
		}

		@GraphQLField(description = "Retrieves the wiki node")
		public WikiNode wikiNode() throws Exception {
			return _applyComponentServiceObjects(
				_wikiNodeResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wikiNodeResource -> wikiNodeResource.getWikiNode(
					_wikiPage.getWikiNodeId()));
		}

		private WikiPage _wikiPage;

	}

	@GraphQLTypeExtension(KnowledgeBaseArticle.class)
	public class GetKnowledgeBaseArticleKnowledgeBaseArticlesPageTypeExtension {

		public GetKnowledgeBaseArticleKnowledgeBaseArticlesPageTypeExtension(
			KnowledgeBaseArticle knowledgeBaseArticle) {

			_knowledgeBaseArticle = knowledgeBaseArticle;
		}

		@GraphQLField(
			description = "Retrieves the parent knowledge base article's child knowledge base articles. Results can be paginated, filtered, searched, and sorted."
		)
		public KnowledgeBaseArticlePage knowledgeBaseArticles(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_knowledgeBaseArticleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseArticleResource -> new KnowledgeBaseArticlePage(
					knowledgeBaseArticleResource.
						getKnowledgeBaseArticleKnowledgeBaseArticlesPage(
							_knowledgeBaseArticle.getId(), flatten, search,
							_aggregationBiFunction.apply(
								knowledgeBaseArticleResource, aggregations),
							_filterBiFunction.apply(
								knowledgeBaseArticleResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								knowledgeBaseArticleResource, sortsString))));
		}

		private KnowledgeBaseArticle _knowledgeBaseArticle;

	}

	@GraphQLTypeExtension(MessageBoardThread.class)
	public class GetMessageBoardThreadMessageBoardAttachmentsPageTypeExtension {

		public GetMessageBoardThreadMessageBoardAttachmentsPageTypeExtension(
			MessageBoardThread messageBoardThread) {

			_messageBoardThread = messageBoardThread;
		}

		@GraphQLField(
			description = "Retrieves the message board thread's attachments."
		)
		public MessageBoardAttachmentPage messageBoardAttachments()
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardAttachmentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardAttachmentResource ->
					new MessageBoardAttachmentPage(
						messageBoardAttachmentResource.
							getMessageBoardThreadMessageBoardAttachmentsPage(
								_messageBoardThread.getId())));
		}

		private MessageBoardThread _messageBoardThread;

	}

	@GraphQLTypeExtension(NavigationMenu.class)
	public class GetNavigationMenuPermissionsPageTypeExtension {

		public GetNavigationMenuPermissionsPageTypeExtension(
			NavigationMenu navigationMenu) {

			_navigationMenu = navigationMenu;
		}

		@GraphQLField
		public NavigationMenuPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_navigationMenuResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				navigationMenuResource -> new NavigationMenuPage(
					navigationMenuResource.getNavigationMenuPermissionsPage(
						_navigationMenu.getId(), roleNames)));
		}

		private NavigationMenu _navigationMenu;

	}

	@GraphQLTypeExtension(MessageBoardSection.class)
	public class GetMessageBoardSectionPermissionsPageTypeExtension {

		public GetMessageBoardSectionPermissionsPageTypeExtension(
			MessageBoardSection messageBoardSection) {

			_messageBoardSection = messageBoardSection;
		}

		@GraphQLField
		public MessageBoardSectionPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardSectionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardSectionResource -> new MessageBoardSectionPage(
					messageBoardSectionResource.
						getMessageBoardSectionPermissionsPage(
							_messageBoardSection.getId(), roleNames)));
		}

		private MessageBoardSection _messageBoardSection;

	}

	@GraphQLTypeExtension(ContentStructure.class)
	public class GetContentStructurePermissionsPageTypeExtension {

		public GetContentStructurePermissionsPageTypeExtension(
			ContentStructure contentStructure) {

			_contentStructure = contentStructure;
		}

		@GraphQLField
		public ContentStructurePage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_contentStructureResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				contentStructureResource -> new ContentStructurePage(
					contentStructureResource.getContentStructurePermissionsPage(
						_contentStructure.getId(), roleNames)));
		}

		private ContentStructure _contentStructure;

	}

	@GraphQLTypeExtension(KnowledgeBaseFolder.class)
	public class GetKnowledgeBaseFolderKnowledgeBaseFoldersPageTypeExtension {

		public GetKnowledgeBaseFolderKnowledgeBaseFoldersPageTypeExtension(
			KnowledgeBaseFolder knowledgeBaseFolder) {

			_knowledgeBaseFolder = knowledgeBaseFolder;
		}

		@GraphQLField(
			description = "Retrieves the knowledge base folder's subfolders."
		)
		public KnowledgeBaseFolderPage knowledgeBaseFolders(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_knowledgeBaseFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseFolderResource -> new KnowledgeBaseFolderPage(
					knowledgeBaseFolderResource.
						getKnowledgeBaseFolderKnowledgeBaseFoldersPage(
							_knowledgeBaseFolder.getId(),
							Pagination.of(page, pageSize))));
		}

		private KnowledgeBaseFolder _knowledgeBaseFolder;

	}

	@GraphQLTypeExtension(ContentStructure.class)
	public class GetContentStructureStructuredContentsPageTypeExtension {

		public GetContentStructureStructuredContentsPageTypeExtension(
			ContentStructure contentStructure) {

			_contentStructure = contentStructure;
		}

		@GraphQLField(
			description = "Retrieves a list of the content structure's structured content. Results can be paginated, filtered, searched, and sorted."
		)
		public StructuredContentPage structuredContents(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_structuredContentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentResource -> new StructuredContentPage(
					structuredContentResource.
						getContentStructureStructuredContentsPage(
							_contentStructure.getId(), search,
							_aggregationBiFunction.apply(
								structuredContentResource, aggregations),
							_filterBiFunction.apply(
								structuredContentResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								structuredContentResource, sortsString))));
		}

		private ContentStructure _contentStructure;

	}

	@GraphQLTypeExtension(BlogPosting.class)
	public class GetBlogPostingCommentsPageTypeExtension {

		public GetBlogPostingCommentsPageTypeExtension(
			BlogPosting blogPosting) {

			_blogPosting = blogPosting;
		}

		@GraphQLField(
			description = "Retrieves the blog post's comments in a list. Results can be paginated, filtered, searched, and sorted."
		)
		public CommentPage comments(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_commentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				commentResource -> new CommentPage(
					commentResource.getBlogPostingCommentsPage(
						_blogPosting.getId(), search,
						_aggregationBiFunction.apply(
							commentResource, aggregations),
						_filterBiFunction.apply(commentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(commentResource, sortsString))));
		}

		private BlogPosting _blogPosting;

	}

	@GraphQLTypeExtension(WikiPage.class)
	public class GetWikiPageWikiPagesPageTypeExtension {

		public GetWikiPageWikiPagesPageTypeExtension(WikiPage wikiPage) {
			_wikiPage = wikiPage;
		}

		@GraphQLField(
			description = "Retrieves the child wiki page's of a wiki page."
		)
		public WikiPagePage wikiPages() throws Exception {
			return _applyComponentServiceObjects(
				_wikiPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wikiPageResource -> new WikiPagePage(
					wikiPageResource.getWikiPageWikiPagesPage(
						_wikiPage.getId())));
		}

		private WikiPage _wikiPage;

	}

	@GraphQLTypeExtension(StructuredContent.class)
	public class GetStructuredContentCommentsPageTypeExtension {

		public GetStructuredContentCommentsPageTypeExtension(
			StructuredContent structuredContent) {

			_structuredContent = structuredContent;
		}

		@GraphQLField(
			description = "Retrieves the structured content's comments. Results can be paginated, filtered, searched, and sorted."
		)
		public CommentPage comments(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_commentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				commentResource -> new CommentPage(
					commentResource.getStructuredContentCommentsPage(
						_structuredContent.getId(), search,
						_aggregationBiFunction.apply(
							commentResource, aggregations),
						_filterBiFunction.apply(commentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(commentResource, sortsString))));
		}

		private StructuredContent _structuredContent;

	}

	@GraphQLTypeExtension(KnowledgeBaseArticle.class)
	public class GetKnowledgeBaseArticleMyRatingTypeExtension {

		public GetKnowledgeBaseArticleMyRatingTypeExtension(
			KnowledgeBaseArticle knowledgeBaseArticle) {

			_knowledgeBaseArticle = knowledgeBaseArticle;
		}

		@GraphQLField(
			description = "Retrieves the knowledge base article's rating."
		)
		public Rating myRating() throws Exception {
			return _applyComponentServiceObjects(
				_knowledgeBaseArticleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseArticleResource ->
					knowledgeBaseArticleResource.
						getKnowledgeBaseArticleMyRating(
							_knowledgeBaseArticle.getId()));
		}

		private KnowledgeBaseArticle _knowledgeBaseArticle;

	}

	@GraphQLTypeExtension(MessageBoardMessage.class)
	public class GetMessageBoardThreadTypeExtension {

		public GetMessageBoardThreadTypeExtension(
			MessageBoardMessage messageBoardMessage) {

			_messageBoardMessage = messageBoardMessage;
		}

		@GraphQLField(description = "Retrieves the message board thread.")
		public MessageBoardThread messageBoardThread() throws Exception {
			return _applyComponentServiceObjects(
				_messageBoardThreadResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardThreadResource ->
					messageBoardThreadResource.getMessageBoardThread(
						_messageBoardMessage.getMessageBoardThreadId()));
		}

		private MessageBoardMessage _messageBoardMessage;

	}

	@GraphQLTypeExtension(BlogPosting.class)
	public class GetBlogPostingMyRatingTypeExtension {

		public GetBlogPostingMyRatingTypeExtension(BlogPosting blogPosting) {
			_blogPosting = blogPosting;
		}

		@GraphQLField(
			description = "Retrieves the blog post rating of the user who authenticated the request."
		)
		public Rating myRating() throws Exception {
			return _applyComponentServiceObjects(
				_blogPostingResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				blogPostingResource ->
					blogPostingResource.getBlogPostingMyRating(
						_blogPosting.getId()));
		}

		private BlogPosting _blogPosting;

	}

	@GraphQLTypeExtension(Document.class)
	public class GetDocumentCommentsPageTypeExtension {

		public GetDocumentCommentsPageTypeExtension(Document document) {
			_document = document;
		}

		@GraphQLField(
			description = "Retrieves the document's comments. Results can be paginated, filtered, searched, and sorted."
		)
		public CommentPage comments(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_commentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				commentResource -> new CommentPage(
					commentResource.getDocumentCommentsPage(
						_document.getId(), search,
						_aggregationBiFunction.apply(
							commentResource, aggregations),
						_filterBiFunction.apply(commentResource, filterString),
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(commentResource, sortsString))));
		}

		private Document _document;

	}

	@GraphQLTypeExtension(StructuredContentFolder.class)
	public class GetStructuredContentFolderStructuredContentsPageTypeExtension {

		public GetStructuredContentFolderStructuredContentsPageTypeExtension(
			StructuredContentFolder structuredContentFolder) {

			_structuredContentFolder = structuredContentFolder;
		}

		@GraphQLField(
			description = "Retrieves the folder's structured content. Results can be paginated, filtered, searched, and sorted."
		)
		public StructuredContentPage structuredContents(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_structuredContentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentResource -> new StructuredContentPage(
					structuredContentResource.
						getStructuredContentFolderStructuredContentsPage(
							_structuredContentFolder.getId(), flatten, search,
							_aggregationBiFunction.apply(
								structuredContentResource, aggregations),
							_filterBiFunction.apply(
								structuredContentResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								structuredContentResource, sortsString))));
		}

		private StructuredContentFolder _structuredContentFolder;

	}

	@GraphQLTypeExtension(MessageBoardThread.class)
	public class GetMessageBoardThreadPermissionsPageTypeExtension {

		public GetMessageBoardThreadPermissionsPageTypeExtension(
			MessageBoardThread messageBoardThread) {

			_messageBoardThread = messageBoardThread;
		}

		@GraphQLField
		public MessageBoardThreadPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardThreadResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardThreadResource -> new MessageBoardThreadPage(
					messageBoardThreadResource.
						getMessageBoardThreadPermissionsPage(
							_messageBoardThread.getId(), roleNames)));
		}

		private MessageBoardThread _messageBoardThread;

	}

	@GraphQLTypeExtension(StructuredContentFolder.class)
	public class
		GetStructuredContentFolderStructuredContentFoldersPageTypeExtension {

		public GetStructuredContentFolderStructuredContentFoldersPageTypeExtension(
			StructuredContentFolder structuredContentFolder) {

			_structuredContentFolder = structuredContentFolder;
		}

		@GraphQLField(
			description = "Retrieves the parent structured content folder's subfolders. Results can be paginated, filtered, searched, and sorted."
		)
		public StructuredContentFolderPage structuredContentFolders(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_structuredContentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentFolderResource ->
					new StructuredContentFolderPage(
						structuredContentFolderResource.
							getStructuredContentFolderStructuredContentFoldersPage(
								_structuredContentFolder.getId(), search,
								_aggregationBiFunction.apply(
									structuredContentFolderResource,
									aggregations),
								_filterBiFunction.apply(
									structuredContentFolderResource,
									filterString),
								Pagination.of(page, pageSize),
								_sortsBiFunction.apply(
									structuredContentFolderResource,
									sortsString))));
		}

		private StructuredContentFolder _structuredContentFolder;

	}

	@GraphQLTypeExtension(MessageBoardMessage.class)
	public class GetMessageBoardMessageMessageBoardMessagesPageTypeExtension {

		public GetMessageBoardMessageMessageBoardMessagesPageTypeExtension(
			MessageBoardMessage messageBoardMessage) {

			_messageBoardMessage = messageBoardMessage;
		}

		@GraphQLField(
			description = "Retrieves the parent message board message's child messages. Results can be paginated, filtered, searched, and sorted."
		)
		public MessageBoardMessagePage messageBoardMessages(
				@GraphQLName("flatten") Boolean flatten,
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardMessageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardMessageResource -> new MessageBoardMessagePage(
					messageBoardMessageResource.
						getMessageBoardMessageMessageBoardMessagesPage(
							_messageBoardMessage.getId(), flatten, search,
							_aggregationBiFunction.apply(
								messageBoardMessageResource, aggregations),
							_filterBiFunction.apply(
								messageBoardMessageResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								messageBoardMessageResource, sortsString))));
		}

		private MessageBoardMessage _messageBoardMessage;

	}

	@GraphQLTypeExtension(MessageBoardSection.class)
	public class GetMessageBoardSectionMessageBoardSectionsPageTypeExtension {

		public GetMessageBoardSectionMessageBoardSectionsPageTypeExtension(
			MessageBoardSection messageBoardSection) {

			_messageBoardSection = messageBoardSection;
		}

		@GraphQLField(
			description = "Retrieves the parent message board section's subsections. Results can be paginated, filtered, searched, and sorted."
		)
		public MessageBoardSectionPage messageBoardSections(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardSectionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardSectionResource -> new MessageBoardSectionPage(
					messageBoardSectionResource.
						getMessageBoardSectionMessageBoardSectionsPage(
							_messageBoardSection.getId(), search,
							_aggregationBiFunction.apply(
								messageBoardSectionResource, aggregations),
							_filterBiFunction.apply(
								messageBoardSectionResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								messageBoardSectionResource, sortsString))));
		}

		private MessageBoardSection _messageBoardSection;

	}

	@GraphQLTypeExtension(WikiNode.class)
	public class GetWikiNodePermissionsPageTypeExtension {

		public GetWikiNodePermissionsPageTypeExtension(WikiNode wikiNode) {
			_wikiNode = wikiNode;
		}

		@GraphQLField
		public WikiNodePage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_wikiNodeResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wikiNodeResource -> new WikiNodePage(
					wikiNodeResource.getWikiNodePermissionsPage(
						_wikiNode.getId(), roleNames)));
		}

		private WikiNode _wikiNode;

	}

	@GraphQLTypeExtension(Document.class)
	public class
		GetDocumentRenderedContentByDisplayPageDisplayPageKeyTypeExtension {

		public GetDocumentRenderedContentByDisplayPageDisplayPageKeyTypeExtension(
			Document document) {

			_document = document;
		}

		@GraphQLField(
			description = "Retrieves the document's rendered display page"
		)
		public String renderedContentByDisplayPageDisplayPageKey(
				@GraphQLName("displayPageKey") String displayPageKey)
			throws Exception {

			return _applyComponentServiceObjects(
				_documentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentResource ->
					documentResource.
						getDocumentRenderedContentByDisplayPageDisplayPageKey(
							_document.getId(), displayPageKey));
		}

		private Document _document;

	}

	@GraphQLTypeExtension(BlogPosting.class)
	public class
		GetBlogPostingRenderedContentByDisplayPageDisplayPageKeyTypeExtension {

		public GetBlogPostingRenderedContentByDisplayPageDisplayPageKeyTypeExtension(
			BlogPosting blogPosting) {

			_blogPosting = blogPosting;
		}

		@GraphQLField(
			description = "Retrieves the blog post's rendered display page"
		)
		public String renderedContentByDisplayPageDisplayPageKey(
				@GraphQLName("displayPageKey") String displayPageKey)
			throws Exception {

			return _applyComponentServiceObjects(
				_blogPostingResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				blogPostingResource ->
					blogPostingResource.
						getBlogPostingRenderedContentByDisplayPageDisplayPageKey(
							_blogPosting.getId(), displayPageKey));
		}

		private BlogPosting _blogPosting;

	}

	@GraphQLTypeExtension(MessageBoardSection.class)
	public class GetMessageBoardSectionMessageBoardThreadsPageTypeExtension {

		public GetMessageBoardSectionMessageBoardThreadsPageTypeExtension(
			MessageBoardSection messageBoardSection) {

			_messageBoardSection = messageBoardSection;
		}

		@GraphQLField(
			description = "Retrieves the message board section's threads. Results can be paginated, filtered, searched, and sorted."
		)
		public MessageBoardThreadPage messageBoardThreads(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardThreadResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardThreadResource -> new MessageBoardThreadPage(
					messageBoardThreadResource.
						getMessageBoardSectionMessageBoardThreadsPage(
							_messageBoardSection.getId(), search,
							_aggregationBiFunction.apply(
								messageBoardThreadResource, aggregations),
							_filterBiFunction.apply(
								messageBoardThreadResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								messageBoardThreadResource, sortsString))));
		}

		private MessageBoardSection _messageBoardSection;

	}

	@GraphQLTypeExtension(MessageBoardThread.class)
	public class GetMessageBoardThreadMyRatingTypeExtension {

		public GetMessageBoardThreadMyRatingTypeExtension(
			MessageBoardThread messageBoardThread) {

			_messageBoardThread = messageBoardThread;
		}

		@GraphQLField(
			description = "Retrieves the message board thread's rating."
		)
		public Rating myRating() throws Exception {
			return _applyComponentServiceObjects(
				_messageBoardThreadResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardThreadResource ->
					messageBoardThreadResource.getMessageBoardThreadMyRating(
						_messageBoardThread.getId()));
		}

		private MessageBoardThread _messageBoardThread;

	}

	@GraphQLTypeExtension(Document.class)
	public class GetDocumentPermissionsPageTypeExtension {

		public GetDocumentPermissionsPageTypeExtension(Document document) {
			_document = document;
		}

		@GraphQLField
		public DocumentPage permissions(
				@GraphQLName("roleNames") String roleNames)
			throws Exception {

			return _applyComponentServiceObjects(
				_documentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentResource -> new DocumentPage(
					documentResource.getDocumentPermissionsPage(
						_document.getId(), roleNames)));
		}

		private Document _document;

	}

	@GraphQLTypeExtension(MessageBoardThread.class)
	public class GetMessageBoardThreadMessageBoardMessagesPageTypeExtension {

		public GetMessageBoardThreadMessageBoardMessagesPageTypeExtension(
			MessageBoardThread messageBoardThread) {

			_messageBoardThread = messageBoardThread;
		}

		@GraphQLField(
			description = "Retrieves the message board thread's messages. Results can be paginated, filtered, searched, and sorted."
		)
		public MessageBoardMessagePage messageBoardMessages(
				@GraphQLName("search") String search,
				@GraphQLName("aggregation") List<String> aggregations,
				@GraphQLName("filter") String filterString,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_messageBoardMessageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardMessageResource -> new MessageBoardMessagePage(
					messageBoardMessageResource.
						getMessageBoardThreadMessageBoardMessagesPage(
							_messageBoardThread.getId(), search,
							_aggregationBiFunction.apply(
								messageBoardMessageResource, aggregations),
							_filterBiFunction.apply(
								messageBoardMessageResource, filterString),
							Pagination.of(page, pageSize),
							_sortsBiFunction.apply(
								messageBoardMessageResource, sortsString))));
		}

		private MessageBoardThread _messageBoardThread;

	}

	@GraphQLName("BlogPostingPage")
	public class BlogPostingPage {

		public BlogPostingPage(Page blogPostingPage) {
			actions = blogPostingPage.getActions();

			facets = blogPostingPage.getFacets();

			items = blogPostingPage.getItems();
			lastPage = blogPostingPage.getLastPage();
			page = blogPostingPage.getPage();
			pageSize = blogPostingPage.getPageSize();
			totalCount = blogPostingPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<BlogPosting> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("BlogPostingImagePage")
	public class BlogPostingImagePage {

		public BlogPostingImagePage(Page blogPostingImagePage) {
			actions = blogPostingImagePage.getActions();

			facets = blogPostingImagePage.getFacets();

			items = blogPostingImagePage.getItems();
			lastPage = blogPostingImagePage.getLastPage();
			page = blogPostingImagePage.getPage();
			pageSize = blogPostingImagePage.getPageSize();
			totalCount = blogPostingImagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<BlogPostingImage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("CommentPage")
	public class CommentPage {

		public CommentPage(Page commentPage) {
			actions = commentPage.getActions();

			facets = commentPage.getFacets();

			items = commentPage.getItems();
			lastPage = commentPage.getLastPage();
			page = commentPage.getPage();
			pageSize = commentPage.getPageSize();
			totalCount = commentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Comment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContentElementPage")
	public class ContentElementPage {

		public ContentElementPage(Page contentElementPage) {
			actions = contentElementPage.getActions();

			facets = contentElementPage.getFacets();

			items = contentElementPage.getItems();
			lastPage = contentElementPage.getLastPage();
			page = contentElementPage.getPage();
			pageSize = contentElementPage.getPageSize();
			totalCount = contentElementPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ContentElement> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContentSetElementPage")
	public class ContentSetElementPage {

		public ContentSetElementPage(Page contentSetElementPage) {
			actions = contentSetElementPage.getActions();

			facets = contentSetElementPage.getFacets();

			items = contentSetElementPage.getItems();
			lastPage = contentSetElementPage.getLastPage();
			page = contentSetElementPage.getPage();
			pageSize = contentSetElementPage.getPageSize();
			totalCount = contentSetElementPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ContentSetElement> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContentStructurePage")
	public class ContentStructurePage {

		public ContentStructurePage(Page contentStructurePage) {
			actions = contentStructurePage.getActions();

			facets = contentStructurePage.getFacets();

			items = contentStructurePage.getItems();
			lastPage = contentStructurePage.getLastPage();
			page = contentStructurePage.getPage();
			pageSize = contentStructurePage.getPageSize();
			totalCount = contentStructurePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ContentStructure> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ContentTemplatePage")
	public class ContentTemplatePage {

		public ContentTemplatePage(Page contentTemplatePage) {
			actions = contentTemplatePage.getActions();

			facets = contentTemplatePage.getFacets();

			items = contentTemplatePage.getItems();
			lastPage = contentTemplatePage.getLastPage();
			page = contentTemplatePage.getPage();
			pageSize = contentTemplatePage.getPageSize();
			totalCount = contentTemplatePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<ContentTemplate> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DocumentPage")
	public class DocumentPage {

		public DocumentPage(Page documentPage) {
			actions = documentPage.getActions();

			facets = documentPage.getFacets();

			items = documentPage.getItems();
			lastPage = documentPage.getLastPage();
			page = documentPage.getPage();
			pageSize = documentPage.getPageSize();
			totalCount = documentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Document> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DocumentDataDefinitionTypePage")
	public class DocumentDataDefinitionTypePage {

		public DocumentDataDefinitionTypePage(
			Page documentDataDefinitionTypePage) {

			actions = documentDataDefinitionTypePage.getActions();

			facets = documentDataDefinitionTypePage.getFacets();

			items = documentDataDefinitionTypePage.getItems();
			lastPage = documentDataDefinitionTypePage.getLastPage();
			page = documentDataDefinitionTypePage.getPage();
			pageSize = documentDataDefinitionTypePage.getPageSize();
			totalCount = documentDataDefinitionTypePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<DocumentDataDefinitionType> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DocumentFolderPage")
	public class DocumentFolderPage {

		public DocumentFolderPage(Page documentFolderPage) {
			actions = documentFolderPage.getActions();

			facets = documentFolderPage.getFacets();

			items = documentFolderPage.getItems();
			lastPage = documentFolderPage.getLastPage();
			page = documentFolderPage.getPage();
			pageSize = documentFolderPage.getPageSize();
			totalCount = documentFolderPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<DocumentFolder> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DocumentMetadataSetPage")
	public class DocumentMetadataSetPage {

		public DocumentMetadataSetPage(Page documentMetadataSetPage) {
			actions = documentMetadataSetPage.getActions();

			facets = documentMetadataSetPage.getFacets();

			items = documentMetadataSetPage.getItems();
			lastPage = documentMetadataSetPage.getLastPage();
			page = documentMetadataSetPage.getPage();
			pageSize = documentMetadataSetPage.getPageSize();
			totalCount = documentMetadataSetPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<DocumentMetadataSet> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("DocumentShortcutPage")
	public class DocumentShortcutPage {

		public DocumentShortcutPage(Page documentShortcutPage) {
			actions = documentShortcutPage.getActions();

			facets = documentShortcutPage.getFacets();

			items = documentShortcutPage.getItems();
			lastPage = documentShortcutPage.getLastPage();
			page = documentShortcutPage.getPage();
			pageSize = documentShortcutPage.getPageSize();
			totalCount = documentShortcutPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<DocumentShortcut> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("KnowledgeBaseArticlePage")
	public class KnowledgeBaseArticlePage {

		public KnowledgeBaseArticlePage(Page knowledgeBaseArticlePage) {
			actions = knowledgeBaseArticlePage.getActions();

			facets = knowledgeBaseArticlePage.getFacets();

			items = knowledgeBaseArticlePage.getItems();
			lastPage = knowledgeBaseArticlePage.getLastPage();
			page = knowledgeBaseArticlePage.getPage();
			pageSize = knowledgeBaseArticlePage.getPageSize();
			totalCount = knowledgeBaseArticlePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<KnowledgeBaseArticle> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("KnowledgeBaseAttachmentPage")
	public class KnowledgeBaseAttachmentPage {

		public KnowledgeBaseAttachmentPage(Page knowledgeBaseAttachmentPage) {
			actions = knowledgeBaseAttachmentPage.getActions();

			facets = knowledgeBaseAttachmentPage.getFacets();

			items = knowledgeBaseAttachmentPage.getItems();
			lastPage = knowledgeBaseAttachmentPage.getLastPage();
			page = knowledgeBaseAttachmentPage.getPage();
			pageSize = knowledgeBaseAttachmentPage.getPageSize();
			totalCount = knowledgeBaseAttachmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<KnowledgeBaseAttachment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("KnowledgeBaseFolderPage")
	public class KnowledgeBaseFolderPage {

		public KnowledgeBaseFolderPage(Page knowledgeBaseFolderPage) {
			actions = knowledgeBaseFolderPage.getActions();

			facets = knowledgeBaseFolderPage.getFacets();

			items = knowledgeBaseFolderPage.getItems();
			lastPage = knowledgeBaseFolderPage.getLastPage();
			page = knowledgeBaseFolderPage.getPage();
			pageSize = knowledgeBaseFolderPage.getPageSize();
			totalCount = knowledgeBaseFolderPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<KnowledgeBaseFolder> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("LanguagePage")
	public class LanguagePage {

		public LanguagePage(Page languagePage) {
			actions = languagePage.getActions();

			facets = languagePage.getFacets();

			items = languagePage.getItems();
			lastPage = languagePage.getLastPage();
			page = languagePage.getPage();
			pageSize = languagePage.getPageSize();
			totalCount = languagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<Language> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("MessageBoardAttachmentPage")
	public class MessageBoardAttachmentPage {

		public MessageBoardAttachmentPage(Page messageBoardAttachmentPage) {
			actions = messageBoardAttachmentPage.getActions();

			facets = messageBoardAttachmentPage.getFacets();

			items = messageBoardAttachmentPage.getItems();
			lastPage = messageBoardAttachmentPage.getLastPage();
			page = messageBoardAttachmentPage.getPage();
			pageSize = messageBoardAttachmentPage.getPageSize();
			totalCount = messageBoardAttachmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<MessageBoardAttachment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("MessageBoardMessagePage")
	public class MessageBoardMessagePage {

		public MessageBoardMessagePage(Page messageBoardMessagePage) {
			actions = messageBoardMessagePage.getActions();

			facets = messageBoardMessagePage.getFacets();

			items = messageBoardMessagePage.getItems();
			lastPage = messageBoardMessagePage.getLastPage();
			page = messageBoardMessagePage.getPage();
			pageSize = messageBoardMessagePage.getPageSize();
			totalCount = messageBoardMessagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<MessageBoardMessage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("MessageBoardSectionPage")
	public class MessageBoardSectionPage {

		public MessageBoardSectionPage(Page messageBoardSectionPage) {
			actions = messageBoardSectionPage.getActions();

			facets = messageBoardSectionPage.getFacets();

			items = messageBoardSectionPage.getItems();
			lastPage = messageBoardSectionPage.getLastPage();
			page = messageBoardSectionPage.getPage();
			pageSize = messageBoardSectionPage.getPageSize();
			totalCount = messageBoardSectionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<MessageBoardSection> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("MessageBoardThreadPage")
	public class MessageBoardThreadPage {

		public MessageBoardThreadPage(Page messageBoardThreadPage) {
			actions = messageBoardThreadPage.getActions();

			facets = messageBoardThreadPage.getFacets();

			items = messageBoardThreadPage.getItems();
			lastPage = messageBoardThreadPage.getLastPage();
			page = messageBoardThreadPage.getPage();
			pageSize = messageBoardThreadPage.getPageSize();
			totalCount = messageBoardThreadPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<MessageBoardThread> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("NavigationMenuPage")
	public class NavigationMenuPage {

		public NavigationMenuPage(Page navigationMenuPage) {
			actions = navigationMenuPage.getActions();

			facets = navigationMenuPage.getFacets();

			items = navigationMenuPage.getItems();
			lastPage = navigationMenuPage.getLastPage();
			page = navigationMenuPage.getPage();
			pageSize = navigationMenuPage.getPageSize();
			totalCount = navigationMenuPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<NavigationMenu> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SitePagePage")
	public class SitePagePage {

		public SitePagePage(Page sitePagePage) {
			actions = sitePagePage.getActions();

			facets = sitePagePage.getFacets();

			items = sitePagePage.getItems();
			lastPage = sitePagePage.getLastPage();
			page = sitePagePage.getPage();
			pageSize = sitePagePage.getPageSize();
			totalCount = sitePagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<SitePage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("StructuredContentPage")
	public class StructuredContentPage {

		public StructuredContentPage(Page structuredContentPage) {
			actions = structuredContentPage.getActions();

			facets = structuredContentPage.getFacets();

			items = structuredContentPage.getItems();
			lastPage = structuredContentPage.getLastPage();
			page = structuredContentPage.getPage();
			pageSize = structuredContentPage.getPageSize();
			totalCount = structuredContentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<StructuredContent> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("StructuredContentFolderPage")
	public class StructuredContentFolderPage {

		public StructuredContentFolderPage(Page structuredContentFolderPage) {
			actions = structuredContentFolderPage.getActions();

			facets = structuredContentFolderPage.getFacets();

			items = structuredContentFolderPage.getItems();
			lastPage = structuredContentFolderPage.getLastPage();
			page = structuredContentFolderPage.getPage();
			pageSize = structuredContentFolderPage.getPageSize();
			totalCount = structuredContentFolderPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<StructuredContentFolder> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WikiNodePage")
	public class WikiNodePage {

		public WikiNodePage(Page wikiNodePage) {
			actions = wikiNodePage.getActions();

			facets = wikiNodePage.getFacets();

			items = wikiNodePage.getItems();
			lastPage = wikiNodePage.getLastPage();
			page = wikiNodePage.getPage();
			pageSize = wikiNodePage.getPageSize();
			totalCount = wikiNodePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<WikiNode> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WikiPagePage")
	public class WikiPagePage {

		public WikiPagePage(Page wikiPagePage) {
			actions = wikiPagePage.getActions();

			facets = wikiPagePage.getFacets();

			items = wikiPagePage.getItems();
			lastPage = wikiPagePage.getLastPage();
			page = wikiPagePage.getPage();
			pageSize = wikiPagePage.getPageSize();
			totalCount = wikiPagePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<WikiPage> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WikiPageAttachmentPage")
	public class WikiPageAttachmentPage {

		public WikiPageAttachmentPage(Page wikiPageAttachmentPage) {
			actions = wikiPageAttachmentPage.getActions();

			facets = wikiPageAttachmentPage.getFacets();

			items = wikiPageAttachmentPage.getItems();
			lastPage = wikiPageAttachmentPage.getLastPage();
			page = wikiPageAttachmentPage.getPage();
			pageSize = wikiPageAttachmentPage.getPageSize();
			totalCount = wikiPageAttachmentPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected List<Facet> facets;

		@GraphQLField
		protected java.util.Collection<WikiPageAttachment> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLTypeExtension(Comment.class)
	public class ParentCommentCommentIdTypeExtension {

		public ParentCommentCommentIdTypeExtension(Comment comment) {
			_comment = comment;
		}

		@GraphQLField(description = "Retrieves the comment.")
		public Comment parentComment() throws Exception {
			if (_comment.getParentCommentId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_commentResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				commentResource -> commentResource.getComment(
					_comment.getParentCommentId()));
		}

		private Comment _comment;

	}

	@GraphQLTypeExtension(DocumentFolder.class)
	public class ParentDocumentFolderDocumentFolderIdTypeExtension {

		public ParentDocumentFolderDocumentFolderIdTypeExtension(
			DocumentFolder documentFolder) {

			_documentFolder = documentFolder;
		}

		@GraphQLField(description = "Retrieves the document folder.")
		public DocumentFolder parentDocumentFolder() throws Exception {
			if (_documentFolder.getParentDocumentFolderId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_documentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				documentFolderResource ->
					documentFolderResource.getDocumentFolder(
						_documentFolder.getParentDocumentFolderId()));
		}

		private DocumentFolder _documentFolder;

	}

	@GraphQLTypeExtension(KnowledgeBaseArticle.class)
	public class ParentKnowledgeBaseArticleKnowledgeBaseArticleIdTypeExtension {

		public ParentKnowledgeBaseArticleKnowledgeBaseArticleIdTypeExtension(
			KnowledgeBaseArticle knowledgeBaseArticle) {

			_knowledgeBaseArticle = knowledgeBaseArticle;
		}

		@GraphQLField(description = "Retrieves the knowledge base article.")
		public KnowledgeBaseArticle parentKnowledgeBaseArticle()
			throws Exception {

			if (_knowledgeBaseArticle.getParentKnowledgeBaseArticleId() ==
					null) {

				return null;
			}

			return _applyComponentServiceObjects(
				_knowledgeBaseArticleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				knowledgeBaseArticleResource ->
					knowledgeBaseArticleResource.getKnowledgeBaseArticle(
						_knowledgeBaseArticle.
							getParentKnowledgeBaseArticleId()));
		}

		private KnowledgeBaseArticle _knowledgeBaseArticle;

	}

	@GraphQLTypeExtension(MessageBoardMessage.class)
	public class ParentMessageBoardMessageMessageBoardMessageIdTypeExtension {

		public ParentMessageBoardMessageMessageBoardMessageIdTypeExtension(
			MessageBoardMessage messageBoardMessage) {

			_messageBoardMessage = messageBoardMessage;
		}

		@GraphQLField(description = "Retrieves the message board message.")
		public MessageBoardMessage parentMessageBoardMessage()
			throws Exception {

			if (_messageBoardMessage.getParentMessageBoardMessageId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_messageBoardMessageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardMessageResource ->
					messageBoardMessageResource.getMessageBoardMessage(
						_messageBoardMessage.getParentMessageBoardMessageId()));
		}

		private MessageBoardMessage _messageBoardMessage;

	}

	@GraphQLTypeExtension(MessageBoardSection.class)
	public class ParentMessageBoardSectionMessageBoardSectionIdTypeExtension {

		public ParentMessageBoardSectionMessageBoardSectionIdTypeExtension(
			MessageBoardSection messageBoardSection) {

			_messageBoardSection = messageBoardSection;
		}

		@GraphQLField(description = "Retrieves the message board section.")
		public MessageBoardSection parentMessageBoardSection()
			throws Exception {

			if (_messageBoardSection.getParentMessageBoardSectionId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_messageBoardSectionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				messageBoardSectionResource ->
					messageBoardSectionResource.getMessageBoardSection(
						_messageBoardSection.getParentMessageBoardSectionId()));
		}

		private MessageBoardSection _messageBoardSection;

	}

	@GraphQLTypeExtension(NavigationMenuItem.class)
	public class ParentNavigationMenuItemNavigationMenuIdTypeExtension {

		public ParentNavigationMenuItemNavigationMenuIdTypeExtension(
			NavigationMenuItem navigationMenuItem) {

			_navigationMenuItem = navigationMenuItem;
		}

		@GraphQLField
		public NavigationMenu parentNavigationMenuItem() throws Exception {
			if (_navigationMenuItem.getParentNavigationMenuId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_navigationMenuResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				navigationMenuResource ->
					navigationMenuResource.getNavigationMenu(
						_navigationMenuItem.getParentNavigationMenuId()));
		}

		private NavigationMenuItem _navigationMenuItem;

	}

	@GraphQLTypeExtension(StructuredContentFolder.class)
	public class
		ParentStructuredContentFolderStructuredContentFolderIdTypeExtension {

		public ParentStructuredContentFolderStructuredContentFolderIdTypeExtension(
			StructuredContentFolder structuredContentFolder) {

			_structuredContentFolder = structuredContentFolder;
		}

		@GraphQLField(description = "Retrieves the structured content folder.")
		public StructuredContentFolder parentStructuredContentFolder()
			throws Exception {

			if (_structuredContentFolder.getParentStructuredContentFolderId() ==
					null) {

				return null;
			}

			return _applyComponentServiceObjects(
				_structuredContentFolderResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				structuredContentFolderResource ->
					structuredContentFolderResource.getStructuredContentFolder(
						_structuredContentFolder.
							getParentStructuredContentFolderId()));
		}

		private StructuredContentFolder _structuredContentFolder;

	}

	@GraphQLTypeExtension(WikiPage.class)
	public class ParentWikiPageWikiPageIdTypeExtension {

		public ParentWikiPageWikiPageIdTypeExtension(WikiPage wikiPage) {
			_wikiPage = wikiPage;
		}

		@GraphQLField(description = "Retrieves the wiki page")
		public WikiPage parentWikiPage() throws Exception {
			if (_wikiPage.getParentWikiPageId() == null) {
				return null;
			}

			return _applyComponentServiceObjects(
				_wikiPageResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				wikiPageResource -> wikiPageResource.getWikiPage(
					_wikiPage.getParentWikiPageId()));
		}

		private WikiPage _wikiPage;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(
			BlogPostingResource blogPostingResource)
		throws Exception {

		blogPostingResource.setContextAcceptLanguage(_acceptLanguage);
		blogPostingResource.setContextCompany(_company);
		blogPostingResource.setContextHttpServletRequest(_httpServletRequest);
		blogPostingResource.setContextHttpServletResponse(_httpServletResponse);
		blogPostingResource.setContextUriInfo(_uriInfo);
		blogPostingResource.setContextUser(_user);
		blogPostingResource.setGroupLocalService(_groupLocalService);
		blogPostingResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			BlogPostingImageResource blogPostingImageResource)
		throws Exception {

		blogPostingImageResource.setContextAcceptLanguage(_acceptLanguage);
		blogPostingImageResource.setContextCompany(_company);
		blogPostingImageResource.setContextHttpServletRequest(
			_httpServletRequest);
		blogPostingImageResource.setContextHttpServletResponse(
			_httpServletResponse);
		blogPostingImageResource.setContextUriInfo(_uriInfo);
		blogPostingImageResource.setContextUser(_user);
		blogPostingImageResource.setGroupLocalService(_groupLocalService);
		blogPostingImageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(CommentResource commentResource)
		throws Exception {

		commentResource.setContextAcceptLanguage(_acceptLanguage);
		commentResource.setContextCompany(_company);
		commentResource.setContextHttpServletRequest(_httpServletRequest);
		commentResource.setContextHttpServletResponse(_httpServletResponse);
		commentResource.setContextUriInfo(_uriInfo);
		commentResource.setContextUser(_user);
		commentResource.setGroupLocalService(_groupLocalService);
		commentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContentElementResource contentElementResource)
		throws Exception {

		contentElementResource.setContextAcceptLanguage(_acceptLanguage);
		contentElementResource.setContextCompany(_company);
		contentElementResource.setContextHttpServletRequest(
			_httpServletRequest);
		contentElementResource.setContextHttpServletResponse(
			_httpServletResponse);
		contentElementResource.setContextUriInfo(_uriInfo);
		contentElementResource.setContextUser(_user);
		contentElementResource.setGroupLocalService(_groupLocalService);
		contentElementResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContentSetElementResource contentSetElementResource)
		throws Exception {

		contentSetElementResource.setContextAcceptLanguage(_acceptLanguage);
		contentSetElementResource.setContextCompany(_company);
		contentSetElementResource.setContextHttpServletRequest(
			_httpServletRequest);
		contentSetElementResource.setContextHttpServletResponse(
			_httpServletResponse);
		contentSetElementResource.setContextUriInfo(_uriInfo);
		contentSetElementResource.setContextUser(_user);
		contentSetElementResource.setGroupLocalService(_groupLocalService);
		contentSetElementResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContentStructureResource contentStructureResource)
		throws Exception {

		contentStructureResource.setContextAcceptLanguage(_acceptLanguage);
		contentStructureResource.setContextCompany(_company);
		contentStructureResource.setContextHttpServletRequest(
			_httpServletRequest);
		contentStructureResource.setContextHttpServletResponse(
			_httpServletResponse);
		contentStructureResource.setContextUriInfo(_uriInfo);
		contentStructureResource.setContextUser(_user);
		contentStructureResource.setGroupLocalService(_groupLocalService);
		contentStructureResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ContentTemplateResource contentTemplateResource)
		throws Exception {

		contentTemplateResource.setContextAcceptLanguage(_acceptLanguage);
		contentTemplateResource.setContextCompany(_company);
		contentTemplateResource.setContextHttpServletRequest(
			_httpServletRequest);
		contentTemplateResource.setContextHttpServletResponse(
			_httpServletResponse);
		contentTemplateResource.setContextUriInfo(_uriInfo);
		contentTemplateResource.setContextUser(_user);
		contentTemplateResource.setGroupLocalService(_groupLocalService);
		contentTemplateResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(DocumentResource documentResource)
		throws Exception {

		documentResource.setContextAcceptLanguage(_acceptLanguage);
		documentResource.setContextCompany(_company);
		documentResource.setContextHttpServletRequest(_httpServletRequest);
		documentResource.setContextHttpServletResponse(_httpServletResponse);
		documentResource.setContextUriInfo(_uriInfo);
		documentResource.setContextUser(_user);
		documentResource.setGroupLocalService(_groupLocalService);
		documentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DocumentDataDefinitionTypeResource
				documentDataDefinitionTypeResource)
		throws Exception {

		documentDataDefinitionTypeResource.setContextAcceptLanguage(
			_acceptLanguage);
		documentDataDefinitionTypeResource.setContextCompany(_company);
		documentDataDefinitionTypeResource.setContextHttpServletRequest(
			_httpServletRequest);
		documentDataDefinitionTypeResource.setContextHttpServletResponse(
			_httpServletResponse);
		documentDataDefinitionTypeResource.setContextUriInfo(_uriInfo);
		documentDataDefinitionTypeResource.setContextUser(_user);
		documentDataDefinitionTypeResource.setGroupLocalService(
			_groupLocalService);
		documentDataDefinitionTypeResource.setRoleLocalService(
			_roleLocalService);
	}

	private void _populateResourceContext(
			DocumentFolderResource documentFolderResource)
		throws Exception {

		documentFolderResource.setContextAcceptLanguage(_acceptLanguage);
		documentFolderResource.setContextCompany(_company);
		documentFolderResource.setContextHttpServletRequest(
			_httpServletRequest);
		documentFolderResource.setContextHttpServletResponse(
			_httpServletResponse);
		documentFolderResource.setContextUriInfo(_uriInfo);
		documentFolderResource.setContextUser(_user);
		documentFolderResource.setGroupLocalService(_groupLocalService);
		documentFolderResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DocumentMetadataSetResource documentMetadataSetResource)
		throws Exception {

		documentMetadataSetResource.setContextAcceptLanguage(_acceptLanguage);
		documentMetadataSetResource.setContextCompany(_company);
		documentMetadataSetResource.setContextHttpServletRequest(
			_httpServletRequest);
		documentMetadataSetResource.setContextHttpServletResponse(
			_httpServletResponse);
		documentMetadataSetResource.setContextUriInfo(_uriInfo);
		documentMetadataSetResource.setContextUser(_user);
		documentMetadataSetResource.setGroupLocalService(_groupLocalService);
		documentMetadataSetResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			DocumentShortcutResource documentShortcutResource)
		throws Exception {

		documentShortcutResource.setContextAcceptLanguage(_acceptLanguage);
		documentShortcutResource.setContextCompany(_company);
		documentShortcutResource.setContextHttpServletRequest(
			_httpServletRequest);
		documentShortcutResource.setContextHttpServletResponse(
			_httpServletResponse);
		documentShortcutResource.setContextUriInfo(_uriInfo);
		documentShortcutResource.setContextUser(_user);
		documentShortcutResource.setGroupLocalService(_groupLocalService);
		documentShortcutResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			KnowledgeBaseArticleResource knowledgeBaseArticleResource)
		throws Exception {

		knowledgeBaseArticleResource.setContextAcceptLanguage(_acceptLanguage);
		knowledgeBaseArticleResource.setContextCompany(_company);
		knowledgeBaseArticleResource.setContextHttpServletRequest(
			_httpServletRequest);
		knowledgeBaseArticleResource.setContextHttpServletResponse(
			_httpServletResponse);
		knowledgeBaseArticleResource.setContextUriInfo(_uriInfo);
		knowledgeBaseArticleResource.setContextUser(_user);
		knowledgeBaseArticleResource.setGroupLocalService(_groupLocalService);
		knowledgeBaseArticleResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			KnowledgeBaseAttachmentResource knowledgeBaseAttachmentResource)
		throws Exception {

		knowledgeBaseAttachmentResource.setContextAcceptLanguage(
			_acceptLanguage);
		knowledgeBaseAttachmentResource.setContextCompany(_company);
		knowledgeBaseAttachmentResource.setContextHttpServletRequest(
			_httpServletRequest);
		knowledgeBaseAttachmentResource.setContextHttpServletResponse(
			_httpServletResponse);
		knowledgeBaseAttachmentResource.setContextUriInfo(_uriInfo);
		knowledgeBaseAttachmentResource.setContextUser(_user);
		knowledgeBaseAttachmentResource.setGroupLocalService(
			_groupLocalService);
		knowledgeBaseAttachmentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			KnowledgeBaseFolderResource knowledgeBaseFolderResource)
		throws Exception {

		knowledgeBaseFolderResource.setContextAcceptLanguage(_acceptLanguage);
		knowledgeBaseFolderResource.setContextCompany(_company);
		knowledgeBaseFolderResource.setContextHttpServletRequest(
			_httpServletRequest);
		knowledgeBaseFolderResource.setContextHttpServletResponse(
			_httpServletResponse);
		knowledgeBaseFolderResource.setContextUriInfo(_uriInfo);
		knowledgeBaseFolderResource.setContextUser(_user);
		knowledgeBaseFolderResource.setGroupLocalService(_groupLocalService);
		knowledgeBaseFolderResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(LanguageResource languageResource)
		throws Exception {

		languageResource.setContextAcceptLanguage(_acceptLanguage);
		languageResource.setContextCompany(_company);
		languageResource.setContextHttpServletRequest(_httpServletRequest);
		languageResource.setContextHttpServletResponse(_httpServletResponse);
		languageResource.setContextUriInfo(_uriInfo);
		languageResource.setContextUser(_user);
		languageResource.setGroupLocalService(_groupLocalService);
		languageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			MessageBoardAttachmentResource messageBoardAttachmentResource)
		throws Exception {

		messageBoardAttachmentResource.setContextAcceptLanguage(
			_acceptLanguage);
		messageBoardAttachmentResource.setContextCompany(_company);
		messageBoardAttachmentResource.setContextHttpServletRequest(
			_httpServletRequest);
		messageBoardAttachmentResource.setContextHttpServletResponse(
			_httpServletResponse);
		messageBoardAttachmentResource.setContextUriInfo(_uriInfo);
		messageBoardAttachmentResource.setContextUser(_user);
		messageBoardAttachmentResource.setGroupLocalService(_groupLocalService);
		messageBoardAttachmentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			MessageBoardMessageResource messageBoardMessageResource)
		throws Exception {

		messageBoardMessageResource.setContextAcceptLanguage(_acceptLanguage);
		messageBoardMessageResource.setContextCompany(_company);
		messageBoardMessageResource.setContextHttpServletRequest(
			_httpServletRequest);
		messageBoardMessageResource.setContextHttpServletResponse(
			_httpServletResponse);
		messageBoardMessageResource.setContextUriInfo(_uriInfo);
		messageBoardMessageResource.setContextUser(_user);
		messageBoardMessageResource.setGroupLocalService(_groupLocalService);
		messageBoardMessageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			MessageBoardSectionResource messageBoardSectionResource)
		throws Exception {

		messageBoardSectionResource.setContextAcceptLanguage(_acceptLanguage);
		messageBoardSectionResource.setContextCompany(_company);
		messageBoardSectionResource.setContextHttpServletRequest(
			_httpServletRequest);
		messageBoardSectionResource.setContextHttpServletResponse(
			_httpServletResponse);
		messageBoardSectionResource.setContextUriInfo(_uriInfo);
		messageBoardSectionResource.setContextUser(_user);
		messageBoardSectionResource.setGroupLocalService(_groupLocalService);
		messageBoardSectionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			MessageBoardThreadResource messageBoardThreadResource)
		throws Exception {

		messageBoardThreadResource.setContextAcceptLanguage(_acceptLanguage);
		messageBoardThreadResource.setContextCompany(_company);
		messageBoardThreadResource.setContextHttpServletRequest(
			_httpServletRequest);
		messageBoardThreadResource.setContextHttpServletResponse(
			_httpServletResponse);
		messageBoardThreadResource.setContextUriInfo(_uriInfo);
		messageBoardThreadResource.setContextUser(_user);
		messageBoardThreadResource.setGroupLocalService(_groupLocalService);
		messageBoardThreadResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			NavigationMenuResource navigationMenuResource)
		throws Exception {

		navigationMenuResource.setContextAcceptLanguage(_acceptLanguage);
		navigationMenuResource.setContextCompany(_company);
		navigationMenuResource.setContextHttpServletRequest(
			_httpServletRequest);
		navigationMenuResource.setContextHttpServletResponse(
			_httpServletResponse);
		navigationMenuResource.setContextUriInfo(_uriInfo);
		navigationMenuResource.setContextUser(_user);
		navigationMenuResource.setGroupLocalService(_groupLocalService);
		navigationMenuResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SitePageResource sitePageResource)
		throws Exception {

		sitePageResource.setContextAcceptLanguage(_acceptLanguage);
		sitePageResource.setContextCompany(_company);
		sitePageResource.setContextHttpServletRequest(_httpServletRequest);
		sitePageResource.setContextHttpServletResponse(_httpServletResponse);
		sitePageResource.setContextUriInfo(_uriInfo);
		sitePageResource.setContextUser(_user);
		sitePageResource.setGroupLocalService(_groupLocalService);
		sitePageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			StructuredContentResource structuredContentResource)
		throws Exception {

		structuredContentResource.setContextAcceptLanguage(_acceptLanguage);
		structuredContentResource.setContextCompany(_company);
		structuredContentResource.setContextHttpServletRequest(
			_httpServletRequest);
		structuredContentResource.setContextHttpServletResponse(
			_httpServletResponse);
		structuredContentResource.setContextUriInfo(_uriInfo);
		structuredContentResource.setContextUser(_user);
		structuredContentResource.setGroupLocalService(_groupLocalService);
		structuredContentResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			StructuredContentFolderResource structuredContentFolderResource)
		throws Exception {

		structuredContentFolderResource.setContextAcceptLanguage(
			_acceptLanguage);
		structuredContentFolderResource.setContextCompany(_company);
		structuredContentFolderResource.setContextHttpServletRequest(
			_httpServletRequest);
		structuredContentFolderResource.setContextHttpServletResponse(
			_httpServletResponse);
		structuredContentFolderResource.setContextUriInfo(_uriInfo);
		structuredContentFolderResource.setContextUser(_user);
		structuredContentFolderResource.setGroupLocalService(
			_groupLocalService);
		structuredContentFolderResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(WikiNodeResource wikiNodeResource)
		throws Exception {

		wikiNodeResource.setContextAcceptLanguage(_acceptLanguage);
		wikiNodeResource.setContextCompany(_company);
		wikiNodeResource.setContextHttpServletRequest(_httpServletRequest);
		wikiNodeResource.setContextHttpServletResponse(_httpServletResponse);
		wikiNodeResource.setContextUriInfo(_uriInfo);
		wikiNodeResource.setContextUser(_user);
		wikiNodeResource.setGroupLocalService(_groupLocalService);
		wikiNodeResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(WikiPageResource wikiPageResource)
		throws Exception {

		wikiPageResource.setContextAcceptLanguage(_acceptLanguage);
		wikiPageResource.setContextCompany(_company);
		wikiPageResource.setContextHttpServletRequest(_httpServletRequest);
		wikiPageResource.setContextHttpServletResponse(_httpServletResponse);
		wikiPageResource.setContextUriInfo(_uriInfo);
		wikiPageResource.setContextUser(_user);
		wikiPageResource.setGroupLocalService(_groupLocalService);
		wikiPageResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			WikiPageAttachmentResource wikiPageAttachmentResource)
		throws Exception {

		wikiPageAttachmentResource.setContextAcceptLanguage(_acceptLanguage);
		wikiPageAttachmentResource.setContextCompany(_company);
		wikiPageAttachmentResource.setContextHttpServletRequest(
			_httpServletRequest);
		wikiPageAttachmentResource.setContextHttpServletResponse(
			_httpServletResponse);
		wikiPageAttachmentResource.setContextUriInfo(_uriInfo);
		wikiPageAttachmentResource.setContextUser(_user);
		wikiPageAttachmentResource.setGroupLocalService(_groupLocalService);
		wikiPageAttachmentResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<BlogPostingResource>
		_blogPostingResourceComponentServiceObjects;
	private static ComponentServiceObjects<BlogPostingImageResource>
		_blogPostingImageResourceComponentServiceObjects;
	private static ComponentServiceObjects<CommentResource>
		_commentResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContentElementResource>
		_contentElementResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContentSetElementResource>
		_contentSetElementResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContentStructureResource>
		_contentStructureResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContentTemplateResource>
		_contentTemplateResourceComponentServiceObjects;
	private static ComponentServiceObjects<DocumentResource>
		_documentResourceComponentServiceObjects;
	private static ComponentServiceObjects<DocumentDataDefinitionTypeResource>
		_documentDataDefinitionTypeResourceComponentServiceObjects;
	private static ComponentServiceObjects<DocumentFolderResource>
		_documentFolderResourceComponentServiceObjects;
	private static ComponentServiceObjects<DocumentMetadataSetResource>
		_documentMetadataSetResourceComponentServiceObjects;
	private static ComponentServiceObjects<DocumentShortcutResource>
		_documentShortcutResourceComponentServiceObjects;
	private static ComponentServiceObjects<KnowledgeBaseArticleResource>
		_knowledgeBaseArticleResourceComponentServiceObjects;
	private static ComponentServiceObjects<KnowledgeBaseAttachmentResource>
		_knowledgeBaseAttachmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<KnowledgeBaseFolderResource>
		_knowledgeBaseFolderResourceComponentServiceObjects;
	private static ComponentServiceObjects<LanguageResource>
		_languageResourceComponentServiceObjects;
	private static ComponentServiceObjects<MessageBoardAttachmentResource>
		_messageBoardAttachmentResourceComponentServiceObjects;
	private static ComponentServiceObjects<MessageBoardMessageResource>
		_messageBoardMessageResourceComponentServiceObjects;
	private static ComponentServiceObjects<MessageBoardSectionResource>
		_messageBoardSectionResourceComponentServiceObjects;
	private static ComponentServiceObjects<MessageBoardThreadResource>
		_messageBoardThreadResourceComponentServiceObjects;
	private static ComponentServiceObjects<NavigationMenuResource>
		_navigationMenuResourceComponentServiceObjects;
	private static ComponentServiceObjects<SitePageResource>
		_sitePageResourceComponentServiceObjects;
	private static ComponentServiceObjects<StructuredContentResource>
		_structuredContentResourceComponentServiceObjects;
	private static ComponentServiceObjects<StructuredContentFolderResource>
		_structuredContentFolderResourceComponentServiceObjects;
	private static ComponentServiceObjects<WikiNodeResource>
		_wikiNodeResourceComponentServiceObjects;
	private static ComponentServiceObjects<WikiPageResource>
		_wikiPageResourceComponentServiceObjects;
	private static ComponentServiceObjects<WikiPageAttachmentResource>
		_wikiPageAttachmentResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private BiFunction<Object, List<String>, Aggregation>
		_aggregationBiFunction;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction<Object, String, Filter> _filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}