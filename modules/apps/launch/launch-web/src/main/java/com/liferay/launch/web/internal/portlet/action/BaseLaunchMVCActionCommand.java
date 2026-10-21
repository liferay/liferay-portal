/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseTransactionalMVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose Luis Navarro
 */
public abstract class BaseLaunchMVCActionCommand
	extends BaseTransactionalMVCActionCommand {

	protected abstract void doLaunchCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception;

	@Override
	protected void doTransactionalCommand(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			doLaunchCommand(actionRequest, actionResponse);
		}
		catch (PortalException portalException) {
			hideDefaultErrorMessage(actionRequest);

			JSONPortletResponseUtil.writeJSON(
				actionRequest, actionResponse,
				_getErrorJSONObject(actionRequest, portalException));

			throw portalException;
		}
	}

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	private JSONObject _getErrorJSONObject(
		ActionRequest actionRequest, Exception exception) {

		HttpServletRequest httpServletRequest = portal.getHttpServletRequest(
			actionRequest);

		if (exception instanceof PrincipalException) {
			return JSONUtil.put(
				"error",
				language.get(
					httpServletRequest,
					"you-do-not-have-the-required-permissions"));
		}

		if (_log.isWarnEnabled()) {
			_log.warn(exception);
		}

		return JSONUtil.put(
			"error",
			language.get(httpServletRequest, "an-unexpected-error-occurred"));
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseLaunchMVCActionCommand.class);

}