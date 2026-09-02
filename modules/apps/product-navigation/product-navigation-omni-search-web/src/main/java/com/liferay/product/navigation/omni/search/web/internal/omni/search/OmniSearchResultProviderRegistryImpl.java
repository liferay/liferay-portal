/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.omni.search;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.product.navigation.omni.search.OmniSearchResultProvider;
import com.liferay.product.navigation.omni.search.OmniSearchResultProviderRegistry;

import java.util.Collections;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Thiago Buarque
 */
@Component(service = OmniSearchResultProviderRegistry.class)
public class OmniSearchResultProviderRegistryImpl
	implements OmniSearchResultProviderRegistry {

	@Override
	public List<OmniSearchResultProvider> getOmniSearchResultProviders() {
		return _serviceTrackerList.toList();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, OmniSearchResultProvider.class,
			Collections.reverseOrder(
				new PropertyServiceReferenceComparator<>(
					"omni.search.result.provider.order")));
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList<OmniSearchResultProvider> _serviceTrackerList;

}