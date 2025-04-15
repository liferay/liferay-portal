/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.taglib.ui;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Alejandro Tardín
 */
public class BreadcrumbEntryContributorUtil {

	public static List<BreadcrumbEntry> contribute(
		List<BreadcrumbEntry> breadcrumbEntries,
		HttpServletRequest httpServletRequest) {

		for (BreadcrumbEntryContributor breadcrumbEntryContributor :
				_breadcrumbEntryContributors) {

			breadcrumbEntries = breadcrumbEntryContributor.getBreadcrumbEntries(
				breadcrumbEntries, httpServletRequest);
		}

		return breadcrumbEntries;
	}

	private static final ServiceTrackerList<BreadcrumbEntryContributor>
		_breadcrumbEntryContributors = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(),
			BreadcrumbEntryContributor.class);

}