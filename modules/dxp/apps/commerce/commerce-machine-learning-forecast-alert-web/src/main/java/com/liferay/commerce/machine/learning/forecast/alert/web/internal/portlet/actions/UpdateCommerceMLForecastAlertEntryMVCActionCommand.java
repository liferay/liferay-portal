/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.machine.learning.forecast.alert.web.internal.portlet.actions;

import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertActionKeys;
import com.liferay.commerce.machine.learning.forecast.alert.constants.CommerceMLForecastAlertPortletKeys;
import com.liferay.commerce.machine.learning.forecast.alert.service.CommerceMLForecastAlertEntryService;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Ferrari
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommerceMLForecastAlertPortletKeys.COMMERCE_ML_FORECAST_ALERT,
		"mvc.command.name=/commerce_ml_forecast_alert/update_commerce_ml_forecast_alert_entry"
	},
	service = MVCActionCommand.class
)
public class UpdateCommerceMLForecastAlertEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		try {
			if (cmd.equals(
					CommerceMLForecastAlertActionKeys.MANAGE_ALERT_STATUS)) {

				long commerceMLForecastAlertEntryId = ParamUtil.getLong(
					actionRequest, "commerceMLForecastAlertEntryId");

				int status = ParamUtil.getInteger(actionRequest, "status");

				_commerceMLForecastAlertEntryService.updateStatus(
					commerceMLForecastAlertEntryId, status);
			}
		}
		catch (Throwable throwable) {
			if (throwable instanceof PrincipalException) {
				hideDefaultErrorMessage(actionRequest);

				SessionErrors.add(actionRequest, "principalException");

				sendRedirect(actionRequest, actionResponse);
			}
		}
	}

	@Reference
	private CommerceMLForecastAlertEntryService
		_commerceMLForecastAlertEntryService;

}