/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.internal.layout.asset.entry.provider;

import com.liferay.asset.display.page.layout.asset.entry.provider.LayoutAssetEntryProvider;
import com.liferay.asset.display.page.layout.asset.entry.provider.LayoutAssetEntryProviderRegistry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Roberto Díaz
 */
@Component(service = LayoutAssetEntryProviderRegistry.class)
public class LayoutAssetEntryProviderRegistryImpl
	implements LayoutAssetEntryProviderRegistry {

	@Override
	public LayoutAssetEntryProvider getLayoutAssetEntryProvider(
		String portletId) {

		return _serviceTrackerMap.getService(portletId);
	}

	@Activate
	@Modified
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, LayoutAssetEntryProvider.class,
			"jakarta.portlet.name");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerMap.close();
	}

	private volatile ServiceTrackerMap<String, LayoutAssetEntryProvider>
		_serviceTrackerMap;

}