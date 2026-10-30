/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link AssetListEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryService
 * @generated
 */
public class AssetListEntryServiceWrapper
	implements AssetListEntryService, ServiceWrapper<AssetListEntryService> {

	public AssetListEntryServiceWrapper() {
		this(null);
	}

	public AssetListEntryServiceWrapper(
		AssetListEntryService assetListEntryService) {

		_assetListEntryService = assetListEntryService;
	}

	@Override
	public void addAssetEntrySelection(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.addAssetEntrySelection(
			assetListEntryId, assetEntryId, segmentsEntryId, serviceContext);
	}

	@Override
	public void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds, long segmentsEntryId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.addAssetEntrySelections(
			assetListEntryId, assetEntryIds, segmentsEntryId, serviceContext);
	}

	@Override
	public void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds,
			String audienceEntryERC,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.addAssetEntrySelections(
			assetListEntryId, assetEntryIds, audienceEntryERC, serviceContext);
	}

	@Override
	public AssetListEntry addAssetListEntry(
			String externalReferenceCode, long groupId, String title, int type,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.addAssetListEntry(
			externalReferenceCode, groupId, title, type, serviceContext);
	}

	@Override
	public AssetListEntry addDynamicAssetListEntry(
			String externalReferenceCode, long groupId, String title,
			String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.addDynamicAssetListEntry(
			externalReferenceCode, groupId, title, typeSettings,
			serviceContext);
	}

	@Override
	public AssetListEntry addManualAssetListEntry(
			String externalReferenceCode, long groupId, String title,
			long[] assetEntryIds,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.addManualAssetListEntry(
			externalReferenceCode, groupId, title, assetEntryIds,
			serviceContext);
	}

	@Override
	public void deleteAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.deleteAssetEntrySelection(
			assetListEntryId, segmentsEntryId, position);
	}

	@Override
	public void deleteAssetEntrySelection(
			long assetListEntryId, String audienceEntryERC, int position)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.deleteAssetEntrySelection(
			assetListEntryId, audienceEntryERC, position);
	}

	@Override
	public void deleteAssetListEntries(long[] assetListEntriesIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.deleteAssetListEntries(assetListEntriesIds);
	}

	@Override
	public AssetListEntry deleteAssetListEntry(long assetListEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.deleteAssetListEntry(assetListEntryId);
	}

	@Override
	public void deleteAssetListEntry(
			long assetListEntryId, long segmentsEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.deleteAssetListEntry(
			assetListEntryId, segmentsEntryId);
	}

	@Override
	public AssetListEntry fetchAssetListEntry(long assetListEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.fetchAssetListEntry(assetListEntryId);
	}

	@Override
	public AssetListEntry fetchAssetListEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.
			fetchAssetListEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long groupId, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupId, title, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupIds, title, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupIds, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupIds, title, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupIds, title, assetEntryTypes, start, end, orderByComparator);
	}

	@Override
	public java.util.List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<AssetListEntry>
			orderByComparator) {

		return _assetListEntryService.getAssetListEntries(
			groupIds, assetEntryTypes, start, end, orderByComparator);
	}

	@Override
	public int getAssetListEntriesCount(long groupId) {
		return _assetListEntryService.getAssetListEntriesCount(groupId);
	}

	@Override
	public int getAssetListEntriesCount(long groupId, String title) {
		return _assetListEntryService.getAssetListEntriesCount(groupId, title);
	}

	@Override
	public int getAssetListEntriesCount(long[] groupIds) {
		return _assetListEntryService.getAssetListEntriesCount(groupIds);
	}

	@Override
	public int getAssetListEntriesCount(long[] groupIds, String title) {
		return _assetListEntryService.getAssetListEntriesCount(groupIds, title);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return _assetListEntryService.getAssetListEntriesCount(
			groupIds, assetEntrySubtype, assetEntryType);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return _assetListEntryService.getAssetListEntriesCount(
			groupIds, title, assetEntrySubtype, assetEntryType);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return _assetListEntryService.getAssetListEntriesCount(
			groupIds, title, assetEntryTypes);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String[] assetEntryTypes) {

		return _assetListEntryService.getAssetListEntriesCount(
			groupIds, assetEntryTypes);
	}

	@Override
	public AssetListEntry getAssetListEntry(long assetListEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.getAssetListEntry(assetListEntryId);
	}

	@Override
	public AssetListEntry getAssetListEntry(
			long groupId, String assetListEntryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.getAssetListEntry(
			groupId, assetListEntryKey);
	}

	@Override
	public AssetListEntry getAssetListEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.getAssetListEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	@Override
	public AssetListEntry getAssetListEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.getAssetListEntryByUuidAndGroupId(
			uuid, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _assetListEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public void moveAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.moveAssetEntrySelection(
			assetListEntryId, segmentsEntryId, position, newPosition);
	}

	@Override
	public void moveAssetEntrySelection(
			long assetListEntryId, String audienceEntryERC, int position,
			int newPosition)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.moveAssetEntrySelection(
			assetListEntryId, audienceEntryERC, position, newPosition);
	}

	@Override
	public void updateAssetListEntry(
			long assetListEntryId, long segmentsEntryId, String typeSettings,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.updateAssetListEntry(
			assetListEntryId, segmentsEntryId, typeSettings, serviceContext);
	}

	@Override
	public AssetListEntry updateAssetListEntry(
			long assetListEntryId, String title)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _assetListEntryService.updateAssetListEntry(
			assetListEntryId, title);
	}

	@Override
	public void updateAssetListEntryTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.updateAssetListEntryTypeSettings(
			assetListEntryId, segmentsEntryId, typeSettings);
	}

	@Override
	public void updateAssetListEntryTypeSettings(
			long assetListEntryId, String audienceEntryERC, String typeSettings)
		throws com.liferay.portal.kernel.exception.PortalException {

		_assetListEntryService.updateAssetListEntryTypeSettings(
			assetListEntryId, audienceEntryERC, typeSettings);
	}

	@Override
	public AssetListEntryService getWrappedService() {
		return _assetListEntryService;
	}

	@Override
	public void setWrappedService(AssetListEntryService assetListEntryService) {
		_assetListEntryService = assetListEntryService;
	}

	private AssetListEntryService _assetListEntryService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-444328579