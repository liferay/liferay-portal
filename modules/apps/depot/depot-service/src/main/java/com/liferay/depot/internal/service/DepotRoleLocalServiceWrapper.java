/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.service;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.RoleSubtypeException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalServiceWrapper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceWrapper;
import com.liferay.portal.kernel.util.Validator;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Stefano Motta
 */
@Component(service = ServiceWrapper.class)
public class DepotRoleLocalServiceWrapper extends RoleLocalServiceWrapper {

	@Override
	public Role addRole(
			String externalReferenceCode, long userId, String className,
			long classPK, String name, Map<Locale, String> titleMap,
			Map<Locale, String> descriptionMap, int type, String subtype,
			ServiceContext serviceContext)
		throws PortalException {

		_validate(subtype, type);

		return super.addRole(
			externalReferenceCode, userId, className, classPK, name, titleMap,
			descriptionMap, type, subtype, serviceContext);
	}

	@Override
	public Role updateRole(
			String externalReferenceCode, long roleId, String name,
			Map<Locale, String> titleMap, Map<Locale, String> descriptionMap,
			String subtype, ServiceContext serviceContext)
		throws PortalException {

		Role role = super.getRole(roleId);

		_validate(subtype, role.getType());

		return super.updateRole(
			externalReferenceCode, roleId, name, titleMap, descriptionMap,
			subtype, serviceContext);
	}

	private void _validate(String subtype, int type)
		throws RoleSubtypeException {

		if ((type != RoleConstants.TYPE_DEPOT) || Validator.isNull(subtype)) {
			return;
		}

		if (Objects.equals(
				DepotRolesConstants.SUBTYPE_DESIGN_LIBRARY, subtype) &&
			FeatureFlagManagerUtil.isEnabled(
				CompanyThreadLocal.getCompanyId(), "LPD-57283")) {

			return;
		}

		if (Objects.equals(DepotRolesConstants.SUBTYPE_PROJECT, subtype) ||
			Objects.equals(DepotRolesConstants.SUBTYPE_SPACE, subtype)) {

			return;
		}

		throw new RoleSubtypeException();
	}

}