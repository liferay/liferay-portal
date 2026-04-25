/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.production.readiness.ignore.service.ProductionReadinessIgnoreLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author lily
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/server_admin/edit_production_readiness_ignore"
	},
	service = MVCActionCommand.class
)
public class EditProductionReadinessIgnoreMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		if (_productionReadinessIgnoreLocalService == null) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String ruleKey = ParamUtil.getString(actionRequest, "ruleKey");
		boolean ignore = ParamUtil.getBoolean(actionRequest, "ignore");

		try {
			if (ignore) {
				_productionReadinessIgnoreLocalService.addProductionReadinessIgnore(
					themeDisplay.getUserId(), themeDisplay.getCompanyId(),
					ruleKey, "Ignored from UI");
			}
			else {
				_productionReadinessIgnoreLocalService.deleteProductionReadinessIgnore(
					themeDisplay.getCompanyId(), ruleKey);
			}
		}
		catch (Exception e) {
		}

		return true;
	}

	@Reference
	private ProductionReadinessIgnoreLocalService
		_productionReadinessIgnoreLocalService;

}