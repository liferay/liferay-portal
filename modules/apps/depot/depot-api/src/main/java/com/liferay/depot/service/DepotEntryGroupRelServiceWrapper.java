/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DepotEntryGroupRelService}.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryGroupRelService
 * @generated
 */
public class DepotEntryGroupRelServiceWrapper
	implements DepotEntryGroupRelService,
			   ServiceWrapper<DepotEntryGroupRelService> {

	public DepotEntryGroupRelServiceWrapper() {
		this(null);
	}

	public DepotEntryGroupRelServiceWrapper(
		DepotEntryGroupRelService depotEntryGroupRelService) {

		_depotEntryGroupRelService = depotEntryGroupRelService;
	}

	@Override
	public DepotEntryGroupRel addDepotEntryGroupRel(
			long depotEntryId, long toGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.addDepotEntryGroupRel(
			depotEntryId, toGroupId);
	}

	@Override
	public DepotEntryGroupRel deleteDepotEntryGroupRel(
			long depotEntryGroupRelId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.deleteDepotEntryGroupRel(
			depotEntryGroupRelId);
	}

	@Override
	public DepotEntryGroupRel getDepotEntryGroupRelByDepotEntryIdToGroupId(
			long depotEntryId, long toGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.
			getDepotEntryGroupRelByDepotEntryIdToGroupId(
				depotEntryId, toGroupId);
	}

	@Override
	public java.util.List<DepotEntryGroupRel> getDepotEntryGroupRels(
			long groupId, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.getDepotEntryGroupRels(
			groupId, start, end);
	}

	@Override
	public int getDepotEntryGroupRelsCount(
			com.liferay.depot.model.DepotEntry depotEntry)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.getDepotEntryGroupRelsCount(
			depotEntry);
	}

	@Override
	public int getDepotEntryGroupRelsCount(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.getDepotEntryGroupRelsCount(groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _depotEntryGroupRelService.getOSGiServiceIdentifier();
	}

	@Override
	public DepotEntryGroupRel updateDDMStructuresAvailable(
			long depotEntryGroupRelId, boolean ddmStructuresAvailable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.updateDDMStructuresAvailable(
			depotEntryGroupRelId, ddmStructuresAvailable);
	}

	@Override
	public DepotEntryGroupRel updateSearchable(
			long depotEntryGroupRelId, boolean searchable)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryGroupRelService.updateSearchable(
			depotEntryGroupRelId, searchable);
	}

	@Override
	public DepotEntryGroupRelService getWrappedService() {
		return _depotEntryGroupRelService;
	}

	@Override
	public void setWrappedService(
		DepotEntryGroupRelService depotEntryGroupRelService) {

		_depotEntryGroupRelService = depotEntryGroupRelService;
	}

	private DepotEntryGroupRelService _depotEntryGroupRelService;

}