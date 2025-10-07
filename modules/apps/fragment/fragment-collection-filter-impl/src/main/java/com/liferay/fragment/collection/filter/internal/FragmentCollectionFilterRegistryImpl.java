/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.filter.internal;

import com.liferay.fragment.collection.filter.FragmentCollectionFilter;
import com.liferay.fragment.collection.filter.FragmentCollectionFilterRegistry;
import com.liferay.fragment.exception.FragmentEntryConfigurationException;
import com.liferay.fragment.validator.FragmentEntryValidator;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Pablo Molina
 */
@Component(service = FragmentCollectionFilterRegistry.class)
public class FragmentCollectionFilterRegistryImpl
	implements FragmentCollectionFilterRegistry {

	@Override
	public FragmentCollectionFilter getFragmentCollectionFilter(String key) {
		return _serviceTrackerMap.getService(key);
	}

	@Override
	public List<FragmentCollectionFilter> getFragmentCollectionFilters() {
		return new ArrayList<>(_serviceTrackerMap.values());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, FragmentCollectionFilter.class, null,
			(serviceReference, emitter) -> {
				FragmentCollectionFilter fragmentCollectionFilter =
					bundleContext.getService(serviceReference);

				emitter.emit(fragmentCollectionFilter.getFilterKey());
			},
			new FragmentCollectionFilterServiceTrackerCustomizer(
				bundleContext));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FragmentCollectionFilterRegistryImpl.class);

	@Reference
	private FragmentEntryValidator _fragmentEntryValidator;

	private ServiceTrackerMap<String, FragmentCollectionFilter>
		_serviceTrackerMap;

	private class FragmentCollectionFilterServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<FragmentCollectionFilter, FragmentCollectionFilter> {

		public FragmentCollectionFilterServiceTrackerCustomizer(
			BundleContext bundleContext) {

			_bundleContext = bundleContext;
		}

		@Override
		public FragmentCollectionFilter addingService(
			ServiceReference<FragmentCollectionFilter> serviceReference) {

			FragmentCollectionFilter fragmentCollectionFilter =
				_bundleContext.getService(serviceReference);

			try {
				_fragmentEntryValidator.validateConfiguration(
					fragmentCollectionFilter.getConfigurationJSONObject());

				return fragmentCollectionFilter;
			}
			catch (FragmentEntryConfigurationException
						fragmentEntryConfigurationException) {

				_log.error(
					String.format(
						"Fragment collection filter with filter key %s and " +
							"label %s could not be registered due to invalid " +
								"configuration",
						fragmentCollectionFilter.getFilterKey(),
						fragmentCollectionFilter.getLabel(
							LocaleUtil.getMostRelevantLocale())),
					fragmentEntryConfigurationException);
			}

			return null;
		}

		@Override
		public void modifiedService(
			ServiceReference<FragmentCollectionFilter> serviceReference,
			FragmentCollectionFilter fragmentCollectionFilter) {
		}

		@Override
		public void removedService(
			ServiceReference<FragmentCollectionFilter> serviceReference,
			FragmentCollectionFilter fragmentCollectionFilter) {

			_bundleContext.ungetService(serviceReference);
		}

		private final BundleContext _bundleContext;

	}

}