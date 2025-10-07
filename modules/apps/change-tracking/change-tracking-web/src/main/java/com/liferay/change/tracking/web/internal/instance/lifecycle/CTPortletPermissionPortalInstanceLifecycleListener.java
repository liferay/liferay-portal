/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.instance.lifecycle;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.constants.CTRoleConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.web.internal.util.PublicationsRegularRolesUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.instance.lifecycle.BasePortalInstanceLifecycleListener;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Shuyang Zhou
 */
@Component(service = PortalInstanceLifecycleListener.class)
public class CTPortletPermissionPortalInstanceLifecycleListener
	extends BasePortalInstanceLifecycleListener {

	@Override
	public void portalInstanceRegistered(Company company)
		throws PortalException {

		if (_log.isDebugEnabled()) {
			_log.debug(
				"Initializing " + _portlet.getPortletId() +
					" permissions for publications roles");
		}

		_checkPublicationsRegularRoles(company);

		_checkPublicationsReviewerRole(company);

		_checkPublicationsUserRole(company.getCompanyId());
	}

	private void _addPortletResourcePermission(Role role, String actionId)
		throws PortalException {

		_resourcePermissionLocalService.addResourcePermission(
			role.getCompanyId(), CTPortletKeys.PUBLICATIONS,
			ResourceConstants.SCOPE_COMPANY,
			String.valueOf(role.getCompanyId()), role.getRoleId(), actionId);
	}

	private void _checkPublicationsRegularRoles(Company company)
		throws PortalException {

		for (String publicationsRegularRoleName :
				PublicationsRegularRolesUtil.PUBLICATIONS_REGULAR_ROLE_NAMES) {

			Role role = _roleLocalService.fetchRole(
				company.getCompanyId(), publicationsRegularRoleName);

			if (role == null) {
				User guestUser = company.getGuestUser();

				role = _roleLocalService.addRole(
					null, guestUser.getUserId(), null, 0,
					publicationsRegularRoleName, null,
					HashMapBuilder.put(
						company.getLocale(),
						PropsUtil.get(
							StringBundler.concat(
								"system.role.",
								StringUtil.replace(
									publicationsRegularRoleName, CharPool.SPACE,
									CharPool.PERIOD),
								".description"))
					).build(),
					RoleConstants.TYPE_REGULAR, null, null);
			}

			ResourcePermission modelResourcePermission =
				_resourcePermissionLocalService.fetchResourcePermission(
					company.getCompanyId(), CTCollection.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(company.getCompanyId()), role.getRoleId());

			if (modelResourcePermission != null) {
				continue;
			}

			for (String actionId :
					PublicationsRegularRolesUtil.getModelResourceActions(
						publicationsRegularRoleName)) {

				_resourcePermissionLocalService.addResourcePermission(
					company.getCompanyId(), CTCollection.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(company.getCompanyId()), role.getRoleId(),
					actionId);
			}
		}
	}

	private void _checkPublicationsReviewerRole(Company company)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(
			company.getCompanyId(), CTRoleConstants.PUBLICATIONS_REVIEWER);

		if (role == null) {
			User guestUser = company.getGuestUser();

			role = _roleLocalService.addRole(
				null, guestUser.getUserId(), null, 0,
				CTRoleConstants.PUBLICATIONS_REVIEWER, null,
				HashMapBuilder.put(
					LocaleUtil.getDefault(),
					"Guest users who have access to a publication should be " +
						"assigned this role."
				).build(),
				RoleConstants.TYPE_PUBLICATIONS, null, null);
		}

		ResourcePermission portletResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				company.getCompanyId(), CTPortletKeys.PUBLICATIONS,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(company.getCompanyId()), role.getRoleId());

		if (portletResourcePermission == null) {
			_addPortletResourcePermission(
				role, ActionKeys.ACCESS_IN_CONTROL_PANEL);
			_addPortletResourcePermission(role, ActionKeys.VIEW);
		}

		ResourcePermission modelResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				role.getRoleId());

		if (modelResourcePermission == null) {
			_resourcePermissionLocalService.addResourcePermission(
				company.getCompanyId(), CTCollection.class.getName(),
				ResourceConstants.SCOPE_GROUP_TEMPLATE,
				String.valueOf(GroupConstants.DEFAULT_PARENT_GROUP_ID),
				role.getRoleId(), ActionKeys.VIEW);
		}
	}

	private void _checkPublicationsUserRole(long companyId)
		throws PortalException {

		Role role = _roleLocalService.fetchRole(
			companyId, RoleConstants.PUBLICATIONS_USER);

		ResourcePermission rootModelResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				role.getCompanyId(),
				_resourceActions.getPortletRootModelResource(
					CTPortletKeys.PUBLICATIONS),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(role.getCompanyId()), role.getRoleId());

		if (rootModelResourcePermission == null) {
			_resourcePermissionLocalService.addResourcePermission(
				role.getCompanyId(),
				_resourceActions.getPortletRootModelResource(
					CTPortletKeys.PUBLICATIONS),
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(role.getCompanyId()), role.getRoleId(),
				CTActionKeys.ADD_PUBLICATION);
		}

		ResourcePermission portletResourcePermission =
			_resourcePermissionLocalService.fetchResourcePermission(
				companyId, CTPortletKeys.PUBLICATIONS,
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId());

		if (portletResourcePermission == null) {
			_addPortletResourcePermission(
				role, ActionKeys.ACCESS_IN_CONTROL_PANEL);
			_addPortletResourcePermission(role, ActionKeys.VIEW);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CTPortletPermissionPortalInstanceLifecycleListener.class);

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