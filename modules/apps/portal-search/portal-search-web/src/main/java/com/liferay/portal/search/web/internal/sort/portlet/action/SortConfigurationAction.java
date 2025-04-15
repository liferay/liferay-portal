/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.sort.portlet.action;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.sort.constants.SortPortletKeys;
import com.liferay.portal.search.web.internal.sort.display.context.builder.SortDisplayContextBuilder;
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferences;
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferencesImpl;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portlet.display.template.portlet.action.BaseConfigurationAction;

import jakarta.portlet.PortletConfig;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	property = "jakarta.portlet.name=" + SortPortletKeys.SORT,
	service = ConfigurationAction.class
)
public class SortConfigurationAction extends BaseConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/sort/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		RenderRequest renderRequest =
			(RenderRequest)httpServletRequest.getAttribute(
				JavaConstants.JAVAX_PORTLET_REQUEST);

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(renderRequest);

		SortPortletPreferences sortPortletPreferences =
			new SortPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		SortDisplayContextBuilder sortDisplayContextBuilder =
			_createSortDisplayContextBuilder(
				language, portal, renderRequest, sortPortletPreferences);

		httpServletRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, sortDisplayContextBuilder.build());

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	private SortDisplayContextBuilder _createSortDisplayContextBuilder(
		Language language, Portal portal, RenderRequest renderRequest,
		SortPortletPreferences sortPortletPreferences) {

		try {
			return new SortDisplayContextBuilder(
				language, portal, renderRequest, sortPortletPreferences);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	@Reference
	private PortletSharedSearchRequest _portletSharedSearchRequest;

}