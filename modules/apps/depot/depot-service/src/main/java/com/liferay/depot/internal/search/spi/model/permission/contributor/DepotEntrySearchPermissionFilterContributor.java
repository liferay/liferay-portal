/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.search.spi.model.permission.contributor;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermsFilter;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.search.spi.model.permission.contributor.SearchPermissionFilterContributor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 * @author Roberto Díaz
 */
@Component(service = SearchPermissionFilterContributor.class)
public class DepotEntrySearchPermissionFilterContributor
	implements SearchPermissionFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, long companyId, long[] groupIds,
		long userId, PermissionChecker permissionChecker, String className) {

		try {
			_contribute(booleanFilter, companyId, userId, permissionChecker);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}
	}

	private void _contribute(
			BooleanFilter booleanFilter, long companyId, long userId,
			PermissionChecker permissionChecker)
		throws PortalException {

		if (userId == 0) {
			return;
		}

		Role role = _roleLocalService.fetchRole(
			companyId, DepotRolesConstants.ASSET_LIBRARY_MEMBER);

		if (role == null) {
			return;
		}

		TermsFilter groupRolesTermsFilter = new TermsFilter(
			Field.GROUP_ROLE_ID);

		for (long groupId :
				_depotEntryLocalService.getDepotEntryGroupIds(
					permissionChecker.getCompanyId(), userId,
					DepotConstants.TYPE_ANY, true)) {

			groupRolesTermsFilter.addValue(
				StringBundler.concat(
					groupId, StringPool.DASH, role.getRoleId()));
		}

		if (!groupRolesTermsFilter.isEmpty()) {
			booleanFilter.add(groupRolesTermsFilter, BooleanClauseOccur.SHOULD);
		}

		if (!FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")) {
			return;
		}

		Role designLibraryMemberRole = _roleLocalService.fetchRole(
			companyId, DepotRolesConstants.DESIGN_LIBRARY_MEMBER);

		if (designLibraryMemberRole == null) {
			return;
		}

		TermsFilter designLibraryGroupRolesTermsFilter = new TermsFilter(
			Field.GROUP_ROLE_ID);

		for (long groupId :
				_depotEntryLocalService.getDepotEntryGroupIds(
					permissionChecker.getCompanyId(), userId,
					DepotConstants.TYPE_DESIGN_LIBRARY, true)) {

			designLibraryGroupRolesTermsFilter.addValue(
				StringBundler.concat(
					groupId, StringPool.DASH,
					designLibraryMemberRole.getRoleId()));
		}

		if (!designLibraryGroupRolesTermsFilter.isEmpty()) {
			booleanFilter.add(
				designLibraryGroupRolesTermsFilter, BooleanClauseOccur.SHOULD);
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DepotEntrySearchPermissionFilterContributor.class);

	@Reference
	private DepotEntryLocalService _depotEntryLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}