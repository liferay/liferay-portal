/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.rss.web.internal.portlet;

import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.rss.constants.RSSPortletKeys;
import com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	configurationPid = "com.liferay.rss.web.internal.configuration.RSSWebCacheConfiguration",
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.application-type=widget",
		"com.liferay.portlet.css-class-wrapper=portlet-rss",
		"com.liferay.portlet.display-category=category.news",
		"com.liferay.portlet.icon=/icons/rss.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=0",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=RSS",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + RSSPortletKeys.RSS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class RSSPortlet extends MVCPortlet {

	@Override
	public void doView(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			RSSWebCacheConfiguration.class.getName(),
			_rssWebCacheConfiguration);

		super.doView(renderRequest, renderResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_rssWebCacheConfiguration = ConfigurableUtil.createConfigurable(
			RSSWebCacheConfiguration.class, properties);

		_portletRegistry.registerAlias("rss", RSSPortletKeys.RSS);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	private static final String _ALIAS = "rss";

	@Reference
	private PortletRegistry _portletRegistry;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.rss.web)(&(release.schema.version>=3.0.0)(!(release.schema.version>=4.0.0))))"
	)
	private Release _release;

	private volatile RSSWebCacheConfiguration _rssWebCacheConfiguration;

}