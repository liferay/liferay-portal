/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet.action;

import com.liferay.oauth2.provider.service.OAuth2AuthorizationService;
import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Tomas Polesovsky
 * @author Stian Sigvartsen
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_ADMIN,
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_CONNECTED_APPLICATIONS,
		"mvc.command.name=/admin/revoke_oauth2_authorizations",
		"mvc.command.name=/connected_applications/revoke_oauth2_authorizations"
	},
	service = MVCActionCommand.class
)
public class RevokeOAuth2AuthorizationsMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws PortletException {

		long oAuth2ApplicationId = ParamUtil.getLong(
			actionRequest, "oAuth2ApplicationId", -1);

		long[] oAuth2AuthorizationIds = StringUtil.split(
			ParamUtil.getString(actionRequest, "oAuth2AuthorizationIds"), 0L);

		try {
			if ((oAuth2AuthorizationIds.length == 0) &&
				(oAuth2ApplicationId != -1)) {

				_oAuth2AuthorizationService.revokeAllOAuth2Authorizations(
					oAuth2ApplicationId);
			}
			else {
				for (long oAuth2AuthorizationId : oAuth2AuthorizationIds) {
					_oAuth2AuthorizationService.revokeOAuth2Authorization(
						oAuth2AuthorizationId);
				}
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			SessionErrors.add(actionRequest, portalException.getClass());
		}

		String backURL = ParamUtil.get(
			actionRequest, "backURL", StringPool.BLANK);

		if (Validator.isNotNull(backURL)) {
			actionResponse.setRenderParameter("redirect", backURL);
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RevokeOAuth2AuthorizationsMVCActionCommand.class);

	@Reference
	private OAuth2AuthorizationService _oAuth2AuthorizationService;

}