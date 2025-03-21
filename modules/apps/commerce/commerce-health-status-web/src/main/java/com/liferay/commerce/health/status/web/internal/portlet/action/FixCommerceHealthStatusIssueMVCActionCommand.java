/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.health.status.web.internal.portlet.action;

import com.liferay.commerce.constants.CommercePortletKeys;
import com.liferay.commerce.health.status.CommerceHealthStatus;
import com.liferay.commerce.health.status.CommerceHealthStatusRegistry;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CommercePortletKeys.COMMERCE_HEALTH_CHECK,
		"mvc.command.name=/commerce_health_check/fix_commerce_health_status_issue"
	},
	service = MVCActionCommand.class
)
public class FixCommerceHealthStatusIssueMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			actionRequest);
		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		String key = ParamUtil.getString(actionRequest, "key");

		try {
			CommerceHealthStatus commerceHealthStatus =
				_commerceHealthStatusRegistry.getCommerceHealthStatus(key);

			if (commerceHealthStatus != null) {
				commerceHealthStatus.fixIssue(httpServletRequest);

				Thread.sleep(2000);

				jsonObject.put(
					"success",
					commerceHealthStatus.isFixed(
						_portal.getCompanyId(httpServletRequest),
						_portal.getScopeGroupId(httpServletRequest)));
			}
		}
		catch (Exception exception) {
			hideDefaultErrorMessage(actionRequest);

			_log.error(exception);

			jsonObject.put(
				"error", exception.getMessage()
			).put(
				"success", false
			);
		}

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		_writeJSON(actionResponse, jsonObject);

		hideDefaultSuccessMessage(actionRequest);
	}

	private void _writeJSON(ActionResponse actionResponse, Object object)
		throws Exception {

		HttpServletResponse httpServletResponse =
			_portal.getHttpServletResponse(actionResponse);

		httpServletResponse.setContentType(ContentTypes.APPLICATION_JSON);

		ServletResponseUtil.write(httpServletResponse, object.toString());

		httpServletResponse.flushBuffer();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FixCommerceHealthStatusIssueMVCActionCommand.class);

	@Reference
	private CommerceHealthStatusRegistry _commerceHealthStatusRegistry;

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private Portal _portal;

}