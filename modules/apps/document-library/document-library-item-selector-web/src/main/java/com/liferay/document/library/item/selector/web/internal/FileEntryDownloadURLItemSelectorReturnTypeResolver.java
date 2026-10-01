/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.criteria.DownloadURLItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "service.ranking:Integer=100",
	service = ItemSelectorReturnTypeResolver.class
)
public class FileEntryDownloadURLItemSelectorReturnTypeResolver
	implements ItemSelectorReturnTypeResolver
		<DownloadURLItemSelectorReturnType, FileEntry> {

	@Override
	public Class<DownloadURLItemSelectorReturnType>
		getItemSelectorReturnTypeClass() {

		return DownloadURLItemSelectorReturnType.class;
	}

	@Override
	public Class<FileEntry> getModelClass() {
		return FileEntry.class;
	}

	@Override
	public String getValue(FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		return _dlURLHelper.getDownloadURL(
			fileEntry, fileEntry.getFileVersion(), themeDisplay,
			StringPool.BLANK, false, false);
	}

	@Reference
	private DLURLHelper _dlURLHelper;

}