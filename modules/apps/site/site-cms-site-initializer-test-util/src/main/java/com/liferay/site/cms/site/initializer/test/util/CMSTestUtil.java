/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.test.util;

import com.liferay.batch.engine.test.util.BatchEngineTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.site.initializer.SiteInitializer;
import com.liferay.site.initializer.SiteInitializerRegistry;

/**
 * @author Roberto Díaz
 * @author Stefano Motta
 */
public class CMSTestUtil {

	public static Group getOrAddGroup(Class<?> clazz) throws Exception {
		long companyId = TestPropsValues.getCompanyId();

		Group group = GroupLocalServiceUtil.fetchGroup(
			companyId, GroupConstants.CMS);

		if (group != null) {
			return group;
		}

		// Create the group with the guest user as the creator, exactly as the
		// production GroupLocalServiceImpl#checkSystemGroups path provisions
		// the CMS system group. A nonguest creator would be added as the site
		// owner and a group member, which production never does, and that
		// membership would dangle once the group is removed.

		group = GroupTestUtil.addGroup(
			companyId, UserLocalServiceUtil.getGuestUserId(companyId), 0,
			GroupConstants.CMS);

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		String originalName = PrincipalThreadLocal.getName();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

			PrincipalThreadLocal.setName(TestPropsValues.getUserId());

			ServiceContextThreadLocal.pushServiceContext(
				ServiceContextTestUtil.getServiceContext(group.getGroupId()));

			try (SafeCloseable safeCloseable =
					CompanyThreadLocal.setCompanyIdWithSafeCloseable(
						group.getCompanyId())) {

				// These tests require the CMS instance lifecycle initializer
				// to have run so that the role is created. Instance
				// initialization during tests does not run it automatically,
				// so run the instance lifecycle initializer manually here.

				SiteInitializerRegistry siteInitializerRegistry =
					_siteInitializerRegistrySnapshot.get();

				SiteInitializer siteInitializer =
					siteInitializerRegistry.getSiteInitializer(
						_BUNDLE_SYMBOLIC_NAME);

				siteInitializer.initialize(group.getGroupId());

				BatchEngineTestUtil.processBatchEngineUnits(
					_BUNDLE_SYMBOLIC_NAME, clazz,
					new String[] {
						"." + _BUNDLE_SYMBOLIC_NAME +
							".internal.batch.00.list.type.definition",
						"." + _BUNDLE_SYMBOLIC_NAME +
							".internal.batch.01.object.folder",
						"." + _BUNDLE_SYMBOLIC_NAME +
							".internal.batch.02.object.definition"
					});
			}
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);

			PrincipalThreadLocal.setName(originalName);

			ServiceContextThreadLocal.popServiceContext();
		}

		return group;
	}

	private static final String _BUNDLE_SYMBOLIC_NAME =
		"com.liferay.site.initializer.cms";

	private static final Snapshot<SiteInitializerRegistry>
		_siteInitializerRegistrySnapshot = new Snapshot<>(
			CMSTestUtil.class, SiteInitializerRegistry.class);

}