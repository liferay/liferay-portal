/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.item.selector.internal;

import com.liferay.adaptive.media.image.item.selector.AMImageFileEntryItemSelectorReturnType;
import com.liferay.document.library.helper.DLURLHelper;
import com.liferay.item.selector.ItemSelectorReturnTypeResolver;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
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
public class FileEntryAMImageFileEntryItemSelectorReturnTypeResolver
	implements ItemSelectorReturnTypeResolver
		<AMImageFileEntryItemSelectorReturnType, FileEntry> {

	@Override
	public Class<AMImageFileEntryItemSelectorReturnType>
		getItemSelectorReturnTypeClass() {

		return AMImageFileEntryItemSelectorReturnType.class;
	}

	@Override
	public Class<FileEntry> getModelClass() {
		return FileEntry.class;
	}

	@Override
	public String getValue(FileEntry fileEntry, ThemeDisplay themeDisplay)
		throws Exception {

		String previewURL = null;

		long repositoryId = fileEntry.getRepositoryId();

		if (RepositoryUtil.isExternalRepository(repositoryId) ||
			(fileEntry.getGroupId() == repositoryId)) {

			previewURL = _dlURLHelper.getPreviewURL(
				fileEntry, fileEntry.getFileVersion(), themeDisplay,
				StringPool.BLANK, false, false);
		}
		else {
			previewURL = _portletFileRepository.getPortletFileEntryURL(
				themeDisplay, fileEntry, StringPool.BLANK, false);
		}

		return JSONUtil.put(
			"fileEntryId", String.valueOf(fileEntry.getFileEntryId())
		).put(
			"url", previewURL
		).toString();
	}

	@Reference
	private DLURLHelper _dlURLHelper;

	@Reference
	private PortletFileRepository _portletFileRepository;

}