/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.item.selector.web.internal;

import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.item.selector.criteria.URLItemSelectorReturnType;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepositoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.util.RepositoryUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = "service.ranking:Integer=100",
	service = ItemSelectorReturnTypeResolver.class
)
public class FileEntryURLItemSelectorReturnTypeResolver
	implements ItemSelectorReturnTypeResolver
		<URLItemSelectorReturnType, FileEntry> {

	@Override
	public Class<URLItemSelectorReturnType> getItemSelectorReturnTypeClass() {
		return URLItemSelectorReturnType.class;
	}

	@Override
	public Class<FileEntry> getModelClass() {
		return FileEntry.class;
	}

	@Override
	public String getValue(FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		long repositoryId = fileEntry.getRepositoryId();

		if (RepositoryUtil.isExternalRepository(repositoryId) ||
			(fileEntry.getGroupId() == repositoryId)) {

			return _dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK, false, false);
		}

		return PortletFileRepositoryUtil.getPortletFileEntryURL(
			themeDisplay, fileEntry, StringPool.BLANK, false);
	}

	@Reference
	private DLURLHelper _dlURLHelper;

}