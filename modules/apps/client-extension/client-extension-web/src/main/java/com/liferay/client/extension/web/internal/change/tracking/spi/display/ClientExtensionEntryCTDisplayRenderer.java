/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.change.tracking.spi.display;

import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.client.extension.model.ClientExtensionEntry;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brooke Dalton
 */
@Component(service = CTDisplayRenderer.class)
public class ClientExtensionEntryCTDisplayRenderer
	extends BaseCTDisplayRenderer<ClientExtensionEntry> {

	@Override
	public String getEditURL(
			HttpServletRequest httpServletRequest,
			ClientExtensionEntry clientExtensionEntry)
		throws PortalException {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/client_extension_admin/edit_client_extension_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"externalReferenceCode",
			clientExtensionEntry.getExternalReferenceCode()
		).buildString();
	}

	@Override
	public Class<ClientExtensionEntry> getModelClass() {
		return ClientExtensionEntry.class;
	}

	@Override
	public String getTitle(
		Locale locale, ClientExtensionEntry clientExtensionEntry) {

		return clientExtensionEntry.getName(locale);
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<ClientExtensionEntry> displayBuilder) {

		ClientExtensionEntry clientExtensionEntry = displayBuilder.getModel();

		displayBuilder.display(
			"name", clientExtensionEntry.getName(displayBuilder.getLocale())
		).display(
			"description", clientExtensionEntry.getDescription(), true, true
		).display(
			"created-by",
			() -> {
				String userName = clientExtensionEntry.getUserName();

				if (Validator.isNotNull(userName)) {
					return userName;
				}

				return null;
			}
		).display(
			"source-code-url", clientExtensionEntry.getSourceCodeURL()
		).display(
			"type", clientExtensionEntry.getType()
		).display(
			"type-settings", clientExtensionEntry.getTypeSettings()
		).display(
			"properties", clientExtensionEntry.getProperties()
		).display(
			"create-date", clientExtensionEntry.getCreateDate()
		).display(
			"last-modified", clientExtensionEntry.getModifiedDate()
		);
	}

	@Reference
	private Portal _portal;

}