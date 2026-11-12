/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.model.listener;

import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModelListener;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = ModelListener.class)
public class RoleModelListener extends BaseModelListener<Role> {

	@Override
	public void onAfterRemove(Role role) throws ModelListenerException {
		if ((role.getType() == RoleConstants.TYPE_REGULAR) ||
			(role.getType() == RoleConstants.TYPE_SITE)) {

			try {
				_menuAccessConfigurationManager.deleteRoleAccessToControlMenu(
					role);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	@Override
	public void onAfterUpdate(Role originalRole, Role role)
		throws ModelListenerException {

		if (((role.getType() == RoleConstants.TYPE_REGULAR) ||
			 (role.getType() == RoleConstants.TYPE_SITE)) &&
			!Objects.equals(
				originalRole.getExternalReferenceCode(),
				role.getExternalReferenceCode())) {

			try {
				_menuAccessConfigurationManager.updateRoleAccessToControlMenu(
					originalRole, role);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		RoleModelListener.class);

	@Reference
	private MenuAccessConfigurationManager _menuAccessConfigurationManager;

}