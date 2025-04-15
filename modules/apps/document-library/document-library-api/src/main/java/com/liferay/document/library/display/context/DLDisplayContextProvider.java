/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.document.library.kernel.model.DLFileEntryType;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Alejandro Tardín
 */
public interface DLDisplayContextProvider {

	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse,
		DLFileEntryType dlFileEntryType);

	public DLEditFileEntryDisplayContext getDLEditFileEntryDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileEntry fileEntry);

	public DLViewFileEntryHistoryDisplayContext
		getDLViewFileEntryHistoryDisplayContext(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FileVersion fileVersion);

	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileShortcut fileShortcut);

	public DLViewFileVersionDisplayContext getDLViewFileVersionDisplayContext(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion);

}