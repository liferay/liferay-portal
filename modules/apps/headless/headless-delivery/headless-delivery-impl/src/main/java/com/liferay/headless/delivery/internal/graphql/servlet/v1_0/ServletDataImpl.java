/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.graphql.servlet.v1_0;

import com.liferay.headless.delivery.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.delivery.internal.graphql.query.v1_0.Query;
import com.liferay.headless.delivery.internal.resource.v1_0.BlogPostingImageResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.BlogPostingResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.CommentResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.ContentElementResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.ContentSetElementResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.ContentStructureResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.ContentTemplateResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.DocumentDataDefinitionTypeResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.DocumentFolderResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.DocumentMetadataSetResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.DocumentResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.DocumentShortcutResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.KnowledgeBaseArticleResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.KnowledgeBaseAttachmentResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.KnowledgeBaseFolderResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.LanguageResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.MessageBoardAttachmentResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.MessageBoardMessageResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.MessageBoardSectionResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.MessageBoardThreadResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.NavigationMenuResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.SitePageResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.StructuredContentFolderResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.StructuredContentResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.WikiNodeResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.WikiPageAttachmentResourceImpl;
import com.liferay.headless.delivery.internal.resource.v1_0.WikiPageResourceImpl;
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
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setBlogPostingResourceComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects);
		Mutation.setBlogPostingImageResourceComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects);
		Mutation.setCommentResourceComponentServiceObjects(
			_commentResourceComponentServiceObjects);
		Mutation.setContentElementResourceComponentServiceObjects(
			_contentElementResourceComponentServiceObjects);
		Mutation.setContentStructureResourceComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects);
		Mutation.setContentTemplateResourceComponentServiceObjects(
			_contentTemplateResourceComponentServiceObjects);
		Mutation.setDocumentResourceComponentServiceObjects(
			_documentResourceComponentServiceObjects);
		Mutation.setDocumentDataDefinitionTypeResourceComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects);
		Mutation.setDocumentFolderResourceComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects);
		Mutation.setDocumentMetadataSetResourceComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects);
		Mutation.setDocumentShortcutResourceComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects);
		Mutation.setKnowledgeBaseArticleResourceComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects);
		Mutation.setKnowledgeBaseAttachmentResourceComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects);
		Mutation.setKnowledgeBaseFolderResourceComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects);
		Mutation.setLanguageResourceComponentServiceObjects(
			_languageResourceComponentServiceObjects);
		Mutation.setMessageBoardAttachmentResourceComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects);
		Mutation.setMessageBoardMessageResourceComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects);
		Mutation.setMessageBoardSectionResourceComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects);
		Mutation.setMessageBoardThreadResourceComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects);
		Mutation.setNavigationMenuResourceComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects);
		Mutation.setSitePageResourceComponentServiceObjects(
			_sitePageResourceComponentServiceObjects);
		Mutation.setStructuredContentResourceComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects);
		Mutation.setStructuredContentFolderResourceComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects);
		Mutation.setWikiNodeResourceComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects);
		Mutation.setWikiPageResourceComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects);
		Mutation.setWikiPageAttachmentResourceComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects);

		Query.setBlogPostingResourceComponentServiceObjects(
			_blogPostingResourceComponentServiceObjects);
		Query.setBlogPostingImageResourceComponentServiceObjects(
			_blogPostingImageResourceComponentServiceObjects);
		Query.setCommentResourceComponentServiceObjects(
			_commentResourceComponentServiceObjects);
		Query.setContentElementResourceComponentServiceObjects(
			_contentElementResourceComponentServiceObjects);
		Query.setContentSetElementResourceComponentServiceObjects(
			_contentSetElementResourceComponentServiceObjects);
		Query.setContentStructureResourceComponentServiceObjects(
			_contentStructureResourceComponentServiceObjects);
		Query.setContentTemplateResourceComponentServiceObjects(
			_contentTemplateResourceComponentServiceObjects);
		Query.setDocumentResourceComponentServiceObjects(
			_documentResourceComponentServiceObjects);
		Query.setDocumentDataDefinitionTypeResourceComponentServiceObjects(
			_documentDataDefinitionTypeResourceComponentServiceObjects);
		Query.setDocumentFolderResourceComponentServiceObjects(
			_documentFolderResourceComponentServiceObjects);
		Query.setDocumentMetadataSetResourceComponentServiceObjects(
			_documentMetadataSetResourceComponentServiceObjects);
		Query.setDocumentShortcutResourceComponentServiceObjects(
			_documentShortcutResourceComponentServiceObjects);
		Query.setKnowledgeBaseArticleResourceComponentServiceObjects(
			_knowledgeBaseArticleResourceComponentServiceObjects);
		Query.setKnowledgeBaseAttachmentResourceComponentServiceObjects(
			_knowledgeBaseAttachmentResourceComponentServiceObjects);
		Query.setKnowledgeBaseFolderResourceComponentServiceObjects(
			_knowledgeBaseFolderResourceComponentServiceObjects);
		Query.setLanguageResourceComponentServiceObjects(
			_languageResourceComponentServiceObjects);
		Query.setMessageBoardAttachmentResourceComponentServiceObjects(
			_messageBoardAttachmentResourceComponentServiceObjects);
		Query.setMessageBoardMessageResourceComponentServiceObjects(
			_messageBoardMessageResourceComponentServiceObjects);
		Query.setMessageBoardSectionResourceComponentServiceObjects(
			_messageBoardSectionResourceComponentServiceObjects);
		Query.setMessageBoardThreadResourceComponentServiceObjects(
			_messageBoardThreadResourceComponentServiceObjects);
		Query.setNavigationMenuResourceComponentServiceObjects(
			_navigationMenuResourceComponentServiceObjects);
		Query.setSitePageResourceComponentServiceObjects(
			_sitePageResourceComponentServiceObjects);
		Query.setStructuredContentResourceComponentServiceObjects(
			_structuredContentResourceComponentServiceObjects);
		Query.setStructuredContentFolderResourceComponentServiceObjects(
			_structuredContentFolderResourceComponentServiceObjects);
		Query.setWikiNodeResourceComponentServiceObjects(
			_wikiNodeResourceComponentServiceObjects);
		Query.setWikiPageResourceComponentServiceObjects(
			_wikiPageResourceComponentServiceObjects);
		Query.setWikiPageAttachmentResourceComponentServiceObjects(
			_wikiPageAttachmentResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Delivery";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-delivery-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#deleteBlogPosting",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"deleteBlogPosting"));
					put(
						"mutation#deleteBlogPostingBatch",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"deleteBlogPostingBatch"));
					put(
						"mutation#patchBlogPosting",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class, "patchBlogPosting"));
					put(
						"mutation#updateBlogPosting",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class, "putBlogPosting"));
					put(
						"mutation#updateBlogPostingBatch",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"putBlogPostingBatch"));
					put(
						"mutation#deleteBlogPostingMyRating",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"deleteBlogPostingMyRating"));
					put(
						"mutation#createBlogPostingMyRating",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"postBlogPostingMyRating"));
					put(
						"mutation#updateBlogPostingMyRating",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"putBlogPostingMyRating"));
					put(
						"mutation#updateBlogPostingPermissionsPage",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"putBlogPostingPermissionsPage"));
					put(
						"mutation#createSiteBlogPostingsPageExportBatch",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"postSiteBlogPostingsPageExportBatch"));
					put(
						"mutation#createSiteBlogPosting",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"postSiteBlogPosting"));
					put(
						"mutation#createSiteBlogPostingBatch",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"postSiteBlogPostingBatch"));
					put(
						"mutation#deleteSiteBlogPostingByExternalReferenceCode",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"deleteSiteBlogPostingByExternalReferenceCode"));
					put(
						"mutation#updateSiteBlogPostingByExternalReferenceCode",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"putSiteBlogPostingByExternalReferenceCode"));
					put(
						"mutation#updateSiteBlogPostingPermissionsPage",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"putSiteBlogPostingPermissionsPage"));
					put(
						"mutation#updateSiteBlogPostingSubscribe",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"putSiteBlogPostingSubscribe"));
					put(
						"mutation#updateSiteBlogPostingUnsubscribe",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"putSiteBlogPostingUnsubscribe"));
					put(
						"mutation#deleteBlogPostingImage",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"deleteBlogPostingImage"));
					put(
						"mutation#deleteBlogPostingImageBatch",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"deleteBlogPostingImageBatch"));
					put(
						"mutation#createSiteBlogPostingImagesPageExportBatch",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"postSiteBlogPostingImagesPageExportBatch"));
					put(
						"mutation#createSiteBlogPostingImage",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"postSiteBlogPostingImage"));
					put(
						"mutation#createSiteBlogPostingImageBatch",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"postSiteBlogPostingImageBatch"));
					put(
						"mutation#deleteSiteBlogPostingImageByExternalReferenceCode",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"deleteSiteBlogPostingImageByExternalReferenceCode"));
					put(
						"mutation#createBlogPostingCommentsPageExportBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postBlogPostingCommentsPageExportBatch"));
					put(
						"mutation#createBlogPostingComment",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postBlogPostingComment"));
					put(
						"mutation#createBlogPostingCommentBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postBlogPostingCommentBatch"));
					put(
						"mutation#deleteComment",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "deleteComment"));
					put(
						"mutation#deleteCommentBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "deleteCommentBatch"));
					put(
						"mutation#updateComment",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "putComment"));
					put(
						"mutation#updateCommentBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "putCommentBatch"));
					put(
						"mutation#createCommentComment",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "postCommentComment"));
					put(
						"mutation#createDocumentCommentsPageExportBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postDocumentCommentsPageExportBatch"));
					put(
						"mutation#createDocumentComment",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "postDocumentComment"));
					put(
						"mutation#createDocumentCommentBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postDocumentCommentBatch"));
					put(
						"mutation#deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"deleteSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#updateSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"putSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"deleteSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#updateSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"putSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"deleteSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#updateSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"putSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"deleteSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#updateSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"putSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"mutation#createStructuredContentCommentsPageExportBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postStructuredContentCommentsPageExportBatch"));
					put(
						"mutation#createStructuredContentComment",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postStructuredContentComment"));
					put(
						"mutation#createStructuredContentCommentBatch",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"postStructuredContentCommentBatch"));
					put(
						"mutation#createAssetLibraryContentElementsPageExportBatch",
						new ObjectValuePair<>(
							ContentElementResourceImpl.class,
							"postAssetLibraryContentElementsPageExportBatch"));
					put(
						"mutation#createSiteContentElementsPageExportBatch",
						new ObjectValuePair<>(
							ContentElementResourceImpl.class,
							"postSiteContentElementsPageExportBatch"));
					put(
						"mutation#createAssetLibraryContentStructuresPageExportBatch",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"postAssetLibraryContentStructuresPageExportBatch"));
					put(
						"mutation#updateAssetLibraryContentStructurePermissionsPage",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"putAssetLibraryContentStructurePermissionsPage"));
					put(
						"mutation#updateContentStructurePermissionsPage",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"putContentStructurePermissionsPage"));
					put(
						"mutation#createSiteContentStructuresPageExportBatch",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"postSiteContentStructuresPageExportBatch"));
					put(
						"mutation#updateSiteContentStructurePermissionsPage",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"putSiteContentStructurePermissionsPage"));
					put(
						"mutation#createAssetLibraryContentTemplatesPageExportBatch",
						new ObjectValuePair<>(
							ContentTemplateResourceImpl.class,
							"postAssetLibraryContentTemplatesPageExportBatch"));
					put(
						"mutation#createSiteContentTemplatesPageExportBatch",
						new ObjectValuePair<>(
							ContentTemplateResourceImpl.class,
							"postSiteContentTemplatesPageExportBatch"));
					put(
						"mutation#createAssetLibraryDocumentsPageExportBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postAssetLibraryDocumentsPageExportBatch"));
					put(
						"mutation#createAssetLibraryDocument",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postAssetLibraryDocument"));
					put(
						"mutation#createAssetLibraryDocumentBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postAssetLibraryDocumentBatch"));
					put(
						"mutation#deleteAssetLibraryDocumentByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"deleteAssetLibraryDocumentByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryDocumentByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"putAssetLibraryDocumentByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryDocumentPermissionsPage",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"putAssetLibraryDocumentPermissionsPage"));
					put(
						"mutation#createDocumentFolderDocumentsPageExportBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postDocumentFolderDocumentsPageExportBatch"));
					put(
						"mutation#createDocumentFolderDocument",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postDocumentFolderDocument"));
					put(
						"mutation#createDocumentFolderDocumentBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postDocumentFolderDocumentBatch"));
					put(
						"mutation#deleteDocument",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "deleteDocument"));
					put(
						"mutation#deleteDocumentBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "deleteDocumentBatch"));
					put(
						"mutation#patchDocument",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "patchDocument"));
					put(
						"mutation#updateDocument",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "putDocument"));
					put(
						"mutation#updateDocumentBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "putDocumentBatch"));
					put(
						"mutation#deleteDocumentMyRating",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"deleteDocumentMyRating"));
					put(
						"mutation#createDocumentMyRating",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postDocumentMyRating"));
					put(
						"mutation#updateDocumentMyRating",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "putDocumentMyRating"));
					put(
						"mutation#updateDocumentPermissionsPage",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"putDocumentPermissionsPage"));
					put(
						"mutation#createSiteDocumentsPageExportBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postSiteDocumentsPageExportBatch"));
					put(
						"mutation#createSiteDocument",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "postSiteDocument"));
					put(
						"mutation#createSiteDocumentBatch",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"postSiteDocumentBatch"));
					put(
						"mutation#deleteSiteDocumentByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"deleteSiteDocumentByExternalReferenceCode"));
					put(
						"mutation#updateSiteDocumentByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"putSiteDocumentByExternalReferenceCode"));
					put(
						"mutation#updateSiteDocumentPermissionsPage",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"putSiteDocumentPermissionsPage"));
					put(
						"mutation#createAssetLibraryDocumentDataDefinitionTypesPageExportBatch",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"postAssetLibraryDocumentDataDefinitionTypesPageExportBatch"));
					put(
						"mutation#createAssetLibraryDocumentDataDefinitionType",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"postAssetLibraryDocumentDataDefinitionType"));
					put(
						"mutation#createAssetLibraryDocumentDataDefinitionTypeBatch",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"postAssetLibraryDocumentDataDefinitionTypeBatch"));
					put(
						"mutation#deleteDocumentDataDefinitionType",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"deleteDocumentDataDefinitionType"));
					put(
						"mutation#deleteDocumentDataDefinitionTypeBatch",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"deleteDocumentDataDefinitionTypeBatch"));
					put(
						"mutation#createSiteDocumentDataDefinitionTypesPageExportBatch",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"postSiteDocumentDataDefinitionTypesPageExportBatch"));
					put(
						"mutation#createSiteDocumentDataDefinitionType",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"postSiteDocumentDataDefinitionType"));
					put(
						"mutation#createSiteDocumentDataDefinitionTypeBatch",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"postSiteDocumentDataDefinitionTypeBatch"));
					put(
						"mutation#createAssetLibraryDocumentFoldersPageExportBatch",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postAssetLibraryDocumentFoldersPageExportBatch"));
					put(
						"mutation#createAssetLibraryDocumentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postAssetLibraryDocumentFolder"));
					put(
						"mutation#createAssetLibraryDocumentFolderBatch",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postAssetLibraryDocumentFolderBatch"));
					put(
						"mutation#updateAssetLibraryDocumentFolderPermissionsPage",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putAssetLibraryDocumentFolderPermissionsPage"));
					put(
						"mutation#deleteDocumentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"deleteDocumentFolder"));
					put(
						"mutation#deleteDocumentFolderBatch",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"deleteDocumentFolderBatch"));
					put(
						"mutation#patchDocumentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"patchDocumentFolder"));
					put(
						"mutation#updateDocumentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putDocumentFolder"));
					put(
						"mutation#updateDocumentFolderBatch",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putDocumentFolderBatch"));
					put(
						"mutation#deleteDocumentFolderMyRating",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"deleteDocumentFolderMyRating"));
					put(
						"mutation#createDocumentFolderMyRating",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postDocumentFolderMyRating"));
					put(
						"mutation#updateDocumentFolderMyRating",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putDocumentFolderMyRating"));
					put(
						"mutation#updateDocumentFolderPermissionsPage",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putDocumentFolderPermissionsPage"));
					put(
						"mutation#updateDocumentFolderSubscribe",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putDocumentFolderSubscribe"));
					put(
						"mutation#updateDocumentFolderUnsubscribe",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putDocumentFolderUnsubscribe"));
					put(
						"mutation#createDocumentFolderDocumentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postDocumentFolderDocumentFolder"));
					put(
						"mutation#createSiteDocumentFoldersPageExportBatch",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postSiteDocumentFoldersPageExportBatch"));
					put(
						"mutation#createSiteDocumentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postSiteDocumentFolder"));
					put(
						"mutation#createSiteDocumentFolderBatch",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"postSiteDocumentFolderBatch"));
					put(
						"mutation#updateSiteDocumentFolderPermissionsPage",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putSiteDocumentFolderPermissionsPage"));
					put(
						"mutation#deleteSiteDocumentsFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"deleteSiteDocumentsFolderByExternalReferenceCode"));
					put(
						"mutation#updateSiteDocumentsFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"putSiteDocumentsFolderByExternalReferenceCode"));
					put(
						"mutation#createAssetLibraryDocumentMetadataSetsPageExportBatch",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"postAssetLibraryDocumentMetadataSetsPageExportBatch"));
					put(
						"mutation#createAssetLibraryDocumentMetadataSet",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"postAssetLibraryDocumentMetadataSet"));
					put(
						"mutation#createAssetLibraryDocumentMetadataSetBatch",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"postAssetLibraryDocumentMetadataSetBatch"));
					put(
						"mutation#deleteAssetLibraryDocumentMetadataSetByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"deleteAssetLibraryDocumentMetadataSetByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryDocumentMetadataSetByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"putAssetLibraryDocumentMetadataSetByExternalReferenceCode"));
					put(
						"mutation#deleteDocumentMetadataSet",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"deleteDocumentMetadataSet"));
					put(
						"mutation#deleteDocumentMetadataSetBatch",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"deleteDocumentMetadataSetBatch"));
					put(
						"mutation#createSiteDocumentMetadataSetsPageExportBatch",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"postSiteDocumentMetadataSetsPageExportBatch"));
					put(
						"mutation#createSiteDocumentMetadataSet",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"postSiteDocumentMetadataSet"));
					put(
						"mutation#createSiteDocumentMetadataSetBatch",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"postSiteDocumentMetadataSetBatch"));
					put(
						"mutation#deleteSiteDocumentMetadataSetByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"deleteSiteDocumentMetadataSetByExternalReferenceCode"));
					put(
						"mutation#updateSiteDocumentMetadataSetByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"putSiteDocumentMetadataSetByExternalReferenceCode"));
					put(
						"mutation#createAssetLibraryDocumentShortcutsPageExportBatch",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"postAssetLibraryDocumentShortcutsPageExportBatch"));
					put(
						"mutation#createAssetLibraryDocumentShortcut",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"postAssetLibraryDocumentShortcut"));
					put(
						"mutation#createAssetLibraryDocumentShortcutBatch",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"postAssetLibraryDocumentShortcutBatch"));
					put(
						"mutation#deleteDocumentShortcut",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"deleteDocumentShortcut"));
					put(
						"mutation#deleteDocumentShortcutBatch",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"deleteDocumentShortcutBatch"));
					put(
						"mutation#patchDocumentShortcut",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"patchDocumentShortcut"));
					put(
						"mutation#updateDocumentShortcut",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"putDocumentShortcut"));
					put(
						"mutation#updateDocumentShortcutBatch",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"putDocumentShortcutBatch"));
					put(
						"mutation#createSiteDocumentShortcutsPageExportBatch",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"postSiteDocumentShortcutsPageExportBatch"));
					put(
						"mutation#createSiteDocumentShortcut",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"postSiteDocumentShortcut"));
					put(
						"mutation#createSiteDocumentShortcutBatch",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"postSiteDocumentShortcutBatch"));
					put(
						"mutation#deleteSiteDocumentShortcutByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"deleteSiteDocumentShortcutByExternalReferenceCode"));
					put(
						"mutation#updateSiteDocumentShortcutByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"putSiteDocumentShortcutByExternalReferenceCode"));
					put(
						"mutation#deleteKnowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"deleteKnowledgeBaseArticle"));
					put(
						"mutation#deleteKnowledgeBaseArticleBatch",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"deleteKnowledgeBaseArticleBatch"));
					put(
						"mutation#patchKnowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"patchKnowledgeBaseArticle"));
					put(
						"mutation#updateKnowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putKnowledgeBaseArticle"));
					put(
						"mutation#updateKnowledgeBaseArticleBatch",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putKnowledgeBaseArticleBatch"));
					put(
						"mutation#deleteKnowledgeBaseArticleMyRating",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"deleteKnowledgeBaseArticleMyRating"));
					put(
						"mutation#createKnowledgeBaseArticleMyRating",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postKnowledgeBaseArticleMyRating"));
					put(
						"mutation#updateKnowledgeBaseArticleMyRating",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putKnowledgeBaseArticleMyRating"));
					put(
						"mutation#updateKnowledgeBaseArticlePermissionsPage",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putKnowledgeBaseArticlePermissionsPage"));
					put(
						"mutation#updateKnowledgeBaseArticleSubscribe",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putKnowledgeBaseArticleSubscribe"));
					put(
						"mutation#updateKnowledgeBaseArticleUnsubscribe",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putKnowledgeBaseArticleUnsubscribe"));
					put(
						"mutation#createKnowledgeBaseArticleKnowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postKnowledgeBaseArticleKnowledgeBaseArticle"));
					put(
						"mutation#createKnowledgeBaseFolderKnowledgeBaseArticlesPageExportBatch",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postKnowledgeBaseFolderKnowledgeBaseArticlesPageExportBatch"));
					put(
						"mutation#createKnowledgeBaseFolderKnowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postKnowledgeBaseFolderKnowledgeBaseArticle"));
					put(
						"mutation#createKnowledgeBaseFolderKnowledgeBaseArticleBatch",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postKnowledgeBaseFolderKnowledgeBaseArticleBatch"));
					put(
						"mutation#createSiteKnowledgeBaseArticlesPageExportBatch",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postSiteKnowledgeBaseArticlesPageExportBatch"));
					put(
						"mutation#createSiteKnowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postSiteKnowledgeBaseArticle"));
					put(
						"mutation#createSiteKnowledgeBaseArticleBatch",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"postSiteKnowledgeBaseArticleBatch"));
					put(
						"mutation#deleteSiteKnowledgeBaseArticleByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"deleteSiteKnowledgeBaseArticleByExternalReferenceCode"));
					put(
						"mutation#updateSiteKnowledgeBaseArticleByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putSiteKnowledgeBaseArticleByExternalReferenceCode"));
					put(
						"mutation#updateSiteKnowledgeBaseArticlePermissionsPage",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putSiteKnowledgeBaseArticlePermissionsPage"));
					put(
						"mutation#updateSiteKnowledgeBaseArticleSubscribe",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putSiteKnowledgeBaseArticleSubscribe"));
					put(
						"mutation#updateSiteKnowledgeBaseArticleUnsubscribe",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"putSiteKnowledgeBaseArticleUnsubscribe"));
					put(
						"mutation#createKnowledgeBaseArticleKnowledgeBaseAttachmentsPageExportBatch",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"postKnowledgeBaseArticleKnowledgeBaseAttachmentsPageExportBatch"));
					put(
						"mutation#createKnowledgeBaseArticleKnowledgeBaseAttachment",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"postKnowledgeBaseArticleKnowledgeBaseAttachment"));
					put(
						"mutation#createKnowledgeBaseArticleKnowledgeBaseAttachmentBatch",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"postKnowledgeBaseArticleKnowledgeBaseAttachmentBatch"));
					put(
						"mutation#deleteKnowledgeBaseAttachment",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"deleteKnowledgeBaseAttachment"));
					put(
						"mutation#deleteKnowledgeBaseAttachmentBatch",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"deleteKnowledgeBaseAttachmentBatch"));
					put(
						"mutation#deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"deleteSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode"));
					put(
						"mutation#deleteKnowledgeBaseFolder",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"deleteKnowledgeBaseFolder"));
					put(
						"mutation#deleteKnowledgeBaseFolderBatch",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"deleteKnowledgeBaseFolderBatch"));
					put(
						"mutation#patchKnowledgeBaseFolder",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"patchKnowledgeBaseFolder"));
					put(
						"mutation#updateKnowledgeBaseFolder",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"putKnowledgeBaseFolder"));
					put(
						"mutation#updateKnowledgeBaseFolderBatch",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"putKnowledgeBaseFolderBatch"));
					put(
						"mutation#updateKnowledgeBaseFolderPermissionsPage",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"putKnowledgeBaseFolderPermissionsPage"));
					put(
						"mutation#createKnowledgeBaseFolderKnowledgeBaseFolder",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"postKnowledgeBaseFolderKnowledgeBaseFolder"));
					put(
						"mutation#createSiteKnowledgeBaseFoldersPageExportBatch",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"postSiteKnowledgeBaseFoldersPageExportBatch"));
					put(
						"mutation#createSiteKnowledgeBaseFolder",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"postSiteKnowledgeBaseFolder"));
					put(
						"mutation#createSiteKnowledgeBaseFolderBatch",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"postSiteKnowledgeBaseFolderBatch"));
					put(
						"mutation#deleteSiteKnowledgeBaseFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"deleteSiteKnowledgeBaseFolderByExternalReferenceCode"));
					put(
						"mutation#updateSiteKnowledgeBaseFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"putSiteKnowledgeBaseFolderByExternalReferenceCode"));
					put(
						"mutation#updateSiteKnowledgeBaseFolderPermissionsPage",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"putSiteKnowledgeBaseFolderPermissionsPage"));
					put(
						"mutation#createAssetLibraryLanguagesPageExportBatch",
						new ObjectValuePair<>(
							LanguageResourceImpl.class,
							"postAssetLibraryLanguagesPageExportBatch"));
					put(
						"mutation#createSiteLanguagesPageExportBatch",
						new ObjectValuePair<>(
							LanguageResourceImpl.class,
							"postSiteLanguagesPageExportBatch"));
					put(
						"mutation#deleteMessageBoardAttachment",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"deleteMessageBoardAttachment"));
					put(
						"mutation#deleteMessageBoardAttachmentBatch",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"deleteMessageBoardAttachmentBatch"));
					put(
						"mutation#createMessageBoardMessageMessageBoardAttachmentsPageExportBatch",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"postMessageBoardMessageMessageBoardAttachmentsPageExportBatch"));
					put(
						"mutation#createMessageBoardMessageMessageBoardAttachment",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"postMessageBoardMessageMessageBoardAttachment"));
					put(
						"mutation#createMessageBoardMessageMessageBoardAttachmentBatch",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"postMessageBoardMessageMessageBoardAttachmentBatch"));
					put(
						"mutation#createMessageBoardThreadMessageBoardAttachmentsPageExportBatch",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"postMessageBoardThreadMessageBoardAttachmentsPageExportBatch"));
					put(
						"mutation#createMessageBoardThreadMessageBoardAttachment",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"postMessageBoardThreadMessageBoardAttachment"));
					put(
						"mutation#createMessageBoardThreadMessageBoardAttachmentBatch",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"postMessageBoardThreadMessageBoardAttachmentBatch"));
					put(
						"mutation#deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"deleteSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode"));
					put(
						"mutation#deleteMessageBoardMessage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"deleteMessageBoardMessage"));
					put(
						"mutation#deleteMessageBoardMessageBatch",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"deleteMessageBoardMessageBatch"));
					put(
						"mutation#patchMessageBoardMessage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"patchMessageBoardMessage"));
					put(
						"mutation#updateMessageBoardMessage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessage"));
					put(
						"mutation#updateMessageBoardMessageBatch",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessageBatch"));
					put(
						"mutation#updateMessageBoardMessageMarkAsAnswer",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessageMarkAsAnswer"));
					put(
						"mutation#deleteMessageBoardMessageMyRating",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"deleteMessageBoardMessageMyRating"));
					put(
						"mutation#createMessageBoardMessageMyRating",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"postMessageBoardMessageMyRating"));
					put(
						"mutation#updateMessageBoardMessageMyRating",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessageMyRating"));
					put(
						"mutation#updateMessageBoardMessagePermissionsPage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessagePermissionsPage"));
					put(
						"mutation#updateMessageBoardMessageSubscribe",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessageSubscribe"));
					put(
						"mutation#updateMessageBoardMessageUnmarkAsAnswer",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessageUnmarkAsAnswer"));
					put(
						"mutation#updateMessageBoardMessageUnsubscribe",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putMessageBoardMessageUnsubscribe"));
					put(
						"mutation#createMessageBoardMessageMessageBoardMessage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"postMessageBoardMessageMessageBoardMessage"));
					put(
						"mutation#createMessageBoardThreadMessageBoardMessagesPageExportBatch",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"postMessageBoardThreadMessageBoardMessagesPageExportBatch"));
					put(
						"mutation#createMessageBoardThreadMessageBoardMessage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"postMessageBoardThreadMessageBoardMessage"));
					put(
						"mutation#createMessageBoardThreadMessageBoardMessageBatch",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"postMessageBoardThreadMessageBoardMessageBatch"));
					put(
						"mutation#createSiteMessageBoardMessagesPageExportBatch",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"postSiteMessageBoardMessagesPageExportBatch"));
					put(
						"mutation#deleteSiteMessageBoardMessageByExternalReferenceCode",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"deleteSiteMessageBoardMessageByExternalReferenceCode"));
					put(
						"mutation#updateSiteMessageBoardMessageByExternalReferenceCode",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putSiteMessageBoardMessageByExternalReferenceCode"));
					put(
						"mutation#updateSiteMessageBoardMessagePermissionsPage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"putSiteMessageBoardMessagePermissionsPage"));
					put(
						"mutation#deleteMessageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"deleteMessageBoardSection"));
					put(
						"mutation#deleteMessageBoardSectionBatch",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"deleteMessageBoardSectionBatch"));
					put(
						"mutation#patchMessageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"patchMessageBoardSection"));
					put(
						"mutation#updateMessageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"putMessageBoardSection"));
					put(
						"mutation#updateMessageBoardSectionBatch",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"putMessageBoardSectionBatch"));
					put(
						"mutation#updateMessageBoardSectionPermissionsPage",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"putMessageBoardSectionPermissionsPage"));
					put(
						"mutation#updateMessageBoardSectionSubscribe",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"putMessageBoardSectionSubscribe"));
					put(
						"mutation#updateMessageBoardSectionUnsubscribe",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"putMessageBoardSectionUnsubscribe"));
					put(
						"mutation#createMessageBoardSectionMessageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"postMessageBoardSectionMessageBoardSection"));
					put(
						"mutation#createSiteMessageBoardSectionsPageExportBatch",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"postSiteMessageBoardSectionsPageExportBatch"));
					put(
						"mutation#createSiteMessageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"postSiteMessageBoardSection"));
					put(
						"mutation#createSiteMessageBoardSectionBatch",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"postSiteMessageBoardSectionBatch"));
					put(
						"mutation#updateSiteMessageBoardSectionPermissionsPage",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"putSiteMessageBoardSectionPermissionsPage"));
					put(
						"mutation#createMessageBoardSectionMessageBoardThreadsPageExportBatch",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"postMessageBoardSectionMessageBoardThreadsPageExportBatch"));
					put(
						"mutation#createMessageBoardSectionMessageBoardThread",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"postMessageBoardSectionMessageBoardThread"));
					put(
						"mutation#createMessageBoardSectionMessageBoardThreadBatch",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"postMessageBoardSectionMessageBoardThreadBatch"));
					put(
						"mutation#deleteMessageBoardThread",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"deleteMessageBoardThread"));
					put(
						"mutation#deleteMessageBoardThreadBatch",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"deleteMessageBoardThreadBatch"));
					put(
						"mutation#patchMessageBoardThread",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"patchMessageBoardThread"));
					put(
						"mutation#updateMessageBoardThread",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"putMessageBoardThread"));
					put(
						"mutation#updateMessageBoardThreadBatch",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"putMessageBoardThreadBatch"));
					put(
						"mutation#deleteMessageBoardThreadMyRating",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"deleteMessageBoardThreadMyRating"));
					put(
						"mutation#createMessageBoardThreadMyRating",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"postMessageBoardThreadMyRating"));
					put(
						"mutation#updateMessageBoardThreadMyRating",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"putMessageBoardThreadMyRating"));
					put(
						"mutation#updateMessageBoardThreadPermissionsPage",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"putMessageBoardThreadPermissionsPage"));
					put(
						"mutation#updateMessageBoardThreadSubscribe",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"putMessageBoardThreadSubscribe"));
					put(
						"mutation#updateMessageBoardThreadUnsubscribe",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"putMessageBoardThreadUnsubscribe"));
					put(
						"mutation#createSiteMessageBoardThreadsPageExportBatch",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"postSiteMessageBoardThreadsPageExportBatch"));
					put(
						"mutation#createSiteMessageBoardThread",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"postSiteMessageBoardThread"));
					put(
						"mutation#createSiteMessageBoardThreadBatch",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"postSiteMessageBoardThreadBatch"));
					put(
						"mutation#updateSiteMessageBoardThreadPermissionsPage",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"putSiteMessageBoardThreadPermissionsPage"));
					put(
						"mutation#deleteNavigationMenu",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"deleteNavigationMenu"));
					put(
						"mutation#deleteNavigationMenuBatch",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"deleteNavigationMenuBatch"));
					put(
						"mutation#updateNavigationMenu",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"putNavigationMenu"));
					put(
						"mutation#updateNavigationMenuBatch",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"putNavigationMenuBatch"));
					put(
						"mutation#updateNavigationMenuPermissionsPage",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"putNavigationMenuPermissionsPage"));
					put(
						"mutation#createSiteNavigationMenusPageExportBatch",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"postSiteNavigationMenusPageExportBatch"));
					put(
						"mutation#createSiteNavigationMenu",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"postSiteNavigationMenu"));
					put(
						"mutation#createSiteNavigationMenuBatch",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"postSiteNavigationMenuBatch"));
					put(
						"mutation#updateSiteNavigationMenuPermissionsPage",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"putSiteNavigationMenuPermissionsPage"));
					put(
						"mutation#createSiteSitePagesPageExportBatch",
						new ObjectValuePair<>(
							SitePageResourceImpl.class,
							"postSiteSitePagesPageExportBatch"));
					put(
						"mutation#createSiteSitePage",
						new ObjectValuePair<>(
							SitePageResourceImpl.class, "postSiteSitePage"));
					put(
						"mutation#createSiteSitePageBatch",
						new ObjectValuePair<>(
							SitePageResourceImpl.class,
							"postSiteSitePageBatch"));
					put(
						"mutation#createAssetLibraryStructuredContentsPageExportBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postAssetLibraryStructuredContentsPageExportBatch"));
					put(
						"mutation#createAssetLibraryStructuredContent",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postAssetLibraryStructuredContent"));
					put(
						"mutation#createAssetLibraryStructuredContentBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postAssetLibraryStructuredContentBatch"));
					put(
						"mutation#deleteAssetLibraryStructuredContentByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"deleteAssetLibraryStructuredContentByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryStructuredContentByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putAssetLibraryStructuredContentByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryStructuredContentPermissionsPage",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putAssetLibraryStructuredContentPermissionsPage"));
					put(
						"mutation#createContentStructureStructuredContentsPageExportBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postContentStructureStructuredContentsPageExportBatch"));
					put(
						"mutation#createSiteStructuredContentsPageExportBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postSiteStructuredContentsPageExportBatch"));
					put(
						"mutation#createSiteStructuredContent",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postSiteStructuredContent"));
					put(
						"mutation#createSiteStructuredContentBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postSiteStructuredContentBatch"));
					put(
						"mutation#deleteSiteStructuredContentByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"deleteSiteStructuredContentByExternalReferenceCode"));
					put(
						"mutation#updateSiteStructuredContentByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putSiteStructuredContentByExternalReferenceCode"));
					put(
						"mutation#updateSiteStructuredContentPermissionsPage",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putSiteStructuredContentPermissionsPage"));
					put(
						"mutation#createStructuredContentFolderStructuredContentsPageExportBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postStructuredContentFolderStructuredContentsPageExportBatch"));
					put(
						"mutation#createStructuredContentFolderStructuredContent",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postStructuredContentFolderStructuredContent"));
					put(
						"mutation#createStructuredContentFolderStructuredContentBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postStructuredContentFolderStructuredContentBatch"));
					put(
						"mutation#deleteStructuredContent",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"deleteStructuredContent"));
					put(
						"mutation#deleteStructuredContentBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"deleteStructuredContentBatch"));
					put(
						"mutation#patchStructuredContent",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"patchStructuredContent"));
					put(
						"mutation#updateStructuredContent",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putStructuredContent"));
					put(
						"mutation#updateStructuredContentBatch",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putStructuredContentBatch"));
					put(
						"mutation#deleteStructuredContentMyRating",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"deleteStructuredContentMyRating"));
					put(
						"mutation#createStructuredContentMyRating",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"postStructuredContentMyRating"));
					put(
						"mutation#updateStructuredContentMyRating",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putStructuredContentMyRating"));
					put(
						"mutation#updateStructuredContentPermissionsPage",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putStructuredContentPermissionsPage"));
					put(
						"mutation#updateStructuredContentSubscribe",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putStructuredContentSubscribe"));
					put(
						"mutation#updateStructuredContentUnsubscribe",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"putStructuredContentUnsubscribe"));
					put(
						"mutation#createAssetLibraryStructuredContentFoldersPageExportBatch",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"postAssetLibraryStructuredContentFoldersPageExportBatch"));
					put(
						"mutation#createAssetLibraryStructuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"postAssetLibraryStructuredContentFolder"));
					put(
						"mutation#createAssetLibraryStructuredContentFolderBatch",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"postAssetLibraryStructuredContentFolderBatch"));
					put(
						"mutation#deleteAssetLibraryStructuredContentFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"deleteAssetLibraryStructuredContentFolderByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryStructuredContentFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putAssetLibraryStructuredContentFolderByExternalReferenceCode"));
					put(
						"mutation#updateAssetLibraryStructuredContentFolderPermissionsPage",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putAssetLibraryStructuredContentFolderPermissionsPage"));
					put(
						"mutation#createSiteStructuredContentFoldersPageExportBatch",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"postSiteStructuredContentFoldersPageExportBatch"));
					put(
						"mutation#createSiteStructuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"postSiteStructuredContentFolder"));
					put(
						"mutation#createSiteStructuredContentFolderBatch",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"postSiteStructuredContentFolderBatch"));
					put(
						"mutation#deleteSiteStructuredContentFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"deleteSiteStructuredContentFolderByExternalReferenceCode"));
					put(
						"mutation#updateSiteStructuredContentFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putSiteStructuredContentFolderByExternalReferenceCode"));
					put(
						"mutation#updateSiteStructuredContentFolderPermissionsPage",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putSiteStructuredContentFolderPermissionsPage"));
					put(
						"mutation#updateStructuredContentFolderPermissionsPage",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putStructuredContentFolderPermissionsPage"));
					put(
						"mutation#createStructuredContentFolderStructuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"postStructuredContentFolderStructuredContentFolder"));
					put(
						"mutation#deleteStructuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"deleteStructuredContentFolder"));
					put(
						"mutation#deleteStructuredContentFolderBatch",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"deleteStructuredContentFolderBatch"));
					put(
						"mutation#patchStructuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"patchStructuredContentFolder"));
					put(
						"mutation#updateStructuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putStructuredContentFolder"));
					put(
						"mutation#updateStructuredContentFolderBatch",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putStructuredContentFolderBatch"));
					put(
						"mutation#updateStructuredContentFolderSubscribe",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putStructuredContentFolderSubscribe"));
					put(
						"mutation#updateStructuredContentFolderUnsubscribe",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"putStructuredContentFolderUnsubscribe"));
					put(
						"mutation#createSiteWikiNodesPageExportBatch",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"postSiteWikiNodesPageExportBatch"));
					put(
						"mutation#createSiteWikiNode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class, "postSiteWikiNode"));
					put(
						"mutation#createSiteWikiNodeBatch",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"postSiteWikiNodeBatch"));
					put(
						"mutation#deleteSiteWikiNodeByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"deleteSiteWikiNodeByExternalReferenceCode"));
					put(
						"mutation#updateSiteWikiNodeByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"putSiteWikiNodeByExternalReferenceCode"));
					put(
						"mutation#updateSiteWikiNodePermissionsPage",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"putSiteWikiNodePermissionsPage"));
					put(
						"mutation#deleteWikiNode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class, "deleteWikiNode"));
					put(
						"mutation#deleteWikiNodeBatch",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class, "deleteWikiNodeBatch"));
					put(
						"mutation#updateWikiNode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class, "putWikiNode"));
					put(
						"mutation#updateWikiNodeBatch",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class, "putWikiNodeBatch"));
					put(
						"mutation#updateWikiNodePermissionsPage",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"putWikiNodePermissionsPage"));
					put(
						"mutation#updateWikiNodeSubscribe",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"putWikiNodeSubscribe"));
					put(
						"mutation#updateWikiNodeUnsubscribe",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"putWikiNodeUnsubscribe"));
					put(
						"mutation#deleteSiteWikiPageByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"deleteSiteWikiPageByExternalReferenceCode"));
					put(
						"mutation#updateSiteWikiPageByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"putSiteWikiPageByExternalReferenceCode"));
					put(
						"mutation#createWikiNodeWikiPagesPageExportBatch",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"postWikiNodeWikiPagesPageExportBatch"));
					put(
						"mutation#createWikiNodeWikiPage",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"postWikiNodeWikiPage"));
					put(
						"mutation#createWikiNodeWikiPageBatch",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"postWikiNodeWikiPageBatch"));
					put(
						"mutation#createWikiPageWikiPage",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"postWikiPageWikiPage"));
					put(
						"mutation#deleteWikiPage",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class, "deleteWikiPage"));
					put(
						"mutation#deleteWikiPageBatch",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class, "deleteWikiPageBatch"));
					put(
						"mutation#updateWikiPage",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class, "putWikiPage"));
					put(
						"mutation#updateWikiPageBatch",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class, "putWikiPageBatch"));
					put(
						"mutation#updateWikiPagePermissionsPage",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"putWikiPagePermissionsPage"));
					put(
						"mutation#updateWikiPageSubscribe",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"putWikiPageSubscribe"));
					put(
						"mutation#updateWikiPageUnsubscribe",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"putWikiPageUnsubscribe"));
					put(
						"mutation#deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"deleteSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode"));
					put(
						"mutation#deleteWikiPageAttachment",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"deleteWikiPageAttachment"));
					put(
						"mutation#deleteWikiPageAttachmentBatch",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"deleteWikiPageAttachmentBatch"));
					put(
						"mutation#createWikiPageWikiPageAttachmentsPageExportBatch",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"postWikiPageWikiPageAttachmentsPageExportBatch"));
					put(
						"mutation#createWikiPageWikiPageAttachment",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"postWikiPageWikiPageAttachment"));
					put(
						"mutation#createWikiPageWikiPageAttachmentBatch",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"postWikiPageWikiPageAttachmentBatch"));

					put(
						"query#blogPosting",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class, "getBlogPosting"));
					put(
						"query#blogPostingMyRating",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getBlogPostingMyRating"));
					put(
						"query#blogPostingPermissions",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getBlogPostingPermissionsPage"));
					put(
						"query#blogPostingRenderedContentByDisplayPageDisplayPageKey",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getBlogPostingRenderedContentByDisplayPageDisplayPageKey"));
					put(
						"query#blogPostings",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getSiteBlogPostingsPage"));
					put(
						"query#blogPostingByExternalReferenceCode",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getSiteBlogPostingByExternalReferenceCode"));
					put(
						"query#siteBlogPostingPermissions",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getSiteBlogPostingPermissionsPage"));
					put(
						"query#blogPostingImage",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"getBlogPostingImage"));
					put(
						"query#blogPostingImages",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"getSiteBlogPostingImagesPage"));
					put(
						"query#blogPostingImageByExternalReferenceCode",
						new ObjectValuePair<>(
							BlogPostingImageResourceImpl.class,
							"getSiteBlogPostingImageByExternalReferenceCode"));
					put(
						"query#blogPostingComments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getBlogPostingCommentsPage"));
					put(
						"query#comment",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "getComment"));
					put(
						"query#commentComments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getCommentCommentsPage"));
					put(
						"query#documentComments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getDocumentCommentsPage"));
					put(
						"query#blogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getSiteBlogPostingByExternalReferenceCodeBlogPostingExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"query#commentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getSiteCommentByExternalReferenceCodeParentCommentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"query#documentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getSiteDocumentByExternalReferenceCodeDocumentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"query#structuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getSiteStructuredContentByExternalReferenceCodeStructuredContentExternalReferenceCodeCommentByExternalReferenceCode"));
					put(
						"query#structuredContentComments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getStructuredContentCommentsPage"));
					put(
						"query#assetLibraryContentElements",
						new ObjectValuePair<>(
							ContentElementResourceImpl.class,
							"getAssetLibraryContentElementsPage"));
					put(
						"query#contentElements",
						new ObjectValuePair<>(
							ContentElementResourceImpl.class,
							"getSiteContentElementsPage"));
					put(
						"query#assetLibraryContentSetByKeyContentSetElements",
						new ObjectValuePair<>(
							ContentSetElementResourceImpl.class,
							"getAssetLibraryContentSetByKeyContentSetElementsPage"));
					put(
						"query#assetLibraryContentSetByUuidContentSetElements",
						new ObjectValuePair<>(
							ContentSetElementResourceImpl.class,
							"getAssetLibraryContentSetByUuidContentSetElementsPage"));
					put(
						"query#contentSetContentSetElements",
						new ObjectValuePair<>(
							ContentSetElementResourceImpl.class,
							"getContentSetContentSetElementsPage"));
					put(
						"query#contentSetProviderByKeyContentSetElements",
						new ObjectValuePair<>(
							ContentSetElementResourceImpl.class,
							"getSiteContentSetProviderByKeyContentSetElementsPage"));
					put(
						"query#contentSetByKeyContentSetElements",
						new ObjectValuePair<>(
							ContentSetElementResourceImpl.class,
							"getSiteContentSetByKeyContentSetElementsPage"));
					put(
						"query#contentSetByUuidContentSetElements",
						new ObjectValuePair<>(
							ContentSetElementResourceImpl.class,
							"getSiteContentSetByUuidContentSetElementsPage"));
					put(
						"query#assetLibraryContentStructures",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getAssetLibraryContentStructuresPage"));
					put(
						"query#assetLibraryContentStructurePermissions",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getAssetLibraryContentStructurePermissionsPage"));
					put(
						"query#contentStructure",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getContentStructure"));
					put(
						"query#contentStructurePermissions",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getContentStructurePermissionsPage"));
					put(
						"query#contentStructures",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getSiteContentStructuresPage"));
					put(
						"query#siteContentStructurePermissions",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getSiteContentStructurePermissionsPage"));
					put(
						"query#assetLibraryContentTemplates",
						new ObjectValuePair<>(
							ContentTemplateResourceImpl.class,
							"getAssetLibraryContentTemplatesPage"));
					put(
						"query#contentTemplates",
						new ObjectValuePair<>(
							ContentTemplateResourceImpl.class,
							"getSiteContentTemplatesPage"));
					put(
						"query#contentTemplate",
						new ObjectValuePair<>(
							ContentTemplateResourceImpl.class,
							"getSiteContentTemplate"));
					put(
						"query#assetLibraryDocuments",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getAssetLibraryDocumentsPage"));
					put(
						"query#assetLibraryDocumentByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getAssetLibraryDocumentByExternalReferenceCode"));
					put(
						"query#assetLibraryDocumentPermissions",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getAssetLibraryDocumentPermissionsPage"));
					put(
						"query#assetLibraryDocumentsRatedByMe",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getAssetLibraryDocumentsRatedByMePage"));
					put(
						"query#documentFolderDocuments",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getDocumentFolderDocumentsPage"));
					put(
						"query#document",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "getDocument"));
					put(
						"query#documentMyRating",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "getDocumentMyRating"));
					put(
						"query#documentPermissions",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getDocumentPermissionsPage"));
					put(
						"query#documentRenderedContentByDisplayPageDisplayPageKey",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getDocumentRenderedContentByDisplayPageDisplayPageKey"));
					put(
						"query#documents",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getSiteDocumentsPage"));
					put(
						"query#documentByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getSiteDocumentByExternalReferenceCode"));
					put(
						"query#siteDocumentPermissions",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getSiteDocumentPermissionsPage"));
					put(
						"query#documentsRatedByMe",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getSiteDocumentsRatedByMePage"));
					put(
						"query#assetLibraryDocumentDataDefinitionTypes",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"getAssetLibraryDocumentDataDefinitionTypesPage"));
					put(
						"query#documentDataDefinitionType",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"getDocumentDataDefinitionType"));
					put(
						"query#documentDataDefinitionTypes",
						new ObjectValuePair<>(
							DocumentDataDefinitionTypeResourceImpl.class,
							"getSiteDocumentDataDefinitionTypesPage"));
					put(
						"query#assetLibraryDocumentFolders",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getAssetLibraryDocumentFoldersPage"));
					put(
						"query#assetLibraryDocumentFolderPermissions",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getAssetLibraryDocumentFolderPermissionsPage"));
					put(
						"query#assetLibraryDocumentFoldersRatedByMe",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getAssetLibraryDocumentFoldersRatedByMePage"));
					put(
						"query#documentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolder"));
					put(
						"query#documentFolderMyRating",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolderMyRating"));
					put(
						"query#documentFolderPermissions",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolderPermissionsPage"));
					put(
						"query#documentFolderDocumentFolders",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolderDocumentFoldersPage"));
					put(
						"query#documentFolders",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getSiteDocumentFoldersPage"));
					put(
						"query#siteDocumentFolderPermissions",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getSiteDocumentFolderPermissionsPage"));
					put(
						"query#documentFoldersRatedByMe",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getSiteDocumentFoldersRatedByMePage"));
					put(
						"query#documentsFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getSiteDocumentsFolderByExternalReferenceCode"));
					put(
						"query#assetLibraryDocumentMetadataSets",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"getAssetLibraryDocumentMetadataSetsPage"));
					put(
						"query#assetLibraryDocumentMetadataSetByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"getAssetLibraryDocumentMetadataSetByExternalReferenceCode"));
					put(
						"query#documentMetadataSet",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"getDocumentMetadataSet"));
					put(
						"query#documentMetadataSets",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"getSiteDocumentMetadataSetsPage"));
					put(
						"query#documentMetadataSetByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentMetadataSetResourceImpl.class,
							"getSiteDocumentMetadataSetByExternalReferenceCode"));
					put(
						"query#assetLibraryDocumentShortcuts",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"getAssetLibraryDocumentShortcutsPage"));
					put(
						"query#documentShortcut",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"getDocumentShortcut"));
					put(
						"query#documentShortcuts",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"getSiteDocumentShortcutsPage"));
					put(
						"query#documentShortcutByExternalReferenceCode",
						new ObjectValuePair<>(
							DocumentShortcutResourceImpl.class,
							"getSiteDocumentShortcutByExternalReferenceCode"));
					put(
						"query#knowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticle"));
					put(
						"query#knowledgeBaseArticleMyRating",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticleMyRating"));
					put(
						"query#knowledgeBaseArticlePermissions",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticlePermissionsPage"));
					put(
						"query#knowledgeBaseArticleKnowledgeBaseArticles",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticleKnowledgeBaseArticlesPage"));
					put(
						"query#knowledgeBaseFolderKnowledgeBaseArticles",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseFolderKnowledgeBaseArticlesPage"));
					put(
						"query#knowledgeBaseArticles",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getSiteKnowledgeBaseArticlesPage"));
					put(
						"query#knowledgeBaseArticleByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getSiteKnowledgeBaseArticleByExternalReferenceCode"));
					put(
						"query#siteKnowledgeBaseArticlePermissions",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getSiteKnowledgeBaseArticlePermissionsPage"));
					put(
						"query#knowledgeBaseArticleKnowledgeBaseAttachments",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage"));
					put(
						"query#knowledgeBaseAttachment",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"getKnowledgeBaseAttachment"));
					put(
						"query#knowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"getSiteKnowledgeBaseArticleByExternalReferenceCodeKnowledgeBaseArticleExternalReferenceCodeKnowledgeBaseAttachmentByExternalReferenceCode"));
					put(
						"query#knowledgeBaseFolder",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getKnowledgeBaseFolder"));
					put(
						"query#knowledgeBaseFolderPermissions",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getKnowledgeBaseFolderPermissionsPage"));
					put(
						"query#knowledgeBaseFolderKnowledgeBaseFolders",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getKnowledgeBaseFolderKnowledgeBaseFoldersPage"));
					put(
						"query#knowledgeBaseFolders",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getSiteKnowledgeBaseFoldersPage"));
					put(
						"query#knowledgeBaseFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getSiteKnowledgeBaseFolderByExternalReferenceCode"));
					put(
						"query#siteKnowledgeBaseFolderPermissions",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getSiteKnowledgeBaseFolderPermissionsPage"));
					put(
						"query#assetLibraryLanguages",
						new ObjectValuePair<>(
							LanguageResourceImpl.class,
							"getAssetLibraryLanguagesPage"));
					put(
						"query#languages",
						new ObjectValuePair<>(
							LanguageResourceImpl.class,
							"getSiteLanguagesPage"));
					put(
						"query#messageBoardAttachment",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"getMessageBoardAttachment"));
					put(
						"query#messageBoardMessageMessageBoardAttachments",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"getMessageBoardMessageMessageBoardAttachmentsPage"));
					put(
						"query#messageBoardThreadMessageBoardAttachments",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"getMessageBoardThreadMessageBoardAttachmentsPage"));
					put(
						"query#messageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"getSiteMessageBoardMessageByExternalReferenceCodeMessageBoardMessageExternalReferenceCodeMessageBoardAttachmentByExternalReferenceCode"));
					put(
						"query#messageBoardMessage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessage"));
					put(
						"query#messageBoardMessageMyRating",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessageMyRating"));
					put(
						"query#messageBoardMessagePermissions",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessagePermissionsPage"));
					put(
						"query#messageBoardMessageMessageBoardMessages",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessageMessageBoardMessagesPage"));
					put(
						"query#messageBoardThreadMessageBoardMessages",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardThreadMessageBoardMessagesPage"));
					put(
						"query#messageBoardMessages",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getSiteMessageBoardMessagesPage"));
					put(
						"query#messageBoardMessageByExternalReferenceCode",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getSiteMessageBoardMessageByExternalReferenceCode"));
					put(
						"query#messageBoardMessageByFriendlyUrlPath",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getSiteMessageBoardMessageByFriendlyUrlPath"));
					put(
						"query#siteMessageBoardMessagePermissions",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getSiteMessageBoardMessagePermissionsPage"));
					put(
						"query#userMessageBoardMessagesActivity",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getSiteUserMessageBoardMessagesActivityPage"));
					put(
						"query#messageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getMessageBoardSection"));
					put(
						"query#messageBoardSectionPermissions",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getMessageBoardSectionPermissionsPage"));
					put(
						"query#messageBoardSectionMessageBoardSections",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getMessageBoardSectionMessageBoardSectionsPage"));
					put(
						"query#messageBoardSectionByFriendlyUrlPath",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getSiteMessageBoardSectionByFriendlyUrlPath"));
					put(
						"query#messageBoardSections",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getSiteMessageBoardSectionsPage"));
					put(
						"query#siteMessageBoardSectionPermissions",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getSiteMessageBoardSectionPermissionsPage"));
					put(
						"query#messageBoardSectionMessageBoardThreads",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardSectionMessageBoardThreadsPage"));
					put(
						"query#messageBoardThreadsRanked",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardThreadsRankedPage"));
					put(
						"query#messageBoardThread",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardThread"));
					put(
						"query#messageBoardThreadMyRating",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardThreadMyRating"));
					put(
						"query#messageBoardThreadPermissions",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardThreadPermissionsPage"));
					put(
						"query#messageBoardThreads",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getSiteMessageBoardThreadsPage"));
					put(
						"query#messageBoardThreadByFriendlyUrlPath",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getSiteMessageBoardThreadByFriendlyUrlPath"));
					put(
						"query#siteMessageBoardThreadPermissions",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getSiteMessageBoardThreadPermissionsPage"));
					put(
						"query#navigationMenu",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"getNavigationMenu"));
					put(
						"query#navigationMenuPermissions",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"getNavigationMenuPermissionsPage"));
					put(
						"query#navigationMenus",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"getSiteNavigationMenusPage"));
					put(
						"query#siteNavigationMenuPermissions",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"getSiteNavigationMenuPermissionsPage"));
					put(
						"query#sitePages",
						new ObjectValuePair<>(
							SitePageResourceImpl.class,
							"getSiteSitePagesPage"));
					put(
						"query#sitePage",
						new ObjectValuePair<>(
							SitePageResourceImpl.class, "getSiteSitePage"));
					put(
						"query#sitePagesExperiences",
						new ObjectValuePair<>(
							SitePageResourceImpl.class,
							"getSiteSitePagesExperiencesPage"));
					put(
						"query#sitePageExperienceExperienceKey",
						new ObjectValuePair<>(
							SitePageResourceImpl.class,
							"getSiteSitePageExperienceExperienceKey"));
					put(
						"query#sitePageExperienceExperienceKeyRenderedPage",
						new ObjectValuePair<>(
							SitePageResourceImpl.class,
							"getSiteSitePageExperienceExperienceKeyRenderedPage"));
					put(
						"query#sitePageRenderedPage",
						new ObjectValuePair<>(
							SitePageResourceImpl.class,
							"getSiteSitePageRenderedPage"));
					put(
						"query#assetLibraryStructuredContents",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getAssetLibraryStructuredContentsPage"));
					put(
						"query#assetLibraryStructuredContentByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getAssetLibraryStructuredContentByExternalReferenceCode"));
					put(
						"query#assetLibraryStructuredContentPermissions",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getAssetLibraryStructuredContentPermissionsPage"));
					put(
						"query#contentStructureStructuredContents",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getContentStructureStructuredContentsPage"));
					put(
						"query#structuredContents",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getSiteStructuredContentsPage"));
					put(
						"query#structuredContentByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getSiteStructuredContentByExternalReferenceCode"));
					put(
						"query#structuredContentByKey",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getSiteStructuredContentByKey"));
					put(
						"query#structuredContentByUuid",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getSiteStructuredContentByUuid"));
					put(
						"query#siteStructuredContentPermissions",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getSiteStructuredContentPermissionsPage"));
					put(
						"query#structuredContentFolderStructuredContents",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentFolderStructuredContentsPage"));
					put(
						"query#structuredContent",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContent"));
					put(
						"query#structuredContentMyRating",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentMyRating"));
					put(
						"query#structuredContentPermissions",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentPermissionsPage"));
					put(
						"query#structuredContentRenderedContentByDisplayPageDisplayPageKey",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentRenderedContentByDisplayPageDisplayPageKey"));
					put(
						"query#structuredContentRenderedContentContentTemplate",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentRenderedContentContentTemplate"));
					put(
						"query#assetLibraryStructuredContentFolders",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getAssetLibraryStructuredContentFoldersPage"));
					put(
						"query#assetLibraryStructuredContentFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getAssetLibraryStructuredContentFolderByExternalReferenceCode"));
					put(
						"query#assetLibraryStructuredContentFolderPermissions",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getAssetLibraryStructuredContentFolderPermissionsPage"));
					put(
						"query#structuredContentFolders",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getSiteStructuredContentFoldersPage"));
					put(
						"query#structuredContentFolderByExternalReferenceCode",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getSiteStructuredContentFolderByExternalReferenceCode"));
					put(
						"query#siteStructuredContentFolderPermissions",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getSiteStructuredContentFolderPermissionsPage"));
					put(
						"query#structuredContentFolderPermissions",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getStructuredContentFolderPermissionsPage"));
					put(
						"query#structuredContentFolderStructuredContentFolders",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getStructuredContentFolderStructuredContentFoldersPage"));
					put(
						"query#structuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getStructuredContentFolder"));
					put(
						"query#wikiNodes",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"getSiteWikiNodesPage"));
					put(
						"query#wikiNodeByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"getSiteWikiNodeByExternalReferenceCode"));
					put(
						"query#siteWikiNodePermissions",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"getSiteWikiNodePermissionsPage"));
					put(
						"query#wikiNode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class, "getWikiNode"));
					put(
						"query#wikiNodePermissions",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"getWikiNodePermissionsPage"));
					put(
						"query#wikiPageByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"getSiteWikiPageByExternalReferenceCode"));
					put(
						"query#wikiNodeWikiPages",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"getWikiNodeWikiPagesPage"));
					put(
						"query#wikiPageWikiPages",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"getWikiPageWikiPagesPage"));
					put(
						"query#wikiPage",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class, "getWikiPage"));
					put(
						"query#wikiPagePermissions",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"getWikiPagePermissionsPage"));
					put(
						"query#wikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"getSiteWikiPageByExternalReferenceCodeWikiPageExternalReferenceCodeWikiPageAttachmentByExternalReferenceCode"));
					put(
						"query#wikiPageAttachment",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"getWikiPageAttachment"));
					put(
						"query#wikiPageWikiPageAttachments",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"getWikiPageWikiPageAttachmentsPage"));

					put(
						"query#Document.folder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolder"));
					put(
						"query#MessageBoardThread.messageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getMessageBoardSection"));
					put(
						"query#Document.myRating",
						new ObjectValuePair<>(
							DocumentResourceImpl.class, "getDocumentMyRating"));
					put(
						"query#WikiPage.permissions",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"getWikiPagePermissionsPage"));
					put(
						"query#MessageBoardMessage.messageBoardAttachments",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"getMessageBoardMessageMessageBoardAttachmentsPage"));
					put(
						"query#DocumentFolder.documents",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getDocumentFolderDocumentsPage"));
					put(
						"query#StructuredContentFolder.permissions",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getStructuredContentFolderPermissionsPage"));
					put(
						"query#WikiPage.wikiPageAttachments",
						new ObjectValuePair<>(
							WikiPageAttachmentResourceImpl.class,
							"getWikiPageWikiPageAttachmentsPage"));
					put(
						"query#StructuredContent.renderedContentContentTemplate",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentRenderedContentContentTemplate"));
					put(
						"query#WikiNode.wikiPages",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"getWikiNodeWikiPagesPage"));
					put(
						"query#BlogPosting.permissions",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getBlogPostingPermissionsPage"));
					put(
						"query#DocumentFolder.documentFolders",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolderDocumentFoldersPage"));
					put(
						"query#KnowledgeBaseFolder.knowledgeBaseArticles",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseFolderKnowledgeBaseArticlesPage"));
					put(
						"query#StructuredContent.myRating",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentMyRating"));
					put(
						"query#KnowledgeBaseArticle.knowledgeBaseAttachments",
						new ObjectValuePair<>(
							KnowledgeBaseAttachmentResourceImpl.class,
							"getKnowledgeBaseArticleKnowledgeBaseAttachmentsPage"));
					put(
						"query#StructuredContent.renderedContentByDisplayPageDisplayPageKey",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentRenderedContentByDisplayPageDisplayPageKey"));
					put(
						"query#MessageBoardMessage.myRating",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessageMyRating"));
					put(
						"query#DocumentFolder.myRating",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolderMyRating"));
					put(
						"query#Comment.comments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getCommentCommentsPage"));
					put(
						"query#StructuredContent.contentStructure",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getContentStructure"));
					put(
						"query#StructuredContent.folder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getStructuredContentFolder"));
					put(
						"query#MessageBoardMessage.permissions",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessagePermissionsPage"));
					put(
						"query#KnowledgeBaseArticle.permissions",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticlePermissionsPage"));
					put(
						"query#DocumentFolder.permissions",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolderPermissionsPage"));
					put(
						"query#KnowledgeBaseFolder.permissions",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getKnowledgeBaseFolderPermissionsPage"));
					put(
						"query#WikiPage.wikiNode",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class, "getWikiNode"));
					put(
						"query#KnowledgeBaseArticle.knowledgeBaseArticles",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticleKnowledgeBaseArticlesPage"));
					put(
						"query#MessageBoardThread.messageBoardAttachments",
						new ObjectValuePair<>(
							MessageBoardAttachmentResourceImpl.class,
							"getMessageBoardThreadMessageBoardAttachmentsPage"));
					put(
						"query#NavigationMenu.permissions",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"getNavigationMenuPermissionsPage"));
					put(
						"query#MessageBoardSection.permissions",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getMessageBoardSectionPermissionsPage"));
					put(
						"query#ContentStructure.permissions",
						new ObjectValuePair<>(
							ContentStructureResourceImpl.class,
							"getContentStructurePermissionsPage"));
					put(
						"query#KnowledgeBaseFolder.knowledgeBaseFolders",
						new ObjectValuePair<>(
							KnowledgeBaseFolderResourceImpl.class,
							"getKnowledgeBaseFolderKnowledgeBaseFoldersPage"));
					put(
						"query#ContentStructure.structuredContents",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getContentStructureStructuredContentsPage"));
					put(
						"query#BlogPosting.comments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getBlogPostingCommentsPage"));
					put(
						"query#WikiPage.wikiPages",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class,
							"getWikiPageWikiPagesPage"));
					put(
						"query#StructuredContent.comments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getStructuredContentCommentsPage"));
					put(
						"query#KnowledgeBaseArticle.myRating",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticleMyRating"));
					put(
						"query#MessageBoardMessage.messageBoardThread",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardThread"));
					put(
						"query#BlogPosting.myRating",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getBlogPostingMyRating"));
					put(
						"query#Document.comments",
						new ObjectValuePair<>(
							CommentResourceImpl.class,
							"getDocumentCommentsPage"));
					put(
						"query#StructuredContentFolder.structuredContents",
						new ObjectValuePair<>(
							StructuredContentResourceImpl.class,
							"getStructuredContentFolderStructuredContentsPage"));
					put(
						"query#MessageBoardThread.permissions",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardThreadPermissionsPage"));
					put(
						"query#StructuredContentFolder.structuredContentFolders",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getStructuredContentFolderStructuredContentFoldersPage"));
					put(
						"query#MessageBoardMessage.messageBoardMessages",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessageMessageBoardMessagesPage"));
					put(
						"query#MessageBoardSection.messageBoardSections",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getMessageBoardSectionMessageBoardSectionsPage"));
					put(
						"query#WikiNode.permissions",
						new ObjectValuePair<>(
							WikiNodeResourceImpl.class,
							"getWikiNodePermissionsPage"));
					put(
						"query#Document.renderedContentByDisplayPageDisplayPageKey",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getDocumentRenderedContentByDisplayPageDisplayPageKey"));
					put(
						"query#BlogPosting.renderedContentByDisplayPageDisplayPageKey",
						new ObjectValuePair<>(
							BlogPostingResourceImpl.class,
							"getBlogPostingRenderedContentByDisplayPageDisplayPageKey"));
					put(
						"query#MessageBoardSection.messageBoardThreads",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardSectionMessageBoardThreadsPage"));
					put(
						"query#MessageBoardThread.myRating",
						new ObjectValuePair<>(
							MessageBoardThreadResourceImpl.class,
							"getMessageBoardThreadMyRating"));
					put(
						"query#Document.permissions",
						new ObjectValuePair<>(
							DocumentResourceImpl.class,
							"getDocumentPermissionsPage"));
					put(
						"query#MessageBoardThread.messageBoardMessages",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardThreadMessageBoardMessagesPage"));

					put(
						"query#Comment.parentComment",
						new ObjectValuePair<>(
							CommentResourceImpl.class, "getComment"));
					put(
						"query#DocumentFolder.parentDocumentFolder",
						new ObjectValuePair<>(
							DocumentFolderResourceImpl.class,
							"getDocumentFolder"));
					put(
						"query#KnowledgeBaseArticle.parentKnowledgeBaseArticle",
						new ObjectValuePair<>(
							KnowledgeBaseArticleResourceImpl.class,
							"getKnowledgeBaseArticle"));
					put(
						"query#MessageBoardMessage.parentMessageBoardMessage",
						new ObjectValuePair<>(
							MessageBoardMessageResourceImpl.class,
							"getMessageBoardMessage"));
					put(
						"query#MessageBoardSection.parentMessageBoardSection",
						new ObjectValuePair<>(
							MessageBoardSectionResourceImpl.class,
							"getMessageBoardSection"));
					put(
						"query#NavigationMenuItem.parentNavigationMenuItem",
						new ObjectValuePair<>(
							NavigationMenuResourceImpl.class,
							"getNavigationMenu"));
					put(
						"query#StructuredContentFolder.parentStructuredContentFolder",
						new ObjectValuePair<>(
							StructuredContentFolderResourceImpl.class,
							"getStructuredContentFolder"));
					put(
						"query#WikiPage.parentWikiPage",
						new ObjectValuePair<>(
							WikiPageResourceImpl.class, "getWikiPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<BlogPostingResource>
		_blogPostingResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<BlogPostingImageResource>
		_blogPostingImageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CommentResource>
		_commentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ContentElementResource>
		_contentElementResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ContentStructureResource>
		_contentStructureResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ContentTemplateResource>
		_contentTemplateResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DocumentResource>
		_documentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DocumentDataDefinitionTypeResource>
		_documentDataDefinitionTypeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DocumentFolderResource>
		_documentFolderResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DocumentMetadataSetResource>
		_documentMetadataSetResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<DocumentShortcutResource>
		_documentShortcutResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<KnowledgeBaseArticleResource>
		_knowledgeBaseArticleResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<KnowledgeBaseAttachmentResource>
		_knowledgeBaseAttachmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<KnowledgeBaseFolderResource>
		_knowledgeBaseFolderResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<LanguageResource>
		_languageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<MessageBoardAttachmentResource>
		_messageBoardAttachmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<MessageBoardMessageResource>
		_messageBoardMessageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<MessageBoardSectionResource>
		_messageBoardSectionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<MessageBoardThreadResource>
		_messageBoardThreadResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<NavigationMenuResource>
		_navigationMenuResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SitePageResource>
		_sitePageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<StructuredContentResource>
		_structuredContentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<StructuredContentFolderResource>
		_structuredContentFolderResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WikiNodeResource>
		_wikiNodeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WikiPageResource>
		_wikiPageResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WikiPageAttachmentResource>
		_wikiPageAttachmentResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ContentSetElementResource>
		_contentSetElementResourceComponentServiceObjects;

}