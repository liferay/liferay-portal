/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.internal.dto.v1_0.converter;

import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagService;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.constants.CPAttachmentFileEntryConstants;
import com.liferay.commerce.product.model.CPAttachmentFileEntry;
import com.liferay.commerce.product.service.CPAttachmentFileEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.kernel.service.DLFileEntryTypeLocalService;
import com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Attachment;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalService;
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
 * @author Andrea Sbarra
 */
@Component(
	property = {"default=true", "dto.class.name=CPAttachmentFileEntry"},
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

		AttachmentDTOConverterContext attachmentDTOConverterContext =
			(AttachmentDTOConverterContext)dtoConverterContext;

		CPAttachmentFileEntry cpAttachmentFileEntry =
			_cpAttachmentFileEntryLocalService.getCPAttachmentFileEntry(
				(Long)attachmentDTOConverterContext.getId());

		return new Attachment() {
			{
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
								attachmentDTOConverterContext.
									getCommerceAccountId(),
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
					() -> cpAttachmentFileEntry.getTitle(
						_language.getLanguageId(
							attachmentDTOConverterContext.getLocale())));
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
	private CPAttachmentFileEntryLocalService
		_cpAttachmentFileEntryLocalService;

	@Reference
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Reference
	private DLFileEntryTypeLocalService _dlFileEntryTypeLocalService;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}