/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.connector.web.internal.display.context;

import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemBuilder;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.NavigationItemList;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.List;

/**
 * @author Katie Nesterovich
 */
public class CommerceAvalaraDisplayContext {

	public CommerceAvalaraDisplayContext(
		Language language, RenderRequest renderRequest,
		RenderResponse renderResponse) {

		_language = language;
		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public List<NavigationItem> getNavigationItems() {
		String toolbarItem = ParamUtil.getString(
			_renderRequest, "toolbarItem", "view-credentials");

		return NavigationItemList.of(
			NavigationItemBuilder.setActive(
				toolbarItem.equals("view-credentials")
			).setHref(
				PortletURLBuilder.create(
					_renderResponse.createRenderURL()
				).setMVCPath(
					"/view.jsp"
				).setParameter(
					"toolbarItem", "view-credentials"
				).setParameter(
					"type", getType()
				).buildString()
			).setLabel(
				_language.get(_renderRequest.getLocale(), "credentials")
			).build());
	}

	public int getType() {
		return ParamUtil.getInteger(_renderRequest, "type");
	}

	private final Language _language;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}