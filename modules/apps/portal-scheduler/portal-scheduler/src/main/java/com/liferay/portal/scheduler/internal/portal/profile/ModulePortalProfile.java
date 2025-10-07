/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.internal.portal.profile;

import com.liferay.portal.kernel.scheduler.SchedulerEngineAuditor;
import com.liferay.portal.kernel.scheduler.SchedulerEngineHelper;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.profile.BaseDSModulePortalProfile;
import com.liferay.portal.profile.PortalProfile;
import com.liferay.portal.scheduler.internal.SchedulerEngineAuditorImpl;
import com.liferay.portal.scheduler.internal.SchedulerEngineHelperImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Tina Tian
 */
@Component(service = PortalProfile.class)
public class ModulePortalProfile extends BaseDSModulePortalProfile {

	@Activate
	protected void activate(ComponentContext componentContext) {
		List<String> supportedPortalProfileNames = null;

		if (GetterUtil.getBoolean(PropsUtil.get(PropsKeys.SCHEDULER_ENABLED))) {
			supportedPortalProfileNames = new ArrayList<>();

			supportedPortalProfileNames.add(
				PortalProfile.PORTAL_PROFILE_NAME_CE);
			supportedPortalProfileNames.add(
				PortalProfile.PORTAL_PROFILE_NAME_DXP);
		}
		else {
			supportedPortalProfileNames = Collections.emptyList();

			BundleContext bundleContext = componentContext.getBundleContext();

			bundleContext.registerService(
				SchedulerEngineAuditor.class,
				ProxyFactory.newDummyInstance(SchedulerEngineAuditor.class),
				new HashMapDictionary<String, Object>());
			bundleContext.registerService(
				SchedulerEngineHelper.class,
				ProxyFactory.newDummyInstance(SchedulerEngineHelper.class),
				new HashMapDictionary<String, Object>());
		}

		init(
			componentContext, supportedPortalProfileNames,
			SchedulerEngineAuditorImpl.class.getName(),
			SchedulerEngineHelperImpl.class.getName());
	}

}