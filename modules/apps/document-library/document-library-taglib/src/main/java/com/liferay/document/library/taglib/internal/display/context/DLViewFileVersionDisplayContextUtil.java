/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.taglib.internal.display.context;

import com.liferay.document.library.display.context.DLDisplayContextProvider;
import com.liferay.document.library.display.context.DLViewFileVersionDisplayContext;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Alejandro Tardín
 */
public class DLViewFileVersionDisplayContextUtil {

	public static DLViewFileVersionDisplayContext
		getDLViewFileVersionDisplayContext(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		DLDisplayContextProvider dlDisplayContextProvider =
			_dlDisplayContextProviderSnapshot.get();

		return dlDisplayContextProvider.getDLViewFileVersionDisplayContext(
			httpServletRequest, httpServletResponse, fileVersion);
	}

	private static final Snapshot<DLDisplayContextProvider>
		_dlDisplayContextProviderSnapshot = new Snapshot<>(
			DLViewFileVersionDisplayContextUtil.class,
			DLDisplayContextProvider.class);

}