/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal.struts;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.servlet.ServletContextPool;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.theme.ThemeUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(property = "path=/portal/api/jsonws", service = StrutsAction.class)
public class JSONWebServiceStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher("/index.jsp");

			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			PipingServletResponse pipingServletResponse =
				new PipingServletResponse(
					httpServletResponse, unsyncStringWriter);

			requestDispatcher.include(
				httpServletRequest, pipingServletResponse);

			StringBundler sb = new StringBundler(3);

			Theme theme = (Theme)httpServletRequest.getAttribute(WebKeys.THEME);

			String html = ThemeUtil.include(
				ServletContextPool.get(StringPool.BLANK), httpServletRequest,
				httpServletResponse, "portal_pop_up.jsp", theme, false);

			int index =
				html.indexOf(StringPool.GREATER_THAN, html.indexOf("<body")) +
					1;

			sb.append(html.substring(0, index));

			sb.append(unsyncStringWriter.toString());
			sb.append(html.substring(index));

			ServletResponseUtil.write(httpServletResponse, sb.toString());
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONWebServiceStrutsAction.class);

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.remote.json.web.service.web)"
	)
	private ServletContext _servletContext;

}