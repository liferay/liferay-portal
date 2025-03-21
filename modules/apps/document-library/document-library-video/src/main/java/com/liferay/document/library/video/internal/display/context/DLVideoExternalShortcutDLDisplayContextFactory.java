/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.display.context;

import com.liferay.document.library.display.context.BaseDLDisplayContextFactory;
import com.liferay.document.library.display.context.DLDisplayContextFactory;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.resolver.DLVideoExternalShortcutResolver;
import com.liferay.document.library.video.internal.constants.DLVideoConstants;
import com.liferay.document.library.video.internal.helper.DLVideoExternalShortcutMetadataHelper;
import com.liferay.document.library.video.internal.helper.DLVideoExternalShortcutMetadataHelperFactory;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Alejandro Tardín
 */
@Component(
	property = "service.ranking:Integer=-100",
	service = DLDisplayContextFactory.class
)
public class DLVideoExternalShortcutDLDisplayContextFactory
	extends BaseDLDisplayContextFactory {

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DLFileEntryType dlFileEntryType) {

		DDMStructure dlVideoExternalShortcutDDMStructure =
			DLVideoExternalShortcutMetadataHelper.
				getDLVideoExternalShortcutDDMStructure(dlFileEntryType);

		if (dlVideoExternalShortcutDDMStructure != null) {
			return new DLVideoExternalShortcutDLEditFileEntryDisplayContext(
				parentDLEditFileEntryDisplayContext, httpServletRequest,
				httpServletResponse, dlFileEntryType, _servletContext);
		}

		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileEntry fileEntry) {

		DLVideoExternalShortcutMetadataHelper
			dlVideoExternalShortcutMetadataHelper =
				_dlVideoExternalShortcutMetadataHelperFactory.
					getDLVideoExternalShortcutMetadataHelper(fileEntry);

		if ((dlVideoExternalShortcutMetadataHelper != null) &&
			dlVideoExternalShortcutMetadataHelper.isExternalShortcut()) {

			return new DLVideoExternalShortcutDLEditFileEntryDisplayContext(
				parentDLEditFileEntryDisplayContext, httpServletRequest,
				httpServletResponse,
				_getDLVideoExternalShortcut(
					dlVideoExternalShortcutMetadataHelper),
				fileEntry, _servletContext);
		}

		return parentDLEditFileEntryDisplayContext;
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileShortcut fileShortcut) {

		try {
			long fileEntryId = fileShortcut.getToFileEntryId();

			FileEntry fileEntry = _dlAppService.getFileEntry(fileEntryId);

			return getDLViewFileVersionDisplayContext(
				parentDLViewFileVersionDisplayContext, httpServletRequest,
				httpServletResponse, fileEntry.getFileVersion());
		}
		catch (PortalException portalException) {
			throw new SystemException(
				"Unable to build document library view file version display " +
					"context for file shortcut " + fileShortcut.getPrimaryKey(),
				portalException);
		}
	}

	@Override
	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		DLViewFileVersionDisplayContext parentDLViewFileVersionDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		DLVideoExternalShortcutMetadataHelper
			dlVideoExternalShortcutMetadataHelper =
				_dlVideoExternalShortcutMetadataHelperFactory.
					getDLVideoExternalShortcutMetadataHelper(fileVersion);

		if ((dlVideoExternalShortcutMetadataHelper != null) &&
			dlVideoExternalShortcutMetadataHelper.isExternalShortcut()) {

			return new DLVideoExternalShortcutDLViewFileVersionDisplayContext(
				parentDLViewFileVersionDisplayContext, httpServletRequest,
				httpServletResponse, fileVersion);
		}

		return parentDLViewFileVersionDisplayContext;
	}

	private DLVideoExternalShortcut _getDLVideoExternalShortcut(
		DLVideoExternalShortcutMetadataHelper
			dlVideoExternalShortcutMetadataHelper) {

		if (!dlVideoExternalShortcutMetadataHelper.containsField(
				DLVideoConstants.DDM_FIELD_NAME_URL)) {

			return null;
		}

		return _dlVideoExternalShortcutResolver.resolve(
			dlVideoExternalShortcutMetadataHelper.getFieldValue(
				DLVideoConstants.DDM_FIELD_NAME_URL));
	}

	@Reference
	private DLAppService _dlAppService;

	@Reference
	private DLVideoExternalShortcutMetadataHelperFactory
		_dlVideoExternalShortcutMetadataHelperFactory;

	@Reference
	private DLVideoExternalShortcutResolver _dlVideoExternalShortcutResolver;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.video)"
	)
	private ServletContext _servletContext;

}