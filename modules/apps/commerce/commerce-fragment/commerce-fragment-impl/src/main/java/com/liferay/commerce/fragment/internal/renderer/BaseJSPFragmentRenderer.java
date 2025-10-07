/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.renderer;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Alessio Antonio Rendina
 */
public abstract class BaseJSPFragmentRenderer<T> extends BaseFragmentRenderer {

	@Override
	public String getCollectionKey() {
		return getLabelKey();
	}

	@Override
	public String getLabel(Locale locale) {
		return language.get(locale, getLabelKey());
	}

	@Override
	public void render(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			CommerceContext commerceContext =
				(CommerceContext)httpServletRequest.getAttribute(
					CommerceWebKeys.COMMERCE_CONTEXT);

			if ((commerceContext == null) ||
				(commerceContext.getCommerceChannelId() <= 0)) {

				printPortletMessageInfo(
					httpServletRequest, httpServletResponse,
					"this-site-does-not-have-a-channel");

				return;
			}

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
			"/fragment/renderer/", StringUtil.replace(getLabelKey(), '-', '_'),
			"/page.jsp");
	}

}