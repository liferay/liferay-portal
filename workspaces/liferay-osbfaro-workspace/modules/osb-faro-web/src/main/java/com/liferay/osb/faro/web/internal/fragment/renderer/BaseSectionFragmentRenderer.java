/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.osb.faro.web.internal.util.FaroNavigationUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseSectionFragmentRenderer extends BaseFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "liferay-data-platform";
	}

	@Override
	public String getLabel(Locale locale) {
		return LanguageUtil.get(locale, getLabelKey());
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			Layout layout = themeDisplay.getLayout();

			portal.addPageTitle(
				layout.getHTMLTitle(themeDisplay.getLocale()),
				httpServletRequest);

			String key = "analytics-cloud";

			if (FaroNavigationUtil.isLDPEnabled(
					themeDisplay.getScopeGroupId())) {

				key = "liferay-data-platform";
			}

			portal.addPageTitle(
				LanguageUtil.get(httpServletRequest, key), httpServletRequest);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}

		super.render(
			fragmentRendererContext, httpServletRequest, httpServletResponse);
	}

	protected abstract String getLabelKey();

}