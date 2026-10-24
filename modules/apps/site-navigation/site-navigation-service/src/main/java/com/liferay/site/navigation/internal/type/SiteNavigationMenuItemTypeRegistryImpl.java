/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.type;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.osgi.service.tracker.collections.map.PropertyServiceReferenceComparator;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.type.SiteNavigationMenuItemType;
import com.liferay.site.navigation.type.SiteNavigationMenuItemTypeRegistry;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Pavel Savinov
 */
@Component(service = SiteNavigationMenuItemTypeRegistry.class)
public class SiteNavigationMenuItemTypeRegistryImpl
	implements SiteNavigationMenuItemTypeRegistry {

	@Override
	public SiteNavigationMenuItemType getSiteNavigationMenuItemType(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		return getSiteNavigationMenuItemType(siteNavigationMenuItem.getType());
	}

	@Override
	public SiteNavigationMenuItemType getSiteNavigationMenuItemType(
		String type) {

		return _serviceTrackerMap.getService(type);
	}

	@Override
	public List<SiteNavigationMenuItemType> getSiteNavigationMenuItemTypes() {
		return _serviceTrackerList.toList();
	}

	@Override
	public String[] getTypes() {
		List<String> types = new ArrayList<>();

		for (SiteNavigationMenuItemType siteNavigationMenuItemType :
				_serviceTrackerList) {

			types.add(siteNavigationMenuItemType.getType());
		}

		return types.toArray(new String[0]);
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, SiteNavigationMenuItemType.class,
			new PropertyServiceReferenceComparator<>("service.ranking"));
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, SiteNavigationMenuItemType.class,
			"site.navigation.menu.item.type");
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
		_serviceTrackerMap.close();
	}

	private ServiceTrackerList<SiteNavigationMenuItemType> _serviceTrackerList;
	private volatile ServiceTrackerMap<String, SiteNavigationMenuItemType>
		_serviceTrackerMap;

}