/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.display.context;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.fragment.web.internal.configuration.helper.FragmentServiceConfigurationHelper;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
public class FragmentServiceConfigurationDisplayContext {

	public FragmentServiceConfigurationDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse,
		FragmentServiceConfigurationHelper fragmentServiceConfigurationHelper,
		String scope) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
		_fragmentServiceConfigurationHelper =
			fragmentServiceConfigurationHelper;
		_scope = scope;
	}

	public String getEditFragmentServiceConfigurationURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/instance_settings/edit_fragment_service_configuration"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"scope", _scope
		).setParameter(
			"scopePK", _getScopePK()
		).buildString();
	}

	public String getPropagateContributedFragmentEntriesChangesURL() {
		return PortletURLBuilder.createActionURL(
			_liferayPortletResponse
		).setActionName(
			"/instance_settings/propagate_contributed_fragment_entries_changes"
		).setRedirect(
			PortalUtil.getCurrentURL(_httpServletRequest)
		).setParameter(
			"scope", _scope
		).setParameter(
			"scopePK", _getScopePK()
		).buildString();
	}

	public String getRedirect() {
		return PortletURLBuilder.create(
			PortalUtil.getControlPanelPortletURL(
				_httpServletRequest,
				ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
				PortletRequest.RENDER_PHASE)
		).buildString();
	}

	public boolean isAlreadyPropagateContributedFragmentChanges() {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PortletPreferences portletPreferences =
			PortalPreferencesLocalServiceUtil.getPreferences(
				themeDisplay.getCompanyId(),
				PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		return GetterUtil.getBoolean(
			portletPreferences.getValue(
				"alreadyPropagateContributedFragmentChanges", null));
	}

	public boolean isPropagateChangesEnabled() {
		return _fragmentServiceConfigurationHelper.isPropagateChanges(
			_scope, _getScopePK());
	}

	public boolean isPropagateContributedFragmentChangesEnabled() {
		return _fragmentServiceConfigurationHelper.
			isPropagateContributedFragmentChanges(_scope, _getScopePK());
	}

	public boolean showInfoMessage() throws Exception {
		if (!Objects.equals(
				_scope,
				ExtendedObjectClassDefinition.Scope.COMPANY.getValue()) ||
			_fragmentServiceConfigurationHelper.hasScopedConfiguration(
				_getScopePK())) {

			return false;
		}

		return true;
	}

	private long _getScopePK() {
		if (Objects.equals(
				_scope,
				ExtendedObjectClassDefinition.Scope.COMPANY.getValue())) {

			ThemeDisplay themeDisplay =
				(ThemeDisplay)_httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return themeDisplay.getCompanyId();
		}
		else if (Objects.equals(
					_scope,
					ExtendedObjectClassDefinition.Scope.SYSTEM.getValue())) {

			return 0L;
		}

		throw new IllegalArgumentException("Unsupported scope: " + _scope);
	}

	private final FragmentServiceConfigurationHelper
		_fragmentServiceConfigurationHelper;
	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;
	private final String _scope;

}