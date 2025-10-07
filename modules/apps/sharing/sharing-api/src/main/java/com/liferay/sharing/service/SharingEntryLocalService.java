/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service;

import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.Projection;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.search.Indexable;
import com.liferay.portal.kernel.search.IndexableType;
import com.liferay.portal.kernel.service.BaseLocalService;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.systemevent.SystemEvent;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.security.permission.SharingEntryAction;

import java.io.Serializable;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.osgi.annotation.versioning.ProviderType;

/**
 * Provides the local service interface for SharingEntry. Methods of this
 * service will not have security checks based on the propagated JAAS
 * credentials because this service can only be accessed from within the same
 * VM.
 *
 * @author Brian Wing Shun Chan
 * @see SharingEntryLocalServiceUtil
 * @generated
 */
@ProviderType
@Transactional(
	isolation = Isolation.PORTAL,
	rollbackFor = {PortalException.class, SystemException.class}
)
public interface SharingEntryLocalService
	extends BaseLocalService, PersistedModelLocalService {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this interface directly. Add custom service methods to <code>com.liferay.sharing.service.impl.SharingEntryLocalServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface. Consume the sharing entry local service via injection or a <code>org.osgi.util.tracker.ServiceTracker</code>. Use {@link SharingEntryLocalServiceUtil} if injection and service tracking are not available.
	 */

	/**
	 * Adds a new sharing entry in the database or updates an existing one.
	 *
	 * @param userId the ID of the user sharing the resource
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @param groupId the primary key of the resource's group
	 * @param shareable whether the user specified by {@code toUserId} can
	 share the resource
	 * @param sharingEntryActions the sharing entry actions
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry actions are invalid (e.g.,
	 empty, don't contain {@code SharingEntryAction#VIEW}, or contain
	 a {@code null} value), if the to/from user IDs are the same, or
	 if the expiration date is a past value
	 * @review
	 */
	public SharingEntry addOrUpdateSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			Collection<SharingEntryAction> sharingEntryActions,
			Date expirationDate, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Adds the sharing entry to the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntry the sharing entry
	 * @return the sharing entry that was added
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SharingEntry addSharingEntry(SharingEntry sharingEntry);

	/**
	 * Adds a new sharing entry in the database.
	 *
	 * @param userId the ID of the user sharing the resource
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @param groupId the primary key of the resource's group
	 * @param shareable whether the user specified by {@code toUserId} can
	 share the resource
	 * @param sharingEntryActions the sharing entry actions
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if a sharing entry already exists for the to/from
	 user IDs, if the sharing entry actions are invalid (e.g., empty,
	 don't contain {@code SharingEntryAction#VIEW}, or contain a
	 {@code null} value), if the to/from user IDs are the same, or if
	 the expiration date is a past value
	 * @review
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SharingEntry addSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			Collection<SharingEntryAction> sharingEntryActions,
			Date expirationDate, ServiceContext serviceContext)
		throws PortalException;

	/**
	 * @throws PortalException
	 */
	public PersistedModel createPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Creates a new sharing entry with the primary key. Does not add the sharing entry to the database.
	 *
	 * @param sharingEntryId the primary key for the new sharing entry
	 * @return the new sharing entry
	 */
	@Transactional(enabled = false)
	public SharingEntry createSharingEntry(long sharingEntryId);

	public void deleteCompanySharingEntries(long companyId, long classNameId);

	/**
	 * Deletes the sharing entries whose expiration date is before the current
	 * date.
	 */
	public void deleteExpiredEntries();

	/**
	 * Deletes the group's sharing entries.
	 *
	 * @param groupId the group's ID
	 */
	public void deleteGroupSharingEntries(long groupId);

	/**
	 * @throws PortalException
	 */
	@Override
	public PersistedModel deletePersistedModel(PersistedModel persistedModel)
		throws PortalException;

	/**
	 * Deletes the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 */
	public void deleteSharingEntries(long classNameId, long classPK);

	/**
	 * Deletes the sharing entry with the primary key from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry that was removed
	 * @throws PortalException if a sharing entry with the primary key could not be found
	 */
	@Indexable(type = IndexableType.DELETE)
	public SharingEntry deleteSharingEntry(long sharingEntryId)
		throws PortalException;

	/**
	 * Deletes the sharing entry for the resource and users. The class name ID
	 * and class primary key identify the resource's type and instance,
	 * respectively.
	 *
	 * @param toUserId the ID of the user the resource is shared with
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the deleted sharing entry
	 */
	public SharingEntry deleteSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws PortalException;

	/**
	 * Deletes the sharing entry from the database. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntry the sharing entry
	 * @return the sharing entry that was removed
	 */
	@Indexable(type = IndexableType.DELETE)
	@SystemEvent(type = SystemEventConstants.TYPE_DELETE)
	public SharingEntry deleteSharingEntry(SharingEntry sharingEntry);

	public SharingEntry deleteSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Deletes the sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 */
	public void deleteToUserSharingEntries(long toUserId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> T dslQuery(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int dslQueryCount(DSLQuery dslQuery);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public DynamicQuery dynamicQuery();

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(DynamicQuery dynamicQuery);

	/**
	 * Performs a dynamic query on the database and returns a range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharing.model.impl.SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @return the range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end);

	/**
	 * Performs a dynamic query on the database and returns an ordered range of the matching rows.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharing.model.impl.SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param dynamicQuery the dynamic query
	 * @param start the lower bound of the range of model instances
	 * @param end the upper bound of the range of model instances (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the ordered range of matching rows
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(DynamicQuery dynamicQuery);

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public long dynamicQueryCount(
		DynamicQuery dynamicQuery, Projection projection);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry fetchSharingEntry(long sharingEntryId);

	/**
	 * Returns the sharing entry for the resource shared with the user or
	 * <code>null</code> if there's none. The class name ID and class primary
	 * key identify the resource's type and instance, respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entry or <code>null</code> if none
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry fetchSharingEntry(
		long toUserId, long classNameId, long classPK);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry fetchSharingEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId);

	/**
	 * Returns the sharing entry matching the UUID and group.
	 *
	 * @param uuid the sharing entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry fetchSharingEntryByUuidAndGroupId(
		String uuid, long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ActionableDynamicQuery getActionableDynamicQuery();

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getCompanySharingEntriesCount(long companyId, long classNameId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public ExportActionableDynamicQuery getExportActionableDynamicQuery(
		PortletDataContext portletDataContext);

	/**
	 * Returns the ordered range of sharing entries for the type of resource
	 * shared by the user. The class name ID identifies the resource type.
	 *
	 * @param fromUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @param start the ordered range's lower bound
	 * @param end the ordered range's upper bound (not inclusive)
	 * @param orderByComparator the comparator that orders the sharing entries
	 * @return the ordered range of sharing entries
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getFromUserSharingEntries(
		long fromUserId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator);

	/**
	 * Returns the number of sharing entries for the type of resource shared by
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param fromUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getFromUserSharingEntriesCount(
		long fromUserId, long classNameId);

	/**
	 * Returns the the group's sharing entries.
	 *
	 * @param groupId the primary key of the group
	 * @return the sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getGroupSharingEntries(long groupId);

	/**
	 * Returns the the group's sharing entries count.
	 *
	 * @param groupId the primary key of the group
	 * @return the sharing entries count
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getGroupSharingEntriesCount(long groupId);

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public IndexableActionableDynamicQuery getIndexableActionableDynamicQuery();

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public String getOSGiServiceIdentifier();

	/**
	 * @throws PortalException
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException;

	/**
	 * Returns a range of all the sharing entries.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end - start</code> instances. <code>start</code> and <code>end</code> are not primary keys, they are indexes in the result set. Thus, <code>0</code> refers to the first result in the set. Setting both <code>start</code> and <code>end</code> to <code>com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS</code> will return the full result set. If <code>orderByComparator</code> is specified, then the query will include the given ORDER BY logic. If <code>orderByComparator</code> is absent, then the query will include the default ORDER BY logic from <code>com.liferay.sharing.model.impl.SharingEntryModelImpl</code>.
	 * </p>
	 *
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @return the range of sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getSharingEntries(int start, int end);

	/**
	 * Returns the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getSharingEntries(long classNameId, long classPK);

	/**
	 * Returns the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @param start the range's lower bound
	 * @param end the range's upper bound (not inclusive)
	 * @return the sharing entries
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getSharingEntries(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator);

	/**
	 * Returns all the sharing entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the sharing entries
	 * @param companyId the primary key of the company
	 * @return the matching sharing entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getSharingEntriesByUuidAndCompanyId(
		String uuid, long companyId);

	/**
	 * Returns a range of sharing entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the sharing entries
	 * @param companyId the primary key of the company
	 * @param start the lower bound of the range of sharing entries
	 * @param end the upper bound of the range of sharing entries (not inclusive)
	 * @param orderByComparator the comparator to order the results by (optionally <code>null</code>)
	 * @return the range of matching sharing entries, or an empty list if no matches were found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getSharingEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator);

	/**
	 * Returns the number of sharing entries.
	 *
	 * @return the number of sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSharingEntriesCount();

	/**
	 * Returns the resource's sharing entries count. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entries count
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getSharingEntriesCount(long classNameId, long classPK);

	/**
	 * Returns the sharing entry with the primary key.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry
	 * @throws PortalException if a sharing entry with the primary key could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry getSharingEntry(long sharingEntryId)
		throws PortalException;

	/**
	 * Returns the sharing entry for the resource shared with the user. The
	 * class name ID and class primary key identify the resource's type and
	 * instance, respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entry
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry getSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws PortalException;

	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry getSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException;

	/**
	 * Returns the sharing entry matching the UUID and group.
	 *
	 * @param uuid the sharing entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching sharing entry
	 * @throws PortalException if a matching sharing entry could not be found
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public SharingEntry getSharingEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException;

	/**
	 * Returns the list of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @return the list of sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getToUserSharingEntries(long toUserId);

	/**
	 * Returns the range of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @param start the range's lower bound
	 * @param end the range's upper bound (not inclusive)
	 * @return the range of sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getToUserSharingEntries(
		long toUserId, int start, int end);

	/**
	 * Returns the list of sharing entries for the type of resource shared with
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the list of sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getToUserSharingEntries(
		long toUserId, long classNameId);

	/**
	 * Returns the ordered range of sharing entries for the type of resource
	 * shared with the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @param start the ordered range's lower bound
	 * @param end the ordered range's upper bound (not inclusive)
	 * @param orderByComparator the comparator that orders the sharing entries
	 * @return the ordered range of sharing entries
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public List<SharingEntry> getToUserSharingEntries(
		long toUserId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator);

	/**
	 * Returns the number of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @return the number of sharing entries
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getToUserSharingEntriesCount(long toUserId);

	/**
	 * Returns the number of sharing entries for the type of resource shared
	 * with the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public int getToUserSharingEntriesCount(long toUserId, long classNameId);

	/**
	 * Returns {@code true} if the resource with the sharing entry action has
	 * been shared with a user who can also share that resource. The class name
	 * ID and class primary key identify the resource's type and instance,
	 * respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the shared resource
	 * @param sharingEntryAction the sharing entry action
	 * @return {@code true} if the resource with the sharing entry action has
	 been shared with a user who can also share that resource; {@code
	 false} otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasShareableSharingPermission(
		long toUserId, long classNameId, long classPK,
		SharingEntryAction sharingEntryAction);

	/**
	 * Returns {@code true} if the resource with the sharing entry action has
	 * been shared with the user. The class name ID and class primary key
	 * identify the resource's type and instance, respectively.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the shared resource
	 * @param sharingEntryAction the sharing entry action
	 * @return {@code true} if the resource with the sharing entry action has
	 been shared with the user; {@code false} otherwise
	 */
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public boolean hasSharingPermission(
		long toUserId, long classNameId, long classPK,
		SharingEntryAction sharingEntryAction);

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @param sharingEntryActions the sharing entry actions
	 * @param shareable whether the user the resource is shared with can
	 also share it
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry does not exist, if the
	 sharing entry actions are invalid (e.g., empty, don't contain
	 {@code SharingEntryAction#VIEW}, or contain a {@code null}
	 value), or if the expiration date is a past value
	 * @deprecated As of Mueller (7.2.x), replaced by {@link
	 SharingEntryLocalService#updateSharingEntry(
	 long, long, Collection, boolean, Date, ServiceContext)}
	 * @review
	 */
	@Deprecated
	public SharingEntry updateSharingEntry(
			long sharingEntryId,
			Collection<SharingEntryAction> sharingEntryActions,
			boolean shareable, Date expirationDate,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the sharing entry in the database.
	 *
	 * @param userId the primary key of the user updating the sharing entry
	 * @param sharingEntryId the primary key of the sharing entry
	 * @param sharingEntryActions the sharing entry actions
	 * @param shareable whether the user the resource is shared with can also
	 share it
	 * @param expirationDate the date when the sharing entry expires
	 * @param serviceContext the service context
	 * @return the sharing entry
	 * @throws PortalException if the sharing entry does not exist, if the
	 sharing entry actions are invalid (e.g., empty, don't contain
	 {@code SharingEntryAction#VIEW}, or contain a {@code null}
	 value), or if the expiration date is a past value
	 * @review
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SharingEntry updateSharingEntry(
			long userId, long sharingEntryId,
			Collection<SharingEntryAction> sharingEntryActions,
			boolean shareable, Date expirationDate,
			ServiceContext serviceContext)
		throws PortalException;

	/**
	 * Updates the sharing entry in the database or adds it if it does not yet exist. Also notifies the appropriate model listeners.
	 *
	 * <p>
	 * <strong>Important:</strong> Inspect SharingEntryLocalServiceImpl for overloaded versions of the method. If provided, use these entry points to the API, as the implementation logic may require the additional parameters defined there.
	 * </p>
	 *
	 * @param sharingEntry the sharing entry
	 * @return the sharing entry that was updated
	 */
	@Indexable(type = IndexableType.REINDEX)
	public SharingEntry updateSharingEntry(SharingEntry sharingEntry);

}