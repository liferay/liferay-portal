/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.criteria.DownloadFileEntryItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "service.ranking:Integer=100",
	service = ItemSelectorReturnTypeResolver.class
)
public class FileEntryDownloadFileEntryItemSelectorReturnTypeResolver
	implements ItemSelectorReturnTypeResolver
		<DownloadFileEntryItemSelectorReturnType, FileEntry> {

	@Override
	public Class<DownloadFileEntryItemSelectorReturnType>
		getItemSelectorReturnTypeClass() {

		return DownloadFileEntryItemSelectorReturnType.class;
	}

	@Override
	public Class<FileEntry> getModelClass() {
		return FileEntry.class;
	}

	@Override
	public String getValue(FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		Group group = _groupLocalService.getGroup(fileEntry.getGroupId());

		return JSONUtil.put(
			"classNameId", _portal.getClassNameId(FileEntry.class)
		).put(
			"externalReferenceCode", fileEntry.getExternalReferenceCode()
		).put(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId())
		).put(
			"groupExternalReferenceCode", group.getExternalReferenceCode()
		).put(
			"groupId", String.valueOf(fileEntry.getGroupId())
		).put(
			"title", fileEntry.getTitle()
		).put(
			"type", "document"
		).put(
			"url",
			_dlURLHelper.getDownloadURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK, false, false)
		).put(
			"uuid", fileEntry.getUuid()
		).toString();
	}

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

}