/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.internal.util.PermissionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalService;
import com.liferay.portal.kernel.service.LayoutSetPrototypeServiceWrapper;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(service = ServiceWrapper.class)
public class DepotLayoutSetPrototypeServiceWrapper
	extends LayoutSetPrototypeServiceWrapper {

	@Override
	public List<LayoutSetPrototype> search(
		long companyId, Boolean active, int start, int end,
		OrderByComparator<LayoutSetPrototype> orderByComparator) {

		try {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			long userId = permissionChecker.getUserId();

			if (PermissionUtil.hasCMSAdministratorRole(companyId, userId) ||
				PermissionUtil.isDepotGroupAdminOrOwner(companyId, userId)) {

				return _layoutSetPrototypeLocalService.search(
					companyId, active, start, end, orderByComparator);
			}

			return super.search(
				companyId, active, start, end, orderByComparator);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return super.search(
				companyId, active, start, end, orderByComparator);
		}
	}

	@Override
	public int searchCount(long companyId, Boolean active) {
		try {
			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			long userId = permissionChecker.getUserId();

			if (PermissionUtil.hasCMSAdministratorRole(companyId, userId) ||
				PermissionUtil.isDepotGroupAdminOrOwner(companyId, userId)) {

				return _layoutSetPrototypeLocalService.searchCount(
					companyId, active);
			}

			return super.searchCount(companyId, active);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return super.searchCount(companyId, active);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotLayoutSetPrototypeServiceWrapper.class);

	@Reference
	private LayoutSetPrototypeLocalService _layoutSetPrototypeLocalService;

}