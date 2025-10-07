/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.scheduler.quartz.internal.portal.profile;

import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.scheduler.SchedulerEngine;
import com.liferay.portal.kernel.scheduler.TriggerFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.profile.BaseDSModulePortalProfile;
import com.liferay.portal.profile.PortalProfile;
import com.liferay.portal.scheduler.quartz.internal.QuartzSchedulerEngine;
import com.liferay.portal.scheduler.quartz.internal.QuartzTriggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

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

			_schedulerEngineServiceRegistration = bundleContext.registerService(
				SchedulerEngine.class,
				ProxyFactory.newDummyInstance(SchedulerEngine.class),
				MapUtil.singletonDictionary(
					"scheduler.engine.proxy", Boolean.FALSE));

			_triggerFactoryServiceRegistration = bundleContext.registerService(
				TriggerFactory.class,
				ProxyFactory.newDummyInstance(TriggerFactory.class),
				new HashMapDictionary<>());
		}

		init(
			componentContext, supportedPortalProfileNames,
			QuartzSchedulerEngine.class.getName(),
			QuartzTriggerFactory.class.getName());
	}

	@Deactivate
	protected void deactivate() {
		if (_triggerFactoryServiceRegistration != null) {
			_triggerFactoryServiceRegistration.unregister();
		}

		if (_schedulerEngineServiceRegistration != null) {
			_schedulerEngineServiceRegistration.unregister();
		}
	}

	@Reference(
		target = "(release.bundle.symbolic.name=com.liferay.portal.scheduler.quartz)"
	)
	private Release _release;

	private ServiceRegistration<SchedulerEngine>
		_schedulerEngineServiceRegistration;
	private ServiceRegistration<TriggerFactory>
		_triggerFactoryServiceRegistration;

}