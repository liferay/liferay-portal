/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.bar.portlet;

import com.liferay.fragment.processor.PortletRegistry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.web.constants.SearchBarPortletKeys;
import com.liferay.portal.search.web.internal.portlet.preferences.PortletPreferencesLookup;
import com.liferay.portal.search.web.internal.search.bar.portlet.display.context.SearchBarPortletDisplayContext;
import com.liferay.portal.search.web.internal.search.bar.portlet.display.context.factory.SearchBarPortletDisplayContextFactory;
import com.liferay.portal.search.web.internal.search.bar.portlet.helper.SearchBarPrecedenceHelper;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;

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
 * @author André de Oliveira
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-search-bar",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Search Bar",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/search/bar/view.jsp",
		"jakarta.portlet.name=" + SearchBarPortletKeys.SEARCH_BAR,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SearchBarPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		SearchBarPortletDisplayContextFactory
			searchBarPortletDisplayContextFactory = null;

		try {
			searchBarPortletDisplayContextFactory =
				new SearchBarPortletDisplayContextFactory(
					layoutLocalService, portal, renderRequest,
					userLocalService);

			SearchBarPortletDisplayContext searchBarPortletDisplayContext =
				searchBarPortletDisplayContextFactory.create(
					portletPreferencesLookup, portletSharedSearchRequest,
					searchBarPrecedenceHelper, searchCapabilities);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				searchBarPortletDisplayContext);

			if (searchBarPortletDisplayContext.isDestinationUnreachable() ||
				searchBarPortletDisplayContext.isRenderNothing()) {

				renderRequest.setAttribute(
					WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
			}
		}
		catch (ConfigurationException configurationException) {
			if (_log.isWarnEnabled()) {
				_log.warn(configurationException);
			}
		}

		super.render(renderRequest, renderResponse);
	}

	@Activate
	protected void activate() {
		_portletRegistry.registerAlias(_ALIAS, SearchBarPortletKeys.SEARCH_BAR);
	}

	@Deactivate
	protected void deactivate() {
		_portletRegistry.unregisterAlias(_ALIAS);
	}

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected PortletPreferencesLookup portletPreferencesLookup;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	protected SearchBarPrecedenceHelper searchBarPrecedenceHelper;

	@Reference
	protected SearchCapabilities searchCapabilities;

	@Reference
	protected UserLocalService userLocalService;

	private static final String _ALIAS = "search-bar";

	private static final Log _log = LogFactoryUtil.getLog(
		SearchBarPortlet.class);

	@Reference
	private PortletRegistry _portletRegistry;

}