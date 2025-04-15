/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.renderer;

import com.liferay.info.type.Keyed;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Jorge Ferrer
 */
public interface InfoItemRenderer<T> extends Keyed {

	public default String getLabel(Locale locale) {
		return LanguageUtil.get(locale, getKey());
	}

	public default boolean isAvailable() {
		return true;
	}

	public void render(
		T t, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

}