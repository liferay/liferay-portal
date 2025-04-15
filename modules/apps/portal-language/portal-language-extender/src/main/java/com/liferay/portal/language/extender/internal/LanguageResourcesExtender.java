/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal;

import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.resource.bundle.CacheResourceBundleLoader;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;
import java.util.ResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

/**
 * @author Preston Crary
 */
@Component(service = {})
public class LanguageResourcesExtender
	implements BundleTrackerCustomizer<LanguageResourcesExtension> {

	@Override
	public LanguageResourcesExtension addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);

		List<BundleCapability> bundleCapabilities =
			bundleWiring.getCapabilities("liferay.language.resources");

		if (ListUtil.isEmpty(bundleCapabilities)) {
			bundleCapabilities = bundleWiring.getCapabilities(
				"liferay.resource.bundle");

			if (ListUtil.isEmpty(bundleCapabilities)) {
				return null;
			}
		}

		LanguageResourcesExtension languageResourcesExtension =
			new LanguageResourcesExtension(
				_bundleContext, bundle, bundleCapabilities);

		try {
			languageResourcesExtension.start();
		}
		catch (InvalidSyntaxException invalidSyntaxException) {
			languageResourcesExtension.stop();

			throw new RuntimeException(invalidSyntaxException);
		}

		return languageResourcesExtension;
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		LanguageResourcesExtension languageResourcesExtension) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		LanguageResourcesExtension languageResourcesExtension) {

		languageResourcesExtension.stop();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		DependencyManagerSyncUtil.registerSyncCallable(
			() -> {
				bundleContext.addServiceListener(
					_serviceListener,
					"(&(!(jakarta.portlet.name=*))(language.id=*)(objectClass=" +
						ResourceBundle.class.getName() + "))");

				return null;
			});

		_bundleTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext.removeServiceListener(_serviceListener);

		_bundleTracker.close();
	}

	private BundleContext _bundleContext;
	private BundleTracker<?> _bundleTracker;
	private final ServiceListener _serviceListener =
		serviceEvent -> CacheResourceBundleLoader.clearCache();

}