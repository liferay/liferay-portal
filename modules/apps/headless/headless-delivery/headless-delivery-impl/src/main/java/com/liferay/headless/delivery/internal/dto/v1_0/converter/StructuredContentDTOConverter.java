/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMTemplateLocalService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.headless.delivery.dto.v1_0.ContentField;
import com.liferay.headless.delivery.dto.v1_0.RenderedContent;
import com.liferay.headless.delivery.dto.v1_0.StructuredContent;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.dto.v1_0.util.ContentFieldUtil;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.DisplayPageRendererUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RelatedContentUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RenderedContentValueUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.headless.delivery.internal.resource.v1_0.BaseStructuredContentResourceImpl;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.journal.util.JournalContent;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;
import com.liferay.portal.vulcan.util.JaxRsLinkUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.ratings.kernel.service.RatingsStatsLocalService;
import com.liferay.subscription.service.SubscriptionLocalService;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 * @author Víctor Galán
 */
@Component(
	property = "dto.class.name=com.liferay.journal.model.JournalArticle",
	service = DTOConverter.class
)
public class StructuredContentDTOConverter
	implements DTOConverter<JournalArticle, StructuredContent> {

	@Override
	public String getContentType() {
		return StructuredContent.class.getSimpleName();
	}

	@Override
	public String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return JaxRsLinkUtil.getJaxRsLink(
			"headless-delivery", BaseStructuredContentResourceImpl.class,
			"getStructuredContent", uriInfo, classPK);
	}

	@Override
	public StructuredContent toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		JournalArticle journalArticle = _journalArticleService.getLatestArticle(
			(Long)dtoConverterContext.getId());

		return toDTO(dtoConverterContext, journalArticle);
	}

	@Override
	public StructuredContent toDTO(
			DTOConverterContext dtoConverterContext,
			JournalArticle journalArticle)
		throws Exception {

		DDMStructure ddmStructure = journalArticle.getDDMStructure();

		Group group = _groupLocalService.fetchGroup(
			journalArticle.getGroupId());

		return new StructuredContent() {
			{
				setActions(dtoConverterContext::getActions);
				setAggregateRating(
					() -> AggregateRatingUtil.toAggregateRating(
						_ratingsStatsLocalService.fetchStats(
							JournalArticle.class.getName(),
							journalArticle.getResourcePrimKey())));
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setAvailableLanguages(
					() -> LocaleUtil.toW3cLanguageIds(
						journalArticle.getAvailableLanguageIds()));
				setContentFields(
					() -> _toContentFields(
						_dlAppService, _dlURLHelper, dtoConverterContext,
						journalArticle, _journalArticleService,
						_layoutLocalService));
				setContentStructureId(ddmStructure::getStructureId);
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(
							journalArticle.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						JournalArticle.class.getName(), journalArticle.getId(),
						journalArticle.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(journalArticle::getCreateDate);
				setDateExpired(journalArticle::getExpirationDate);
				setDateModified(journalArticle::getModifiedDate);
				setDatePublished(journalArticle::getDisplayDate);
				setDescription(
					() -> journalArticle.getDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						_filterDescriptionMap(
							journalArticle.getDescriptionMap())));
				setExternalReferenceCode(
					journalArticle::getExternalReferenceCode);
				setFriendlyUrlPath(
					() -> journalArticle.getUrlTitle(
						dtoConverterContext.getLocale()));
				setFriendlyUrlPath_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						journalArticle.getFriendlyURLMap()));
				setId(journalArticle::getResourcePrimKey);
				setKey(journalArticle::getArticleId);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							JournalArticle.class.getName(),
							journalArticle.getResourcePrimKey()),
						AssetTag.NAME_ACCESSOR));
				setNeverExpire(
					() -> {
						if (journalArticle.getExpirationDate() == null) {
							return true;
						}

						return false;
					});
				setNumberOfComments(
					() -> _commentManager.getCommentsCount(
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()));
				setPriority(
					() -> {
						AssetEntry assetEntry =
							_assetEntryLocalService.fetchEntry(
								journalArticle.getModelClassName(),
								journalArticle.getResourcePrimKey());

						if (assetEntry == null) {
							return null;
						}

						return assetEntry.getPriority();
					});
				setRelatedContents(
					() -> RelatedContentUtil.toRelatedContents(
						_assetEntryLocalService, _assetLinkLocalService,
						dtoConverterContext.getDTOConverterRegistry(),
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey(),
						dtoConverterContext.getLocale()));
				setRenderedContents(
					() -> _toRenderedContents(
						ddmStructure, dtoConverterContext, journalArticle));
				setSiteId(() -> GroupUtil.getSiteId(group));
				setStructuredContentFolderId(journalArticle::getFolderId);
				setSubscribed(
					() -> _subscriptionLocalService.isSubscribed(
						journalArticle.getCompanyId(),
						dtoConverterContext.getUserId(),
						JournalArticle.class.getName(),
						journalArticle.getResourcePrimKey()));
				setTaxonomyCategoryBriefs(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							JournalArticle.class.getName(),
							journalArticle.getResourcePrimKey()),
						assetCategory ->
							TaxonomyCategoryBriefUtil.toTaxonomyCategoryBrief(
								assetCategory, dtoConverterContext),
						TaxonomyCategoryBrief.class));
				setTitle(
					() -> journalArticle.getTitle(
						dtoConverterContext.getLocale()));
				setTitle_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						journalArticle.getTitleMap()));
				setUuid(journalArticle::getUuid);
			}
		};
	}

	private Map<Locale, String> _filterDescriptionMap(
		Map<Locale, String> descriptionMap) {

		Map<Locale, String> filterDescriptionMap = new HashMap<>();

		for (Map.Entry<Locale, String> entry : descriptionMap.entrySet()) {
			if (StringPool.BLANK.equals(entry.getValue())) {
				continue;
			}

			filterDescriptionMap.put(entry.getKey(), entry.getValue());
		}

		return filterDescriptionMap;
	}

	private ContentField[] _toContentFields(
			DLAppService dlAppService, DLURLHelper dlURLHelper,
			DTOConverterContext dtoConverterContext,
			JournalArticle journalArticle,
			JournalArticleService journalArticleService,
			LayoutLocalService layoutLocalService)
		throws Exception {

		DDMFormValues ddmFormValues = journalArticle.getDDMFormValues();

		return TransformUtil.transformToArray(
			ddmFormValues.getDDMFormFieldValues(),
			ddmFormFieldValue -> ContentFieldUtil.toContentField(
				ddmFormFieldValue, dlAppService, dlURLHelper,
				dtoConverterContext, journalArticleService, layoutLocalService),
			ContentField.class);
	}

	private RenderedContent[] _toRenderedContents(
		DDMStructure ddmStructure, DTOConverterContext dtoConverterContext,
		JournalArticle journalArticle) {

		UriInfo uriInfo = dtoConverterContext.getUriInfo();

		if (uriInfo == null) {
			return null;
		}

		boolean acceptAllLanguages = dtoConverterContext.isAcceptAllLanguages();
		HttpServletRequest httpServletRequest =
			dtoConverterContext.getHttpServletRequest();
		Locale locale = dtoConverterContext.getLocale();

		RenderedContent[] renderedContents = TransformUtil.transformToArray(
			ddmStructure.getTemplates(),
			ddmTemplate -> new RenderedContent() {
				{
					setContentTemplateId(ddmTemplate::getTemplateKey);
					setContentTemplateName(() -> ddmTemplate.getName(locale));
					setContentTemplateName_i18n(
						() -> LocalizedMapUtil.getI18nMap(
							acceptAllLanguages, ddmTemplate.getNameMap()));
					setMarkedAsDefault(
						() -> Objects.equals(
							ddmTemplate.getTemplateKey(),
							journalArticle.getDDMTemplateKey()));
					setRenderedContentURL(
						() -> JaxRsLinkUtil.getJaxRsLink(
							"headless-delivery",
							BaseStructuredContentResourceImpl.class,
							"getStructuredContentRenderedContentContent" +
								"Template",
							uriInfo, journalArticle.getResourcePrimKey(),
							ddmTemplate.getTemplateKey()));
					setRenderedContentValue(
						() -> {
							if (!dtoConverterContext.containsNestedFieldsValue(
									"renderedContentValue")) {

								return null;
							}

							return RenderedContentValueUtil.renderTemplate(
								_classNameLocalService,
								_ddmTemplateLocalService, _groupLocalService,
								httpServletRequest, _journalArticleService,
								_journalContent, locale,
								journalArticle.getResourcePrimKey(),
								ddmTemplate.getTemplateKey(), uriInfo);
						});
				}
			},
			RenderedContent.class);

		RenderedContent[] displayPagesRenderedContents =
			DisplayPageRendererUtil.getRenderedContent(
				BaseStructuredContentResourceImpl.class,
				JournalArticle.class.getName(),
				journalArticle.getResourcePrimKey(),
				ddmStructure.getStructureId(), dtoConverterContext,
				journalArticle.getGroupId(), journalArticle,
				_infoItemServiceRegistry, _layoutDisplayPageProviderRegistry,
				_layoutLocalService, _layoutPageTemplateEntryService,
				"getStructuredContentRenderedContentByDisplayPageDisplayPage" +
					"Key");

		return ArrayUtil.append(renderedContents, displayPagesRenderedContents);
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
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DDMTemplateLocalService _ddmTemplateLocalService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JournalArticleService _journalArticleService;

	@Reference
	private JournalContent _journalContent;

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
	private SubscriptionLocalService _subscriptionLocalService;

	@Reference
	private UserLocalService _userLocalService;

}