/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link LayoutPageTemplateEntryService}.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntryService
 * @generated
 */
public class LayoutPageTemplateEntryServiceWrapper
	implements LayoutPageTemplateEntryService,
			   ServiceWrapper<LayoutPageTemplateEntryService> {

	public LayoutPageTemplateEntryServiceWrapper() {
		this(null);
	}

	public LayoutPageTemplateEntryServiceWrapper(
		LayoutPageTemplateEntryService layoutPageTemplateEntryService) {

		_layoutPageTemplateEntryService = layoutPageTemplateEntryService;
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String classTypeKey, String name, int type,
			long previewFileEntryId, boolean defaultTemplate,
			long layoutPrototypeId, long plid, long masterLayoutPlid,
			int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
			externalReferenceCode, groupId, layoutPageTemplateCollectionId,
			layoutPageTemplateEntryKey, classNameId, classTypeId, classTypeKey,
			name, type, previewFileEntryId, defaultTemplate, layoutPrototypeId,
			plid, masterLayoutPlid, status, serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String classTypeKey, String name,
			long masterLayoutPlid, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
			externalReferenceCode, groupId, layoutPageTemplateCollectionId,
			layoutPageTemplateEntryKey, classNameId, classTypeId, classTypeKey,
			name, masterLayoutPlid, status, serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, String name, int type,
			long masterLayoutPlid, int status,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.addLayoutPageTemplateEntry(
			externalReferenceCode, groupId, layoutPageTemplateCollectionId,
			layoutPageTemplateEntryKey, name, type, masterLayoutPlid, status,
			serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry copyLayoutPageTemplateEntry(
			long groupId, long layoutPageTemplateCollectionId,
			long sourceLayoutPageTemplateEntryId, boolean copyPermissions,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return _layoutPageTemplateEntryService.copyLayoutPageTemplateEntry(
			groupId, layoutPageTemplateCollectionId,
			sourceLayoutPageTemplateEntryId, copyPermissions, serviceContext);
	}

	@Override
	public LayoutPageTemplateEntry createLayoutPageTemplateEntryFromLayout(
			long segmentsExperienceId,
			com.liferay.portal.kernel.model.Layout sourceLayout, String name,
			long targetLayoutPageTemplateCollectionId,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws Exception {

		return _layoutPageTemplateEntryService.
			createLayoutPageTemplateEntryFromLayout(
				segmentsExperienceId, sourceLayout, name,
				targetLayoutPageTemplateCollectionId, serviceContext);
	}

	@Override
	public void deleteLayoutPageTemplateEntries(
			long[] layoutPageTemplateEntryIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		_layoutPageTemplateEntryService.deleteLayoutPageTemplateEntries(
			layoutPageTemplateEntryIds);
	}

	@Override
	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
	}

	@Override
	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.deleteLayoutPageTemplateEntry(
			externalReferenceCode, groupId);
	}

	@Override
	public LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, int type, int status) {

		return _layoutPageTemplateEntryService.
			fetchDefaultLayoutPageTemplateEntry(groupId, type, status);
	}

	@Override
	public LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, long classNameId, long classTypeId) {

		return _layoutPageTemplateEntryService.
			fetchDefaultLayoutPageTemplateEntry(
				groupId, classNameId, classTypeId);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.fetchLayoutPageTemplateEntry(
			groupId, layoutPageTemplateCollectionId, name, type);
	}

	@Override
	public LayoutPageTemplateEntry
			fetchLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.
			fetchLayoutPageTemplateEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntryByPlid(long plid)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.
			fetchLayoutPageTemplateEntryByPlid(plid);
	}

	@Override
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return _layoutPageTemplateEntryService.
			fetchLayoutPageTemplateEntryByUuidAndGroupId(uuid, groupId);
	}

	@Override
	public java.util.List<Object>
		getLayoutPageCollectionsAndLayoutPageTemplateEntries(
			long groupId, long layoutPageTemplateCollectionId, int type,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator<Object>
				orderByComparator) {

		return _layoutPageTemplateEntryService.
			getLayoutPageCollectionsAndLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, type, start, end,
				orderByComparator);
	}

	@Override
	public java.util.List<Object>
		getLayoutPageCollectionsAndLayoutPageTemplateEntries(
			long groupId, long layoutPageTemplateCollectionId, long classNameId,
			long classTypeId, String name, int type, int status, int start,
			int end,
			com.liferay.portal.kernel.util.OrderByComparator<Object>
				orderByComparator) {

		return _layoutPageTemplateEntryService.
			getLayoutPageCollectionsAndLayoutPageTemplateEntries(
				groupId, layoutPageTemplateCollectionId, classNameId,
				classTypeId, name, type, status, start, end, orderByComparator);
	}

	@Override
	public int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return _layoutPageTemplateEntryService.
			getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
				groupId, layoutPageTemplateCollectionId, type);
	}

	@Override
	public int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, long classNameId,
		long classTypeId, String name, int type, int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
				groupId, layoutPageTemplateCollectionId, classNameId,
				classTypeId, name, type, status);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int status) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, status);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, type, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, types, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, types, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, status);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, int type, boolean defaultTemplate) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, classNameId, type, defaultTemplate);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, status, start, end);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, type);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type,
		int status) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, type, status);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type, int status,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, type, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type, int start,
		int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, type, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, name, type, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, classNameId, classTypeId, name, type, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, name, status, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, layoutPageTemplateCollectionId, name, start, end,
			orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int type, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, name, type, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int type, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, name, type, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int[] types, int status, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, name, types, status, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int[] types, int start, int end,
		com.liferay.portal.kernel.util.OrderByComparator
			<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntries(
			groupId, name, types, start, end, orderByComparator);
	}

	@Override
	public java.util.List<LayoutPageTemplateEntry>
		getLayoutPageTemplateEntriesByType(
			long groupId, long layoutPageTemplateCollectionId, int type,
			int start, int end,
			com.liferay.portal.kernel.util.OrderByComparator
				<LayoutPageTemplateEntry> orderByComparator) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesByType(
				groupId, layoutPageTemplateCollectionId, type, start, end,
				orderByComparator);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(long groupId, int type) {
		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, type);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, int type, int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, type, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(long groupId, int[] types) {
		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, types);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, int[] types, int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, types, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, layoutPageTemplateCollectionId);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, layoutPageTemplateCollectionId, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, int type) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, classNameId, classTypeId, type);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, int type,
		int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, classNameId, classTypeId, type, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, String name,
		int type) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, classNameId, classTypeId, name, type);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, classNameId, classTypeId, name, type, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, String name) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, layoutPageTemplateCollectionId, name);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(
				groupId, layoutPageTemplateCollectionId, name, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int type) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, name, type);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int type, int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, name, type, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int[] types) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, name, types);
	}

	@Override
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int[] types, int status) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCount(groupId, name, types, status);
	}

	@Override
	public int getLayoutPageTemplateEntriesCountByType(
		long groupId, long layoutPageTemplateCollectionId, int type) {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntriesCountByType(
				groupId, layoutPageTemplateCollectionId, type);
	}

	@Override
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
			layoutPageTemplateEntryId);
	}

	@Override
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long groupId, String layoutPageTemplateEntryKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.getLayoutPageTemplateEntry(
			groupId, layoutPageTemplateEntryKey);
	}

	@Override
	public LayoutPageTemplateEntry
			getLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.
			getLayoutPageTemplateEntryByExternalReferenceCode(
				externalReferenceCode, groupId);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _layoutPageTemplateEntryService.getOSGiServiceIdentifier();
	}

	@Override
	public LayoutPageTemplateEntry moveLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId,
			long targetLayoutPageTemplateCollectionId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.moveLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, targetLayoutPageTemplateCollectionId);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, boolean defaultTemplate)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, defaultTemplate);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long previewFileEntryId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, previewFileEntryId);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long classNameId, long classTypeId,
			String classTypeKey)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, classNameId, classTypeId, classTypeKey);
	}

	@Override
	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.updateLayoutPageTemplateEntry(
			layoutPageTemplateEntryId, name);
	}

	@Override
	public LayoutPageTemplateEntry updateStatus(
			long layoutPageTemplateEntryId, int status)
		throws com.liferay.portal.kernel.exception.PortalException {

		return _layoutPageTemplateEntryService.updateStatus(
			layoutPageTemplateEntryId, status);
	}

	@Override
	public LayoutPageTemplateEntryService getWrappedService() {
		return _layoutPageTemplateEntryService;
	}

	@Override
	public void setWrappedService(
		LayoutPageTemplateEntryService layoutPageTemplateEntryService) {

		_layoutPageTemplateEntryService = layoutPageTemplateEntryService;
	}

	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

}