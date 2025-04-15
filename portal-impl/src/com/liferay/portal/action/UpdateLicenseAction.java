/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.license.util.LicenseManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.AuthTokenUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.struts.Action;
import com.liferay.portal.struts.model.ActionForward;
import com.liferay.portal.struts.model.ActionMapping;
import com.liferay.portal.util.LicenseUtil;
import com.liferay.portlet.admin.util.OmniadminUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

/**
 * @author Amos Fong
 */
public class UpdateLicenseAction implements Action {

	@Override
	public ActionForward execute(
			ActionMapping actionMapping, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		// PLACEHOLDER 01
		// PLACEHOLDER 02
		// PLACEHOLDER 03
		// PLACEHOLDER 04
		// PLACEHOLDER 05
		// PLACEHOLDER 06
		// PLACEHOLDER 07
		// PLACEHOLDER 08

		if (!_isOmniAdmin(httpServletRequest)) {
			httpServletResponse.sendRedirect(
				PortalUtil.getPathContext() + "/c/portal/layout");

			return null;
		}

		if (StringUtil.equalsIgnoreCase(
				httpServletRequest.getMethod(), HttpMethods.GET)) {

			return actionMapping.getActionForward("portal.license");
		}

		if (!_isCSRFTokenValid(httpServletRequest, httpServletResponse)) {
			httpServletResponse.sendRedirect(
				PortalUtil.getPathContext() + "/c/portal/layout");

			return null;
		}

		LicenseUtil.registerOrder(httpServletRequest);

		String cmd = ParamUtil.getString(httpServletRequest, Constants.CMD);

		String clusterNodeId = ParamUtil.getString(
			httpServletRequest, "clusterNodeId");

		if (cmd.equals("licenseProperties")) {
			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			ServletResponseUtil.write(
				httpServletResponse, _getLicenseProperties(clusterNodeId));

			return null;
		}
		else if (cmd.equals("serverInfo")) {
			httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

			ServletResponseUtil.write(
				httpServletResponse, _getServerInfo(clusterNodeId));

			return null;
		}

		return actionMapping.getActionForward("portal.license");
	}

	private String _getLicenseProperties(String clusterNodeId) {
		List<Map<String, String>> licenseProperties =
			LicenseManagerUtil.getClusterLicenseProperties(clusterNodeId);

		if (licenseProperties == null) {
			return StringPool.BLANK;
		}

		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (Map<String, String> propertiesMap : licenseProperties) {
			JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

			for (Map.Entry<String, String> entry : propertiesMap.entrySet()) {
				jsonObject.put(entry.getKey(), entry.getValue());
			}

			jsonArray.put(jsonObject);
		}

		return jsonArray.toString();
	}

	private String _getServerInfo(String clusterNodeId) throws Exception {
		Map<String, String> serverInfo = LicenseUtil.getClusterServerInfo(
			clusterNodeId);

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (serverInfo != null) {
			for (Map.Entry<String, String> entry : serverInfo.entrySet()) {
				jsonObject.put(entry.getKey(), entry.getValue());
			}
		}

		return jsonObject.toString();
	}

	private boolean _isCSRFTokenValid(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			AuthTokenUtil.checkCSRFToken(
				httpServletRequest, LicenseUtil.class.getName());

			return true;
		}
		catch (PortalException portalException) {
			_log.error(
				"Invalid authentication token received", portalException);

			PortalUtil.sendError(
				HttpServletResponse.SC_UNAUTHORIZED, portalException,
				httpServletRequest, httpServletResponse);
		}

		return false;
	}

	private boolean _isOmniAdmin(HttpServletRequest httpServletRequest) {

		// PLACEHOLDER 09
		// PLACEHOLDER 10
		// PLACEHOLDER 11
		// PLACEHOLDER 12
		// PLACEHOLDER 13
		// PLACEHOLDER 14
		// PLACEHOLDER 15
		// PLACEHOLDER 16
		// PLACEHOLDER 17
		// PLACEHOLDER 18
		// PLACEHOLDER 19
		// PLACEHOLDER 20
		// PLACEHOLDER 21
		// PLACEHOLDER 22

		User user = null;

		try {
			user = PortalUtil.getUser(httpServletRequest);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		if ((user != null) && OmniadminUtil.isOmniadmin(user)) {
			return true;
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateLicenseAction.class);

}