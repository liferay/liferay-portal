/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.display.context;

import com.liferay.document.library.display.context.BaseDLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLEditFileEntryDisplayContext;
import com.liferay.document.library.display.context.DLFilePicker;
import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.internal.constants.DLVideoConstants;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

/**
 * @author Iván Zaera
 * @author Alejandro Tardín
 */
public class DLVideoExternalShortcutDLEditFileEntryDisplayContext
	extends BaseDLEditFileEntryDisplayContext {

	public DLVideoExternalShortcutDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DLFileEntryType dlFileEntryType, ServletContext servletContext) {

		super(
			_UUID, parentDLEditFileEntryDisplayContext, httpServletRequest,
			httpServletResponse, dlFileEntryType);

		_servletContext = servletContext;
	}

	public DLVideoExternalShortcutDLEditFileEntryDisplayContext(
		DLEditFileEntryDisplayContext parentDLEditFileEntryDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DLVideoExternalShortcut dlVideoExternalShortcut, FileEntry fileEntry,
		ServletContext servletContext) {

		super(
			_UUID, parentDLEditFileEntryDisplayContext, httpServletRequest,
			httpServletResponse, fileEntry);

		_dlVideoExternalShortcut = dlVideoExternalShortcut;
		_servletContext = servletContext;
	}

	@Override
	public DLFilePicker getDLFilePicker(String onFilePickCallback) {
		return new DLVideoExternalShortcutDLFilePicker(
			_dlVideoExternalShortcut, onFilePickCallback, _servletContext);
	}

	@Override
	public long getMaximumUploadSize() {
		return 0;
	}

	@Override
	public boolean isCancelCheckoutDocumentButtonVisible() {
		return false;
	}

	@Override
	public boolean isCheckinButtonVisible() {
		return false;
	}

	@Override
	public boolean isCheckoutDocumentButtonVisible() {
		return false;
	}

	@Override
	public boolean isDDMStructureVisible(DDMStructure ddmStructure)
		throws PortalException {

		String ddmStructureKey = ddmStructure.getStructureKey();

		if (ddmStructureKey.equals(
				DLVideoConstants.
					DDM_STRUCTURE_KEY_DL_VIDEO_EXTERNAL_SHORTCUT)) {

			return false;
		}

		return super.isDDMStructureVisible(ddmStructure);
	}

	@Override
	public boolean isFileNameVisible() {
		return false;
	}

	@Override
	public boolean isVersionInfoVisible() {
		return false;
	}

	private static final UUID _UUID = UUID.fromString(
		"f3dad960-a5ea-4499-badd-0d1a06ee1c93");

	private DLVideoExternalShortcut _dlVideoExternalShortcut;
	private final ServletContext _servletContext;

}