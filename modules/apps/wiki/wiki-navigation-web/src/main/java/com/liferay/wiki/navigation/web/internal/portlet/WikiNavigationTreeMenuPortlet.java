/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.navigation.web.internal.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.wiki.configuration.WikiGroupServiceConfiguration;
import com.liferay.wiki.navigation.web.internal.constants.WikiNavigationPortletKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	configurationPid = "com.liferay.wiki.configuration.WikiGroupServiceConfiguration",
	property = {
		"com.liferay.portlet.css-class-wrapper=wiki-navigation-portlet-tree-menu",
		"com.liferay.portlet.display-category=category.wiki",
		"com.liferay.portlet.header-portlet-css=/tree_menu/css/main.css",
		"com.liferay.portlet.icon=/tree_menu/icons/tree_menu.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.scopeable=true",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Tree Menu",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/tree_menu/view.jsp",
		"jakarta.portlet.name=" + WikiNavigationPortletKeys.TREE_MENU,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user",
		"jakarta.portlet.supported-public-render-parameter=nodeId;http://www.liferay.com/public-render-parameters/wiki",
		"jakarta.portlet.supported-public-render-parameter=title;http://www.liferay.com/public-render-parameters/wiki",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class WikiNavigationTreeMenuPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		if (!FeatureFlagManagerUtil.isEnabled(
				_portal.getCompanyId(renderRequest), "LPD-35013")) {

			return;
		}

		renderRequest.setAttribute(
			WikiGroupServiceConfiguration.class.getName(),
			_wikiGroupServiceConfiguration);

		super.render(renderRequest, renderResponse);
	}

	@Activate
	protected void activate(Map<String, Object> properties) {
		_wikiGroupServiceConfiguration = ConfigurableUtil.createConfigurable(
			WikiGroupServiceConfiguration.class, properties);
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.wiki.navigation.web)(&(release.schema.version>=1.0.0)(!(release.schema.version>=2.0.0))))"
	)
	private Release _release;

	private WikiGroupServiceConfiguration _wikiGroupServiceConfiguration;

}