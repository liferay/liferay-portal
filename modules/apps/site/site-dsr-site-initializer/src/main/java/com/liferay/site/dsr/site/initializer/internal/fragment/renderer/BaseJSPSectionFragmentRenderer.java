/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.fragment.renderer;

import com.liferay.analytics.settings.rest.manager.AnalyticsSettingsManager;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
public abstract class BaseJSPSectionFragmentRenderer<T>
	extends BaseSectionFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return getLabelKey();
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, getLabelKey());
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return true;
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (fragmentRendererContext.isIndexMode()) {
			return;
		}

		try {
			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(getJSPPath());

			T displayContext = getDisplayContext(
				fragmentRendererContext, httpServletRequest);

			Class<?> clazz = displayContext.getClass();

			httpServletRequest.setAttribute(clazz.getName(), displayContext);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (IOException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	protected abstract T getDisplayContext(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws PortalException;

	protected String getJSPPath() {
		return StringBundler.concat(
			"/", StringUtil.replace(getLabelKey(), '-', '_'), ".jsp");
	}

	protected abstract String getLabelKey();

	@Reference
	protected AnalyticsSettingsManager analyticsSettingsManager;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.site.dsr.site.initializer)"
	)
	protected ServletContext servletContext;

}