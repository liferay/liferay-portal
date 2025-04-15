/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.sharing.web.internal.constants.SharingPortletKeys;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = "model.class.name=com.liferay.sharing.model.SharingEntry",
	service = PortletProvider.class
)
public class SharedAssetsPreviewPortletProvider extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return SharingPortletKeys.SHARED_ASSETS;
	}

	@Override
	public PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, Group group)
		throws PortalException {

		return PortletURLBuilder.create(
			super.getPortletURL(httpServletRequest, group)
		).setMVCRenderCommandName(
			"/sharing/view_sharing_entry"
		).buildPortletURL();
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	private final Action[] _supportedActions = {Action.PREVIEW};

}