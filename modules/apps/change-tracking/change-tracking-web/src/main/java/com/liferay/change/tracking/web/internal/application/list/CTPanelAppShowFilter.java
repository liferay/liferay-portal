/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.application.list;

import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppShowFilter;
import com.liferay.change.tracking.web.internal.configuration.CTConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ArrayUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	configurationPid = "com.liferay.change.tracking.web.internal.configuration.CTConfiguration",
	service = PanelAppShowFilter.class
)
public class CTPanelAppShowFilter implements PanelAppShowFilter {

	@Override
	public boolean isShow(
			PanelApp panelApp, PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (CTCollectionThreadLocal.isProductionMode()) {
			return true;
		}

		CTConfiguration ctConfiguration =
			_configurationProvider.getCompanyConfiguration(
				CTConfiguration.class, group.getCompanyId());

		return !ArrayUtil.contains(
			ctConfiguration.hiddenApplications(), panelApp.getPortletId());
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}