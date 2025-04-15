/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN,
		"mvc.command.name=/admin/delete_saml_idp_sp_connection"
	},
	service = MVCActionCommand.class
)
public class DeleteSamlIdpSpConnectionMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long samlIdpSpConnectionId = ParamUtil.getLong(
			actionRequest, "samlIdpSpConnectionId");

		_samlIdpSpConnectionLocalService.deleteSamlIdpSpConnection(
			samlIdpSpConnectionId);
	}

	@Reference
	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;

}