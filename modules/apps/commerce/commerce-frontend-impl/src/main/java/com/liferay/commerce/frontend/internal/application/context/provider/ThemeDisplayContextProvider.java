/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.internal.application.context.provider;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.message.Message;

/**
 * @author Marco Leo
 */
@Provider
public class ThemeDisplayContextProvider
	implements ContextProvider<ThemeDisplay> {

	@Override
	public ThemeDisplay createContext(Message message) {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)message.getContextualProperty("HTTP.REQUEST");

		return (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

}