/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.batch.engine.action;

import com.liferay.batch.engine.BatchEngineTaskItemDelegate;
import com.liferay.batch.engine.action.ImportTaskPostAction;
import com.liferay.batch.engine.context.ImportTaskContext;
import com.liferay.batch.engine.model.BatchEngineImportTask;
import com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Álvaro Saugar
 */
@Component(service = ImportTaskPostAction.class)
public class CookieEntryObjectDefinitionImportTaskPostAction
	implements ImportTaskPostAction {

	@Override
	public void run(
			BatchEngineImportTask batchEngineImportTask,
			BatchEngineTaskItemDelegate<?> batchEngineTaskItemDelegate,
			ImportTaskContext importTaskContext, Object item,
			Object persistedItem)
		throws Exception {

		if (!(persistedItem instanceof ObjectDefinition)) {
			return;
		}

		ObjectDefinition objectDefinition = (ObjectDefinition)persistedItem;

		String externalReferenceCode =
			objectDefinition.getExternalReferenceCode();

		if (!ArrayUtil.contains(
				_EXTERNAL_REFERENCE_CODES, externalReferenceCode)) {

			return;
		}

		long companyId = batchEngineImportTask.getCompanyId();

		com.liferay.object.model.ObjectDefinition
			serviceBuilderObjectDefinition =
				_objectDefinitionLocalService.
					fetchObjectDefinitionByExternalReferenceCode(
						externalReferenceCode, companyId);

		if ((serviceBuilderObjectDefinition == null) ||
			!serviceBuilderObjectDefinition.isApproved()) {

			return;
		}

		for (String roleName : _ROLE_NAMES) {
			Role role = _roleLocalService.fetchRole(companyId, roleName);

			if (role == null) {
				continue;
			}

			_resourcePermissionLocalService.addResourcePermission(
				companyId, serviceBuilderObjectDefinition.getClassName(),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId(), ActionKeys.VIEW);
			_resourcePermissionLocalService.addResourcePermission(
				companyId, serviceBuilderObjectDefinition.getPortletId(),
				ResourceConstants.SCOPE_COMPANY, String.valueOf(companyId),
				role.getRoleId(), ActionKeys.VIEW);
		}
	}

	private static final String[] _EXTERNAL_REFERENCE_CODES = {
		"L_FUNCTIONAL_COOKIE_ENTRY", "L_NECESSARY_COOKIE_ENTRY",
		"L_PERFORMANCE_COOKIE_ENTRY", "L_PERSONALIZATION_COOKIE_ENTRY"
	};

	private static final String[] _ROLE_NAMES = {
		RoleConstants.GUEST, RoleConstants.USER
	};

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}