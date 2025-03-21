/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.portlet.action;

import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.service.ClientExtensionEntryService;
import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminPortletKeys;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminWebKeys;
import com.liferay.client.extension.web.internal.display.context.EditClientExtensionEntryDisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropertiesUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bruno Basto
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN,
		"mvc.command.name=/client_extension_admin/edit_client_extension_entry"
	},
	service = MVCActionCommand.class
)
public class EditClientExtensionEntryMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

			if (cmd.equals(Constants.ADD)) {
				_add(actionRequest);
			}
			else if (cmd.equals(Constants.UPDATE)) {
				_update(actionRequest);
			}

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			if (Validator.isNotNull(redirect)) {
				actionResponse.sendRedirect(redirect);
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			SessionErrors.add(actionRequest, exception.getClass(), exception);

			CET cet = null;

			ClientExtensionEntry clientExtensionEntry =
				_fetchClientExtensionEntry(actionRequest);

			if (clientExtensionEntry != null) {
				cet = _cetFactory.create(clientExtensionEntry, false);
			}
			else {
				cet = _cetFactory.create(actionRequest);
			}

			actionRequest.setAttribute(
				ClientExtensionAdminWebKeys.
					EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT,
				new EditClientExtensionEntryDisplayContext(
					clientExtensionEntry == null, cet, actionRequest));

			actionResponse.setRenderParameter(
				"mvcPath", "/admin/edit_client_extension_entry.jsp");
		}
	}

	private void _add(ActionRequest actionRequest) throws PortalException {
		CET cet = _cetFactory.create(actionRequest);

		_clientExtensionEntryService.addClientExtensionEntry(
			cet.getExternalReferenceCode(), cet.getDescription(),
			_localization.getLocalizationMap(actionRequest, "name"),
			ParamUtil.getString(actionRequest, "properties"),
			cet.getSourceCodeURL(), cet.getType(), cet.getTypeSettings());
	}

	private ClientExtensionEntry _fetchClientExtensionEntry(
			ActionRequest actionRequest)
		throws PortalException {

		String externalReferenceCode = ParamUtil.getString(
			actionRequest, "externalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		return _clientExtensionEntryService.
			fetchClientExtensionEntryByExternalReferenceCode(
				externalReferenceCode, _portal.getCompanyId(actionRequest));
	}

	private void _update(ActionRequest actionRequest) throws PortalException {
		ClientExtensionEntry clientExtensionEntry = _fetchClientExtensionEntry(
			actionRequest);

		CET cet = _cetFactory.create(actionRequest);

		_clientExtensionEntryService.updateClientExtensionEntry(
			clientExtensionEntry.getClientExtensionEntryId(),
			cet.getDescription(),
			_localization.getLocalizationMap(actionRequest, "name"),
			PropertiesUtil.toString(cet.getProperties()),
			cet.getSourceCodeURL(), cet.getTypeSettings());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditClientExtensionEntryMVCActionCommand.class);

	@Reference
	private CETFactory _cetFactory;

	@Reference
	private ClientExtensionEntryService _clientExtensionEntryService;

	@Reference
	private Localization _localization;

	@Reference
	private Portal _portal;

}