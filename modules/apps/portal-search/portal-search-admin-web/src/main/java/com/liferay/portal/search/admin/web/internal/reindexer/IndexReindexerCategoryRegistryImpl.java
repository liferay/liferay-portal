/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.admin.web.internal.reindexer;

import com.liferay.portal.search.spi.reindexer.IndexReindexer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Felipe Lorenz
 */
@Component(service = IndexReindexerCategoryRegistry.class)
public class IndexReindexerCategoryRegistryImpl
	implements IndexReindexerCategoryRegistry {

	@Override
	public String getCategory(String className) {
		return _categories.getOrDefault(className, _DEFAULT_CATEGORY);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTracker = new ServiceTracker<>(
			bundleContext, IndexReindexer.class,
			new ServiceTrackerCustomizer<IndexReindexer, IndexReindexer>() {

				@Override
				public IndexReindexer addingService(
					ServiceReference<IndexReindexer> serviceReference) {

					IndexReindexer indexReindexer = bundleContext.getService(
						serviceReference);

					String category = (String)serviceReference.getProperty(
						_CATEGORY_PROPERTY);

					if (category == null) {
						category = _DEFAULT_CATEGORY;
					}

					_categories.put(
						indexReindexer.getClass(
						).getName(),
						category);

					return indexReindexer;
				}

				@Override
				public void modifiedService(
					ServiceReference<IndexReindexer> serviceReference,
					IndexReindexer indexReindexer) {
				}

				@Override
				public void removedService(
					ServiceReference<IndexReindexer> serviceReference,
					IndexReindexer indexReindexer) {

					_categories.remove(
						indexReindexer.getClass(
						).getName());

					bundleContext.ungetService(serviceReference);
				}

			});

		_serviceTracker.open();
	}

	@Deactivate
	protected void deactivate() {
		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	private static final String _CATEGORY_PROPERTY = "search.index.category";

	private static final String _DEFAULT_CATEGORY = "general";

	private final Map<String, String> _categories = new ConcurrentHashMap<>();
	private ServiceTracker<IndexReindexer, IndexReindexer> _serviceTracker;

}