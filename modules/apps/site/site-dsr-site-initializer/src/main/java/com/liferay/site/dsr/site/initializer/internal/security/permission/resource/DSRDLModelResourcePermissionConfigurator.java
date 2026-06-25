/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.security.permission.resource;

import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionFactory;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;

import java.util.Set;
import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;

/**
 * @author Balazs Breier
 */
@Component(
	property = {
		"model.class.name=com.liferay.document.library.kernel.model.DLFileEntry",
		"model.class.name=com.liferay.document.library.kernel.model.DLFolder"
	},
	service = ModelResourcePermissionFactory.ModelResourcePermissionConfigurator.class
)
public class DSRDLModelResourcePermissionConfigurator
	implements ModelResourcePermissionFactory.
				   ModelResourcePermissionConfigurator<GroupedModel> {

	@Override
	public void configureModelResourcePermissionLogics(
		ModelResourcePermission<GroupedModel> modelResourcePermission,
		Consumer<ModelResourcePermissionLogic<GroupedModel>> consumer) {

		consumer.accept(
			(permissionChecker, name, groupedModel, actionId) -> {
				if (_actionIds.contains(actionId)) {
					return null;
				}

				if (DSRRoomUtil.isReadOnly(
						groupedModel.getGroupId(), permissionChecker)) {

					return false;
				}

				return null;
			});
	}

	private static final Set<String> _actionIds = SetUtil.fromArray(
		ActionKeys.ACCESS, ActionKeys.VIEW);

}