/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.service;

import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.sharing.model.SharingEntry;

import java.io.Serializable;

import java.util.List;

/**
 * Provides the local service utility for SharingEntry. This utility wraps
 * <code>com.liferay.sharing.service.impl.SharingEntryLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see SharingEntryLocalService
 * @generated
 */
public class SharingEntryLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.sharing.service.impl.SharingEntryLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
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
	public static SharingEntry addOrUpdateSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addOrUpdateSharingEntry(
			externalReferenceCode, userId, toUserGroupId, toUserId, classNameId,
			classPK, groupId, shareable, sharingEntryActions, expirationDate,
			serviceContext);
	}

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
	public static SharingEntry addSharingEntry(SharingEntry sharingEntry) {
		return getService().addSharingEntry(sharingEntry);
	}

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
	public static SharingEntry addSharingEntry(
			String externalReferenceCode, long userId, long toUserGroupId,
			long toUserId, long classNameId, long classPK, long groupId,
			boolean shareable,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().addSharingEntry(
			externalReferenceCode, userId, toUserGroupId, toUserId, classNameId,
			classPK, groupId, shareable, sharingEntryActions, expirationDate,
			serviceContext);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel createPersistedModel(
			Serializable primaryKeyObj)
		throws PortalException {

		return getService().createPersistedModel(primaryKeyObj);
	}

	/**
	 * Creates a new sharing entry with the primary key. Does not add the sharing entry to the database.
	 *
	 * @param sharingEntryId the primary key for the new sharing entry
	 * @return the new sharing entry
	 */
	public static SharingEntry createSharingEntry(long sharingEntryId) {
		return getService().createSharingEntry(sharingEntryId);
	}

	public static void deleteCompanySharingEntries(
		long companyId, long classNameId) {

		getService().deleteCompanySharingEntries(companyId, classNameId);
	}

	/**
	 * Deletes the sharing entries whose expiration date is before the current
	 * date.
	 */
	public static void deleteExpiredEntries() {
		getService().deleteExpiredEntries();
	}

	/**
	 * Deletes the group's sharing entries.
	 *
	 * @param groupId the group's ID
	 */
	public static void deleteGroupSharingEntries(long groupId) {
		getService().deleteGroupSharingEntries(groupId);
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel deletePersistedModel(
			PersistedModel persistedModel)
		throws PortalException {

		return getService().deletePersistedModel(persistedModel);
	}

	/**
	 * Deletes the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 */
	public static void deleteSharingEntries(long classNameId, long classPK) {
		getService().deleteSharingEntries(classNameId, classPK);
	}

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
	public static SharingEntry deleteSharingEntry(long sharingEntryId)
		throws PortalException {

		return getService().deleteSharingEntry(sharingEntryId);
	}

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
	public static SharingEntry deleteSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws PortalException {

		return getService().deleteSharingEntry(toUserId, classNameId, classPK);
	}

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
	public static SharingEntry deleteSharingEntry(SharingEntry sharingEntry) {
		return getService().deleteSharingEntry(sharingEntry);
	}

	public static SharingEntry deleteSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().deleteSharingEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Deletes the sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 */
	public static void deleteToUserSharingEntries(long toUserId) {
		getService().deleteToUserSharingEntries(toUserId);
	}

	public static <T> T dslQuery(DSLQuery dslQuery) {
		return getService().dslQuery(dslQuery);
	}

	public static int dslQueryCount(DSLQuery dslQuery) {
		return getService().dslQueryCount(dslQuery);
	}

	public static DynamicQuery dynamicQuery() {
		return getService().dynamicQuery();
	}

	/**
	 * Performs a dynamic query on the database and returns the matching rows.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the matching rows
	 */
	public static <T> List<T> dynamicQuery(DynamicQuery dynamicQuery) {
		return getService().dynamicQuery(dynamicQuery);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end) {

		return getService().dynamicQuery(dynamicQuery, start, end);
	}

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
	public static <T> List<T> dynamicQuery(
		DynamicQuery dynamicQuery, int start, int end,
		OrderByComparator<T> orderByComparator) {

		return getService().dynamicQuery(
			dynamicQuery, start, end, orderByComparator);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(DynamicQuery dynamicQuery) {
		return getService().dynamicQueryCount(dynamicQuery);
	}

	/**
	 * Returns the number of rows matching the dynamic query.
	 *
	 * @param dynamicQuery the dynamic query
	 * @param projection the projection to apply to the query
	 * @return the number of rows matching the dynamic query
	 */
	public static long dynamicQueryCount(
		DynamicQuery dynamicQuery,
		com.liferay.portal.kernel.dao.orm.Projection projection) {

		return getService().dynamicQueryCount(dynamicQuery, projection);
	}

	public static SharingEntry fetchSharingEntry(long sharingEntryId) {
		return getService().fetchSharingEntry(sharingEntryId);
	}

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
	public static SharingEntry fetchSharingEntry(
		long toUserId, long classNameId, long classPK) {

		return getService().fetchSharingEntry(toUserId, classNameId, classPK);
	}

	public static SharingEntry fetchSharingEntryByExternalReferenceCode(
		String externalReferenceCode, long groupId) {

		return getService().fetchSharingEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the sharing entry matching the UUID and group.
	 *
	 * @param uuid the sharing entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching sharing entry, or <code>null</code> if a matching sharing entry could not be found
	 */
	public static SharingEntry fetchSharingEntryByUuidAndGroupId(
		String uuid, long groupId) {

		return getService().fetchSharingEntryByUuidAndGroupId(uuid, groupId);
	}

	public static com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery
		getActionableDynamicQuery() {

		return getService().getActionableDynamicQuery();
	}

	public static int getCompanySharingEntriesCount(
		long companyId, long classNameId) {

		return getService().getCompanySharingEntriesCount(
			companyId, classNameId);
	}

	public static com.liferay.portal.kernel.dao.orm.ExportActionableDynamicQuery
		getExportActionableDynamicQuery(
			com.liferay.exportimport.kernel.lar.PortletDataContext
				portletDataContext) {

		return getService().getExportActionableDynamicQuery(portletDataContext);
	}

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
	public static List<SharingEntry> getFromUserSharingEntries(
		long fromUserId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		return getService().getFromUserSharingEntries(
			fromUserId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries for the type of resource shared by
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param fromUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	public static int getFromUserSharingEntriesCount(
		long fromUserId, long classNameId) {

		return getService().getFromUserSharingEntriesCount(
			fromUserId, classNameId);
	}

	/**
	 * Returns the the group's sharing entries.
	 *
	 * @param groupId the primary key of the group
	 * @return the sharing entries
	 */
	public static List<SharingEntry> getGroupSharingEntries(long groupId) {
		return getService().getGroupSharingEntries(groupId);
	}

	/**
	 * Returns the the group's sharing entries count.
	 *
	 * @param groupId the primary key of the group
	 * @return the sharing entries count
	 */
	public static int getGroupSharingEntriesCount(long groupId) {
		return getService().getGroupSharingEntriesCount(groupId);
	}

	public static
		com.liferay.portal.kernel.dao.orm.IndexableActionableDynamicQuery
			getIndexableActionableDynamicQuery() {

		return getService().getIndexableActionableDynamicQuery();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * @throws PortalException
	 */
	public static PersistedModel getPersistedModel(Serializable primaryKeyObj)
		throws PortalException {

		return getService().getPersistedModel(primaryKeyObj);
	}

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
	public static List<SharingEntry> getSharingEntries(int start, int end) {
		return getService().getSharingEntries(start, end);
	}

	/**
	 * Returns the resource's sharing entries. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entries
	 */
	public static List<SharingEntry> getSharingEntries(
		long classNameId, long classPK) {

		return getService().getSharingEntries(classNameId, classPK);
	}

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
	public static List<SharingEntry> getSharingEntries(
		long classNameId, long classPK, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		return getService().getSharingEntries(
			classNameId, classPK, start, end, orderByComparator);
	}

	/**
	 * Returns all the sharing entries matching the UUID and company.
	 *
	 * @param uuid the UUID of the sharing entries
	 * @param companyId the primary key of the company
	 * @return the matching sharing entries, or an empty list if no matches were found
	 */
	public static List<SharingEntry> getSharingEntriesByUuidAndCompanyId(
		String uuid, long companyId) {

		return getService().getSharingEntriesByUuidAndCompanyId(
			uuid, companyId);
	}

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
	public static List<SharingEntry> getSharingEntriesByUuidAndCompanyId(
		String uuid, long companyId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		return getService().getSharingEntriesByUuidAndCompanyId(
			uuid, companyId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries.
	 *
	 * @return the number of sharing entries
	 */
	public static int getSharingEntriesCount() {
		return getService().getSharingEntriesCount();
	}

	/**
	 * Returns the resource's sharing entries count. The class name ID and class
	 * primary key identify the resource's type and instance, respectively.
	 *
	 * @param classNameId the resource's class name ID
	 * @param classPK the class primary key of the resource
	 * @return the sharing entries count
	 * @review
	 */
	public static int getSharingEntriesCount(long classNameId, long classPK) {
		return getService().getSharingEntriesCount(classNameId, classPK);
	}

	/**
	 * Returns the sharing entry with the primary key.
	 *
	 * @param sharingEntryId the primary key of the sharing entry
	 * @return the sharing entry
	 * @throws PortalException if a sharing entry with the primary key could not be found
	 */
	public static SharingEntry getSharingEntry(long sharingEntryId)
		throws PortalException {

		return getService().getSharingEntry(sharingEntryId);
	}

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
	public static SharingEntry getSharingEntry(
			long toUserId, long classNameId, long classPK)
		throws PortalException {

		return getService().getSharingEntry(toUserId, classNameId, classPK);
	}

	public static SharingEntry getSharingEntryByExternalReferenceCode(
			String externalReferenceCode, long groupId)
		throws PortalException {

		return getService().getSharingEntryByExternalReferenceCode(
			externalReferenceCode, groupId);
	}

	/**
	 * Returns the sharing entry matching the UUID and group.
	 *
	 * @param uuid the sharing entry's UUID
	 * @param groupId the primary key of the group
	 * @return the matching sharing entry
	 * @throws PortalException if a matching sharing entry could not be found
	 */
	public static SharingEntry getSharingEntryByUuidAndGroupId(
			String uuid, long groupId)
		throws PortalException {

		return getService().getSharingEntryByUuidAndGroupId(uuid, groupId);
	}

	/**
	 * Returns the list of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @return the list of sharing entries
	 */
	public static List<SharingEntry> getToUserSharingEntries(long toUserId) {
		return getService().getToUserSharingEntries(toUserId);
	}

	/**
	 * Returns the range of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @param start the range's lower bound
	 * @param end the range's upper bound (not inclusive)
	 * @return the range of sharing entries
	 */
	public static List<SharingEntry> getToUserSharingEntries(
		long toUserId, int start, int end) {

		return getService().getToUserSharingEntries(toUserId, start, end);
	}

	/**
	 * Returns the list of sharing entries for the type of resource shared with
	 * the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the list of sharing entries
	 */
	public static List<SharingEntry> getToUserSharingEntries(
		long toUserId, long classNameId) {

		return getService().getToUserSharingEntries(toUserId, classNameId);
	}

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
	public static List<SharingEntry> getToUserSharingEntries(
		long toUserId, long classNameId, int start, int end,
		OrderByComparator<SharingEntry> orderByComparator) {

		return getService().getToUserSharingEntries(
			toUserId, classNameId, start, end, orderByComparator);
	}

	/**
	 * Returns the number of sharing entries for resources shared with the user.
	 *
	 * @param toUserId the user's ID
	 * @return the number of sharing entries
	 */
	public static int getToUserSharingEntriesCount(long toUserId) {
		return getService().getToUserSharingEntriesCount(toUserId);
	}

	/**
	 * Returns the number of sharing entries for the type of resource shared
	 * with the user. The class name ID identifies the resource type.
	 *
	 * @param toUserId the user's ID
	 * @param classNameId the class name ID of the resources
	 * @return the number of sharing entries
	 * @review
	 */
	public static int getToUserSharingEntriesCount(
		long toUserId, long classNameId) {

		return getService().getToUserSharingEntriesCount(toUserId, classNameId);
	}

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
	public static boolean hasShareableSharingPermission(
		long toUserId, long classNameId, long classPK,
		com.liferay.sharing.security.permission.SharingEntryAction
			sharingEntryAction) {

		return getService().hasShareableSharingPermission(
			toUserId, classNameId, classPK, sharingEntryAction);
	}

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
	public static boolean hasSharingPermission(
		long toUserId, long classNameId, long classPK,
		com.liferay.sharing.security.permission.SharingEntryAction
			sharingEntryAction) {

		return getService().hasSharingPermission(
			toUserId, classNameId, classPK, sharingEntryAction);
	}

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
	public static SharingEntry updateSharingEntry(
			long sharingEntryId,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			boolean shareable, java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSharingEntry(
			sharingEntryId, sharingEntryActions, shareable, expirationDate,
			serviceContext);
	}

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
	public static SharingEntry updateSharingEntry(
			long userId, long sharingEntryId,
			java.util.Collection
				<com.liferay.sharing.security.permission.SharingEntryAction>
					sharingEntryActions,
			boolean shareable, java.util.Date expirationDate,
			com.liferay.portal.kernel.service.ServiceContext serviceContext)
		throws PortalException {

		return getService().updateSharingEntry(
			userId, sharingEntryId, sharingEntryActions, shareable,
			expirationDate, serviceContext);
	}

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
	public static SharingEntry updateSharingEntry(SharingEntry sharingEntry) {
		return getService().updateSharingEntry(sharingEntry);
	}

	public static SharingEntryLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<SharingEntryLocalService> _serviceSnapshot =
		new Snapshot<>(
			SharingEntryLocalServiceUtil.class, SharingEntryLocalService.class);

}