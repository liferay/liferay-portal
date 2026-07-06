/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.internal.instance.lifecycle;

import com.liferay.headless.builder.application.publisher.APIApplicationPublisher;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.EveryNodeEveryStartup;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.transaction.TransactionCallbackUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "service.ranking:Integer=" + Integer.MIN_VALUE,
	service = PortalInstanceLifecycleListener.class
)
public class APIApplicationPublisherPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener
	implements EveryNodeEveryStartup {

	@Override
	public void portalInstanceRegistered(Company company) {
		if (!FeatureFlagManagerUtil.isEnabled(
				company.getCompanyId(), "LPS-178642")) {

			return;
		}

		TransactionCallbackUtil.registerCommitCallback(
			() -> {
				PermissionThreadLocal.setPermissionChecker(
					_permissionCheckerFactory.create(
						_userLocalService.getUser(
							_getAdminUserId(company.getCompanyId()))));

				_apiApplicationPublisher.publish(company.getCompanyId());

				return null;
			});
	}

	private long _getAdminUserId(long companyId) throws Exception {
		Role role = _roleLocalService.getRole(
			companyId, RoleConstants.ADMINISTRATOR);

		long[] userIds = _userLocalService.getRoleUserIds(role.getRoleId());

		if (userIds.length == 0) {
			throw new NoSuchUserException(
				StringBundler.concat(
					"No user exists in company ", companyId, " with role ",
					role.getName()));
		}

		return userIds[0];
	}

	@Reference
	private APIApplicationPublisher _apiApplicationPublisher;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

}