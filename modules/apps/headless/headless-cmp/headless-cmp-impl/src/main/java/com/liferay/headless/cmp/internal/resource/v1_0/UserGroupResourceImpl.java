/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.headless.cmp.resource.v1_0.UserGroupResource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/user-group.properties",
	scope = ServiceScope.PROTOTYPE, service = UserGroupResource.class
)
public class UserGroupResourceImpl extends BaseUserGroupResourceImpl {
}