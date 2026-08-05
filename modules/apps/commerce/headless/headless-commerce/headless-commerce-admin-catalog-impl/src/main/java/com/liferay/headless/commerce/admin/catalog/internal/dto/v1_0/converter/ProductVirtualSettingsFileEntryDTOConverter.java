/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.internal.dto.v1_0.converter;

import com.liferay.account.constants.AccountConstants;
import com.liferay.commerce.media.CommerceMediaResolver;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry;
import com.liferay.commerce.product.type.virtual.model.CPDefinitionVirtualSetting;
import com.liferay.commerce.product.type.virtual.service.CPDVirtualSettingFileEntryLocalService;
import com.liferay.commerce.product.type.virtual.service.CPDefinitionVirtualSettingLocalService;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.internal.util.FileEntryUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = "dto.class.name=com.liferay.commerce.product.type.virtual.model.CPDVirtualSettingFileEntry",
	service = DTOConverter.class
)
public class ProductVirtualSettingsFileEntryDTOConverter
	implements DTOConverter
		<CPDVirtualSettingFileEntry, ProductVirtualSettingsFileEntry> {

	@Override
	public String getContentType() {
		return ProductVirtualSettingsFileEntry.class.getSimpleName();
	}

	@Override
	public ProductVirtualSettingsFileEntry toDTO(
			DTOConverterContext dtoConverterContext)
		throws Exception {

		CPDVirtualSettingFileEntry cpdVirtualSettingFileEntry =
			_cpdVirtualSettingFileEntryLocalService.
				getCPDVirtualSettingFileEntry(
					(Long)dtoConverterContext.getId());

		return new ProductVirtualSettingsFileEntry() {
			{
				setActions(dtoConverterContext::getActions);
				setAttachment(
					() -> FileEntryUtil.getBase64EncodedContent(
						_dlAppLocalService.fetchFileEntry(
							cpdVirtualSettingFileEntry.getFileEntryId())));
				setId(
					cpdVirtualSettingFileEntry::
						getCPDefinitionVirtualSettingFileEntryId);
				setSrc(
					() -> {
						long fileEntryId =
							cpdVirtualSettingFileEntry.getFileEntryId();

						if (fileEntryId == 0) {
							return null;
						}

						CPDefinitionVirtualSetting cpDefinitionVirtualSetting =
							_cpDefinitionVirtualSettingLocalService.
								getCPDefinitionVirtualSetting(
									cpdVirtualSettingFileEntry.
										getCPDefinitionVirtualSettingId());

						return _commerceMediaResolver.
							getDownloadVirtualProductURL(
								CPDefinition.class.getName(),
								cpDefinitionVirtualSetting.getClassPK(),
								AccountConstants.ACCOUNT_ENTRY_ID_ADMIN,
								fileEntryId);
					});
				setUrl(
					() -> {
						if (Validator.isBlank(
								cpdVirtualSettingFileEntry.getUrl())) {

							return null;
						}

						return cpdVirtualSettingFileEntry.getUrl();
					});
				setVersion(
					() -> {
						if (Validator.isBlank(
								cpdVirtualSettingFileEntry.getVersion())) {

							return null;
						}

						return cpdVirtualSettingFileEntry.getVersion();
					});
			}
		};
	}

	@Reference
	private CommerceMediaResolver _commerceMediaResolver;

	@Reference
	private CPDefinitionVirtualSettingLocalService
		_cpDefinitionVirtualSettingLocalService;

	@Reference
	private CPDVirtualSettingFileEntryLocalService
		_cpdVirtualSettingFileEntryLocalService;

	@Reference
	private DLAppLocalService _dlAppLocalService;

}