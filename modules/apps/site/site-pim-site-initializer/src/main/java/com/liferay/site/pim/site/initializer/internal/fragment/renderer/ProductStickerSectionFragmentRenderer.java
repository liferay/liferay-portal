/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.site.pim.site.initializer.internal.display.context.ProductStickerDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Locale;

/**
 * @author Tancredi Covioli
 */
public class ProductStickerSectionFragmentRenderer implements FragmentRenderer {

	public ProductStickerSectionFragmentRenderer(
		Language language, ServletContext servletContext) {

		_language = language;
		_servletContext = servletContext;
	}

	@Override
	public String getCollectionKey() {
		return "pim";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "product-image");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return FeatureFlagManagerUtil.isEnabled(
			themeDisplay.getCompanyId(), "LPD-96666");
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		httpServletRequest.setAttribute(
			ProductStickerDisplayContext.class.getName(),
			new ProductStickerDisplayContext(httpServletRequest));

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/product_sticker.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			_log.error(exception);

			throw new RuntimeException(exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ProductStickerSectionFragmentRenderer.class);

	private final Language _language;
	private final ServletContext _servletContext;

}