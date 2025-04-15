/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.SessionClicks;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.Enumeration;

/**
 * @author Brian Wing Shun Chan
 */
public class SessionClickAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			AuthTokenUtil.checkCSRFToken(
				httpServletRequest, SessionClickAction.class.getName());

			HttpSession httpSession = httpServletRequest.getSession();

			Enumeration<String> enumeration =
				httpServletRequest.getParameterNames();

			boolean useHttpSession = ParamUtil.getBoolean(
				httpServletRequest, "useHttpSession");

			while (enumeration.hasMoreElements()) {
				String name = enumeration.nextElement();

				if (!StringUtil.equals(name, "cmd") &&
					!StringUtil.equals(name, "doAsUserId") &&
					!StringUtil.equals(name, "p_auth")) {

					String value = ParamUtil.getString(
						httpServletRequest, name);

					if (useHttpSession) {
						SessionClicks.put(httpSession, name, value);
					}
					else {
						SessionClicks.put(httpServletRequest, name, value);
					}
				}
			}

			String value = getValue(httpServletRequest);

			if (value != null) {
				String cmd = ParamUtil.getString(
					httpServletRequest, Constants.CMD);

				if (StringUtil.equals(cmd, "get")) {
					httpServletResponse.setContentType(ContentTypes.TEXT_PLAIN);
				}
				else {
					httpServletResponse.setContentType(
						ContentTypes.APPLICATION_JSON);
				}

				ServletOutputStream servletOutputStream =
					httpServletResponse.getOutputStream();

				servletOutputStream.print(value);
			}

			return null;
		}
		catch (Exception exception) {
			PortalUtil.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	protected String getValue(HttpServletRequest httpServletRequest) {
		HttpSession httpSession = httpServletRequest.getSession();

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		boolean useHttpSession = ParamUtil.getBoolean(
			httpServletRequest, "useHttpSession");

		if (StringUtil.equals(cmd, "get")) {
			String key = ParamUtil.getString(httpServletRequest, "key");
			String value = StringPool.BLANK;

			if (useHttpSession) {
				value = SessionClicks.get(httpSession, key, cmd);
			}
			else {
				value = SessionClicks.get(httpServletRequest, key, cmd);
			}

			return value;
		}
		else if (StringUtil.equals(cmd, "getAll")) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			String[] keys = httpServletRequest.getParameterValues("key");

			for (String key : keys) {
				String value = StringPool.BLANK;

				if (useHttpSession) {
					value = SessionClicks.get(httpSession, key, cmd);
				}
				else {
					value = SessionClicks.get(httpServletRequest, key, cmd);
				}

				jsonObject.put(key, value);
			}

			return jsonObject.toString();
		}

		return null;
	}

}