/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.impl;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.base.DepotEntryGroupRelLocalServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.systemevent.SystemEvent;

import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=com.liferay.depot.model.DepotEntryGroupRel",
	service = AopService.class
)
public class DepotEntryGroupRelLocalServiceImpl
	extends DepotEntryGroupRelLocalServiceBaseImpl {

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
		boolean ddmStructuresAvailable, long depotEntryId, long toGroupId,
		boolean searchable) {

		DepotEntryGroupRel depotEntryGroupRel =
			depotEntryGroupRelPersistence.fetchByD_TGI(depotEntryId, toGroupId);

		if (depotEntryGroupRel != null) {
			return depotEntryGroupRel;
		}

		depotEntryGroupRel = depotEntryGroupRelPersistence.create(
			counterLocalService.increment());

		depotEntryGroupRel.setGroupId(toGroupId);
		depotEntryGroupRel.setDdmStructuresAvailable(ddmStructuresAvailable);
		depotEntryGroupRel.setDepotEntryId(depotEntryId);
		depotEntryGroupRel.setSearchable(searchable);
		depotEntryGroupRel.setToGroupId(toGroupId);

		ServiceContext serviceContext =
			ServiceContextThreadLocal.getServiceContext();

		if (serviceContext != null) {
			depotEntryGroupRel.setUuid(serviceContext.getUuid());
		}

		return depotEntryGroupRelPersistence.update(depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
		long depotEntryId, long toGroupId) {

		return addDepotEntryGroupRel(depotEntryId, toGroupId, true);
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
		long depotEntryId, long toGroupId, boolean searchable) {

		return addDepotEntryGroupRel(
			false, depotEntryId, toGroupId, searchable);
	}

	@Override
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public DepotEntryGroupRel deleteDepotEntryGroupRel(
		DepotEntryGroupRel depotEntryGroupRel) {

		return super.deleteDepotEntryGroupRel(depotEntryGroupRel);
	}

	@Override
	public void deleteToGroupDepotEntryGroupRels(long toGroupId) {
		depotEntryGroupRelPersistence.removeByToGroupId(toGroupId);
	}

	@Override
	public DepotEntryGroupRel fetchDepotEntryGroupRelByDepotEntryIdToGroupId(
		long depotEntryId, long toGroupId) {

		return depotEntryGroupRelPersistence.fetchByD_TGI(
			depotEntryId, toGroupId);
	}

	@Override
	public DepotEntryGroupRel getDepotEntryGroupRelByDepotEntryIdToGroupId(
			long depotEntryId, long toGroupId)
		throws PortalException {

		return depotEntryGroupRelPersistence.findByD_TGI(
			depotEntryId, toGroupId);
	}

	@Override
	public List<DepotEntryGroupRel> getDepotEntryGroupRels(
		DepotEntry depotEntry) {

		return depotEntryGroupRelPersistence.findByDepotEntryId(
			depotEntry.getDepotEntryId());
	}

	@Override
	public List<DepotEntryGroupRel> getDepotEntryGroupRels(
		long groupId, int start, int end) {

		return depotEntryGroupRelPersistence.findByToGroupId(
			groupId, start, end);
	}

	@Override
	public int getDepotEntryGroupRelsCount(DepotEntry depotEntry) {
		return depotEntryGroupRelPersistence.countByDepotEntryId(
			depotEntry.getDepotEntryId());
	}

	@Override
	public int getDepotEntryGroupRelsCount(long groupId) {
		return depotEntryGroupRelPersistence.countByToGroupId(groupId);
	}

	@Override
	public List<DepotEntryGroupRel> getSearchableDepotEntryGroupRels(
		long groupId, int start, int end) {

		return depotEntryGroupRelPersistence.findByS_TGI(
			true, groupId, start, end);
	}

	@Override
	public int getSearchableDepotEntryGroupRelsCount(long groupId) {
		return depotEntryGroupRelPersistence.countByS_TGI(true, groupId);
	}

	@Override
	public DepotEntryGroupRel updateDDMStructuresAvailable(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel = getDepotEntryGroupRel(
			depotEntryGroupRelId);

		depotEntryGroupRel.setDdmStructuresAvailable(ddmStructuresAvailable);

		return depotEntryGroupRelPersistence.update(depotEntryGroupRel);
	}

	@Override
	public DepotEntryGroupRel updateSearchable(
			long depotEntryGroupRelId, boolean searchable)
		throws PortalException {

		DepotEntryGroupRel depotEntryGroupRel = getDepotEntryGroupRel(
			depotEntryGroupRelId);

		depotEntryGroupRel.setSearchable(searchable);

		return depotEntryGroupRelPersistence.update(depotEntryGroupRel);
	}

}