/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.model.listener;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.web.internal.util.PublicationsRegularRolesUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(service = ModelListener.class)
public class RoleModelListener extends BaseModelListener<Role> {

	@Override
	public void onAfterAddAssociation(
			Object classPK, String associationClassName,
			Object associationClassPK)
		throws ModelListenerException {

		try {
			Role role = _roleLocalService.getRole((Long)classPK);

			if (!ArrayUtil.contains(
					PublicationsRegularRolesUtil.
						PUBLICATIONS_REGULAR_ROLE_NAMES,
					role.getName())) {

				return;
			}

			long userId = (Long)associationClassPK;

			Role publicationsUserRole = _roleLocalService.getRole(
				role.getCompanyId(), RoleConstants.PUBLICATIONS_USER);

			if (_roleLocalService.hasUserRole(
					userId, publicationsUserRole.getRoleId())) {

				return;
			}

			_roleLocalService.addUserRole(userId, publicationsUserRole);
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}
	}

	@Override
	public void onAfterCreate(Role role) throws ModelListenerException {
		if (!Objects.equals(role.getName(), RoleConstants.PUBLICATIONS_USER) ||
			!role.isSystem()) {

			return;
		}

		if (_log.isDebugEnabled()) {
			_log.debug(
				StringBundler.concat(
					"Initializing ", _portlet.getPortletId(),
					" permissions for role ", role.getRoleId()));
		}

		try {
			_resourcePermissionLocalService.addResourcePermission(
				role.getCompanyId(),
				_resourceActions.getPortletRootModelResource(
					CTPortletKeys.PUBLICATIONS),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(role.getCompanyId()), role.getRoleId(),
				CTActionKeys.ADD_PUBLICATION);
			_resourcePermissionLocalService.addResourcePermission(
				role.getCompanyId(), CTPortletKeys.PUBLICATIONS,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(role.getCompanyId()), role.getRoleId(),
				ActionKeys.ACCESS_IN_CONTROL_PANEL);
			_resourcePermissionLocalService.addResourcePermission(
				role.getCompanyId(), CTPortletKeys.PUBLICATIONS,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(role.getCompanyId()), role.getRoleId(),
				ActionKeys.VIEW);
		}
		catch (PortalException portalException) {
			throw new ModelListenerException(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RoleModelListener.class);

	@Reference(
		target = "(jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS + ")"
	)
	private Portlet _portlet;

	@Reference
	private ResourceActions _resourceActions;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}