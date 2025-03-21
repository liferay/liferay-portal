/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.display.context;

import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Iván Zaera
 */
public interface DLFilePicker {

	public default String getCurrentIconURL() {
		return null;
	}

	public default String getCurrentTitle() {
		return null;
	}

	public String getDescriptionFieldName();

	public default String getFileNameFieldName() {
		return null;
	}

	public String getIconFieldName();

	public String getJavaScript() throws PortalException;

	public String getJavaScriptModuleName();

	public String getOnClickCallback();

	public String getTitleFieldName();

	public default boolean isCustomizedFileButtonVisible() {
		return true;
	}

	public default void renderFilePicker(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {
	}

}