/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.fragment.renderer;

import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = FragmentRenderer.class)
public class FaroPageBarActionsFragmentRenderer implements FragmentRenderer {

	@Override
	public String getCollectionKey() {
		return "liferay-data-platform";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "page-bar-actions");
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		return false;
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/page_bar_actions.jsp");

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			throw new IOException(servletException);
		}
	}

	@Reference
	private Language _language;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.osb.faro.web)")
	private ServletContext _servletContext;

}