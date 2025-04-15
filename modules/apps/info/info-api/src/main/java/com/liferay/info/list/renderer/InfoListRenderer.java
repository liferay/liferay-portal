/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.list.renderer;

import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.info.type.Keyed;
import com.liferay.portal.kernel.language.LanguageUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Jorge Ferrer
 */
public interface InfoListRenderer<T> extends Keyed {

	public default List<InfoItemRenderer<?>> getAvailableInfoItemRenderers() {
		return Collections.emptyList();
	}

	public default String getLabel(Locale locale) {
		return LanguageUtil.get(locale, getKey());
	}

	public void render(
		List<T> list, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

	public default void render(
		List<T> list, InfoListRendererContext infoListRendererContext) {

		render(
			list, infoListRendererContext.getHttpServletRequest(),
			infoListRendererContext.getHttpServletResponse());
	}

}