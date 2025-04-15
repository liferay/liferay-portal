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
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.headless.delivery.dto.v1_0.BlogPosting;
import com.liferay.headless.delivery.dto.v1_0.Image;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.dto.v1_0.util.ContentValueUtil;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.DisplayPageRendererUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RelatedContentUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.headless.delivery.internal.resource.v1_0.BaseBlogPostingResourceImpl;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;

import jakarta.ws.rs.core.UriInfo;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = "dto.class.name=com.liferay.blogs.model.BlogsEntry",
	service = DTOConverter.class
)
public class BlogPostingDTOConverter
	implements DTOConverter<BlogsEntry, BlogPosting> {

	@Override
	public String getContentType() {
		return BlogPosting.class.getSimpleName();
	}

	@Override
	public String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return JaxRsLinkUtil.getJaxRsLink(
			"headless-delivery", BaseBlogPostingResourceImpl.class,
			"getBlogPosting", uriInfo, classPK);
	}

	@Override
	public BlogPosting toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		BlogsEntry blogsEntry = _blogsEntryService.getEntry(
			(Long)dtoConverterContext.getId());

		return new BlogPosting() {
			{
				setActions(dtoConverterContext::getActions);
				setAggregateRating(
					() -> AggregateRatingUtil.toAggregateRating(
						_ratingsStatsLocalService.fetchStats(
							BlogsEntry.class.getName(),
							blogsEntry.getEntryId())));
				setAlternativeHeadline(blogsEntry::getSubtitle);
				setArticleBody(blogsEntry::getContent);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(blogsEntry.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						BlogsEntry.class.getName(), blogsEntry.getEntryId(),
						blogsEntry.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(blogsEntry::getCreateDate);
				setDateModified(blogsEntry::getModifiedDate);
				setDatePublished(blogsEntry::getDisplayDate);
				setDescription(
					() -> {
						String description = blogsEntry.getDescription();

						if (Validator.isNotNull(description)) {
							return description;
						}

						return HtmlUtil.stripHtml(
							StringUtil.shorten(
								blogsEntry.getContent(),
								PropsValues.BLOGS_PAGE_ABSTRACT_LENGTH));
					});
				setEncodingFormat(() -> "text/html");
				setExternalReferenceCode(blogsEntry::getExternalReferenceCode);
				setFriendlyUrlPath(blogsEntry::getUrlTitle);
				setHeadline(blogsEntry::getTitle);
				setId(blogsEntry::getEntryId);
				setImage(() -> _getImage(blogsEntry, dtoConverterContext));
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							BlogsEntry.class.getName(),
							blogsEntry.getEntryId()),
						AssetTag.NAME_ACCESSOR));
				setNumberOfComments(
					() -> _commentManager.getCommentsCount(
						BlogsEntry.class.getName(), blogsEntry.getEntryId()));
				setRelatedContents(
					() -> RelatedContentUtil.toRelatedContents(
						_assetEntryLocalService, _assetLinkLocalService,
						dtoConverterContext.getDTOConverterRegistry(),
						blogsEntry.getModelClassName(), blogsEntry.getEntryId(),
						dtoConverterContext.getLocale()));
				setRenderedContents(
					() -> DisplayPageRendererUtil.getRenderedContent(
						BaseBlogPostingResourceImpl.class,
						BlogsEntry.class.getName(), blogsEntry.getEntryId(), 0,
						dtoConverterContext, blogsEntry.getGroupId(),
						blogsEntry, _infoItemServiceRegistry,
						_layoutDisplayPageProviderRegistry, _layoutLocalService,
						_layoutPageTemplateEntryService,
						"getBlogPostingRenderedContentByDisplayPageDisplay" +
							"PageKey"));
				setSiteId(blogsEntry::getGroupId);
				setTaxonomyCategoryBriefs(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							BlogsEntry.class.getName(),
							blogsEntry.getEntryId()),
						assetCategory ->
							TaxonomyCategoryBriefUtil.toTaxonomyCategoryBrief(
								assetCategory, dtoConverterContext),
						TaxonomyCategoryBrief.class));
				setViewableBy(() -> ViewableBy.ANYONE);
			}
		};
	}

	private Image _getImage(
			BlogsEntry blogsEntry, DTOConverterContext dtoConverterContext)
		throws Exception {

		long coverImageFileEntryId = blogsEntry.getCoverImageFileEntryId();

		if (coverImageFileEntryId == 0) {
			return null;
		}

		FileEntry fileEntry = _dlAppService.getFileEntry(coverImageFileEntryId);

		return new Image() {
			{
				setCaption(blogsEntry::getCoverImageCaption);
				setContentUrl(
					() -> _dlURLHelper.getPreviewURL(
						fileEntry, fileEntry.getFileVersion(), null, "", false,
						false));
				setContentValue(
					() -> ContentValueUtil.toContentValue(
						"image.contentValue", fileEntry::getContentStream,
						dtoConverterContext.getUriInfo()));
				setImageId(() -> coverImageFileEntryId);
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
	private BlogsEntryService _blogsEntryService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private LayoutDisplayPageProviderRegistry
		_layoutDisplayPageProviderRegistry;

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private RatingsStatsLocalService _ratingsStatsLocalService;

	@Reference
	private UserLocalService _userLocalService;

}