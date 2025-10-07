/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.search.spi.model.permission.contributor;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionRoleContributor;

import java.util.function.Consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.depot.model.DepotEntry",
	service = SearchPermissionRoleContributor.class
)
public class DepotEntrySearchPermissionRoleContributor
	implements SearchPermissionRoleContributor {

	@Override
	public void contribute(
		long companyId, long groupId, String className, long classPK,
		Consumer<Role> groupRoleConsumer, Consumer<Role> roleConsumer) {

		try {
			groupRoleConsumer.accept(
				_roleLocalService.getRole(
					companyId, DepotRolesConstants.ASSET_LIBRARY_MEMBER));
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotEntrySearchPermissionRoleContributor.class);

	@Reference
	private RoleLocalService _roleLocalService;

}