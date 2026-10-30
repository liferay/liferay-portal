/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service;

import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.jsonwebservice.JSONWebServiceMode;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.service.BaseService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the remote service interface for AssetListEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see AssetListEntryServiceUtil
 * @generated
 */
@AccessControlled
@CTAware
@JSONWebService
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface AssetListEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.asset.list.service.impl.AssetListEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the asset list entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link AssetListEntryServiceUtil} if injection and service tracking are not available.
	 */
	public void addAssetEntrySelection(
			long assetListEntryId, long assetEntryId, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException;

	public void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds, long segmentsEntryId,
			ServiceContext serviceContext)
		throws PortalException;

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public void addAssetEntrySelections(
			long assetListEntryId, long[] assetEntryIds,
			String audienceEntryERC, ServiceContext serviceContext)
		throws PortalException;

	public AssetListEntry addAssetListEntry(
			String externalReferenceCode, long groupId, String title, int type,
			ServiceContext serviceContext)
		throws PortalException;

	public AssetListEntry addDynamicAssetListEntry(
			String externalReferenceCode, long groupId, String title,
			String typeSettings, ServiceContext serviceContext)
		throws PortalException;

	public AssetListEntry addManualAssetListEntry(
			String externalReferenceCode, long groupId, String title,
			long[] assetEntryIds, ServiceContext serviceContext)
		throws PortalException;

	public void deleteAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position)
		throws PortalException;

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public void deleteAssetEntrySelection(
			long assetListEntryId, String audienceEntryERC, int position)
		throws PortalException;

	public void deleteAssetListEntries(long[] assetListEntriesIds)
		throws PortalException;

	public AssetListEntry deleteAssetListEntry(long assetListEntryId)
		throws PortalException;

	public void deleteAssetListEntry(
			long assetListEntryId, long segmentsEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetListEntry fetchAssetListEntry(long assetListEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetListEntry fetchAssetListEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long groupId, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long groupId, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String assetEntrySubtype, String assetEntryType,
		int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String title, String[] assetEntryTypes, int start,
		int end, OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<AssetListEntry> getAssetListEntries(
		long[] groupIds, String[] assetEntryTypes, int start, int end,
		OrderByComparator<AssetListEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(long groupId, String title);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(long[] groupIds);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(long[] groupIds, String title);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(
		long[] groupIds, String assetEntrySubtype, String assetEntryType);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(
		long[] groupIds, String title, String assetEntrySubtype,
		String assetEntryType);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(
		long[] groupIds, String title, String[] assetEntryTypes);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getAssetListEntriesCount(
		long[] groupIds, String[] assetEntryTypes);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetListEntry getAssetListEntry(long assetListEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetListEntry getAssetListEntry(
			long groupId, String assetListEntryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetListEntry getAssetListEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public AssetListEntry getAssetListEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public void moveAssetEntrySelection(
			long assetListEntryId, long segmentsEntryId, int position,
			int newPosition)
		throws PortalException;

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public void moveAssetEntrySelection(
			long assetListEntryId, String audienceEntryERC, int position,
			int newPosition)
		throws PortalException;

	public void updateAssetListEntry(
			long assetListEntryId, long segmentsEntryId, String typeSettings,
			ServiceContext serviceContext)
		throws PortalException;

	public AssetListEntry updateAssetListEntry(
			long assetListEntryId, String title)
		throws PortalException;

	public void updateAssetListEntryTypeSettings(
			long assetListEntryId, long segmentsEntryId, String typeSettings)
		throws PortalException;

	@JSONWebService(mode = JSONWebServiceMode.IGNORE)
	public void updateAssetListEntryTypeSettings(
			long assetListEntryId, String audienceEntryERC, String typeSettings)
		throws PortalException;

}
// LIFERAY-SERVICE-BUILDER-HASH:-167781752