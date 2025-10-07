/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service;

import com.liferay.depot.model.DepotEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link DepotEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see DepotEntryService
 * @generated
 */
public class DepotEntryServiceWrapper
	implements DepotEntryService, ServiceWrapper<DepotEntryService> {

	public DepotEntryServiceWrapper() {
		this(null);
	}

	public DepotEntryServiceWrapper(DepotEntryService depotEntryService) {
		_depotEntryService = depotEntryService;
	}

	@Override
	public DepotEntry addDepotEntry(
			java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);
	}

	@Override
	public DepotEntry deleteDepotEntry(long depotEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.deleteDepotEntry(depotEntryId);
	}

	@Override
	public DepotEntry fetchGroupDepotEntry(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.fetchGroupDepotEntry(groupId);
	}

	@Override
	public java.util.List<DepotEntry> getCurrentAndGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.getCurrentAndGroupConnectedDepotEntries(
			groupId, type, start, end);
	}

	@Override
	public DepotEntry getDepotEntry(long depotEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.getDepotEntry(depotEntryId);
	}

	@Override
	public java.util.List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, boolean ddmStructuresAvailable, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.getGroupConnectedDepotEntries(
			groupId, ddmStructuresAvailable, start, end);
	}

	@Override
	public java.util.List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.getGroupConnectedDepotEntries(
			groupId, type, start, end);
	}

	@Override
	public int getGroupConnectedDepotEntriesCount(long groupId, int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.getGroupConnectedDepotEntriesCount(
			groupId, type);
	}

	@Override
	public DepotEntry getGroupDepotEntry(long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.getGroupDepotEntry(groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _depotEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public DepotEntry updateDepotEntry(
			long depotEntryId, java.util.Map<java.util.Locale, String> nameMap,
			java.util.Map<java.util.Locale, String> descriptionMap,
			java.util.Map<String, Boolean> depotAppCustomizationMap,
			com.liferay.portal.kernel.util.UnicodeProperties
				typeSettingsUnicodeProperties,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _depotEntryService.updateDepotEntry(
			depotEntryId, nameMap, descriptionMap, depotAppCustomizationMap,
			typeSettingsUnicodeProperties, serviceContext);
	}

	@Override
	public DepotEntryService getWrappedService() {
		return _depotEntryService;
	}

	@Override
	public void setWrappedService(DepotEntryService depotEntryService) {
		_depotEntryService = depotEntryService;
	}

	private DepotEntryService _depotEntryService;

}