/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.impl;

import com.liferay.asset.list.constants.AssetListActionKeys;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.service.base.AssetListEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.dao.orm.custom.sql.CustomSQL;
import com.liferay.portal.kernel.dao.orm.WildcardMode;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"json.web.service.context.name=assetlist",
		"json.web.service.context.path=AssetListEntry"
	},
	service = AopService.class
)
public class AssetListEntryServiceImpl extends AssetListEntryServiceBaseImpl {

	@Override
	public void addAssetEntrySelection(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		assetListEntryLocalService.addAssetEntrySelection(
			assetListEntryId, assetEntryId, segmentsEntryId, serviceContext);
	}

	@Override
	public void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		assetListEntryLocalService.addAssetEntrySelections(
			assetListEntryId, assetEntryIds, segmentsEntryId, serviceContext);
	}

	@Override
	public AssetListEntry addAssetListEntry(
			String externalReferenceCode, long groupId, String title, int type,
			ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			AssetListActionKeys.ADD_ASSET_LIST_ENTRY);

		return assetListEntryLocalService.addAssetListEntry(
			externalReferenceCode, getUserId(), groupId, title, type,
			serviceContext);
	}

	@Override
	public AssetListEntry addDynamicAssetListEntry(
			String externalReferenceCode, long groupId, String title,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			AssetListActionKeys.ADD_ASSET_LIST_ENTRY);

		return assetListEntryLocalService.addDynamicAssetListEntry(
			externalReferenceCode, getUserId(), groupId, title, typeSettings,
			serviceContext);
	}

	@Override
	public AssetListEntry addManualAssetListEntry(
			String externalReferenceCode, long groupId, String title,
			long[] assetEntryIds, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			AssetListActionKeys.ADD_ASSET_LIST_ENTRY);

		return assetListEntryLocalService.addManualAssetListEntry(
			externalReferenceCode, getUserId(), groupId, title, assetEntryIds,
			serviceContext);
	}

	@Override
	public void deleteAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		assetListEntryLocalService.deleteAssetEntrySelection(
			assetListEntryId, segmentsEntryId, position);
	}

	@Override
	public void deleteAssetListEntries(long[] assetListEntriesIds)
		throws PortalException {

		for (long assetListEntryId : assetListEntriesIds) {
			AssetListEntry assetListEntry =
				assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

			_assetListEntryModelResourcePermission.check(
				getPermissionChecker(), assetListEntry, ActionKeys.DELETE);

			assetListEntryLocalService.deleteAssetListEntry(assetListEntry);
		}
	}

	@Override
	public AssetListEntry deleteAssetListEntry(long assetListEntryId)
		throws PortalException {

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(), assetListEntry, ActionKeys.DELETE);

		return assetListEntryLocalService.deleteAssetListEntry(assetListEntry);
	}

	@Override
	public void deleteAssetListEntry(
			long assetListEntryId, long segmentsEntryId)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		assetListEntryLocalService.deleteAssetListEntry(
			assetListEntryId, segmentsEntryId);
	}

	@Override
	public AssetListEntry fetchAssetListEntry(long assetListEntryId)
		throws PortalException {

		AssetListEntry assetListEntry =
			assetListEntryPersistence.fetchByPrimaryKey(assetListEntryId);

		if (assetListEntry != null) {
			_assetListEntryModelResourcePermission.check(
				getPermissionChecker(), assetListEntry, ActionKeys.VIEW);
		}

		return assetListEntry;
	}

	@Override
	public AssetListEntry fetchAssetListEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetListEntry assetListEntry =
			assetListEntryLocalService.
				fetchAssetListEntryByExternalReferenceCode(
					externalReferenceCode, groupId);

		if (assetListEntry != null) {
			_assetListEntryModelResourcePermission.check(
				getPermissionChecker(), assetListEntry, ActionKeys.VIEW);
		}

		return assetListEntry;
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long groupId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByGroupId(
			groupId, start, end, orderByComparator);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByG_LikeT(
			groupId,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByGroupId(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByG_LikeT(
			groupIds,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0], start,
			end, orderByComparator);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType, start, end,
			orderByComparator);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByG_LikeT_AES_AET(
			groupIds,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0],
			assetEntrySubtype, assetEntryType, start, end, orderByComparator);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByG_LikeT_AET(
			groupIds,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0],
			assetEntryTypes, start, end, orderByComparator);
	}

	@Override
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator) {

		return assetListEntryPersistence.filterFindByG_AET(
			groupIds, assetEntryTypes, start, end, orderByComparator);
	}

	@Override
	public int getAssetListEntriesCount(long groupId) {
		return assetListEntryPersistence.filterCountByGroupId(groupId);
	}

	@Override
	public int getAssetListEntriesCount(long groupId, String title) {
		return assetListEntryPersistence.filterCountByG_LikeT(
			groupId,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public int getAssetListEntriesCount(long[] groupIds) {
		return assetListEntryPersistence.filterCountByGroupId(groupIds);
	}

	@Override
	public int getAssetListEntriesCount(long[] groupIds, String title) {
		return assetListEntryPersistence.filterCountByG_LikeT(
			groupIds,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0]);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String assetEntrySubtype, String assetEntryType) {

		return assetListEntryPersistence.filterCountByG_AES_AET(
			groupIds, assetEntrySubtype, assetEntryType);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType) {

		return assetListEntryPersistence.filterCountByG_LikeT_AES_AET(
			groupIds,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0],
			assetEntrySubtype, assetEntryType);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String title, String[] assetEntryTypes) {

		return assetListEntryPersistence.filterCountByG_LikeT_AET(
			groupIds,
			_customSQL.keywords(title, false, WildcardMode.SURROUND)[0],
			assetEntryTypes);
	}

	@Override
	public int getAssetListEntriesCount(
		long[] groupIds, String[] assetEntryTypes) {

		return assetListEntryPersistence.filterCountByG_AET(
			groupIds, assetEntryTypes);
	}

	@Override
	public AssetListEntry getAssetListEntry(long assetListEntryId)
		throws PortalException {

		AssetListEntry assetListEntry =
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId);

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(), assetListEntry, ActionKeys.VIEW);

		return assetListEntry;
	}

	@Override
	public AssetListEntry getAssetListEntry(
			long groupId, String assetListEntryKey)
		throws PortalException {

		AssetListEntry assetListEntry = assetListEntryPersistence.findByG_ALEK(
			groupId, assetListEntryKey);

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(), assetListEntry, ActionKeys.VIEW);

		return assetListEntry;
	}

	@Override
	public AssetListEntry getAssetListEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		AssetListEntry assetListEntry = assetListEntryPersistence.findByERC_G(
			externalReferenceCode, groupId);

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(), assetListEntry, ActionKeys.VIEW);

		return assetListEntry;
	}

	@Override
	public AssetListEntry getAssetListEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		AssetListEntry assetListEntry = assetListEntryPersistence.findByUUID_G(
			uuid, groupId);

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(), assetListEntry, ActionKeys.VIEW);

		return assetListEntry;
	}

	@Override
	public void moveAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		assetListEntryLocalService.moveAssetEntrySelection(
			assetListEntryId, segmentsEntryId, position, newPosition);
	}

	@Override
	public void updateAssetListEntry(
			long assetListEntryId, long segmentsEntryId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		assetListEntryLocalService.updateAssetListEntry(
			assetListEntryId, segmentsEntryId, typeSettings, serviceContext);
	}

	@Override
	public AssetListEntry updateAssetListEntry(
			long assetListEntryId, String title)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		return assetListEntryLocalService.updateAssetListEntry(
			assetListEntryId, title);
	}

	@Override
	public void updateAssetListEntryTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings)
		throws PortalException {

		_assetListEntryModelResourcePermission.check(
			getPermissionChecker(),
			assetListEntryPersistence.findByPrimaryKey(assetListEntryId),
			ActionKeys.UPDATE);

		assetListEntryLocalService.updateAssetListEntryTypeSettings(
			assetListEntryId, segmentsEntryId, typeSettings);
	}

	@Reference(
		target = "(model.class.name=com.liferay.asset.list.model.AssetListEntry)"
	)
	private ModelResourcePermission<AssetListEntry>
		_assetListEntryModelResourcePermission;

	@Reference
	private CustomSQL _customSQL;

	@Reference(target = "(resource.name=com.liferay.asset.list)")
	private PortletResourcePermission _portletResourcePermission;

}