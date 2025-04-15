/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.info.internal.item.renderer;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.info.item.renderer.InfoItemRenderer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "service.ranking:Integer=300", service = InfoItemRenderer.class
)
public class AssetEntryTitleInfoItemRenderer
	implements InfoItemRenderer<AssetEntry> {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "title");
	}

	@Override
	public void render(
		AssetEntry assetEntry, HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			PrintWriter printWriter = httpServletResponse.getWriter();

			printWriter.print(assetEntry.getTitle(themeDisplay.getLocale()));

			printWriter.flush();
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
	}

	@Reference
	private Language _language;

}