/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.navigation.web.internal.portlet;

import com.liferay.asset.tags.navigation.constants.AssetTagsNavigationPortletKeys;
import com.liferay.asset.tags.navigation.web.internal.display.context.AssetTagsNavigationDisplayContext;
import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-asset-tags-navigation",
		"com.liferay.portlet.display-category=category.cms",
		"com.liferay.portlet.icon=/icons/asset_tags_cloud.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Asset Tags Cloud",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + AssetTagsNavigationPortletKeys.ASSET_TAGS_CLOUD,
		"jakarta.portlet.preferences=classpath:/META-INF/portlet-preferences/default-portlet-preferences.xml",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.supported-public-render-parameter=resetCur",
		"jakarta.portlet.supported-public-render-parameter=tag",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AssetTagsCloudPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new AssetTagsNavigationDisplayContext(renderRequest));

		super.render(renderRequest, renderResponse);
	}

	@Activate
	protected void activate() {
		_portletRegistry.registerAlias(
			_ALIAS, AssetTagsNavigationPortletKeys.ASSET_TAGS_CLOUD);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	private static final String _ALIAS = "tag-cloud";

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.asset.tags.navigation.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

}