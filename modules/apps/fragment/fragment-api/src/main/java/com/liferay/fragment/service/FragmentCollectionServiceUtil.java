/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

/**
 * Provides the remote service utility for FragmentCollection. This utility wraps
 * <code>com.liferay.fragment.service.impl.FragmentCollectionServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCollectionService
 * @generated
 */
public class FragmentCollectionServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.fragment.service.impl.FragmentCollectionServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentCollection(
			externalReferenceCode, groupId, name, description, serviceContext);
	}

	public static FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addFragmentCollection(
			externalReferenceCode, groupId, fragmentCollectionKey, name,
			description, marketplace, serviceContext);
	}

	public static FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws PortalException {

		return getService().deleteFragmentCollection(fragmentCollectionId);
	}

	public static FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteFragmentCollection(
			externalReferenceCode, groupId);
	}

	public static void deleteFragmentCollections(long[] fragmentCollectionIds)
		throws PortalException {

		getService().deleteFragmentCollections(fragmentCollectionIds);
	}

	public static FragmentCollection fetchFragmentCollection(
			long fragmentCollectionId)
		throws PortalException {

		return getService().fetchFragmentCollection(fragmentCollectionId);
	}

	public static List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds) {

		return getService().getExportableFragmentCollections(
			fragmentCollectionIds);
	}

	public static List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, int start, int end,
			OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getExportableFragmentCollectionsByGroupId(
			groupIds, start, end, orderByComparator);
	}

	public static List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, String name, int start, int end,
			OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getExportableFragmentCollectionsByGroupId(
			groupIds, name, start, end, orderByComparator);
	}

	public static int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds) {

		return getService().getExportableFragmentCollectionsCount(
			fragmentCollectionIds);
	}

	public static int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds) {

		return getService().getExportableFragmentCollectionsCountByGroupId(
			groupIds);
	}

	public static int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name) {

		return getService().getExportableFragmentCollectionsCountByGroupId(
			groupIds, name);
	}

	public static FragmentCollection getFragmentCollection(
			long fragmentCollectionId)
		throws PortalException {

		return getService().getFragmentCollection(fragmentCollectionId);
	}

	public static FragmentCollection
			getFragmentCollectionByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getFragmentCollectionByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	public static List<com.liferay.portal.kernel.repository.model.FileEntry>
			getFragmentCollectionFileEntries(long fragmentCollectionId)
		throws PortalException {

		return getService().getFragmentCollectionFileEntries(
			fragmentCollectionId);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId) {

		return getService().getFragmentCollections(groupId);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem) {

		return getService().getFragmentCollections(groupId, includeSystem);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, includeSystem, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end) {

		return getService().getFragmentCollections(groupId, start, end);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, name, includeSystem, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupId, name, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds) {

		return getService().getFragmentCollections(groupIds);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, marketplace, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, name, marketplace, start, end, orderByComparator);
	}

	public static List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getService().getFragmentCollections(
			groupIds, name, start, end, orderByComparator);
	}

	public static int getFragmentCollectionsCount(long groupId) {
		return getService().getFragmentCollectionsCount(groupId);
	}

	public static int getFragmentCollectionsCount(
		long groupId, boolean includeSystem) {

		return getService().getFragmentCollectionsCount(groupId, includeSystem);
	}

	public static int getFragmentCollectionsCount(long groupId, String name) {
		return getService().getFragmentCollectionsCount(groupId, name);
	}

	public static int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem) {

		return getService().getFragmentCollectionsCount(
			groupId, name, includeSystem);
	}

	public static int getFragmentCollectionsCount(long[] groupIds) {
		return getService().getFragmentCollectionsCount(groupIds);
	}

	public static int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace) {

		return getService().getFragmentCollectionsCount(groupIds, marketplace);
	}

	public static int getFragmentCollectionsCount(
		long[] groupIds, String name) {

		return getService().getFragmentCollectionsCount(groupIds, name);
	}

	public static int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace) {

		return getService().getFragmentCollectionsCount(
			groupIds, name, marketplace);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static String[] getTempFileNames(long groupId, String folderName)
		throws PortalException {

		return getService().getTempFileNames(groupId, folderName);
	}

	public static FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws PortalException {

		return getService().updateFragmentCollection(
			fragmentCollectionId, name, description);
	}

	public static FragmentCollectionService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<FragmentCollectionService> _serviceSnapshot =
		new Snapshot<>(
			FragmentCollectionServiceUtil.class,
			FragmentCollectionService.class);

}
// LIFERAY-SERVICE-BUILDER-HASH:-157910332