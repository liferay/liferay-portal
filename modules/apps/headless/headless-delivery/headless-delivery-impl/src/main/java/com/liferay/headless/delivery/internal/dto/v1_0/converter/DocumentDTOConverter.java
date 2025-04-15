/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.adaptive.media.AMAttribute;
import com.liferay.adaptive.media.AdaptiveMedia;
import com.liferay.adaptive.media.image.finder.AMImageFinder;
import com.liferay.adaptive.media.image.finder.AMImageQueryBuilder;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.adaptive.media.image.processor.AMImageAttribute;
import com.liferay.adaptive.media.processor.AMProcessor;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetTagLocalService;
import com.liferay.asset.link.service.AssetLinkLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryMetadata;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryMetadataLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.document.library.util.DLFileEntryTypeUtil;
import com.liferay.document.library.util.DLURLHelper;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.dynamic.data.mapping.storage.DDMFormFieldValue;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.storage.DDMStorageEngineManager;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.headless.delivery.dto.v1_0.AdaptedImage;
import com.liferay.headless.delivery.dto.v1_0.ContentField;
import com.liferay.headless.delivery.dto.v1_0.Document;
import com.liferay.headless.delivery.dto.v1_0.DocumentType;
import com.liferay.headless.delivery.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.dto.v1_0.util.ContentFieldUtil;
import com.liferay.headless.delivery.dto.v1_0.util.ContentValueUtil;
import com.liferay.headless.delivery.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.AggregateRatingUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.DisplayPageRendererUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.RelatedContentUtil;
import com.liferay.headless.delivery.internal.dto.v1_0.util.TaxonomyCategoryBriefUtil;
import com.liferay.headless.delivery.internal.resource.v1_0.BaseDocumentResourceImpl;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.journal.service.JournalArticleService;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.comment.CommentManager;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
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

import jakarta.ws.rs.core.UriInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rubén Pulido
 */
@Component(
	property = "dto.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
	service = DTOConverter.class
)
public class DocumentDTOConverter
	implements DTOConverter<DLFileEntry, Document> {

	@Override
	public String getContentType() {
		return Document.class.getSimpleName();
	}

	@Override
	public String getJaxRsLink(long classPK, UriInfo uriInfo) {
		return JaxRsLinkUtil.getJaxRsLink(
			"headless-delivery", BaseDocumentResourceImpl.class, "getDocument",
			uriInfo, classPK);
	}

	@Override
	public Document toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		FileEntry fileEntry = _dlAppService.getFileEntry(
			(Long)dtoConverterContext.getId());

		FileVersion fileVersion = fileEntry.getFileVersion();
		Group group = _groupLocalService.fetchGroup(fileEntry.getGroupId());

		return new Document() {
			{
				setActions(dtoConverterContext::getActions);
				setAdaptedImages(
					() -> _getAdaptiveMedias(dtoConverterContext, fileEntry));
				setAggregateRating(
					() -> AggregateRatingUtil.toAggregateRating(
						_ratingsStatsLocalService.fetchStats(
							DLFileEntry.class.getName(),
							fileEntry.getFileEntryId())));
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setContentUrl(
					() -> {
						if ((fileVersion.getSize() == 0) ||
							!fileEntry.containsPermission(
								PermissionThreadLocal.getPermissionChecker(),
								ActionKeys.DOWNLOAD)) {

							return StringPool.BLANK;
						}

						return _dlURLHelper.getDownloadURL(
							fileEntry, fileVersion, null, StringPool.BLANK);
					});
				setContentValue(
					() -> ContentValueUtil.toContentValue(
						"contentValue", fileEntry::getContentStream,
						dtoConverterContext.getUriInfo()));
				setCreator(
					() -> CreatorUtil.toCreator(
						dtoConverterContext, _portal,
						_userLocalService.fetchUser(fileEntry.getUserId())));
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						DLFileEntry.class.getName(),
						fileVersion.getFileVersionId(),
						fileEntry.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDateCreated(fileEntry::getCreateDate);
				setDateExpired(fileEntry::getExpirationDate);
				setDateModified(fileEntry::getModifiedDate);
				setDatePublished(fileEntry::getDisplayDate);
				setDescription(fileEntry::getDescription);
				setDocumentFolderExternalReferenceCode(
					() -> {
						if (fileEntry.getFolderId() <= 0) {
							return null;
						}

						Folder folder = _dlAppService.getFolder(
							fileEntry.getFolderId());

						return folder.getExternalReferenceCode();
					});
				setDocumentFolderId(fileEntry::getFolderId);
				setDocumentType(
					() -> _toDocumentType(dtoConverterContext, fileVersion));
				setEncodingFormat(fileEntry::getMimeType);
				setExternalReferenceCode(fileEntry::getExternalReferenceCode);
				setFileExtension(fileEntry::getExtension);
				setFileName(fileEntry::getFileName);
				setFriendlyUrlPath(
					() -> {
						FriendlyURLEntry friendlyURLEntry =
							_friendlyURLEntryLocalService.
								fetchMainFriendlyURLEntry(
									_portal.getClassNameId(FileEntry.class),
									fileEntry.getFileEntryId());

						if (friendlyURLEntry == null) {
							return null;
						}

						return friendlyURLEntry.getUrlTitle();
					});
				setId(fileEntry::getFileEntryId);
				setKeywords(
					() -> ListUtil.toArray(
						_assetTagLocalService.getTags(
							DLFileEntry.class.getName(),
							fileEntry.getFileEntryId()),
						AssetTag.NAME_ACCESSOR));
				setNumberOfComments(
					() -> _commentManager.getCommentsCount(
						DLFileEntry.class.getName(),
						fileEntry.getFileEntryId()));
				setRelatedContents(
					() -> RelatedContentUtil.toRelatedContents(
						_assetEntryLocalService, _assetLinkLocalService,
						dtoConverterContext.getDTOConverterRegistry(),
						DLFileEntry.class.getName(), fileEntry.getFileEntryId(),
						dtoConverterContext.getLocale()));
				setRenderedContents(
					() -> DisplayPageRendererUtil.getRenderedContent(
						BaseDocumentResourceImpl.class,
						FileEntry.class.getName(), fileEntry.getFileEntryId(),
						_getDDMStructureId(fileEntry), dtoConverterContext,
						fileEntry.getGroupId(), fileEntry,
						_infoItemServiceRegistry,
						_layoutDisplayPageProviderRegistry, _layoutLocalService,
						_layoutPageTemplateEntryService,
						"getDocumentRenderedContentByDisplayPageDisplayPage" +
							"Key"));
				setSiteId(() -> GroupUtil.getSiteId(group));
				setSizeInBytes(fileEntry::getSize);
				setTaxonomyCategoryBriefs(
					() -> TransformUtil.transformToArray(
						_assetCategoryLocalService.getCategories(
							DLFileEntry.class.getName(),
							fileEntry.getFileEntryId()),
						assetCategory ->
							TaxonomyCategoryBriefUtil.toTaxonomyCategoryBrief(
								assetCategory, dtoConverterContext),
						TaxonomyCategoryBrief.class));
				setTitle(fileEntry::getTitle);
			}
		};
	}

	private AdaptedImage[] _getAdaptiveMedias(
			DTOConverterContext dtoConverterContext, FileEntry fileEntry)
		throws Exception {

		if (!_amImageMimeTypeProvider.isMimeTypeSupported(
				fileEntry.getMimeType())) {

			return new AdaptedImage[0];
		}

		return TransformUtil.transformToArray(
			_amImageFinder.getAdaptiveMedias(
				amImageQueryBuilder -> amImageQueryBuilder.forFileEntry(
					fileEntry
				).withConfigurationStatus(
					AMImageQueryBuilder.ConfigurationStatus.ANY
				).done()),
			adaptiveMedia -> _toAdaptedImage(
				adaptiveMedia, dtoConverterContext.getUriInfo()),
			AdaptedImage.class);
	}

	private List<DDMFormValues> _getDDMFormValues(
			DLFileEntryType dlFileEntryType, DLFileVersion dlFileVersion)
		throws Exception {

		List<DDMFormValues> ddmFormValues = new ArrayList<>();

		for (DDMStructure ddmStructure :
				DLFileEntryTypeUtil.getDDMStructures(dlFileEntryType)) {

			DLFileEntryMetadata dlFileEntryMetadata =
				_dlFileEntryMetadataLocalService.fetchFileEntryMetadata(
					ddmStructure.getStructureId(),
					dlFileVersion.getFileVersionId());

			if (dlFileEntryMetadata == null) {
				continue;
			}

			ddmFormValues.add(
				_ddmStorageEngineManager.getDDMFormValues(
					dlFileEntryMetadata.getDDMStorageId()));
		}

		return ddmFormValues;
	}

	private long _getDDMStructureId(FileEntry fileEntry) throws Exception {
		if (!(fileEntry.getModel() instanceof DLFileEntry)) {
			return 0;
		}

		DLFileEntry dlFileEntry = (DLFileEntry)fileEntry.getModel();

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				dlFileEntry.getFileEntryTypeId());

		if ((dlFileEntryType == null) ||
			(dlFileEntryType.getDataDefinitionId() == 0)) {

			return 0;
		}

		DDMStructure ddmStructure = _ddmStructureService.getStructure(
			dlFileEntryType.getDataDefinitionId());

		return ddmStructure.getStructureId();
	}

	private AdaptedImage _toAdaptedImage(
			AdaptiveMedia<AMProcessor<FileVersion>> adaptiveMedia,
			UriInfo uriInfo)
		throws Exception {

		if (adaptiveMedia == null) {
			return null;
		}

		return new AdaptedImage() {
			{
				setContentUrl(() -> String.valueOf(adaptiveMedia.getURI()));
				setContentValue(
					() -> ContentValueUtil.toContentValue(
						"adaptedImages.contentValue",
						adaptiveMedia::getInputStream, uriInfo));
				setHeight(
					() -> adaptiveMedia.getValue(
						AMImageAttribute.AM_IMAGE_ATTRIBUTE_HEIGHT));
				setResolutionName(
					() -> adaptiveMedia.getValue(
						AMAttribute.getConfigurationUuidAMAttribute()));
				setSizeInBytes(
					() -> adaptiveMedia.getValue(
						AMAttribute.getContentLengthAMAttribute()));
				setWidth(
					() -> adaptiveMedia.getValue(
						AMImageAttribute.AM_IMAGE_ATTRIBUTE_WIDTH));
			}
		};
	}

	private DocumentType _toDocumentType(
			DTOConverterContext dtoConverterContext, FileVersion fileVersion)
		throws Exception {

		if (!(fileVersion.getModel() instanceof DLFileVersion)) {
			return null;
		}

		DLFileVersion dlFileVersion = (DLFileVersion)fileVersion.getModel();

		DLFileEntryType dlFileEntryType = dlFileVersion.getDLFileEntryType();

		List<DDMFormValues> ddmFormValues = _getDDMFormValues(
			dlFileEntryType, dlFileVersion);

		return new DocumentType() {
			{
				setAvailableLanguages(
					() -> {
						Set<Locale> locales = new HashSet<>();

						for (DDMFormValues ddmFormValue : ddmFormValues) {
							locales.addAll(ddmFormValue.getAvailableLocales());
						}

						return LocaleUtil.toW3cLanguageIds(
							locales.toArray(new Locale[0]));
					});
				setContentFields(
					() -> {
						List<DDMFormFieldValue> ddmFormFieldValues =
							new ArrayList<>();

						for (DDMFormValues ddmFormValue : ddmFormValues) {
							ddmFormFieldValues.addAll(
								ddmFormValue.getDDMFormFieldValues());
						}

						return TransformUtil.transformToArray(
							ddmFormFieldValues,
							ddmFormFieldValue ->
								ContentFieldUtil.toContentField(
									ddmFormFieldValue, _dlAppService,
									_dlURLHelper, dtoConverterContext,
									_journalArticleService,
									_layoutLocalService),
							ContentField.class);
					});
				setDescription(
					() -> dlFileEntryType.getDescription(
						dtoConverterContext.getLocale()));
				setDescription_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						dlFileEntryType.getDescriptionMap()));
				setName(
					() -> dlFileEntryType.getName(
						dtoConverterContext.getLocale()));
				setName_i18n(
					() -> LocalizedMapUtil.getI18nMap(
						dtoConverterContext.isAcceptAllLanguages(),
						dlFileEntryType.getNameMap()));
			}
		};
	}

	@Reference
	private AMImageFinder _amImageFinder;

	@Reference
	private AMImageMimeTypeProvider _amImageMimeTypeProvider;

	@Reference
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Reference
	private AssetEntryLocalService _assetEntryLocalService;

	@Reference
	private AssetLinkLocalService _assetLinkLocalService;

	@Reference
	private AssetTagLocalService _assetTagLocalService;

	@Reference
	private CommentManager _commentManager;

	@Reference
	private DDMStorageEngineManager _ddmStorageEngineManager;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLFileEntryMetadataLocalService _dlFileEntryMetadataLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private JournalArticleService _journalArticleService;

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