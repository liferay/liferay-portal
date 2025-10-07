/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.permission;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.security.permission.wrapper.PermissionCheckerWrapperFactory;

import org.osgi.framework.BundleContext;

/**
 * @author Charles May
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class PermissionCheckerFactoryImpl implements PermissionCheckerFactory {

	public void afterPropertiesSet() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		_permissionCheckerWrapperFactories = ServiceTrackerListFactory.open(
			bundleContext, PermissionCheckerWrapperFactory.class);
		_roleContributors = ServiceTrackerListFactory.open(
			bundleContext, RoleContributor.class);
	}

	@Override
	public PermissionChecker create(User user) {
		PermissionChecker permissionChecker = _permissionChecker.clone();

		permissionChecker.init(
			user, _roleContributors.toArray(new RoleContributor[0]));

		permissionChecker = new StagingPermissionChecker(permissionChecker);

		for (PermissionCheckerWrapperFactory permissionCheckerWrapperFactory :
				_permissionCheckerWrapperFactories) {

			permissionChecker =
				permissionCheckerWrapperFactory.wrapPermissionChecker(
					permissionChecker);
		}

		return permissionChecker;
	}

	public void destroy() {
		_permissionCheckerWrapperFactories.close();
		_roleContributors.close();
	}

	private static final PermissionChecker _permissionChecker =
		new AdvancedPermissionChecker();

	private ServiceTrackerList<PermissionCheckerWrapperFactory>
		_permissionCheckerWrapperFactories;
	private ServiceTrackerList<RoleContributor> _roleContributors;

}