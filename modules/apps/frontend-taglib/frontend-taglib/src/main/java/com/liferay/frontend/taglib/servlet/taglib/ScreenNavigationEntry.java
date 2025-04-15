/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.portal.kernel.model.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public interface ScreenNavigationEntry<T> {

	public String getCategoryKey();

	public String getEntryKey();

	public String getLabel(Locale locale);

	public String getScreenNavigationKey();

	public default String getStatusLabel(Locale locale, T context) {
		return null;
	}

	public default String getStatusStyle(T context) {
		return "secondary";
	}

	public default boolean isVisible(User user, T context) {
		return true;
	}

	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

}