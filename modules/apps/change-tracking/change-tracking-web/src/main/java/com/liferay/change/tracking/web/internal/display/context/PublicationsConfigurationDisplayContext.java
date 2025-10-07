/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.display.context;

import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Máté Thurzó
 * @author Samuel Trong Tran
 */
public class PublicationsConfigurationDisplayContext {

	public PublicationsConfigurationDisplayContext(
		CTSettingsConfigurationHelper ctSettingsConfigurationHelper,
		HttpServletRequest httpServletRequest, RenderResponse renderResponse,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_httpServletRequest = httpServletRequest;
		_renderResponse = renderResponse;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		CTSettingsConfiguration ctSettingsConfiguration =
			ctSettingsConfigurationHelper.getCTSettingsConfiguration(
				_themeDisplay.getCompanyId());

		_defaultOwnerActionIds =
			ctSettingsConfiguration.defaultOwnerActionIds();
		_publicationsEnabled = ctSettingsConfiguration.enabled();
		_remoteClientId = ctSettingsConfiguration.remoteClientId();
		_remoteClientSecret = ctSettingsConfiguration.remoteClientSecret();
		_remoteEnabled = ctSettingsConfiguration.remoteEnabled();
		_sandboxOnlyEnabled = ctSettingsConfiguration.sandboxEnabled();
		_unapprovedChangesAllowed =
			ctSettingsConfiguration.unapprovedChangesAllowed();
	}

	public String getActionURL() {
		return PortletURLBuilder.createActionURL(
			_renderResponse
		).setActionName(
			"/change_tracking/update_global_publications_configuration"
		).buildString();
	}

	public String getNavigation() {
		if (_navigation != null) {
			return _navigation;
		}

		if (isPublicationsEnabled()) {
			_navigation = ParamUtil.getString(
				_httpServletRequest, "navigation", "global-settings");
		}
		else {
			_navigation = "global-settings";
		}

		return _navigation;
	}

	public Map<String, Object> getReactData() {
		return HashMapBuilder.<String, Object>put(
			"defaultPermissions",
			() -> {
				HashMapBuilder.HashMapWrapper<Long, Object> hashMapWrapper =
					new HashMapBuilder.HashMapWrapper<>();

				for (String roleName : RoleConstants.SYSTEM_ROLES) {
					Role role = _roleLocalService.getRole(
						_themeDisplay.getCompanyId(), roleName);

					if (roleName.equals(RoleConstants.OWNER)) {
						if (ArrayUtil.isNotEmpty(_defaultOwnerActionIds)) {
							hashMapWrapper.put(
								role.getRoleId(), _defaultOwnerActionIds);
						}
						else {
							hashMapWrapper.put(
								role.getRoleId(),
								ResourceActionsUtil.
									getModelResourceOwnerDefaultActions(
										CTCollection.class.getName()));
						}
					}
					else {
						hashMapWrapper.put(
							role.getRoleId(),
							ResourcePermissionLocalServiceUtil.
								getAvailableResourcePermissionActionIds(
									_themeDisplay.getCompanyId(),
									CTCollection.class.getName(),
									ResourceConstants.SCOPE_COMPANY,
									String.valueOf(
										_themeDisplay.getCompanyId()),
									role.getRoleId(),
									ResourceActionsUtil.getResourceActions(
										null, CTCollection.class.getName())));
					}
				}

				return hashMapWrapper.build();
			}
		).put(
			"namespace", _renderResponse.getNamespace()
		).put(
			"roles",
			() -> {
				List<Map<String, Object>> roles = new ArrayList<>();

				for (String roleName : RoleConstants.SYSTEM_ROLES) {
					roles.add(
						HashMapBuilder.<String, Object>put(
							"label",
							LanguageUtil.get(
								_themeDisplay.getLocale(), roleName)
						).put(
							"name", roleName
						).build());
				}

				return roles;
			}
		).put(
			"updatePermissionsURL",
			() -> PortletURLBuilder.createActionURL(
				_renderResponse
			).setActionName(
				"/change_tracking/update_permissions"
			).buildString()
		).build();
	}

	public String getRemoteClientId() {
		return _remoteClientId;
	}

	public String getRemoteClientSecret() {
		return _remoteClientSecret;
	}

	public boolean hasPublishPermission() {
		if (_defaultOwnerActionIds.length == 0) {
			return true;
		}

		return ArrayUtil.contains(_defaultOwnerActionIds, CTActionKeys.PUBLISH);
	}

	public boolean isPublicationsEnabled() {
		return _publicationsEnabled;
	}

	public boolean isRemoteEnabled() {
		return _remoteEnabled;
	}

	public boolean isSandboxOnlyEnabled() {
		return _sandboxOnlyEnabled;
	}

	public boolean isUnapprovedChangesAllowed() {
		return _unapprovedChangesAllowed;
	}

	private final String[] _defaultOwnerActionIds;
	private final HttpServletRequest _httpServletRequest;
	private String _navigation;
	private final boolean _publicationsEnabled;
	private final String _remoteClientId;
	private final String _remoteClientSecret;
	private final boolean _remoteEnabled;
	private final RenderResponse _renderResponse;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;
	private final boolean _sandboxOnlyEnabled;
	private final ThemeDisplay _themeDisplay;
	private final boolean _unapprovedChangesAllowed;

}