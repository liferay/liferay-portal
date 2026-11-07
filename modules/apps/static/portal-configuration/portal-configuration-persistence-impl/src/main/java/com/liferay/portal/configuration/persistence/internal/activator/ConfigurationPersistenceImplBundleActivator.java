/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.internal.activator;

import com.liferay.portal.configuration.persistence.ReloadablePersistenceManager;
import com.liferay.portal.configuration.persistence.internal.ConfigurationPersistenceManager;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.InfrastructureUtil;

import org.apache.felix.cm.PersistenceManager;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Carlos Sierra Andrés
 */
public class ConfigurationPersistenceImplBundleActivator
	implements BundleActivator {

	@Override
	public void start(BundleContext bundleContext) {
		_configurationPersistenceManager = new ConfigurationPersistenceManager(
			bundleContext, InfrastructureUtil.getDataSource());

		_configurationPersistenceManager.start();

		_configurationPersistenceManagerServiceRegistration =
			bundleContext.registerService(
				new String[] {
					PersistenceManager.class.getName(),
					ReloadablePersistenceManager.class.getName()
				},
				_configurationPersistenceManager,
				HashMapDictionaryBuilder.<String, Object>put(
					Constants.SERVICE_RANKING, Integer.MAX_VALUE - 1000
				).put(
					PersistenceManager.PROPERTY_NAME,
					ConfigurationPersistenceManager.class.getName()
				).build());
	}

	@Override
	public void stop(BundleContext bundleContext) {
		if (_configurationPersistenceManagerServiceRegistration != null) {
			_configurationPersistenceManagerServiceRegistration.unregister();
		}

		_configurationPersistenceManager.stop();
	}

	private ConfigurationPersistenceManager _configurationPersistenceManager;
	private ServiceRegistration<?>
		_configurationPersistenceManagerServiceRegistration;

}