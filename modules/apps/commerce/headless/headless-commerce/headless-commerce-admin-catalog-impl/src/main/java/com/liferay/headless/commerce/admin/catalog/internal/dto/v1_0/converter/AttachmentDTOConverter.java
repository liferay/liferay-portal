/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.account.constants.AccountConstants;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.Attachment;
import com.liferay.headless.commerce.admin.catalog.internal.util.FileEntryUtil;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.custom.field.CustomFieldsUtil;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 * @author Igor Beslic
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.product.model.CPAttachmentFileEntry",
	service = DTOConverter.class
)
public class AttachmentDTOConverter
	implements DTOConverter<CPAttachmentFileEntry, Attachment> {

	@Override
	public String getContentType() {
		return Attachment.class.getSimpleName();
	}

	@Override
	public Attachment toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryService.getCPAttachmentFileEntry(
				(Long)dtoConverterContext.getId());

		FileEntry fileEntry = cpAttachmentFileEntry.fetchFileEntry();

		return new Attachment() {
			{
				setAttachment(
					() -> FileEntryUtil.getBase64EncodedContent(fileEntry));
				setCdnEnabled(cpAttachmentFileEntry::isCDNEnabled);
				setCdnURL(cpAttachmentFileEntry::getCDNURL);
				setCustomFields(
					() -> CustomFieldsUtil.toCustomFields(
						dtoConverterContext.isAcceptAllLanguages(),
						CPAttachmentFileEntry.class.getName(),
						cpAttachmentFileEntry.getCPAttachmentFileEntryId(),
						cpAttachmentFileEntry.getCompanyId(),
						dtoConverterContext.getLocale()));
				setDisplayDate(cpAttachmentFileEntry::getDisplayDate);
				setExpirationDate(cpAttachmentFileEntry::getExpirationDate);
				setExternalReferenceCode(
					cpAttachmentFileEntry::getExternalReferenceCode);
				setFileEntryExternalReferenceCode(
					() -> {
						if (fileEntry == null) {
							return null;
						}

						return fileEntry.getExternalReferenceCode();
					});
				setFileEntryGroupExternalReferenceCode(
					() -> {
						if (fileEntry == null) {
							return null;
						}

						Group group = _groupLocalService.fetchGroup(
							fileEntry.getGroupId());

						if (group == null) {
							return null;
						}

						return group.getExternalReferenceCode();
					});
				setFileEntryId(cpAttachmentFileEntry::getFileEntryId);
				setGalleryEnabled(cpAttachmentFileEntry::isGalleryEnabled);
				setId(cpAttachmentFileEntry::getCPAttachmentFileEntryId);
				setOptions(() -> _getAttachmentOptions(cpAttachmentFileEntry));
				setPriority(cpAttachmentFileEntry::getPriority);
				setSrc(
					() -> {
						Company company = _companyLocalService.getCompany(
							cpAttachmentFileEntry.getCompanyId());

						String portalURL = _portal.getPortalURL(
							company.getVirtualHostname(),
							_portal.getPortalServerPort(false), true);

						String downloadURL =
							_commerceMediaResolver.getDownloadURL(
								AccountConstants.ACCOUNT_ENTRY_ID_ADMIN,
								cpAttachmentFileEntry.
									getCPAttachmentFileEntryId());

						return _getAttachmentDownloadURL(
							cpAttachmentFileEntry, portalURL + downloadURL);
					});
				setTags(
					() -> TransformUtil.transformToArray(
						_assetTagService.getTags(
							cpAttachmentFileEntry.getModelClassName(),
							cpAttachmentFileEntry.getCPAttachmentFileEntryId()),
						AssetTag::getName, String.class));
				setTitle(
					() -> LanguageUtils.getLanguageIdMap(
						cpAttachmentFileEntry.getTitleMap()));
				setType(cpAttachmentFileEntry::getType);
			}
		};
	}

	private String _getAttachmentDownloadURL(
		CPAttachmentFileEntry cpAttachmentFileEntry, String downloadURL) {

		DLFileEntry dlFileEntry = _dlFileEntryLocalService.fetchDLFileEntry(
			cpAttachmentFileEntry.getFileEntryId());

		if (dlFileEntry == null) {
			return downloadURL;
		}

		DLFileEntryType dlFileEntryType =
			_dlFileEntryTypeLocalService.fetchDLFileEntryType(
				dlFileEntry.getFileEntryTypeId());

		if (dlFileEntryType == null) {
			return downloadURL;
		}

		String fileEntryTypeKey = dlFileEntryType.getFileEntryTypeKey();

		if (fileEntryTypeKey.equals(
				CPAttachmentFileEntryConstants.
					DL_VIDEO_EXTERNAL_SHORTCUT_TYPE_KEY)) {

			return StringPool.BLANK;
		}

		return downloadURL;
	}

	private Map<String, String> _getAttachmentOptions(
			CPAttachmentFileEntry cpAttachmentFileEntry)
		throws Exception {

		String json = cpAttachmentFileEntry.getJson();

		if (Validator.isNull(json)) {
			return Collections.emptyMap();
		}

		Map<String, String> options = new HashMap<>();

		JSONArray jsonArray = _jsonFactory.createJSONArray(json);

		for (Object element : jsonArray) {
			JSONObject jsonObject = (JSONObject)element;

			if (!jsonObject.has("key")) {
				continue;
			}

			options.put(
				jsonObject.getString("key"), jsonObject.getString("value"));
		}

		return options;
	}

	@Reference
	private AssetTagService _assetTagService;

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CPAttachmentFileEntryService _cpAttachmentFileEntryService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}