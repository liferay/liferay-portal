/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.web.internal.item.filter;

import com.liferay.content.dashboard.item.filter.provider.ContentDashboardItemFilterProvider;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;

import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Cristina González
 */
@Component(service = ContentDashboardItemFilterProviderRegistry.class)
public class ContentDashboardItemFilterProviderRegistry {

	public List<ContentDashboardItemFilterProvider>
		getContentDashboardItemFilterProviders() {

		return _serviceTrackerList.toList();
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, ContentDashboardItemFilterProvider.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList<ContentDashboardItemFilterProvider>
		_serviceTrackerList;

}