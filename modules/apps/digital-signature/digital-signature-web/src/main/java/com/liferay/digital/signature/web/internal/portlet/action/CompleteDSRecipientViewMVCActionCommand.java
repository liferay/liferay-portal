/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.digital.signature.model.DSEnvelope;
import com.liferay.digital.signature.model.DSRecipient;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

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

		String dsEnvelopeId = ParamUtil.getString(
			actionRequest, "dsEnvelopeId");

		if (Validator.isNotNull(dsEnvelopeId)) {
			_processSignedDSEnvelope(actionRequest, dsEnvelopeId, themeDisplay);
		}

		String backURL = ParamUtil.getString(actionRequest, "backURL");

		if (Validator.isNull(backURL)) {
			backURL = themeDisplay.getURLHome();
		}

		actionResponse.sendRedirect(backURL);
	}

	private DSRecipient _getSignedDSRecipient(
		DSEnvelope dsEnvelope, User user) {

		List<DSRecipient> dsRecipients = dsEnvelope.getDSRecipients();

		if (dsRecipients == null) {
			return null;
		}

		String emailAddress = user.getEmailAddress();

		for (DSRecipient dsRecipient : dsRecipients) {
			if (StringUtil.equalsIgnoreCase(
					emailAddress, dsRecipient.getEmailAddress()) &&
				StringUtil.equalsIgnoreCase(
					"completed", dsRecipient.getStatus())) {

				return dsRecipient;
			}
		}

		return null;
	}

	private void _processSignedDSEnvelope(
		ActionRequest actionRequest, String dsEnvelopeId,
		ThemeDisplay themeDisplay) {

		long companyId = themeDisplay.getCompanyId();
		long groupId = themeDisplay.getScopeGroupId();

		try {
			DSRecipient dsRecipient = _getSignedDSRecipient(
				_dsEnvelopeManager.getDSEnvelope(
					companyId, groupId, dsEnvelopeId,
					"custom_fields,recipients"),
				themeDisplay.getUser());

			if (dsRecipient == null) {
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
				"Unable to verify signing for envelope " + dsEnvelopeId,
				exception);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompleteDSRecipientViewMVCActionCommand.class);

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}