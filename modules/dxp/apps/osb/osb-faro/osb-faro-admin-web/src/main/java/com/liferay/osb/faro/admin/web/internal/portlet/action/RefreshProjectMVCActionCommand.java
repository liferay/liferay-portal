/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.portlet.action;

import com.liferay.osb.faro.admin.web.internal.constants.FaroAdminPortletKeys;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matthew Kong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FaroAdminPortletKeys.FARO_ADMIN,
		"mvc.command.name=/faro_admin/refresh_project"
	},
	service = MVCActionCommand.class
)
public class RefreshProjectMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		Http.Options options = new Http.Options();

		options.setHeaders(getHeaders(actionRequest));

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		long groupId = ParamUtil.getLong(actionRequest, "groupId");

		if (groupId > 0) {
			sendRequest(
				actionRequest, options, themeDisplay.getPortalURL(), groupId);
		}
		else {
			for (FaroProject faroProject :
					_faroProjectLocalService.getFaroProjects(
						QueryUtil.ALL_POS, QueryUtil.ALL_POS)) {

				if (StringUtil.equals(
						faroProject.getState(),
						FaroProjectConstants.STATE_READY)) {

					sendRequest(
						actionRequest, options, themeDisplay.getPortalURL(),
						faroProject.getGroupId());
				}
			}
		}
	}

	protected Map<String, String> getHeaders(ActionRequest actionRequest) {
		Map<String, String> headers = new HashMap<>();

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);

		Enumeration<String> enumeration = httpServletRequest.getHeaderNames();

		while (enumeration.hasMoreElements()) {
			String headerName = enumeration.nextElement();

			headers.put(headerName, httpServletRequest.getHeader(headerName));
		}

		return headers;
	}

	protected String getURL(String portalURL, long groupId) {
		String url = StringBundler.concat(
			portalURL, "/o/faro/main/project/", groupId);

		url = HttpComponentsUtil.addParameter(url, "forceUpdate", true);

		return HttpComponentsUtil.addParameter(url, "updateLastAccess", false);
	}

	protected void sendRequest(
		ActionRequest actionRequest, Http.Options options, String portalURL,
		long groupId) {

		try {
			options.setLocation(getURL(portalURL, groupId));

			_http.URLtoString(options);
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(actionRequest, exception.getClass());
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RefreshProjectMVCActionCommand.class);

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private Http _http;

	@Reference
	private Portal _portal;

}