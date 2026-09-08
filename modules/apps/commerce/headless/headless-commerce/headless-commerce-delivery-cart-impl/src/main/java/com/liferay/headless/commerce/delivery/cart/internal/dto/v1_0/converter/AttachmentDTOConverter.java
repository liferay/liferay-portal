/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.internal.dto.v1_0.converter;

import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.headless.commerce.delivery.cart.dto.v1_0.Attachment;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.headless.commerce.delivery.cart.dto.v1_0.Attachment"
	},
	service = DTOConverter.class
)
public class AttachmentDTOConverter
	implements DTOConverter<FileEntry, Attachment> {

	@Override
	public String getContentType() {
		return Attachment.class.getSimpleName();
	}

	@Override
	public Attachment toDTO(
			DTOConverterContext dtoConverterContext, FileEntry fileEntry)
		throws Exception {

		if (fileEntry == null) {
			return null;
		}

		return new Attachment() {
			{
				setExternalReferenceCode(fileEntry::getExternalReferenceCode);
				setId(fileEntry::getFileEntryId);
				setTitle(fileEntry::getTitle);
				setUrl(
					() -> DLURLHelperUtil.getDownloadURL(
						fileEntry, fileEntry.getLatestFileVersion(), null,
						StringPool.BLANK, true, true));
			}
		};
	}

}