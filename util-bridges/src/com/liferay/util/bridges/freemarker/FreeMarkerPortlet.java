/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.util.bridges.freemarker;

import com.liferay.petra.io.unsync.UnsyncPrintWriter;
import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.template.Template;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.template.TemplateResourceLoaderUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Raymond Augé
 */
public class FreeMarkerPortlet extends MVCPortlet {

	@Override
	protected void include(
			String path, PortletRequest portletRequest,
			PortletResponse portletResponse, String lifecycle)
		throws IOException, PortletException {

		PortletContext portletContext = getPortletContext();

		String servletContextName = portletContext.getPortletContextName();

		String resourcePath = StringBundler.concat(
			servletContextName, TemplateConstants.SERVLET_SEPARATOR, path);

		boolean resourceExists = false;

		try {
			resourceExists = TemplateResourceLoaderUtil.hasTemplateResource(
				TemplateConstants.LANG_TYPE_FTL, resourcePath);
		}
		catch (TemplateException templateException) {
			throw new IOException(templateException);
		}

		if (!resourceExists) {
			_log.error(path + " is not a valid include");
		}
		else {
			try {
				Template template = TemplateManagerUtil.getTemplate(
					TemplateConstants.LANG_TYPE_FTL,
					TemplateResourceLoaderUtil.getTemplateResource(
						TemplateConstants.LANG_TYPE_FTL, resourcePath),
					false);

				template.prepareTaglib(
					PortalUtil.getHttpServletRequest(portletRequest),
					PortalUtil.getHttpServletResponse(portletResponse));

				template.put("portletContext", getPortletContext());
				template.put(
					"userInfo",
					portletRequest.getAttribute(PortletRequest.USER_INFO));

				template.prepare(
					PortalUtil.getHttpServletRequest(portletRequest));

				Writer writer = null;

				if (portletResponse instanceof MimeResponse) {
					MimeResponse mimeResponse = (MimeResponse)portletResponse;

					writer = new UnsyncPrintWriter(mimeResponse.getWriter());
				}
				else {
					writer = new UnsyncStringWriter();
				}

				template.processTemplate(writer);
			}
			catch (Exception exception) {
				throw new PortletException(exception);
			}
		}

		if (clearRequestParameters &&
			lifecycle.equals(PortletRequest.RENDER_PHASE)) {

			portletResponse.setProperty("clear-request-parameters", "true");
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FreeMarkerPortlet.class);

}