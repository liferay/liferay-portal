/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN,
		"mvc.command.name=/oauth_client_admin/update_oauth_client_entry"
	},
	service = MVCActionCommand.class
)
public class UpdateOAuthClientEntryMVCActionCommand
	implements MVCActionCommand {

	@Override
	public boolean processAction(
		ActionRequest actionRequest, ActionResponse actionResponse) {

		try {
			long oAuthClientEntryId = ParamUtil.getLong(
				actionRequest, "oAuthClientEntryId");

			String authRequestParametersJSON = ParamUtil.getString(
				actionRequest, "authRequestParametersJSON");
			String authServerWellKnownURI = ParamUtil.getString(
				actionRequest, "authServerWellKnownURI");
			String customClaimsJSON = _getCustomClaimsJSON(actionRequest);
			String infoJSON = ParamUtil.getString(actionRequest, "infoJSON");
			String matcherField = ParamUtil.getString(
				actionRequest, "matcherField");
			long metadataCacheTime = ParamUtil.getLong(
				actionRequest, "metadataCacheTime");
			String oidcUserInfoMapperJSON = ParamUtil.getString(
				actionRequest, "OIDCUserInfoMapperJSON");
			int tokenConnectionTimeout = ParamUtil.getInteger(
				actionRequest, "tokenConnectionTimeout");
			String tokenRequestParametersJSON = ParamUtil.getString(
				actionRequest, "tokenRequestParametersJSON");

			if (oAuthClientEntryId > 0) {
				_oAuthClientEntryService.updateOAuthClientEntry(
					oAuthClientEntryId, authRequestParametersJSON,
					authServerWellKnownURI, customClaimsJSON, infoJSON,
					matcherField, metadataCacheTime, oidcUserInfoMapperJSON,
					tokenConnectionTimeout, tokenRequestParametersJSON);
			}
			else {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				_oAuthClientEntryService.addOAuthClientEntry(
					null, themeDisplay.getUserId(), authRequestParametersJSON,
					authServerWellKnownURI, customClaimsJSON, infoJSON,
					matcherField, metadataCacheTime, oidcUserInfoMapperJSON,
					tokenConnectionTimeout, tokenRequestParametersJSON);
			}

			return true;
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.error(portalException);
			}

			Class<?> clazz = portalException.getClass();

			SessionErrors.add(actionRequest, clazz.getName(), portalException);

			return false;
		}
	}

	private String _getCustomClaimsJSON(ActionRequest actionRequest) {
		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(actionRequest), "LPD-49855")) {

			return null;
		}

		JSONObject jsonObject = _jsonFactory.createJSONObject();

		int[] indexes = ParamUtil.getIntegerValues(
			actionRequest, "customClaimsIndexes");

		for (int index : indexes) {
			String key = ParamUtil.getString(
				actionRequest, "customClaimsKey-" + index);
			String value = ParamUtil.getString(
				actionRequest, "customClaimsValue-" + index);

			if (Validator.isNull(key) || Validator.isNull(value)) {
				continue;
			}

			jsonObject.put(key, value);
		}

		return jsonObject.toString();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UpdateOAuthClientEntryMVCActionCommand.class);

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private OAuthClientEntryService _oAuthClientEntryService;

	@Reference
	private Portal _portal;

}