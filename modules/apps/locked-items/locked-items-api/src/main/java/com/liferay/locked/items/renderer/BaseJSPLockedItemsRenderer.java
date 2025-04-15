/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.locked.items.renderer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseJSPLockedItemsRenderer
	implements LockedItemsRenderer {

	@Override
	public void render(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			ServletContext servletContext = getServletContext();

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(getJspPath());

			setAttributes(httpServletRequest, httpServletResponse);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error("Unable to render " + getJspPath(), exception);
		}
	}

	protected abstract String getJspPath();

	protected abstract ServletContext getServletContext();

	protected abstract void setAttributes(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	private static final Log _log = LogFactoryUtil.getLog(
		BaseJSPLockedItemsRenderer.class);

}