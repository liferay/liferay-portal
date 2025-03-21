/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.SourceService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gavin Wan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
		"mvc.command.name=/reports_admin/edit_data_source"
	},
	service = MVCActionCommand.class
)
public class EditDataSourceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long sourceId = ParamUtil.getLong(actionRequest, "sourceId");

		Map<Locale, String> nameMap = _localization.getLocalizationMap(
			actionRequest, "name");
		String driverClassName = ParamUtil.getString(
			actionRequest, "driverClassName");
		String driverUrl = ParamUtil.getString(actionRequest, "driverUrl");
		String driverUserName = ParamUtil.getString(
			actionRequest, "driverUserName");
		String driverPassword = ParamUtil.getString(
			actionRequest, "driverPassword");

		ServiceContext serviceContext = ServiceContextFactory.getInstance(
			Source.class.getName(), actionRequest);

		if (sourceId <= 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			_sourceService.addSource(
				themeDisplay.getScopeGroupId(), nameMap, driverClassName,
				driverUrl, driverUserName, driverPassword, serviceContext);
		}
		else {
			_sourceService.updateSource(
				sourceId, nameMap, driverClassName, driverUrl, driverUserName,
				driverPassword, serviceContext);
		}
	}

	@Reference
	private Localization _localization;

	@Reference
	private SourceService _sourceService;

}