/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.internal.security.permission.contributor;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.contributor.RoleCollection;
import com.liferay.portal.kernel.security.permission.contributor.RoleContributor;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(service = RoleContributor.class)
public class SiteRoleContributor implements RoleContributor {

	@Override
	public void contribute(RoleCollection roleCollection) {
		try {
			if (roleCollection.getGroupId() <= 0) {
				return;
			}

			Group group = _groupLocalService.getGroup(
				roleCollection.getGroupId());

			if (group.isCMS() &&
				_isSpaceDepotEntryMember(
					group.getCompanyId(), roleCollection.getUser())) {

				Role role = _roleLocalService.getRole(
					roleCollection.getCompanyId(), RoleConstants.SITE_MEMBER);

				roleCollection.addRoleId(role.getRoleId());
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private boolean _isSpaceDepotEntryMember(long companyId, User user) {
		if (companyId != user.getCompanyId()) {
			return false;
		}

		List<Long> depotEntryGroupIds =
			_depotEntryLocalService.getDepotEntryGroupIds(
				companyId, user.getUserId(), DepotConstants.TYPE_SPACE);

		return !depotEntryGroupIds.isEmpty();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SiteRoleContributor.class);

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}