/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service;

import com.liferay.fragment.model.FragmentCollection;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link FragmentCollectionService}.
 *
 * @author Brian Wing Shun Chan
 * @see FragmentCollectionService
 * @generated
 */
public class FragmentCollectionServiceWrapper
	implements FragmentCollectionService,
			   ServiceWrapper<FragmentCollectionService> {

	public FragmentCollectionServiceWrapper() {
		this(null);
	}

	public FragmentCollectionServiceWrapper(
		FragmentCollectionService fragmentCollectionService) {

		_fragmentCollectionService = fragmentCollectionService;
	}

	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId, String name,
			String description,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.addFragmentCollection(
			externalReferenceCode, groupId, name, description, serviceContext);
	}

	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.addFragmentCollection(
			externalReferenceCode, groupId, fragmentCollectionKey, name,
			description, marketplace, serviceContext);
	}

	@Override
	public FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.deleteFragmentCollection(
			fragmentCollectionId);
	}

	@Override
	public FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.deleteFragmentCollection(
			externalReferenceCode, groupId);
	}

	@Override
	public void deleteFragmentCollections(long[] fragmentCollectionIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_fragmentCollectionService.deleteFragmentCollections(
			fragmentCollectionIds);
	}

	@Override
	public FragmentCollection fetchFragmentCollection(long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.fetchFragmentCollection(
			fragmentCollectionId);
	}

	@Override
	public java.util.List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds) {

		return _fragmentCollectionService.getExportableFragmentCollections(
			fragmentCollectionIds);
	}

	@Override
	public java.util.List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
				orderByComparator) {

		return _fragmentCollectionService.
			getExportableFragmentCollectionsByGroupId(
				groupIds, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection>
		getExportableFragmentCollectionsByGroupId(
			long[] groupIds, String name, int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
				orderByComparator) {

		return _fragmentCollectionService.
			getExportableFragmentCollectionsByGroupId(
				groupIds, name, start, end, orderByComparator);
	}

	@Override
	public int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds) {

		return _fragmentCollectionService.getExportableFragmentCollectionsCount(
			fragmentCollectionIds);
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(long[] groupIds) {
		return _fragmentCollectionService.
			getExportableFragmentCollectionsCountByGroupId(groupIds);
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name) {

		return _fragmentCollectionService.
			getExportableFragmentCollectionsCountByGroupId(groupIds, name);
	}

	@Override
	public FragmentCollection getFragmentCollection(long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.getFragmentCollection(
			fragmentCollectionId);
	}

	@Override
	public FragmentCollection getFragmentCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.
			getFragmentCollectionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public java.util.List<com.liferay.portal.kernel.repository.model.FileEntry>
			getFragmentCollectionFileEntries(long fragmentCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.getFragmentCollectionFileEntries(
			fragmentCollectionId);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId) {

		return _fragmentCollectionService.getFragmentCollections(groupId);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem) {

		return _fragmentCollectionService.getFragmentCollections(
			groupId, includeSystem);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupId, includeSystem, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end) {

		return _fragmentCollectionService.getFragmentCollections(
			groupId, start, end);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupId, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupId, name, includeSystem, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupId, name, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds) {

		return _fragmentCollectionService.getFragmentCollections(groupIds);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupIds, marketplace, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupIds, name, marketplace, start, end, orderByComparator);
	}

	@Override
	public java.util.List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator<FragmentCollection>
			orderByComparator) {

		return _fragmentCollectionService.getFragmentCollections(
			groupIds, name, start, end, orderByComparator);
	}

	@Override
	public int getFragmentCollectionsCount(long groupId) {
		return _fragmentCollectionService.getFragmentCollectionsCount(groupId);
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, boolean includeSystem) {

		return _fragmentCollectionService.getFragmentCollectionsCount(
			groupId, includeSystem);
	}

	@Override
	public int getFragmentCollectionsCount(long groupId, String name) {
		return _fragmentCollectionService.getFragmentCollectionsCount(
			groupId, name);
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem) {

		return _fragmentCollectionService.getFragmentCollectionsCount(
			groupId, name, includeSystem);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds) {
		return _fragmentCollectionService.getFragmentCollectionsCount(groupIds);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace) {

		return _fragmentCollectionService.getFragmentCollectionsCount(
			groupIds, marketplace);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds, String name) {
		return _fragmentCollectionService.getFragmentCollectionsCount(
			groupIds, name);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace) {

		return _fragmentCollectionService.getFragmentCollectionsCount(
			groupIds, name, marketplace);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _fragmentCollectionService.getOSGiServiceIdentifier();
	}

	@Override
	public String[] getTempFileNames(long groupId, String folderName)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.getTempFileNames(groupId, folderName);
	}

	@Override
	public FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _fragmentCollectionService.updateFragmentCollection(
			fragmentCollectionId, name, description);
	}

	@Override
	public FragmentCollectionService getWrappedService() {
		return _fragmentCollectionService;
	}

	@Override
	public void setWrappedService(
		FragmentCollectionService fragmentCollectionService) {

		_fragmentCollectionService = fragmentCollectionService;
	}

	private FragmentCollectionService _fragmentCollectionService;

}
// LIFERAY-SERVICE-BUILDER-HASH:-424141358