/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.graphql.mutation.v1_0;

import com.liferay.headless.delivery.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.dto.v1_0.BlogPostingImage;
import com.liferay.headless.delivery.dto.v1_0.Comment;
import com.liferay.headless.delivery.dto.v1_0.Document;
import com.liferay.headless.delivery.dto.v1_0.DocumentDataDefinitionType;
import com.liferay.headless.delivery.dto.v1_0.DocumentFolder;
import com.liferay.headless.delivery.dto.v1_0.DocumentMetadataSet;
import com.liferay.headless.delivery.dto.v1_0.DocumentShortcut;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseAttachment;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseFolder;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardAttachment;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardMessage;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardSection;
import com.liferay.headless.delivery.dto.v1_0.MessageBoardThread;
import com.liferay.headless.delivery.dto.v1_0.NavigationMenu;
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
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.multipart.MultipartBody;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.function.BiFunction;

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.validation.constraints.NotEmpty;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Mutation {

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

	@GraphQLField(
		description = "Deletes the blog post and returns a 204 if the operation succeeds."
	)
	public boolean deleteBlogPosting(
			@GraphQLName("blogPostingId") Long blogPostingId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.deleteBlogPosting(
				blogPostingId));

		return true;
	}

	@GraphQLField
	public Response deleteBlogPostingBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.deleteBlogPostingBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Updates the blog post using only the fields received in the request body. Any other fields are left untouched. Returns the updated blog post."
	)
	public BlogPosting patchBlogPosting(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("blogPosting") BlogPosting blogPosting)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.patchBlogPosting(
				blogPostingId, blogPosting));
	}

	@GraphQLField(
		description = "Replaces the blog post with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public BlogPosting updateBlogPosting(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("blogPosting") BlogPosting blogPosting)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.putBlogPosting(
				blogPostingId, blogPosting));
	}

	@GraphQLField
	public Response updateBlogPostingBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.putBlogPostingBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the blog post rating of the user who authenticated the request."
	)
	public boolean deleteBlogPostingMyRating(
			@GraphQLName("blogPostingId") Long blogPostingId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.deleteBlogPostingMyRating(blogPostingId));

		return true;
	}

	@GraphQLField(
		description = "Creates a new blog post rating by the user who authenticated the request."
	)
	public Rating createBlogPostingMyRating(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.postBlogPostingMyRating(
				blogPostingId, rating));
	}

	@GraphQLField(
		description = "Replaces an existing blog post rating by the user who authenticated the request."
	)
	public Rating updateBlogPostingMyRating(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.putBlogPostingMyRating(
				blogPostingId, rating));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateBlogPostingPermissionsPage(
				@GraphQLName("blogPostingId") Long blogPostingId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> {
				Page paginationPage =
					blogPostingResource.putBlogPostingPermissionsPage(
						blogPostingId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createSiteBlogPostingsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.postSiteBlogPostingsPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(blogPostingResource, filterString),
					_sortsBiFunction.apply(blogPostingResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new blog post.")
	public BlogPosting createSiteBlogPosting(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("blogPosting") BlogPosting blogPosting)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.postSiteBlogPosting(
				Long.valueOf(siteKey), blogPosting));
	}

	@GraphQLField
	public Response createSiteBlogPostingBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> blogPostingResource.postSiteBlogPostingBatch(
				Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the blog post by external reference code."
	)
	public boolean deleteSiteBlogPostingByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.
					deleteSiteBlogPostingByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's blog post with the given external reference code, or creates it if it not exists."
	)
	public BlogPosting updateSiteBlogPostingByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("blogPosting") BlogPosting blogPosting)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.putSiteBlogPostingByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode, blogPosting));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteBlogPostingPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource -> {
				Page paginationPage =
					blogPostingResource.putSiteBlogPostingPermissionsPage(
						Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateSiteBlogPostingSubscribe(
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.putSiteBlogPostingSubscribe(
					Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField
	public boolean updateSiteBlogPostingUnsubscribe(
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingResource ->
				blogPostingResource.putSiteBlogPostingUnsubscribe(
					Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField(description = "Deletes the blog post's image.")
	public boolean deleteBlogPostingImage(
			@GraphQLName("blogPostingImageId") Long blogPostingImageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.deleteBlogPostingImage(
					blogPostingImageId));

		return true;
	}

	@GraphQLField
	public Response deleteBlogPostingImageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.deleteBlogPostingImageBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response createSiteBlogPostingImagesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.
					postSiteBlogPostingImagesPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							blogPostingImageResource, filterString),
						_sortsBiFunction.apply(
							blogPostingImageResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a blog post image. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`blogPostingImage`) with the metadata."
	)
	@GraphQLName(
		description = "Creates a blog post image. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`blogPostingImage`) with the metadata.",
		value = "postSiteBlogPostingImageSiteIdMultipartBody"
	)
	public BlogPostingImage createSiteBlogPostingImage(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.postSiteBlogPostingImage(
					Long.valueOf(siteKey), multipartBody));
	}

	@GraphQLField
	public Response createSiteBlogPostingImageBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.postSiteBlogPostingImageBatch(
					Long.valueOf(siteKey), multipartBody, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's blog post image by external reference code."
	)
	public boolean deleteSiteBlogPostingImageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects,
			this::_populateResourceContext,
			blogPostingImageResource ->
				blogPostingImageResource.
					deleteSiteBlogPostingImageByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField
	public Response createBlogPostingCommentsPageExportBatch(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.postBlogPostingCommentsPageExportBatch(
					blogPostingId, search,
					_filterBiFunction.apply(commentResource, filterString),
					_sortsBiFunction.apply(commentResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new comment on the blog post.")
	public Comment createBlogPostingComment(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.postBlogPostingComment(
				blogPostingId, comment));
	}

	@GraphQLField
	public Response createBlogPostingCommentBatch(
			@GraphQLName("blogPostingId") Long blogPostingId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.postBlogPostingCommentBatch(
				blogPostingId, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the comment and returns a 204 if the operation succeeded."
	)
	public boolean deleteComment(@GraphQLName("commentId") Long commentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.deleteComment(commentId));

		return true;
	}

	@GraphQLField
	public Response deleteCommentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.deleteCommentBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the comment with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public Comment updateComment(
			@GraphQLName("commentId") Long commentId,
			@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.putComment(commentId, comment));
	}

	@GraphQLField
	public Response updateCommentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.putCommentBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Creates a new child comment of the existing comment."
	)
	public Comment createCommentComment(
			@GraphQLName("parentCommentId") Long parentCommentId,
			@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.postCommentComment(
				parentCommentId, comment));
	}

	@GraphQLField
	public Response createDocumentCommentsPageExportBatch(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.postDocumentCommentsPageExportBatch(
					documentId, search,
					_filterBiFunction.apply(commentResource, filterString),
					_sortsBiFunction.apply(commentResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new comment on the document.")
	public Comment createDocumentComment(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.postDocumentComment(
				documentId, comment));
	}

	@GraphQLField
	public Response createDocumentCommentBatch(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.postDocumentCommentBatch(
				documentId, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the blog posting's comment by blog posting's and comment's external reference codes."
	)
	public boolean
			deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("blogPostingExternalReferenceCode") String
					blogPostingExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey), blogPostingExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the blog posting's comment given the blog posting's and comment's external reference codes, or creates it if it not exists."
	)
	public Comment
			updateSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("blogPostingExternalReferenceCode") String
					blogPostingExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					putSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey), blogPostingExternalReferenceCode,
						externalReferenceCode, comment));
	}

	@GraphQLField(
		description = "Deletes the parent comment's comment by its parent comment's and comment's external reference codes."
	)
	public boolean
			deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("parentCommentExternalReferenceCode") String
					parentCommentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey),
						parentCommentExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the parent comment's comment given the parent comment's and comment's external reference codes, or creates it if it not exists."
	)
	public Comment
			updateSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("parentCommentExternalReferenceCode") String
					parentCommentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					putSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey),
						parentCommentExternalReferenceCode,
						externalReferenceCode, comment));
	}

	@GraphQLField(
		description = "Deletes the document's comment by document's and comment's external reference codes."
	)
	public boolean
			deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("documentExternalReferenceCode") String
					documentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey), documentExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the document's comment given the document's and comment's external reference codes, or creates it if it not exists."
	)
	public Comment
			updateSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("documentExternalReferenceCode") String
					documentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					putSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey), documentExternalReferenceCode,
						externalReferenceCode, comment));
	}

	@GraphQLField(
		description = "Deletes the structured content's comment by structured content's and comment's external reference codes."
	)
	public boolean
			deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("structuredContentExternalReferenceCode") String
					structuredContentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey),
						structuredContentExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the structured content's comment given the structured content's and comment's external reference codes, or creates it if it not exists."
	)
	public Comment
			updateSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("structuredContentExternalReferenceCode") String
					structuredContentExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.
					putSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode(
						Long.valueOf(siteKey),
						structuredContentExternalReferenceCode,
						externalReferenceCode, comment));
	}

	@GraphQLField
	public Response createStructuredContentCommentsPageExportBatch(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.postStructuredContentCommentsPageExportBatch(
					structuredContentId, search,
					_filterBiFunction.apply(commentResource, filterString),
					_sortsBiFunction.apply(commentResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new comment on the structured content."
	)
	public Comment createStructuredContentComment(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("comment") Comment comment)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource -> commentResource.postStructuredContentComment(
				structuredContentId, comment));
	}

	@GraphQLField
	public Response createStructuredContentCommentBatch(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_commentResourceComponentServiceObjects,
			this::_populateResourceContext,
			commentResource ->
				commentResource.postStructuredContentCommentBatch(
					structuredContentId, callbackURL, object));
	}

	@GraphQLField
	public Response createAssetLibraryContentElementsPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentElementResource ->
				contentElementResource.
					postAssetLibraryContentElementsPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							contentElementResource, filterString),
						_sortsBiFunction.apply(
							contentElementResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createSiteContentElementsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentElementResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentElementResource ->
				contentElementResource.postSiteContentElementsPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(
						contentElementResource, filterString),
					_sortsBiFunction.apply(contentElementResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createAssetLibraryContentStructuresPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource ->
				contentStructureResource.
					postAssetLibraryContentStructuresPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							contentStructureResource, filterString),
						_sortsBiFunction.apply(
							contentStructureResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryContentStructurePermissionsPage(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> {
				Page paginationPage =
					contentStructureResource.
						putAssetLibraryContentStructurePermissionsPage(
							Long.valueOf(assetLibraryId), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateContentStructurePermissionsPage(
				@GraphQLName("contentStructureId") Long contentStructureId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> {
				Page paginationPage =
					contentStructureResource.putContentStructurePermissionsPage(
						contentStructureId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createSiteContentStructuresPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource ->
				contentStructureResource.
					postSiteContentStructuresPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							contentStructureResource, filterString),
						_sortsBiFunction.apply(
							contentStructureResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteContentStructurePermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentStructureResource -> {
				Page paginationPage =
					contentStructureResource.
						putSiteContentStructurePermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createAssetLibraryContentTemplatesPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentTemplateResource ->
				contentTemplateResource.
					postAssetLibraryContentTemplatesPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							contentTemplateResource, filterString),
						_sortsBiFunction.apply(
							contentTemplateResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createSiteContentTemplatesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_contentTemplateResourceComponentServiceObjects,
			this::_populateResourceContext,
			contentTemplateResource ->
				contentTemplateResource.postSiteContentTemplatesPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(
						contentTemplateResource, filterString),
					_sortsBiFunction.apply(
						contentTemplateResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentsPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.postAssetLibraryDocumentsPageExportBatch(
					Long.valueOf(assetLibraryId), search,
					_filterBiFunction.apply(documentResource, filterString),
					_sortsBiFunction.apply(documentResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	@GraphQLName(
		description = "null",
		value = "postAssetLibraryDocumentAssetLibraryIdMultipartBody"
	)
	public Document createAssetLibraryDocument(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.postAssetLibraryDocument(
				Long.valueOf(assetLibraryId), multipartBody));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.postAssetLibraryDocumentBatch(
				Long.valueOf(assetLibraryId), multipartBody, callbackURL,
				object));
	}

	@GraphQLField(
		description = "Deletes the asset library's document by external reference code."
	)
	public boolean deleteAssetLibraryDocumentByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.
					deleteAssetLibraryDocumentByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Replaces the document by external reference code with the information sent in the request body, or replaces it if it not exists. Any missing fields are deleted, unless they are required. The request body must be `multipart/form-data` with two parts, the file'sbytes (`file`), and an optional JSON string (`document`) with the metadata."
	)
	@GraphQLName(
		description = "Replaces the document by external reference code with the information sent in the request body, or replaces it if it not exists. Any missing fields are deleted, unless they are required. The request body must be `multipart/form-data` with two parts, the file'sbytes (`file`), and an optional JSON string (`document`) with the metadata.",
		value = "putAssetLibraryDocumentByExternalReferenceCodeAssetLibraryIdExternalReferenceCodeMultipartBody"
	)
	public Document updateAssetLibraryDocumentByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.putAssetLibraryDocumentByExternalReferenceCode(
					Long.valueOf(assetLibraryId), externalReferenceCode,
					multipartBody));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryDocumentPermissionsPage(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> {
				Page paginationPage =
					documentResource.putAssetLibraryDocumentPermissionsPage(
						Long.valueOf(assetLibraryId), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createDocumentFolderDocumentsPageExportBatch(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.postDocumentFolderDocumentsPageExportBatch(
					documentFolderId, search,
					_filterBiFunction.apply(documentResource, filterString),
					_sortsBiFunction.apply(documentResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new document inside the folder identified by `documentFolderId`. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata."
	)
	@GraphQLName(
		description = "Creates a new document inside the folder identified by `documentFolderId`. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata.",
		value = "postDocumentFolderDocumentDocumentFolderIdMultipartBody"
	)
	public Document createDocumentFolderDocument(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.postDocumentFolderDocument(
				documentFolderId, multipartBody));
	}

	@GraphQLField
	public Response createDocumentFolderDocumentBatch(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.postDocumentFolderDocumentBatch(
					documentFolderId, multipartBody, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the document and returns a 204 if the operation succeeds."
	)
	public boolean deleteDocument(@GraphQLName("documentId") Long documentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.deleteDocument(documentId));

		return true;
	}

	@GraphQLField
	public Response deleteDocumentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.deleteDocumentBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata."
	)
	@GraphQLName(
		description = "Updates only the fields received in the request body, leaving any other fields untouched. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata.",
		value = "patchDocumentDocumentIdMultipartBody"
	)
	public Document patchDocument(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.patchDocument(
				documentId, multipartBody));
	}

	@GraphQLField(
		description = "Replaces the document with the information sent in the request body. Any missing fields are deleted, unless they are required. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata."
	)
	@GraphQLName(
		description = "Replaces the document with the information sent in the request body. Any missing fields are deleted, unless they are required. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata.",
		value = "putDocumentDocumentIdMultipartBody"
	)
	public Document updateDocument(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.putDocument(
				documentId, multipartBody));
	}

	@GraphQLField
	public Response updateDocumentBatch(
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.putDocumentBatch(
				multipartBody, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the document's rating and returns a 204 if the operation succeeded."
	)
	public boolean deleteDocumentMyRating(
			@GraphQLName("documentId") Long documentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.deleteDocumentMyRating(
				documentId));

		return true;
	}

	@GraphQLField(
		description = "Creates a new rating for the document, by the user who authenticated the request."
	)
	public Rating createDocumentMyRating(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.postDocumentMyRating(
				documentId, rating));
	}

	@GraphQLField(
		description = "Replaces the rating with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public Rating updateDocumentMyRating(
			@GraphQLName("documentId") Long documentId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.putDocumentMyRating(
				documentId, rating));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateDocumentPermissionsPage(
				@GraphQLName("documentId") Long documentId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> {
				Page paginationPage =
					documentResource.putDocumentPermissionsPage(
						documentId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createSiteDocumentsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.postSiteDocumentsPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(documentResource, filterString),
					_sortsBiFunction.apply(documentResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new document. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata."
	)
	@GraphQLName(
		description = "Creates a new document. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`document`) with the metadata.",
		value = "postSiteDocumentSiteIdMultipartBody"
	)
	public Document createSiteDocument(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.postSiteDocument(
				Long.valueOf(siteKey), multipartBody));
	}

	@GraphQLField
	public Response createSiteDocumentBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> documentResource.postSiteDocumentBatch(
				Long.valueOf(siteKey), multipartBody, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's document by external reference code returns a 204 if the operation succeeds."
	)
	public boolean deleteSiteDocumentByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.deleteSiteDocumentByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Replaces the document by external reference code with the information sent in the request body, or replaces it if it not exists. Any missing fields are deleted, unless they are required. The request body must be `multipart/form-data` with two parts, the file'sbytes (`file`), and an optional JSON string (`document`) with the metadata."
	)
	@GraphQLName(
		description = "Replaces the document by external reference code with the information sent in the request body, or replaces it if it not exists. Any missing fields are deleted, unless they are required. The request body must be `multipart/form-data` with two parts, the file'sbytes (`file`), and an optional JSON string (`document`) with the metadata.",
		value = "putSiteDocumentByExternalReferenceCodeSiteIdExternalReferenceCodeMultipartBody"
	)
	public Document updateSiteDocumentByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource ->
				documentResource.putSiteDocumentByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode,
					multipartBody));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteDocumentPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentResource -> {
				Page paginationPage =
					documentResource.putSiteDocumentPermissionsPage(
						Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response
			createAssetLibraryDocumentDataDefinitionTypesPageExportBatch(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("sort") String sortsString,
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("contentType") String contentType,
				@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					postAssetLibraryDocumentDataDefinitionTypesPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							documentDataDefinitionTypeResource, filterString),
						_sortsBiFunction.apply(
							documentDataDefinitionTypeResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new document data definition type.")
	public DocumentDataDefinitionType
			createAssetLibraryDocumentDataDefinitionType(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("documentDataDefinitionType")
					DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					postAssetLibraryDocumentDataDefinitionType(
						Long.valueOf(assetLibraryId),
						documentDataDefinitionType));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentDataDefinitionTypeBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					postAssetLibraryDocumentDataDefinitionTypeBatch(
						Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField
	public boolean deleteDocumentDataDefinitionType(
			@GraphQLName("documentDataDefinitionTypeId") Long
				documentDataDefinitionTypeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					deleteDocumentDataDefinitionType(
						documentDataDefinitionTypeId));

		return true;
	}

	@GraphQLField
	public Response deleteDocumentDataDefinitionTypeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					deleteDocumentDataDefinitionTypeBatch(callbackURL, object));
	}

	@GraphQLField
	public Response createSiteDocumentDataDefinitionTypesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					postSiteDocumentDataDefinitionTypesPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							documentDataDefinitionTypeResource, filterString),
						_sortsBiFunction.apply(
							documentDataDefinitionTypeResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new document data definition type.")
	public DocumentDataDefinitionType createSiteDocumentDataDefinitionType(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("documentDataDefinitionType")
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					postSiteDocumentDataDefinitionType(
						Long.valueOf(siteKey), documentDataDefinitionType));
	}

	@GraphQLField
	public Response createSiteDocumentDataDefinitionTypeBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentDataDefinitionTypeResource ->
				documentDataDefinitionTypeResource.
					postSiteDocumentDataDefinitionTypeBatch(
						Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentFoldersPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.
					postAssetLibraryDocumentFoldersPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							documentFolderResource, filterString),
						_sortsBiFunction.apply(
							documentFolderResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public DocumentFolder createAssetLibraryDocumentFolder(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("documentFolder") DocumentFolder documentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.postAssetLibraryDocumentFolder(
					Long.valueOf(assetLibraryId), documentFolder));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentFolderBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.postAssetLibraryDocumentFolderBatch(
					Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryDocumentFolderPermissionsPage(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> {
				Page paginationPage =
					documentFolderResource.
						putAssetLibraryDocumentFolderPermissionsPage(
							Long.valueOf(assetLibraryId), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Deletes the document folder and returns a 204 if the operation succeeds."
	)
	public boolean deleteDocumentFolder(
			@GraphQLName("documentFolderId") Long documentFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.deleteDocumentFolder(documentFolderId));

		return true;
	}

	@GraphQLField
	public Response deleteDocumentFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.deleteDocumentFolderBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body. Any other fields are left untouched."
	)
	public DocumentFolder patchDocumentFolder(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("documentFolder") DocumentFolder documentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.patchDocumentFolder(
					documentFolderId, documentFolder));
	}

	@GraphQLField(
		description = "Replaces the document folder with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public DocumentFolder updateDocumentFolder(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("documentFolder") DocumentFolder documentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> documentFolderResource.putDocumentFolder(
				documentFolderId, documentFolder));
	}

	@GraphQLField
	public Response updateDocumentFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.putDocumentFolderBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the document folder's rating and returns a 204 if the operation succeeded."
	)
	public boolean deleteDocumentFolderMyRating(
			@GraphQLName("documentFolderId") Long documentFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.deleteDocumentFolderMyRating(
					documentFolderId));

		return true;
	}

	@GraphQLField(
		description = "Creates a new rating for the document folder, by the user who authenticated the request."
	)
	public Rating createDocumentFolderMyRating(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.postDocumentFolderMyRating(
					documentFolderId, rating));
	}

	@GraphQLField(
		description = "Replaces the rating with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public Rating updateDocumentFolderMyRating(
			@GraphQLName("documentFolderId") Long documentFolderId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.putDocumentFolderMyRating(
					documentFolderId, rating));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateDocumentFolderPermissionsPage(
				@GraphQLName("documentFolderId") Long documentFolderId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> {
				Page paginationPage =
					documentFolderResource.putDocumentFolderPermissionsPage(
						documentFolderId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateDocumentFolderSubscribe(
			@GraphQLName("documentFolderId") Long documentFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.putDocumentFolderSubscribe(
					documentFolderId));

		return true;
	}

	@GraphQLField
	public boolean updateDocumentFolderUnsubscribe(
			@GraphQLName("documentFolderId") Long documentFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.putDocumentFolderUnsubscribe(
					documentFolderId));

		return true;
	}

	@GraphQLField(
		description = "Creates a new folder in a folder identified by `parentDocumentFolderId`."
	)
	public DocumentFolder createDocumentFolderDocumentFolder(
			@GraphQLName("parentDocumentFolderId") Long parentDocumentFolderId,
			@GraphQLName("documentFolder") DocumentFolder documentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.postDocumentFolderDocumentFolder(
					parentDocumentFolderId, documentFolder));
	}

	@GraphQLField
	public Response createSiteDocumentFoldersPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.postSiteDocumentFoldersPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(
						documentFolderResource, filterString),
					_sortsBiFunction.apply(documentFolderResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new document folder.")
	public DocumentFolder createSiteDocumentFolder(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("documentFolder") DocumentFolder documentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.postSiteDocumentFolder(
					Long.valueOf(siteKey), documentFolder));
	}

	@GraphQLField
	public Response createSiteDocumentFolderBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.postSiteDocumentFolderBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteDocumentFolderPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource -> {
				Page paginationPage =
					documentFolderResource.putSiteDocumentFolderPermissionsPage(
						Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Deletes the site's document folder by external reference code returns a 204 if the operation succeeds."
	)
	public boolean deleteSiteDocumentsFolderByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.
					deleteSiteDocumentsFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Replaces the document folder by external reference code with the information sent in the request body, or replaces it if it not exists."
	)
	public DocumentFolder updateSiteDocumentsFolderByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("documentFolder") DocumentFolder documentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentFolderResource ->
				documentFolderResource.
					putSiteDocumentsFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						documentFolder));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentMetadataSetsPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					postAssetLibraryDocumentMetadataSetsPageExportBatch(
						Long.valueOf(assetLibraryId), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public DocumentMetadataSet createAssetLibraryDocumentMetadataSet(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("documentMetadataSet") DocumentMetadataSet
				documentMetadataSet)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.postAssetLibraryDocumentMetadataSet(
					Long.valueOf(assetLibraryId), documentMetadataSet));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentMetadataSetBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					postAssetLibraryDocumentMetadataSetBatch(
						Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the asset library's Document Metadata Set by external reference code."
	)
	public boolean deleteAssetLibraryDocumentMetadataSetByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					deleteAssetLibraryDocumentMetadataSetByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the asset Library's Document Metadata Set with the given external reference code, or creates it if it does not exists."
	)
	public DocumentMetadataSet
			updateAssetLibraryDocumentMetadataSetByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("documentMetadataSet") DocumentMetadataSet
					documentMetadataSet)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					putAssetLibraryDocumentMetadataSetByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode,
						documentMetadataSet));
	}

	@GraphQLField(
		description = "Deletes the document metadata set and returns a 204 if the operation succeeds."
	)
	public boolean deleteDocumentMetadataSet(
			@GraphQLName("documentMetadataSetId") Long documentMetadataSetId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.deleteDocumentMetadataSet(
					documentMetadataSetId));

		return true;
	}

	@GraphQLField
	public Response deleteDocumentMetadataSetBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.deleteDocumentMetadataSetBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response createSiteDocumentMetadataSetsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					postSiteDocumentMetadataSetsPageExportBatch(
						Long.valueOf(siteKey), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public DocumentMetadataSet createSiteDocumentMetadataSet(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("documentMetadataSet") DocumentMetadataSet
				documentMetadataSet)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.postSiteDocumentMetadataSet(
					Long.valueOf(siteKey), documentMetadataSet));
	}

	@GraphQLField
	public Response createSiteDocumentMetadataSetBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.postSiteDocumentMetadataSetBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's Document Metadata Set by external reference code."
	)
	public boolean deleteSiteDocumentMetadataSetByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					deleteSiteDocumentMetadataSetByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's Document Metadata Set with the given external reference code, or creates it if it does not exist."
	)
	public DocumentMetadataSet
			updateSiteDocumentMetadataSetByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("documentMetadataSet") DocumentMetadataSet
					documentMetadataSet)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentMetadataSetResource ->
				documentMetadataSetResource.
					putSiteDocumentMetadataSetByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						documentMetadataSet));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentShortcutsPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.
					postAssetLibraryDocumentShortcutsPageExportBatch(
						Long.valueOf(assetLibraryId), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public DocumentShortcut createAssetLibraryDocumentShortcut(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("documentShortcut") DocumentShortcut documentShortcut)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.postAssetLibraryDocumentShortcut(
					Long.valueOf(assetLibraryId), documentShortcut));
	}

	@GraphQLField
	public Response createAssetLibraryDocumentShortcutBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.postAssetLibraryDocumentShortcutBatch(
					Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the document shortcut and returns a 204 if the operation succeeds."
	)
	public boolean deleteDocumentShortcut(
			@GraphQLName("documentShortcutId") Long documentShortcutId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.deleteDocumentShortcut(
					documentShortcutId));

		return true;
	}

	@GraphQLField
	public Response deleteDocumentShortcutBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.deleteDocumentShortcutBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public DocumentShortcut patchDocumentShortcut(
			@GraphQLName("documentShortcutId") Long documentShortcutId,
			@GraphQLName("documentShortcut") DocumentShortcut documentShortcut)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.patchDocumentShortcut(
					documentShortcutId, documentShortcut));
	}

	@GraphQLField(
		description = "Replaces the document shortcut with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public DocumentShortcut updateDocumentShortcut(
			@GraphQLName("documentShortcutId") Long documentShortcutId,
			@GraphQLName("documentShortcut") DocumentShortcut documentShortcut)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.putDocumentShortcut(
					documentShortcutId, documentShortcut));
	}

	@GraphQLField
	public Response updateDocumentShortcutBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.putDocumentShortcutBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response createSiteDocumentShortcutsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.
					postSiteDocumentShortcutsPageExportBatch(
						Long.valueOf(siteKey), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField
	public DocumentShortcut createSiteDocumentShortcut(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("documentShortcut") DocumentShortcut documentShortcut)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.postSiteDocumentShortcut(
					Long.valueOf(siteKey), documentShortcut));
	}

	@GraphQLField
	public Response createSiteDocumentShortcutBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.postSiteDocumentShortcutBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's document shortcut by external reference code returns a 204 if the operation succeeds."
	)
	public boolean deleteSiteDocumentShortcutByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.
					deleteSiteDocumentShortcutByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField
	public DocumentShortcut updateSiteDocumentShortcutByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("documentShortcut") DocumentShortcut documentShortcut)
		throws Exception {

		return _applyComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects,
			this::_populateResourceContext,
			documentShortcutResource ->
				documentShortcutResource.
					putSiteDocumentShortcutByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						documentShortcut));
	}

	@GraphQLField(
		description = "Deletes the knowledge base article and returns a 204 if the operation succeeds."
	)
	public boolean deleteKnowledgeBaseArticle(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.deleteKnowledgeBaseArticle(
					knowledgeBaseArticleId));

		return true;
	}

	@GraphQLField
	public Response deleteKnowledgeBaseArticleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.deleteKnowledgeBaseArticleBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public KnowledgeBaseArticle patchKnowledgeBaseArticle(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId,
			@GraphQLName("knowledgeBaseArticle") KnowledgeBaseArticle
				knowledgeBaseArticle)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.patchKnowledgeBaseArticle(
					knowledgeBaseArticleId, knowledgeBaseArticle));
	}

	@GraphQLField(
		description = "Replaces the knowledge base article with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public KnowledgeBaseArticle updateKnowledgeBaseArticle(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId,
			@GraphQLName("knowledgeBaseArticle") KnowledgeBaseArticle
				knowledgeBaseArticle)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.putKnowledgeBaseArticle(
					knowledgeBaseArticleId, knowledgeBaseArticle));
	}

	@GraphQLField
	public Response updateKnowledgeBaseArticleBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.putKnowledgeBaseArticleBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the knowledge base article's rating and returns a 204 if the operation succeeds."
	)
	public boolean deleteKnowledgeBaseArticleMyRating(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.deleteKnowledgeBaseArticleMyRating(
					knowledgeBaseArticleId));

		return true;
	}

	@GraphQLField(
		description = "Creates a rating for the knowledge base article."
	)
	public Rating createKnowledgeBaseArticleMyRating(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.postKnowledgeBaseArticleMyRating(
					knowledgeBaseArticleId, rating));
	}

	@GraphQLField(
		description = "Replaces the rating with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public Rating updateKnowledgeBaseArticleMyRating(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.putKnowledgeBaseArticleMyRating(
					knowledgeBaseArticleId, rating));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateKnowledgeBaseArticlePermissionsPage(
				@GraphQLName("knowledgeBaseArticleId") Long
					knowledgeBaseArticleId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource -> {
				Page paginationPage =
					knowledgeBaseArticleResource.
						putKnowledgeBaseArticlePermissionsPage(
							knowledgeBaseArticleId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateKnowledgeBaseArticleSubscribe(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.putKnowledgeBaseArticleSubscribe(
					knowledgeBaseArticleId));

		return true;
	}

	@GraphQLField
	public boolean updateKnowledgeBaseArticleUnsubscribe(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.putKnowledgeBaseArticleUnsubscribe(
					knowledgeBaseArticleId));

		return true;
	}

	@GraphQLField(
		description = "Creates a child knowledge base article of the knowledge base article identified by `parentKnowledgeBaseArticleId`."
	)
	public KnowledgeBaseArticle createKnowledgeBaseArticleKnowledgeBaseArticle(
			@GraphQLName("parentKnowledgeBaseArticleId") Long
				parentKnowledgeBaseArticleId,
			@GraphQLName("knowledgeBaseArticle") KnowledgeBaseArticle
				knowledgeBaseArticle)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					postKnowledgeBaseArticleKnowledgeBaseArticle(
						parentKnowledgeBaseArticleId, knowledgeBaseArticle));
	}

	@GraphQLField
	public Response
			createKnowledgeBaseFolderKnowledgeBaseArticlesPageExportBatch(
				@GraphQLName("knowledgeBaseFolderId") Long
					knowledgeBaseFolderId,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("sort") String sortsString,
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("contentType") String contentType,
				@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					postKnowledgeBaseFolderKnowledgeBaseArticlesPageExportBatch(
						knowledgeBaseFolderId, search,
						_filterBiFunction.apply(
							knowledgeBaseArticleResource, filterString),
						_sortsBiFunction.apply(
							knowledgeBaseArticleResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new knowledge base article in the folder."
	)
	public KnowledgeBaseArticle createKnowledgeBaseFolderKnowledgeBaseArticle(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId,
			@GraphQLName("knowledgeBaseArticle") KnowledgeBaseArticle
				knowledgeBaseArticle)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					postKnowledgeBaseFolderKnowledgeBaseArticle(
						knowledgeBaseFolderId, knowledgeBaseArticle));
	}

	@GraphQLField
	public Response createKnowledgeBaseFolderKnowledgeBaseArticleBatch(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					postKnowledgeBaseFolderKnowledgeBaseArticleBatch(
						knowledgeBaseFolderId, callbackURL, object));
	}

	@GraphQLField
	public Response createSiteKnowledgeBaseArticlesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					postSiteKnowledgeBaseArticlesPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							knowledgeBaseArticleResource, filterString),
						_sortsBiFunction.apply(
							knowledgeBaseArticleResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new knowledge base article.")
	public KnowledgeBaseArticle createSiteKnowledgeBaseArticle(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("knowledgeBaseArticle") KnowledgeBaseArticle
				knowledgeBaseArticle)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.postSiteKnowledgeBaseArticle(
					Long.valueOf(siteKey), knowledgeBaseArticle));
	}

	@GraphQLField
	public Response createSiteKnowledgeBaseArticleBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.postSiteKnowledgeBaseArticleBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the knowledge base article by external reference code."
	)
	public boolean deleteSiteKnowledgeBaseArticleByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					deleteSiteKnowledgeBaseArticleByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's knowledge base article with the given external reference code, or creates it if it not exists."
	)
	public KnowledgeBaseArticle
			updateSiteKnowledgeBaseArticleByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("knowledgeBaseArticle") KnowledgeBaseArticle
					knowledgeBaseArticle)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					putSiteKnowledgeBaseArticleByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						knowledgeBaseArticle));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteKnowledgeBaseArticlePermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource -> {
				Page paginationPage =
					knowledgeBaseArticleResource.
						putSiteKnowledgeBaseArticlePermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateSiteKnowledgeBaseArticleSubscribe(
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					putSiteKnowledgeBaseArticleSubscribe(
						Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField
	public boolean updateSiteKnowledgeBaseArticleUnsubscribe(
			@GraphQLName("siteKey") @NotEmpty String siteKey)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseArticleResource ->
				knowledgeBaseArticleResource.
					putSiteKnowledgeBaseArticleUnsubscribe(
						Long.valueOf(siteKey)));

		return true;
	}

	@GraphQLField
	public Response
			createKnowledgeBaseArticleKnowledgeBaseAttachmentsPageExportBatch(
				@GraphQLName("knowledgeBaseArticleId") Long
					knowledgeBaseArticleId,
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("contentType") String contentType,
				@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.
					postKnowledgeBaseArticleKnowledgeBaseAttachmentsPageExportBatch(
						knowledgeBaseArticleId, callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField(
		description = "Creates a new attachment for an existing knowledge base article. The request body must be `multipart/form-data` with two parts, a `file` part with the file's bytes, and an optional JSON string (`knowledgeBaseAttachment`) with the metadata."
	)
	@GraphQLName(
		description = "Creates a new attachment for an existing knowledge base article. The request body must be `multipart/form-data` with two parts, a `file` part with the file's bytes, and an optional JSON string (`knowledgeBaseAttachment`) with the metadata.",
		value = "postKnowledgeBaseArticleKnowledgeBaseAttachmentKnowledgeBaseArticleIdMultipartBody"
	)
	public KnowledgeBaseAttachment
			createKnowledgeBaseArticleKnowledgeBaseAttachment(
				@GraphQLName("knowledgeBaseArticleId") Long
					knowledgeBaseArticleId,
				@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.
					postKnowledgeBaseArticleKnowledgeBaseAttachment(
						knowledgeBaseArticleId, multipartBody));
	}

	@GraphQLField
	public Response createKnowledgeBaseArticleKnowledgeBaseAttachmentBatch(
			@GraphQLName("knowledgeBaseArticleId") Long knowledgeBaseArticleId,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.
					postKnowledgeBaseArticleKnowledgeBaseAttachmentBatch(
						knowledgeBaseArticleId, multipartBody, callbackURL,
						object));
	}

	@GraphQLField(
		description = "Deletes the knowledge base file attachment and returns a 204 if the operation succeeds."
	)
	public boolean deleteKnowledgeBaseAttachment(
			@GraphQLName("knowledgeBaseAttachmentId") Long
				knowledgeBaseAttachmentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.deleteKnowledgeBaseAttachment(
					knowledgeBaseAttachmentId));

		return true;
	}

	@GraphQLField
	public Response deleteKnowledgeBaseAttachmentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.
					deleteKnowledgeBaseAttachmentBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Delete the knowledge base attachment by knowledge base article's and knowledge base attachment's external reference codes."
	)
	public boolean
			deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("knowledgeBaseArticleExternalReferenceCode") String
					knowledgeBaseArticleExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseAttachmentResource ->
				knowledgeBaseAttachmentResource.
					deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode(
						Long.valueOf(siteKey),
						knowledgeBaseArticleExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Deletes the knowledge base folder and returns a 204 if the operation succeeds."
	)
	public boolean deleteKnowledgeBaseFolder(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.deleteKnowledgeBaseFolder(
					knowledgeBaseFolderId));

		return true;
	}

	@GraphQLField
	public Response deleteKnowledgeBaseFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.deleteKnowledgeBaseFolderBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public KnowledgeBaseFolder patchKnowledgeBaseFolder(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId,
			@GraphQLName("knowledgeBaseFolder") KnowledgeBaseFolder
				knowledgeBaseFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.patchKnowledgeBaseFolder(
					knowledgeBaseFolderId, knowledgeBaseFolder));
	}

	@GraphQLField(
		description = "Replaces the knowledge base folder with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public KnowledgeBaseFolder updateKnowledgeBaseFolder(
			@GraphQLName("knowledgeBaseFolderId") Long knowledgeBaseFolderId,
			@GraphQLName("knowledgeBaseFolder") KnowledgeBaseFolder
				knowledgeBaseFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.putKnowledgeBaseFolder(
					knowledgeBaseFolderId, knowledgeBaseFolder));
	}

	@GraphQLField
	public Response updateKnowledgeBaseFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.putKnowledgeBaseFolderBatch(
					callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateKnowledgeBaseFolderPermissionsPage(
				@GraphQLName("knowledgeBaseFolderId") Long
					knowledgeBaseFolderId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource -> {
				Page paginationPage =
					knowledgeBaseFolderResource.
						putKnowledgeBaseFolderPermissionsPage(
							knowledgeBaseFolderId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Creates a knowledge base folder inside the parent folder."
	)
	public KnowledgeBaseFolder createKnowledgeBaseFolderKnowledgeBaseFolder(
			@GraphQLName("parentKnowledgeBaseFolderId") Long
				parentKnowledgeBaseFolderId,
			@GraphQLName("knowledgeBaseFolder") KnowledgeBaseFolder
				knowledgeBaseFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.
					postKnowledgeBaseFolderKnowledgeBaseFolder(
						parentKnowledgeBaseFolderId, knowledgeBaseFolder));
	}

	@GraphQLField
	public Response createSiteKnowledgeBaseFoldersPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.
					postSiteKnowledgeBaseFoldersPageExportBatch(
						Long.valueOf(siteKey), callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField(description = "Creates a new knowledge base folder.")
	public KnowledgeBaseFolder createSiteKnowledgeBaseFolder(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("knowledgeBaseFolder") KnowledgeBaseFolder
				knowledgeBaseFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.postSiteKnowledgeBaseFolder(
					Long.valueOf(siteKey), knowledgeBaseFolder));
	}

	@GraphQLField
	public Response createSiteKnowledgeBaseFolderBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.postSiteKnowledgeBaseFolderBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the knowledge base folder by external reference code."
	)
	public boolean deleteSiteKnowledgeBaseFolderByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.
					deleteSiteKnowledgeBaseFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's knowledge base folder with the given external reference code, or creates it if it not exists."
	)
	public KnowledgeBaseFolder
			updateSiteKnowledgeBaseFolderByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("knowledgeBaseFolder") KnowledgeBaseFolder
					knowledgeBaseFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource ->
				knowledgeBaseFolderResource.
					putSiteKnowledgeBaseFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						knowledgeBaseFolder));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteKnowledgeBaseFolderPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			knowledgeBaseFolderResource -> {
				Page paginationPage =
					knowledgeBaseFolderResource.
						putSiteKnowledgeBaseFolderPermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createAssetLibraryLanguagesPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageResource ->
				languageResource.postAssetLibraryLanguagesPageExportBatch(
					Long.valueOf(assetLibraryId), callbackURL, contentType,
					fieldNames));
	}

	@GraphQLField
	public Response createSiteLanguagesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_languageResourceComponentServiceObjects,
			this::_populateResourceContext,
			languageResource ->
				languageResource.postSiteLanguagesPageExportBatch(
					Long.valueOf(siteKey), callbackURL, contentType,
					fieldNames));
	}

	@GraphQLField(
		description = "Deletes the message board attachment and returns a 204 if the operation succeeds."
	)
	public boolean deleteMessageBoardAttachment(
			@GraphQLName("messageBoardAttachmentId") Long
				messageBoardAttachmentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.deleteMessageBoardAttachment(
					messageBoardAttachmentId));

		return true;
	}

	@GraphQLField
	public Response deleteMessageBoardAttachmentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					deleteMessageBoardAttachmentBatch(callbackURL, object));
	}

	@GraphQLField
	public Response
			createMessageBoardMessageMessageBoardAttachmentsPageExportBatch(
				@GraphQLName("messageBoardMessageId") Long
					messageBoardMessageId,
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("contentType") String contentType,
				@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					postMessageBoardMessageMessageBoardAttachmentsPageExportBatch(
						messageBoardMessageId, callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField(
		description = "Creates an attachment for the message board message. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`MessageBoardAttachment`) with the metadata."
	)
	@GraphQLName(
		description = "Creates an attachment for the message board message. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`MessageBoardAttachment`) with the metadata.",
		value = "postMessageBoardMessageMessageBoardAttachmentMessageBoardMessageIdMultipartBody"
	)
	public MessageBoardAttachment
			createMessageBoardMessageMessageBoardAttachment(
				@GraphQLName("messageBoardMessageId") Long
					messageBoardMessageId,
				@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					postMessageBoardMessageMessageBoardAttachment(
						messageBoardMessageId, multipartBody));
	}

	@GraphQLField
	public Response createMessageBoardMessageMessageBoardAttachmentBatch(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					postMessageBoardMessageMessageBoardAttachmentBatch(
						messageBoardMessageId, multipartBody, callbackURL,
						object));
	}

	@GraphQLField
	public Response
			createMessageBoardThreadMessageBoardAttachmentsPageExportBatch(
				@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("contentType") String contentType,
				@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					postMessageBoardThreadMessageBoardAttachmentsPageExportBatch(
						messageBoardThreadId, callbackURL, contentType,
						fieldNames));
	}

	@GraphQLField(
		description = "Creates a new attachment for the message board thread. The request body should be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`knowledgeBaseAttachment`) with the metadata."
	)
	@GraphQLName(
		description = "Creates a new attachment for the message board thread. The request body should be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`knowledgeBaseAttachment`) with the metadata.",
		value = "postMessageBoardThreadMessageBoardAttachmentMessageBoardThreadIdMultipartBody"
	)
	public MessageBoardAttachment
			createMessageBoardThreadMessageBoardAttachment(
				@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
				@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					postMessageBoardThreadMessageBoardAttachment(
						messageBoardThreadId, multipartBody));
	}

	@GraphQLField
	public Response createMessageBoardThreadMessageBoardAttachmentBatch(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					postMessageBoardThreadMessageBoardAttachmentBatch(
						messageBoardThreadId, multipartBody, callbackURL,
						object));
	}

	@GraphQLField(
		description = "Delete the message board attachment by message board message's and message board attachment's external reference codes."
	)
	public boolean
			deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("messageBoardMessageExternalReferenceCode") String
					messageBoardMessageExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardAttachmentResource ->
				messageBoardAttachmentResource.
					deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode(
						Long.valueOf(siteKey),
						messageBoardMessageExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Deletes the message board message and returns a 204 if the operation succeeds."
	)
	public boolean deleteMessageBoardMessage(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.deleteMessageBoardMessage(
					messageBoardMessageId));

		return true;
	}

	@GraphQLField
	public Response deleteMessageBoardMessageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.deleteMessageBoardMessageBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public MessageBoardMessage patchMessageBoardMessage(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId,
			@GraphQLName("messageBoardMessage") MessageBoardMessage
				messageBoardMessage)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.patchMessageBoardMessage(
					messageBoardMessageId, messageBoardMessage));
	}

	@GraphQLField(
		description = "Replaces the message board message with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public MessageBoardMessage updateMessageBoardMessage(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId,
			@GraphQLName("messageBoardMessage") MessageBoardMessage
				messageBoardMessage)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.putMessageBoardMessage(
					messageBoardMessageId, messageBoardMessage));
	}

	@GraphQLField
	public Response updateMessageBoardMessageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.putMessageBoardMessageBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean updateMessageBoardMessageMarkAsAnswer(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.putMessageBoardMessageMarkAsAnswer(
					messageBoardMessageId));

		return true;
	}

	@GraphQLField(
		description = "Deletes the message board message's rating and returns a 204 if the operation succeeds."
	)
	public boolean deleteMessageBoardMessageMyRating(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.deleteMessageBoardMessageMyRating(
					messageBoardMessageId));

		return true;
	}

	@GraphQLField(
		description = "Creates a rating for the message board message."
	)
	public Rating createMessageBoardMessageMyRating(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.postMessageBoardMessageMyRating(
					messageBoardMessageId, rating));
	}

	@GraphQLField(
		description = "Replaces the rating with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public Rating updateMessageBoardMessageMyRating(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.putMessageBoardMessageMyRating(
					messageBoardMessageId, rating));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateMessageBoardMessagePermissionsPage(
				@GraphQLName("messageBoardMessageId") Long
					messageBoardMessageId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> {
				Page paginationPage =
					messageBoardMessageResource.
						putMessageBoardMessagePermissionsPage(
							messageBoardMessageId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateMessageBoardMessageSubscribe(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.putMessageBoardMessageSubscribe(
					messageBoardMessageId));

		return true;
	}

	@GraphQLField
	public boolean updateMessageBoardMessageUnmarkAsAnswer(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					putMessageBoardMessageUnmarkAsAnswer(
						messageBoardMessageId));

		return true;
	}

	@GraphQLField
	public boolean updateMessageBoardMessageUnsubscribe(
			@GraphQLName("messageBoardMessageId") Long messageBoardMessageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.putMessageBoardMessageUnsubscribe(
					messageBoardMessageId));

		return true;
	}

	@GraphQLField(
		description = "Creates a child message board message of the parent message."
	)
	public MessageBoardMessage createMessageBoardMessageMessageBoardMessage(
			@GraphQLName("parentMessageBoardMessageId") Long
				parentMessageBoardMessageId,
			@GraphQLName("messageBoardMessage") MessageBoardMessage
				messageBoardMessage)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					postMessageBoardMessageMessageBoardMessage(
						parentMessageBoardMessageId, messageBoardMessage));
	}

	@GraphQLField
	public Response createMessageBoardThreadMessageBoardMessagesPageExportBatch(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					postMessageBoardThreadMessageBoardMessagesPageExportBatch(
						messageBoardThreadId, search,
						_filterBiFunction.apply(
							messageBoardMessageResource, filterString),
						_sortsBiFunction.apply(
							messageBoardMessageResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new message in the message board thread."
	)
	public MessageBoardMessage createMessageBoardThreadMessageBoardMessage(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("messageBoardMessage") MessageBoardMessage
				messageBoardMessage)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					postMessageBoardThreadMessageBoardMessage(
						messageBoardThreadId, messageBoardMessage));
	}

	@GraphQLField
	public Response createMessageBoardThreadMessageBoardMessageBatch(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					postMessageBoardThreadMessageBoardMessageBatch(
						messageBoardThreadId, callbackURL, object));
	}

	@GraphQLField
	public Response createSiteMessageBoardMessagesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					postSiteMessageBoardMessagesPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							messageBoardMessageResource, filterString),
						_sortsBiFunction.apply(
							messageBoardMessageResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Deletes the site's message board message by external reference code."
	)
	public boolean deleteSiteMessageBoardMessageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					deleteSiteMessageBoardMessageByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's message board message with the given external reference code, or creates it if it not exists."
	)
	public MessageBoardMessage
			updateSiteMessageBoardMessageByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("messageBoardMessage") MessageBoardMessage
					messageBoardMessage)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource ->
				messageBoardMessageResource.
					putSiteMessageBoardMessageByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						messageBoardMessage));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteMessageBoardMessagePermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardMessageResource -> {
				Page paginationPage =
					messageBoardMessageResource.
						putSiteMessageBoardMessagePermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Deletes the message board section and returns a 204 if the operation succeeds."
	)
	public boolean deleteMessageBoardSection(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.deleteMessageBoardSection(
					messageBoardSectionId));

		return true;
	}

	@GraphQLField
	public Response deleteMessageBoardSectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.deleteMessageBoardSectionBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public MessageBoardSection patchMessageBoardSection(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("messageBoardSection") MessageBoardSection
				messageBoardSection)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.patchMessageBoardSection(
					messageBoardSectionId, messageBoardSection));
	}

	@GraphQLField(
		description = "Replaces the message board section with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public MessageBoardSection updateMessageBoardSection(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("messageBoardSection") MessageBoardSection
				messageBoardSection)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.putMessageBoardSection(
					messageBoardSectionId, messageBoardSection));
	}

	@GraphQLField
	public Response updateMessageBoardSectionBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.putMessageBoardSectionBatch(
					callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateMessageBoardSectionPermissionsPage(
				@GraphQLName("messageBoardSectionId") Long
					messageBoardSectionId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource -> {
				Page paginationPage =
					messageBoardSectionResource.
						putMessageBoardSectionPermissionsPage(
							messageBoardSectionId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateMessageBoardSectionSubscribe(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.putMessageBoardSectionSubscribe(
					messageBoardSectionId));

		return true;
	}

	@GraphQLField
	public boolean updateMessageBoardSectionUnsubscribe(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.putMessageBoardSectionUnsubscribe(
					messageBoardSectionId));

		return true;
	}

	@GraphQLField(
		description = "Creates a new message board section in the parent section."
	)
	public MessageBoardSection createMessageBoardSectionMessageBoardSection(
			@GraphQLName("parentMessageBoardSectionId") Long
				parentMessageBoardSectionId,
			@GraphQLName("messageBoardSection") MessageBoardSection
				messageBoardSection)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.
					postMessageBoardSectionMessageBoardSection(
						parentMessageBoardSectionId, messageBoardSection));
	}

	@GraphQLField
	public Response createSiteMessageBoardSectionsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.
					postSiteMessageBoardSectionsPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							messageBoardSectionResource, filterString),
						_sortsBiFunction.apply(
							messageBoardSectionResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new message board section.")
	public MessageBoardSection createSiteMessageBoardSection(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("messageBoardSection") MessageBoardSection
				messageBoardSection)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.postSiteMessageBoardSection(
					Long.valueOf(siteKey), messageBoardSection));
	}

	@GraphQLField
	public Response createSiteMessageBoardSectionBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource ->
				messageBoardSectionResource.postSiteMessageBoardSectionBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteMessageBoardSectionPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardSectionResource -> {
				Page paginationPage =
					messageBoardSectionResource.
						putSiteMessageBoardSectionPermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createMessageBoardSectionMessageBoardThreadsPageExportBatch(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.
					postMessageBoardSectionMessageBoardThreadsPageExportBatch(
						messageBoardSectionId, search,
						_filterBiFunction.apply(
							messageBoardThreadResource, filterString),
						_sortsBiFunction.apply(
							messageBoardThreadResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new message board thread inside a section."
	)
	public MessageBoardThread createMessageBoardSectionMessageBoardThread(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("messageBoardThread") MessageBoardThread
				messageBoardThread)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.
					postMessageBoardSectionMessageBoardThread(
						messageBoardSectionId, messageBoardThread));
	}

	@GraphQLField
	public Response createMessageBoardSectionMessageBoardThreadBatch(
			@GraphQLName("messageBoardSectionId") Long messageBoardSectionId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.
					postMessageBoardSectionMessageBoardThreadBatch(
						messageBoardSectionId, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the message board thread and returns a 204 if the operation succeeds."
	)
	public boolean deleteMessageBoardThread(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.deleteMessageBoardThread(
					messageBoardThreadId));

		return true;
	}

	@GraphQLField
	public Response deleteMessageBoardThreadBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.deleteMessageBoardThreadBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public MessageBoardThread patchMessageBoardThread(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("messageBoardThread") MessageBoardThread
				messageBoardThread)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.patchMessageBoardThread(
					messageBoardThreadId, messageBoardThread));
	}

	@GraphQLField(
		description = "Replaces the message board thread with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public MessageBoardThread updateMessageBoardThread(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("messageBoardThread") MessageBoardThread
				messageBoardThread)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.putMessageBoardThread(
					messageBoardThreadId, messageBoardThread));
	}

	@GraphQLField
	public Response updateMessageBoardThreadBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.putMessageBoardThreadBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the message board thread's rating and returns a 204 if the operation succeeds."
	)
	public boolean deleteMessageBoardThreadMyRating(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.deleteMessageBoardThreadMyRating(
					messageBoardThreadId));

		return true;
	}

	@GraphQLField(description = "Creates the message board thread's rating.")
	public Rating createMessageBoardThreadMyRating(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.postMessageBoardThreadMyRating(
					messageBoardThreadId, rating));
	}

	@GraphQLField(
		description = "Replaces the rating with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public Rating updateMessageBoardThreadMyRating(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.putMessageBoardThreadMyRating(
					messageBoardThreadId, rating));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateMessageBoardThreadPermissionsPage(
				@GraphQLName("messageBoardThreadId") Long messageBoardThreadId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource -> {
				Page paginationPage =
					messageBoardThreadResource.
						putMessageBoardThreadPermissionsPage(
							messageBoardThreadId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateMessageBoardThreadSubscribe(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.putMessageBoardThreadSubscribe(
					messageBoardThreadId));

		return true;
	}

	@GraphQLField
	public boolean updateMessageBoardThreadUnsubscribe(
			@GraphQLName("messageBoardThreadId") Long messageBoardThreadId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.putMessageBoardThreadUnsubscribe(
					messageBoardThreadId));

		return true;
	}

	@GraphQLField
	public Response createSiteMessageBoardThreadsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.
					postSiteMessageBoardThreadsPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							messageBoardThreadResource, filterString),
						_sortsBiFunction.apply(
							messageBoardThreadResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new message board thread.")
	public MessageBoardThread createSiteMessageBoardThread(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("messageBoardThread") MessageBoardThread
				messageBoardThread)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.postSiteMessageBoardThread(
					Long.valueOf(siteKey), messageBoardThread));
	}

	@GraphQLField
	public Response createSiteMessageBoardThreadBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource ->
				messageBoardThreadResource.postSiteMessageBoardThreadBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteMessageBoardThreadPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects,
			this::_populateResourceContext,
			messageBoardThreadResource -> {
				Page paginationPage =
					messageBoardThreadResource.
						putSiteMessageBoardThreadPermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Deletes the navigation menu and returns a 204 if the operation succeeds"
	)
	public boolean deleteNavigationMenu(
			@GraphQLName("navigationMenuId") Long navigationMenuId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource ->
				navigationMenuResource.deleteNavigationMenu(navigationMenuId));

		return true;
	}

	@GraphQLField
	public Response deleteNavigationMenuBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource ->
				navigationMenuResource.deleteNavigationMenuBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the navigation menu with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public NavigationMenu updateNavigationMenu(
			@GraphQLName("navigationMenuId") Long navigationMenuId,
			@GraphQLName("navigationMenu") NavigationMenu navigationMenu)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource -> navigationMenuResource.putNavigationMenu(
				navigationMenuId, navigationMenu));
	}

	@GraphQLField
	public Response updateNavigationMenuBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource ->
				navigationMenuResource.putNavigationMenuBatch(
					callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateNavigationMenuPermissionsPage(
				@GraphQLName("navigationMenuId") Long navigationMenuId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource -> {
				Page paginationPage =
					navigationMenuResource.putNavigationMenuPermissionsPage(
						navigationMenuId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createSiteNavigationMenusPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource ->
				navigationMenuResource.postSiteNavigationMenusPageExportBatch(
					Long.valueOf(siteKey), callbackURL, contentType,
					fieldNames));
	}

	@GraphQLField(description = "Creates a new navigation menu.")
	public NavigationMenu createSiteNavigationMenu(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("navigationMenu") NavigationMenu navigationMenu)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource ->
				navigationMenuResource.postSiteNavigationMenu(
					Long.valueOf(siteKey), navigationMenu));
	}

	@GraphQLField
	public Response createSiteNavigationMenuBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource ->
				navigationMenuResource.postSiteNavigationMenuBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteNavigationMenuPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects,
			this::_populateResourceContext,
			navigationMenuResource -> {
				Page paginationPage =
					navigationMenuResource.putSiteNavigationMenuPermissionsPage(
						Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createSiteSitePagesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource ->
				sitePageResource.postSiteSitePagesPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(sitePageResource, filterString),
					_sortsBiFunction.apply(sitePageResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Adds a new site page")
	public SitePage createSiteSitePage(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("sitePage") SitePage sitePage)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource -> sitePageResource.postSiteSitePage(
				Long.valueOf(siteKey), sitePage));
	}

	@GraphQLField
	public Response createSiteSitePageBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_sitePageResourceComponentServiceObjects,
			this::_populateResourceContext,
			sitePageResource -> sitePageResource.postSiteSitePageBatch(
				Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public Response createAssetLibraryStructuredContentsPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					postAssetLibraryStructuredContentsPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							structuredContentResource, filterString),
						_sortsBiFunction.apply(
							structuredContentResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public StructuredContent createAssetLibraryStructuredContent(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("structuredContent") StructuredContent
				structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.postAssetLibraryStructuredContent(
					Long.valueOf(assetLibraryId), structuredContent));
	}

	@GraphQLField
	public Response createAssetLibraryStructuredContentBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					postAssetLibraryStructuredContentBatch(
						Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the asset library's structured content by external reference code."
	)
	public boolean deleteAssetLibraryStructuredContentByExternalReferenceCode(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					deleteAssetLibraryStructuredContentByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the asset library's structured content with the given external reference code, or creates it if it not exists."
	)
	public StructuredContent
			updateAssetLibraryStructuredContentByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("structuredContent") StructuredContent
					structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					putAssetLibraryStructuredContentByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode,
						structuredContent));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryStructuredContentPermissionsPage(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> {
				Page paginationPage =
					structuredContentResource.
						putAssetLibraryStructuredContentPermissionsPage(
							Long.valueOf(assetLibraryId), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createContentStructureStructuredContentsPageExportBatch(
			@GraphQLName("contentStructureId") Long contentStructureId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					postContentStructureStructuredContentsPageExportBatch(
						contentStructureId, search,
						_filterBiFunction.apply(
							structuredContentResource, filterString),
						_sortsBiFunction.apply(
							structuredContentResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createSiteStructuredContentsPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					postSiteStructuredContentsPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							structuredContentResource, filterString),
						_sortsBiFunction.apply(
							structuredContentResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new structured content.")
	public StructuredContent createSiteStructuredContent(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("structuredContent") StructuredContent
				structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.postSiteStructuredContent(
					Long.valueOf(siteKey), structuredContent));
	}

	@GraphQLField
	public Response createSiteStructuredContentBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.postSiteStructuredContentBatch(
					Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's structured content by external reference code."
	)
	public boolean deleteSiteStructuredContentByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					deleteSiteStructuredContentByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's structured content with the given external reference code, or creates it if it not exists."
	)
	public StructuredContent updateSiteStructuredContentByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("structuredContent") StructuredContent
				structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					putSiteStructuredContentByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						structuredContent));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteStructuredContentPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> {
				Page paginationPage =
					structuredContentResource.
						putSiteStructuredContentPermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response
			createStructuredContentFolderStructuredContentsPageExportBatch(
				@GraphQLName("structuredContentFolderId") Long
					structuredContentFolderId,
				@GraphQLName("search") String search,
				@GraphQLName("filter") String filterString,
				@GraphQLName("sort") String sortsString,
				@GraphQLName("callbackURL") String callbackURL,
				@GraphQLName("contentType") String contentType,
				@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					postStructuredContentFolderStructuredContentsPageExportBatch(
						structuredContentFolderId, search,
						_filterBiFunction.apply(
							structuredContentResource, filterString),
						_sortsBiFunction.apply(
							structuredContentResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates a new structured content in the folder."
	)
	public StructuredContent createStructuredContentFolderStructuredContent(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId,
			@GraphQLName("structuredContent") StructuredContent
				structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					postStructuredContentFolderStructuredContent(
						structuredContentFolderId, structuredContent));
	}

	@GraphQLField
	public Response createStructuredContentFolderStructuredContentBatch(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.
					postStructuredContentFolderStructuredContentBatch(
						structuredContentFolderId, callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the structured content and returns a 204 if the operation succeeds."
	)
	public boolean deleteStructuredContent(
			@GraphQLName("structuredContentId") Long structuredContentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.deleteStructuredContent(
					structuredContentId));

		return true;
	}

	@GraphQLField
	public Response deleteStructuredContentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.deleteStructuredContentBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public StructuredContent patchStructuredContent(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("structuredContent") StructuredContent
				structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.patchStructuredContent(
					structuredContentId, structuredContent));
	}

	@GraphQLField(
		description = "Replaces the structured content with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public StructuredContent updateStructuredContent(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("structuredContent") StructuredContent
				structuredContent)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.putStructuredContent(
					structuredContentId, structuredContent));
	}

	@GraphQLField
	public Response updateStructuredContentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.putStructuredContentBatch(
					callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the structured content's rating and returns a 204 if the operation succeeds."
	)
	public boolean deleteStructuredContentMyRating(
			@GraphQLName("structuredContentId") Long structuredContentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.deleteStructuredContentMyRating(
					structuredContentId));

		return true;
	}

	@GraphQLField(description = "Create a rating for the structured content.")
	public Rating createStructuredContentMyRating(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.postStructuredContentMyRating(
					structuredContentId, rating));
	}

	@GraphQLField(
		description = "Replaces the rating with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public Rating updateStructuredContentMyRating(
			@GraphQLName("structuredContentId") Long structuredContentId,
			@GraphQLName("rating") Rating rating)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.putStructuredContentMyRating(
					structuredContentId, rating));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateStructuredContentPermissionsPage(
				@GraphQLName("structuredContentId") Long structuredContentId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource -> {
				Page paginationPage =
					structuredContentResource.
						putStructuredContentPermissionsPage(
							structuredContentId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateStructuredContentSubscribe(
			@GraphQLName("structuredContentId") Long structuredContentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.putStructuredContentSubscribe(
					structuredContentId));

		return true;
	}

	@GraphQLField
	public boolean updateStructuredContentUnsubscribe(
			@GraphQLName("structuredContentId") Long structuredContentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentResource ->
				structuredContentResource.putStructuredContentUnsubscribe(
					structuredContentId));

		return true;
	}

	@GraphQLField
	public Response createAssetLibraryStructuredContentFoldersPageExportBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					postAssetLibraryStructuredContentFoldersPageExportBatch(
						Long.valueOf(assetLibraryId), search,
						_filterBiFunction.apply(
							structuredContentFolderResource, filterString),
						_sortsBiFunction.apply(
							structuredContentFolderResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public StructuredContentFolder createAssetLibraryStructuredContentFolder(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("structuredContentFolder") StructuredContentFolder
				structuredContentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					postAssetLibraryStructuredContentFolder(
						Long.valueOf(assetLibraryId), structuredContentFolder));
	}

	@GraphQLField
	public Response createAssetLibraryStructuredContentFolderBatch(
			@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					postAssetLibraryStructuredContentFolderBatch(
						Long.valueOf(assetLibraryId), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the asset library's structured content folder by external reference code."
	)
	public boolean
			deleteAssetLibraryStructuredContentFolderByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					deleteAssetLibraryStructuredContentFolderByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the asset library's structured content folder with the given external reference code, or creates it if it not exists."
	)
	public StructuredContentFolder
			updateAssetLibraryStructuredContentFolderByExternalReferenceCode(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("structuredContentFolder") StructuredContentFolder
					structuredContentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					putAssetLibraryStructuredContentFolderByExternalReferenceCode(
						Long.valueOf(assetLibraryId), externalReferenceCode,
						structuredContentFolder));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateAssetLibraryStructuredContentFolderPermissionsPage(
				@GraphQLName("assetLibraryId") @NotEmpty String assetLibraryId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> {
				Page paginationPage =
					structuredContentFolderResource.
						putAssetLibraryStructuredContentFolderPermissionsPage(
							Long.valueOf(assetLibraryId), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createSiteStructuredContentFoldersPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					postSiteStructuredContentFoldersPageExportBatch(
						Long.valueOf(siteKey), search,
						_filterBiFunction.apply(
							structuredContentFolderResource, filterString),
						_sortsBiFunction.apply(
							structuredContentFolderResource, sortsString),
						callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new structured content folder.")
	public StructuredContentFolder createSiteStructuredContentFolder(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("structuredContentFolder") StructuredContentFolder
				structuredContentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.postSiteStructuredContentFolder(
					Long.valueOf(siteKey), structuredContentFolder));
	}

	@GraphQLField
	public Response createSiteStructuredContentFolderBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					postSiteStructuredContentFolderBatch(
						Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField
	public boolean deleteSiteStructuredContentFolderByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					deleteSiteStructuredContentFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField
	public StructuredContentFolder
			updateSiteStructuredContentFolderByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("structuredContentFolder") StructuredContentFolder
					structuredContentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					putSiteStructuredContentFolderByExternalReferenceCode(
						Long.valueOf(siteKey), externalReferenceCode,
						structuredContentFolder));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteStructuredContentFolderPermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> {
				Page paginationPage =
					structuredContentFolderResource.
						putSiteStructuredContentFolderPermissionsPage(
							Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateStructuredContentFolderPermissionsPage(
				@GraphQLName("structuredContentFolderId") Long
					structuredContentFolderId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource -> {
				Page paginationPage =
					structuredContentFolderResource.
						putStructuredContentFolderPermissionsPage(
							structuredContentFolderId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Creates a new structured content folder in an existing folder."
	)
	public StructuredContentFolder
			createStructuredContentFolderStructuredContentFolder(
				@GraphQLName("parentStructuredContentFolderId") Long
					parentStructuredContentFolderId,
				@GraphQLName("structuredContentFolder") StructuredContentFolder
					structuredContentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					postStructuredContentFolderStructuredContentFolder(
						parentStructuredContentFolderId,
						structuredContentFolder));
	}

	@GraphQLField(
		description = "Deletes the structured content folder and returns a 204 if the operation succeeds."
	)
	public boolean deleteStructuredContentFolder(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.deleteStructuredContentFolder(
					structuredContentFolderId));

		return true;
	}

	@GraphQLField
	public Response deleteStructuredContentFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					deleteStructuredContentFolderBatch(callbackURL, object));
	}

	@GraphQLField(
		description = "Updates only the fields received in the request body, leaving any other fields untouched."
	)
	public StructuredContentFolder patchStructuredContentFolder(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId,
			@GraphQLName("structuredContentFolder") StructuredContentFolder
				structuredContentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.patchStructuredContentFolder(
					structuredContentFolderId, structuredContentFolder));
	}

	@GraphQLField(
		description = "Replaces the structured content folder with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public StructuredContentFolder updateStructuredContentFolder(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId,
			@GraphQLName("structuredContentFolder") StructuredContentFolder
				structuredContentFolder)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.putStructuredContentFolder(
					structuredContentFolderId, structuredContentFolder));
	}

	@GraphQLField
	public Response updateStructuredContentFolderBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.putStructuredContentFolderBatch(
					callbackURL, object));
	}

	@GraphQLField
	public boolean updateStructuredContentFolderSubscribe(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					putStructuredContentFolderSubscribe(
						structuredContentFolderId));

		return true;
	}

	@GraphQLField
	public boolean updateStructuredContentFolderUnsubscribe(
			@GraphQLName("structuredContentFolderId") Long
				structuredContentFolderId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects,
			this::_populateResourceContext,
			structuredContentFolderResource ->
				structuredContentFolderResource.
					putStructuredContentFolderUnsubscribe(
						structuredContentFolderId));

		return true;
	}

	@GraphQLField
	public Response createSiteWikiNodesPageExportBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource ->
				wikiNodeResource.postSiteWikiNodesPageExportBatch(
					Long.valueOf(siteKey), search,
					_filterBiFunction.apply(wikiNodeResource, filterString),
					_sortsBiFunction.apply(wikiNodeResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new wiki node")
	public WikiNode createSiteWikiNode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("wikiNode") WikiNode wikiNode)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.postSiteWikiNode(
				Long.valueOf(siteKey), wikiNode));
	}

	@GraphQLField
	public Response createSiteWikiNodeBatch(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.postSiteWikiNodeBatch(
				Long.valueOf(siteKey), callbackURL, object));
	}

	@GraphQLField(
		description = "Deletes the site's wiki node by external reference code."
	)
	public boolean deleteSiteWikiNodeByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource ->
				wikiNodeResource.deleteSiteWikiNodeByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the site's wiki node with the given external reference code, or creates it if it not exists."
	)
	public WikiNode updateSiteWikiNodeByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("wikiNode") WikiNode wikiNode)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource ->
				wikiNodeResource.putSiteWikiNodeByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode, wikiNode));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateSiteWikiNodePermissionsPage(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> {
				Page paginationPage =
					wikiNodeResource.putSiteWikiNodePermissionsPage(
						Long.valueOf(siteKey), permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField(
		description = "Deletes the wiki node and returns a 204 if the operation succeeds."
	)
	public boolean deleteWikiNode(@GraphQLName("wikiNodeId") Long wikiNodeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.deleteWikiNode(wikiNodeId));

		return true;
	}

	@GraphQLField
	public Response deleteWikiNodeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.deleteWikiNodeBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the wiki node with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public WikiNode updateWikiNode(
			@GraphQLName("wikiNodeId") Long wikiNodeId,
			@GraphQLName("wikiNode") WikiNode wikiNode)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.putWikiNode(
				wikiNodeId, wikiNode));
	}

	@GraphQLField
	public Response updateWikiNodeBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.putWikiNodeBatch(
				callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateWikiNodePermissionsPage(
				@GraphQLName("wikiNodeId") Long wikiNodeId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> {
				Page paginationPage =
					wikiNodeResource.putWikiNodePermissionsPage(
						wikiNodeId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateWikiNodeSubscribe(
			@GraphQLName("wikiNodeId") Long wikiNodeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.putWikiNodeSubscribe(
				wikiNodeId));

		return true;
	}

	@GraphQLField
	public boolean updateWikiNodeUnsubscribe(
			@GraphQLName("wikiNodeId") Long wikiNodeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiNodeResource -> wikiNodeResource.putWikiNodeUnsubscribe(
				wikiNodeId));

		return true;
	}

	@GraphQLField(
		description = "Deletes the wiki page by external reference code."
	)
	public boolean deleteSiteWikiPageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource ->
				wikiPageResource.deleteSiteWikiPageByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Updates the wiki page with the given external reference code, or creates it if it not exists."
	)
	public WikiPage updateSiteWikiPageByExternalReferenceCode(
			@GraphQLName("siteKey") @NotEmpty String siteKey,
			@GraphQLName("externalReferenceCode") String externalReferenceCode,
			@GraphQLName("wikiPage") WikiPage wikiPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource ->
				wikiPageResource.putSiteWikiPageByExternalReferenceCode(
					Long.valueOf(siteKey), externalReferenceCode, wikiPage));
	}

	@GraphQLField
	public Response createWikiNodeWikiPagesPageExportBatch(
			@GraphQLName("wikiNodeId") Long wikiNodeId,
			@GraphQLName("search") String search,
			@GraphQLName("filter") String filterString,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource ->
				wikiPageResource.postWikiNodeWikiPagesPageExportBatch(
					wikiNodeId, search,
					_filterBiFunction.apply(wikiPageResource, filterString),
					_sortsBiFunction.apply(wikiPageResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField(description = "Creates a new wiki page")
	public WikiPage createWikiNodeWikiPage(
			@GraphQLName("wikiNodeId") Long wikiNodeId,
			@GraphQLName("wikiPage") WikiPage wikiPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.postWikiNodeWikiPage(
				wikiNodeId, wikiPage));
	}

	@GraphQLField
	public Response createWikiNodeWikiPageBatch(
			@GraphQLName("wikiNodeId") Long wikiNodeId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.postWikiNodeWikiPageBatch(
				wikiNodeId, callbackURL, object));
	}

	@GraphQLField(
		description = "Creates a child wiki page of the parent wiki page."
	)
	public WikiPage createWikiPageWikiPage(
			@GraphQLName("parentWikiPageId") Long parentWikiPageId,
			@GraphQLName("wikiPage") WikiPage wikiPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.postWikiPageWikiPage(
				parentWikiPageId, wikiPage));
	}

	@GraphQLField(
		description = "Deletes the wiki page and returns a 204 if the operation succeeds."
	)
	public boolean deleteWikiPage(@GraphQLName("wikiPageId") Long wikiPageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.deleteWikiPage(wikiPageId));

		return true;
	}

	@GraphQLField
	public Response deleteWikiPageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.deleteWikiPageBatch(
				callbackURL, object));
	}

	@GraphQLField(
		description = "Replaces the wiki page with the information sent in the request body. Any missing fields are deleted, unless they are required."
	)
	public WikiPage updateWikiPage(
			@GraphQLName("wikiPageId") Long wikiPageId,
			@GraphQLName("wikiPage") WikiPage wikiPage)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.putWikiPage(
				wikiPageId, wikiPage));
	}

	@GraphQLField
	public Response updateWikiPageBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.putWikiPageBatch(
				callbackURL, object));
	}

	@GraphQLField
	public java.util.Collection<com.liferay.portal.vulcan.permission.Permission>
			updateWikiPagePermissionsPage(
				@GraphQLName("wikiPageId") Long wikiPageId,
				@GraphQLName("permissions")
					com.liferay.portal.vulcan.permission.Permission[]
						permissions)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> {
				Page paginationPage =
					wikiPageResource.putWikiPagePermissionsPage(
						wikiPageId, permissions);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public boolean updateWikiPageSubscribe(
			@GraphQLName("wikiPageId") Long wikiPageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.putWikiPageSubscribe(
				wikiPageId));

		return true;
	}

	@GraphQLField
	public boolean updateWikiPageUnsubscribe(
			@GraphQLName("wikiPageId") Long wikiPageId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageResource -> wikiPageResource.putWikiPageUnsubscribe(
				wikiPageId));

		return true;
	}

	@GraphQLField(
		description = "Delete the wiki page attachment by wiki page's and wiki page attachment's external reference codes."
	)
	public boolean
			deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(
				@GraphQLName("siteKey") @NotEmpty String siteKey,
				@GraphQLName("wikiPageExternalReferenceCode") String
					wikiPageExternalReferenceCode,
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.
					deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode(
						Long.valueOf(siteKey), wikiPageExternalReferenceCode,
						externalReferenceCode));

		return true;
	}

	@GraphQLField(
		description = "Deletes the wiki page attachment and returns a 204 if the operation succeeds."
	)
	public boolean deleteWikiPageAttachment(
			@GraphQLName("wikiPageAttachmentId") Long wikiPageAttachmentId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.deleteWikiPageAttachment(
					wikiPageAttachmentId));

		return true;
	}

	@GraphQLField
	public Response deleteWikiPageAttachmentBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.deleteWikiPageAttachmentBatch(
					callbackURL, object));
	}

	@GraphQLField
	public Response createWikiPageWikiPageAttachmentsPageExportBatch(
			@GraphQLName("wikiPageId") Long wikiPageId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.
					postWikiPageWikiPageAttachmentsPageExportBatch(
						wikiPageId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField(
		description = "Creates an attachment for the wiki page. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`WikiPageAttachment`) with the metadata."
	)
	@GraphQLName(
		description = "Creates an attachment for the wiki page. The request body must be `multipart/form-data` with two parts, the file's bytes (`file`), and an optional JSON string (`WikiPageAttachment`) with the metadata.",
		value = "postWikiPageWikiPageAttachmentWikiPageIdMultipartBody"
	)
	public WikiPageAttachment createWikiPageWikiPageAttachment(
			@GraphQLName("wikiPageId") Long wikiPageId,
			@GraphQLName("multipartBody") MultipartBody multipartBody)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.postWikiPageWikiPageAttachment(
					wikiPageId, multipartBody));
	}

	@GraphQLField
	public Response createWikiPageWikiPageAttachmentBatch(
			@GraphQLName("wikiPageId") Long wikiPageId,
			@GraphQLName("multipartBody") MultipartBody multipartBody,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects,
			this::_populateResourceContext,
			wikiPageAttachmentResource ->
				wikiPageAttachmentResource.postWikiPageWikiPageAttachmentBatch(
					wikiPageId, multipartBody, callbackURL, object));
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

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
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

		blogPostingResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		blogPostingResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		blogPostingImageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		blogPostingImageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		commentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		commentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		contentElementResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		contentElementResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		contentStructureResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		contentStructureResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		contentTemplateResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		contentTemplateResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		documentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		documentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		documentDataDefinitionTypeResource.
			setVulcanBatchEngineExportTaskResource(
				_vulcanBatchEngineExportTaskResource);

		documentDataDefinitionTypeResource.
			setVulcanBatchEngineImportTaskResource(
				_vulcanBatchEngineImportTaskResource);
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

		documentFolderResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		documentFolderResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		documentMetadataSetResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		documentMetadataSetResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		documentShortcutResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		documentShortcutResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		knowledgeBaseArticleResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		knowledgeBaseArticleResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		knowledgeBaseAttachmentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		knowledgeBaseAttachmentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		knowledgeBaseFolderResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		knowledgeBaseFolderResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		languageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		languageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		messageBoardAttachmentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		messageBoardAttachmentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		messageBoardMessageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		messageBoardMessageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		messageBoardSectionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		messageBoardSectionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		messageBoardThreadResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		messageBoardThreadResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		navigationMenuResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		navigationMenuResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		sitePageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		sitePageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		structuredContentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		structuredContentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		structuredContentFolderResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		structuredContentFolderResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		wikiNodeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		wikiNodeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		wikiPageResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		wikiPageResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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

		wikiPageAttachmentResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		wikiPageAttachmentResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<BlogPostingResource>
		_blogPostingResourceComponentServiceObjects;
	private static ComponentServiceObjects<BlogPostingImageResource>
		_blogPostingImageResourceComponentServiceObjects;
	private static ComponentServiceObjects<CommentResource>
		_commentResourceComponentServiceObjects;
	private static ComponentServiceObjects<ContentElementResource>
		_contentElementResourceComponentServiceObjects;
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
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction<Object, String, Filter> _filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, Sort[]> _sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}