/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Renders file previews in conjunction with {@link DLPreviewRendererProvider}.
 *
 * @author Alejandro Tardín
 */
public interface DLPreviewRenderer {

	/**
	 * Renders content to the response.
	 *
	 * @param httpServletRequest the request
	 * @param httpServletResponse the response
	 * @see   DLPreviewRendererProvider#getPreviewDLPreviewRenderer(FileVersion)
	 * @see   DLPreviewRendererProvider#getThumbnailDLPreviewRenderer(
	 *        FileVersion)
	 */
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, PortalException, ServletException;

}