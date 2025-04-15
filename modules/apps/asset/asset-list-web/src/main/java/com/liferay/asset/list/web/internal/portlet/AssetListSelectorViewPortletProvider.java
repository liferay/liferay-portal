/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.portlet;

import com.liferay.asset.list.constants.AssetListPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.BasePortletProvider;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pavel Savinov
 */
@Component(
	property = "model.class.name=com.liferay.asset.list.model.AssetListEntry",
	service = PortletProvider.class
)
public class AssetListSelectorViewPortletProvider extends BasePortletProvider {

	@Override
	public String getPortletName() {
		return AssetListPortletKeys.ASSET_LIST;
	}

	@Override
	public PortletURL getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		return PortletURLBuilder.create(
			super.getPortletURL(httpServletRequest)
		).setMVCRenderCommandName(
			"/asset_list/view_list_items"
		).buildPortletURL();
	}

	@Override
	public Action[] getSupportedActions() {
		return _supportedActions;
	}

	private final Action[] _supportedActions = {Action.VIEW};

}