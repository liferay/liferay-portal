/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.portlet.action;

import com.liferay.content.dashboard.web.internal.constants.ContentDashboardPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletPreferences;
import jakarta.portlet.ValidatorException;

import org.osgi.service.component.annotations.Component;

/**
 * @author David Arques
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContentDashboardPortletKeys.CONTENT_DASHBOARD_ADMIN,
		"mvc.command.name=/content_dashboard/update_content_dashboard_configuration"
	},
	service = MVCActionCommand.class
)
public class UpdateContentDashboardConfigurationMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String[] assetVocabularyIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "assetVocabularyIds"));

		if (assetVocabularyIds.length == 0) {
			hideDefaultSuccessMessage(actionRequest);
			SessionMessages.add(actionRequest, "emptyAssetVocabularyIds", true);
		}
		else {
			PortletPreferences portletPreferences =
				actionRequest.getPreferences();

			portletPreferences.setValues(
				"assetVocabularyIds", assetVocabularyIds);

			try {
				portletPreferences.store();
			}
			catch (ValidatorException validatorException) {
				SessionErrors.add(
					actionRequest, ValidatorException.class.getName(),
					validatorException);
			}
		}

		String redirect = ParamUtil.getString(actionRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			sendRedirect(actionRequest, actionResponse, redirect);
		}
	}

}