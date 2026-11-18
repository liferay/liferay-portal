/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.web.internal.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.sharing.web.internal.constants.SharingPortletKeys;
import com.liferay.sharing.web.internal.display.context.ViewSharedAssetsDisplayContext;
import com.liferay.sharing.web.internal.display.context.ViewSharedAssetsDisplayContextFactory;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=shared-assets-portlet",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Shared Content",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/shared_assets/view.jsp",
		"jakarta.portlet.name=" + SharingPortletKeys.SHARED_ASSETS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SharedAssetsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			ViewSharedAssetsDisplayContext.class.getName(),
			_viewSharedAssetsDisplayContextFactory.
				getViewSharedAssetsDisplayContext(
					renderRequest, renderResponse));

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private ViewSharedAssetsDisplayContextFactory
		_viewSharedAssetsDisplayContextFactory;

}