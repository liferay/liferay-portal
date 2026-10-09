/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.extender.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.util.AggregateResourceBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Preston Crary
 */
public class ServiceTrackerResourceBundleLoader
	implements ResourceBundleLoader {

	public ServiceTrackerResourceBundleLoader(
			BundleContext bundleContext, String aggregate, int aggregateId,
			int serviceRanking)
		throws InvalidSyntaxException {

		List<String> filterStrings = StringUtil.split(aggregate);

		_serviceTrackers = new ArrayList<>(filterStrings.size());

		for (String filterString : filterStrings) {
			Filter filter = bundleContext.createFilter(
				StringBundler.concat(
					"(&(objectClass=", ResourceBundleLoader.class.getName(),
					")", filterString, "(|(!(aggregateId=*))(!(aggregateId=",
					aggregateId, ")))(!(service.ranking>=", serviceRanking,
					")))"));

			ServiceTracker<ResourceBundleLoader, ResourceBundleLoader>
				serviceTracker = new ServiceTracker<>(
					bundleContext, filter, null);

			_serviceTrackers.add(serviceTracker);
		}

		for (ServiceTracker<?, ?> serviceTracker : _serviceTrackers) {
			serviceTracker.open();
		}
	}

	public void close() {
		for (ServiceTracker<?, ?> serviceTracker : _serviceTrackers) {
			serviceTracker.close();
		}
	}

	@Override
	public ResourceBundle loadResourceBundle(Locale locale) {
		List<ResourceBundle> resourceBundles = new ArrayList<>();

		for (ServiceTracker<ResourceBundleLoader, ResourceBundleLoader>
				serviceTracker : _serviceTrackers) {

			ResourceBundleLoader resourceBundleLoader =
				serviceTracker.getService();

			if (resourceBundleLoader == null) {
				continue;
			}

			ResourceBundle resourceBundle =
				resourceBundleLoader.loadResourceBundle(locale);

			if (resourceBundle == null) {
				continue;
			}

			resourceBundles.add(resourceBundle);
		}

		if (resourceBundles.isEmpty()) {
			return null;
		}

		if (resourceBundles.size() == 1) {
			return resourceBundles.get(0);
		}

		return new AggregateResourceBundle(
			resourceBundles.toArray(new ResourceBundle[0]));
	}

	private final List
		<ServiceTracker<ResourceBundleLoader, ResourceBundleLoader>>
			_serviceTrackers;

}