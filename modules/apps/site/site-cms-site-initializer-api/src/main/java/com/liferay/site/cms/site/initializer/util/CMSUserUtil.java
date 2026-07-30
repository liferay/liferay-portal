/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.util;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.site.cms.site.initializer.users.provider.CMSUsersProvider;

import java.util.LinkedHashSet;
import java.util.Set;

import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Pedro Leite
 */
public class CMSUserUtil {

	public static Set<User> getUsers() {
		return getUsers(null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	public static Set<User> getUsers(String keywords, int start, int end) {
		CMSUsersProvider cmsUsersProvider = _serviceTracker.getService();

		return new LinkedHashSet<>(
			cmsUsersProvider.getUsers(keywords, start, end));
	}

	private static final ServiceTracker<CMSUsersProvider, CMSUsersProvider>
		_serviceTracker =
			new ServiceTracker<CMSUsersProvider, CMSUsersProvider>(
				SystemBundleUtil.getBundleContext(), CMSUsersProvider.class,
				null) {

				{
					open();
				}
			};

}