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
import com.liferay.portal.reports.engine.console.model.Source;
import com.liferay.portal.reports.engine.console.service.SourceLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Leon Chi
 */
@Component(
	property = "model.class.name=com.liferay.portal.reports.engine.console.model.Source",
	service = ModelResourcePermission.class
)
public class SourceModelResourcePermissionWrapper
	extends BaseModelResourcePermissionWrapper<Source> {

	@Override
	protected ModelResourcePermission<Source> doGetModelResourcePermission() {
		return ModelResourcePermissionFactory.create(
			Source.class, Source::getSourceId, _sourceLocalService::getSource,
			_portletResourcePermission,
			(modelResourcePermission, consumer) -> {
			});
	}

	@Reference(
		target = "(resource.name=" + ReportsEngineConsoleConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

	@Reference
	private SourceLocalService _sourceLocalService;

}