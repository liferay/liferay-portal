/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.theme.dxp.override.internal.portal.profile;

import com.liferay.frontend.theme.dxp.override.internal.admin.AdminThemeFragmentBundleInstaller;
import com.liferay.frontend.theme.dxp.override.internal.classic.ClassicThemeFragmentBundleInstaller;
import com.liferay.portal.profile.BaseDSModulePortalProfile;
import com.liferay.portal.profile.PortalProfile;

import java.util.Collections;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(service = PortalProfile.class)
public class ModulePortalProfile extends BaseDSModulePortalProfile {

	@Activate
	protected void activate(ComponentContext componentContext) {
		init(
			componentContext,
			Collections.singleton(PortalProfile.PORTAL_PROFILE_NAME_DXP),
			AdminThemeFragmentBundleInstaller.class.getName(),
			ClassicThemeFragmentBundleInstaller.class.getName());
	}

}