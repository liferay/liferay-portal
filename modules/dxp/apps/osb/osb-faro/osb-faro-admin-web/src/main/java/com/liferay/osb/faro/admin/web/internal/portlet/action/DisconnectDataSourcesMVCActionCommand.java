/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.admin.web.internal.portlet.action;

import com.liferay.osb.faro.admin.web.internal.constants.FaroAdminPortletKeys;
import com.liferay.osb.faro.engine.client.ContactsEngineClient;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leslie Wong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FaroAdminPortletKeys.FARO_ADMIN,
		"mvc.command.name=/faro_admin/disconnect_data_sources"
	},
	service = MVCActionCommand.class
)
public class DisconnectDataSourcesMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		long faroProjectId = ParamUtil.getLong(actionRequest, "faroProjectId");

		try {
			FaroProject faroProject = _faroProjectLocalService.getFaroProject(
				faroProjectId);

			_contactsEngineClient.disconnectDataSources(faroProject);

			faroProject.setDataSourceConnected(false);

			_faroProjectLocalService.updateFaroProject(faroProject);
		}
		catch (Exception exception) {
			_log.error(exception);

			SessionErrors.add(actionRequest, exception.getClass());
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

	private static final Log _log = LogFactoryUtil.getLog(
		DisconnectDataSourcesMVCActionCommand.class);

	@Reference
	private ContactsEngineClient _contactsEngineClient;

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private Portal _portal;

}