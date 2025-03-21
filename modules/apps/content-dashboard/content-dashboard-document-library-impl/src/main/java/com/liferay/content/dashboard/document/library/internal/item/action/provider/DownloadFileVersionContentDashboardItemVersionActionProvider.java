/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.item.action.provider;

import com.liferay.content.dashboard.document.library.internal.item.action.DownloadFileVersionContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.ContentDashboardItemVersionAction;
import com.liferay.content.dashboard.item.action.provider.ContentDashboardItemVersionActionProvider;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemFieldValuesProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = "service.ranking:Integer=300",
	service = ContentDashboardItemVersionActionProvider.class
)
public class DownloadFileVersionContentDashboardItemVersionActionProvider
	implements ContentDashboardItemVersionActionProvider<FileVersion> {

	@Override
	public ContentDashboardItemVersionAction
		getContentDashboardItemVersionAction(
			FileVersion fileVersion, HttpServletRequest httpServletRequest) {

		if (!isShow(fileVersion, httpServletRequest)) {
			return null;
		}

		FileEntry fileEntry = _getFileEntry(fileVersion);

		if (fileEntry == null) {
			return null;
		}

		return _getContentDashboardItemVersionAction(fileEntry);
	}

	@Override
	public boolean isShow(
		FileVersion fileVersion, HttpServletRequest httpServletRequest) {

		FileEntry fileEntry = _getFileEntry(fileVersion);

		if ((fileEntry == null) ||
			Objects.equals(
				fileEntry.getMimeType(),
				ContentTypes.
					APPLICATION_VND_LIFERAY_VIDEO_EXTERNAL_SHORTCUT_HTML)) {

			return false;
		}

		ContentDashboardItemVersionAction contentDashboardItemVersionAction =
			_getContentDashboardItemVersionAction(fileEntry);

		return Validator.isNotNull(contentDashboardItemVersionAction.getURL());
	}

	private ContentDashboardItemVersionAction
		_getContentDashboardItemVersionAction(FileEntry fileEntry) {

		InfoItemFieldValuesProvider<FileEntry> infoItemFieldValuesProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemFieldValuesProvider.class, FileEntry.class.getName());

		return new DownloadFileVersionContentDashboardItemVersionAction(
			fileEntry, infoItemFieldValuesProvider, _language);
	}

	private FileEntry _getFileEntry(FileVersion fileVersion) {
		try {
			return fileVersion.getFileEntry();
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DownloadFileVersionContentDashboardItemVersionActionProvider.class);

	@Reference
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@Reference
	private Language _language;

}