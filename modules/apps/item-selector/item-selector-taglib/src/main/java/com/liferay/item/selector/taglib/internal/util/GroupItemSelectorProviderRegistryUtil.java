/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.item.selector.taglib.internal.util;

import com.liferay.item.selector.provider.GroupItemSelectorProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Group;

import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Cristina González
 */
public class GroupItemSelectorProviderRegistryUtil {

	public static GroupItemSelectorProvider getGroupItemSelectorProvider(
		Group group) {

		for (GroupItemSelectorProvider groupItemSelectorProvider :
				_serviceTrackerMap.values()) {

			if (groupItemSelectorProvider.isEnabled() &&
				groupItemSelectorProvider.isGroupType(group)) {

				return groupItemSelectorProvider;
			}
		}

		return null;
	}

	public static GroupItemSelectorProvider getGroupItemSelectorProvider(
		String groupType) {

		GroupItemSelectorProvider groupItemSelectorProvider =
			_serviceTrackerMap.getService(groupType);

		if ((groupItemSelectorProvider != null) &&
			groupItemSelectorProvider.isEnabled()) {

			return groupItemSelectorProvider;
		}

		return null;
	}

	public static Set<String> getGroupItemSelectorProviderTypes() {
		Set<String> types = new HashSet<>();

		for (GroupItemSelectorProvider groupItemSelectorProvider :
				_serviceTrackerMap.values()) {

			if (!groupItemSelectorProvider.isEnabled()) {
				continue;
			}

			types.add(groupItemSelectorProvider.getGroupType());
		}

		return types;
	}

	private static final ServiceTrackerMap<String, GroupItemSelectorProvider>
		_serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			GroupItemSelectorProviderRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, GroupItemSelectorProvider.class, null,
			ServiceReferenceMapperFactory.create(
				bundleContext,
				(service, emitter) -> emitter.emit(service.getGroupType())));
	}

}