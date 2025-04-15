/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.info.item.renderer;

import com.liferay.info.item.renderer.template.InfoItemRendererTemplate;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public interface InfoItemTemplatedRenderer<T> extends InfoItemRenderer<T> {

	public default List<InfoItemRendererTemplate> getInfoItemRendererTemplates(
		String className, String classTypeKey, Locale locale) {

		return Collections.emptyList();
	}

	public List<InfoItemRendererTemplate> getInfoItemRendererTemplates(
		T t, Locale locale);

	public default String getInfoItemRendererTemplatesGroupLabel(
		String className, String classTypeKey, Locale locale) {

		return getLabel(locale);
	}

	public default String getInfoItemRendererTemplatesGroupLabel(
		T t, Locale locale) {

		return getLabel(locale);
	}

	@Override
	public default void render(
		T t, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		List<InfoItemRendererTemplate> infoItemRendererTemplates =
			getInfoItemRendererTemplates(t, LocaleUtil.getMostRelevantLocale());

		if (ListUtil.isEmpty(infoItemRendererTemplates)) {
			return;
		}

		InfoItemRendererTemplate infoItemRendererTemplate =
			infoItemRendererTemplates.get(0);

		render(
			t, infoItemRendererTemplate.getTemplateKey(), httpServletRequest,
			httpServletResponse);
	}

	public void render(
		T t, String templateKey, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse);

}