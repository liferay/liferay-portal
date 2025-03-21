/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.display.context;

import com.liferay.client.extension.type.factory.CETFactory;
import com.liferay.client.extension.web.internal.display.context.util.CETLabelUtil;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.portlet.url.builder.ResourceURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public class ClientExtensionAdminDisplayContext {

	public ClientExtensionAdminDisplayContext(
		CETFactory cetFactory, PortletRequest portletRequest,
		PortletResponse portletResponse) {

		_cetFactory = cetFactory;

		_liferayPortletRequest = PortalUtil.getLiferayPortletRequest(
			portletRequest);
		_liferayPortletResponse = PortalUtil.getLiferayPortletResponse(
			portletResponse);
	}

	public CreationMenu getCreationMenu() {
		CreationMenu creationMenu = new CreationMenu();

		for (String type : _cetFactory.getTypes()) {
			String key = CETFactory.FEATURE_FLAG_KEYS.get(type);

			if ((key != null) && !FeatureFlagManagerUtil.isEnabled(key)) {
				continue;
			}

			creationMenu.addDropdownItem(
				dropdownItem -> {
					dropdownItem.setHref(
						PortletURLBuilder.createRenderURL(
							_liferayPortletResponse
						).setMVCRenderCommandName(
							"/client_extension_admin" +
								"/edit_client_extension_entry"
						).setRedirect(
							_getRedirect()
						).setParameter(
							"type", type
						).buildPortletURL());
					dropdownItem.setLabel(
						CETLabelUtil.getAddLabel(
							_liferayPortletRequest.getLocale(), type));
				});
		}

		return creationMenu;
	}

	public String getImportClientExtensionEntrySuccessURL() {
		return PortletURLBuilder.createRenderURL(
			_liferayPortletResponse
		).buildString();
	}

	public String getImportClientExtensionEntryURL() {
		return ResourceURLBuilder.createResourceURL(
			_liferayPortletResponse
		).setResourceID(
			"/client_extension_admin/import_client_extension_entry"
		).buildString();
	}

	public String getRedirect() {
		return ParamUtil.getString(_liferayPortletRequest, "redirect");
	}

	private HttpServletRequest _getHttpServletRequest() {
		return PortalUtil.getHttpServletRequest(_liferayPortletRequest);
	}

	private String _getRedirect() {
		return PortalUtil.getCurrentURL(_getHttpServletRequest());
	}

	private final CETFactory _cetFactory;
	private final LiferayPortletRequest _liferayPortletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}