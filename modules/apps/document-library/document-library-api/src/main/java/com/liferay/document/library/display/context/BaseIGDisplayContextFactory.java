/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.portal.kernel.display.context.BaseDisplayContextFactory;
import com.liferay.portal.kernel.repository.model.FileShortcut;
import com.liferay.portal.kernel.repository.model.FileVersion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Iván Zaera
 */
public class BaseIGDisplayContextFactory
	extends BaseDisplayContextFactory implements IGDisplayContextFactory {

	@Override
	public IGViewFileVersionDisplayContext getIGViewFileVersionDisplayContext(
		IGViewFileVersionDisplayContext parentIGViewFileVersionDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileShortcut fileShortcut) {

		return parentIGViewFileVersionDisplayContext;
	}

	@Override
	public IGViewFileVersionDisplayContext getIGViewFileVersionDisplayContext(
		IGViewFileVersionDisplayContext parentIGViewFileVersionDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, FileVersion fileVersion) {

		return parentIGViewFileVersionDisplayContext;
	}

}