/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.digital.signature.web.internal.portlet.action;

import com.liferay.digital.signature.constants.DigitalSignaturePortletKeys;
import com.liferay.digital.signature.manager.DSEnvelopeManager;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Nícolas Moura
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DigitalSignaturePortletKeys.DIGITAL_SIGNATURE,
		"mvc.command.name=/digital_signature/delete_ds_envelope"
	},
	service = MVCActionCommand.class
)
public class DeleteDSEnvelopeMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_dsEnvelopeManager.deleteDSEnvelopes(
			themeDisplay.getCompanyId(), themeDisplay.getSiteGroupId(),
			ParamUtil.getString(actionRequest, "dsEnvelopeId"));
	}

	@Reference
	private DSEnvelopeManager _dsEnvelopeManager;

}