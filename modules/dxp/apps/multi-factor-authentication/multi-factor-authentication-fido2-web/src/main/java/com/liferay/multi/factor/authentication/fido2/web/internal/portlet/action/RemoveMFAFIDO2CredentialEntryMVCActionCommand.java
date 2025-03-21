/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.multi.factor.authentication.fido2.web.internal.portlet.action;

import com.liferay.multi.factor.authentication.fido2.credential.model.MFAFIDO2CredentialEntry;
import com.liferay.multi.factor.authentication.fido2.credential.service.MFAFIDO2CredentialEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/multi_factor_authentication_fido2/remove_mfa_fido2_credential_entry"
	},
	service = MVCActionCommand.class
)
public class RemoveMFAFIDO2CredentialEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		MFAFIDO2CredentialEntry mfaFIDO2CredentialEntry =
			_mfaFIDO2CredentialEntryLocalService.getMFAFIDO2CredentialEntry(
				ParamUtil.getLong(actionRequest, "mfaFIDO2CredentialEntryId"));

		if ((themeDisplay == null) ||
			(themeDisplay.getUserId() != mfaFIDO2CredentialEntry.getUserId())) {

			throw new PrincipalException();
		}

		try {
			_mfaFIDO2CredentialEntryLocalService.deleteMFAFIDO2CredentialEntry(
				mfaFIDO2CredentialEntry.getMfaFIDO2CredentialEntryId());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(portalException);
			}
		}

		String redirect = _portal.escapeRedirect(
			ParamUtil.getString(actionRequest, "redirect"));

		if (Validator.isBlank(redirect)) {
			redirect = themeDisplay.getPortalURL();
		}

		actionResponse.sendRedirect(redirect);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RemoveMFAFIDO2CredentialEntryMVCActionCommand.class);

	@Reference
	private MFAFIDO2CredentialEntryLocalService
		_mfaFIDO2CredentialEntryLocalService;

	@Reference
	private Portal _portal;

}