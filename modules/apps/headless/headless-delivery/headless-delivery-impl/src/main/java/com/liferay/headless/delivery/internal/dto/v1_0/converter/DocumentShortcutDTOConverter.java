/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.internal.dto.v1_0.converter;

import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.headless.delivery.dto.v1_0.DocumentShortcut;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.vulcan.util.GroupUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sam Ziemer
 */
@Component(
	property = {
		"default=true",
		"dto.class.name=com.liferay.document.library.kernel.model.DLFileShortcut"
	},
	service = DTOConverter.class
)
public class DocumentShortcutDTOConverter
	implements DTOConverter<DLFileShortcut, DocumentShortcut> {

	@Override
	public String getContentType() {
		return DLFileShortcut.class.getSimpleName();
	}

	@Override
	public DocumentShortcut toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		FileShortcut fileShortcut = _dlAppService.getFileShortcut(
			(Long)dtoConverterContext.getId());

		Group group = _groupLocalService.getGroup(fileShortcut.getGroupId());

		return new DocumentShortcut() {
			{
				setActions(dtoConverterContext::getActions);
				setAssetLibraryKey(() -> GroupUtil.getAssetLibraryKey(group));
				setDateCreated(fileShortcut::getCreateDate);
				setDateModified(fileShortcut::getModifiedDate);
				setExternalReferenceCode(
					fileShortcut::getExternalReferenceCode);
				setFolderId(fileShortcut::getFolderId);
				setId(fileShortcut::getFileShortcutId);
				setSiteId(fileShortcut::getGroupId);
				setTargetDocumentId(fileShortcut::getToFileEntryId);
				setTitle(fileShortcut::getToTitle);
			}
		};
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private GroupLocalService _groupLocalService;

}