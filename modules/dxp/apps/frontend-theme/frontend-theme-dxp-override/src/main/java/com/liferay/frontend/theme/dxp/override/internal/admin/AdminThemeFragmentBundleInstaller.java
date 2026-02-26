/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.theme.dxp.override.internal.admin;

import com.liferay.frontend.theme.dxp.override.internal.BaseThemeFragmentBundleInstaller;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(enabled = false, service = {})
public class AdminThemeFragmentBundleInstaller
	extends BaseThemeFragmentBundleInstaller {

	@Override
	protected String getHostBundleSymbolicName() {
		return "admin-theme";
	}

	@Override
	protected String[] getResources() {
		return new String[] {"favicon.ico"};
	}

}