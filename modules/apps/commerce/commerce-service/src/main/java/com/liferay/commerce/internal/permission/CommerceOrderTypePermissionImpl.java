/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.internal.permission;

import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.permission.CommerceOrderTypePermission;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.util.CommerceGroupThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.util.ArrayUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Riccardo Alberti
 */
@Component(service = CommerceOrderTypePermission.class)
public class CommerceOrderTypePermissionImpl
	implements CommerceOrderTypePermission {

	@Override
	public void check(
			PermissionChecker permissionChecker,
			CommerceOrderType commerceOrderType, String actionId)
		throws PortalException {

		if (!contains(permissionChecker, commerceOrderType, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommerceOrderType.class.getName(),
				commerceOrderType.getCommerceOrderTypeId(), actionId);
		}
	}

	@Override
	public void check(
			PermissionChecker permissionChecker, long commerceOrderTypeId,
			String actionId)
		throws PortalException {

		if (!contains(permissionChecker, commerceOrderTypeId, actionId)) {
			throw new PrincipalException.MustHavePermission(
				permissionChecker, CommerceOrderType.class.getName(),
				commerceOrderTypeId, actionId);
		}
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker,
			CommerceOrderType commerceOrderType, String actionId)
		throws PortalException {

		return contains(
			permissionChecker, commerceOrderType.getCommerceOrderTypeId(),
			actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long commerceOrderTypeId,
			String actionId)
		throws PortalException {

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.fetchCommerceOrderType(
				commerceOrderTypeId);

		if (commerceOrderType == null) {
			return false;
		}

		return _contains(permissionChecker, commerceOrderType, actionId);
	}

	@Override
	public boolean contains(
			PermissionChecker permissionChecker, long[] commerceOrderTypeIds,
			String actionId)
		throws PortalException {

		if (ArrayUtil.isEmpty(commerceOrderTypeIds)) {
			return false;
		}

		for (long commerceOrderTypeId : commerceOrderTypeIds) {
			if (!contains(permissionChecker, commerceOrderTypeId, actionId)) {
				return false;
			}
		}

		return true;
	}

	private boolean _contains(
			PermissionChecker permissionChecker,
			CommerceOrderType commerceOrderType, String actionId)
		throws PortalException {

		if (permissionChecker.isCompanyAdmin(
				commerceOrderType.getCompanyId()) ||
			permissionChecker.isOmniadmin()) {

			return true;
		}

		if (permissionChecker.hasOwnerPermission(
				permissionChecker.getCompanyId(),
				CommerceOrderType.class.getName(),
				commerceOrderType.getCommerceOrderTypeId(),
				permissionChecker.getUserId(), actionId) &&
			(commerceOrderType.getUserId() == permissionChecker.getUserId())) {

			return true;
		}

		return permissionChecker.hasPermission(
			CommerceGroupThreadLocal.get(), CommerceOrderType.class.getName(),
			commerceOrderType.getCommerceOrderTypeId(), actionId);
	}

	@Reference
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

}