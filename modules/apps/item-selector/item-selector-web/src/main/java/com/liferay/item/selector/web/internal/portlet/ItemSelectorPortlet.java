/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.web.internal.portlet;

import com.liferay.item.selector.ItemSelector;
import com.liferay.item.selector.ItemSelectorRendering;
import com.liferay.item.selector.constants.ItemSelectorPortletKeys;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jose A. Jimenez
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-item-selector",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.system=true",
		"com.liferay.portlet.use-default-template=false",
		"jakarta.portlet.display-name=Document Selector",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + ItemSelectorPortletKeys.ITEM_SELECTOR,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ItemSelectorPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ItemSelectorRendering itemSelectorRendering =
			_itemSelector.getItemSelectorRendering(
				RequestBackedPortletURLFactoryUtil.create(renderRequest),
				renderRequest.getParameterMap(), themeDisplay);

		LocalizedItemSelectorRendering localizedItemSelectorRendering =
			new LocalizedItemSelectorRendering(
				renderRequest.getLocale(), itemSelectorRendering);

		localizedItemSelectorRendering.store(renderRequest);

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.item.selector.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}