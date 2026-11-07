/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.internal.security.permission.resource;

import com.liferay.portal.kernel.security.permission.resource.BaseModelResourcePermissionWrapper;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsoleConstants;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.service.DefinitionLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leon Chi
 */
@Component(
	property = "model.class.name=com.liferay.portal.reports.engine.console.model.Definition",
	service = ModelResourcePermission.class
)
public class DefinitionModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<Definition> {

	@Override
	protected ModelResourcePermission<Definition>
		doGetModelResourcePermission() {

		return ModelResourcePermissionFactory.create(
			Definition.class, Definition::getDefinitionId,
			_definitionLocalService::getDefinition, _portletResourcePermission,
			(modelResourcePermission, consumer) -> {
			});
	}

	@Reference
	private DefinitionLocalService _definitionLocalService;

	@Reference(
		target = "(resource.name=" + ReportsEngineConsoleConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}