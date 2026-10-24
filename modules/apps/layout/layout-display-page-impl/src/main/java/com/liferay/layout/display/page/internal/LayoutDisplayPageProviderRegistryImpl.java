/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.display.page.internal;

import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProviderRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge Ferrer
 */
@Component(service = LayoutDisplayPageProviderRegistry.class)
public class LayoutDisplayPageProviderRegistryImpl
	implements LayoutDisplayPageProviderRegistry {

	@Override
	public LayoutDisplayPageProvider<?> getLayoutDisplayPageProviderByClassName(
		long companyId, String className) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderByClassNameServiceTrackerMap.getService(
				_getKey(companyId, className));

		if (layoutDisplayPageProvider == null) {
			layoutDisplayPageProvider =
				_layoutDisplayPageProviderByClassNameServiceTrackerMap.
					getService(_getKey(0, className));
		}

		return layoutDisplayPageProvider;
	}

	@Override
	public LayoutDisplayPageProvider<?>
		getLayoutDisplayPageProviderByURLSeparator(
			long companyId, String urlSeparator) {

		LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
			_layoutDisplayPageProviderByURLSeparatorServiceTrackerMap.
				getService(_getKey(companyId, urlSeparator));

		if (layoutDisplayPageProvider == null) {
			layoutDisplayPageProvider =
				_layoutDisplayPageProviderByURLSeparatorServiceTrackerMap.
					getService(_getKey(0, urlSeparator));
		}

		return layoutDisplayPageProvider;
	}

	@Override
	public List<LayoutDisplayPageProvider<?>> getLayoutDisplayPageProviders() {
		return new ArrayList(
			_layoutDisplayPageProviderByClassNameServiceTrackerMap.values());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_layoutDisplayPageProviderByClassNameServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<LayoutDisplayPageProvider<?>>)
					(Class<?>)LayoutDisplayPageProvider.class,
				null,
				(serviceReference, emitter) -> {
					LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
						bundleContext.getService(serviceReference);

					try {
						long companyId = GetterUtil.getLong(
							serviceReference.getProperty("company.id"));

						emitter.emit(
							_getKey(
								companyId,
								layoutDisplayPageProvider.getClassName()));
					}
					finally {
						bundleContext.ungetService(serviceReference);
					}
				});
		_layoutDisplayPageProviderByURLSeparatorServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<LayoutDisplayPageProvider<?>>)
					(Class<?>)LayoutDisplayPageProvider.class,
				null,
				(serviceReference, emitter) -> {
					LayoutDisplayPageProvider<?> layoutDisplayPageProvider =
						bundleContext.getService(serviceReference);

					try {
						long companyId = GetterUtil.getLong(
							serviceReference.getProperty("company.id"));

						emitter.emit(
							_getKey(
								companyId,
								_getURLSeparator(layoutDisplayPageProvider)));
					}
					finally {
						bundleContext.ungetService(serviceReference);
					}
				});
	}

	private String _getKey(long companyId, String key) {
		return companyId + StringPool.PERIOD + key;
	}

	private String _getURLSeparator(
		LayoutDisplayPageProvider layoutDisplayPageProvider) {

		if (Validator.isNotNull(
				layoutDisplayPageProvider.getDefaultURLSeparator())) {

			return layoutDisplayPageProvider.getDefaultURLSeparator();
		}

		return layoutDisplayPageProvider.getURLSeparator();
	}

	private ServiceTrackerMap<String, LayoutDisplayPageProvider<?>>
		_layoutDisplayPageProviderByClassNameServiceTrackerMap;
	private ServiceTrackerMap<String, LayoutDisplayPageProvider<?>>
		_layoutDisplayPageProviderByURLSeparatorServiceTrackerMap;

}