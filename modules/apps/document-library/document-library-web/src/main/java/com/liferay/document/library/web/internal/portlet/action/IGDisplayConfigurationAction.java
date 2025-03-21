/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.display.context.IGConfigurationDisplayContext;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.trash.TrashHelper;

import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.MEDIA_GALLERY_DISPLAY,
	service = ConfigurationAction.class
)
public class IGDisplayConfigurationAction
	extends BaseValidateRootFolderConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/image_gallery_display/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			IGConfigurationDisplayContext.class.getName(),
			new IGConfigurationDisplayContext(
				_itemSelector, httpServletRequest,
				_portletPreferencesLocalService, _trashHelper));

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Reference
	private TrashHelper _trashHelper;

}