/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.web.internal.portlet.action;

import com.liferay.exportimport.constants.ExportImportPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Daniel Kocsis
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ExportImportPortletKeys.EXPORT_IMPORT,
		"mvc.command.name=/export_import/export_import"
	},
	service = MVCResourceCommand.class
)
public class ExportImportMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		String cmd = ParamUtil.getString(resourceRequest, Constants.CMD);

		PortletRequestDispatcher portletRequestDispatcher = null;

		if (cmd.equals(Constants.EXPORT)) {
			portletRequestDispatcher = getPortletRequestDispatcher(
				resourceRequest, "/export_portlet_processes.jsp");
		}
		else if (cmd.equals(Constants.IMPORT)) {
			portletRequestDispatcher = getPortletRequestDispatcher(
				resourceRequest, "/import_portlet_processes.jsp");
		}
		else if (cmd.equals("export_import")) {
			portletRequestDispatcher = getPortletRequestDispatcher(
				resourceRequest, "/export_import_process.jsp");
		}
		else {
			portletRequestDispatcher = getPortletRequestDispatcher(
				resourceRequest, "/import_portlet_resources.jsp");
		}

		resourceRequest = ActionUtil.getWrappedResourceRequest(
			resourceRequest, null);

		portletRequestDispatcher.include(resourceRequest, resourceResponse);
	}

}