/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action;

import com.liferay.digital.signature.constants.DSRequestConstants;
import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSRequestManager;
import com.liferay.digital.signature.model.DSRequest;
import com.liferay.digital.signature.model.DSRequestRecipient;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Danny Situ
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DigitalSignaturePortletKeys.SIGN_DIGITAL_SIGNATURE,
		"mvc.command.name=/digital_signature/complete_ds_recipient_view"
	},
	service = MVCActionCommand.class
)
public class CompleteDSRecipientViewMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		DSRequest dsRequest = _dsRequestManager.fetchDSRequest(
			themeDisplay.getCompanyId(),
			ParamUtil.getLong(actionRequest, "dsRequestId"));

		if ((dsRequest != null) &&
			_dsRequestManager.hasPermission(
				themeDisplay.getPermissionChecker(), dsRequest,
				ActionKeys.VIEW)) {

			_updateDSRequest(actionRequest, dsRequest, themeDisplay);
		}

		SessionMessages.add(
			actionRequest,
			_portal.getPortletId(actionRequest) +
				SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_SUCCESS_MESSAGE);

		String backURL = _portal.escapeRedirect(
			ParamUtil.getString(actionRequest, "backURL"));

		if (Validator.isNull(backURL)) {
			backURL = themeDisplay.getURLHome();
		}

		actionResponse.sendRedirect(backURL);
	}

	private boolean _isCompleted(DSRequest dsRequest, User user) {
		if (dsRequest == null) {
			return false;
		}

		for (DSRequestRecipient dsRequestRecipient :
				dsRequest.getDSRequestRecipients()) {

			if ((dsRequestRecipient.getUserId() == user.getUserId()) &&
				Objects.equals(
					DSRequestConstants.STATUS_COMPLETED,
					dsRequestRecipient.getStatus())) {

				return true;
			}
		}

		return false;
	}

	private void _updateDSRequest(
		ActionRequest actionRequest, DSRequest dsRequest,
		ThemeDisplay themeDisplay) {

		try {
			_dsRequestManager.updateDSRequest(dsRequest.getDSRequestId());

			if (!_isCompleted(
					_dsRequestManager.fetchDSRequest(
						themeDisplay.getCompanyId(),
						dsRequest.getDSRequestId()),
					themeDisplay.getUser())) {

				return;
			}

			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(actionRequest);

			SessionMessages.add(
				httpServletRequest, "requestProcessed",
				_language.get(
					httpServletRequest, "your-document-has-been-signed"));
		}
		catch (Exception exception) {
			_log.error(
				"Unable to verify signing for digital signature request " +
					dsRequest.getDSRequestId(),
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompleteDSRecipientViewMVCActionCommand.class);

	@Reference
	private DSRequestManager _dsRequestManager;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}