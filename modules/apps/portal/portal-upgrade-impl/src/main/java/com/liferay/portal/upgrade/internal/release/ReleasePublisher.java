/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.internal.release;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.model.ReleaseConstants;
import com.liferay.portal.kernel.module.framework.ModuleServiceLifecycle;
import com.liferay.portal.kernel.service.ReleaseLocalService;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.upgrade.internal.model.listener.ReleaseModelListener;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Pastor
 * @author Carlos Sierra Andrés
 */
@Component(service = ReleasePublisher.class)
public class ReleasePublisher {

	public void publish(Release release, boolean initialRelease) {
		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put(
			"release.bundle.symbolic.name", release.getBundleSymbolicName());
		properties.put("release.initial", initialRelease);

		try {
			if (Validator.isNotNull(release.getSchemaVersion())) {
				Version version = new Version(release.getSchemaVersion());

				properties.put("release.schema.version", version);
			}
		}
		catch (IllegalArgumentException illegalArgumentException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Invalid schema version for release: " + release,
					illegalArgumentException);
			}
		}

		ServiceRegistration<Release> serviceRegistration =
			_bundleContext.registerService(Release.class, release, properties);

		_serviceConfiguratorRegistrations.compute(
			release.getServletContextName(),
			(servletContextName, existingServiceRegistration) -> {
				if (existingServiceRegistration == null) {
					return serviceRegistration;
				}

				ServiceReference<Release> serviceReference =
					existingServiceRegistration.getReference();

				if (serviceReference == null) {
					return serviceRegistration;
				}

				Version existingVersion = (Version)serviceReference.getProperty(
					"release.schema.version");

				Version currentVersion = (Version)properties.get(
					"release.schema.version");

				if ((existingVersion == null) || (currentVersion == null) ||
					(currentVersion.compareTo(existingVersion) >= 0)) {

					existingServiceRegistration.unregister();

					return serviceRegistration;
				}

				serviceRegistration.unregister();

				return existingServiceRegistration;
			});
	}

	public void unpublish(Release release) {
		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				ServiceRegistration<Release> serviceRegistration =
					_serviceConfiguratorRegistrations.remove(
						release.getServletContextName());

				if (serviceRegistration != null) {
					serviceRegistration.unregister();
				}

				return null;
			});
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		List<Release> releases = _releaseLocalService.getReleases(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (Release release : releases) {
			if (release.getState() != ReleaseConstants.STATE_GOOD) {
				continue;
			}

			publish(release, false);
		}

		_serviceRegistration = bundleContext.registerService(
			ModelListener.class, new ReleaseModelListener(this), null);
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();

		for (ServiceRegistration<Release> serviceRegistration :
				_serviceConfiguratorRegistrations.values()) {

			serviceRegistration.unregister();
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ReleasePublisher.class);

	private BundleContext _bundleContext;

	@Reference(target = ModuleServiceLifecycle.PORTAL_INITIALIZED)
	private ModuleServiceLifecycle _moduleServiceLifecycle;

	@Reference
	private ReleaseLocalService _releaseLocalService;

	private final Map<String, ServiceRegistration<Release>>
		_serviceConfiguratorRegistrations = new ConcurrentHashMap<>();
	private ServiceRegistration<?> _serviceRegistration;

}