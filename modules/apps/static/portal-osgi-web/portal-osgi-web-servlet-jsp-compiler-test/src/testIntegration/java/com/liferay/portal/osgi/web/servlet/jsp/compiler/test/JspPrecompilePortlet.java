/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.servlet.jsp.compiler.test;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

/**
 * @author Matthew Tambara
 */
public class JspPrecompilePortlet extends MVCPortlet {

	public static final String PORTLET_NAME = StringUtil.replace(
		JspPrecompilePortlet.class.getName(), CharPool.PERIOD,
		CharPool.UNDERLINE);

	public static String getJspFileNameParameterName() {
		return StringBundler.concat(
			StringPool.UNDERLINE, PORTLET_NAME, StringPool.UNDERLINE,
			_JSP_FILE_NAME_PARAMETER_NAME);
	}

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		String jspFileName = renderRequest.getParameter(
			_JSP_FILE_NAME_PARAMETER_NAME);

		if (jspFileName == null) {
			throw new IllegalArgumentException(
				_JSP_FILE_NAME_PARAMETER_NAME + " query must not be null");
		}

		PortletContext portletContext = getPortletContext();

		PortletRequestDispatcher portletRequestDispatcher =
			portletContext.getRequestDispatcher(jspFileName);

		portletRequestDispatcher.include(renderRequest, renderResponse);
	}

	private static final String _JSP_FILE_NAME_PARAMETER_NAME = "jspFileName";

}