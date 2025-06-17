/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.util;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;

import java.util.Objects;

/**
 * @author Gislayne Vitorino
 */
public class PublicationsRegularRolesUtil {

	public static final String[] PUBLICATIONS_REGULAR_ROLES = {
		RoleConstants.PUBLICATIONS_ADMIN, RoleConstants.PUBLICATIONS_EDITOR,
		RoleConstants.PUBLICATIONS_PUBLISHER, RoleConstants.PUBLICATIONS_VIEWER
	};

	public static String[] getModelResourceActions(String role) {
		if (Objects.equals(role, RoleConstants.PUBLICATIONS_ADMIN)) {
			return new String[] {
				ActionKeys.DELETE, ActionKeys.PERMISSIONS, ActionKeys.UPDATE,
				ActionKeys.VIEW, CTActionKeys.INVITE_USERS, CTActionKeys.PUBLISH
			};
		}
		else if (Objects.equals(role, RoleConstants.PUBLICATIONS_EDITOR)) {
			return new String[] {ActionKeys.UPDATE, ActionKeys.VIEW};
		}
		else if (Objects.equals(role, RoleConstants.PUBLICATIONS_PUBLISHER)) {
			return new String[] {
				ActionKeys.UPDATE, ActionKeys.VIEW, CTActionKeys.PUBLISH
			};
		}

		return new String[] {ActionKeys.VIEW};
	}

}