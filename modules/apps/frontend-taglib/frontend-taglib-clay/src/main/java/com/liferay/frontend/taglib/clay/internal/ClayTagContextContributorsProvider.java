/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.internal;

import com.liferay.frontend.taglib.clay.servlet.taglib.contributor.ClayTagContextContributor;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Rodolfo Roza Miranda
 */
public class ClayTagContextContributorsProvider {

	public static List<ClayTagContextContributor> getClayTagContextContributors(
		String clayTagContextContributorKey) {

		return _serviceTrackerMap.getService(clayTagContextContributorKey);
	}

	private static final ServiceTrackerMap
		<String, List<ClayTagContextContributor>> _serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			ClayTagContextContributorsProvider.class);

		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			bundle.getBundleContext(), ClayTagContextContributor.class,
			"clay.tag.context.contributor.key");
	}

}