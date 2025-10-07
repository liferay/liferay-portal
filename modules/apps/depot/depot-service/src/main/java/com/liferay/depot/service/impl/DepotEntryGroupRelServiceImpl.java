/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.impl;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.base.DepotEntryGroupRelServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=depot",
		"json.web.service.context.path=DepotEntryGroupRel"
	},
	service = AopService.class
)
public class DepotEntryGroupRelServiceImpl
	extends DepotEntryGroupRelServiceBaseImpl {

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
			long depotEntryId, long toGroupId)
		throws PortalException {

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntryId, ActionKeys.UPDATE);

		return depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntryId, toGroupId);
	}

	@Override
	public DepotEntryGroupRel deleteDepotEntryGroupRel(
			long depotEntryGroupRelId)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel =
			depotEntryGroupRelLocalService.getDepotEntryGroupRel(
				depotEntryGroupRelId);

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntryGroupRel.getDepotEntryId(),
			ActionKeys.UPDATE);

		return depotEntryGroupRelLocalService.deleteDepotEntryGroupRel(
			depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel getDepotEntryGroupRelByDepotEntryIdToGroupId(
			long depotEntryId, long toGroupId)
		throws PortalException {

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntryId, ActionKeys.VIEW);

		return depotEntryGroupRelLocalService.
			getDepotEntryGroupRelByDepotEntryIdToGroupId(
				depotEntryId, toGroupId);
	}

	@Override
	public List<DepotEntryGroupRel> getDepotEntryGroupRels(
			DepotEntry depotEntry, int start, int end)
		throws PortalException {

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntry.getDepotEntryId(),
			ActionKeys.VIEW);

		return depotEntryGroupRelLocalService.getDepotEntryGroupRels(
			depotEntry, start, end);
	}

	@Override
	public List<DepotEntryGroupRel> getDepotEntryGroupRels(
			long groupId, int type, int start, int end)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return depotEntryGroupRelLocalService.getDepotEntryGroupRels(
			groupId, type, start, end);
	}

	@Override
	public int getDepotEntryGroupRelsCount(DepotEntry depotEntry)
		throws PortalException {

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntry.getDepotEntryId(),
			ActionKeys.VIEW);

		return depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
			depotEntry);
	}

	@Override
	public int getDepotEntryGroupRelsCount(long groupId, int type)
		throws PortalException {

		GroupPermissionUtil.check(
			getPermissionChecker(), groupId, ActionKeys.VIEW);

		return depotEntryGroupRelLocalService.getDepotEntryGroupRelsCount(
			groupId, type);
	}

	@Override
	public DepotEntryGroupRel updateDDMStructuresAvailable(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel =
			depotEntryGroupRelLocalService.getDepotEntryGroupRel(
				depotEntryGroupRelId);

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntryGroupRel.getDepotEntryId(),
			ActionKeys.UPDATE);

		return depotEntryGroupRelLocalService.updateDDMStructuresAvailable(
			depotEntryGroupRelId, ddmStructuresAvailable);
	}

	@Override
	public DepotEntryGroupRel updateSearchable(
			long depotEntryGroupRelId, boolean searchable)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel =
			depotEntryGroupRelLocalService.getDepotEntryGroupRel(
				depotEntryGroupRelId);

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntryGroupRel.getDepotEntryId(),
			ActionKeys.UPDATE);

		return depotEntryGroupRelLocalService.updateSearchable(
			depotEntryGroupRelId, searchable);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.depot.model.DepotEntry)"
	)
	private volatile ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

}