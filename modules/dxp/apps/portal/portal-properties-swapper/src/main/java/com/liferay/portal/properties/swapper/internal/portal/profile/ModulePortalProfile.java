/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.properties.swapper.internal.portal.profile;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.profile.BaseDSModulePortalProfile;
import com.liferay.portal.profile.PortalProfile;
import com.liferay.portal.properties.swapper.internal.DefaultCompanyLogoSwapper;
import com.liferay.portal.properties.swapper.internal.DefaultCompanyNameSwapper;
import com.liferay.portal.properties.swapper.internal.DefaultLiferayLogoSwapper;
import com.liferay.portal.properties.swapper.internal.DefaultLiferayNameSwapper;
import com.liferay.portal.properties.swapper.internal.SwapDefaultGuestGroupLogoPortalInstanceLifecycleListener;

import java.util.Collections;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
			DefaultCompanyLogoSwapper.class.getName(),
			DefaultCompanyNameSwapper.class.getName(),
			DefaultLiferayLogoSwapper.class.getName(),
			DefaultLiferayNameSwapper.class.getName(),
			SwapDefaultGuestGroupLogoPortalInstanceLifecycleListener.class.
				getName());
	}

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED, unbind = "-")
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference(target = "(default=true)")
	private Store _store;

}