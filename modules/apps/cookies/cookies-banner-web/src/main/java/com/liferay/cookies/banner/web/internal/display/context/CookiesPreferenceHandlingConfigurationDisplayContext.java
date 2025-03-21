/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.banner.web.internal.display.context;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.ResourceURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Rachael Koestartyo
 */
public class CookiesPreferenceHandlingConfigurationDisplayContext {

	public CookiesPreferenceHandlingConfigurationDisplayContext(
		CookiesConfigurationProvider cookiesConfigurationProvider,
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse,
		ExtendedObjectClassDefinition.Scope scope, long scopePK) {

		_cookiesConfigurationProvider = cookiesConfigurationProvider;
		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_scope = scope;
		_scopePK = scopePK;
	}

	public boolean getCookiesPreferenceHandlingEnabled() {
		return _cookiesConfigurationProvider.isCookiesPreferenceHandlingEnabled(
			_scope, _scopePK);
	}

	public boolean getCookiesPreferenceHandlingExplicitConsentMode() {
		return _cookiesConfigurationProvider.
			isCookiesPreferenceHandlingExplicitConsentMode(_scope, _scopePK);
	}

	public String getDeleteConfigurationActionURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/instance_settings" +
				"/delete_cookies_preference_handling_configuration"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"scope", _scope
		).setParameter(
			"scopePK", _scopePK
		).buildString();
	}

	public String getEditCookiesPreferenceHandlingConfigurationURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/instance_settings/edit_cookies_preference_handling_configuration"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"scope", _scope
		).setParameter(
			"scopePK", _scopePK
		).buildString();
	}

	public String getExportConfigurationActionURL() throws PortalException {
		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			RequestBackedPortletURLFactoryUtil.create(_httpServletRequest);

		String portletId = null;

		if (_scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			portletId = ConfigurationAdminPortletKeys.INSTANCE_SETTINGS;
		}
		else if (_scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			portletId = ConfigurationAdminPortletKeys.SITE_SETTINGS;
		}
		else if (_scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			portletId = ConfigurationAdminPortletKeys.SYSTEM_SETTINGS;
		}

		ResourceURL resourceURL =
			(ResourceURL)requestBackedPortletURLFactory.createResourceURL(
				portletId);

		if (_scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			resourceURL.setParameters(
				HttpComponentsUtil.getParameterMap(
					_cookiesConfigurationProvider.getCompanyConfigurationURL(
						_httpServletRequest)));
		}
		else if (_scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			resourceURL.setParameters(
				HttpComponentsUtil.getParameterMap(
					_cookiesConfigurationProvider.getGroupConfigurationURL(
						_httpServletRequest)));
		}
		else if (_scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			resourceURL.setParameters(
				HttpComponentsUtil.getParameterMap(
					_cookiesConfigurationProvider.getSystemConfigurationURL(
						_httpServletRequest)));
		}

		resourceURL.setResourceID("/configuration_admin/export_configuration");

		return resourceURL.toString();
	}

	public boolean isCookiesPreferenceHandlingConfigurationDefined()
		throws Exception {

		return _cookiesConfigurationProvider.
			isCookiesPreferenceHandlingConfigurationDefined(_scope, _scopePK);
	}

	private final CookiesConfigurationProvider _cookiesConfigurationProvider;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final ExtendedObjectClassDefinition.Scope _scope;
	private final long _scopePK;

}