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
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN,
		"mvc.command.name=/client_extension_admin/edit_client_extension_entry"
	},
	service = MVCRenderCommand.class
)
public class EditClientExtensionEntryMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CET cet = null;

			ClientExtensionEntry clientExtensionEntry =
				_fetchClientExtensionEntry(renderRequest);

			if (clientExtensionEntry != null) {
				cet = _cetFactory.create(clientExtensionEntry, false);
			}
			else {
				cet = _cetFactory.create(renderRequest);
			}

			renderRequest.setAttribute(
				ClientExtensionAdminWebKeys.
					EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT,
				new EditClientExtensionEntryDisplayContext(
					clientExtensionEntry == null, cet, renderRequest));

			return "/admin/edit_client_extension_entry.jsp";
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private ClientExtensionEntry _fetchClientExtensionEntry(
			RenderRequest renderRequest)
		throws PortalException {

		String externalReferenceCode = ParamUtil.getString(
			renderRequest, "externalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		return _clientExtensionEntryService.
			fetchClientExtensionEntryByExternalReferenceCode(
				externalReferenceCode, _portal.getCompanyId(renderRequest));
	}

	@Reference
	private CETFactory _cetFactory;

	@Reference
	private ClientExtensionEntryService _clientExtensionEntryService;

	@Reference
	private Portal _portal;

}