/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service;

import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.portal.kernel.cache.thread.local.ThreadLocalCachable;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.jsonwebservice.JSONWebService;
import com.liferay.portal.kernel.model.Layout;
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
 * Provides the remote service interface for LayoutPageTemplateEntry. Methods of this
 * service are expected to have security checks based on the propagated JAAS
 * credentials because this service can be accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see LayoutPageTemplateEntryServiceUtil
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
public interface LayoutPageTemplateEntryService extends BaseService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.layout.page.template.service.impl.LayoutPageTemplateEntryServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the layout page template entry remote service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link LayoutPageTemplateEntryServiceUtil} if injection and service tracking are not available.
	 */
	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, int type, long previewFileEntryId,
			boolean defaultTemplate, long layoutPrototypeId, long plid,
			long masterLayoutPlid, int status, ServiceContext serviceContext)
		throws PortalException;

	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, long classNameId,
			long classTypeId, String name, long masterLayoutPlid, int status,
			ServiceContext serviceContext)
		throws PortalException;

	public LayoutPageTemplateEntry addLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId,
			long layoutPageTemplateCollectionId,
			String layoutPageTemplateEntryKey, String name, int type,
			long masterLayoutPlid, int status, ServiceContext serviceContext)
		throws PortalException;

	public LayoutPageTemplateEntry copyLayoutPageTemplateEntry(
			long groupId, long layoutPageTemplateCollectionId,
			long sourceLayoutPageTemplateEntryId, boolean copyPermissions,
			ServiceContext serviceContext)
		throws Exception;

	public LayoutPageTemplateEntry createLayoutPageTemplateEntryFromLayout(
			long segmentsExperienceId, Layout sourceLayout, String name,
			long targetLayoutPageTemplateCollectionId,
			ServiceContext serviceContext)
		throws Exception;

	public void deleteLayoutPageTemplateEntries(
			long[] layoutPageTemplateEntryIds)
		throws PortalException;

	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException;

	public LayoutPageTemplateEntry deleteLayoutPageTemplateEntry(
			String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, int type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry fetchDefaultLayoutPageTemplateEntry(
		long groupId, long classNameId, long classTypeId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntry(
			long groupId, long layoutPageTemplateCollectionId, String name,
			int type)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry
			fetchLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry fetchLayoutPageTemplateEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getLayoutPageCollectionsAndLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<Object> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<Object> getLayoutPageCollectionsAndLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, long classNameId,
		long classTypeId, String name, int type, int status, int start, int end,
		OrderByComparator<Object> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageCollectionsAndLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, long classNameId,
		long classTypeId, String name, int type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, int type, boolean defaultTemplate);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type);

	@ThreadLocalCachable
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type, int status,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int type, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int type, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int[] types, int status, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntries(
		long groupId, String name, int[] types, int start, int end,
		OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<LayoutPageTemplateEntry> getLayoutPageTemplateEntriesByType(
		long groupId, long layoutPageTemplateCollectionId, int type, int start,
		int end, OrderByComparator<LayoutPageTemplateEntry> orderByComparator);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(long groupId, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, int type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(long groupId, int[] types);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, int[] types, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, int type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, String name,
		int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long classNameId, long classTypeId, String name, int type,
		int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, String name);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, long layoutPageTemplateCollectionId, String name,
		int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int type, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int[] types);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCount(
		long groupId, String name, int[] types, int status);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getLayoutPageTemplateEntriesCountByType(
		long groupId, long layoutPageTemplateCollectionId, int type);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry getLayoutPageTemplateEntry(
			long groupId, String layoutPageTemplateEntryKey)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public LayoutPageTemplateEntry
			getLayoutPageTemplateEntryByExternalReferenceCode(
				String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	public LayoutPageTemplateEntry moveLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId,
			long targetLayoutPageTemplateCollectionId)
		throws PortalException;

	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, boolean defaultTemplate)
		throws PortalException;

	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long previewFileEntryId)
		throws PortalException;

	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, long classNameId, long classTypeId)
		throws PortalException;

	public LayoutPageTemplateEntry updateLayoutPageTemplateEntry(
			long layoutPageTemplateEntryId, String name)
		throws PortalException;

	public LayoutPageTemplateEntry updateStatus(
			long layoutPageTemplateEntryId, int status)
		throws PortalException;

}