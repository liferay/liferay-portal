/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.service.impl;

import com.liferay.fragment.constants.FragmentActionKeys;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.fragment.service.base.FragmentCollectionServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileEntry;
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
		"json.web.service.context.name=fragment",
		"json.web.service.context.path=FragmentCollection"
	},
	service = AopService.class
)
public class FragmentCollectionServiceImpl
	extends FragmentCollectionServiceBaseImpl {

	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId, String name,
			String description, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentCollectionLocalService.addFragmentCollection(
			externalReferenceCode, getUserId(), groupId, name, description,
			serviceContext);
	}

	@Override
	public FragmentCollection addFragmentCollection(
			String externalReferenceCode, long groupId,
			String fragmentCollectionKey, String name, String description,
			boolean marketplace, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentCollectionLocalService.addFragmentCollection(
			externalReferenceCode, getUserId(), groupId, fragmentCollectionKey,
			name, description, marketplace, serviceContext);
	}

	@Override
	public FragmentCollection deleteFragmentCollection(
			long fragmentCollectionId)
		throws PortalException {

		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.findByPrimaryKey(
				fragmentCollectionId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentCollection.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentCollectionLocalService.deleteFragmentCollection(
			fragmentCollection);
	}

	@Override
	public FragmentCollection deleteFragmentCollection(
			String externalReferenceCode, long groupId)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		FragmentCollection fragmentCollection =
			fragmentCollectionLocalService.
				getFragmentCollectionByExternalReferenceCode(
					externalReferenceCode, groupId);

		return fragmentCollectionLocalService.deleteFragmentCollection(
			fragmentCollection);
	}

	@Override
	public void deleteFragmentCollections(long[] fragmentCollectionIds)
		throws PortalException {

		for (long fragmentCollectionId : fragmentCollectionIds) {
			FragmentCollection fragmentCollection =
				fragmentCollectionPersistence.findByPrimaryKey(
					fragmentCollectionId);

			_portletResourcePermission.check(
				getPermissionChecker(), fragmentCollection.getGroupId(),
				FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

			fragmentCollectionLocalService.deleteFragmentCollection(
				fragmentCollection);
		}
	}

	@Override
	public FragmentCollection fetchFragmentCollection(long fragmentCollectionId)
		throws PortalException {

		return fragmentCollectionLocalService.fetchFragmentCollection(
			fragmentCollectionId);
	}

	@Override
	public List<FragmentCollection> getExportableFragmentCollections(
		long[] fragmentCollectionIds) {

		return fragmentCollectionLocalService.getExportableFragmentCollections(
			fragmentCollectionIds);
	}

	@Override
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.
			getExportableFragmentCollectionsByGroupId(
				groupIds, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getExportableFragmentCollectionsByGroupId(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.
			getExportableFragmentCollectionsByGroupId(
				groupIds, name, start, end, orderByComparator);
	}

	@Override
	public int getExportableFragmentCollectionsCount(
		long[] fragmentCollectionIds) {

		return fragmentCollectionLocalService.
			getExportableFragmentCollectionsCount(fragmentCollectionIds);
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(long[] groupIds) {
		return fragmentCollectionLocalService.
			getExportableFragmentCollectionsCountByGroupId(groupIds);
	}

	@Override
	public int getExportableFragmentCollectionsCountByGroupId(
		long[] groupIds, String name) {

		return fragmentCollectionLocalService.
			getExportableFragmentCollectionsCountByGroupId(groupIds, name);
	}

	@Override
	public FragmentCollection getFragmentCollection(long fragmentCollectionId)
		throws PortalException {

		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.findByPrimaryKey(
				fragmentCollectionId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentCollection.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentCollection;
	}

	@Override
	public FragmentCollection getFragmentCollectionByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return fragmentCollectionLocalService.
			getFragmentCollectionByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public List<FileEntry> getFragmentCollectionFileEntries(
			long fragmentCollectionId)
		throws PortalException {

		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.findByPrimaryKey(
				fragmentCollectionId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentCollection.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentCollection.getResources();
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(long groupId) {
		return fragmentCollectionLocalService.getFragmentCollections(groupId);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupId, includeSystem);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupId, includeSystem, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupId, start, end);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getFragmentCollections(
			groupId, false, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, boolean includeSystem, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupId, name, includeSystem, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long groupId, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return getFragmentCollections(
			groupId, name, false, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(long[] groupIds) {
		return fragmentCollectionLocalService.getFragmentCollections(groupIds);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupIds, marketplace, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupIds, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, boolean marketplace, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupIds, name, marketplace, start, end, orderByComparator);
	}

	@Override
	public List<FragmentCollection> getFragmentCollections(
		long[] groupIds, String name, int start, int end,
		OrderByComparator<FragmentCollection> orderByComparator) {

		return fragmentCollectionLocalService.getFragmentCollections(
			groupIds, name, start, end, orderByComparator);
	}

	@Override
	public int getFragmentCollectionsCount(long groupId) {
		return getFragmentCollectionsCount(groupId, false);
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, boolean includeSystem) {

		return fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupId, includeSystem);
	}

	@Override
	public int getFragmentCollectionsCount(long groupId, String name) {
		return getFragmentCollectionsCount(groupId, name, false);
	}

	@Override
	public int getFragmentCollectionsCount(
		long groupId, String name, boolean includeSystem) {

		return fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupId, name, includeSystem);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds) {
		return fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, boolean marketplace) {

		return fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds, marketplace);
	}

	@Override
	public int getFragmentCollectionsCount(long[] groupIds, String name) {
		return fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds, name);
	}

	@Override
	public int getFragmentCollectionsCount(
		long[] groupIds, String name, boolean marketplace) {

		return fragmentCollectionLocalService.getFragmentCollectionsCount(
			groupIds, name, marketplace);
	}

	@Override
	public String[] getTempFileNames(long groupId, String folderName)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), groupId,
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return _fragmentEntryLocalService.getTempFileNames(
			getUserId(), groupId, folderName);
	}

	@Override
	public FragmentCollection updateFragmentCollection(
			long fragmentCollectionId, String name, String description)
		throws PortalException {

		FragmentCollection fragmentCollection =
			fragmentCollectionPersistence.findByPrimaryKey(
				fragmentCollectionId);

		_portletResourcePermission.check(
			getPermissionChecker(), fragmentCollection.getGroupId(),
			FragmentActionKeys.MANAGE_FRAGMENT_ENTRIES);

		return fragmentCollectionLocalService.updateFragmentCollection(
			fragmentCollectionId, name, description);
	}

	@Reference
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Reference(
		target = "(resource.name=" + FragmentConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}