/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Alberto Montero
 */
public class GoogleGadgetServlet extends HttpServlet {

	@Override
	public void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		try {
			String content = getContent(httpServletRequest);

			if (content == null) {
				PortalUtil.sendError(
					HttpServletResponse.SC_NOT_FOUND,
					new NoSuchLayoutException(), httpServletRequest,
					httpServletResponse);
			}
			else {
				httpServletRequest.setAttribute(
					WebKeys.GOOGLE_GADGET, Boolean.TRUE);

				httpServletResponse.setContentType(ContentTypes.TEXT_XML);

				ServletResponseUtil.write(httpServletResponse, content);
			}
		}
		catch (Exception exception) {
			_log.error(exception);

			PortalUtil.sendError(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception,
				httpServletRequest, httpServletResponse);
		}
	}

	protected String getContent(HttpServletRequest httpServletRequest)
		throws Exception {

		String path = GetterUtil.getString(httpServletRequest.getPathInfo());

		if (Validator.isNull(path)) {
			return null;
		}

		int pos = path.indexOf(Portal.FRIENDLY_URL_SEPARATOR);

		if (pos == -1) {
			return null;
		}

		String portletId = path.substring(
			pos + Portal.FRIENDLY_URL_SEPARATOR.length());

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			PortalUtil.getCompanyId(httpServletRequest), portletId);

		String title = portlet.getDisplayName();

		String widgetURL = String.valueOf(httpServletRequest.getRequestURL());

		widgetURL = widgetURL.replaceFirst(
			PropsValues.GOOGLE_GADGET_SERVLET_MAPPING,
			PropsValues.WIDGET_SERVLET_MAPPING);

		StringBundler sb = new StringBundler(14);

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<Module>");
		sb.append("<ModulePrefs title=\"");
		sb.append(title);
		sb.append("\"/>");
		sb.append("<Content type=\"html\">");
		sb.append("<![CDATA[");
		sb.append("<iframe frameborder=\"0\" height=\"100%\" src=\"");
		sb.append(HtmlUtil.escape(widgetURL));
		sb.append("\" width=\"100%\">");
		sb.append("</iframe>");
		sb.append("]]>");
		sb.append("</Content>");
		sb.append("</Module>");

		return sb.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GoogleGadgetServlet.class);

}