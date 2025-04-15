/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.portlet;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.BaseControlPanelEntry;
import com.liferay.portal.kernel.portlet.ControlPanelEntry;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.search.admin.web.internal.constants.SearchAdminPortletKeys;
import com.liferay.portal.search.configuration.ReindexConfiguration;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Adam Brandizzi
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.ReindexConfiguration",
	property = "jakarta.portlet.name=" + SearchAdminPortletKeys.SEARCH_ADMIN,
	service = ControlPanelEntry.class
)
public class SearchAdminControlPanelEntry extends BaseControlPanelEntry {

	@Override
	public boolean hasAccessPermission(
			PermissionChecker permissionChecker, Group group, Portlet portlet)
		throws Exception {

		if (permissionChecker.isOmniadmin()) {
			return true;
		}

		if (!_isSystemSettingsEnabledForCompany(group)) {
			return false;
		}

		return super.hasAccessPermission(permissionChecker, group, portlet);
	}

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_reindexConfiguration = ConfigurableUtil.createConfigurable(
			ReindexConfiguration.class, properties);
	}

	private boolean _isSystemSettingsEnabledForCompany(Group group) {
		if (FeatureFlagManagerUtil.isEnabled("LPS-183672") &&
			(_reindexConfiguration.indexActionsInAllVirtualInstancesEnabled() ||
			 ArrayUtil.contains(
				 _reindexConfiguration.indexActionsVirtualInstance(),
				 String.valueOf(group.getCompanyId())))) {

			return true;
		}

		return false;
	}

	private volatile ReindexConfiguration _reindexConfiguration;

}