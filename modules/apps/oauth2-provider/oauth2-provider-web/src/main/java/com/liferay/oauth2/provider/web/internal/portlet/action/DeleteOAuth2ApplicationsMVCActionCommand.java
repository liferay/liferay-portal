/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.oauth2.provider.service.OAuth2ApplicationService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
 */
@Component(
	configurationPid = "com.liferay.oauth2.provider.configuration.OAuth2ProviderConfiguration",
	property = {
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN,
		"mvc.command.name=/oauth2_provider/delete_oauth2_applications"
	},
	service = MVCActionCommand.class
)
public class DeleteOAuth2ApplicationsMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		long[] oAuth2ApplicationIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "oAuth2ApplicationIds"), 0L);

		try {
			for (long oAuth2ApplicationId : oAuth2ApplicationIds) {
				_oAuth2ApplicationService.deleteOAuth2Application(
					oAuth2ApplicationId);
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			SessionErrors.add(actionRequest, portalException.getClass());
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DeleteOAuth2ApplicationsMVCActionCommand.class);

	@Reference
	private OAuth2ApplicationService _oAuth2ApplicationService;

}