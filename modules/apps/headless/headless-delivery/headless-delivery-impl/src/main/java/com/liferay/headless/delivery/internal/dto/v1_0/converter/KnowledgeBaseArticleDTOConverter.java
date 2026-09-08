/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.headless.delivery.dto.v1_0.KnowledgeBaseArticle;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.ParentKnowledgeBaseFolderUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RelatedContentUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.service.KBArticleService;
import com.liferay.knowledge.base.service.KBFolderService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.knowledge.base.model.KBArticle"
	},
	service = DTOConverter.class
)
public class KnowledgeBaseArticleDTOConverter
	implements DTOConverter<KBArticle, KnowledgeBaseArticle> {

	@Override
	public String getContentType() {
		return KnowledgeBaseArticle.class.getSimpleName();
	}

	@Override
	public KnowledgeBaseArticle toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		KBArticle kbArticle = _kbArticleService.getLatestKBArticle(
			(Long)dtoConverterContext.getId(),
			WorkflowConstants.STATUS_APPROVED);

		if (kbArticle == null) {
			return null;
		}

		return new KnowledgeBaseArticle() {
			{
				setActions(dtoConverterContext::getActions);
				setAggregateRating(
					() -> AggregateRatingUtil.toAggregateRating(
						_ratingsStatsLocalService.fetchStats(
							KBArticle.class.getName(),
							kbArticle.getResourcePrimKey())));
				setArticleBody(kbArticle::getContent);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(kbArticle.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						KBArticle.class.getName(), kbArticle.getKbArticleId(),
						kbArticle.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(kbArticle::getCreateDate);
				setDateModified(kbArticle::getModifiedDate);
				setDatePublished(kbArticle::getDisplayDate);
				setDescription(kbArticle::getDescription);
				setEncodingFormat(() -> "text/html");
				setExternalReferenceCode(kbArticle::getExternalReferenceCode);
				setFriendlyUrlPath(kbArticle::getUrlTitle);
				setId(kbArticle::getResourcePrimKey);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							KBArticle.class.getName(), kbArticle.getClassPK()),
						AssetTag.NAME_ACCESSOR));
				setNumberOfAttachments(
					() -> {
						List<FileEntry> fileEntries =
							kbArticle.getAttachmentsFileEntries();

						if (fileEntries != null) {
							return fileEntries.size();
						}

						return 0;
					});
				setNumberOfKnowledgeBaseArticles(
					() -> _kbArticleService.getKBArticlesCount(
						kbArticle.getGroupId(), kbArticle.getResourcePrimKey(),
						WorkflowConstants.STATUS_APPROVED));
				setParentKnowledgeBaseArticleId(
					kbArticle::getParentResourcePrimKey);
				setParentKnowledgeBaseFolder(
					() -> {
						if (kbArticle.getKbFolderId() <= 0) {
							return null;
						}

						return ParentKnowledgeBaseFolderUtil.
							toParentKnowledgeBaseFolder(
								_kbFolderService.getKBFolder(
									kbArticle.getKbFolderId()));
					});
				setParentKnowledgeBaseFolderId(kbArticle::getKbFolderId);
				setRelatedContents(
					() -> RelatedContentUtil.toRelatedContents(
						_assetEntryLocalService, _assetLinkLocalService,
						dtoConverterContext.getDTOConverterRegistry(),
						kbArticle.getModelClassName(),
						kbArticle.getResourcePrimKey(),
						dtoConverterContext.getLocale()));
				setSiteId(kbArticle::getGroupId);
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						kbArticle.getCompanyId(),
						dtoConverterContext.getUserId(),
						KBArticle.class.getName(),
						kbArticle.getResourcePrimKey()));
				setTaxonomyCategoryBriefs(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							KBArticle.class.getName(), kbArticle.getClassPK()),
						assetCategory ->
							TaxonomyCategoryBriefUtil.toTaxonomyCategoryBrief(
								assetCategory, dtoConverterContext),
						TaxonomyCategoryBrief.class));
				setTitle(kbArticle::getTitle);
			}
		};
	}

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private KBArticleService _kbArticleService;

	@Reference
	private KBFolderService _kbFolderService;

	@Reference
	private Portal _portal;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}