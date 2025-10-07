/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.fragment.internal.renderer;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.frontend.taglib.react.servlet.taglib.ComponentTag;
import com.liferay.taglib.servlet.PageContextFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Map;

/**
 * @author Alessio Antonio Rendina
 */
public abstract class BaseComponentFragmentRenderer
	extends BaseFragmentRenderer {

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

			ComponentTag componentTag = new ComponentTag();

			componentTag.setModule(getModule());
			componentTag.setPageContext(
				PageContextFactoryUtil.create(
					httpServletRequest, httpServletResponse));
			componentTag.setProps(
				getProps(fragmentRendererContext, httpServletRequest));
			componentTag.setServletContext(servletContext);

			componentTag.doStartTag();

			componentTag.doEndTag();
		}
		catch (IOException | RuntimeException exception) {
			throw exception;
		}
		catch (Exception exception) {
			throw new IOException(exception);
		}
	}

	protected abstract String getModule();

	protected abstract Map<String, Object> getProps(
			FragmentRendererContext fragmentRendererContext,
			HttpServletRequest httpServletRequest)
		throws Exception;

}